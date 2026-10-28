import { useEffect, useState } from 'react';
import {
  Box, Card, CardContent, Typography, Grid, Chip, Alert, CircularProgress,
  Table, TableHead, TableRow, TableCell, TableBody,
} from '@mui/material';
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell,
} from 'recharts';
import { getDashboard } from '../api/dashboardApi';

const inr = (n) =>
  '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const fmtDate = (d) => (d ? new Date(d).toLocaleDateString('en-GB') : '');

const STATUS_COLORS = {
  DRAFT: '#9E9E9E', SENT: '#2196F3', PARTIALLY_PAID: '#FB8C00',
  PAID: '#2E7D32', OVERDUE: '#D32F2F', CANCELLED: '#795548',
};
const STATUS_LABEL = (s) => s.replace('_', ' ');

function Kpi({ label, value, sub, color }) {
  return (
    <Card sx={{ height: '100%' }}>
      <CardContent>
        <Typography variant="body2" color="text.secondary">{label}</Typography>
        <Typography variant="h5" sx={{ fontWeight: 700, color, mt: 0.5 }}>{value}</Typography>
        {sub && <Typography variant="body2" sx={{ color }}>{sub}</Typography>}
      </CardContent>
    </Card>
  );
}

function StatusChip({ status }) {
  return (
    <Chip size="small" label={STATUS_LABEL(status)}
      sx={{ bgcolor: STATUS_COLORS[status], color: '#fff', fontWeight: 600 }} />
  );
}

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    getDashboard().then(setData).catch(() => setError('Failed to load dashboard'));
  }, []);

  if (error) return <Alert severity="error">{error}</Alert>;
  if (!data) return <Box sx={{ p: 4, textAlign: 'center' }}><CircularProgress /></Box>;

  const { stats, monthlyRevenue, statusCounts, recentInvoices, dueSoon } = data;
  const donut = Object.entries(statusCounts)
    .filter(([, v]) => v > 0)
    .map(([name, value]) => ({ name, value }));
  const today = new Date().toISOString().slice(0, 10);

  return (
    <Box>
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Kpi label="Total Invoiced" value={inr(stats.totalInvoiced)} color="#14152D" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Kpi label="Total Received" value={inr(stats.totalReceived)}
            sub={`This month: ${inr(stats.receivedThisMonth)}`} color="#2E7D32" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Kpi label="Outstanding" value={inr(stats.pendingAmount)}
            sub={`${stats.pendingCount} open invoices`} color="#FB8C00" />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Kpi label="Overdue" value={`${stats.overdueCount} invoices`}
            sub={inr(stats.overdueAmount)} color="#D32F2F" />
        </Grid>
      </Grid>

      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid size={{ xs: 12, md: 7 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6">Monthly Revenue</Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>Last 6 months</Typography>
              <Box sx={{ height: 280 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={monthlyRevenue}>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis dataKey="month" />
                    <YAxis tickFormatter={(v) => (v >= 1000 ? `${v / 1000}K` : v)} />
                    <Tooltip formatter={(v) => inr(v)} />
                    <Bar dataKey="amount" fill="#5046B8" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 5 }}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 1 }}>Invoice Status</Typography>
              {donut.length === 0 ? (
                <Typography color="text.secondary">No invoices yet</Typography>
              ) : (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, flexWrap: 'wrap' }}>
                  <Box sx={{ position: 'relative', width: 220, height: 220 }}>
                    <ResponsiveContainer>
                      <PieChart>
                        <Pie data={donut} dataKey="value" nameKey="name"
                          innerRadius={65} outerRadius={100} paddingAngle={2}>
                          {donut.map((d) => <Cell key={d.name} fill={STATUS_COLORS[d.name]} />)}
                        </Pie>
                        <Tooltip />
                      </PieChart>
                    </ResponsiveContainer>
                    <Box sx={{ position: 'absolute', inset: 0, display: 'flex',
                      flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
                      pointerEvents: 'none' }}>
                      <Typography variant="body2" color="text.secondary">Total</Typography>
                      <Typography variant="h5" sx={{ fontWeight: 700 }}>{stats.totalInvoices}</Typography>
                    </Box>
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 140 }}>
                    {donut.map((d) => (
                      <Box key={d.name} sx={{ display: 'flex', alignItems: 'center', py: 0.5 }}>
                        <Box sx={{ width: 10, height: 10, borderRadius: '50%',
                          bgcolor: STATUS_COLORS[d.name], mr: 1 }} />
                        <Typography variant="body2" sx={{ flex: 1 }}>{STATUS_LABEL(d.name)}</Typography>
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

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, md: 6 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 1 }}>Recent Invoices</Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Invoice No</TableCell><TableCell>Customer</TableCell>
                    <TableCell align="right">Total</TableCell><TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {recentInvoices.length === 0 && (
                    <TableRow><TableCell colSpan={4}>No invoices yet</TableCell></TableRow>
                  )}
                  {recentInvoices.map((i) => (
                    <TableRow key={i.id}>
                      <TableCell>{i.invoiceNumber}</TableCell>
                      <TableCell>{i.customerName}</TableCell>
                      <TableCell align="right">{inr(i.total)}</TableCell>
                      <TableCell><StatusChip status={i.status} /></TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>

        <Grid size={{ xs: 12, md: 6 }}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 1 }}>Due Soon / Overdue</Typography>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Invoice No</TableCell><TableCell>Customer</TableCell>
                    <TableCell>Due Date</TableCell><TableCell align="right">Balance</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {dueSoon.length === 0 && (
                    <TableRow><TableCell colSpan={4}>Nothing due soon</TableCell></TableRow>
                  )}
                  {dueSoon.map((i) => (
                    <TableRow key={i.id}>
                      <TableCell>{i.invoiceNumber}</TableCell>
                      <TableCell>{i.customerName}</TableCell>
                      <TableCell sx={{ color: i.dueDate < today ? 'error.main' : 'inherit' }}>
                        {fmtDate(i.dueDate)}
                      </TableCell>
                      <TableCell align="right">{inr(i.balance)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}