document.addEventListener('DOMContentLoaded', function() {
    const nextButton = document.querySelector('.nextbutton');
    const phoneInput = document.querySelector('input[name="phonenum"]');
    const checkInput = document.querySelector('input[name="checknum"]');
    const authButton = document.querySelector('.inner-button1');
    const confirmButton = document.querySelector('.inner-button2');

    let isAuthSent = false;
    let isConfirmChecked = false;

    authButton.addEventListener('click', function() {
        const phoneValue = phoneInput.value.trim();
        const isNumeric = /^\d+$/.test(phoneValue);

        if (phoneValue === '') {
            alert('전화번호를 입력하세요.');
            return;
        }

        if (!isNumeric) {
            alert('제대로된 전화번호를 입력하세요.');
            return;
        }

            //인증번호 발송 로직 추가하기

        alert('인증번호가 발송되었습니다.');
        isAuthSent = true;
    });

    confirmButton.addEventListener('click', function() {
        if (!isAuthSent) {
            alert('먼저 전화번호로 인증해 주세요.');
            return;
        }

        const checkValue = checkInput.value.trim();

        if (checkValue === '') {
            alert('인증번호를 입력하세요.');
            return;
        }

        //인증번호 확인 로직 추가하기

        alert('인증번호가 확인되었습니다.');
        isConfirmChecked = true;
    });

    nextButton.addEventListener('click', function() {
        const phoneValue = phoneInput.value.trim();
        const checkValue = checkInput.value.trim();
        const isNumeric = /^\d+$/.test(phoneValue);

        if (phoneValue === '') {
            alert('전화번호를 입력하세요.');
            return;
        }

        if (!isNumeric) {
            alert('제대로된 전화번호를 입력하세요.');
            return;
        }

        if (checkValue === '') {
            alert('인증번호를 입력하세요.');
            return;
        }

        if (!isAuthSent) {
            alert('먼저 전화번호로 인증해 주세요.');
            return;
        }

        if (!isConfirmChecked) {
            alert('먼저 인증번호를 입력해 주세요.');
            return;
        }

        location.href = '/security-login/find-email-2';
    });
});
