document.addEventListener("DOMContentLoaded", () => {
  loadFullSchedule();
  setActiveFooterLink();
});

async function loadFullSchedule() {
  const track = document.getElementById("carouselTrack");
  let currentIndex = 0;

  track.innerHTML = "<p>Laster dagsplan...</p>";

  try {
    const response = await fetch("http://localhost:8080/api/departures/full-schedule");
    if (!response.ok) throw new Error("Feil ved henting av full dagsplan");

    const schedules = await response.json();

    if (!schedules || schedules.length === 0) {
      track.innerHTML = "<p>Ingen avganger i dag.</p>";
      return;
    }

    track.innerHTML = "";
    const cards = [];

    schedules.forEach(route => {
      const card = document.createElement("div");
      card.classList.add("route-card");
      card.style.position = "relative"; // for absolute knappplassering

      // Header med rutenummer og fra → til
      const headerDiv = document.createElement("div");
      headerDiv.classList.add("route-header");

      const routeNumber = document.createElement("div");
      routeNumber.classList.add("route-number");
      routeNumber.textContent = route.routeNumber;

      const routeName = document.createElement("div");
      routeName.classList.add("route-name");
      routeName.innerHTML = `
        ${route.fromStopName}
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640" width="16" height="16">
          <path d="M598.6 342.6C611.1 330.1 611.1 309.8 598.6 297.3L470.6 169.3C458.1 156.8 437.8 156.8 425.3 169.3C412.8 181.8 412.8 202.1 425.3 214.6L498.7 288L64 288C46.3 288 32 302.3 32 320C32 337.7 46.3 352 64 352L498.7 352L425.3 425.4C412.8 437.9 412.8 458.2 425.3 470.7C437.8 483.2 458.1 483.2 470.6 470.7L598.6 342.7z"/>
        </svg>
        ${route.toStopName}
      `;

      headerDiv.appendChild(routeNumber);
      headerDiv.appendChild(routeName);

      // Stoppene
      const stopsDiv = document.createElement("div");
      stopsDiv.classList.add("stops");
      if (route.stops && route.stops.length > 0) {
        stopsDiv.textContent = route.stops.join(" - ");
      }

      // Avgangstider
      const timesDiv = document.createElement("div");
      timesDiv.classList.add("route-times");
      route.plannedDepartures.forEach(t => {
        const span = document.createElement("span");
        span.textContent = t.slice(0, 5); // HH:MM
        timesDiv.appendChild(span);
      });

      // Lag knappene
      const prevBtn = document.createElement("button");
      prevBtn.className = "carousel-btn prev";
      prevBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><path d="M73.4 297.4C60.9 309.9 60.9 330.2 73.4 342.7L233.4 502.7C245.9 515.2 266.2 515.2 278.7 502.7C291.2 490.2 291.2 469.9 278.7 457.4L173.3 352L544 352C561.7 352 576 337.7 576 320C576 302.3 561.7 288 544 288L173.3 288L278.7 182.6C291.2 170.1 291.2 149.8 278.7 137.3C266.2 124.8 245.9 124.8 233.4 137.3L73.4 297.3z"/></svg>`;
      const nextBtn = document.createElement("button");
      nextBtn.className = "carousel-btn next";
      nextBtn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 640 640"><path d="M566.6 342.6C579.1 330.1 579.1 309.8 566.6 297.3L406.6 137.3C394.1 124.8 373.8 124.8 361.3 137.3C348.8 149.8 348.8 170.1 361.3 182.6L466.7 288L96 288C78.3 288 64 302.3 64 320C64 337.7 78.3 352 96 352L466.7 352L361.3 457.4C348.8 469.9 348.8 490.2 361.3 502.7C373.8 515.2 394.1 515.2 406.6 502.7L566.6 342.7z"/></svg>`;

      // Legg alt til kortet
      card.appendChild(headerDiv);
      card.appendChild(stopsDiv);
      card.appendChild(timesDiv);
      card.appendChild(prevBtn);
      card.appendChild(nextBtn);

      // Legg til kort i array
      cards.push(card);
      track.appendChild(card);
    });

    function updateCarousel() {
      cards.forEach((c, i) => {
        if (i === currentIndex) {
          c.style.display = "block";
        } else {
          c.style.display = "none";
        }
      });
    }

    // Knapp-handlere
    cards.forEach((card, index) => {
      const prevBtn = card.querySelector(".carousel-btn.prev");
      const nextBtn = card.querySelector(".carousel-btn.next");

      prevBtn.addEventListener("click", () => {
        currentIndex = (currentIndex - 1 + cards.length) % cards.length;
        updateCarousel();
      });
      nextBtn.addEventListener("click", () => {
        currentIndex = (currentIndex + 1) % cards.length;
        updateCarousel();
      });
    });

    updateCarousel(); // vis første kort

  } catch (error) {
    console.error("Feil ved henting av full dagsplan:", error);
    track.innerHTML = "<p>Kunne ikke hente dagsplanen.</p>";
  }
}
