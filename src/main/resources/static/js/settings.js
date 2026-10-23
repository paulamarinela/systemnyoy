import { api } from './api.js';

export const settings = {
    async changePassword(oldPassword, newPassword) {
        return await api.post(`/settings/change-password?oldPassword=${encodeURIComponent(oldPassword)}&newPassword=${encodeURIComponent(newPassword)}`);
    },

    async getReceipt(repairId) {
        return await api.get(`/settings/receipt/${repairId}`);
    },

    async createBackup() {
        return await api.post('/settings/backup');
    }
};