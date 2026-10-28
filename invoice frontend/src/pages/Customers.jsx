import { useEffect, useState } from "react";
import {
  Box, Paper, Typography, TextField, Button, Table, TableHead, TableRow,
  TableCell, TableBody, TablePagination, IconButton, Dialog, DialogTitle,
  DialogContent, DialogActions, Alert, InputAdornment,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import SearchIcon from "@mui/icons-material/Search";
import api from "../api/axiosConfig";

const emptyForm = { name: "", email: "", phone: "", address: "", gstNumber: "" };

export default function Customers() {
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
        const { data } = await api.get("/api/customers", { params: { q, page, size } });
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
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) e.email = "Invalid email";
    if (form.phone && !/^[6-9]\d{9}$/.test(form.phone)) e.phone = "Enter a valid 10-digit mobile number";
    if (form.gstNumber && !/^\d{2}[A-Z]{5}\d{4}[A-Z][1-9A-Z]Z[0-9A-Z]$/.test(form.gstNumber))
      e.gstNumber = "Invalid GST number (e.g. 27ABCDE1234F1Z5)";
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

  const openEdit = (c) => {
    setEditingId(c.id);
    setForm({
      name: c.name || "",
      email: c.email || "",
      phone: c.phone || "",
      address: c.address || "",
      gstNumber: c.gstNumber || "",
    });
    setError("");
    setFieldErrors({});
    setOpen(true);
  };

  const handleChange = (e) => {
    let { name, value } = e.target;
    if (name === "phone") value = value.replace(/\D/g, "").slice(0, 10);
    if (name === "gstNumber") value = value.toUpperCase().slice(0, 15);
    setForm({ ...form, [name]: value });
  };

  const handleSave = async () => {
    if (!validate()) return;
    setError("");
    try {
      if (editingId) {
        await api.put(`/api/customers/${editingId}`, form);
      } else {
        await api.post("/api/customers", form);
      }
      setOpen(false);
      setReload((r) => r + 1);
    } catch (e) {
      const status = e.response?.status;
      if (status === 409) setError("Phone already exists");
      else if (status === 400) setError("Please check the fields and try again");
      else setError(e.response?.data?.message || "Something went wrong");
    }
  };

  const handleDelete = async () => {
    try {
      await api.delete(`/api/customers/${deleteTarget.id}`);
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
          placeholder="Search name, email or phone"
          value={q}
          onChange={(e) => { setQ(e.target.value); setPage(0); }}
          sx={{ width: 320 }}
          slotProps={{ input: { startAdornment: (
              <InputAdornment position="start"><SearchIcon fontSize="small" /></InputAdornment>
            ) } }}
        />
        <Button variant="contained" startIcon={<AddIcon />} onClick={openAdd}>
          Add Customer
        </Button>
      </Box>

      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Phone</TableCell>
            <TableCell>GST Number</TableCell>
            <TableCell>Address</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((c) => (
            <TableRow key={c.id} hover>
              <TableCell>{c.name}</TableCell>
              <TableCell>{c.email}</TableCell>
              <TableCell>{c.phone}</TableCell>
              <TableCell>{c.gstNumber}</TableCell>
              <TableCell>{c.address}</TableCell>
              <TableCell align="right">
                <IconButton size="small" onClick={() => openEdit(c)}><EditIcon fontSize="small" /></IconButton>
                <IconButton size="small" color="error" onClick={() => setDeleteTarget(c)}>
                  <DeleteIcon fontSize="small" />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
          {rows.length === 0 && (
            <TableRow>
              <TableCell colSpan={6} align="center" sx={{ py: 4, color: "text.secondary" }}>
                No customers found
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
        <DialogTitle>{editingId ? "Edit Customer" : "Add Customer"}</DialogTitle>
        <DialogContent>
          {error && <Alert severity="error" sx={{ mb: 1 }}>{error}</Alert>}
          <TextField label="Name" name="name" value={form.name} onChange={handleChange} fullWidth required margin="dense" error={!!fieldErrors.name} helperText={fieldErrors.name} />
          <TextField label="Email" name="email" value={form.email} onChange={handleChange} fullWidth margin="dense" error={!!fieldErrors.email} helperText={fieldErrors.email} />
          <TextField label="Phone" name="phone" value={form.phone} onChange={handleChange} fullWidth margin="dense" error={!!fieldErrors.phone} helperText={fieldErrors.phone} />
          <TextField label="GST Number" name="gstNumber" value={form.gstNumber} onChange={handleChange} fullWidth margin="dense" error={!!fieldErrors.gstNumber} helperText={fieldErrors.gstNumber} />
          <TextField label="Address" name="address" value={form.address} onChange={handleChange} fullWidth multiline rows={2} margin="dense" />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSave} disabled={!form.name.trim()}>
            {editingId ? "Update" : "Save"}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)}>
        <DialogTitle>Delete customer?</DialogTitle>
        <DialogContent>
          <Typography>{deleteTarget?.name} will be removed from the list.</Typography>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setDeleteTarget(null)}>Cancel</Button>
          <Button color="error" variant="contained" onClick={handleDelete}>Delete</Button>
        </DialogActions>
      </Dialog>
    </Paper>
  );
}