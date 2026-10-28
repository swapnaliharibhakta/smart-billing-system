import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import {
  Box, Typography, TextField, Button, Alert,
  InputAdornment, IconButton, Checkbox, FormControlLabel, Divider, Link,
} from "@mui/material";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import LockPersonOutlinedIcon from "@mui/icons-material/LockPersonOutlined";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutlined";
import CheckIcon from "@mui/icons-material/Check";
import GoogleIcon from "@mui/icons-material/Google";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import api from "../api/axiosConfig";
import { loginSuccess } from "../store/authSlice";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const { data } = await api.post("/api/auth/login", form);
      dispatch(loginSuccess({ token: data.token, user: data.user }));
      navigate("/");
    } catch (err) {
      setError(err.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  const features = [
    "Create invoices in seconds",
    "Track payments automatically",
    "GST-ready billing",
  ];

  return (
    <Box sx={{ minHeight: "100vh", display: "flex" }}>

      {/* ══ LEFT PANEL ══ */}
      <Box sx={{
        display: { xs: "none", md: "flex" },
        flex: { md: "0 0 50%" }, position: "relative", overflow: "hidden",
        flexDirection: "column", justifyContent: "center",
        pl: "80px", pr: 6, py: 8,
        background: "linear-gradient(135deg, #3b2fa0 0%, #5b4bd5 50%, #7c6fe0 100%)",
      }}>
        {/* decorative circles */}
        <Box sx={{ position: "absolute", top: -80, left: -80,
          width: 320, height: 320, borderRadius: "50%",
          bgcolor: "rgba(255,255,255,0.08)", pointerEvents: "none" }} />
        <Box sx={{ position: "absolute", bottom: -100, right: -100,
          width: 400, height: 400, borderRadius: "50%",
          bgcolor: "rgba(255,255,255,0.08)", pointerEvents: "none" }} />

        <Box component="img" src="/login-illustration.svg" alt="Invoice illustration"
          sx={{ width: "100%", maxWidth: 620, mb: 4 }} />

        <Typography sx={{ fontSize: 36, fontWeight: 700, lineHeight: 1.25,
          color: "#fff", maxWidth: 480, mb: 5 }}>
          Manage invoices, track payments, and grow your business
        </Typography>

        <Box sx={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          {features.map((f) => (
            <Box key={f} sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
              <Box sx={{
                width: 28, height: 28, borderRadius: "50%",
                bgcolor: "#fff", flexShrink: 0,
                display: "flex", alignItems: "center", justifyContent: "center",
              }}>
                <CheckIcon sx={{ fontSize: 16, color: "#5B45D1" }} />
              </Box>
              <Typography sx={{ color: "#fff", fontSize: 16 }}>{f}</Typography>
            </Box>
          ))}
        </Box>
      </Box>

      {/* ══ RIGHT PANEL ══ */}
      <Box sx={{
        flex: 1,
        display: "flex", alignItems: "center", justifyContent: "center",
        backgroundColor: "#FAFAFF",
        px: { xs: 2, sm: 5 }, py: 6,
      }}>
        {/* white card */}
        <Box sx={{
          width: "100%", maxWidth: 460,
          bgcolor: "#fff", borderRadius: 3, p: 5,
          boxShadow: "0 4px 24px rgba(108,77,224,0.10)",
          border: "1px solid #E6E3F8",
          display: "flex", flexDirection: "column", alignItems: "center",
        }}>
          {/* lock badge */}
          <Box sx={{ width: 64, height: 64, borderRadius: "50%",
            bgcolor: "#E9E4FC",
            display: "flex", alignItems: "center", justifyContent: "center",
            mb: 2 }}>
            <LockOutlinedIcon sx={{ fontSize: 28, color: "#6C4DE0" }} />
          </Box>

          <Typography sx={{ fontSize: 32, fontWeight: 700,
            color: "#1B1B4B", textAlign: "center", mb: 0.5 }}>
            Welcome Back
          </Typography>
          <Typography sx={{ fontSize: 15, color: "#7B7AA0",
            textAlign: "center", mb: 3 }}>
            Sign in to your account
          </Typography>

          {error && (
            <Alert severity="error"
              sx={{ mb: 2, width: "100%", borderRadius: 2 }}>
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit} style={{ width: "100%" }}>
            <TextField
              label="Email Address"
              name="email"
              value={form.email}
              onChange={handleChange}
              fullWidth required
              sx={{
                mb: 2,
                "& .MuiOutlinedInput-root": {
                  borderRadius: 2,
                  "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                    borderColor: "#6C4DE0",
                  },
                },
                "& .MuiInputLabel-root.Mui-focused": { color: "#6C4DE0" },
              }}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <EmailOutlinedIcon sx={{ fontSize: 20, color: "#8b8bb0" }} />
                    </InputAdornment>
                  ),
                },
              }}
            />
            <TextField
              label="Password"
              name="password"
              type={showPassword ? "text" : "password"}
              value={form.password}
              onChange={handleChange}
              fullWidth required
              sx={{
                mb: 1.5,
                "& .MuiOutlinedInput-root": {
                  borderRadius: 2,
                  "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
                    borderColor: "#6C4DE0",
                  },
                },
                "& .MuiInputLabel-root.Mui-focused": { color: "#6C4DE0" },
              }}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockPersonOutlinedIcon sx={{ fontSize: 20, color: "#8b8bb0" }} />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        size="small" edge="end"
                        onClick={() => setShowPassword((v) => !v)}
                        aria-label={showPassword ? "Hide password" : "Show password"}
                      >
                        {showPassword
                          ? <VisibilityOff sx={{ fontSize: 20, color: "#8b8bb0" }} />
                          : <Visibility sx={{ fontSize: 20, color: "#8b8bb0" }} />}
                      </IconButton>
                    </InputAdornment>
                  ),
                },
              }}
            />

            {/* remember me + forgot password */}
            <Box sx={{ display: "flex", alignItems: "center",
              justifyContent: "space-between", mb: 3 }}>
              <FormControlLabel
                control={<Checkbox size="small" sx={{
                  color: "#6C4DE0",
                  "&.Mui-checked": { color: "#6C4DE0" },
                }} />}
                label={<Typography sx={{ fontSize: 14, color: "#7B7AA0" }}>Remember me</Typography>}
              />
              <Link href="#" underline="none"
                sx={{ fontSize: 14, fontWeight: 700, color: "#6C4DE0" }}>
                Forgot password?
              </Link>
            </Box>

            <Button
              type="submit" variant="contained" fullWidth
              disabled={loading}
              sx={{
                height: 48, borderRadius: 2,
                textTransform: "none", fontSize: 16, fontWeight: 600,
                bgcolor: "#6C4DE0", color: "#fff",
                boxShadow: "0 4px 14px rgba(108,77,224,0.30)",
                "&:hover": { bgcolor: "#5a3ecb" },
                "&.Mui-disabled": { opacity: 0.7, color: "#fff" },
              }}
            >
              {loading ? "Signing in..." : "Sign In"}
            </Button>
          </form>

          {/* OR divider */}
          <Divider sx={{ width: "100%", my: 3, color: "#B0AECF", fontSize: 13 }}>OR</Divider>

          {/* Google button — UI only */}
          <Button
            variant="outlined" fullWidth
            startIcon={<GoogleIcon />}
            sx={{
              height: 48, borderRadius: 2,
              textTransform: "none", fontSize: 15, fontWeight: 500,
              color: "#1B1B4B", borderColor: "#E6E3F8",
              "&:hover": { borderColor: "#6C4DE0", bgcolor: "#F5F3FF" },
            }}
          >
            Sign in with Google
          </Button>

          {/* sign up prompt */}
          <Typography sx={{ mt: 3, fontSize: 14, color: "#7B7AA0" }}>
            Don't have an account?{" "}
            <Link href="#" underline="none"
              sx={{ fontWeight: 700, color: "#6C4DE0" }}>
              Sign Up
            </Link>
          </Typography>
        </Box>
      </Box>
    </Box>
  );
}