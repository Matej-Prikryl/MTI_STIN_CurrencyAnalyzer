const calculateBtn = document.getElementById('calculate-btn');
const strongestCurrencyDisplay = document.getElementById('strongest-currency');
const weakestCurrencyDisplay = document.getElementById('weakest-currency');
const resultsBody = document.getElementById('results-body');
const settingsBtn = document.getElementById('settings-btn');
const settingsScreen = document.getElementById('settings-screen');
const saveSettingsBtn = document.getElementById('save-settings-btn');

calculateBtn.addEventListener('click', async () => {
    const baseCurrency = document.getElementById('base-currency').value;
    const startDate = document.getElementById('start-date').value;
    const endDate = document.getElementById('end-date').value;

    console.log(`Base Currency: ${baseCurrency}, Start Date: ${startDate}, End Date: ${endDate}`);

    const url = `/api/rates/currencyinfo?base=${baseCurrency}&startDate=${startDate}&endDate=${endDate}&targetCurrencies=USD,GBP,CZK`;

    try {
        const response = await fetch(url);

        if (response.redirected) {
            window.location.href = response.url;
            return;
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: "Unknown Server Error" }));

            alert(`Error (${response.status}): ${errorData.message || 'Unable to fetch data. Please try again later.'}`);
            return;
        }

        const data = await response.json();

        strongestCurrencyDisplay.querySelector('h1').textContent = data.extremes.strongest.key;
        strongestCurrencyDisplay.querySelector('p').textContent = data.extremes.strongest.value.toFixed(2);

        weakestCurrencyDisplay.querySelector('h1').textContent = data.extremes.weakest.key;
        weakestCurrencyDisplay.querySelector('p').textContent = data.extremes.weakest.value.toFixed(2);

        resultsBody.innerHTML = '';

        Object.entries(data.averages).forEach(([currency, value]) => {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${currency}</td>
                <td>${value.toFixed(2)}</td>
            `;

            resultsBody.appendChild(row);
        });

    } catch (error) {
        console.error('Network or JS Error:', error);
        alert('Unable to connect to server.');
    }
});

settingsBtn.addEventListener('click', () => {
    if (!settingsScreen.classList.contains('hidden')) {
        settingsScreen.classList.add('hidden');
        settingsScreen.classList.remove('flex');
        return;
    }
    settingsScreen.classList.remove('hidden');
    settingsScreen.classList.add('flex');
});

saveSettingsBtn.addEventListener('click', () => {
    saveSettings();

    settingsScreen.classList.add('hidden');
    settingsScreen.classList.remove('flex');
});


document.addEventListener('DOMContentLoaded', () => {
    loadSettings();

    const dateConfig = {
        locale: "en",
        dateFormat: "Y-m-d",
        maxDate: "today",
        disableMobile: "true"
    };

    const startPicker = flatpickr("#start-date", {
        ...dateConfig,
        onChange: function (selectedDates, dateStr) {
            endPicker.set("minDate", dateStr);
        }
    });

    const endPicker = flatpickr("#end-date", {
        ...dateConfig,
        onChange: function (selectedDates, dateStr) {
            startPicker.set("maxDate", dateStr);
        }
    });
});

async function loadSettings() {
    try {
        const response = await fetch('/api/settings');

        if (response.redirected) {
            window.location.href = response.url;
            return;
        }

        const settings = await response.json();

        // Apply settings to the UI
        changeLanguage(settings.language);
        document.querySelector(`.lang-select input[value="${settings.language}"]`).checked = true;
        document.getElementById('base-currency').value = settings.baseCurrency;
        document.querySelectorAll('.preferred-currencies-selector input').forEach((checkbox) => {
            checkbox.checked = settings.preferredCurrencies.includes(checkbox.value);
        });
    } catch (error) {
        console.error('Error loading settings:', error);
    }
}

async function saveSettings() {
    const language = document.querySelector('.lang-select input:checked').value;
    const baseCurrency = document.getElementById('base-currency').value;
    const preferredCurrencies = Array.from(document.querySelectorAll('.preferred-currencies-selector input:checked')).map(input => input.value);

    changeLanguage(language);

    const settings = {
        language,
        baseCurrency,
        preferredCurrencies
    };

    try {
        const response = await fetch('/api/settings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(settings)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: "Unknown Server Error" }));
            alert(`Error (${response.status}): ${errorData.message || 'Unable to save settings. Please try again later.'}`);
        } else {
            alert('Settings saved successfully.');
        }
    } catch (error) {
        console.error('Error saving settings:', error);
        alert('Unable to connect to server.');
    }

}
