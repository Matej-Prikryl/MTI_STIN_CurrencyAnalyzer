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
        const data = await response.json();
        console.log(data);

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
        console.error('Error fetching currency rates:', error);
    }

});


document.addEventListener('DOMContentLoaded', () => {

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