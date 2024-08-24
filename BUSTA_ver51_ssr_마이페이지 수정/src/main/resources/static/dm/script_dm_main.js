var stompClient = null;
var friend = new URLSearchParams(window.location.search).get('friend');
var roomId = null;
var meta = document.querySelector('meta[name="loginId"]');
var username = meta ? meta.content : 'defaultUsername';

function connect() {
    fetch(`/chat/roomId?friend=${friend}`)
        .then(response => response.json())
        .then(data => {
            roomId = data.roomId;
            console.log('Room ID: ' + roomId);

            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);

            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);

                // roomId를 받은 후 메시지 구독
                stompClient.subscribe('/topic/messages/' + roomId, function (messageOutput) {
                    var message = JSON.parse(messageOutput.body);
                    console.log('Received message: ' + messageOutput.body);
                    showMessage(message);
                });

                // 사용자 추가 메시지 전송
                // stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN', roomId: roomId}));

                // Load chat history
                loadChatHistory();
            }, function (error) {
                console.log('STOMP error: ' + error);
            });
        })
        .catch(error => {
            console.error('Error fetching room ID:', error);
        });
}

function sendMessage() {
    var messageContent = document.getElementById('message').value;
    if (messageContent && stompClient && roomId) {
        var chatMessage = {
            sender: username,
            receiver: friend,
            content: messageContent,
            roomId: roomId,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        console.log('Sent message: ' + JSON.stringify(chatMessage));
        document.getElementById('message').value = '';
    } else {
        console.log('Message content is empty or STOMP client is not connected.');
    }
}

function showMessage(message) {

    var messageArea = document.getElementById('messageArea');
    var messageElement = document.createElement('li');
    messageElement.classList.add('list-group-item', 'message');

    if (message.sender === username) {
        messageElement.classList.add('sent');
    } else {
        messageElement.classList.add('received');
    }

    messageElement.textContent = message.content;
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function loadChatHistory() {
    if (!roomId) {
        console.error('roomId is missing.');
        return;
    }

    fetch('/chat/history?friend=' + friend + '&roomId=' + roomId)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(message => {
                showMessage(message);
            });
        })
        .catch(error => {
            console.error('Error loading chat history:', error);
        });
}

document.addEventListener("DOMContentLoaded", function() {
    connect();
});
