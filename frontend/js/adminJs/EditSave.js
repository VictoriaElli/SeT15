window.remove = function (id) {
  window.timetable = window.timetable.filter(t => t.id !== id);
  window.message("Avgang slettet", "success");
  document.getElementById("avdeling2").dispatchEvent(new Event("submit"));
};

window.edit = function(id){
  const t = timetable.find(x => x.id === id);
  route.value = t.routeId;
  weekday.value = t.weekday;
  departureTime.value = t.time.slice(0,5);
  type.value = t.type;
  submitBtn.textContent = "Oppdater avgang";
  message("Endre og lagre","success");
};

document.getElementById("avdeling1").addEventListener("submit",function(e){
  e.preventDefault();

  const r = +route.value;
  const d = weekday.value;
  const t = departureTime.value + ":00";
  const ty = type.value;

  const exists = timetable.some(x =>
    x.routeId === r && x.weekday === d && x.time === t && x.type !== "OMITTED"
  );

  if(ty!=="OMITTED" && exists){
    message("Avgang finnes allerede","error");
    return;
  }

  const id = Math.max(...timetable.map(x => x.id)) + 1;
  timetable.push({ id, routeId:r, weekday:d, time:t, type:ty });
  message("Avgang lagret","success");

  this.reset();
  submitBtn.textContent = "Lagre avgang";
  document.getElementById("avdeling2").dispatchEvent(new Event("submit"));
});
