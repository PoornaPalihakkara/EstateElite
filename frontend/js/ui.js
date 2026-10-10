// ==========================================================
// Estate Elite - shared UI helpers used on every page
// ==========================================================

const Ui = {
  toast(message, type = "success") {
    let stack = document.querySelector(".toast-stack");
    if (!stack) {
      stack = document.createElement("div");
      stack.className = "toast-stack";
      document.body.appendChild(stack);
    }
    const el = document.createElement("div");
    el.className = `toast ${type}`;
    el.textContent = message;
    stack.appendChild(el);
    setTimeout(() => el.remove(), 4200);
  },

  errorToast(err) {
    this.toast(err && err.message ? err.message : "Something went wrong", "error");
  },

  money(amount) {
    if (amount === null || amount === undefined) return "-";
    return "LKR " + Number(amount).toLocaleString("en-LK", { maximumFractionDigits: 0 });
  },

  dateTime(iso) {
    if (!iso) return "-";
    const d = new Date(iso);
    return d.toLocaleString("en-LK", { day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" });
  },

  date(iso) {
    if (!iso) return "-";
    return new Date(iso).toLocaleDateString("en-LK", { day: "2-digit", month: "short", year: "numeric" });
  },

  timeAgo(iso) {
    const seconds = Math.floor((Date.now() - new Date(iso).getTime()) / 1000);
    const steps = [
      [60, "s"], [60, "m"], [24, "h"], [7, "d"], [4.345, "w"], [12, "mo"], [Infinity, "y"],
    ];
    let value = seconds, unit = "s";
    for (const [div, label] of steps) {
      if (value < div) { unit = label; break; }
      value = Math.floor(value / div);
      unit = label;
    }
    return value <= 1 && unit === "s" ? "just now" : `${value}${unit} ago`;
  },

  stars(rating) {
    const r = Math.round(rating || 0);
    return "★".repeat(r) + "☆".repeat(5 - r);
  },

  statusBadgeClass(status) {
    const map = {
      AVAILABLE: "badge-green", SOLD: "badge-gray", RESERVED: "badge-blue",
      PENDING: "badge-gold", CONFIRMED: "badge-green", DECLINED: "badge-red",
      CANCELLED: "badge-gray", COMPLETED: "badge-green",
      DRAFT: "badge-gray", REFUNDED: "badge-blue",
      ACTIVE: "badge-green", INACTIVE: "badge-gray",
    };
    return map[status] || "badge-gray";
  },

  escape(str) {
    if (str === null || str === undefined) return "";
    return String(str).replace(/[&<>"']/g, (c) => ({
      "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;",
    }[c]));
  },

  placeholderPropertyImg(seed) {
    // Locally generated SVG placeholder - no external network dependency,
    // so it always renders even offline or on a locked-down network.
    // (Previously used Unsplash's "Source" endpoint, which Unsplash
    // discontinued - that's why images silently failed to load.)
    const palette = ["#c9a15a", "#dab973", "#e9cd97", "#9cc6e6", "#7fd6b3", "#f0a8a2"];
    const n = Math.abs(Number(seed) || 0);
    const accent = palette[n % palette.length];
    const svg = `
      <svg xmlns="http://www.w3.org/2000/svg" width="600" height="400" viewBox="0 0 600 400">
        <defs>
          <linearGradient id="bg" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stop-color="#16213a"/>
            <stop offset="100%" stop-color="#0b1120"/>
          </linearGradient>
        </defs>
        <rect width="600" height="400" fill="url(#bg)"/>
        <g transform="translate(300,185)" fill="none" stroke="${accent}" stroke-width="6" stroke-linejoin="round" stroke-linecap="round" opacity="0.92">
          <path d="M-95 25 L0 -65 L95 25"/>
          <path d="M-72 5 L-72 95 L72 95 L72 5"/>
          <rect x="-18" y="35" width="36" height="60" stroke-width="5"/>
          <rect x="-58" y="40" width="26" height="26" stroke-width="4"/>
          <rect x="32" y="40" width="26" height="26" stroke-width="4"/>
        </g>
        <text x="300" y="325" text-anchor="middle" fill="#c9a15a" font-family="Georgia, serif" font-size="22" letter-spacing="3">ESTATE ELITE</text>
        <text x="300" y="352" text-anchor="middle" fill="#56617d" font-family="Arial, sans-serif" font-size="13" letter-spacing="1.5">NO PHOTO UPLOADED YET</text>
      </svg>`;
    return "data:image/svg+xml;utf8," + encodeURIComponent(svg);
  },
};

// ---------- Shared top navigation ----------
function renderNav(activePage = "") {
  const mount = document.getElementById("ee-nav-mount");
  if (!mount) return;
  const user = Auth.currentUser();

  const loggedInLinks = user ? `
    <a class="ee-navlink ${activePage === 'dashboard' ? 'active' : ''}" href="${Auth.dashboardFor(user.role)}">Dashboard</a>
    <a class="ee-navlink ${activePage === 'browse' ? 'active' : ''}" href="browse.html">Browse</a>
  ` : `
    <a class="ee-navlink ${activePage === 'browse' ? 'active' : ''}" href="browse.html">Browse</a>
    <a class="ee-navlink ${activePage === 'about' ? 'active' : ''}" href="index.html#how-it-works">How it works</a>
  `;

  const rightSide = user ? `
    <div class="notif-wrapper" id="notif-wrapper">
      <button class="notif-btn" id="notif-btn" title="Notifications">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M6 8a6 6 0 0 1 12 0c0 4.5 1.5 6 2 7H4c.5-1 2-2.5 2-7Z"/><path d="M10 19a2 2 0 0 0 4 0"/></svg>
        <span class="notif-dot" id="notif-dot" style="display:none;">0</span>
      </button>
      <div class="notif-dropdown" id="notif-dropdown">
        <div style="padding:14px 16px; display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid var(--border-subtle);">
          <strong style="font-size:0.9rem;">Notifications</strong>
          <button class="btn btn-ghost btn-sm" id="notif-mark-all">Mark all read</button>
        </div>
        <div id="notif-list"><div class="empty-state" style="padding:24px;">Loading…</div></div>
      </div>
    </div>
    <button type="button" class="theme-toggle" id="theme-toggle" title="Toggle theme" aria-label="Toggle theme">
      <svg class="icon-moon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
      <svg class="icon-sun" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></svg>
    </button>
    <a href="profile.html" class="avatar" title="${Ui.escape(user.fullName)}">${user.fullName.charAt(0).toUpperCase()}</a>
    <button class="btn btn-ghost btn-sm" id="logout-btn">Log out</button>
  ` : `
    <button type="button" class="theme-toggle" id="theme-toggle" title="Toggle theme" aria-label="Toggle theme">
      <svg class="icon-moon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
      <svg class="icon-sun" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></svg>
    </button>
    <a href="login.html" class="btn btn-ghost btn-sm">Log in</a>
    <a href="register.html" class="btn btn-primary btn-sm">Get started</a>
  `;

  mount.innerHTML = `
    <nav class="ee-nav">
      <div class="ee-nav-inner">
        <a href="index.html" class="ee-logo"><span class="mark">EE</span> Estate Elite</a>
        <div class="ee-navlinks">${loggedInLinks}</div>
        <div class="ee-nav-right">${rightSide}</div>
      </div>
    </nav>
  `;

  const logoutBtn = document.getElementById("logout-btn");
  if (logoutBtn) logoutBtn.addEventListener("click", () => Auth.logout());

  // Theme toggle is wired by theme.js (avoid double listeners that flip twice)
  if (window.Theme && typeof window.Theme.wire === "function") {
    window.Theme.wire();
  }

  if (user) initNotificationWidget();
}

async function initNotificationWidget() {
  const btn = document.getElementById("notif-btn");
  const dropdown = document.getElementById("notif-dropdown");
  const dot = document.getElementById("notif-dot");
  const list = document.getElementById("notif-list");
  const markAllBtn = document.getElementById("notif-mark-all");
  if (!btn) return;

  async function refreshUnread() {
    try {
      const { count } = await Api.get("/api/notifications/unread-count");
      if (count > 0) { dot.style.display = "flex"; dot.textContent = count > 9 ? "9+" : count; }
      else { dot.style.display = "none"; }
    } catch (e) {  }
  }

  async function loadList() {
    try {
      const items = await Api.get("/api/notifications");
      if (!items.length) {
        list.innerHTML = `<div class="empty-state" style="padding:24px;"><div class="glyph">🔕</div>No notifications yet</div>`;
        return;
      }
      list.innerHTML = items.slice(0, 15).map(n => `
        <div class="notif-item ${n.isRead ? "" : "unread"}" data-id="${n.id}">
          <div>${Ui.escape(n.message)}</div>
          <div class="time">${Ui.timeAgo(n.createdAt)}</div>
        </div>
      `).join("");
    } catch (e) { list.innerHTML = `<div class="empty-state" style="padding:24px;">Couldn't load notifications</div>`; }
  }

  btn.addEventListener("click", async () => {
    dropdown.classList.toggle("open");
    if (dropdown.classList.contains("open")) await loadList();
  });

  document.addEventListener("click", (e) => {
    if (!document.getElementById("notif-wrapper").contains(e.target)) {
      dropdown.classList.remove("open");
    }
  });

  markAllBtn.addEventListener("click", async () => {
    try {
      await Api.patch("/api/notifications/read-all");
      await loadList();
      await refreshUnread();
    } catch (e) { Ui.errorToast(e); }
  });

  await refreshUnread();
  setInterval(refreshUnread, 20000);
}

document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.getAttribute("data-nav") || "";
  renderNav(page);
});
