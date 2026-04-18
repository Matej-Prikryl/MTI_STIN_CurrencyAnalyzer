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