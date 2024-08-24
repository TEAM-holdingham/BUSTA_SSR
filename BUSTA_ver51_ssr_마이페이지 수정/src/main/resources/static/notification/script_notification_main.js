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
    document.querySelector(".notification_main").style.marginLeft = "250px";
    document.querySelector(".notification_main").style.width = "calc(100% - 250px)";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0px";
    document.querySelector(".notification_main").style.marginLeft = "50px";
    document.querySelector(".notification_main").style.width = "calc(100% - 0px)";
}

//여기서부터 notification_script

// 알림 데이터 (실제로는 서버에서 가져와야함.)
const notifications = [
    { date: '2024.03.11 (월)', time: '15:15:00', content: '시애기님이 친구 신청을 요청했습니다.' },
    { date: '2024.03.11 (월)', time: '13:30:30', content: '공지사항 쓸 말이 없어요...' },
    { date: '2024.03.11 (월)', time: '11:15:00', content: '채욘님이 친구 신청을 요청했습니다.' },
    { date: '2024.03.11 (월)', time: '10:30:00', content: '허찐따님이 친구 신청을 신청을 요청했습니다.' },
    { date: '2024.03.10 (일)', time: '18:25:05', content: '계정 도용과 관련해 안내드립니다.' },
    { date: '2024.03.10 (일)', time: '12:10:30', content: '공지사항 쓸 말이 없어요...' },
    { date: '2024.03.10 (일)', time: '11:15:00', content: '예언님이 친구 신청을 요청했습니다.' },
];

// 알림 목록을 동적으로 생성하는 함수
function renderNotifications() {
    const notificationList = document.querySelector('.notification-list');
    notificationList.innerHTML = '';

    let currentDate = '';
    let currentDateBox = null;

    notifications.forEach(notification => {
        if (notification.date !== currentDate) {
            currentDate = notification.date;
            currentDateBox = document.createElement('div');
            currentDateBox.className = 'notification-date-box';
            
            const dateElement = document.createElement('div');
            dateElement.className = 'notification-date';
            dateElement.textContent = currentDate;
            currentDateBox.appendChild(dateElement);
            
            notificationList.appendChild(currentDateBox);
        }

        const notificationItem = document.createElement('div');
        notificationItem.className = 'notification-item';
        notificationItem.innerHTML = `
            <span class="notification-time">${notification.time}</span>
            <span class="notification-content">${notification.content}</span>
            ${notification.content.includes('친구 신청') ? 
                `<button class="accept-btn">수락</button>
                 <button class="reject-btn">거절</button>` : ''}
        `;
        currentDateBox.appendChild(notificationItem);
    });
}

// 수락 및 거절 버튼 이벤트 처리
document.addEventListener('DOMContentLoaded', function() {
    // 수락 버튼 클릭 시 이벤트 리스너
    document.addEventListener('click', function(e) {
      if (e.target.classList.contains('accept-btn')) {
        const notificationTime = e.target.parentElement.querySelector('.notification-time').textContent;
        showModal(`친구 요청을 수락하시겠습니까?`, () => {
          // 여기에 실제 수락 로직을 구현
          // 예를 들어, AJAX 요청 등을 사용하여 서버에 신청을 수락하는 동작을 수행할 수 있음
        });
      } else if (e.target.classList.contains('reject-btn')) {
        const notificationTime = e.target.parentElement.querySelector('.notification-time').textContent;
        showModal(`친구 요청을 거절하시겠습니까?`, () => {
          // 여기에 실제 거절 로직을 구현
          // 예를 들어, AJAX 요청 등을 사용하여 서버에 신청을 거절하는 동작을 수행할 수 있음
        });
      }
    });
  
    // 모달 창 관련 함수
    function showModal(text, confirmCallback) {
      const modal = document.getElementById('modal');
      const modalText = document.getElementById('modalText');
      const confirmBtn = document.getElementById('confirmBtn');
      const cancelBtn = document.getElementById('cancelBtn');
  
      modalText.textContent = text;
      modal.style.display = 'block';
  
      confirmBtn.onclick = function() {
        confirmCallback();
        modal.style.display = 'none';
      };
  
      cancelBtn.onclick = function() {
        modal.style.display = 'none';
      };
    }
  });
  
  // 초기 알림 목록 렌더링
  renderNotifications();