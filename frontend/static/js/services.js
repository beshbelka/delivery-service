document.addEventListener('DOMContentLoaded', () => {
    // 1. Берем тип из URL: /services/food -> "food"
    const pathParts = window.location.pathname.split('/').filter(Boolean);
    const type = pathParts[pathParts.length - 1];

    const catalogGrid = document.querySelector('.catalog__grid');
    if (!catalogGrid) return;

    if (!type) {
        showMessage(catalogGrid, 'Не удалось определить тип услуг');
        return;
    }

    catalogGrid.innerHTML = '<p style="grid-column: 1 / -1; text-align: center;">Загрузка товаров...</p>';

    // 2. Запрос через API
    API.get(`/api/services/data/${type}`)
        .then(res => {
            if (!res || !res.success) {
                showMessage(catalogGrid, (res && res.message) || 'Ошибка загрузки');
                return;
            }

            const items = res.data ? res.data.items : null;

            if (!items || !Array.isArray(items) || items.length === 0) {
                showMessage(catalogGrid, res.message || 'Товаров пока нет 🩷');
                return;
            }

            renderItems(catalogGrid, items);
        })
        .catch(err => {
            console.error('Ошибка запроса:', err);
            showMessage(catalogGrid, err.message || 'Не удалось загрузить данные');
        });

    // 3. Делегированный обработчик "В корзину"
    catalogGrid.addEventListener('click', (e) => {
        const btn = e.target.closest('.product__btn');
        if (!btn) return;

        const product = btn.closest('.product');
        const sku = product?.dataset.sku;
        if (!sku) return;

        btn.disabled = true;

        API.post('/api/services/add', { sku })
            .then(res => {
                if (!res || !res.success) {
                    notify((res && res.message) || 'Не удалось добавить в корзину');
                    return;
                }
                notify(res.message || 'Добавлено в корзину');
            })
            .catch(err => {
                notify(err.message || 'Не удалось добавить в корзину');
            })
            .finally(() => {
                btn.disabled = false;
            });
    });
});

function renderItems(container, items) {
    container.innerHTML = '';

    items.forEach(item => {
        const productDiv = document.createElement('div');
        productDiv.className = 'product';
        productDiv.dataset.sku = item.sku ?? '';

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

function notify(text) {
    // Если есть готовая система уведомлений — замените тело функции на её вызов.
    // Пока — простой тост.
    let toast = document.getElementById('app-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'app-toast';
        toast.style.cssText = [
            'position: fixed',
            'left: 50%',
            'bottom: 24px',
            'transform: translateX(-50%)',
            'background: #222',
            'color: #fff',
            'padding: 10px 16px',
            'border-radius: 8px',
            'font-size: 14px',
            'z-index: 9999',
            'opacity: 0',
            'transition: opacity .2s',
            'pointer-events: none'
        ].join(';');
        document.body.appendChild(toast);
    }
    toast.textContent = text;
    toast.style.opacity = '1';
    clearTimeout(toast._t);
    toast._t = setTimeout(() => { toast.style.opacity = '0'; }, 2500);
}