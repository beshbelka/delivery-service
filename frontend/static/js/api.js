const API = {
    async post(path, body) {
        const res = await fetch(path, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const data = await res.json().catch(() => ({}));
        if (!res.ok) {
            throw new Error(data.message || 'Ошибка запроса');
        }
        return data;
    },

    async get(path) {
        const res = await fetch('/api' + path);
        const data = await res.json().catch(() => ({}));
        if (!res.ok) {
            throw new Error(data.message || 'Ошибка запроса');
        }
        return data;
    }
};