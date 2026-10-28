import { useEffect, useState, useCallback } from 'react';
import {
  Box, Paper, Grid, Card, CardContent, Typography, TextField, Button,
  Table, TableHead, TableRow, TableCell, TableBody, Alert, CircularProgress,
  Chip,
} from '@mui/material';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell,
} from 'recharts';
import { getReportSummary } from '../api/reportsApi';

const inr = n => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const STATUS_COLORS = {
  DRAFT: '#9E9E9E', SENT: '#2196F3', PARTIALLY_PAID: '#FB8C00',
  PAID: '#2E7D32', OVERDUE: '#D32F2F', CANCELLED: '#795548',
};

function KpiCard({ label, value, color }) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="body2" color="text.secondary">{label}</Typography>
        <Typography variant="h6" sx={{ fontWeight: 700, color, mt: 0.5 }}>{value}</Typography>
      </CardContent>
    </Card>
  );
}

const thisYear = new Date().getFullYear();
const defaultFrom = `${thisYear}-01-01`;
const defaultTo = new Date().toISOString().slice(0, 10);

export default function Reports() {
  const [from, setFrom] = useState(defaultFrom);
  const [to, setTo] = useState(defaultTo);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(() => {
    if (!from || !to || from > to) {
      setError('Please select a valid date range');
      return;
    }
    setError('');
    setLoading(true);
    getReportSummary(from, to)
      .then(setData)
      .catch(() => setError('Failed to load report data'))
      .finally(() => setLoading(false));
  }, [from, to]);

  useEffect(() => { load(); }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const donut = data
    ? Object.entries(data.statusCounts).filter(([, v]) => v > 0).map(([name, value]) => ({ name, value }))
    : [];

  const exportCsv = () => {
    if (!data) return;
    const rows = [
      ['Report', `${from} to ${to}`],
      [],
      ['Summary'],
      ['Total Invoiced', data.stats.totalInvoiced],
      ['Total Paid', data.stats.totalPaid],
      ['Pending', data.stats.totalPending],
      ['Overdue', data.stats.totalOverdue],
      ['Invoice Count', data.stats.invoiceCount],
      [],
      ['Monthly Revenue'],
      ['Month', 'Amount'],
      ...data.monthlyRevenue.map(r => [r.month, r.amount]),
      [],
      ['Top Customers'],
      ['Customer', 'Invoices', 'Total Invoiced', 'Total Paid'],
      ...data.topCustomers.map(c => [c.name, c.invoiceCount, c.totalInvoiced, c.totalPaid]),
      [],
      ['GST Summary'],
      ['Taxable Amount', data.gstSummary.taxableAmount],
      ['Total Tax', data.gstSummary.totalTax],
    ];
    const csv = rows.map(r => r.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `report_${from}_${to}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Box>
      {/* ── Date range filter ── */}
      <Paper sx={{ p: 2, mb: 3, display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
        <TextField label="From" type="date" size="small" value={from}
          onChange={e => setFrom(e.target.value)}
          slotProps={{ inputLabel: { shrink: true } }} />
        <TextField label="To" type="date" size="small" value={to}
          onChange={e => setTo(e.target.value)}
          slotProps={{ inputLabel: { shrink: true } }} />
        <Button variant="contained" onClick={load} disabled={loading}
          sx={{ bgcolor: '#5B45D1', '&:hover': { bgcolor: '#4a37b8' } }}>
          {loading ? 'Loading…' : 'Apply'}
        </Button>
        <Button variant="outlined" onClick={exportCsv} disabled={!data}
          sx={{ ml: 'auto', borderColor: '#5B45D1', color: '#5B45D1' }}>
          Export CSV
        </Button>
      </Paper>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading && <Box sx={{ textAlign: 'center', py: 6 }}><CircularProgress /></Box>}

      {!loading && !data && !error && (
        <Typography color="text.secondary" sx={{ textAlign: 'center', py: 6 }}>
          No data for the selected range.
        </Typography>
      )}

      {!loading && data && (
        <>
          {/* ── KPI cards ── */}
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <KpiCard label="Total Invoiced" value={inr(data.stats.totalInvoiced)} color="#14152D" />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <KpiCard label="Total Paid" value={inr(data.stats.totalPaid)} color="#2E7D32" />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <KpiCard label="Pending" value={inr(data.stats.totalPending)} color="#FB8C00" />
            </Grid>
            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <KpiCard label="Overdue" value={inr(data.stats.totalOverdue)} color="#D32F2F" />
            </Grid>
          </Grid>

          {/* ── Charts ── */}
          <Grid container spacing={3} sx={{ mb: 3 }}>
            <Grid size={{ xs: 12, md: 7 }}>
              <Card>
                <CardContent>
                  <Typography variant="h6">Monthly Revenue</Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>Payments received per month</Typography>
                  {data.monthlyRevenue.length === 0 ? (
                    <Typography color="text.secondary">No payment data in this range</Typography>
                  ) : (
                    <Box sx={{ height: 260 }}>
                      <ResponsiveContainer width="100%" height="100%">
                        <BarChart data={data.monthlyRevenue}>
                          <CartesianGrid strokeDasharray="3 3" vertical={false} />
                          <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                          <YAxis tickFormatter={v => v >= 1000 ? `${v / 1000}K` : v} />
                          <Tooltip formatter={v => inr(v)} />
                          <Bar dataKey="amount" fill="#5B45D1" radius={[6, 6, 0, 0]} />
                        </BarChart>
                      </ResponsiveContainer>
                    </Box>
                  )}
                </CardContent>
              </Card>
            </Grid>

            <Grid size={{ xs: 12, md: 5 }}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Typography variant="h6" sx={{ mb: 1 }}>Invoice Status</Typography>
                  {donut.length === 0 ? (
                    <Typography color="text.secondary">No invoices in this range</Typography>
                  ) : (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
                      <Box sx={{ position: 'relative', width: 200, height: 200 }}>
                        <ResponsiveContainer>
                          <PieChart>
                            <Pie data={donut} dataKey="value" nameKey="name"
                              innerRadius={60} outerRadius={90} paddingAngle={2}>
                              {donut.map(d => <Cell key={d.name} fill={STATUS_COLORS[d.name]} />)}
                            </Pie>
                            <Tooltip />
                          </PieChart>
                        </ResponsiveContainer>
                        <Box sx={{ position: 'absolute', inset: 0, display: 'flex',
                          flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
                          pointerEvents: 'none' }}>
                          <Typography variant="body2" color="text.secondary">Total</Typography>
                          <Typography variant="h6" sx={{ fontWeight: 700 }}>{data.stats.invoiceCount}</Typography>
                        </Box>
                      </Box>
                      <Box sx={{ flex: 1, minWidth: 120 }}>
                        {donut.map(d => (
                          <Box key={d.name} sx={{ display: 'flex', alignItems: 'center', py: 0.4 }}>
                            <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: STATUS_COLORS[d.name], mr: 1 }} />
                            <Typography variant="body2" sx={{ flex: 1 }}>{d.name.replace('_', ' ')}</Typography>
                            <Typography variant="body2" sx={{ fontWeight: 600 }}>{d.value}</Typography>
                          </Box>
                        ))}
                      </Box>
                    </Box>
                  )}
                </CardContent>
              </Card>
            </Grid>
          </Grid>

          {/* ── Top customers + GST summary ── */}
          <Grid container spacing={3}>
            <Grid size={{ xs: 12, md: 7 }}>
              <Card>
                <CardContent>
                  <Typography variant="h6" sx={{ mb: 1 }}>Top 5 Customers</Typography>
                  {data.topCustomers.length === 0 ? (
                    <Typography color="text.secondary">No data</Typography>
                  ) : (
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>Customer</TableCell>
                          <TableCell align="right">Invoices</TableCell>
                          <TableCell align="right">Invoiced</TableCell>
                          <TableCell align="right">Paid</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {data.topCustomers.map((c, i) => (
                          <TableRow key={i} hover>
                            <TableCell>{c.name}</TableCell>
                            <TableCell align="right">{c.invoiceCount}</TableCell>
                            <TableCell align="right">{inr(c.totalInvoiced)}</TableCell>
                            <TableCell align="right">{inr(c.totalPaid)}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  )}
                </CardContent>
              </Card>
            </Grid>

            <Grid size={{ xs: 12, md: 5 }}>
              <Card>
                <CardContent>
                  <Typography variant="h6" sx={{ mb: 2 }}>GST Summary</Typography>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography color="text.secondary">Taxable Amount</Typography>
                    <Typography fontWeight={600}>{inr(data.gstSummary.taxableAmount)}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography color="text.secondary">CGST (50%)</Typography>
                    <Typography>{inr(Number(data.gstSummary.totalTax) / 2)}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography color="text.secondary">SGST (50%)</Typography>
                    <Typography>{inr(Number(data.gstSummary.totalTax) / 2)}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', pt: 1, borderTop: '1px solid', borderColor: 'divider' }}>
                    <Typography fontWeight={700}>Total Tax</Typography>
                    <Typography fontWeight={700} color="#5B45D1">{inr(data.gstSummary.totalTax)}</Typography>
                  </Box>
                  <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block' }}>
                    CGST/SGST split assumes intra-state. IGST breakdown requires per-invoice state data (not in current schema).
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </>
      )}
    </Box>
  );
}
