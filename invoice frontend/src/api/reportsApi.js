import api from './axiosConfig';

export const getReportSummary = (from, to) =>
  api.get('/api/reports/summary', { params: { from, to } }).then(r => r.data);
