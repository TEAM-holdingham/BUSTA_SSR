// 토큰 검증 함수
function verifyEmailToken() {
    const token = document.querySelector(".input_token input").value;

    if (!token) {
        alert("토큰을 입력해 주세요.");
        return;
    }

    fetch('/password-reset/verify-token?token=' + encodeURIComponent(token), {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(response => response.text())
        .then(result => {
            if (result === "Valid token") {
                alert("토큰이 유효합니다. 비밀번호를 재설정해 주세요.");
                // 토큰을 로컬 스토리지에 저장하거나 변수에 저장하여 이후 비밀번호 재설정에 사용합니다.
                localStorage.setItem("resetToken", token);
                document.querySelector(".reset_password").style.display = 'block'; // 비밀번호 입력 필드 표시
            } else {
                alert("유효하지 않거나 만료된 토큰입니다.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("서버 오류가 발생했습니다.");
        });
}

// 비밀번호 재설정 함수
function resetPassword() {
    const token = localStorage.getItem("resetToken"); // 저장된 토큰을 가져옴
    const newPassword = document.getElementById("newPasswordInput").value;

    if (!token) {
        alert("토큰이 유효하지 않습니다.");
        return;
    }

    if (!newPassword) {
        alert("새 비밀번호를 입력해 주세요.");
        return;
    }

    fetch('/password-reset/reset?token=' + encodeURIComponent(token) + '&newPassword=' + encodeURIComponent(newPassword), {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
        .then(response => response.text())
        .then(result => {
            alert(result);
            if (result === "Password successfully reset.") {
                // 비밀번호 재설정 성공 후 리디렉션 또는 다른 처리
                window.location.href = '/security-login/login';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("서버 오류가 발생했습니다.");
        });
}
