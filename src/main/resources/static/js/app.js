import { auth } from './auth.js';
import { repairs } from './repairs.js';
import { customers } from './customers.js';
import { audit } from './audit.js';
import { reports } from './reports.js';
import { settings } from './settings.js';

// State Management
let currentRepairs = [];
let currentAuditLogs = [];
let openPanelId = null;

// DOM Elements
let loginPage, app, repairsTbody, auditTbody, customerTbody, navUsername, navAvatar;

// Initial Setup
document.addEventListener('DOMContentLoaded', () => {
    // Initialize DOM Elements
    loginPage = document.getElementById('login-page');
    app = document.getElementById('app');
    repairsTbody = document.getElementById('repairs-tbody');
    customerTbody = document.getElementById('customer-tbody');
    navUsername = document.querySelector('.nav-username');
    navAvatar = document.querySelector('.nav-avatar');

    buildMatrix();
    if (auth.isLoggedIn()) {
        showApp();
    } else {
        showLogin();
    }

    // Event Listeners
    setupEventListeners();
});

function setupEventListeners() {
    // Login form
    const loginBtn = document.querySelector('.btn-login');
    if (loginBtn) {
        loginBtn.addEventListener('click', handleLogin);
    }

    // Add Repair form
    const saveRepairBtn = document.querySelector('.drawer-footer .btn-primary');
    if (saveRepairBtn) {
        saveRepairBtn.addEventListener('click', handleAddRepair);
    }
}

// UI Controllers
window.showApp = async function() {
    loginPage.style.display = 'none';
    app.style.display = 'block';

    // Set username in nav
    const username = auth.getUsername();
    navUsername.textContent = username;
    navAvatar.textContent = username.substring(0, 2).toUpperCase();

    // Initial data load
    loadRepairs();
}

window.showLogin = function() {
    loginPage.style.display = 'flex';
    app.style.display = 'none';
}

window.switchNav = function(el, section) {
    document.querySelectorAll('.nav-link').forEach(n => n.classList.remove('active'));
    if (el) el.classList.add('active');
    document.querySelectorAll('.page-section').forEach(s => s.classList.remove('active'));
    const target = document.getElementById('sec-' + section);
    if (target) target.classList.add('active');

    // Load data based on section
    if (section === 'repairs') loadRepairs();
    if (section === 'sales') loadSalesData();
    if (section === 'audit') loadAuditLogs();
    if (section === 'settings') loadSettingsData();
    if (section === 'customers') loadCustomers();
}

