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
    document.querySelector(".chat-container").style.marginLeft = "250px";
    document.querySelector(".chat-container").style.width = "calc(100% - 250px)";
    document.querySelector(".input-container").style.width = "calc(75% - 305px)";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0px";
    document.querySelector(".chat-container").style.marginLeft = "50px";
    document.querySelector(".chat-container").style.width = "calc(100% - 0px)";
    document.querySelector(".input-container").style.width = "calc(75% - 170px)";
}

//여기서부터 dm_script

 // 가짜 데이터 (실제 데이터로 대체되어야 함)
const messages = {
    1: [
        { type: 'sent', content: '안녕하세요!' },
        { type: 'received', content: '민공지능 최고~~><' }
    ],
    2: [
        { type: 'received', content: '인증번호 확인을 확인해' },
        { type: 'sent', content: '인증번호 확인을 확인하는 것이야' },
        { type: 'sent', content: '인증번호 확인을 확인해서 확인하여 확인해보거라..확인혀' },
        { type: 'received', content: '인증번호 확인이 참말인가...? 대답.' },
        { type: 'sent', content: '인증번호 확인!!!'},
        { type: 'received', content: '인증번호 확인~~' }
                
    ],
    3: [
        { type: 'sent', content: '오늘 뭐해요?' },
        { type: 'received', content: '우린 운명의 데스티니야 0.<' }
    ],

    4: [
        { type: 'sent', content: '새벽은 자라고 있는 것이 아니라 피그마하라고 있는 것이야..'}
    ]
};


// 채팅 메시지를 보여주는 함수
function showChat(chatId) {
    const chatMain = document.querySelector('.chat-main');
    const chatContent = document.getElementById('chat-content');
    const chatMessages = document.getElementById('chatMessages');
    const inputContainer = document.querySelector('.input-container');
    const chatPreviewArea = document.getElementById('chatPreviewArea');
    
    chatMain.style.backgroundColor = "#F1F1F1";
    chatContent.style.display = 'block';
    inputContainer.style.display = 'flex';
    chatMessages.innerHTML = '';

    messages[chatId].forEach(message => {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', message.type);
        messageElement.textContent = message.content;
        chatMessages.appendChild(messageElement);
    });

    // 채팅 목록에서 활성화 표시 변경
    const chatListItems = document.querySelectorAll('.chat-list-item');
    chatListItems.forEach(item => item.classList.remove('active'));
    chatListItems[chatId - 1].classList.add('active');


    // 채팅 메시지 영역 스크롤을 맨 아래로 이동
    const chatMessagesContainer = document.querySelector('.chat-messages-container');
    chatMessagesContainer.scrollTop = chatMessagesContainer.scrollHeight;   
}

// 채팅 목록 업데이트 함수
function updateChatList() {
    const chatListItems = document.querySelectorAll('.chat-list-item');
    chatListItems.forEach((item, index) => {
        const chatId = index + 1;
        const lastMessage = messages[chatId][messages[chatId].length - 1];
        const previewElement = item.querySelector('.preview');
        previewElement.textContent = lastMessage.content;
    });
}

// 페이지 로드 시와 새 메시지 전송 후 채팅 목록 업데이트
window.onload = function() {
    openNav();
    updateChatList();
}


// 메시지 전송 함수
function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value.trim();

    if (message === '') {
        return; // 빈 메시지는 전송하지 않음
    }

    const activeChatId = document.querySelector('.chat-list-item.active').innerText.trim();
    const chatMessages = document.getElementById('chatMessages');
    const messageElement = document.createElement('div');
    messageElement.classList.add('message', 'sent');
    messageElement.textContent = message;
    chatMessages.appendChild(messageElement);

    // 메시지 전송 후 입력창 초기화
    messageInput.value = '';

    // 실제로는 여기서 서버에 메시지를 전송하는 코드를 추가해야 함
    // 서버로 메시지를 전송하고 응답을 받아와야 함 (실제 채팅 애플리케이션에서는)

    // 가짜 데이터 업데이트 (실제로는 서버 응답을 기다려야 함)
    const chatId = parseInt(activeChatId.charAt(activeChatId.length - 1)); // '친구X'에서 X를 추출
    messages[chatId].push({ type: 'received', content: '응답 메시지 예시' });

    // 다시 채팅창 업데이트
    showChat(chatId);

    updateChatList();
}


