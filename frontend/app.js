const calculateBtn = document.getElementById('calculate-btn');
const ratesTable = document.getElementById('rates-table');

calculateBtn.addEventListener('click', async () => {
    const baseCurrency = document.getElementById('base-currency').value;
    const startDate = document.getElementById('start-date').value;
    const endDate = document.getElementById('end-date').value;

    console.log(`Base Currency: ${baseCurrency}, Start Date: ${startDate}, End Date: ${endDate}`);

    const url = `/api/rates/currencyinfo?base=${baseCurrency}&startDate=${startDate}&endDate=${endDate}&targetCurrencies=USD`;

    try {
        const response = await fetch(url);
        const data = await response.json();
        console.log(data);
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
        onChange: function(selectedDates, dateStr) {
            endPicker.set("minDate", dateStr);
        }
    });

    const endPicker = flatpickr("#end-date", {
        ...dateConfig,
        onChange: function(selectedDates, dateStr) {
            startPicker.set("maxDate", dateStr);
        }
    });
});