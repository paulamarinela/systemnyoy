const API_BASE_URL = '/api/v1';

async function apiRequest(endpoint, options = {}) {
    const token = localStorage.getItem('jwt_token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (response.status === 401 || response.status === 403) {
        // If unauthorized, clear token and redirect to login
        if (!endpoint.includes('/auth/login')) {
            localStorage.removeItem('jwt_token');
            window.location.reload();
        }
    }

    if (!response.ok) {
        const error = await response.text();
        throw new Error(error || 'API Request failed');
    }

    // Return null for 204 No Content or empty responses
    if (response.status === 204 || response.headers.get('content-length') === '0') {
        return null;
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
        return await response.json();
    }
    return await response.text();
}

export const api = {
    get: (endpoint) => apiRequest(endpoint, { method: 'GET' }),
    post: (endpoint, body) => apiRequest(endpoint, { method: 'POST', body: JSON.stringify(body) }),
    put: (endpoint, body) => apiRequest(endpoint, { method: 'PUT', body: JSON.stringify(body) }),
    patch: (endpoint, params) => {
        const url = new URL(`${window.location.origin}${API_BASE_URL}${endpoint}`);
        if (params) {
            Object.keys(params).forEach(key => url.searchParams.append(key, params[key]));
        }
        return apiRequest(endpoint + url.search, { method: 'PATCH' });
    },
    delete: (endpoint) => apiRequest(endpoint, { method: 'DELETE' }),

    // Special case for multipart/form-data (adding repairs with photos)
    postMultipart: async (endpoint, formData) => {
        const token = localStorage.getItem('jwt_token');
        const headers = {};
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers,
            body: formData,
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'API Request failed');
        }
        return await response.json();
    }
};