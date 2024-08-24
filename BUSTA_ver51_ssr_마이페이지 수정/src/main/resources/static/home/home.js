document.addEventListener("DOMContentLoaded", function () {
  // 사이드바 상태를 기본적으로 열려 있도록 설정
  const mainElement = document.getElementById("main");



  const startBtn = document.getElementById("startBtn");
  const pauseBtn = document.getElementById("pauseBtn");
  const stopBtn = document.getElementById("stopBtn");
  const timerElement = document.querySelector(".timer");
  const timerCountDown = document.getElementById("timerCountDown");
  const progressBar = document.getElementById("studyProgress");
  const overlay = document.getElementById("popupOverlay");
  const popup = document.getElementById("popup");

  let timerInterval;
  let timerRunning = false;
  let startTime;
  let elapsedTime = 0;
  const resetHour = 16; // 리셋할 시간 (24시간 형식)
  const resetMinute = 31;
  const targetTime = 1 * 60 * 60; // 1시간을 초로 변환

  window.onload = function () {
    const storedTime = localStorage.getItem('elapsedTime');
    const storedStartTime = localStorage.getItem('startTime');

    if (storedTime) {
      elapsedTime = parseInt(storedTime, 10);
    }

    if (storedStartTime) {
      // 타이머가 멈춘 상태에서 새로고침된 경우
      const savedStartTime = parseInt(storedStartTime, 10);
      const currentTime = Date.now();
      const timeSinceLastStart = Math.floor((currentTime - savedStartTime) / 1000);
      elapsedTime += timeSinceLastStart;

      startTimer(true); // 자동으로 타이머 시작
    } else {
      updateTimerDisplay(elapsedTime);
      updateCountdownDisplay(targetTime - elapsedTime);
      updateProgressBar(elapsedTime);
    }

    scheduleNextReset(); // 다음 리셋 시간 설정
  };

  function startTimer(autoStart = false) {
    fetch('/timer/start', { method: 'POST' })
        .then(response => {
          if (response.ok) {
            console.log('Timer started successfully');
            timerRunning = true;
            startTime = Date.now(); // 타이머 시작 시간 설정
            localStorage.setItem('startTime', startTime); // 시작 시간 저장
            timerInterval = setInterval(updateTimer, 1000);

            // autoStart와 상관없이 버튼 상태를 업데이트
            startBtn.disabled = true;
            pauseBtn.disabled = false;
            stopBtn.disabled = false;

            scheduleNextReset(); // 다음 리셋 시간 설정
          } else {
            console.error('Failed to start timer');
          }
        });
  }


  function pauseTimer() {
    if (timerRunning) {
      clearInterval(timerInterval);
      timerRunning = false;
      elapsedTime += Math.floor((Date.now() - startTime) / 1000); // 경과 시간을 초 단위로 추가
      localStorage.setItem('elapsedTime', elapsedTime); // 경과 시간 저장
      localStorage.removeItem('startTime'); // 타이머 멈춘 상태에서의 시작 시간 삭제
      showPopup();
      startBtn.disabled = false;
      pauseBtn.disabled = true;
      stopBtn.disabled = false;
    }
  }

  function stopTimer() {
    fetch('/timer/stop', { method: 'POST' })
        .then(response => {
          if (response.ok) {
            console.log('Timer stopped successfully');
            clearInterval(timerInterval);
            timerRunning = false;
            elapsedTime = 0;
            timerElement.textContent = "00:00:00";
            timerCountDown.textContent = "목적지까지 01시간 00분 00초 남았습니다.";
            progressBar.style.width = "0%";
            localStorage.removeItem('elapsedTime'); // 타이머 중지 시 저장된 시간 초기화
            localStorage.removeItem('startTime'); // 타이머 시작 시간 삭제
            startBtn.disabled = false;
            pauseBtn.disabled = true;
            stopBtn.disabled = true;
          } else {
            console.error('Failed to stop timer');
          }
        });
  }

  function showPopup() {
    overlay.style.display = "block";
    popup.style.display = "block";
  }

  function hidePopup() {
    overlay.style.display = "none";
    popup.style.display = "none";
  }

  function submitActivity() {
    const activityDescription = document.getElementById("activityDescription").value;
    fetch('/timer/pause', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ activityDescription })
    })
        .then(response => {
          if (response.ok) {
            console.log('Activity submitted successfully');
            hidePopup();
            startBtn.disabled = false;
            pauseBtn.disabled = true;
            stopBtn.disabled = false;
          } else {
            console.error('Failed to submit activity');
          }
        });
  }

  function updateTimer() {
    const currentTime = Date.now();
    const runningTime = Math.floor((currentTime - startTime) / 1000); // 타이머 재개 후 경과 시간 계산
    const totalElapsedTime = elapsedTime + runningTime; // 총 경과 시간
    const remainingTime = Math.max(targetTime - totalElapsedTime, 0);

    updateTimerDisplay(totalElapsedTime);
    updateCountdownDisplay(remainingTime);
    updateProgressBar(totalElapsedTime);

    localStorage.setItem('elapsedTime', totalElapsedTime); // 경과 시간 저장

    // 매 초마다 리셋 시간 확인
    if (isResetTime()) {
      resetTimer();
    }
  }

  function updateTimerDisplay(totalElapsedTime) {
    const hours = Math.floor(totalElapsedTime / 3600);
    const minutes = Math.floor((totalElapsedTime % 3600) / 60);
    const seconds = totalElapsedTime % 60;

    timerElement.textContent = `${padZero(hours)}:${padZero(minutes)}:${padZero(seconds)}`;
  }

  function updateCountdownDisplay(remainingTime) {
    const remainingHours = Math.floor(remainingTime / 3600);
    const remainingMinutes = Math.floor((remainingTime % 3600) / 60);
    const remainingSeconds = remainingTime % 60;

    timerCountDown.textContent = `목적지까지 ${padZero(remainingHours)}시간 ${padZero(remainingMinutes)}분 ${padZero(remainingSeconds)}초 남았습니다.`;
  }

  function updateProgressBar(totalElapsedTime) {
    const progress = (totalElapsedTime / targetTime) * 100;
    progressBar.style.width = `${progress}%`;
  }

  function padZero(num) {
    return num.toString().padStart(2, "0");
  }

  // 다음 리셋 시간 예약
  function scheduleNextReset() {
    let now = new Date();
    let resetTime = new Date();
    resetTime.setHours(resetHour, resetMinute, 0, 0);

    if (resetTime <= now) {
      resetTime.setDate(resetTime.getDate() + 1); // 리셋 시간이 이미 지난 경우 다음 날로 설정
    }

    let timeUntilReset = resetTime - now;
    setTimeout(resetTimer, timeUntilReset);
  }

  // 리셋 시간이 되었을 때 타이머 리셋
  function resetTimer() {
    fetch('/timer/reset', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ elapsedTime: Math.floor(elapsedTime / 1000) }) // 경과 시간 초 단위로 전송
    }).then(response => {
      if (response.ok) {
        console.log('Timer reset successfully and elapsed time saved');
      } else {
        console.error('Failed to reset timer');
      }
    });

    clearInterval(timerInterval);
    timerRunning = false;
    elapsedTime = 0;
    updateTimerDisplay(elapsedTime);
    updateCountdownDisplay(targetTime);
    updateProgressBar(0);
    localStorage.removeItem('elapsedTime');
    localStorage.removeItem('startTime');
    scheduleNextReset();
  }

  // 현재 시간이 리셋 시간인지 확인
  function isResetTime() {
    let now = new Date();
    return now.getHours() === resetHour && now.getMinutes() === resetMinute && now.getSeconds() === 0;
  }

  startBtn.addEventListener("click", startTimer);
  pauseBtn.addEventListener("click", pauseTimer);
  stopBtn.addEventListener("click", stopTimer);

  document.getElementById("submitActivityBtn").addEventListener("click", submitActivity);
});
