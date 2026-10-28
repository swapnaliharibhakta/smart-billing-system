import api from './axiosConfig';

export const getSettings = () => api.get('/api/settings').then(r => r.data);

export const saveSettings = (body) => api.put('/api/settings', body).then(r => r.data);
