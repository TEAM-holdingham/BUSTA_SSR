document.addEventListener('DOMContentLoaded', function() {
    const confirmButton = document.querySelector('.inner-button');
    const emailInput = document.querySelector('input[type="email"]');

    confirmButton.addEventListener('click', function() {

        const email = emailInput.value.trim();

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(email)) {
            alert('올바른 이메일 주소를 입력하세요.');
            return;
        }

        location.href = 'find_password-2.html';
    });
});