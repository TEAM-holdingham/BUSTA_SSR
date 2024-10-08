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













document.addEventListener('DOMContentLoaded', function() {
    const addTaskButton = document.querySelector('.add-task');
    
    addTaskButton.addEventListener('click', function() {
        const taskList = document.querySelector('.task-list');
        const newTask = document.createElement('li');
        newTask.className = 'task pending';
        newTask.textContent = '새로운 목표 추가';
        taskList.appendChild(newTask);
    });
});
