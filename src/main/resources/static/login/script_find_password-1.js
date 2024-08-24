document.addEventListener('DOMContentLoaded', function() {
    const confirmButton = document.querySelector('.inner-button');
    const emailInput = document.querySelector('input[type="email"]');

    confirmButton.addEventListener('click', function() {
        const email = emailInput.value.trim();

        // 이메일 유효성 검사 정규식
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(email)) {
            alert('올바른 이메일 주소를 입력하세요.');
            return;
        }

        // 서버로 비밀번호 재설정 요청을 보냄
        fetch('/password-reset/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                loginId: email
            })
        })
            .then(response => response.text())
            .then(data => {
                if (data.includes("Password reset email sent.")) {
                    // 이메일 전송 성공 시 처리
                    alert('비밀번호 재설정 이메일이 전송되었습니다.');
                    // 이메일이 유효한 경우 localStorage에 저장하고 다음 페이지로 이동
                    localStorage.setItem('userEmail', email);
                    location.href = '/security-login/find-password-2';
                } else {
                    // 실패 시 처리
                    alert('이메일 전송에 실패했습니다. 다시 시도하세요.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생했습니다. 다시 시도하세요.');
            });
    });
});
