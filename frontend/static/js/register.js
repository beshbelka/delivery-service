
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const errorEl = document.getElementById('registerError');
    errorEl.hidden = true;
    errorEl.textContent = '';

    const name      = document.getElementById('name').value.trim();
    const login     = document.getElementById('login').value.trim();
    const password1  = document.getElementById('password').value;
    const password2 = document.getElementById('password2').value;

    if (!name || !login) {
        showError(errorEl, 'Заполните все поля');
        return;
    }
    if (password1 !== password2) {
        showError(errorEl, 'Пароли не совпадают');
        return;
    }
    if (password1.length < 8) {
        showError(errorEl, 'Пароль должен быть минимум 8 символов');
        return;
    }

    try {
        const res = await API.post('/auth/register', {
            name, login, password1, password2
        });
        if (res.success) {
            window.location.href = '/profile';
        } else {
            showError(res.message);
        }
    } catch (err) {
        showError(errorEl, err.message);
    }
});

function showError(el, message) {
    el.textContent = message;
    el.hidden = false;
}

fetch('/auth/profile', { credentials: 'include' })
    .then(r => { if (r.ok) location.href = '/profile'; });