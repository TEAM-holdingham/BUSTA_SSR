document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("setTimeModal");
    const btn = document.querySelector(".set-time-container button");
    const span = document.getElementsByClassName("close")[0];
    const saveButton = document.getElementById("saveTimeButton");
    const hoursInput = document.getElementById("hoursInput");
    const minutesInput = document.getElementById("minutesInput");

    // 현재 날짜 표시 함수
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

    // 모달을 열 때 현재 날짜 업데이트
    btn.onclick = function () {
        modal.style.display = "block";
        updateCurrentDate();
    };

    // 닫기 버튼 기능
    span.onclick = function () {
        modal.style.display = "none";
    };

    // 모달 외부 클릭 시에도 닫아줌
    window.onclick = function (event) {
        if (event.target == modal) {
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

    // 시간 증감 버튼 클릭할때
    document.querySelectorAll(".time-unit button").forEach((button) => {
        button.addEventListener("click", function () {
            const input = this.parentNode.querySelector("input");
            const isIncrement = this.classList.contains("increment");
            const isHours = input.id === "hoursInput";
            updateTime(input, isIncrement, isHours ? 23 : 55, isHours ? 1 : 5);
        });
    });

    // 저장 버튼 클릭 이벤트는 더 이상 필요하지 않습니다.
    // 폼 서브밋이 자동으로 처리됩니다.
});
