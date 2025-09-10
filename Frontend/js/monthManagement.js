// monthManagement.js
// Place in /js/monthManagement.js and reference from monthManagement.html

const API_BASE = "http://localhost:8080/api"; // base for months and batches
const MONTHS_BASE = `${API_BASE}/months`;
const BATCHES_BASE = `${API_BASE}/batches`;

const token = localStorage.getItem('accessToken');

const monthTableBody = document.getElementById('monthTableBody');
const monthModal = document.getElementById('monthModal');
const openModalBtn = document.getElementById('openModalBtn');
const closeModalBtn = document.getElementById('closeModalBtn');
const monthForm = document.getElementById('monthForm');

const monthIdField = document.getElementById('monthId');
const monthNameField = document.getElementById('monthName');
const batchSelect = document.getElementById('batchSelect');
const modalTitle = document.getElementById('modalTitle');
const saveMonthBtn = document.getElementById('saveMonthBtn');

let isEdit = false;

// cache for batchId -> batchName lookups
let batchesById = {};

// --- Auth guard ---
if (!token) {
  Swal.fire({
    icon: 'error',
    title: 'Unauthorized',
    text: 'Please login to access months management.'
  }).then(() => {
    window.location.href = '/login.html';
  });
}

// --- Utility to safely extract batch info from month object returned by backend ---
function parseMonthBackendShape(m) {
  // backend might return:
  // 1) flattened: { monthId, monthName, batchId: 1, batchName: '2027 Theory' }
  // 2) nested: { monthId, monthName, batch: { batchId:1, batchName: '...' } }
  // 3) nested primitive: { monthId, monthName, batch: 1 } (batch is just an id)
  const monthId = m.monthId ?? m.id ?? m.idMonth ?? null;
  const monthName = m.monthName ?? m.name ?? '';

  let batchId = null, batchName = '';

  if (m.batch !== undefined && m.batch !== null) {
    // if batch is an object with details
    if (typeof m.batch === 'object') {
      batchId = m.batch.batchId ?? m.batch.id ?? m.batch.batch_id ?? null;
      batchName = m.batch.batchName ?? m.batch.name ?? m.batch.batch_name ?? '';
    } else {
      // batch is a primitive id (number or string)
      batchId = m.batch;
      batchName = '';
    }
  } else {
    // fallback flattened fields
    batchId = m.batchId ?? m.batch_id ?? null;
    batchName = m.batchName ?? m.batch_name ?? '';
  }

  // normalize numeric strings to number where possible
  if (typeof batchId === 'string' && /^\d+$/.test(batchId)) batchId = parseInt(batchId, 10);

  return { monthId, monthName, batchId, batchName };
}

// --- Open / Close modal ---
openModalBtn.addEventListener('click', () => {
  isEdit = false;
  modalTitle.textContent = 'Add Month';
  monthForm.reset();
  monthIdField.value = '';
  showModal();
  loadBatches(true); // ensure latest batch list when opening (force fetch)
});

closeModalBtn.addEventListener('click', hideModal);
window.addEventListener('click', (e) => {
  if (e.target === monthModal) hideModal();
});

function showModal() {
  monthModal.classList.add('show');
  monthModal.setAttribute('aria-hidden', 'false');
}
function hideModal() {
  monthModal.classList.remove('show');
  monthModal.setAttribute('aria-hidden', 'true');
}

