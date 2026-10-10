// ==========================================================
// Estate Elite - API client
// Talks to the Spring Boot backend at API_BASE.
// ==========================================================

const API_BASE = "http://localhost:8080";

const Api = {
  token() {
    return localStorage.getItem("ee_token");
  },

  authHeader() {
    const t = this.token();
    return t ? { Authorization: "Bearer " + t } : {};
  },

  async request(path, { method = "GET", body, isForm = false, auth = true } = {}) {
    const headers = {};
    if (auth) Object.assign(headers, this.authHeader());
    if (!isForm) headers["Content-Type"] = "application/json";

    const res = await fetch(API_BASE + path, {
      method,
      headers,
      body: body ? (isForm ? body : JSON.stringify(body)) : undefined,
    });

    if (res.status === 204) return null;

    let data = null;
    const text = await res.text();
    try { data = text ? JSON.parse(text) : null; } catch { data = text; }

    if (!res.ok) {
      const message = (data && (data.message || data.error)) ||
        (data && data.errors && Object.values(data.errors).join(", ")) ||
        `Request failed (${res.status})`;
      throw new Error(message);
    }
    return data;
  },

  get(path, auth = true) { return this.request(path, { method: "GET", auth }); },
  post(path, body, auth = true) { return this.request(path, { method: "POST", body, auth }); },
  put(path, body, auth = true) { return this.request(path, { method: "PUT", body, auth }); },
  patch(path, body, auth = true) { return this.request(path, { method: "PATCH", body, auth }); },
  del(path, auth = true) { return this.request(path, { method: "DELETE", auth }); },

  upload(path, file, fieldName = "file") {
    const form = new FormData();
    form.append(fieldName, file);
    return this.request(path, { method: "POST", body: form, isForm: true });
  },


  postForm(path, formData, auth = true) {
    return this.request(path, { method: "POST", body: formData, isForm: true, auth });
  },


  async download(path, filename = "download") {
    const res = await fetch(API_BASE + path, {
      method: "GET",
      headers: { ...this.authHeader() },
    });
    if (!res.ok) {
      let message = `Download failed (${res.status})`;
      try {
        const data = await res.json();
        if (data && data.message) message = data.message;
      } catch (_) {  }
      throw new Error(message);
    }
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  },
  fileUrl(relativePath) {
    if (!relativePath) return null;
    if (relativePath.startsWith("http")) return relativePath;
    return API_BASE + relativePath;
  },
};
