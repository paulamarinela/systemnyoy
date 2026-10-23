import { api } from './api.js';

export const reports = {
    async getRevenue() {
        return await api.get('/reports/revenue');
    },

    async getStatusSummary() {
        return await api.get('/reports/status-summary');
    },

    async getStorageStatus() {
        return await api.get('/reports/storage-status');
    },

    async getFreeSpace() {
        return await api.get('/reports/free-space');
    },

    async getPaidRepairs(start, end) {
        return await api.get(`/reports/paid-repairs?start=${start}&end=${end}`);
    }
};