document.addEventListener("DOMContentLoaded", function () {
  // 초기 변수 및 요소 설정
  const timerElement = document.querySelector(".timer");
  const timerCountDown = document.getElementById("timerCountDown");
  const progressBar = document.getElementById("studyProgress");
  const overlay = document.getElementById("popupOverlay");
  const popup = document.getElementById("popup");

  const startBtn = document.getElementById("startBtn");
  const pauseBtn = document.getElementById("pauseBtn");
  const stopBtn = document.getElementById("stopBtn");

  let timerInterval;
  let timerRunning = false;
  let startTime;
  let elapsedTime = 0;

  // Thymeleaf에서 전달된 시간 정보 가져오기
  const hours = parseInt(document.getElementById('hours').textContent, 10);
  const minutes = parseInt(document.getElementById('minutes').textContent, 10);
  const targetTime = (hours * 3600) + (minutes * 60); // 목표 시간을 초로 변환

  // 서버에서 기록된 경과 시간을 불러오는 함수
  function loadElapsedTimeFromDB() {
    fetch('/timer/load')  // 경과 시간을 로드하는 서버 엔드포인트 호출
        .then(response => response.json())
        .then(data => {
          elapsedTime = data.elapsedTime || 0;
          updateTimerDisplay(elapsedTime);
          updateCountdownDisplay(targetTime - elapsedTime);
          updateProgressBar(elapsedTime);
        })
        .catch(error => {
          console.error('Failed to load elapsed time:', error);
        });
  }

  // 타이머 시작 함수
  function startTimer() {
    fetch('/timer/start', { method: 'POST' })
        .then(response => {
          if (response.ok) {
            console.log('Timer started successfully');
            timerRunning = true;
            startTime = Date.now();
            timerInterval = setInterval(updateTimer, 1000);
            startBtn.disabled = true;
            pauseBtn.disabled = false;
            stopBtn.disabled = false;
          } else {
            console.error('Failed to start timer');
          }
        });
  }

  // 타이머 일시 정지 함수
  function pauseTimer() {
    if (timerRunning) {
      clearInterval(timerInterval);
      timerRunning = false;
      elapsedTime += Math.floor((Date.now() - startTime) / 1000);
      localStorage.setItem('elapsedTime', elapsedTime);
      localStorage.removeItem('startTime');
      showPopup();
      startBtn.disabled = false;
      pauseBtn.disabled = true;
      stopBtn.disabled = false;
    }
  }

  // 타이머 중지 함수
  function stopTimer() {
    fetch('/timer/stop', { method: 'POST' })
        .then(response => {
          if (response.ok) {
            console.log('Timer stopped successfully');
            clearInterval(timerInterval);
            timerRunning = false;
            startTime = null;
            localStorage.setItem('elapsedTime', elapsedTime);  // Save the elapsed time in local storage
            localStorage.removeItem('startTime');
            startBtn.disabled = false;
            pauseBtn.disabled = true;
            stopBtn.disabled = true;
          } else {
            console.error('Failed to stop timer');
          }
        });
  }

  // 팝업 표시 함수
  function showPopup() {
    overlay.style.display = "block";
    popup.style.display = "block";
  }

  // 팝업 숨기기 함수
  function hidePopup() {
    overlay.style.display = "none";
    popup.style.display = "none";
  }

  // 활동 제출 함수
  function submitActivity() {
    const activityDescription = document.getElementById("activityDescription").value;
    fetch('/timer/pause', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
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

  // 타이머 업데이트 함수
  function updateTimer() {
    const currentTime = Date.now();
    const runningTime = Math.floor((currentTime - startTime) / 1000);
    const totalElapsedTime = elapsedTime + runningTime;
    const remainingTime = Math.max(targetTime - totalElapsedTime, 0);

    updateTimerDisplay(totalElapsedTime);
    updateCountdownDisplay(remainingTime);
    updateProgressBar(totalElapsedTime);

    localStorage.setItem('elapsedTime', totalElapsedTime);

    if (isResetTime()) {
      resetTimer();
    }
  }

  // 타이머 표시 업데이트 함수
  function updateTimerDisplay(totalElapsedTime) {
    const hours = Math.floor(totalElapsedTime / 3600);
    const minutes = Math.floor((totalElapsedTime % 3600) / 60);
    const seconds = totalElapsedTime % 60;

    timerElement.textContent = `${padZero(hours)}:${padZero(minutes)}:${padZero(seconds)}`;
  }

  // 카운트다운 표시 업데이트 함수
  function updateCountdownDisplay(remainingTime) {
    const remainingHours = Math.floor(remainingTime / 3600);
    const remainingMinutes = Math.floor((remainingTime % 3600) / 60);
    const remainingSeconds = remainingTime % 60;

    timerCountDown.textContent = `목적지까지 ${padZero(remainingHours)}시간 ${padZero(remainingMinutes)}분 ${padZero(remainingSeconds)}초 남았습니다.`;
  }

  // 진행 바 업데이트 함수
  function updateProgressBar(totalElapsedTime) {
    const progress = (totalElapsedTime / targetTime) * 100;
    progressBar.style.width = `${progress}%`;
  }

  // 2자리 숫자로 포맷팅하는 함수
  function padZero(num) {
    return num.toString().padStart(2, "0");
  }

  // 다음 리셋 시간 예약
  function scheduleNextReset() {
    let now = new Date();
    let resetTime = new Date();
    resetTime.setHours(resetHour, resetMinute, 0, 0);

    if (resetTime <= now) {
      resetTime.setDate(resetTime.getDate() + 1);
    }

    let timeUntilReset = resetTime - now;
    setTimeout(resetTimer, timeUntilReset);
  }

  // 타이머 리셋 함수
  function resetTimer() {
    fetch('/timer/reset', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ elapsedTime: Math.floor(elapsedTime / 1000) })
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

  // 리셋 시간 확인 함수
  function isResetTime() {
    let now = new Date();
    return now.getHours() === resetHour && now.getMinutes() === resetMinute && now.getSeconds() === 0;
  }

  startBtn.addEventListener("click", startTimer);
  pauseBtn.addEventListener("click", pauseTimer);
  stopBtn.addEventListener("click", stopTimer);
  document.getElementById("submitActivityBtn").addEventListener("click", submitActivity);

  // 페이지 로드 시 서버에서 경과 시간 불러오기
  loadElapsedTimeFromDB();
});
