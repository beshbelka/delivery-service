document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const errorEl = document.getElementById('loginError');
    errorEl.hidden = true;
    errorEl.textContent = '';

    const login = document.getElementById('login').value.trim();
    const password = document.getElementById('password').value;

    try {
        const res = await API.post('/auth/login', { login, password });
        if (res.success) {
            window.location.href = '/profile';
        } else {
            errorEl.textContent = res.message;
            errorEl.hidden = false;
        }
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.hidden = false;
    }
});