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

        // нужны все три поля — иначе идём на сервер
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
                const data = await API.get('/auth/profile');
                // ждём именно login, name, role
                if (!data.login || !data.name || !data.role) {
                    notify('Профиль вернул неполные данные', true);
                    return;
                }

                currentUser = {
                    login: data.login,
                    name:  data.name,
                    role:  data.role,
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
            const res = await API.get('/api/orders');
            const orders = (res.data && res.data.orders) || [];
            renderOrders(orders);
        } catch (err) {
            renderOrders([]);
        }
    }

    function renderOrders(orders) {
        els.ordersCount.textContent = orders.length;

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

    loadProfile();
})();