// --- Load batches into dropdown and populate batchesById map ---
async function loadBatches(force = false) {
  try {
    // simple cache unless force=true
    if (!force && Object.keys(batchesById).length > 0) {
      // still ensure dropdown is populated from cache
      populateBatchSelectFromCache();
      return;
    }

    const res = await fetch(`${BATCHES_BASE}/all`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error(`Failed to fetch batches (${res.status})`);
    const batches = await res.json();

    console.debug('Raw batches response:', batches);

    batchesById = {}; // reset
    batchSelect.innerHTML = '<option value="">-- Select Batch --</option>';

    (Array.isArray(batches) ? batches : []).forEach(b => {
      // accept multiple possible shapes
      const id = b.batchId ?? b.id ?? b.batch_id ?? (b.batch && (b.batch.batchId ?? b.batch.id)) ?? null;
      const name = b.batchName ?? b.name ?? b.batch_name ?? (b.batch && (b.batch.batchName ?? b.batch.name)) ?? `Batch ${id}`;

      if (id === null || id === undefined) return; // skip malformed
      const key = typeof id === 'number' ? id : String(id);
      batchesById[key] = name;

      const opt = document.createElement('option');
      opt.value = key;
      opt.textContent = name;
      batchSelect.appendChild(opt);
    });
  } catch (err) {
    console.error('Error loading batches:', err);
    Swal.fire('Error', 'Could not load batches for dropdown', 'error');
  }
}

function populateBatchSelectFromCache() {
  batchSelect.innerHTML = '<option value="">-- Select Batch --</option>';
  Object.keys(batchesById).forEach(k => {
    const opt = document.createElement('option');
    opt.value = k;
    opt.textContent = batchesById[k];
    batchSelect.appendChild(opt);
  });
}

// --- Load months and render table ---
async function loadMonths() {
  try {
    // make sure we have a batches map so we can display names reliably
    await loadBatches(); // safe to call; it may use cache

    const res = await fetch(`${MONTHS_BASE}/all`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) {
      if (res.status === 403) throw new Error('Access denied. Please check your permissions.');
      throw new Error(`Failed to fetch months (${res.status})`);
    }

    const months = await res.json();
    console.debug('Raw months response:', months);

    monthTableBody.innerHTML = '';

    if (!Array.isArray(months) || months.length === 0) {
      monthTableBody.innerHTML = `
        <tr><td colspan="4" class="empty-state">
          <i class="fas fa-calendar-alt" style="font-size:32px; display:block; margin-bottom:8px;"></i>
          No months found. Add your first month.
        </td></tr>`;
      return;
    }

    for (const m of months) {
      const { monthId, monthName, batchId, batchName } = parseMonthBackendShape(m);
      const lookupKey = batchId === null || batchId === undefined ? null : String(batchId);
      const displayBatchName = batchName || (lookupKey && batchesById[lookupKey]) || (lookupKey ? `Batch ${lookupKey}` : '—');

      const safeMonthId = JSON.stringify(monthId); // handles strings and numbers safely

      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(monthId ?? '')}</td>
        <td>${escapeHtml(monthName)}</td>
        <td>${escapeHtml(displayBatchName)}</td>
        <td>
          <button class="action-btn update" onclick="editMonth(${safeMonthId})"><i class="fas fa-edit"></i> Edit</button>
          <button class="action-btn delete" onclick="deleteMonth(${safeMonthId})"><i class="fas fa-trash"></i> Delete</button>
        </td>
      `;
      monthTableBody.appendChild(tr);
    }
  } catch (err) {
    console.error('Error loading months:', err);
    monthTableBody.innerHTML = `<tr><td colspan="4" style="text-align:center; color:#ff6b6b;">${escapeHtml(err.message)}</td></tr>`;
  }
}

// small helper to prevent accidental HTML injection in table text
function escapeHtml(str) {
  if (str === null || str === undefined) return '';
  return String(str).replace(/[&<>"'`=\/]/g, (s) => ( {
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;', '/': '&#x2F;', '`': '&#x60;', '=': '&#x3D;'
  })[s]);
}

// --- Create / Update month ---
monthForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  saveMonthBtn.disabled = true;
  const id = monthIdField.value;
  const payload = {
    monthName: monthNameField.value.trim(),
    batchId: batchSelect.value ? (isNaN(batchSelect.value) ? batchSelect.value : parseInt(batchSelect.value, 10)) : null
  };

  try {
    if (!payload.monthName) throw new Error('Month name is required');
    if (!payload.batchId) throw new Error('Select a batch');

    let url = `${MONTHS_BASE}/create`, method = 'POST';
    if (isEdit && id) {
      url = `${MONTHS_BASE}/update/${id}`;
      method = 'PUT';
      payload.monthId = isNaN(id) ? id : parseInt(id, 10);
    }

    const res = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      Swal.fire('Success', `Month ${isEdit ? 'updated' : 'added'} successfully.`, 'success');
      hideModal();
      loadMonths();
    } else {
      const text = await res.text();
      throw new Error(text || `Failed to ${isEdit ? 'update' : 'create'} month`);
    }
  } catch (err) {
    console.error('Error saving month:', err);
    Swal.fire('Error', err.message || 'Failed to save month', 'error');
  } finally {
    saveMonthBtn.disabled = false;
  }
});

// --- Edit month ---
window.editMonth = async function (id) {
  try {
    isEdit = true;
    modalTitle.textContent = 'Edit Month';
    monthForm.reset();

    // fetch month details
    const res = await fetch(`${MONTHS_BASE}/${id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch month details');
    const m = await res.json();

    const parsed = parseMonthBackendShape(m);

    monthIdField.value = parsed.monthId;
    monthNameField.value = parsed.monthName;

    // populate dropdown and set selected batch
    await loadBatches(true);
    // set value with a small delay to ensure options exist
    setTimeout(() => {
      if (parsed.batchId !== null && parsed.batchId !== undefined) {
        batchSelect.value = String(parsed.batchId);
      } else {
        batchSelect.value = '';
      }
    }, 50);

    showModal();
  } catch (err) {
    console.error('Error editing month:', err);
    Swal.fire('Error', 'Failed to load month details', 'error');
  }
};

// --- Delete month ---
window.deleteMonth = async function (id) {
  const confirm = await Swal.fire({
    title: 'Are you sure?',
    text: "This will permanently delete the month mapping.",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Yes, delete it!',
    cancelButtonText: 'Cancel'
  });

  if (!confirm.isConfirmed) return;

  try {
    const res = await fetch(`${MONTHS_BASE}/delete/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (res.ok) {
      Swal.fire('Deleted!', 'Month has been deleted.', 'success');
      loadMonths();
    } else {
      const text = await res.text();
      throw new Error(text || 'Failed to delete month');
    }
  } catch (err) {
    console.error('Error deleting month:', err);
    Swal.fire('Error', err.message || 'Failed to delete month', 'error');
  }
};

// --- Initial load ---
document.addEventListener('DOMContentLoaded', () => {
  // Protect UI if no token
  if (!token) return;
  loadBatches(); // seed dropdown
  loadMonths();
});
