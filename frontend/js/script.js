document.addEventListener("DOMContentLoaded", () => {
  setDefaultDate();
  setDefaultTime();
  loadStops();
  setupFormSubmit();
  setupBackToResults();
  setupTimeModeToggle();
});

// Sett dagens dato som standard
function setDefaultDate() {
  const dateInput = document.getElementById("date");
  const now = new Date();
  dateInput.value = now.toISOString().split("T")[0];
}

// Sett nåværende tid som standard
function setDefaultTime() {
  const timeInput = document.getElementById("time");
  const now = new Date();
  timeInput.value = now.toTimeString().split(" ")[0].slice(0,5);
}

// Last inn stopp fra API
async function loadStops() {
  const fromSelect = document.getElementById("fromStop");
  const toSelect = document.getElementById("toStop");
  const submitBtn = document.getElementById("submitBtn");

  try {
    const response = await fetch("http://localhost:8080/api/stops");
    const stops = await response.json();

    stops.forEach(stop => {
      const option1 = document.createElement("option");
      option1.value = stop.name;
      option1.textContent = stop.name;
      const option2 = option1.cloneNode(true);

      fromSelect.appendChild(option1);
      toSelect.appendChild(option2);
    });

    submitBtn.disabled = false;
  } catch (error) {
    console.error("Feil ved henting av stopp:", error);
    submitBtn.disabled = true;
  }
}

// Sett opp skjema-submit
function setupFormSubmit() {
  const form = document.getElementById("travelForm");
  const backBtn = document.getElementById("backToSearchBtn");

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const fromStop = document.getElementById("fromStop").value;
    const toStop = document.getElementById("toStop").value;
    const travelDate = document.getElementById("date").value;
    const travelTime = document.getElementById("time").value;
    const timeModeInput = document.querySelector('input[name="timeMode"]:checked');
    const timeMode = timeModeInput ? timeModeInput.value : "NOW";

    const payload = { fromStop, toStop, travelDate, travelTime, timeMode };

    try {
      const response = await fetch("http://localhost:8080/api/departures/search", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (!response.ok) throw new Error("Feil ved henting av ruter");

      const departures = await response.json();
      showResults(departures);

    } catch (error) {
      console.error(error);
      alert("Noe gikk galt ved henting av ruter.");
    }
  });

  backBtn.addEventListener("click", () => {
    document.getElementById("resultsSection").classList.add("hidden");
    document.getElementById("travelPlanner").classList.remove("hidden");
  });
}

// Vis resultater som kort
function showResults(departures) {
  const travelPlanner = document.getElementById("travelPlanner");
  const resultsSection = document.getElementById("resultsSection");
  const resultsDiv = document.getElementById("results");

  travelPlanner.classList.add("hidden");
  resultsSection.classList.remove("hidden");

  resultsDiv.innerHTML = "";

  if (!departures || departures.length === 0) {
    resultsDiv.textContent = "Ingen ruter funnet.";
    return;
  }

  const list = document.createElement("ul");

  departures.forEach(dep => {
    const plannedDeparture = dep.plannedDeparture.slice(0,5);
    const arrivalTime = dep.arrivalTime.slice(0,5);

    const item = document.createElement("li");
    item.innerHTML = `
      <div class="left">
        <span class="route">${dep.routeNumber}</span>
        <span class="stops"><strong>
          ${dep.fromStopName}
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" width="16" height="16" style="vertical-align:middle; margin:0 5px;">
            <path d="M598.6 342.6C611.1 330.1 611.1 309.8 598.6 297.3L470.6 169.3C458.1 156.8 437.8 156.8 425.3 169.3C412.8 181.8 412.8 202.1 425.3 214.6L498.7 288L64 288C46.3 288 32 302.3 32 320C32 337.7 46.3 352 64 352L498.7 352L425.3 425.4C412.8 437.9 412.8 458.2 425.3 470.7C437.8 483.2 458.1 483.2 470.6 470.7L598.6 342.7z"/>
          </svg>
          ${dep.toStopName}
        </strong></span>
      </div>
      <div class="right">
        <span class="times">Avreise: ${plannedDeparture}</span>
        <span class="times">Ankomst: ${arrivalTime}</span>
      </div>
    `;


    // Klikk for detaljvisning
    item.addEventListener("click", () => {
      showRouteDetails(dep);
    });

    list.appendChild(item);
  });

  resultsDiv.appendChild(list);
}

