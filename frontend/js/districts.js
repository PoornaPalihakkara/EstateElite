// Sri Lanka administrative districts (25) — used for property location dropdowns.
const SL_DISTRICTS = [
  "Ampara",
  "Anuradhapura",
  "Badulla",
  "Batticaloa",
  "Colombo",
  "Galle",
  "Gampaha",
  "Hambantota",
  "Jaffna",
  "Kalutara",
  "Kandy",
  "Kegalle",
  "Kilinochchi",
  "Kurunegala",
  "Mannar",
  "Matale",
  "Matara",
  "Monaragala",
  "Mullaitivu",
  "Nuwara Eliya",
  "Polonnaruwa",
  "Puttalam",
  "Ratnapura",
  "Trincomalee",
  "Vavuniya",
];

function initDistrictSelect(selectId, selected, opts = {}) {
  const allowEmpty = opts.allowEmpty !== false;
  const emptyLabel = opts.emptyLabel || "Select district…";
  const includeSearch = opts.includeSearch !== false;

  const sel = document.getElementById(selectId);
  if (!sel) return;

  function fill(filterText) {
    const q = (filterText || "").trim().toLowerCase();
    let list = q
      ? SL_DISTRICTS.filter((d) => d.toLowerCase().includes(q))
      : SL_DISTRICTS.slice();

    const current = sel.value || selected || "";
    // Preserve legacy free-text locations that are not official district names
    if (current && !SL_DISTRICTS.includes(current) && (!q || current.toLowerCase().includes(q))) {
      list = [current].concat(list);
    }

    sel.innerHTML = "";
    if (allowEmpty) {
      const o = document.createElement("option");
      o.value = "";
      o.textContent = emptyLabel;
      sel.appendChild(o);
    }
    list.forEach((d) => {
      const o = document.createElement("option");
      o.value = d;
      o.textContent = d;
      if (d === current) o.selected = true;
      sel.appendChild(o);
    });
    if (current && [...sel.options].some((o) => o.value === current)) {
      sel.value = current;
    }
  }

  fill("");

  if (includeSearch) {
    // Avoid double-wiring
    const searchId = selectId + "-search";
    let search = document.getElementById(searchId);
    if (!search) {
      search = document.createElement("input");
      search.type = "search";
      search.id = searchId;
      search.placeholder = "Search district…";
      search.autocomplete = "off";
      search.style.marginBottom = "8px";
      search.className = sel.className || "";
      // Insert search input before the select
      sel.parentNode.insertBefore(search, sel);
    }
    search.oninput = () => fill(search.value);
  }
}
