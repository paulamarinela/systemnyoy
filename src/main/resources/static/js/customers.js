import { api } from './api.js';

export const customers = {
    async getAll() {
        return await api.get('/customers');
    },

    async search(name) {
        return await api.get(`/customers/search?name=${encodeURIComponent(name)}`);
    },

    async getHistory(phone) {
        return await api.get(`/customers/${phone}/history`);
    }
};