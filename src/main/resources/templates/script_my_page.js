window.onload = function() {
    openNav(); // 초기에 사이드바가 열려있도록 설정
}

function toggleNav() {
    const sidenav = document.getElementById("mySidenav");
    const isOpen = sidenav.style.width === "250px";

    if (isOpen) {
        closeNav();
    } else {
        openNav();
    }
}

function openNav() {
    document.getElementById("mySidenav").style.width = "250px";
    document.getElementById("main").style.marginLeft = "250px";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0px";
    document.getElementById("main").style.marginLeft = "50px";
}
















/* 마이페이지 */


function toggleEdit(id) {
    const input = document.getElementById(id);
    input.readOnly = !input.readOnly;
    if (!input.readOnly) {
        input.style.border = "1px solid #000";
    } else {
        input.style.border = "1px solid #ccc";
    }
}
