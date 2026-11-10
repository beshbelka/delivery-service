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
            const res = await API.get('/api/order/orders');
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
        // Обновляем счетчик в заголовке
        els.ordersCount.textContent = count;

        // Очищаем список
        els.ordersList.innerHTML = '';

        // Если заказов нет — показываем заглушку
        if (!orders.length) {
            els.ordersEmpty.style.display = 'flex';
            return;
        }

        // Если заказы есть — скрываем заглушку и рендерим их
        els.ordersEmpty.style.display = 'none';
        els.ordersList.innerHTML = orders.map(orderTpl).join('');
    }

    // Шаблон заказа с выпадающим списком
    function orderTpl(order) {
        // Форматируем дату
        const date = new Date(order.createdAt).toLocaleDateString('ru-RU', {
            day: 'numeric', month: 'long', year: 'numeric'
        });

        // Собираем список товаров
        let itemsHtml = '';
        if (order.parts) {
            order.parts.forEach(part => {
                const typeName = {
                    'FOOD': 'Еда 🍕',
                    'MEDICINE': 'Аптека 💊',
                    'FLOWER': 'Цветы 💐',
                    'PRODUCT': 'Продукты 🛒'
                }[part.type] || part.type;

                // Заголовок части
                itemsHtml += `<div class="order-part-title">${typeName}</div>`;

                // Товары в части
                if (part.items) {
                    part.items.forEach(item => {
                        itemsHtml += `
                            <div class="order-detail-item">
                                <span>${escapeHtml(item.name)}</span>
                                <span class="item-qty">x ${item.quantity}</span>
                                <span class="item-price">${item.price * item.quantity} ₽</span>
                            </div>
                        `;
                    });
                }
            });
        }

        // Статус заказа
        const statusText = {
            CREATED: 'Оформлен',
            PAID: 'Оплачен',
            DELIVERED: 'Доставлен',
            CANCELLED: 'Отменён'
        }[order.status] || order.status;

        return `
            <div class="order-card">
                <!-- Шапка заказа (по клику открывается/закрывается) -->
                <div class="order-card__header" onclick="this.parentElement.classList.toggle('is-open')">
                    <div class="order-card__title">
                        <span class="order-arrow">▶</span>
                        Заказ от ${date}
                    </div>
                    <div class="order-card__sum">${order.totalPrice} ₽</div>
                </div>
                
                <!-- Выпадающий список с товарами -->
                <div class="order-card__body">
                    ${itemsHtml}
                    <div class="order-card__status">Статус: ${statusText}</div>
                </div>
            </div>
        `;
    }

    function escapeHtml(str) {
        return String(str).replace(/[&<>"']/g, (s) => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        })[s]);
    }

    // ---------- Logout ----------
    async function logout() {
        try {
            const res = await API.post('/auth/logout');
            if (res && res.success) {
                localStorage.removeItem('login');
                localStorage.removeItem('name');
                localStorage.removeItem('role');
                localStorage.removeItem('isAuthenticated');
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