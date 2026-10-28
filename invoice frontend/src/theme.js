import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: { main: "#534AB7" },
    background: { default: "#f4f5fa" },
  },
  typography: { fontFamily: "Inter, Roboto, Arial, sans-serif" },
  shape: { borderRadius: 10 },
});

export default theme;