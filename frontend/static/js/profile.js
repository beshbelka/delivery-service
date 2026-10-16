(() => {
    const $ = (id) => document.getElementById(id);

    const els = {
        login:       $('loginValue'),
        nameDisplay: $('nameDisplay'),
        role:        $('roleValue'),
        ordersList:  $('ordersList'),
        ordersEmpty: $('ordersEmpty'),
        ordersCount: $('ordersCount'),
        notification:$('notification'),
        logoutBtn:   $('logoutButton'),
    };

    let currentUser = null;

// ---------- Password modal ----------
    const pwdModal     = document.getElementById('passwordModal');
    const pwdForm      = document.getElementById('passwordForm');
    const pwdError     = document.getElementById('passwordError');
    const inpCurrent   = document.getElementById('currentPassword');
    const inpNew1      = document.getElementById('newPassword');
    const inpNew2      = document.getElementById('newPassword2');
    const changePwdBtn = document.getElementById('changePasswordBtn');

// цвета из палитры style.css (rose-deep и border)
    const COLOR_ERROR   = '#d16e83';
    const COLOR_INVALID = '#d16e83';

    function openModal(id) {
        const m = document.getElementById(id);
        if (m) m.style.display = 'flex';
    }

    function closeModal(id) {
        const m = document.getElementById(id);
        if (m) m.style.display = 'none';
    }

    function showPasswordError(msg) {
        pwdError.textContent = msg;
        pwdError.style.display = 'block';
    }

    function clearPasswordError() {
        pwdError.textContent = '';
        pwdError.style.display = 'none';
        [inpCurrent, inpNew1, inpNew2].forEach(i => {
            i.style.borderColor = '';
            i.style.outlineColor = '';
        });
    }

    function resetPasswordForm() {
        pwdForm.reset();
        clearPasswordError();
    }

// Открытие
    changePwdBtn.addEventListener('click', () => {
        resetPasswordForm();
        openModal('passwordModal');
        inpCurrent.focus();
    });

// Закрытие: overlay + крестик + кнопка «Отмена»
    pwdModal.addEventListener('click', (e) => {
        if (e.target.closest('[data-close="passwordModal"]')) {
            closeModal('passwordModal');
            resetPasswordForm();
        }
    });

// Закрытие по Esc
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && pwdModal.style.display === 'flex') {
            closeModal('passwordModal');
            resetPasswordForm();
        }
    });

// Валидация
    function validatePasswordForm() {
        clearPasswordError();

        const oldPass  = inpCurrent.value;
        const newPass1 = inpNew1.value;
        const newPass2 = inpNew2.value;

        let invalid = false;

        if (oldPass.length < 6) {
            inpCurrent.style.borderColor = COLOR_INVALID;
            invalid = true;
        }
        if (newPass1.length < 6) {
            inpNew1.style.borderColor = COLOR_INVALID;
            invalid = true;
        }
        if (newPass2.length < 6) {
            inpNew2.style.borderColor = COLOR_INVALID;
            invalid = true;
        }

        if (invalid) {
            showPasswordError('Длина каждого пароля должна быть не менее 6 символов');
            return null;
        }

        if (newPass1 !== newPass2) {
            inpNew1.style.borderColor = COLOR_INVALID;
            inpNew2.style.borderColor = COLOR_INVALID;
            showPasswordError('Новые пароли не совпадают');
            return null;
        }

        if (newPass1 === oldPass) {
            inpNew1.style.borderColor = COLOR_INVALID;
            showPasswordError('Новый пароль должен отличаться от текущего');
            return null;
        }

        return { oldPass, newPass1, newPass2 };
    }

// Снятие подсветки при вводе
    [inpCurrent, inpNew1, inpNew2].forEach(inp => {
        inp.addEventListener('input', () => {
            inp.style.borderColor = '';
            clearPasswordError();
        });
    });

