// API Endpoints
const API = {
    CURRENCIES: '/api/rates/supported-currencies',
    RATES: '/api/rates/currencyinfo',
    SETTINGS: '/api/settings'
};

// DOM Elements
const elements = {
    calculateBtn: document.getElementById('calculate-btn'),
    strongestCurrency: document.getElementById('strongest-currency'),
    weakestCurrency: document.getElementById('weakest-currency'),
    resultsBody: document.getElementById('results-body'),
    settingsBtn: document.getElementById('settings-btn'),
    settingsScreen: document.getElementById('settings-screen'),
    saveSettingsBtn: document.getElementById('save-settings-btn'),
    baseCurrency: document.getElementById('base-currency'),
    startDate: document.getElementById('start-date'),
    endDate: document.getElementById('end-date')
};

// State
let currentLang = localStorage.getItem('lang') || 'en';
let currentTargetCurrencies = ['USD', 'GBP', 'CZK'];

// Initialize
document.addEventListener('DOMContentLoaded', init);

async function init() {
    loadSupportedCurrencies();
    loadSettings();
    initDatePickers();
    setupEventListeners();
}

async function loadSupportedCurrencies() {
    try {
        const response = await fetch(`${API.CURRENCIES}`);
        const currencies = await response.json();
        const dropdown = elements.baseCurrency;
        const container = document.querySelector('.preferred-currencies-selector');

        currencies.forEach(currency => {
            const id = `currency-${currency}`;
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.id = id;
            checkbox.name = 'currency';
            checkbox.value = currency;
            checkbox.checked = currentTargetCurrencies.includes(currency);
            container.appendChild(checkbox);

            const label = document.createElement('label');
            label.htmlFor = id;
            label.textContent = currency;
            container.appendChild(label);

            const option = document.createElement('option');
            option.value = currency;
            option.textContent = currency;
            dropdown.appendChild(option);
        });

    } catch (error) {
        console.error('Error fetching supported currencies:', error);
    }
}

function initDatePickers(lang = currentLang) {
    const dateConfig = {
        locale: lang,
        dateFormat: 'Y-m-d',
        maxDate: 'today',
        disableMobile: 'true'
    };

    const startPicker = flatpickr('#start-date', {
        ...dateConfig,
        onChange: (selectedDates, dateStr) => {
            endPicker.set('minDate', dateStr);
        }
    });

    const endPicker = flatpickr('#end-date', {
        ...dateConfig,
        onChange: (selectedDates, dateStr) => {
            startPicker.set('maxDate', dateStr);
        }
    });
}

function setupEventListeners() {
    elements.calculateBtn.addEventListener('click', handleCalculate);
    elements.settingsBtn.addEventListener('click', toggleSettings);
    elements.saveSettingsBtn.addEventListener('click', handleSaveSettings);
}

async function handleCalculate() {
    const { baseCurrency, startDate, endDate } = getFormValues();

    if (!startDate || !endDate) {
        alert('Please select both start and end dates.');
        return;
    }

    const url = buildRatesUrl(baseCurrency, startDate, endDate);

    try {
        const data = await fetchData(url);
        updateUI(data);
    } catch (error) {
        console.error('Error fetching rates:', error);
        alert('Unable to connect to server.');
    }
}

function getFormValues() {
    return {
        baseCurrency: elements.baseCurrency.value,
        startDate: elements.startDate.value,
        endDate: elements.endDate.value
    };
}

function buildRatesUrl(base, startDate, endDate) {
    const currencies = currentTargetCurrencies.join(',');
    return `${API.RATES}?base=${base}&startDate=${startDate}&endDate=${endDate}&targetCurrencies=${currencies}`;
}

async function fetchData(url) {
    const response = await fetch(url);

    if (response.redirected) {
        window.location.href = response.url;
        return;
    }

    if (!response.ok) {
        const error = await response.json().catch(() => ({ message: 'Unknown Server Error' }));
        alert(`Error (${response.status}): ${error.message}`);
        throw new Error(`HTTP ${response.status}`);
    }

    return response.json();
}

function updateUI(data) {
    updateExtremes(data.extremes);
    updateRatesTable(data.averages);
}

function updateExtremes(extremes) {
    elements.strongestCurrency.querySelector('h1').textContent = extremes.strongest.key;
    elements.strongestCurrency.querySelector('p').textContent = extremes.strongest.value.toFixed(2);

    elements.weakestCurrency.querySelector('h1').textContent = extremes.weakest.key;
    elements.weakestCurrency.querySelector('p').textContent = extremes.weakest.value.toFixed(2);
}

function updateRatesTable(averages) {
    elements.resultsBody.innerHTML = '';

    Object.entries(averages).forEach(([currency, value]) => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${currency}</td>
            <td>${value.toFixed(2)}</td>
        `;
        elements.resultsBody.appendChild(row);
    });
}

function toggleSettings() {
    const isHidden = elements.settingsScreen.classList.contains('hidden');
    
    if (isHidden) {
        elements.settingsScreen.classList.remove('hidden');
        elements.settingsScreen.classList.add('flex');
    } else {
        elements.settingsScreen.classList.add('hidden');
        elements.settingsScreen.classList.remove('flex');
    }
}

async function loadSettings() {
    try {
        const settings = await fetchData(API.SETTINGS);
        applySettings(settings);
    } catch (error) {
        console.error('Error loading settings:', error);
    }
}

function applySettings(settings) {
    changeLanguage(settings.language);
    
    const langRadio = document.querySelector(`.lang-select input[value="${settings.language}"]`);
    if (langRadio) langRadio.checked = true;

    elements.baseCurrency.value = settings.baseCurrency;
    
    document.querySelectorAll('.preferred-currencies-selector input').forEach((checkbox) => {
        checkbox.checked = settings.preferredCurrencies.includes(checkbox.value);
    });
}

async function handleSaveSettings() {
    const settings = collectSettings();
    
    try {
        const response = await fetch(API.SETTINGS, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(settings)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({ message: 'Unknown Server Error' }));
            alert(`Error (${response.status}): ${error.message}`);
            return;
        }

        applyRuntimeSettings(settings);
        toggleSettings();
        alert('Settings saved successfully.');
    } catch (error) {
        console.error('Error saving settings:', error);
        alert('Unable to connect to server.');
    }
}

function collectSettings() {
    return {
        language: document.querySelector('.lang-select input:checked').value,
        baseCurrency: elements.baseCurrency.value,
        preferredCurrencies: Array.from(
            document.querySelectorAll('.preferred-currencies-selector input:checked')
        ).map(input => input.value)
    };
}

function applyRuntimeSettings(settings) {
    changeLanguage(settings.language);
    initDatePickers(settings.language);
    currentTargetCurrencies = settings.preferredCurrencies;
}
