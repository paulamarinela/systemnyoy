import { api } from './api.js';

export const auth = {
    async login(username, password) {
        try {
            const data = await api.post('/auth/login', { username, password });
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('username', data.username);
            localStorage.setItem('role', data.role);
            return data;
        } catch (error) {
            console.error('Login error:', error);
            throw error;
        }
    },

    logout() {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('username');
        localStorage.removeItem('role');
        window.location.reload();
    },

    isLoggedIn() {
        return !!localStorage.getItem('jwt_token');
    },

    getUsername() {
        return localStorage.getItem('username') || 'User';
    },

    getRole() {
        return localStorage.getItem('role') || 'STAFF';
    },

    async forgotPassword(email) {
        return await api.post(`/auth/forgot-password?email=${encodeURIComponent(email)}`);
    }
};