// Vis detaljside for en rute
function showRouteDetails(dep) {
  const resultsSection = document.getElementById("resultsSection");
  const routeDetails = document.getElementById("routeDetails");
  const detailsContent = document.getElementById("detailsContent");

  resultsSection.classList.add("hidden");
  routeDetails.classList.remove("hidden");

  const plannedDeparture = dep.plannedDeparture.slice(0,5);
  const arrivalTime = dep.arrivalTime.slice(0,5);

  detailsContent.innerHTML = `
    <h2>Rute ${dep.routeNumber}</h2>
    <p><strong>Fra:</strong> ${dep.fromStopName}</p>
    <p><strong>Til:</strong> ${dep.toStopName}</p>
    <p><strong>Avreise:</strong> ${plannedDeparture}</p>
    <p><strong>Ankomst:</strong> ${arrivalTime}</p>
    <p><strong>Forsinkelse:</strong> ${dep.delayMinutes} minutter</p>
    <p><strong>Meldinger:</strong> ${dep.operationMessage || "Ingen"}</p>
    <p class="environment"><strong>Miljøgevinst:</strong> ${dep.environmentSavings.emissionSaved} g CO₂, ${dep.environmentSavings.costSaved} kr spart</p>
  `;

}

// Tilbake-knapp fra detaljvisning
function setupBackToResults() {
  const backBtn = document.getElementById("backToResultsBtn");
  if (!backBtn) return;

  backBtn.addEventListener("click", () => {
    document.getElementById("routeDetails").classList.add("hidden");
    document.getElementById("resultsSection").classList.remove("hidden");
  });
}

function setupTimeModeToggle() {
  const nowRadio = document.getElementById("now");
  const departRadio = document.getElementById("depart");
  const arrivalRadio = document.getElementById("arrival");
  const timeInput = document.getElementById("time");
  const dateInput = document.getElementById("date");
  const toggleIndicator = document.querySelector(".toggle-indicator");

  const nowBtnLabel = document.querySelector(".time-btn");
  const departLabel = document.querySelector('label[for="depart"]');
  const arrivalLabel = document.querySelector('label[for="arrival"]');

  function setTimeToNow() {
    const now = new Date();
    const hh = String(now.getHours()).padStart(2, "0");
    const mm = String(now.getMinutes()).padStart(2, "0");
    timeInput.value = `${hh}:${mm}`;
  }

  function isToday(dateStr) {
    const today = new Date();
    const inputDate = new Date(dateStr);
    return today.toDateString() === inputDate.toDateString();
  }

  function switchToDepart() {
    nowRadio.checked = false;
    departRadio.checked = true;
    toggleIndicator.style.opacity = "1";
    toggleIndicator.style.transform = "translateX(0%)";
  }

  // Klikk på NÅ
  nowBtnLabel.addEventListener("click", () => {
    if (!isToday(dateInput.value)) return; // kun tillatt hvis dato er i dag
    nowRadio.checked = true;
    departRadio.checked = false;
    arrivalRadio.checked = false;
    setTimeToNow();
    toggleIndicator.style.opacity = "0"; // skjul glideindikator
  });

  // Klikk på Avreise
  departLabel.addEventListener("click", switchToDepart);

  // Klikk på Ankomst
  arrivalLabel.addEventListener("click", () => {
    nowRadio.checked = false;
    arrivalRadio.checked = true;
    toggleIndicator.style.opacity = "1";
    toggleIndicator.style.transform = "translateX(100%)";
  });

  // Hvis brukeren endrer tid eller dato manuelt → bytt til Avreise
  function onTimeOrDateChange() {
    if (nowRadio.checked) {
      if (!isToday(dateInput.value)) {
        switchToDepart();
      }
    }
  }

  timeInput.addEventListener("input", onTimeOrDateChange);
  dateInput.addEventListener("input", onTimeOrDateChange);

  // Init: sett tid til nå og skjul glideindikator
  setTimeToNow();
  toggleIndicator.style.opacity = "0";
}