// Отправка
    pwdForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const payload = validatePasswordForm();
        if (!payload) return;

        const submitBtn = pwdForm.querySelector('button[type="submit"]');
        submitBtn.disabled = true;

        try {
            const res = await API.post('/auth/changePassword', payload);

            // ApiResponse: { success, code, message, data }
            if (res && res.success === true) {
                closeModal('passwordModal');
                resetPasswordForm();
                window.location.reload();
            } else {
                showPasswordError((res && res.message) || 'Не удалось сменить пароль');
            }
        } catch (err) {
            showPasswordError(err.message || 'Ошибка сервера');
        } finally {
            submitBtn.disabled = false;
        }
    });

    // ---------- Notification ----------
    function notify(message, isError = false) {
        els.notification.textContent = message;
        els.notification.classList.toggle('notification--error', isError);
        els.notification.style.display = 'block';
        clearTimeout(notify._t);
        notify._t = setTimeout(() => {
            els.notification.style.display = 'none';
        }, 3500);
    }

    // ---------- Storage helpers ----------
    function readCachedUser() {
        const login = localStorage.getItem('login');
        const name  = localStorage.getItem('name');
        const role  = localStorage.getItem('role');

        if (!login || !name || !role) return null;

        return { login, name, role };
    }

    function cacheUser(user) {
        if (user.login) localStorage.setItem('login', user.login);
        if (user.name)  localStorage.setItem('name',  user.name);
        if (user.role)  localStorage.setItem('role',  user.role);
    }

    // ---------- Load profile ----------
    async function loadProfile() {
        const cached = readCachedUser();

        if (cached) {
            currentUser = cached;
            renderUser(currentUser);
        } else {
            try {
                const res = await API.get('/auth/user');

                // ApiResponse: { success, code, message, data: {login, name, role} }
                if (!res || !res.success || !res.data) {
                    notify((res && res.message) || 'Не удалось загрузить профиль', true);
                    return;
                }

                const user = res.data;

                if (!user.login || !user.name || !user.role) {
                    notify('Профиль вернул неполные данные', true);
                    return;
                }

                currentUser = {
                    login: user.login,
                    name:  user.name,
                    role:  user.role,
                };

                cacheUser(currentUser);
                renderUser(currentUser);
            } catch (err) {
                notify(err.message || 'Не удалось загрузить профиль', true);
                return;
            }
        }

        await loadOrders();
    }

    function renderUser(user) {
        els.login.textContent       = user.login || '—';
        els.nameDisplay.textContent = user.name  || '—';
        els.role.textContent        = user.role  || 'Пользователь';
    }

    // ---------- Orders ----------
    async function loadOrders() {
        try {
            const res = await API.get('/auth/orders');
            if (!res || !res.success) {
                renderOrders([], 0);
                return;
            }
            const orders = (res.data && res.data.orders) || [];
            const count  = (res.data && res.data.count)  ?? orders.length;
            renderOrders(orders, count);
        } catch (err) {
            renderOrders([], 0);
        }
    }

    function renderOrders(orders, count = orders.length) {
        els.ordersCount.textContent = count;

        if (!orders.length) {
            els.ordersList.innerHTML = '';
            els.ordersEmpty.style.display = 'flex';
            return;
        }

        els.ordersEmpty.style.display = 'none';
        els.ordersList.innerHTML = orders.map(orderTpl).join('');
    }

    function orderTpl(o) {
        const statusClass = {
            NEW: 'status--new',
            IN_PROGRESS: 'status--progress',
            DONE: 'status--done',
            CANCELLED: 'status--cancelled',
        }[o.status] || 'status--new';

        const statusText = {
            NEW: 'Новый',
            IN_PROGRESS: 'В пути',
            DONE: 'Доставлен',
            CANCELLED: 'Отменён',
        }[o.status] || '—';

        const icon = o.icon || '📦';
        const price = (o.price != null) ? `${o.price} ₽` : '—';

        return `
            <div class="order-item">
                <div class="order-item__icon">${icon}</div>
                <div class="order-item__body">
                    <div class="order-item__title">${escapeHtml(o.title || 'Заказ')}</div>
                    <div class="order-item__meta">${escapeHtml(o.address || '')}</div>
                    <span class="order-item__status ${statusClass}">${statusText}</span>
                </div>
                <div class="order-item__price">${price}</div>
            </div>
        `;
    }

    function escapeHtml(str) {
        return String(str).replace(/[&<>"']/g, (s) => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        }[s]));
    }

    // ---------- Logout ----------
    async function logout() {
        try {
            const res = await API.post('/auth/logout');
            if (res && res.success) {
                localStorage.removeItem('login');
                localStorage.removeItem('name');
                localStorage.removeItem('role');
                window.location.href = '/';
            } else {
                notify('Ошибка сервера', true);
            }
        } catch (err) {
            notify('Ошибка сервера', true);
        }
    }

    els.logoutBtn.addEventListener('click', logout);

    loadProfile();
})();