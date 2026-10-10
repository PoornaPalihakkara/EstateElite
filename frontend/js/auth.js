// ==========================================================
// Estate Elite - Auth / session helpers
// ==========================================================

const Auth = {
  saveSession(authResponse) {
    localStorage.setItem("ee_token", authResponse.token);
    localStorage.setItem("ee_user", JSON.stringify({
      id: authResponse.userId,
      fullName: authResponse.fullName,
      email: authResponse.email,
      role: authResponse.role,
    }));
  },

  currentUser() {
    const raw = localStorage.getItem("ee_user");
    return raw ? JSON.parse(raw) : null;
  },

  isLoggedIn() {
    return !!Api.token();
  },

  logout() {
    localStorage.removeItem("ee_token");
    localStorage.removeItem("ee_user");
    window.location.href = "index.html";
  },

  dashboardFor(role) {
    switch (role) {
      case "SELLER": return "dashboard-seller.html";
      case "BUYER": return "dashboard-buyer.html";
      case "AGENT": return "dashboard-agent.html";
      case "ADMIN": return "dashboard-admin.html";
      case "FINANCE_OFFICER": return "dashboard-finance.html";
      default: return "index.html";
    }
  },


  guard(requiredRole = null) {
    if (!this.isLoggedIn()) {
      window.location.href = "login.html";
      return null;
    }
    const user = this.currentUser();
    if (requiredRole && user.role !== requiredRole) {
      window.location.href = this.dashboardFor(user.role);
      return null;
    }
    return user;
  },

  roleLabel(role) {
    const map = {
      SELLER: "Seller / Owner",
      BUYER: "Buyer / Client",
      AGENT: "Agent",
      ADMIN: "Administrator",
      FINANCE_OFFICER: "Finance Officer",
    };
    return map[role] || role;
  },
};
