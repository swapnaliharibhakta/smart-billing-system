import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import {
  Box, Paper, Tabs, Tab, TextField, Button, Alert, Snackbar,
  CircularProgress, Typography, Divider,
} from '@mui/material';
import { getSettings, saveSettings } from '../api/settingsApi';

const GSTIN_RE = /^\d{2}[A-Z]{5}\d{4}[A-Z][1-9A-Z]Z[0-9A-Z]$/;
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_RE = /^[6-9]\d{9}$/;

function Field({ label, name, value, onChange, error, helperText, multiline, rows, type = 'text', inputProps }) {
  return (
    <TextField
      label={label} name={name} value={value ?? ''} onChange={onChange}
      fullWidth margin="normal" size="small" type={type}
      multiline={multiline} rows={rows}
      error={!!error} helperText={error || helperText}
      slotProps={inputProps ? { htmlInput: inputProps } : undefined}
    />
  );
}

export default function Settings() {
  const reduxUser = useSelector(s => s.auth.user);
  const [tab, setTab] = useState(0);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [snack, setSnack] = useState({ open: false, msg: '', severity: 'success' });

  // Business + Invoice fields (from /api/settings)
  const [biz, setBiz] = useState({
    companyName: '', address: '', gstNumber: '', email: '', phone: '',
  });
  const [inv, setInv] = useState({ invoicePrefix: 'INV', defaultDueDays: 30, nextInvoiceNo: 1 });
  const [bizErr, setBizErr] = useState({});
  const [invErr, setInvErr] = useState({});

  // Profile fields (read from Redux; no update endpoint exists)
  const [profile] = useState({
    name: reduxUser?.name ?? '',
    email: reduxUser?.email ?? '',
  });

  useEffect(() => {
    getSettings()
      .then(d => {
        setBiz({
          companyName: d.companyName ?? '',
          address: d.address ?? '',
          gstNumber: d.gstNumber ?? '',
          email: d.email ?? '',
          phone: d.phone ?? '',
        });
        setInv({
          invoicePrefix: d.invoicePrefix ?? 'INV',
          defaultDueDays: d.defaultDueDays ?? 30,
          nextInvoiceNo: d.nextInvoiceNo ?? 1,
        });
      })
      .catch(() => setSnack({ open: true, msg: 'Failed to load settings', severity: 'error' }))
      .finally(() => setLoading(false));
  }, []);

  const handleBiz = e => setBiz({ ...biz, [e.target.name]: e.target.value });
  const handleInv = e => setInv({ ...inv, [e.target.name]: e.target.value });

  const validateBiz = () => {
    const e = {};
    if (!biz.companyName.trim()) e.companyName = 'Company name is required';
    if (biz.email && !EMAIL_RE.test(biz.email)) e.email = 'Invalid email';
    if (biz.phone && !PHONE_RE.test(biz.phone)) e.phone = 'Enter a valid 10-digit mobile number';
    if (biz.gstNumber && !GSTIN_RE.test(biz.gstNumber)) e.gstNumber = 'Invalid GSTIN (e.g. 27ABCDE1234F1Z5)';
    setBizErr(e);
    return Object.keys(e).length === 0;
  };

  const validateInv = () => {
    const e = {};
    if (!inv.invoicePrefix.trim()) e.invoicePrefix = 'Prefix is required';
    if (!inv.defaultDueDays || Number(inv.defaultDueDays) < 1) e.defaultDueDays = 'Must be at least 1';
    setInvErr(e);
    return Object.keys(e).length === 0;
  };

  const saveBiz = async () => {
    if (!validateBiz()) return;
    setSaving(true);
    try {
      const d = await saveSettings({
        companyName: biz.companyName.trim(),
        address: biz.address || null,
        gstNumber: biz.gstNumber || null,
        email: biz.email || null,
        phone: biz.phone || null,
        invoicePrefix: inv.invoicePrefix,
        defaultDueDays: Number(inv.defaultDueDays),
      });
      setBiz({ companyName: d.companyName ?? '', address: d.address ?? '', gstNumber: d.gstNumber ?? '', email: d.email ?? '', phone: d.phone ?? '' });
      setSnack({ open: true, msg: 'Business settings saved', severity: 'success' });
    } catch (err) {
      setSnack({ open: true, msg: err.response?.data?.message || 'Save failed', severity: 'error' });
    } finally {
      setSaving(false);
    }
  };

  const saveInv = async () => {
    if (!validateInv()) return;
    setSaving(true);
    try {
      const d = await saveSettings({
        companyName: biz.companyName || 'My Company',
        address: biz.address || null,
        gstNumber: biz.gstNumber || null,
        email: biz.email || null,
        phone: biz.phone || null,
        invoicePrefix: inv.invoicePrefix.trim(),
        defaultDueDays: Number(inv.defaultDueDays),
      });
      setInv({ invoicePrefix: d.invoicePrefix, defaultDueDays: d.defaultDueDays, nextInvoiceNo: d.nextInvoiceNo });
      setSnack({ open: true, msg: 'Invoice settings saved', severity: 'success' });
    } catch (err) {
      setSnack({ open: true, msg: err.response?.data?.message || 'Save failed', severity: 'error' });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Box sx={{ p: 4, textAlign: 'center' }}><CircularProgress /></Box>;

  return (
    <Paper sx={{ p: 3, maxWidth: 680 }}>
      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 3, borderBottom: 1, borderColor: 'divider' }}>
        <Tab label="Business" />
        <Tab label="Invoice" />
        <Tab label="Profile" />
      </Tabs>

      {/* ── BUSINESS TAB ── */}
      {tab === 0 && (
        <Box>
          <Field label="Company Name *" name="companyName" value={biz.companyName} onChange={handleBiz} error={bizErr.companyName} />
          <Field label="GSTIN" name="gstNumber" value={biz.gstNumber} onChange={e => setBiz({ ...biz, gstNumber: e.target.value.toUpperCase().slice(0, 15) })} error={bizErr.gstNumber} helperText="e.g. 27ABCDE1234F1Z5" />
          <Field label="Email" name="email" value={biz.email} onChange={handleBiz} error={bizErr.email} />
          <Field label="Phone" name="phone" value={biz.phone} onChange={e => setBiz({ ...biz, phone: e.target.value.replace(/\D/g, '').slice(0, 10) })} error={bizErr.phone} />
          <Field label="Address" name="address" value={biz.address} onChange={handleBiz} multiline rows={3} />
          <Box sx={{ mt: 2 }}>
            <Button variant="contained" onClick={saveBiz} disabled={saving}
              sx={{ bgcolor: '#5B45D1', '&:hover': { bgcolor: '#4a37b8' } }}>
              {saving ? 'Saving…' : 'Save Business Settings'}
            </Button>
          </Box>
          <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
            Note: PAN number and company logo are not yet supported by the backend.
          </Typography>
        </Box>
      )}

      {/* ── INVOICE TAB ── */}
      {tab === 1 && (
        <Box>
          <Field label="Invoice Prefix" name="invoicePrefix" value={inv.invoicePrefix} onChange={handleInv} error={invErr.invoicePrefix} helperText="e.g. INV, BILL, GST" />
          <Field label="Default Due Days" name="defaultDueDays" value={inv.defaultDueDays} onChange={handleInv} error={invErr.defaultDueDays} type="number" inputProps={{ min: 1 }} helperText="Days after issue date before invoice is due" />
          <TextField
            label="Next Invoice Number" value={inv.nextInvoiceNo} fullWidth margin="normal" size="small"
            disabled helperText="Auto-incremented by the system — read only"
          />
          <Box sx={{ mt: 2 }}>
            <Button variant="contained" onClick={saveInv} disabled={saving}
              sx={{ bgcolor: '#5B45D1', '&:hover': { bgcolor: '#4a37b8' } }}>
              {saving ? 'Saving…' : 'Save Invoice Settings'}
            </Button>
          </Box>
          <Typography variant="caption" color="text.secondary" sx={{ mt: 2, display: 'block' }}>
            Note: Default GST % and invoice terms/notes fields are not yet in the backend schema.
          </Typography>
        </Box>
      )}

      {/* ── PROFILE TAB ── */}
      {tab === 2 && (
        <Box>
          <TextField label="Name" value={profile.name} fullWidth margin="normal" size="small" disabled />
          <TextField label="Email" value={profile.email} fullWidth margin="normal" size="small" disabled />
          <Divider sx={{ my: 2 }} />
          <Alert severity="info">
            Profile editing and password change are not yet supported by the backend.
            A <code>PUT /api/users/me</code> endpoint needs to be added.
          </Alert>
        </Box>
      )}

      <Snackbar
        open={snack.open} autoHideDuration={3500}
        onClose={() => setSnack(s => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
        <Alert severity={snack.severity} onClose={() => setSnack(s => ({ ...s, open: false }))}>
          {snack.msg}
        </Alert>
      </Snackbar>
    </Paper>
  );
}
