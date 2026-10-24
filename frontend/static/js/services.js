document.addEventListener('DOMContentLoaded', () => {
    // 1. Берем тип из URL: /services/food -> "food"
    // pathname может быть "/services/food" или "/services/food/"
    const pathParts = window.location.pathname.split('/').filter(Boolean);
    const type = pathParts[pathParts.length - 1]; // последний непустой сегмент

    const catalogGrid = document.querySelector('.catalog__grid');
    if (!catalogGrid) return;

    // На случай, если type не определился
    if (!type) {
        showMessage(catalogGrid, 'Не удалось определить тип услуг');
        return;
    }

    catalogGrid.innerHTML = '<p style="grid-column: 1 / -1; text-align: center;">Загрузка товаров...</p>';

    // 2. Делаем запрос через ваш API
    API.get(`/services/data/${type}`)
        .then(res => {
            if (!res || !res.success) {
                showMessage(catalogGrid, (res && res.message) || 'Ошибка загрузки');
                return;
            }

            // 3. Достаем массив из data.items
            const items = res.data ? res.data.items : null;

            if (!items || !Array.isArray(items) || items.length === 0) {
                showMessage(catalogGrid, res.message || 'Товаров пока нет 🩷');
                return;
            }

            // 4. Рендерим
            renderItems(catalogGrid, items);
        })
        .catch(err => {
            console.error('Ошибка запроса:', err);
            showMessage(catalogGrid, err.message || 'Не удалось загрузить данные');
        });
});

function renderItems(container, items) {
    container.innerHTML = '';

    items.forEach(item => {
        const productDiv = document.createElement('div');
        productDiv.className = 'product';

        // Обработка цены
        const priceStr = item.price != null ? `${item.price} ₽` : 'Цена не указана';

        productDiv.innerHTML = `
            <div class="product__image">🩷</div>
            <div class="product__body">
                <h3 class="product__title">${item.name || 'Без названия'}</h3>
                <p class="product__text">${item.description || ''}</p>
                <div class="product__footer">
                    <span class="product__price">${priceStr}</span>
                    <button class="btn btn--primary product__btn" type="button">В корзину</button>
                </div>
            </div>
        `;

        container.appendChild(productDiv);
    });
}

function showMessage(container, text) {
    container.innerHTML = `<p style="grid-column: 1 / -1; text-align: center; font-size: 1.2rem; color: #666; padding: 20px;">${text}</p>`;
}