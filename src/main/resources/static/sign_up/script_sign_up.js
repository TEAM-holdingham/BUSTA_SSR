function checkAll(source) {
  var checkboxes = document.querySelectorAll('input[type="checkbox"]');
  checkboxes.forEach(function (checkbox) {
    if (checkbox.id !== "checkbox1") {
      checkbox.checked = source.checked;
    }
  });
}

function goBack() {
  window.history.back();
}

document.addEventListener("DOMContentLoaded", function () {
  const form = document.querySelector("form");
  const emailInput = document.getElementById("email");
  const emailDomain = document.getElementById("emailDomain");
  const nicknameInput = document.getElementById("nickname");
  const passwordInput = document.getElementById("password");
  const passwordCheckInput = document.getElementById("passwordCheck");
  const checkNicknameBtn = document.getElementById("checkNicknameBtn");
  const phoneNumberInput = document.getElementById("phoneNumber");
  const carrierInput = document.getElementById("carrierDomain");
  const checkAuthenticationBtn = document.getElementById("checkAuthentication");
  const birthDateInput = document.getElementById("birthDate");
  const sexDomainInput = document.getElementById("sexDomain");

  let isNicknameChecked = false;
  let isPhoneAuthenticated = false;

  // 닉네임 입력 시 중복 확인 버튼 활성화
  nicknameInput.addEventListener("input", function () {
    checkNicknameBtn.disabled = nicknameInput.value.trim() === "";
    isNicknameChecked = false; // 닉네임이 변경되면 중복 확인 상태 초기화
  });

  // 중복 확인 버튼 클릭 이벤트
  checkNicknameBtn.addEventListener("click", function (event) {
    event.preventDefault();
    // 여기에 실제 중복 확인 로직을 구현해야 합니다.
    // 지금은 예시로 항상 사용 가능하다고 가정합니다.
    alert("사용할 수 있는 닉네임입니다.");
    isNicknameChecked = true;
  });

  // 인증 버튼 클릭 이벤트
  checkAuthenticationBtn.addEventListener("click", function (event) {
    event.preventDefault();
    // 여기에 실제 인증 로직을 구현해야 합니다.
    // 지금은 예시로 항상 인증 성공으로 가정합니다.
    alert("인증되었습니다.");
    isPhoneAuthenticated = true;
  });

  // 비밀번호 보이기/숨기기 토글
  function togglePasswordVisibility(inputElement, buttonElement) {
    buttonElement.addEventListener("click", function () {
      const type = inputElement.type === "password" ? "text" : "password";
      inputElement.type = type;
      const icon = buttonElement.querySelector("i");
      icon.classList.toggle("fa-eye");
      icon.classList.toggle("fa-eye-slash");
    });
  }

  togglePasswordVisibility(
    passwordInput,
    document.getElementById("showPassword")
  );
  togglePasswordVisibility(
    passwordCheckInput,
    document.getElementById("showPasswordCheck")
  );

  // 폼 제출 처리
  form.addEventListener("submit", function (event) {
    event.preventDefault();

    // 이메일 주소 조합
    const fullEmail = emailInput.value + "@" + emailDomain.value;

    // 모든 필드가 채워져 있는지 확인
    if (
      !emailInput.value ||
      !nicknameInput.value ||
      !passwordInput.value ||
      !passwordCheckInput.value ||
      !phoneNumberInput.value ||
      !birthDateInput.value ||
      !sexDomainInput.value
    ) {
      alert("모든 필드를 채워주세요.");
      return;
    }

    // 닉네임 중복 확인이 완료되었는지 확인
    if (!isNicknameChecked) {
      alert("닉네임 중복 확인을 해주세요.");
      return;
    }

    // 전화번호 인증이 완료되었는지 확인
    if (!isPhoneAuthenticated) {
      alert("전화번호 인증을 완료해주세요.");
      return;
    }

    // 비밀번호 일치 여부 확인
    if (passwordInput.value !== passwordCheckInput.value) {
      alert("비밀번호가 일치하지 않습니다. 다시 확인해 주세요.");
      return;
    }

    // 모든 검사를 통과하면 로그인 페이지로 이동
    window.location.href = "/security-login/login";
  });
});
