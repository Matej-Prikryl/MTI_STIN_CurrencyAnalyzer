const calculateBtn = document.getElementById('calculate-btn');
const strongestCurrencyDisplay = document.getElementById('strongest-currency');
const weakestCurrencyDisplay = document.getElementById('weakest-currency');
const resultsBody = document.getElementById('results-body');

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

const translations = {
    "en": {
        "app-title": "Currency Analyzer",
        "base-currency": "Base Currency",
        "start-date": "From",
        "end-date": "To",
        "calculate-btn": "Calculate",
        "strongest-currency": "Strongest",
        "weakest-currency": "Weakest",
        "currency": "Currency",
        "average-rate": "Average Rate"
    },
    "cs": {
        "app-title": "Měnový Analyzér",
        "base-currency": "Základní Měna",
        "start-date": "Od",
        "end-date": "Do",
        "calculate-btn": "Vypočítat",
        "strongest-currency": "Nejsilnější",
        "weakest-currency": "Nejslabší",
        "currency": "Měna",
        "average-rate": "Průměrný Kurz"
    }
};

let currentLang = 'en'; //localStorage.getItem('lang') || 'cs';

function changeLanguage(lang) {
    currentLang = lang;
    localStorage.setItem('lang', lang); // Zapamatujeme si volbu
    
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        if (translations[lang][key]) {
            element.textContent = translations[lang][key];
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {

    changeLanguage(currentLang);

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