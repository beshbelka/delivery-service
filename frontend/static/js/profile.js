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

    changePwdBtn.addEventListener('click', () => {
        resetPasswordForm();
        openModal('passwordModal');
        inpCurrent.focus();
    });

    pwdModal.addEventListener('click', (e) => {
        if (e.target.closest('[data-close="passwordModal"]')) {
            closeModal('passwordModal');
            resetPasswordForm();
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && pwdModal.style.display === 'flex') {
            closeModal('passwordModal');
            resetPasswordForm();
        }
    });

    function validatePasswordForm() {
        clearPasswordError();
        const oldPass  = inpCurrent.value;
        const newPass1 = inpNew1.value;
        const newPass2 = inpNew2.value;
        let invalid = false;

        if (oldPass.length < 6) { inpCurrent.style.borderColor = COLOR_INVALID; invalid = true; }
        if (newPass1.length < 6) { inpNew1.style.borderColor = COLOR_INVALID; invalid = true; }
        if (newPass2.length < 6) { inpNew2.style.borderColor = COLOR_INVALID; invalid = true; }

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

    [inpCurrent, inpNew1, inpNew2].forEach(inp => {
        inp.addEventListener('input', () => {
            inp.style.borderColor = '';
            clearPasswordError();
        });
    });

    pwdForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = validatePasswordForm();
        if (!payload) return;

        const submitBtn = pwdForm.querySelector('button[type="submit"]');
        submitBtn.disabled = true;

        try {
            const res = await API.post('/user/changePassword', payload);
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

    // ---------- Edit Profile Modal ----------
    const editModal      = document.getElementById('editProfileModal');
    const editForm       = document.getElementById('editProfileForm');
    const editNameInput  = document.getElementById('editName');
    const emojiPicker    = document.getElementById('emojiPicker');
    const colorPicker    = document.getElementById('colorPicker');
    const avatarPreview  = document.getElementById('avatarPreview');
    const toggleEditBtn  = document.getElementById('toggleEditBtn');

    const EMOJIS = ['🐱', '🍕', '🍔', '🚀', '💖', '🌸', '🍺', '🌿', '🐶', '🐼', '🍩', '☕'];
    const COLORS = ['#b8dcc6', '#f7c4a8', '#cebce4', '#f5da9a', '#b3d6ea', '#f2bcc6', '#e0d5c4', '#e2ece9'];

    let selectedEmoji = '🐙';
    let selectedColor = '#b8dcc6';

    function renderPickers() {
        emojiPicker.innerHTML = EMOJIS.map(e =>
            `<button type="button" class="avatar-picker__item ${e === selectedEmoji ? 'avatar-picker__item--active' : ''}" data-emoji="${e}">${e}</button>`
        ).join('');

        colorPicker.innerHTML = COLORS.map(c =>
            `<button type="button" class="avatar-picker__item color-picker__item ${c === selectedColor ? 'color-picker__item--active' : ''}" style="background-color: ${c};" data-color="${c}"></button>`
        ).join('');
        updatePreview();
    }

    function updatePreview() {
        avatarPreview.textContent = selectedEmoji;
        avatarPreview.style.background = selectedColor;
    }

    editModal.addEventListener('click', (e) => {
        const emojiBtn = e.target.closest('[data-emoji]');
        if (emojiBtn) {
            selectedEmoji = emojiBtn.dataset.emoji;
            renderPickers();
            return;
        }
        const colorBtn = e.target.closest('[data-color]');
        if (colorBtn) {
            selectedColor = colorBtn.dataset.color;
            renderPickers();
            return;
        }
        if (e.target.closest('[data-close="editProfileModal"]')) {
            closeModal('editProfileModal');
        }
    });

    toggleEditBtn.addEventListener('click', () => {
        if (currentUser) {
            editNameInput.value = currentUser.name || '';
            selectedEmoji = currentUser.emoji || '🐙';
            selectedColor = currentUser.color || '#b8dcc6';
        }
        renderPickers();
        openModal('editProfileModal');
    });

    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = editForm.querySelector('button[type="submit"]');
        submitBtn.disabled = true;

        try {
            const payload = {
                name: editNameInput.value.trim(),
                emoji: selectedEmoji,
                color: selectedColor
            };
            const res = await API.post('/user/editProfile', payload);
            if (res && res.success) {
                closeModal('editProfileModal');
                localStorage.setItem('name', payload.name);
                localStorage.setItem('emoji', payload.emoji);
                localStorage.setItem('color', payload.color);
                window.location.reload();
            } else {
                notify(res?.message || 'Не удалось сохранить профиль', true);
            }
        } catch (err) {
            notify(err.message || 'Ошибка сервера', true);
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
        const emoji = localStorage.getItem('emoji');
        const color = localStorage.getItem('color');
        if (!login || !name || !role) return null;
        return { login, name, role, emoji, color };
    }

    function cacheUser(user) {
        if (user.login) localStorage.setItem('login', user.login);
        if (user.name)  localStorage.setItem('name',  user.name);
        if (user.role)  localStorage.setItem('role',  user.role);
        if (user.emoji) localStorage.setItem('emoji', user.emoji);
        if (user.color) localStorage.setItem('color', user.color);
    }

    // ---------- Load profile ----------
    async function loadProfile() {
        const cached = readCachedUser();
        if (cached) {
            currentUser = cached;
            renderUser(currentUser);
        }

        try {
            const res = await API.get('/user');
            if (!res || !res.success || !res.data) {
                if (!cached) notify((res && res.message) || 'Не удалось загрузить профиль', true);
                return;
            }

            const user = res.data;

            currentUser = {
                login: user.login,
                name:  user.name,
                role:  user.role,
                emoji: user.emoji || '🐙',
                color: user.color || '#b8dcc6'
            };

            cacheUser(currentUser);
            renderUser(currentUser);

        } catch (err) {
            console.error('Ошибка загрузки профиля:', err);
            if (!cached) notify(err.message || 'Не удалось загрузить профиль', true);
        }

        await loadOrders();
    }

    function renderUser(user) {
        els.login.textContent       = user.login || '—';
        els.nameDisplay.textContent = user.name  || '—';
        els.role.textContent        = user.role  || 'Пользователь';

        const avatarDisplay = document.getElementById('avatarDisplay');
        if (avatarDisplay) {
            avatarDisplay.textContent = user.emoji || '🐙';
            avatarDisplay.style.background = user.color || 'var(--mint)';
            avatarDisplay.style.visibility = 'visible';
        }
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
        els.ordersCount.textContent = count;
        els.ordersList.innerHTML = '';

        if (!orders.length) {
            els.ordersEmpty.style.display = 'flex';
            return;
        }

        els.ordersEmpty.style.display = 'none';
        els.ordersList.innerHTML = orders.map(orderTpl).join('');
    }

    function orderTpl(order) {
        const date = new Date(order.createdAt).toLocaleDateString('ru-RU', {
            day: 'numeric', month: 'long', year: 'numeric'
        });

        let itemsHtml = '';
        if (order.parts) {
            order.parts.forEach(part => {
                const typeName = {
                    'FOOD': 'Еда 🍕',
                    'MEDICINE': 'Аптека 💊',
                    'FLOWER': 'Цветы 💐',
                    'PRODUCT': 'Продукты 🛒'
                }[part.type] || part.type;

                itemsHtml += `<div class="order-part-title">${typeName}</div>`;

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

        const statusText = {
            CREATED: 'Оформлен',
            PAID: 'Оплачен',
            DELIVERED: 'Доставлен',
            CANCELLED: 'Отменён'
        }[order.status] || order.status;

        return `
            <div class="order-card">
                <!-- Шапка для раскрытия/закрытия аккордеона -->
                <div class="order-card__header" onclick="this.parentElement.classList.toggle('is-open')">
                    <div class="order-card__title">
                        <span class="order-arrow">▶</span>
                        Заказ от ${date}
                    </div>
                    <div class="order-card__sum">${order.totalPrice} ₽</div>
                </div>
                
                <!-- Содержимое аккордеона -->
                <div class="order-card__body">
                    ${itemsHtml}
                    
                    <div class="order-card__footer">
                        <div class="order-card__status">Статус: ${statusText}</div>
                        
                        <!-- КНОПКА ПЕРЕХОДА -->
                        <a href="/order?id=${order.id}" class="btn btn--primary btn--sm" style="text-decoration: none;">
                            Подробнее
                        </a>
                    </div>
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
                localStorage.removeItem('emoji');
                localStorage.removeItem('color');
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

    // ---------- Delete Account ----------
    const deleteAccountBtn = document.getElementById('deleteAccountBtn');

    if (deleteAccountBtn) {
        deleteAccountBtn.addEventListener('click', async () => {
            // 1. Спрашиваем подтверждение
            const confirmed = confirm('Вы уверены, что хотите удалить аккаунт? Это действие нельзя отменить.');
            if (!confirmed) return;

            // 2. Блокируем кнопку на время запроса
            deleteAccountBtn.disabled = true;
            const originalText = deleteAccountBtn.textContent;
            deleteAccountBtn.textContent = 'Удаление...';

            try {
                // 3. Отправляем POST запрос
                const res = await API.post('/user/delete');

                if (res && res.success) {
                    alert('Аккаунт успешно удален.');
                    // 4. Очищаем localStorage и перенаправляем на главную
                    localStorage.removeItem('login');
                    localStorage.removeItem('name');
                    localStorage.removeItem('role');
                    localStorage.removeItem('emoji');
                    localStorage.removeItem('color');
                    localStorage.removeItem('isAuthenticated');
                    window.location.href = '/';
                } else {
                    alert(res?.message || 'Не удалось удалить аккаунт');
                }
            } catch (err) {
                console.error('Ошибка удаления:', err);
                alert(err.message || 'Ошибка сервера при удалении аккаунта');
            } finally {
                // Возвращаем кнопку в исходное состояние (если не было перехода)
                deleteAccountBtn.disabled = false;
                deleteAccountBtn.textContent = originalText;
            }
        });
    }

    loadProfile();
})();