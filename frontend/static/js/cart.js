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

        API.get('/api/order/cart')
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
        // --- ОБРАБОТКА КНОПКИ "ОФОРМИТЬ ЗАКАЗ" ---
        const checkoutBtn = e.target.closest('.checkout-btn');
        if (checkoutBtn) {
            checkoutBtn.disabled = true;
            const originalText = checkoutBtn.textContent;
            checkoutBtn.textContent = 'Оформляем...';

            API.post('/api/order/make')
                .then(res => {
                    if (res && res.success) {
                        alert('Заказ успешно оформлен!');
                        window.location.reload();
                    } else {
                        const code = res?.code || 'Ошибка';
                        const msg = res?.message || 'Не удалось оформить заказ';
                        alert(`Ошибка ${code}: ${msg}`);
                    }
                })
                .catch(err => {
                    alert(`Ошибка ${err.code || 'сети'}: ${err.message}`);
                })
                .finally(() => {
                    checkoutBtn.disabled = false;
                    checkoutBtn.textContent = originalText;
                });

            return; // Прерываем дальнейшую обработку клика
        }

        // --- ОБРАБОТКА КНОПОК + И - ---
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
                    } else {
                        // Если корзина не опустела, пересчитываем итоговую сумму внизу
                        updateTotalSum();
                    }
                } else {
                    // Обычное обновление цифр
                    const newQty = currentQty + (action === 'plus' ? 1 : -1);
                    qtySpan.textContent = newQty;

                    const totalSpan = itemDiv.querySelector('.bucket-item__total');
                    const priceText = itemDiv.querySelector('.bucket-item__price').textContent;
                    const price = parseFloat(priceText);
                    totalSpan.textContent = `${newQty * price} ₽`;

                    // Пересчитываем общую сумму внизу
                    updateTotalSum();
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

    // Функция для пересчета итоговой суммы при изменении количества без полной перерисовки
    function updateTotalSum() {
        let total = 0;
        const items = orderCard.querySelectorAll('.bucket-item');
        items.forEach(item => {
            const priceText = item.querySelector('.bucket-item__price').textContent;
            const price = parseFloat(priceText);
            const qty = parseInt(item.querySelector('.bucket-item__quantity').textContent, 10);
            total += price * qty;
        });

        const totalSpan = orderCard.querySelector('.bucket-footer__total span:last-child');
        if (totalSpan) {
            totalSpan.textContent = `${total} ₽`;
        }
    }

    loadBucket();
});

function renderBucket(container, responseData, categoryNames) {
    // 1. Достаем саму корзину из ответа
    const cart = responseData?.cart;

    // 2. Формируем шапку
    let html = `
        <div class="bucket-card__header">
            <h2 class="bucket-card__title">Ваш заказ</h2>
        </div>
        <div class="bucket-card__body" id="bucketBody">
    `;

    let hasItems = false;
    let bodyHtml = '';
    let totalOrderSum = 0;

    // 3. Проверяем, есть ли части в корзине
    if (cart && cart.parts && cart.parts.length > 0) {

        // Проходимся по каждой части (Еда, Аптека...)
        cart.parts.forEach(part => {
            if (!part.items || part.items.length === 0) return;

            hasItems = true;
            const type = part.type.toLowerCase(); // "food", "medicine"
            const title = categoryNames[type] || type.toUpperCase();

            bodyHtml += `
                <div class="bucket-category">
                    <h3 class="bucket-category__title">${title}</h3>
                    <div class="bucket-items">
            `;

            // Проходимся по товарам внутри части
            part.items.forEach(item => {
                const totalPrice = item.price * item.quantity;
                totalOrderSum += totalPrice;

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
        });
    }

    // 4. Собираем всё вместе
    html += bodyHtml;
    html += `</div>`; // Закрываем bucket-card__body

    container.innerHTML = html;

    if (!hasItems) {
        container.innerHTML += `
            <div class="bucket-empty">
                <p>В корзине пока ничего нет.</p>
                <a href="/#services" class="btn btn--primary">Выбрать услугу</a>
            </div>
        `;
    } else {
        const footerHtml = `
            <div class="bucket-footer" style="display: block !important;">
                <div class="bucket-footer__total">
                    <span>Итого:</span>
                    <span>${cart.totalPrice || totalOrderSum} ₽</span>
                </div>
                <button class="btn btn--primary btn--full checkout-btn" style="margin-top: 16px;">
                    Оформить заказ
                </button>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', footerHtml);
    }
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