let seconds = 0;
let minutes = 0;
let hours = 0;
let spanHours = document.getElementById('hours');
let spanMinutes = document.getElementById('minutes');
let spanSeconds = document.getElementById('seconds');
let buttonStart = document.getElementById('button-start');
let buttonStop = document.getElementById('button-stop');
let buttonReset = document.getElementById('button-reset');
let interval; // 1/100초씩 자동 실행 되는 곳에 사용할 변수 

buttonStart.onclick = function(){
    if(!interval){
        interval = setInterval(startTimer, 1000); // 1초마다 실행
    }
}

buttonStop.onclick = function(){
    clearInterval(interval); // 타이머 멈춤
    interval = null;
}

buttonReset.onclick = function(){
    clearInterval(interval); // 타이머 멈춤
    interval = null;
    seconds = 0;
    minutes = 0;
    hours = 0;
    spanSeconds.innerText = '00';
    spanMinutes.innerText = '00';
    spanHours.innerText = '00';
}

function startTimer(){  // 시간 계산하는 함수
    seconds++; // 1증가 // seconds = seconds + 1, 1초 상승
    if(seconds >= 60) {
        minutes++;
        seconds = 0;
        if(munutes >= 60){
            hours++;
            minutes = 0;
        }
    }

    let displaySeconds = seconds < 10 ? '0' + seconds : seconds;
    let displayMinutes = minutes < 10 ? '0' + minutes : minutes;
    let displayHours = hours < 10 ? '0' + hours : hours;

    spanSeconds.innerText = displaySeconds;
    spanMinutes.innerText = displayMinutes;
    spanHours.innerText = displayHours;
}