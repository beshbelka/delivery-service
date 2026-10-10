(() => {
    const link = document.querySelector('[data-auth-link]');
    if (!link) return;

    function renderAuthorized() {
        link.textContent = 'Личный кабинет';
        link.setAttribute('href', '/profile');
    }

    function renderGuest() {
        link.textContent = 'Вход';
        link.setAttribute('href', '/login');
    }

    fetch('/auth/profile', { credentials: 'include' })
        .then(res => res.ok ? res.json() : null)
        .then(data => {
            if (data && data.success) {
                renderAuthorized();
            } else {
                renderGuest();
            }
        })
        .catch(() => renderGuest());
})();