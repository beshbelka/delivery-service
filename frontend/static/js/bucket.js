document.addEventListener('DOMContentLoaded', () => {
    const bucketContainer = document.querySelector('.bucket');
    if (!bucketContainer) return;

    const orderCard = document.querySelector('.bucket-card');
    const categoryNames = {
        'food': 'Еда',
        'product': 'Продукты',
        'flower': 'Цветы',
        'medicine': 'Аптека'
    };

    function loadBucket() {
        // Показываем загрузку
        orderCard.innerHTML = `
            <div class="bucket-card__header">
                <h2 class="bucket-card__title">Ваш заказ</h2>
            </div>
            <div class="bucket-empty"><p>Загрузка...</p></div>
        `;

        API.get('/api/order/bucket')
            .then(res => {
                if (!res || !res.success) {
                    showError(orderCard, res?.message || 'Ошибка загрузки');
                    return;
                }
                renderBucket(orderCard, res.data || {}, categoryNames);
            })
            .catch(err => {
                console.error('Ошибка:', err);
                showError(orderCard, err.message || 'Не удалось загрузить корзину');
            });
    }

    // Загружаем при старте
    loadBucket();
});

function renderBucket(container, bucketData, categoryNames) {
    let html = `
        <div class="bucket-card__header">
            <h2 class="bucket-card__title">Ваш заказ</h2>
        </div>
        <div class="bucket-card__body" id="bucketBody">
    `;

    let hasItems = false;
    let totalItemsCount = 0;
    let bodyHtml = '';

    // Проходимся по всем категориям
    for (const [type, order] of Object.entries(bucketData)) {
        // Пропускаем пустые корзины (у которых нет товаров)
        if (!order || !order.items || order.items.length === 0) continue;

        hasItems = true;
        const title = categoryNames[type] || type.toUpperCase();

        bodyHtml += `
            <div class="bucket-category">
                <h3 class="bucket-category__title">${title}</h3>
                <div class="bucket-items">
        `;

        order.items.forEach(item => {
            totalItemsCount += item.quantity;
            const totalPrice = item.price * item.quantity;

            bodyHtml += `
                <div class="bucket-item">
                    <div class="bucket-item__image">🩷</div>
                    <div class="bucket-item__info">
                        <div class="bucket-item__name">${item.name}</div>
                        <div class="bucket-item__price">${item.price} ₽ / шт.</div>
                    </div>
                    <div class="bucket-item__quantity">x ${item.quantity}</div>
                    <div class="bucket-item__total">${totalPrice} ₽</div>
                </div>
            `;
        });

        bodyHtml += `</div></div>`;
    }

    // Добавляем счетчик в заголовок
    html = html.replace('</h2>', `</h2><div class="bucket-card__meta"><span class="bucket-card__count">${totalItemsCount}</span></div>`);

    if (!hasItems) {
        html += `
            <div class="bucket-empty">
                <p>В корзине пока ничего нет.</p>
                <a href="/#services" class="btn btn--primary">Выбрать услугу</a>
            </div>
        `;
    } else {
        html += bodyHtml;
    }

    html += `</div>`;
    container.innerHTML = html;
}

function showError(container, text) {
    container.innerHTML = `
        <div class="bucket-card__header">
            <h2 class="bucket-card__title">Ваш заказ</h2>
        </div>
        <div class="bucket-empty">
            <p style="color: #d32f2f;">${text}</p>
        </div>
    `;
}