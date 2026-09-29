// frontend/static/js/register.js
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const errorEl = document.getElementById('registerError');
    errorEl.hidden = true;
    errorEl.textContent = '';

    const name      = document.getElementById('name').value.trim();
    const email     = document.getElementById('email').value.trim();
    const phone     = document.getElementById('phone').value.trim();
    const password  = document.getElementById('password').value;
    const password2 = document.getElementById('password2').value;

    // клиентская валидация
    if (password !== password2) {
        errorEl.textContent = 'Пароли не совпадают';
        errorEl.hidden = false;
        return;
    }

    if (password.length < 8) {
        errorEl.textContent = 'Пароль должен быть минимум 8 символов';
        errorEl.hidden = false;
        return;
    }

    try {
        const data = await API.post('/auth/register', {
            name, email, phone, password
        });

        // если бэк вернул токен — сразу логиним
        if (data && data.token) {
            localStorage.setItem('token', data.token);
            window.location.href = '/';
        } else {
            // иначе отправляем на страницу входа
            window.location.href = '/login?registered=1';
        }
    } catch (err) {
        errorEl.textContent = err.message;
        errorEl.hidden = false;
    }
});