// ==========================================================
// Estate Elite - shared form validation helpers
// ==========================================================

const Validate = {

  fail(el, message) {
    if (el) {
      el.classList.add("is-invalid");
      let hint = el.parentElement && el.parentElement.querySelector(".field-error");
      if (!hint && el.parentElement) {
        hint = document.createElement("div");
        hint.className = "field-error";
        el.parentElement.appendChild(hint);
      }
      if (hint) hint.textContent = message;
      try { el.focus(); } catch (_) {  }
    }
    Ui.toast(message, "error");
    return false;
  },


  clear(...els) {
    els.forEach((el) => {
      if (!el) return;
      el.classList.remove("is-invalid");
      const hint = el.parentElement && el.parentElement.querySelector(".field-error");
      if (hint) hint.textContent = "";
    });
  },

  required(el, label) {
    const v = (el && el.value != null) ? String(el.value).trim() : "";
    if (!v) return this.fail(el, `${label} is required`);
    return true;
  },


  email(el, label = "Email") {
    const v = (el && el.value != null) ? String(el.value).trim() : "";
    if (!v) return this.fail(el, `${label} is required`);
    // Must include @ and at least one dot after @
    if (!v.includes("@") || !v.includes(".")) {
      return this.fail(el, `${label} must include '@' and '.' (e.g. you@example.com)`);
    }
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!re.test(v)) {
      return this.fail(el, `${label} format is invalid (e.g. you@example.com)`);
    }
    return true;
  },


  phone(el, label = "Phone", { optional = false } = {}) {
    const raw = (el && el.value != null) ? String(el.value).trim() : "";
    if (!raw) {
      if (optional) return true;
      return this.fail(el, `${label} is required`);
    }
    // Keep leading + if present; count only digits for length
    const digits = raw.replace(/\D/g, "");
    if (digits.length < 10 || digits.length > 12) {
      return this.fail(el, `${label} must be 10–12 digits (e.g. 0701234567 or +94701234567)`);
    }
    // Allow optional leading +
    if (!/^\+?[\d\s\-()]+$/.test(raw)) {
      return this.fail(el, `${label} contains invalid characters`);
    }
    return true;
  },


  password(el, label = "Password", minLen = 8) {
    const v = el ? el.value : "";
    if (!v) return this.fail(el, `${label} is required`);
    if (v.length < minLen) {
      return this.fail(el, `${label} must be at least ${minLen} characters`);
    }
    return true;
  },


  positiveNumber(el, label) {
    const v = el ? el.value : "";
    if (v === "" || v === null) return this.fail(el, `${label} is required`);
    const n = Number(v);
    if (Number.isNaN(n) || n <= 0) {
      return this.fail(el, `${label} must be a number greater than 0`);
    }
    return true;
  },


  nonNegativeNumber(el, label, { optional = false } = {}) {
    const v = el ? String(el.value).trim() : "";
    if (!v) {
      if (optional) return true;
      return this.fail(el, `${label} is required`);
    }
    const n = Number(v);
    if (Number.isNaN(n) || n < 0) {
      return this.fail(el, `${label} must be zero or a positive number`);
    }
    return true;
  },


  futureDateTime(el, label = "Date & time") {
    const v = el ? el.value : "";
    if (!v) return this.fail(el, `${label} is required`);
    const chosen = new Date(v);
    if (Number.isNaN(chosen.getTime())) {
      return this.fail(el, `${label} is invalid`);
    }
    if (chosen.getTime() <= Date.now()) {
      return this.fail(el, `${label} cannot be in the past — choose a future date and time`);
    }
    return true;
  },


  selectRequired(el, label) {
    const v = el ? String(el.value).trim() : "";
    if (!v) return this.fail(el, `Please select ${label}`);
    return true;
  },


  rangeOrder(minEl, maxEl, label = "Range") {
    const minV = minEl && minEl.value !== "" ? Number(minEl.value) : null;
    const maxV = maxEl && maxEl.value !== "" ? Number(maxEl.value) : null;
    if (minV != null && maxV != null && !Number.isNaN(minV) && !Number.isNaN(maxV) && minV > maxV) {
      return this.fail(maxEl, `${label}: minimum cannot be greater than maximum`);
    }
    return true;
  },


  imageFile(file, { optional = true } = {}) {
    if (!file) {
      if (optional) return true;
      Ui.toast("Please choose an image file", "error");
      return false;
    }
    if (!file.type || !file.type.startsWith("image/")) {
      Ui.toast("Only image files are allowed (jpg, png, webp, …)", "error");
      return false;
    }
    const max = 10 * 1024 * 1024;
    if (file.size > max) {
      Ui.toast("Image must be 10MB or smaller", "error");
      return false;
    }
    return true;
  },
};
