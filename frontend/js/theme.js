// ==========================================================
// Estate Elite — theme (dark / light)
// Persists under localStorage key "ee_theme".
// Call Theme.wire() after dynamically inserting #theme-toggle (e.g. nav).
// ==========================================================

(function () {
  const KEY = "ee_theme";

  function resolveTheme() {
    try {
      const saved = localStorage.getItem(KEY);
      if (saved === "light" || saved === "dark") return saved;
    } catch (_) {  }
    // Product default is dark (do not follow OS — keeps pages consistent)
    return "dark";
  }

  function applyTheme(theme, opts) {
    const persist = !opts || opts.persist !== false;
    const t = theme === "light" ? "light" : "dark";
    document.documentElement.setAttribute("data-theme", t);
    if (persist) {
      try {
        localStorage.setItem(KEY, t);
      } catch (_) {  }
    }
    requestAnimationFrame(function () {
      document.documentElement.classList.add("theme-ready");
    });
    syncToggleUi(t);
  }

  function syncToggleUi(t) {
    const theme = t || resolveTheme();
    document.querySelectorAll("#theme-toggle, .theme-toggle").forEach(function (btn) {
      const title = theme === "light" ? "Switch to dark mode" : "Switch to light mode";
      btn.title = title;
      btn.setAttribute("aria-label", title);
    });
  }

  function toggleTheme() {
    const current = document.documentElement.getAttribute("data-theme") || resolveTheme();
    applyTheme(current === "light" ? "dark" : "light");
  }

  function wireToggle(root) {
    const scope = root || document;
    const buttons = scope.querySelectorAll("#theme-toggle, .theme-toggle");
    buttons.forEach(function (btn) {
      if (btn.dataset.themeWired === "1") return;
      btn.dataset.themeWired = "1";
      btn.addEventListener("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        toggleTheme();
      });
    });
    syncToggleUi(document.documentElement.getAttribute("data-theme") || resolveTheme());
  }

  // Apply as soon as this file runs (should be in <head>)
  applyTheme(resolveTheme(), { persist: true });

  window.Theme = {
    apply: applyTheme,
    toggle: toggleTheme,
    wire: wireToggle,
    current: function () {
      return document.documentElement.getAttribute("data-theme") || resolveTheme();
    },
    KEY: KEY,
  };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      wireToggle();
    });
  } else {
    wireToggle();
  }
})();
