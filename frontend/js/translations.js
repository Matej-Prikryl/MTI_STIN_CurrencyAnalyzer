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
        "average-rate": "Average Rate",

        "login-title": "Log In",
        "login-username": "Username",
        "login-password": "Password",
        "login-button": "Log In"
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
        "average-rate": "Průměrný Kurz",

        "login-title": "Přihlášení",
        "login-username": "Uživatelské jméno",
        "login-password": "Heslo",
        "login-button": "Přihlásit se"
    }
};

let currentLang = 'en'; //localStorage.getItem('lang') || 'cs';

function changeLanguage(lang) {
    currentLang = lang;
    localStorage.setItem('lang', lang);
    
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        if (translations[lang][key]) {
            element.textContent = translations[lang][key];
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    changeLanguage(currentLang);
});