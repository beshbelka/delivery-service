(() => {
    const link = document.querySelector('[data-auth-link]');
    const bucket = document.querySelector('[data-bucket-link]');
    if (!link) return;

    function renderAuthorized() {
        link.textContent = 'Личный кабинет';
        link.setAttribute('href', '/profile');
        bucket.textContent = 'Корзина';
        bucket.setAttribute('href', '/bucket');
        bucket.setAttribute('style', 'visibility: visible');
    }

    function renderGuest() {
        link.textContent = 'Вход';
        link.setAttribute('href', '/login');
    }

    const cachedAuth = localStorage.getItem('isAuthenticated');
    if (cachedAuth === 'true') {
        renderAuthorized();
    } else {
        renderGuest();
    }

    fetch('/auth/profile', { credentials: 'include' })
        .then(res => res.ok ? res.json() : null)
        .then(data => {
            if (data && data.success) {
                localStorage.setItem('isAuthenticated', 'true')
                renderAuthorized();
            } else {
                localStorage.removeItem('isAuthenticated')
                renderGuest();
            }
        })
        .catch(() => renderGuest());
})();