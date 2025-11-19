document.getElementById("avdeling2").addEventListener("submit", function (e) {
  e.preventDefault();

  const route = document.getElementById("filterRoute").value;
  const time = document.getElementById("filterTime").value + ":00";

let days = [];

if (typeof window.getSelectedWeekdays === "function") {
  days = window.getSelectedWeekdays();
}

if (!days.length || days[0] === "") {
  days = ["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"];
}


  const list = window.timetable
    .filter(t =>
      t.routeId == route &&
      days.includes(t.weekday) &&
      t.time >= time &&
      t.type !== "CANCELLED"
    )
    .sort((a,b) => a.time.localeCompare(b.time));

  let html = `<p><strong>Rute ${routes[route].num}: ${routes[route].from} → ${routes[route].to}</strong><br>
    ${days.map(d => weekdays[d]).join(", ")} fra kl. ${time.slice(0,5)}</p>`;

  if(list.length === 0) {
    html += "<p>Ingen avganger</p>";
  } else {
    html += "<table><thead><tr><th>Rute</th><th>Dag</th><th>Fra</th><th>Til</th><th>Tid</th><th>Type</th><th></th></tr></thead><tbody>";

    list.forEach(t => {
      const color = t.type === "EXTRA" ? "green" :
                    t.type === "OMITTED" ? "red" : "black";
      html += `<tr>
        <td>${routes[t.routeId].num}</td>
        <td>${weekdays[t.weekday]}</td>
        <td>${routes[t.routeId].from}</td>
        <td>${routes[t.routeId].to}</td>
        <td>${t.time.slice(0,5)}</td>
        <td style="color:${color}">${t.type}</td>
        <td>
          <button onclick="edit(${t.id})">Endre</button>
          <button class="delete" onclick="remove(${t.id})">Slett</button>
        </td>
      </tr>`;
    });

    html += "</tbody></table>";
  }

  document.getElementById("routeResult").innerHTML = html;
});
