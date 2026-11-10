document.addEventListener('DOMContentLoaded', () => {
    // 1. Получаем ID из URL (например, /order?id=5)
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('id');

    const titleEl = document.getElementById('orderTitle');
    const dateEl = document.getElementById('orderDate');
    const detailsEl = document.getElementById('orderDetails');

    if (!orderId) {
        titleEl.textContent = 'Ошибка';
        detailsEl.innerHTML = '<div class="bucket-empty"><p>Заказ не указан</p></div>';
        return;
    }

    // 2. Запрашиваем данные с бэкенда (используем метод из контроллера)
    API.get(`/api/order/${orderId}`)
        .then(res => {
            // Проверяем, что бэкенд вернул успех и внутри есть data.order
            if (!res || !res.success || !res.data || !res.data.order) {
                titleEl.textContent = 'Ошибка';
                detailsEl.innerHTML = `<div class="bucket-empty"><p>${res?.message || 'Заказ не найден или нет доступа'}</p></div>`;
                return;
            }

            renderOrderDetails(res.data.order, titleEl, dateEl, detailsEl);
        })
        .catch(err => {
            console.error('Ошибка:', err);
            titleEl.textContent = 'Ошибка';
            detailsEl.innerHTML = `<div class="bucket-empty"><p>${err.message || 'Ошибка сервера'}</p></div>`;
        });
});

function renderOrderDetails(order, titleEl, dateEl, detailsEl) {
    // Форматируем дату
    const dateStr = new Date(order.createdAt).toLocaleDateString('ru-RU', {
        day: 'numeric', month: 'long', year: 'numeric'
    });

    titleEl.textContent = `Заказ от ${dateStr}`;
    dateEl.textContent = `Оформлен в ${new Date(order.createdAt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}`;

    // Определяем статус
    const statusMap = {
        CREATED: { text: 'Оформлен', class: 'order-status--created' },
        PAID: { text: 'Оплачен', class: 'order-status--paid' },
        DELIVERED: { text: 'Доставлен', class: 'order-status--delivered' },
        CANCELLED: { text: 'Отменён', class: 'order-status--cancelled' }
    };
    const status = statusMap[order.status] || { text: order.status, class: '' };

    // Формируем HTML
    let html = `
        <div class="order-header-info">
            <div>
                <span style="color: var(--text-muted); font-size: 14px;">Статус заказа</span>
                <div class="order-status-badge ${status.class}" style="margin-top: 4px;">${status.text}</div>
            </div>
            <div style="text-align: right;">
                <span style="color: var(--text-muted); font-size: 14px;">Итого</span>
                <div style="font-size: 24px; font-weight: 800; color: var(--mint-deep);">${order.totalPrice} ₽</div>
            </div>
        </div>
    `;

    // Перебираем части заказа (Еда, Аптека и т.д.)
    if (order.parts && order.parts.length > 0) {
        order.parts.forEach(part => {
            const typeName = {
                'FOOD': 'Еда 🍕',
                'MEDICINE': 'Аптека 💊',
                'FLOWER': 'Цветы 💐',
                'PRODUCT': 'Продукты 🛒'
            }[part.type] || part.type;

            html += `<div class="order-part-block"><h3>${typeName}</h3>`;

            if (part.items) {
                part.items.forEach(item => {
                    html += `
                        <div class="order-item-row">
                            <span>${escapeHtml(item.name)}</span>
                            <span>x ${item.quantity}</span>
                            <span>${item.price * item.quantity} ₽</span>
                        </div>
                    `;
                });
            }
            html += `</div>`;
        });
    }

    // Кнопка отмены (только если статус CREATED)
    html += `
        <div class="order-total-block">
            <span>Итого:</span>
            <span>${order.totalPrice} ₽</span>
        </div>
    `;

    // Кнопка отмены (только если статус CREATED)
    if (order.status === 'CREATED') {
        html += `
            <div class="order-actions" style="display: flex; justify-content: flex-end; margin-top: 24px;">
                <button class="btn btn--danger" onclick="cancelOrder(${order.id})">Отменить заказ</button>
            </div>
        `;
    }

    detailsEl.innerHTML = html;
}

function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, (s) => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[s]);
}

// Отмена заказа
function cancelOrder(id) {
    if (!confirm('Вы уверены, что хотите отменить заказ?')) {
        return;
    }

    // Блокируем кнопку визуально (опционально, если передать её в функцию)

    // Отправляем POST запрос на бэкенд
    API.post(`/api/order/${id}/cancel`)
        .then(res => {
            if (res && res.success) {
                alert('Заказ успешно отменён!');
                window.location.reload(); // Перезагружаем страницу, чтобы увидеть новый статус
            } else {
                // Если бэкенд вернул ошибку (например, заказ уже доставлен)
                alert(res?.message || 'Не удалось отменить заказ');
            }
        })
        .catch(err => {
            console.error('Ошибка отмены:', err);
            alert(err.message || 'Ошибка сервера при отмене заказа');
        });
}