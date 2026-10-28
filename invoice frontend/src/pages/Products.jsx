import { useEffect, useState } from "react";
import {
  Box, Paper, Typography, TextField, Button, Table, TableHead, TableRow,
  TableCell, TableBody, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, Alert, InputAdornment, Chip,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import SearchIcon from "@mui/icons-material/Search";
import api from "../api/axiosConfig";

const emptyForm = { name: "", unitPrice: "", taxRate: "" };

const formatMoney = (v) =>
  Number(v ?? 0).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });

export default function Products() {
  const [rows, setRows] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [q, setQ] = useState("");
  const [reload, setReload] = useState(0);

  const [open, setOpen] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => {
    const t = setTimeout(async () => {
      try {
        const { data } = await api.get("/api/products", { params: { q, page, size } });
        setRows(data.content);
        setTotal(data.totalElements);
      } catch {
        // silently ignore load errors
      }
    }, 300);
    return () => clearTimeout(t);
  }, [q, page, size, reload]);

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = "Name is required";
    if (form.unitPrice === "" || Number(form.unitPrice) < 0)
      e.unitPrice = "Enter a valid unit price (0 or more)";
    if (form.taxRate !== "" && Number(form.taxRate) < 0)
      e.taxRate = "Tax rate cannot be negative";
    setFieldErrors(e);
    return Object.keys(e).length === 0;
  };

  const openAdd = () => {
    setEditingId(null);
    setForm(emptyForm);
    setError("");
    setFieldErrors({});
    setOpen(true);
  };

  const openEdit = (p) => {
    setEditingId(p.id);
    setForm({
      name: p.name || "",
      unitPrice: p.unitPrice ?? "",
      taxRate: p.taxRate ?? "",
    });
    setError("");
    setFieldErrors({});
    setOpen(true);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const handleSave = async () => {
    if (!validate()) return;
    setError("");
    const payload = {
      name: form.name.trim(),
      unitPrice: Number(form.unitPrice),
      taxRate: form.taxRate === "" ? 0 : Number(form.taxRate),
    };
    try {
      if (editingId) {
        await api.put(`/api/products/${editingId}`, payload);
      } else {
        await api.post("/api/products", payload);
      }
      setOpen(false);
      setReload((r) => r + 1);
    } catch (e) {
      const status = e.response?.status;
      if (status === 400) setError("Please check the fields and try again");
      else setError(e.response?.data?.message || "Something went wrong");
    }
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/api/products/${deleteTarget.id}`);
      setDeleteTarget(null);
      setReload((r) => r + 1);
    } catch {
      // silently ignore delete errors
    }
  };

  return (
    <Paper sx={{ p: 3 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", mb: 2, gap: 2 }}>
        <TextField
          size="small"
          placeholder="Search product name"
          value={q}
          onChange={(e) => { setQ(e.target.value); setPage(0); }}
          sx={{ width: 320 }}
          slotProps={{ input: { startAdornment: (
              <InputAdornment position="start"><SearchIcon fontSize="small" /></InputAdornment>
            ) } }}
        />
        <Button variant="contained" startIcon={<AddIcon />} onClick={openAdd}>
          Add Product
        </Button>
      </Box>

      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell align="right">Unit Price (₹)</TableCell>
            <TableCell align="right">Tax Rate (%)</TableCell>
            <TableCell>Status</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((p) => (
            <TableRow key={p.id} hover>
              <TableCell>{p.name}</TableCell>
              <TableCell align="right">{formatMoney(p.unitPrice)}</TableCell>
              <TableCell align="right">{Number(p.taxRate ?? 0)}</TableCell>
              <TableCell>
                <Chip
                  size="small"
                  label={p.active ? "Active" : "Inactive"}
                  color={p.active ? "success" : "default"}
                />
              </TableCell>
              <TableCell align="right">
                <IconButton size="small" onClick={() => openEdit(p)}><EditIcon fontSize="small" /></IconButton>
                <IconButton size="small" color="error" onClick={() => setDeleteTarget(p)}>
                  <DeleteIcon fontSize="small" />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
          {rows.length === 0 && (
            <TableRow>
              <TableCell colSpan={5} align="center" sx={{ py: 4, color: "text.secondary" }}>
                No products found
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

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{editingId ? "Edit Product" : "Add Product"}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 1 }}>{error}</Alert>}
          <TextField label="Name" name="name" value={form.name} onChange={handleChange} fullWidth required margin="dense" error={!!fieldErrors.name} helperText={fieldErrors.name} />
          <TextField label="Unit Price (₹)" name="unitPrice" type="number" value={form.unitPrice} onChange={handleChange} fullWidth required margin="dense" inputProps={{ min: 0, step: "0.01" }} error={!!fieldErrors.unitPrice} helperText={fieldErrors.unitPrice} />
          <TextField label="Tax Rate (%)" name="taxRate" type="number" value={form.taxRate} onChange={handleChange} fullWidth margin="dense" inputProps={{ min: 0, step: "0.01" }} error={!!fieldErrors.taxRate} helperText={fieldErrors.taxRate || "Optional. Defaults to 0 if left empty"} />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSave} disabled={!form.name.trim()}>
            {editingId ? "Update" : "Save"}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)}>
        <DialogTitle>Delete product?</DialogTitle>
        <DialogContent>
          <Typography>{deleteTarget?.name} will be marked inactive.</Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setDeleteTarget(null)}>Cancel</Button>
          <Button color="error" variant="contained" onClick={handleDelete}>Delete</Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
}