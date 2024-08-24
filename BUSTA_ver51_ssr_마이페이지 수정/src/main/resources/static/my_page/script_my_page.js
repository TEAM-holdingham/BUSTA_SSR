window.onload = function () {
  if (document.getElementById("mySidenav")) {
    openNav();
  }
};

function logout() {
  localStorage.removeItem("isLoggedIn");
  localStorage.removeItem("userEmail");
  window.location.href = "../login/login.html";
}

// 사이드바 관련 함수들
function toggleNav() {
  const sidenav = document.getElementById("mySidenav");
  if (sidenav) {
    const isOpen = sidenav.style.width === "250px";
    if (isOpen) {
      closeNav();
    } else {
      openNav();
    }
  }
}

function openNav() {
  const sidenav = document.getElementById("mySidenav");
  const main = document.getElementById("main");
  if (sidenav) sidenav.style.width = "250px";
  if (main) main.style.marginLeft = "250px";
}

function closeNav() {
  const sidenav = document.getElementById("mySidenav");
  const main = document.getElementById("main");
  if (sidenav) sidenav.style.width = "0px";
  if (main) main.style.marginLeft = "50px";
}

// 프로필 정보 수정 관련 함수
function toggleEdit(id) {
  const input = document.getElementById(id);
  if (input) {
    input.readOnly = !input.readOnly;
    input.style.border = input.readOnly ? "1px solid #ccc" : "1px solid #000";

    if (input.readOnly) {
      // 수정이 완료된 경우 폼을 제출
      document.getElementById('profileForm').submit();
    } else {
      input.focus();
    }
  }
}

function previewProfilePicture(event) {
  const reader = new FileReader();
  reader.onload = function() {
    const imgElement = document.getElementById('profile-picture');
    imgElement.src = reader.result;
  };
  // event 객체에서 파일을 제대로 가져오고 있는지 확인
  if (event && event.target && event.target.files) {
    reader.readAsDataURL(event.target.files[0]);
  } else {
    console.error("파일을 읽어올 수 없습니다.");
  }
}

