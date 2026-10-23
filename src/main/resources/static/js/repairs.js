import { api } from './api.js';

export const repairs = {
    async getAll() {
        return await api.get('/repairs');
    },

    async add(repairData, photoFile) {
        const formData = new FormData();
        formData.append('repair', JSON.stringify(repairData));
        if (photoFile) {
            formData.append('photo', photoFile);
        }
        return await api.postMultipart('/repairs', formData);
    },

    async update(id, repairData) {
        return await api.put(`/repairs/${id}`, repairData);
    },

    async delete(id) {
        return await api.delete(`/repairs/${id}`);
    },

    async updateStatus(id, status) {
        return await api.patch(`/repairs/${id}/status`, { status });
    },

    async updatePayment(id, status) {
        return await api.patch(`/repairs/${id}/payment`, { status });
    },

    async notify(id) {
        return await api.post(`/repairs/${id}/notify`);
    }
};