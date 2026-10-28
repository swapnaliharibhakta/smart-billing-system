import api from './axiosConfig';

export const getDashboard = () =>
  api.get('/api/dashboard/summary').then((r) => r.data);