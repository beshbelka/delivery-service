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

    orderCard.addEventListener('click', (e) => {
        const btn = e.target.closest('.qty-btn');
        if (!btn) return;

        const itemDiv = btn.closest('.bucket-item');
        const sku = itemDiv?.dataset.sku;
        const action = btn.dataset.action;

        if (!sku) return;

        // Проверяем текущее количество
        const qtySpan = itemDiv.querySelector('.bucket-item__quantity');
        const currentQty = parseInt(qtySpan.textContent, 10);

        // Если хотят уменьшить количество, когда там 1 — предлагаем удалить
        if (action === 'minus' && currentQty <= 1) {
            const confirmDelete = confirm('Вы точно хотите удалить товар?');
            if (!confirmDelete) {
                return; // Пользователь нажал "Нет", ничего не делаем
            }
        }

        const body = { sku };
        if (action === 'minus') body.count = -1;

        const buttons = itemDiv.querySelectorAll('.qty-btn');
        buttons.forEach(b => b.disabled = true);

        API.post('/api/services/add', body)
            .then(res => {
                if (!res || !res.success) {
                    console.error(res?.message || 'Ошибка');
                    return;
                }

                // Если это было удаление (количество упало до 0), убираем элемент с экрана
                if (action === 'minus' && currentQty === 1) {
                    itemDiv.remove(); // Удаляем HTML-блок товара

                    // Если в категории не осталось товаров, можно убрать и заголовок категории
                    const categoryDiv = itemDiv.closest('.bucket-category');
                    if (categoryDiv && categoryDiv.querySelectorAll('.bucket-item').length === 0) {
                        categoryDiv.remove();
                    }

                    // Если корзина полностью опустела, показываем заглушку
                    if (orderCard.querySelectorAll('.bucket-item').length === 0) {
                        renderBucket(orderCard, {}, categoryNames); // Перерисовываем пустую
                    }
                } else {
                    // Обычное обновление цифр
                    const newQty = currentQty + (action === 'plus' ? 1 : -1);
                    qtySpan.textContent = newQty;

                    const totalSpan = itemDiv.querySelector('.bucket-item__total');
                    const priceText = itemDiv.querySelector('.bucket-item__price').textContent;
                    const price = parseFloat(priceText);
                    totalSpan.textContent = `${newQty * price} ₽`;
                }
            })
            .catch(err => {
                console.error(err);
            })
            .finally(() => {
                // Если элемент был удален, кнопок уже нет, поэтому проверяем наличие
                if (document.body.contains(itemDiv)) {
                    buttons.forEach(b => b.disabled = false);
                }
            });
    });

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
    let bodyHtml = '';

    for (const [type, order] of Object.entries(bucketData)) {
        if (!order || !order.items || order.items.length === 0) continue;

        hasItems = true;
        const title = categoryNames[type] || type.toUpperCase();

        bodyHtml += `
            <div class="bucket-category">
                <h3 class="bucket-category__title">${title}</h3>
                <div class="bucket-items">
        `;

        order.items.forEach(item => {
            const totalPrice = item.price * item.quantity;

            bodyHtml += `
                <div class="bucket-item" data-sku="${item.sku}">
                    <div class="bucket-item__image">🩷</div>
                    <div class="bucket-item__info">
                        <div class="bucket-item__name">${item.name}</div>
                        <div class="bucket-item__price">${item.price} ₽ / шт.</div>
                    </div>
                    
                    <div class="bucket-item__quantity-wrapper">
                        <button class="qty-btn" data-action="minus" type="button">−</button>
                        <span class="bucket-item__quantity">${item.quantity}</span>
                        <button class="qty-btn" data-action="plus" type="button">+</button>
                    </div>

                    <div class="bucket-item__total">${totalPrice} ₽</div>
                </div>
            `;
        });

        bodyHtml += `</div></div>`;
    }

    // В шапке больше нет блока со счётчиком, просто закрываем хедер
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