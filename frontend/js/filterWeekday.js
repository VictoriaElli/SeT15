document.addEventListener("DOMContentLoaded", () => {

  const select = document.getElementById('filterWeekday');
  if (!select) return;

  let chosenDays = [];

  select.addEventListener('mousedown', function(e) {
    e.preventDefault();

    const existing = document.querySelector('.weekday-popup');
    if (existing) existing.remove();

    const menu = document.createElement('div');
    menu.className = 'weekday-popup';

    Array.from(select.options).forEach(opt => {
      if (!opt.value) return;

      const label = document.createElement('label');
      const cb = document.createElement('input');
      cb.type = 'checkbox';
      cb.value = opt.value;
      cb.checked = chosenDays.some(d => d.value === opt.value);

      cb.addEventListener('change', () => {
        if (cb.checked) {
          if (!chosenDays.some(d => d.value === opt.value)) {
            chosenDays.push({ value: opt.value, text: opt.textContent });
          }
        } else {
          chosenDays = chosenDays.filter(d => d.value !== opt.value);
        }
        updateLabel();
      });

      label.appendChild(cb);
      label.appendChild(document.createTextNode(opt.textContent));
      menu.appendChild(label);
    });

    document.body.appendChild(menu);

    const rect = select.getBoundingClientRect();
    menu.style.left = rect.left + "px";
    menu.style.top = rect.bottom + window.scrollY + "px";
    menu.style.width = rect.width + "px";

    function closeMenu(ev) {
      if (!menu.contains(ev.target) && ev.target !== select) {
        menu.remove();
        document.removeEventListener('click', closeMenu);
      }
    }
    document.addEventListener('click', closeMenu);
  });

  function updateLabel() {
    const text = chosenDays.map(d => d.text);
    select.options[0].textContent = text.length ? text.join(', ') : "Velg dag";
  }

  window.getSelectedWeekdays = () => chosenDays.map(d => d.value);

});
