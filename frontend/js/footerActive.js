function setActiveFooterLink() {
  const links = document.querySelectorAll('.footer-icons .nav-link');
  const currentPage = window.location.pathname.split('/').pop();
  const indicator = document.querySelector('.footer-indicator');

  links.forEach(link => {
    if (link.getAttribute('href') === currentPage) {
      const rect = link.getBoundingClientRect();
      const parentRect = link.parentElement.getBoundingClientRect();

      // Sett left og bredde
      indicator.style.left = (rect.left - parentRect.left) + 'px';
      indicator.style.width = rect.width + 'px';
    }
  });
}

window.addEventListener('DOMContentLoaded', setActiveFooterLink);
