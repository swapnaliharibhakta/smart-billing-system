import { Outlet, NavLink, useLocation, useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import {
  Box, Drawer, List, ListItemButton, ListItemIcon, ListItemText,
  AppBar, Toolbar, Typography, Button,
} from "@mui/material";
import DashboardIcon from "@mui/icons-material/Dashboard";
import PeopleIcon from "@mui/icons-material/People";
import Inventory2Icon from "@mui/icons-material/Inventory2";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import PaymentsIcon from "@mui/icons-material/Payments";
import BarChartIcon from "@mui/icons-material/BarChart";
import SettingsIcon from "@mui/icons-material/Settings";
import { logout } from "../store/authSlice";

const drawerWidth = 240;

const menu = [
  { label: "Dashboard", path: "/", icon: <DashboardIcon /> },
  { label: "Customers", path: "/customers", icon: <PeopleIcon /> },
  { label: "Products", path: "/products", icon: <Inventory2Icon /> },
  { label: "Invoices", path: "/invoices", icon: <ReceiptLongIcon /> },
  { label: "Payments", path: "/payments", icon: <PaymentsIcon /> },
  { label: "Reports", path: "/reports", icon: <BarChartIcon /> },
  { label: "Settings", path: "/settings", icon: <SettingsIcon /> },
];

export default function MainLayout() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const current = menu.find((m) =>
    m.path === "/" ? location.pathname === "/" : location.pathname.startsWith(m.path)
  );

  const handleLogout = () => {
    dispatch(logout());
    navigate("/login");
  };

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
            backgroundColor: "#12152b",
            color: "#c9cbe0",
            borderRight: "none",
          },
        }}
      >
        <Toolbar sx={{ px: 3 }}>
          <Typography variant="h6" fontWeight={700} color="#fff">
            SmartBilling
          </Typography>
        </Toolbar>

        <List sx={{ px: 1.5 }}>
          {menu.map((item) => (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              selected={current?.path === item.path}
              sx={{
                borderRadius: 2,
                mb: 0.5,
                color: "inherit",
                "&.Mui-selected": { backgroundColor: "#534AB7", color: "#fff" },
                "&.Mui-selected:hover": { backgroundColor: "#534AB7" },
                "&:hover": { backgroundColor: "rgba(255,255,255,0.08)" },
              }}
            >
              <ListItemIcon sx={{ color: "inherit", minWidth: 40 }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}
        </List>
      </Drawer>

      <Box sx={{ flexGrow: 1, display: "flex", flexDirection: "column" }}>
        <AppBar
          position="static"
          elevation={0}
          sx={{ backgroundColor: "#fff", color: "#12152b", borderBottom: "1px solid #e5e7f0" }}
        >
          <Toolbar sx={{ justifyContent: "space-between" }}>
            <Typography variant="h6" fontWeight={600}>
              {current?.label || ""}
            </Typography>
            <Button variant="outlined" onClick={handleLogout}>
              Logout
            </Button>
          </Toolbar>
        </AppBar>

        <Box component="main" sx={{ p: 3, flexGrow: 1 }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}