
const API = (() => {

    let accessToken = null;

    function setAccessToken(token) {
        accessToken = token;
    }

    function clearAccessToken() {
        accessToken = null;
    }

    function getAccessToken() {
        return accessToken;
    }

    async function doFetch(method, path, body) {
        const opts = {
            method,
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include'
        };

        if (accessToken) {
            opts.headers['Authorization'] = 'Bearer ' + accessToken;
        }
        if (body !== undefined) {
            opts.body = JSON.stringify(body);
        }

        return fetch(path, opts);
    }

    async function parse(res) {
        let data = null;
        const text = await res.text();
        if (text) {
            try { data = JSON.parse(text); } catch { /* не JSON */ }
        }
        return data;
    }

    async function request(method, path, body) {
        const res = await doFetch(method, path, body);

        const data = await parse(res);

        if (!res.ok) {
            const message = (data && data.message) || ('Ошибка ' + res.status);
            const err = new Error(message);
            err.code = data && data.code;
            err.data = data && data.data;
            throw err;
        }

        return data;
    }

    return {
        setAccessToken,
        clearAccessToken,
        getAccessToken,
        get:  (path)        => request('GET', path),
        post: (path, body)  => request('POST', path, body),
        put:  (path, body)  => request('PUT', path, body),
        del:  (path)        => request('DELETE', path)
    };
})();