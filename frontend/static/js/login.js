
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const errorEl = document.getElementById('loginError');
    errorEl.hidden = true;
    errorEl.textContent = '';

    const login    = document.getElementById('login').value.trim();
    const password = document.getElementById('password').value;

    if (!login || !password) {
        errorEl.textContent = 'Введите логин и пароль';
        errorEl.hidden = false;
        return;
    }

    try {
        const res = await API.post('/auth/login', { login, password });
        const token = res.data && res.data.accessToken;

        if (!token) {
            errorEl.textContent = 'Сервер не вернул токен';
            errorEl.hidden = false;
            return;
        }

        API.setAccessToken(token);
        window.location.href = '/';
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.hidden = false;
    }
});