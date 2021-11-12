import { createTheme } from "@mui/material";

export const theme = createTheme(
  {
    palette: {
      mode: "dark",
      primary: {
        main: "#7cb342",
        light: "#bef67a",
        dark: "#5a9216",
        contrastText: "#000000"
      },
      secondary: {
        main: "#f57c00",
        light: "#ffad42",
        dark: "#bb4d00",
        contrastText: "#000000"
      }
    }
  }
);