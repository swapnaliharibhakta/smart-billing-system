import api from './axiosConfig';

export const getInvoices = () =>
  api.get('/api/invoices', { params: { page: 0, size: 100 } })
     .then(r => (Array.isArray(r.data) ? r.data : r.data.content || []));

export const getPayments = (invoiceId) =>
  api.get(`/api/invoices/${invoiceId}/payments`).then(r => r.data);

export const addPayment = (invoiceId, body) =>
  api.post(`/api/invoices/${invoiceId}/payments`, body).then(r => r.data);

export const deletePayment = (id) => api.delete(`/api/payments/${id}`);