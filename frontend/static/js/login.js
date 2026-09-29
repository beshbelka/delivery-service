document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const errorEl = document.getElementById('loginError');
    errorEl.hidden = true;

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const data = await API.post('/auth/login', { email, password });
        // например, сохранить токен
        localStorage.setItem('token', data.token);
        window.location.href = '/';
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.hidden = false;
    }
});