const resetHour = 5; // 리셋할 시간 (24시간 형식)
const resetMinute = 0;

document.addEventListener("DOMContentLoaded", function () {
  // 사용자 고유 ID 설정 (이 값을 실제 사용자 ID로 교체해야 합니다)

  console.log("User ID: ", userId);  // 이 값을 사용자가 로그인할 때 올바르게 받는지 확인

  // 초기 변수 및 요소 설정
  const timerElement = document.querySelector(".timer");
  const timerCountDown = document.getElementById("timerCountDown");
  const progressBar = document.getElementById("studyProgress");
  const overlay = document.getElementById("popupOverlay");
  const popup = document.getElementById("popup");

  // 시간 설정 모달 관련 요소 설정
  const modal = document.getElementById("setTimeModal");
  const openModalButton = document.querySelector(".set-time-container button");
  const closeModalButton = document.getElementsByClassName("close")[0];
  const saveButton = document.getElementById("saveTimeButton");
  const hoursInput = document.getElementById("hoursInput");
  const minutesInput = document.getElementById("minutesInput");

  // 모달을 열 때 현재 날짜 업데이트
  function updateCurrentDate() {
    const now = new Date();
    const year = now.getFullYear().toString().slice(-2);
    const month = (now.getMonth() + 1).toString().padStart(2, "0");
    const date = now.getDate().toString().padStart(2, "0");
    const days = ["일", "월", "화", "수", "목", "금", "토"];
    const day = days[now.getDay()];

    const dateString = `${year}년 ${month}월 ${date}일 ${day}요일`;
    document.getElementById("currentDate").textContent = dateString;
  }

  // 모달 열기 버튼 클릭 이벤트
  openModalButton.onclick = function () {
    modal.style.display = "block";
    updateCurrentDate();
  };

  // 모달 닫기 버튼 클릭 이벤트
  closeModalButton.onclick = function () {
    modal.style.display = "none";
  };

  // 모달 외부 클릭 시 모달 닫기
  window.onclick = function (event) {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  };

  // 시간 업데이트 함수
  function updateTime(input, increment, max, step) {
    let value = parseInt(input.value);
    if (increment) {
      value = (value + step) % (max + step);
    } else {
      value = (value - step + max + step) % (max + step);
    }
    input.value = value.toString().padStart(2, "0");
  }

  // 시간 증감 버튼 클릭 이벤트
  document.querySelectorAll(".time-unit button").forEach((button) => {
    button.addEventListener("click", function () {
      const input = this.parentNode.querySelector("input");
      const isIncrement = this.classList.contains("increment");
      const isHours = input.id === "hoursInput";
      updateTime(input, isIncrement, isHours ? 23 : 55, isHours ? 1 : 5);
    });
  });

  // 타이머 관련 변수 및 함수
  let timerInterval;
  let timerRunning = false;
  let startTime;
  let elapsedTime = 0;

  // Thymeleaf에서 전달된 시간 정보 가져오기
  const hours = parseInt(document.getElementById('hours').textContent, 10);
  const minutes = parseInt(document.getElementById('minutes').textContent, 10);
  const targetTime = (hours * 3600) + (minutes * 60); // 목표 시간을 초로 변환

  function loadElapsedTimeFromDB() {
    const storedElapsedTime = parseInt(localStorage.getItem(`${userId}_elapsedTime`) || '0', 10);

    fetch(`/timer/load?userId=${userId}`)  // 서버에서 경과 시간 로드
        .then(response => response.json())
        .then(data => {
          let serverElapsedTime = 0;

          // 서버에서 받은 elapsedTime이 숫자 형식인지 확인 후 사용
          if (typeof data.elapsedTime === 'string' && data.elapsedTime.includes(":")) {
            serverElapsedTime = convertElapsedTimeToSeconds(data.elapsedTime);
          } else if (typeof data.elapsedTime === 'number') {
            serverElapsedTime = data.elapsedTime;
          }

          elapsedTime = Math.max(serverElapsedTime, storedElapsedTime || 0); // 클라이언트와 서버 중 더 큰 값을 사용
          updateTimerDisplay(elapsedTime);
          updateCountdownDisplay(targetTime - elapsedTime);
          updateProgressBar(elapsedTime);
        })
        .catch(error => {
          console.error('Failed to load elapsed time:', error);
          elapsedTime = storedElapsedTime || 0; // 서버 호출 실패 시 클라이언트 저장 값 사용
          updateTimerDisplay(elapsedTime);
          updateCountdownDisplay(targetTime - elapsedTime);
          updateProgressBar(elapsedTime);
        });
  }

  function convertElapsedTimeToSeconds(elapsedTime) {
    const [hours, minutes, seconds] = elapsedTime.split(":").map(Number);
    return (hours * 3600) + (minutes * 60) + seconds;
  }

  function startTimer() {
    fetch('/timer/start', { method: 'POST' })
        .then(response => {
          if (response.ok) {
            console.log('Timer started successfully');
            timerRunning = true;
            startTime = Date.now();
            localStorage.setItem(`${userId}_startTime`, startTime); // 사용자별 시작 시간 저장
            timerInterval = setInterval(updateTimer, 1000);
            startBtn.disabled = true;
            pauseBtn.disabled = false;
            stopBtn.disabled = false;
          } else {
            console.error('Failed to start timer');
          }
        });
  }

  function pauseTimer() {
    if (timerRunning) {
      clearInterval(timerInterval);
      timerRunning = false;
      elapsedTime += Math.floor((Date.now() - startTime) / 1000);
      localStorage.setItem(`${userId}_elapsedTime`, elapsedTime); // 사용자별 경과 시간 저장
      localStorage.removeItem(`${userId}_startTime`);
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
            startTime = null;
            localStorage.setItem(`${userId}_elapsedTime`, elapsedTime);  // 사용자별 경과 시간 저장
            localStorage.removeItem(`${userId}_startTime`);
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

  function updateTimer() {
    const currentTime = Date.now();
    const runningTime = Math.floor((currentTime - startTime) / 1000);
    const totalElapsedTime = elapsedTime + runningTime;
    const remainingTime = Math.max(targetTime - totalElapsedTime, 0);

    updateTimerDisplay(totalElapsedTime);
    updateCountdownDisplay(remainingTime);
    updateProgressBar(totalElapsedTime);

    localStorage.setItem(`${userId}_elapsedTime`, totalElapsedTime); // 사용자별 경과 시간 저장

    if (isResetTime()) {
      resetTimer();
    }
  }

  function updateTimerDisplay(totalElapsedTime) {
    if (isNaN(totalElapsedTime)) {
      totalElapsedTime = 0; // NaN을 방지하기 위한 기본값 설정
    }

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
    localStorage.removeItem(`${userId}_elapsedTime`);
    localStorage.removeItem(`${userId}_startTime`);
    scheduleNextReset();
  }

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
