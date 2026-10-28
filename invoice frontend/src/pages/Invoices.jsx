import { useEffect, useMemo, useState } from "react";
import {
  Box, Paper, Typography, TextField, Button, Table, TableHead, TableRow,
  TableCell, TableBody, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, Alert, InputAdornment, Chip, Autocomplete,
  Divider, Tooltip,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import VisibilityIcon from "@mui/icons-material/Visibility";
import SendIcon from "@mui/icons-material/Send";
import CancelIcon from "@mui/icons-material/Cancel";
import DeleteIcon from "@mui/icons-material/Delete";
import SearchIcon from "@mui/icons-material/Search";
import api from "../api/axiosConfig";

const money = (v) =>
  Number(v ?? 0).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const round2 = (n) => Math.round((n + Number.EPSILON) * 100) / 100;

const today = () => new Date().toISOString().slice(0, 10);

const formatDate = (d) => {
  if (!d) return "";
  const [y, m, day] = d.split("-");
  return `${day}/${m}/${y}`;
};

const statusColor = {
  DRAFT: "default",
  SENT: "info",
  PARTIALLY_PAID: "warning",
  PAID: "success",
  OVERDUE: "error",
  CANCELLED: "default",
};

const statusLabel = (s) => s.replace("_", " ");

const emptyItem = () => ({
  product: null,
  productId: null,
  description: "",
  quantity: "1",
  unitPrice: "",
  taxRate: "0",
});

const emptyForm = () => ({
  customer: null,
  issueDate: today(),
  dueDate: "",
  notes: "",
});

export default function Invoices() {
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [q, setQ] = useState("");
  const [reload, setReload] = useState(0);

  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);

  const [open, setOpen] = useState(false);
  const [readOnly, setReadOnly] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm());
  const [items, setItems] = useState([emptyItem()]);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
  const [cancelTarget, setCancelTarget] = useState(null);
  const [sendTarget, setSendTarget] = useState(null);

  // list
  useEffect(() => {
    const t = setTimeout(async () => {
      try {
        const { data } = await api.get("/api/invoices", { params: { q, page, size } });
        setRows(data.content);
        setTotal(data.totalElements);
        setError("");
      } catch {
        setError("Could not load invoices");
      }
    }, 300);
    return () => clearTimeout(t);
  }, [q, page, size, reload]);

  // dropdown data (customers + active products)
  useEffect(() => {
    (async () => {
      try {
        const [c, p] = await Promise.all([
          api.get("/api/customers", { params: { q: "", page: 0, size: 200 } }),
          api.get("/api/products", { params: { q: "", page: 0, size: 200 } }),
        ]);
        setCustomers(c.data.content);
        setProducts(p.data.content.filter((x) => x.active));
      } catch {
        // silently ignore dropdown load errors
      }
    })();
  }, []);

  // live totals
  const totals = useMemo(() => {
    let subtotal = 0;
    let tax = 0;
    items.forEach((it) => {
      const line = round2((Number(it.quantity) || 0) * (Number(it.unitPrice) || 0));
      subtotal += line;
      tax += round2((line * (Number(it.taxRate) || 0)) / 100);
    });
    return { subtotal: round2(subtotal), tax: round2(tax), total: round2(subtotal + tax) };
  }, [items]);

  const openAdd = () => {
    setEditingId(null);
    setReadOnly(false);
    setForm(emptyForm());
    setItems([emptyItem()]);
    setFormError("");
    setOpen(true);
  };

  const openExisting = (inv, ro) => {
    setEditingId(inv.id);
    setReadOnly(ro);
    setForm({
      customer: customers.find((c) => c.id === inv.customerId) || {
        id: inv.customerId,
        name: inv.customerName,
      },
      issueDate: inv.issueDate || "",
      dueDate: inv.dueDate || "",
      notes: inv.notes || "",
    });
    setItems(
      inv.items.map((it) => ({
        product: products.find((p) => p.id === it.productId) || null,
        productId: it.productId,
        description: it.description,
        quantity: String(it.quantity),
        unitPrice: String(it.unitPrice),
        taxRate: String(it.taxRate ?? 0),
      }))
    );
    setFormError("");
    setOpen(true);
  };

  const updateItem = (index, patch) =>
    setItems((prev) => prev.map((it, i) => (i === index ? { ...it, ...patch } : it)));

  const pickProduct = (index, product) => {
    if (!product) {
      updateItem(index, { product: null, productId: null });
      return;
    }
    updateItem(index, {
      product,
      productId: product.id,
      description: product.name,
      unitPrice: String(product.unitPrice ?? ""),
      taxRate: String(product.taxRate ?? 0),
    });
  };

  const validate = () => {
    if (!form.customer) return "Please select a customer";
    if (form.issueDate && form.dueDate && form.dueDate < form.issueDate)
      return "Due date cannot be before issue date";
    if (items.length === 0) return "Add at least one item";
    for (let i = 0; i < items.length; i++) {
      const it = items[i];
      const n = i + 1;
      if (!it.description.trim()) return `Item ${n}: description is required`;
      if (!(Number(it.quantity) > 0)) return `Item ${n}: quantity must be greater than 0`;
      if (it.unitPrice === "" || Number(it.unitPrice) < 0)
        return `Item ${n}: enter a valid unit price`;
      if (Number(it.taxRate) < 0) return `Item ${n}: tax rate cannot be negative`;
    }
    return "";
  };

  const handleSave = async () => {
    const msg = validate();
    if (msg) {
      setFormError(msg);
      return;
    }
    setFormError("");
    const payload = {
      customerId: form.customer.id,
      issueDate: form.issueDate || null,
      dueDate: form.dueDate || null,
      notes: form.notes.trim() || null,
      items: items.map((it) => ({
        productId: it.productId ?? null,
        description: it.description.trim(),
        quantity: Number(it.quantity),
        unitPrice: Number(it.unitPrice),
        taxRate: it.taxRate === "" ? 0 : Number(it.taxRate),
      })),
    };
    try {
      if (editingId) await api.put(`/api/invoices/${editingId}`, payload);
      else await api.post("/api/invoices", payload);
      setOpen(false);
      setReload((r) => r + 1);
    } catch (e) {
      const status = e.response?.status;
      if (status === 401 || status === 403) setFormError("Session expired. Please log in again");
      else if (status === 409) setFormError("Only draft invoices can be edited");
      else if (status === 400) setFormError(e.response?.data?.message || "Please check the fields and try again");
      else setFormError(e.response?.data?.message || "Something went wrong");
    }
  };

  const handleCancelInvoice = async () => {
    try {
      await api.delete(`/api/invoices/${cancelTarget.id}`);
      setCancelTarget(null);
      setReload((r) => r + 1);
    } catch (e) {
      setCancelTarget(null);
      setError(e.response?.data?.message || "Could not cancel this invoice");
    }
  };

  const handleMarkAsSent = async () => {
    try {
      await api.patch(`/api/invoices/${sendTarget.id}/send`);
      setSendTarget(null);
      setReload((r) => r + 1);
    } catch (e) {
      setSendTarget(null);
      setError(e.response?.data?.message || "Could not mark invoice as sent");
    }
  };

  const canCancel = (s) => s === "DRAFT" || s === "SENT" || s === "OVERDUE";

  return (
    <Paper sx={{ p: 3 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 2, gap: 2 }}>
        <TextField
          size="small"
          placeholder="Search invoice number or customer"
          value={q}
          onChange={(e) => { setQ(e.target.value); setPage(0); }}
          sx={{ width: 340 }}
          slotProps={{ input: { startAdornment: (
              <InputAdornment position="start"><SearchIcon fontSize="small" /></InputAdornment>
            ) } }}
        />
        <Button variant="contained" startIcon={<AddIcon />} onClick={openAdd}>
          New Invoice
        </Button>
      </Box>

      {error && !open && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Invoice #</TableCell>
            <TableCell>Customer</TableCell>
            <TableCell>Issue Date</TableCell>
            <TableCell>Due Date</TableCell>
            <TableCell align="right">Total (₹)</TableCell>
            <TableCell align="right">Balance (₹)</TableCell>
            <TableCell>Status</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((inv) => (
            <TableRow key={inv.id} hover>
              <TableCell>{inv.invoiceNumber}</TableCell>
              <TableCell>{inv.customerName}</TableCell>
              <TableCell>{formatDate(inv.issueDate)}</TableCell>
              <TableCell>{formatDate(inv.dueDate)}</TableCell>
              <TableCell align="right">{money(inv.total)}</TableCell>
              <TableCell align="right">{money(inv.balanceDue)}</TableCell>
              <TableCell>
                <Chip size="small" label={statusLabel(inv.status)} color={statusColor[inv.status] || "default"} />
              </TableCell>
              <TableCell align="right">
                {inv.status === "DRAFT" ? (
                  <Tooltip title="Edit">
                    <IconButton size="small" onClick={() => openExisting(inv, false)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                ) : (
                  <Tooltip title="View">
                    <IconButton size="small" onClick={() => openExisting(inv, true)}>
                      <VisibilityIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
                {inv.status === "DRAFT" && (
                  <Tooltip title="Mark as SENT">
                    <IconButton size="small" color="info" onClick={() => setSendTarget(inv)}>
                      <SendIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
                {canCancel(inv.status) && (
                  <Tooltip title="Cancel invoice">
                    <IconButton size="small" color="error" onClick={() => setCancelTarget(inv)}>
                      <CancelIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                )}
              </TableCell>
            </TableRow>
          ))}
          {rows.length === 0 && (
            <TableRow>
              <TableCell colSpan={8} align="center" sx={{ py: 4, color: "text.secondary" }}>
                No invoices found
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <TablePagination
        component="div"
        count={total}
        page={page}
        onPageChange={(_, p) => setPage(p)}
        rowsPerPage={size}
        onRowsPerPageChange={(e) => { setSize(parseInt(e.target.value, 10)); setPage(0); }}
        rowsPerPageOptions={[5, 10, 25]}
      />

      {/* create / edit / view dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="lg">
        <DialogTitle>
          {readOnly ? "View Invoice" : editingId ? "Edit Invoice" : "New Invoice"}
        </DialogTitle>
        <DialogContent>
          {formError && <Alert severity="error" sx={{ mb: 2 }}>{formError}</Alert>}

          <Box sx={{ display: "grid", gridTemplateColumns: "2fr 1fr 1fr", gap: 2, mt: 1 }}>
            <Autocomplete
              options={customers}
              value={form.customer}
              disabled={readOnly}
              getOptionLabel={(c) => c.name || ""}
              isOptionEqualToValue={(a, b) => a.id === b.id}
              onChange={(_, v) => setForm({ ...form, customer: v })}
              renderInput={(params) => <TextField {...params} label="Customer" required />}
            />
            <TextField
              label="Issue Date"
              type="date"
              value={form.issueDate}
              disabled={readOnly}
              onChange={(e) => setForm({ ...form, issueDate: e.target.value })}
              InputLabelProps={{ shrink: true }}
            />
            <TextField
              label="Due Date"
              type="date"
              value={form.dueDate}
              disabled={readOnly}
              onChange={(e) => setForm({ ...form, dueDate: e.target.value })}
              InputLabelProps={{ shrink: true }}
              helperText={readOnly ? "" : "Empty = 15 days after issue date"}
            />
          </Box>

          <Typography variant="subtitle1" sx={{ mt: 3, mb: 1 }}>Items</Typography>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell sx={{ width: 220 }}>Product</TableCell>
                <TableCell>Description</TableCell>
                <TableCell sx={{ width: 90 }}>Qty</TableCell>
                <TableCell sx={{ width: 130 }}>Unit Price (₹)</TableCell>
                <TableCell sx={{ width: 100 }}>Tax (%)</TableCell>
                <TableCell align="right" sx={{ width: 130 }}>Line Total (₹)</TableCell>
                {!readOnly && <TableCell sx={{ width: 48 }} />}
              </TableRow>
            </TableHead>
            <TableBody>
              {items.map((it, i) => {
                const line = round2((Number(it.quantity) || 0) * (Number(it.unitPrice) || 0));
                return (
                  <TableRow key={i}>
                    <TableCell>
                      <Autocomplete
                        size="small"
                        options={products}
                        value={it.product}
                        disabled={readOnly}
                        getOptionLabel={(p) => p.name || ""}
                        isOptionEqualToValue={(a, b) => a.id === b.id}
                        onChange={(_, v) => pickProduct(i, v)}
                        renderInput={(params) => <TextField {...params} placeholder="Select (optional)" />}
                      />
                    </TableCell>
                    <TableCell>
                      <TextField
                        size="small"
                        fullWidth
                        value={it.description}
                        disabled={readOnly}
                        onChange={(e) => updateItem(i, { description: e.target.value })}
                      />
                    </TableCell>
                    <TableCell>
                      <TextField
                        size="small"
                        type="number"
                        value={it.quantity}
                        disabled={readOnly}
                        inputProps={{ min: 0, step: "any" }}
                        onChange={(e) => updateItem(i, { quantity: e.target.value })}
                      />
                    </TableCell>
                    <TableCell>
                      <TextField
                        size="small"
                        type="number"
                        value={it.unitPrice}
                        disabled={readOnly}
                        inputProps={{ min: 0, step: "0.01" }}
                        onChange={(e) => updateItem(i, { unitPrice: e.target.value })}
                      />
                    </TableCell>
                    <TableCell>
                      <TextField
                        size="small"
                        type="number"
                        value={it.taxRate}
                        disabled={readOnly}
                        inputProps={{ min: 0, step: "0.01" }}
                        onChange={(e) => updateItem(i, { taxRate: e.target.value })}
                      />
                    </TableCell>
                    <TableCell align="right">{money(line)}</TableCell>
                    {!readOnly && (
                      <TableCell>
                        <IconButton
                          size="small"
                          color="error"
                          disabled={items.length === 1}
                          onClick={() => setItems((prev) => prev.filter((_, idx) => idx !== i))}
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </TableCell>
                    )}
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>

          {!readOnly && (
            <Button
              size="small"
              startIcon={<AddIcon />}
              sx={{ mt: 1 }}
              onClick={() => setItems((prev) => [...prev, emptyItem()])}
            >
              Add Item
            </Button>
          )}

          <Box sx={{ display: "flex", justifyContent: "space-between", gap: 3, mt: 3 }}>
            <TextField
              label="Notes"
              value={form.notes}
              disabled={readOnly}
              onChange={(e) => setForm({ ...form, notes: e.target.value })}
              multiline
              rows={3}
              sx={{ flex: 1 }}
            />
            <Box sx={{ minWidth: 260 }}>
              <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.5 }}>
                <Typography color="text.secondary">Subtotal</Typography>
                <Typography>₹ {money(totals.subtotal)}</Typography>
              </Box>
              <Box sx={{ display: "flex", justifyContent: "space-between", mb: 0.5 }}>
                <Typography color="text.secondary">Tax</Typography>
                <Typography>₹ {money(totals.tax)}</Typography>
              </Box>
              <Divider sx={{ my: 1 }} />
              <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                <Typography variant="subtitle1">Total</Typography>
                <Typography variant="subtitle1">₹ {money(totals.total)}</Typography>
              </Box>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setOpen(false)}>{readOnly ? "Close" : "Cancel"}</Button>
          {!readOnly && (
            <Button variant="contained" onClick={handleSave}>
              {editingId ? "Update" : "Create Invoice"}
            </Button>
          )}
        </DialogActions>
      </Dialog>

      {/* send confirm */}
      <Dialog open={!!sendTarget} onClose={() => setSendTarget(null)}>
        <DialogTitle>Mark invoice as SENT?</DialogTitle>
        <DialogContent>
          <Typography>
            {sendTarget?.invoiceNumber} will be marked as sent. This cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setSendTarget(null)}>Cancel</Button>
          <Button color="info" variant="contained" onClick={handleMarkAsSent}>
            Mark as SENT
          </Button>
        </DialogActions>
      </Dialog>

      {/* cancel confirm */}
      <Dialog open={!!cancelTarget} onClose={() => setCancelTarget(null)}>
        <DialogTitle>Cancel invoice?</DialogTitle>
        <DialogContent>
          <Typography>
            {cancelTarget?.invoiceNumber} will be marked as cancelled.
          </Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setCancelTarget(null)}>Keep</Button>
          <Button color="error" variant="contained" onClick={handleCancelInvoice}>
            Cancel Invoice
          </Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
}