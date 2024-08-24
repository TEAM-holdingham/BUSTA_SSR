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

//여기서부터 settings_main_script

document.addEventListener('DOMContentLoaded', function() {
    const announcements = document.querySelectorAll('.announcement');
    
    announcements.forEach(announcement => {
        announcement.addEventListener('click', function(e) {
            e.preventDefault();
            const detailUrl = this.getAttribute('href');
            // 여기에 공지사항 ID나 다른 식별자를 URL에 추가할 수 있습니다.
            window.location.href = detailUrl;
        });
    });
});