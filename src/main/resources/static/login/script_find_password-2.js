// OTP 검증 함수
// OTP 검증 함수
function verifyEmailToken() {
    const otp = document.querySelector(".input_token input").value;
    const loginId = localStorage.getItem("userEmail"); // 저장된 이메일 가져오기

    if (!otp) {
        alert("OTP를 입력해 주세요.");
        return;
    }

    if (!loginId) {
        alert("이메일 정보가 누락되었습니다. 다시 시도해 주세요.");
        return;
    }

    fetch(`/password-reset/verify-otp?loginId=${encodeURIComponent(loginId)}&otp=${encodeURIComponent(otp)}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(response => response.text())
        .then(result => {
            if (result === "Valid OTP") {
                alert("OTP가 유효합니다. 비밀번호를 재설정해 주세요.");
                localStorage.setItem("otp", otp); // OTP를 저장
                document.querySelector(".reset_password").style.display = 'block';
            } else {
                alert("유효하지 않거나 만료된 OTP입니다.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("서버 오류가 발생했습니다.");
        });
}


// 비밀번호 재설정 함수
// 비밀번호 재설정 함수
function resetPassword() {
    const loginId = localStorage.getItem("userEmail"); // 이메일을 로컬 스토리지에서 가져옴
    const otp = localStorage.getItem("otp"); // 이전에 인증된 OTP를 가져옴
    const newPassword = document.getElementById("newPasswordInput").value;

    if (!otp) {
        alert("OTP가 검증되지 않았습니다.");
        return;
    }

    if (!newPassword) {
        alert("새 비밀번호를 입력해 주세요.");
        return;
    }

    fetch('/password-reset/reset', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ loginId: loginId, otp: otp, newPassword: newPassword })
    })
        .then(response => response.json())
        .then(result => {
            alert(result.message);
            if (result.status === "success") {
                localStorage.removeItem("otp"); // OTP 삭제
                window.location.href = '/security-login/login';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("서버 오류가 발생했습니다.");
        });
}


