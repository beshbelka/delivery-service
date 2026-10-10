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