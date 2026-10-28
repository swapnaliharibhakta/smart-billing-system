import { useEffect, useState, useCallback } from 'react';
import {
  Box, Card, CardContent, Typography, TextField, MenuItem, Button, Alert,
  Table, TableHead, TableRow, TableCell, TableBody, IconButton, Stack, Chip
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { getInvoices, getPayments, addPayment, deletePayment } from '../api/paymentsApi';

const METHODS = ['CASH', 'UPI', 'CARD', 'BANK_TRANSFER'];
const PAYABLE = ['SENT', 'PARTIALLY_PAID', 'OVERDUE'];
const today = () => new Date().toISOString().slice(0, 10);
const fmt = (n) => Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 });

export default function Payments() {
  const [invoices, setInvoices] = useState([]);
  const [invoiceId, setInvoiceId] = useState('');
  const [payments, setPayments] = useState([]);
  const [form, setForm] = useState({ amount: '', method: 'UPI', paidOn: today(), referenceNo: '', notes: '' });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadInvoices = useCallback(() => {
    getInvoices().then(list => setInvoices(list.filter(i => PAYABLE.includes(i.status))))
      .catch(() => setError('Failed to load invoices'));
  }, []);

  const loadPayments = useCallback((id) => {
    if (!id) return setPayments([]);
    getPayments(id).then(setPayments).catch(() => setError('Failed to load payments'));
  }, []);

  useEffect(() => { loadInvoices(); }, [loadInvoices]);
  useEffect(() => { loadPayments(invoiceId); }, [invoiceId, loadPayments]);

  const selected = invoices.find(i => i.id === invoiceId);
  const balance = selected ? Number(selected.total) - Number(selected.amountPaid) : 0;

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError(''); setSuccess('');
    try {
      await addPayment(invoiceId, {
        amount: Number(form.amount),
        method: form.method,
        paidOn: form.paidOn || null,
        referenceNo: form.referenceNo || null,
        notes: form.notes || null,
      });
      setSuccess('Payment saved successfully');
      setForm({ amount: '', method: 'UPI', paidOn: today(), referenceNo: '', notes: '' });
      loadPayments(invoiceId);
      loadInvoices();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save payment');
    }
  };

  const remove = async (id) => {
    if (!window.confirm('Delete this payment?')) return;
    try {
      await deletePayment(id);
      loadPayments(invoiceId);
      loadInvoices();
    } catch (err) {
      setError(err.response?.status === 403 ? 'Only an admin can delete payments' : 'Failed to delete payment');
    }
  };

  return (
    <Box>
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>Add Payment</Typography>
          {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>{success}</Alert>}

          <form onSubmit={submit}>
            <Stack spacing={2} maxWidth={520}>
              <TextField select label="Invoice" value={invoiceId}
                onChange={(e) => setInvoiceId(e.target.value)} required
                helperText={invoices.length === 0 ? 'No SENT / PARTIALLY_PAID / OVERDUE invoices available' : ''}>
                {invoices.map(i => (
                  <MenuItem key={i.id} value={i.id}>
                    {i.invoiceNumber} — {i.customerName || i.customer?.name || ''} — Balance {fmt(Number(i.total) - Number(i.amountPaid))}
                  </MenuItem>
                ))}
              </TextField>

              {selected && (
                <Stack direction="row" spacing={1}>
                  <Chip label={`Total: ${fmt(selected.total)}`} />
                  <Chip label={`Paid: ${fmt(selected.amountPaid)}`} color="success" />
                  <Chip label={`Balance: ${fmt(balance)}`} color="warning" />
                </Stack>
              )}

              <TextField label="Amount" type="number" value={form.amount} onChange={set('amount')}
                required slotProps={{ htmlInput: { min: 0.01, max: balance || undefined, step: '0.01' } }} />
              <TextField select label="Method" value={form.method} onChange={set('method')}>
                {METHODS.map(m => <MenuItem key={m} value={m}>{m}</MenuItem>)}
              </TextField>
              <TextField label="Paid on" type="date" value={form.paidOn} onChange={set('paidOn')}
                slotProps={{ inputLabel: { shrink: true } }} />
              <TextField label="Reference no (UPI ref / txn id)" value={form.referenceNo} onChange={set('referenceNo')} />
              <TextField label="Notes" value={form.notes} onChange={set('notes')} />
              <Button type="submit" variant="contained" disabled={!invoiceId || !form.amount}>Save Payment</Button>
            </Stack>
          </form>
        </CardContent>
      </Card>

      {invoiceId && (
        <Card>
          <CardContent>
            <Typography variant="h6" gutterBottom>Payment History</Typography>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Date</TableCell><TableCell>Method</TableCell>
                  <TableCell align="right">Amount</TableCell><TableCell>Reference</TableCell>
                  <TableCell />
                </TableRow>
              </TableHead>
              <TableBody>
                {payments.length === 0 && (
                  <TableRow><TableCell colSpan={5}>No payments yet</TableCell></TableRow>
                )}
                {payments.map(p => (
                  <TableRow key={p.id}>
                    <TableCell>{p.paidOn}</TableCell>
                    <TableCell>{p.method}</TableCell>
                    <TableCell align="right">{fmt(p.amount)}</TableCell>
                    <TableCell>{p.referenceNo}</TableCell>
                    <TableCell align="right">
                      <IconButton size="small" onClick={() => remove(p.id)}><DeleteIcon fontSize="small" /></IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}
    </Box>
  );
}