document.addEventListener("DOMContentLoaded", function () {
  const startBtn = document.getElementById("startBtn");
  const pauseBtn = document.getElementById("pauseBtn");
  const stopBtn = document.getElementById("stopBtn");
  const timerElement = document.getElementById("timer");
  const timerCountDown = document.getElementById("timerCountDown");
  const progressBar = document.getElementById("studyProgress");

  let isStudying = false;
  let isPaused = false;
  let startTime;
  let totalStudyTime = 0;
  let pauseStartTime;
  let timerInterval;
  const targetTime = 1 * 60 * 60; // 1시간을 초로 변환

  function updateTimer() {
    const currentTime = Date.now();
    const elapsedTime =
      Math.floor((currentTime - startTime) / 1000) + totalStudyTime;
    const remainingTime = Math.max(targetTime - elapsedTime, 0);

    const hours = Math.floor(elapsedTime / 3600);
    const minutes = Math.floor((elapsedTime % 3600) / 60);
    const seconds = elapsedTime % 60;

    timerElement.textContent = `${padZero(hours)}:${padZero(minutes)}:${padZero(
      seconds
    )}`;

    const remainingHours = Math.floor(remainingTime / 3600);
    const remainingMinutes = Math.floor((remainingTime % 3600) / 60);
    const remainingSeconds = remainingTime % 60;
    timerCountDown.textContent = `목적지까지 ${padZero(
      remainingHours
    )}시간 ${padZero(remainingMinutes)}분 ${padZero(
      remainingSeconds
    )}초 남았습니다.`;

    const progress = (elapsedTime / targetTime) * 100;
    progressBar.style.width = `${progress}%`;
  }

  function padZero(num) {
    return num.toString().padStart(2, "0");
  }

  function startTimer() {
    isStudying = true;
    isPaused = false;
    startTime = Date.now();
    timerInterval = setInterval(updateTimer, 1000);
    startBtn.disabled = true;
    pauseBtn.disabled = false;
    stopBtn.disabled = false;
  }

  startBtn.addEventListener("click", startTimer);

  pauseBtn.addEventListener("click", () => {
    if (isStudying && !isPaused) {
      isPaused = true;
      clearInterval(timerInterval);
      pauseStartTime = Date.now();
      totalStudyTime += Math.floor((pauseStartTime - startTime) / 1000);
      startBtn.disabled = false;
      pauseBtn.disabled = true;
    }
  });

  stopBtn.addEventListener("click", () => {
    isStudying = false;
    isPaused = false;
    clearInterval(timerInterval);
    totalStudyTime = 0;
    timerElement.textContent = "00:00:00";
    timerCountDown.textContent = "목적지까지 01시간 00분 00초 남았습니다.";
    progressBar.style.width = "0%";
    startBtn.disabled = false;
    pauseBtn.disabled = true;
    stopBtn.disabled = true;
  });

  const main = document.getElementById("main");
  const bus = document.getElementById("bus");
  const timerContainer = document.getElementById("timerContainer");
  const closeBtn = document.querySelector(".closebtn");
  const openBtn = document.querySelector(".openbtn");

  function handleNavToggle() {
    main.classList.toggle("menu-closed");
    if (main.classList.contains("menu-closed")) {
      bus.style.opacity = "0";
      bus.style.visibility = "hidden";
    } else {
      bus.style.opacity = "1";
      bus.style.visibility = "visible";
    }
  }

  closeBtn.addEventListener("click", handleNavToggle);
  openBtn.addEventListener("click", handleNavToggle);

  // 초기 상태 설정
  timerElement.textContent = "00:00:00";
  timerCountDown.textContent = "목적지까지 01시간 00분 00초 남았습니다.";
});
