import { createContext, useContext, useEffect, useMemo, useState } from "react";

const STORAGE_KEY = "cheonggiwa_theme";

const ThemeContext = createContext({ theme: "light", setTheme: () => {} });

export function ThemeProvider({ children }) {
  const [theme, setThemeState] = useState(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved === "light" || saved === "dark") return saved;
    } catch {}
    const prefersDark = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
    return prefersDark ? "dark" : "light";
  });

  const setTheme = (next) => {
    setThemeState(next);
    try {
      localStorage.setItem(STORAGE_KEY, next);
    } catch {}
  };

  useEffect(() => {
    const root = document.documentElement;
    root.classList.remove("theme-light", "theme-dark");
    root.classList.add(theme === "dark" ? "theme-dark" : "theme-light");
    // update meta theme-color for mobile
    const meta = document.querySelector('meta[name="theme-color"]');
    if (meta) {
      meta.setAttribute("content", theme === "dark" ? "#0b1220" : "#ffffff");
    }
  }, [theme]);

  // react to system changes if user hasn't explicitly chosen
  useEffect(() => {
    const matcher = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)");
    if (!matcher) return;
    const onChange = (e) => {
      try {
        const saved = localStorage.getItem(STORAGE_KEY);
        if (saved === "light" || saved === "dark") return; // user override
      } catch {}
      setThemeState(e.matches ? "dark" : "light");
    };
    matcher.addEventListener ? matcher.addEventListener("change", onChange) : matcher.addListener(onChange);
    return () => {
      matcher.removeEventListener ? matcher.removeEventListener("change", onChange) : matcher.removeListener(onChange);
    };
  }, []);

  const value = useMemo(() => ({ theme, setTheme }), [theme]);
  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
  return useContext(ThemeContext);
}


