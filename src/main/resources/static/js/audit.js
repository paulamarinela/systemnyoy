import { api } from './api.js';

export const audit = {
    async getAll() {
        return await api.get('/audit');
    },

    async getByUser(username) {
        return await api.get(`/audit/user/${username}`);
    }
};