// Authentication Logic
async function handleLogin() {
    const usernameInput = document.querySelector('input[type="text"]');
    const passwordInput = document.querySelector('input[type="password"]');

    const btn = document.querySelector('.btn-login');
    const originalText = btn.textContent;
    btn.textContent = 'Signing in...';
    btn.disabled = true;

    try {
        await auth.login(usernameInput.value, passwordInput.value);
        showApp();
    } catch (error) {
        alert('Login failed: ' + error.message);
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

window.handleLogout = function() {
    auth.logout();
}

// Forgot Password Logic
window.showForgotPassword = function() {
    document.getElementById('login-card').style.display = 'none';
    document.getElementById('forgot-panel').style.display = 'block';
    const successMsg = document.getElementById('reset-success');
    if (successMsg) successMsg.style.display = 'none';
    document.getElementById('reset-email').value = '';
}

window.hideForgotPassword = function() {
    document.getElementById('forgot-panel').style.display = 'none';
    document.getElementById('login-card').style.display = 'block';
}

window.sendResetLink = async function() {
    const email = document.getElementById('reset-email').value.trim();
    if (!email) return;
    const btn = document.getElementById('reset-btn');
    const originalText = btn.textContent;
    btn.textContent = 'Sending...';
    btn.disabled = true;
    try {
        await auth.forgotPassword(email);
        alert('Reset link sent! Check your email inbox.');
        hideForgotPassword();
    } catch (error) {
        alert('Failed to send reset link: ' + error.message);
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

// Repair Logic
async function loadRepairs() {
    try {
        currentRepairs = await repairs.getAll();
        renderRepairs();
        updateRepairStats();
    } catch (error) {
        console.error('Failed to load repairs:', error);
    }
}

function renderRepairs() {
    repairsTbody.innerHTML = '';
    currentRepairs.forEach(r => {
        const row = document.createElement('tr');
        row.id = `row-${r.id}`;

        const statusDot = r.status === 'COMPLETED' ? 'dot-done' : (r.status === 'ONGOING' ? 'dot-active' : 'dot-empty');
        const badgeClass = `badge-${r.status.toLowerCase()}`;
        const payBadgeClass = `badge-${r.paymentStatus.toLowerCase()}`;

        row.innerHTML = `
            <td><span class="status-dot ${statusDot}"></span></td>
            <td class="td-mono">#${r.id}</td>
            <td class="td-name">${r.customerName}</td>
            <td class="td-mono">${r.contactNumber}</td>
            <td>${r.device}</td>
            <td class="td-dim" style="max-width:140px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${r.issue}</td>
            <td><span class="badge ${badgeClass}">${r.status}</span></td>
            <td class="td-mono">₱${r.bill.toLocaleString()}</td>
            <td><span class="badge ${payBadgeClass}">${r.paymentStatus}</span></td>
            <td class="td-mono" style="font-size:10px;">${new Date(r.dateReceived).toLocaleDateString()}</td>
            <td><div style="display:flex;gap:5px;">
                <button class="btn btn-ghost btn-sm" onclick="toggleView('${r.id}')">View</button>
            </div></td>
        `;

        const panelRow = document.createElement('tr');
        panelRow.id = `panel-${r.id}`;
        panelRow.style.display = 'none';
        panelRow.innerHTML = `<td colspan="11" style="padding:0;border-bottom:1px solid var(--border);"></td>`;

        repairsTbody.appendChild(row);
        repairsTbody.appendChild(panelRow);
    });
}

function updateRepairStats() {
    const statsAll = document.querySelector('.stat-block.s-maroon .stat-value');
    if (statsAll) statsAll.textContent = currentRepairs.length;
    const statsPending = document.querySelector('.stat-block.s-yellow .stat-value');
    if (statsPending) statsPending.textContent = currentRepairs.filter(r => r.status === 'PENDING').length;
    const statsOngoing = document.querySelector('.stat-block.s-blue .stat-value');
    if (statsOngoing) statsOngoing.textContent = currentRepairs.filter(r => r.status === 'ONGOING').length;
    const statsCompleted = document.querySelector('.stat-block.s-green .stat-value');
    if (statsCompleted) statsCompleted.textContent = currentRepairs.filter(r => r.status === 'COMPLETED').length;
}

window.toggleView = function(id) {
    if (openPanelId && openPanelId !== id) {
        closeView(openPanelId);
    }
    const panel = document.getElementById('panel-' + id);
    if (panel.style.display === 'none' || panel.style.display === '') {
        openView(id);
    } else {
        closeView(id);
    }
}

function closeView(id) {
    const panel = document.getElementById('panel-' + id);
    if (panel) { panel.style.display = 'none'; panel.querySelector('td').innerHTML = ''; }
    const btn = document.querySelector(`#row-${id} .btn`);
    if (btn) btn.textContent = 'View';
    openPanelId = null;
}

async function openView(id) {
    const r = currentRepairs.find(repair => repair.id == id);
    if (!r) return;
    openPanelId = id;

    const statusOpts = ['PENDING', 'ONGOING', 'COMPLETED'].map(s =>
        `<option value="${s}" ${r.status === s ? 'selected' : ''}>${s}</option>`
    ).join('');

    const panel = document.getElementById('panel-' + id);
    panel.style.display = 'table-row';
    panel.querySelector('td').innerHTML = `
        <div style="background:var(--surface2);border-top:2px solid var(--maroon);padding:0;">
            <div style="display:grid;grid-template-columns:220px 1fr 1fr;gap:0;border-bottom:1px solid var(--border);">
                <div style="padding:16px 20px;border-right:1px solid var(--border);">
                    <div style="font-size:10px;color:var(--text3);letter-spacing:1px;text-transform:uppercase;font-weight:600;margin-bottom:12px;">Customer</div>
                    <div style="font-size:14px;font-weight:600;margin-bottom:8px;">${r.customerName}</div>
                    <div style="margin-bottom:6px;"><span style="font-size:10px;color:var(--text3);">PHONE  </span><span style="font-family:JetBrains Mono,monospace;font-size:12px;">${r.contactNumber}</span></div>
                    <div style="margin-bottom:6px;"><span style="font-size:10px;color:var(--text3);">EMAIL  </span><span style="font-family:JetBrains Mono,monospace;font-size:11px;">${r.customerEmail || 'N/A'}</span></div>
                    <div><span style="font-size:10px;color:var(--text3);">ADDR   </span><span style="font-size:12px;color:var(--text2);">${r.address || 'N/A'}</span></div>
                </div>
                <div style="padding:16px 20px;border-right:1px solid var(--border);">
                    <div style="font-size:10px;color:var(--text3);letter-spacing:1px;text-transform:uppercase;font-weight:600;margin-bottom:12px;">Device & Issue</div>
                    <div style="margin-bottom:6px;"><span style="font-size:10px;color:var(--text3);">DEVICE  </span><span style="font-size:13px;font-weight:500;">${r.device}</span></div>
                    <div style="margin-bottom:6px;"><span style="font-size:10px;color:var(--text3);">SERIAL  </span><span style="font-family:JetBrains Mono,monospace;font-size:11px;color:var(--text2);">${r.serialNumber || 'N/A'}</span></div>
                    <div style="margin-bottom:6px;"><span style="font-size:10px;color:var(--text3);">UUID    </span><span style="font-family:JetBrains Mono,monospace;font-size:10px;color:var(--text3);">${r.uuid}</span></div>
                    <div style="margin-top:10px;font-size:10px;color:var(--text3);margin-bottom:4px;">ISSUE</div>
                    <div style="font-size:12px;color:var(--text2);line-height:1.6;">${r.issue}</div>
                    ${r.notes ? `<div style="margin-top:8px;font-size:10px;color:var(--text3);">NOTES</div><div style="font-size:12px;color:var(--text2);">${r.notes}</div>` : ''}
                </div>
                <div style="padding:16px 20px;">
                    <div style="font-size:10px;color:var(--text3);letter-spacing:1px;text-transform:uppercase;font-weight:600;margin-bottom:12px;">Status & Actions</div>
                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:14px;">
                        <div>
                            <div style="font-size:10px;color:var(--text3);margin-bottom:4px;">REPAIR STATUS</div>
                            <select id="status-${r.id}" class="form-select" style="padding:7px 10px;font-size:12px;">${statusOpts}</select>
                        </div>
                        <div>
                            <div style="font-size:10px;color:var(--text3);margin-bottom:4px;">BILL AMOUNT</div>
                            <input id="bill-${r.id}" type="text" value="${r.bill}" class="form-input" style="padding:7px 10px;font-family:JetBrains Mono,monospace;font-size:12px;"/>
                        </div>
                        <div>
                            <div style="font-size:10px;color:var(--text3);margin-bottom:4px;">PAYMENT</div>
                            <select id="pay-${r.id}" class="form-select" style="padding:7px 10px;font-size:12px;">
                                <option value="UNPAID" ${r.paymentStatus === 'UNPAID' ? 'selected' : ''}>UNPAID</option>
                                <option value="PAID" ${r.paymentStatus === 'PAID' ? 'selected' : ''}>PAID</option>
                            </select>
                        </div>
                    </div>
                    <div style="display:flex;gap:6px;flex-wrap:wrap;">
                        <button class="btn btn-primary btn-sm" onclick="saveChanges('${r.id}')">Save Changes</button>
                        ${r.status === 'COMPLETED' ? `<button class="btn btn-ghost btn-sm" onclick="notifyCustomer('${r.id}')">✉ Notify</button>` : ''}
                        <button class="btn btn-danger btn-sm" onclick="handleDelete('${r.id}')">🗑 Delete</button>
                        <button class="btn btn-ghost btn-sm" onclick="toggleView('${r.id}')">✕ Close</button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

window.saveChanges = async function(id) {
    const status = document.getElementById(`status-${id}`).value;
    const bill = document.getElementById(`bill-${id}`).value;
    const payment = document.getElementById(`pay-${id}`).value;

    try {
        await repairs.updateStatus(id, status);
        await repairs.updatePayment(id, payment);

        const r = currentRepairs.find(repair => repair.id == id);
        r.bill = bill;
        await repairs.update(id, r);

        loadRepairs();
        alert('Changes saved successfully');
    } catch (error) {
        alert('Failed to save changes: ' + error.message);
    }
}

window.handleDelete = async function(id) {
    if (!confirm('Are you sure you want to delete this repair record?')) return;
    try {
        await repairs.delete(id);
        loadRepairs();
    } catch (error) {
        alert('Failed to delete: ' + error.message);
    }
}

window.notifyCustomer = async function(id) {
    try {
        await repairs.notify(id);
        alert('Notification sent to customer');
    } catch (error) {
        alert('Failed to send notification: ' + error.message);
    }
}

async function handleAddRepair() {
    const repairData = {
        customerName: document.querySelector('input[placeholder="e.g. Juan dela Cruz"]').value,
        contactNumber: document.querySelector('input[placeholder="09XXXXXXXXX"]').value,
        customerEmail: document.querySelector('input[placeholder="customer@email.com"]').value,
        address: document.querySelector('input[placeholder="Barangay, City"]').value,
        device: document.querySelector('input[placeholder="e.g. iPhone 13 Pro"]').value,
        serialNumber: document.querySelector('input[placeholder="Device serial number"]').value,
        issue: document.querySelector('textarea[placeholder="Describe the problem in detail..."]').value
    };

    const photoInput = document.querySelector('.upload-box input[type="file"]');
    const photoFile = photoInput ? photoInput.files[0] : null;

    try {
        await repairs.add(repairData, photoFile);
        closeDrawer();
        loadRepairs();
        alert('Repair added successfully');
    } catch (error) {
        alert('Failed to add repair: ' + error.message);
    }
}

// Customers Logic
async function loadCustomers() {
    try {
        const data = await customers.getAll();
        customerTbody.innerHTML = '';
        data.forEach(c => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td class="td-name">${c.name}</td>
                <td class="td-mono">${c.contactNumber}</td>
                <td class="td-mono" style="font-size:11px;">${c.email || 'N/A'}</td>
                <td class="td-dim">${c.address || 'N/A'}</td>
                <td><span class="badge badge-maroon">${c.totalVisits} repairs</span></td>
                <td class="td-mono" style="font-size:10px;">${c.lastVisitDate ? new Date(c.lastVisitDate).toLocaleDateString() : 'Never'}</td>
                <td class="row-arrow">→</td>
            `;
            customerTbody.appendChild(row);
        });
    } catch (error) {
        console.error('Failed to load customers:', error);
    }
}

// Audit Logic
async function loadAuditLogs() {
    try {
        currentAuditLogs = await audit.getAll();
        renderAuditLogs();
    } catch (error) {
        console.error('Failed to load audit logs:', error);
    }
}

function renderAuditLogs() {
    const container = document.querySelector('#sec-audit .data-table-wrap');
    if (!container) return;
    container.innerHTML = '';
    currentAuditLogs.sort((a,b) => new Date(b.timestamp) - new Date(a.timestamp)).forEach(log => {
        const row = document.createElement('div');
        row.className = 'audit-row';
        const indicatorColor = log.action.includes('deleted') ? 'var(--red)' : (log.action.includes('added') ? 'var(--green)' : 'var(--blue)');
        row.innerHTML = `
            <div class="audit-indicator" style="background:${indicatorColor};"></div>
            <div class="audit-content">
                <div class="audit-title">${log.action}</div>
                <div class="audit-desc">${log.details || ''} · by <strong>${log.username}</strong></div>
            </div>
            <div class="audit-time">${new Date(log.timestamp).toLocaleString()}</div>
        `;
        container.appendChild(row);
    });
}

// Sales Logic
async function loadSalesData() {
    try {
        const totalRevenue = await reports.getRevenue();
        const statusSummary = await reports.getStatusSummary();

        const revValue = document.querySelector('#sec-sales .stat-block.s-maroon .stat-value');
        if (revValue) revValue.textContent = `₱${totalRevenue.toLocaleString()}`;
        const compValue = document.querySelector('#sec-sales .stat-block.s-blue .stat-value');
        if (compValue) compValue.textContent = statusSummary.COMPLETED || 0;

        // Load paid repairs for the current month
        const now = new Date();
        const firstDay = new Date(now.getFullYear(), now.getMonth(), 1).toISOString();
        const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString();
        const paidRepairs = await reports.getPaidRepairs(firstDay, lastDay);

        const paidTbody = document.querySelector('#sec-sales .data-table tbody');
        if (paidTbody) {
            paidTbody.innerHTML = '';
            paidRepairs.forEach(r => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td class="td-mono">#${r.id}</td>
                    <td>${r.customerName}</td>
                    <td class="td-dim">${r.device}</td>
                    <td class="td-mono" style="color:var(--green);">₱${r.bill.toLocaleString()}</td>
                    <td class="td-mono" style="font-size:10px;">${new Date(r.dateReceived).toLocaleDateString()}</td>
                `;
                paidTbody.appendChild(row);
            });
        }
    } catch (error) {
        console.error('Failed to load sales data:', error);
    }
}

// Settings Logic
async function loadSettingsData() {
    try {
        const freeSpace = await reports.getFreeSpace();
        const storageStatus = await reports.getStorageStatus();

        const storageFill = document.querySelector('.storage-fill');
        if (storageFill) storageFill.style.width = '34%';
        const storageVal = document.querySelector('.info-row .info-val.green');
        if (storageVal) storageVal.textContent = storageStatus;
    } catch (error) {
        console.error('Failed to load settings data:', error);
    }
}

// Drawer & Modal Logic
window.openDrawer = function() {
    document.getElementById('add-repair-drawer').classList.add('open');
    document.getElementById('drawer-overlay').classList.add('open');
    document.body.style.overflow = 'hidden';
}

window.closeDrawer = function() {
    document.getElementById('add-repair-drawer').classList.remove('open');
    document.getElementById('drawer-overlay').classList.remove('open');
    document.body.style.overflow = '';
}

window.switchSettingsTab = function(el, tabId) {
    document.querySelectorAll('.settings-tab').forEach(t => t.style.display = 'none');
    document.querySelectorAll('.tabs .tab').forEach(t => t.classList.remove('active'));
    document.getElementById(tabId).style.display = 'block';
    el.classList.add('active');
}

// Matrix Background Logic
function buildMatrix() {
    const container = document.getElementById('matrix-cols');
    if (!container) return;
    const chars = '01アイウエオカキクケコサシスセソRQTECH2025';
    const cols = Math.floor(window.innerWidth / 16);
    for (let i = 0; i < cols; i++) {
        const col = document.createElement('div');
        col.className = 'matrix-col';
        let str = '';
        for (let j = 0; j < 60; j++) str += chars[Math.floor(Math.random() * chars.length)] + ' ';
        col.textContent = str;
        col.style.animationDuration = (8 + Math.random() * 20) + 's';
        col.style.animationDelay = (-Math.random() * 20) + 's';
        col.style.opacity = (0.03 + Math.random() * 0.06).toString();
        container.appendChild(col);
    }
}