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
    document.querySelector(".announcement_main").style.marginLeft = "250px";
    document.querySelector(".announcement_main").style.width = "calc(100% - 250px)";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0px";
    document.querySelector(".announcement_main").style.marginLeft = "50px";
    document.querySelector(".announcement_main").style.width = "calc(100% - 0px)";
}

//여기서부터 accnouncement_in_script

