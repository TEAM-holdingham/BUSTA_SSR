console.log("스크립트 로드됨");

let principalName = "";

document.addEventListener('DOMContentLoaded', () => {
    fetch('/friends/user/principal')
        .then(response => response.text())
        .then(data => {
            principalName = data;
            loadPendingRequests();
            loadFriends();
        }).catch(error => console.error('에러:', error));
});

document.getElementById('search-button').addEventListener('click', function() {
    const searchQuery = document.getElementById('search-input').value;
    searchFriends(searchQuery);
});


function searchFriends(query) {
    fetch(`/friends/search?nickname=${encodeURIComponent(query)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답이 정상이 아닙니다 ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const searchResults = document.getElementById('search-results');
            searchResults.innerHTML = ''; // 이전 결과 삭제
            data.forEach(user => {
                if (user.nickname !== principalName) {
                    const li = document.createElement('li');
                    li.textContent = user.nickname;

                    const addButton = document.createElement('button');
                    addButton.textContent = '추가';
                    addButton.addEventListener('click', () => {
                        sendFriendRequest(user.nickname);
                    });

                    li.appendChild(addButton);
                    searchResults.appendChild(li);
                }
            });
        })
        .catch(error => console.error('에러:', error));
}

function sendFriendRequest(receiverNickname) {
    fetch('/friends/request?receiverNickname=' + encodeURIComponent(receiverNickname), {
        method: 'POST'
    }).then(response => response.text()).then(data => {
        alert(data);
        loadPendingRequests();
    }).catch(error => console.error('에러:', error));
}

function loadPendingRequests() {
    console.log("대기 요청 로드됨");
    fetch('/friends/pending')
        .then(response => {
            if (!response.ok) {
                throw new Error('네트워크 응답이 정상이 아닙니다 ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            const pendingRequests = document.getElementById('pending-requests');
            pendingRequests.innerHTML = '';
            data.forEach(request => {
                const li = document.createElement('li');
                li.textContent = request.sender && request.sender.nickname ? `Request from ${request.sender.nickname}` : '잘못된 요청 데이터';

                const acceptButton = document.createElement('button');
                acceptButton.textContent = '수락';
                acceptButton.addEventListener('click', () => respondToRequest(request.sender.nickname, true));

                const rejectButton = document.createElement('button');
                rejectButton.textContent = '거절';
                rejectButton.addEventListener('click', () => respondToRequest(request.sender.nickname, false));

                li.appendChild(acceptButton);
                li.appendChild(rejectButton);
                pendingRequests.appendChild(li);
            });
        })
        .catch(error => console.error('에러:', error));
}


/*새친구버튼을위해 추가한 함수*/
document.getElementById('load-requests-button').addEventListener('click', loadPendingRequests);
/*새친구버튼을위해 추가한 함수*/

function respondToRequest(senderNickname, accepted) {
    fetch(`/friends/respond?senderNickname=${encodeURIComponent(senderNickname)}&accepted=${accepted}`, {
        method: 'POST'
    }).then(response => response.text()).then(data => {
        alert(data);
        loadPendingRequests();
        loadFriends();
    }).catch(error => console.error('에러:', error));
}

function loadFriends() {
    console.log("친구 목록 로드됨");

    fetch('/friends/friendsList')
        .then(response => response.json())
        .then(data => {
            const friendsList = document.getElementById('friends-list');
            friendsList.innerHTML = '';

            data.forEach(friend => {
                let friendNickname = '';

                if (friend.sender && friend.sender.nickname === principalName) {
                    if (friend.receiver && friend.receiver.loginId) {
                        fetch(`/users/nickname?loginId=${encodeURIComponent(friend.receiver.loginId)}`)
                            .then(response => response.json())
                            .then(receiverData => {
                                if (receiverData.nickname) {
                                    friendNickname = receiverData.nickname;
                                    displayFriend(friendNickname, friend.receiver.loginId);
                                }
                            })
                            .catch(error => console.error('수신자 닉네임 가져오기 에러:', error));
                    }
                } else if (friend.receiver && friend.receiver.loginId === principalName) {
                    if (friend.sender && friend.sender.nickname) {
                        friendNickname = friend.sender.nickname;
                        displayFriend(friendNickname, friend.sender.loginId);
                    }
                }
            });

            function displayFriend(nickname, friendLoginId) {
                const li = document.createElement('li');
                li.textContent = `Friend: ${nickname}`;

                const starIcon = document.createElement('span');
                starIcon.className = 'star-icon';
                starIcon.innerHTML = '☆';
                starIcon.addEventListener('click', () => {
                    starIcon.classList.toggle('starred');
                    starIcon.innerHTML = starIcon.classList.contains('starred') ? '★' : '☆';
                    // 즐겨찾기 상태를 백엔드에 저장하는 로직 추가
                });

                const chatButton = document.createElement('button');
                chatButton.textContent = '채팅';
                chatButton.addEventListener('click', () => {
                    if (nickname) {
                        window.location.href = `/chat?friend=${encodeURIComponent(nickname)}`;
                    } else {
                        alert('친구 닉네임이 없습니다.');
                    }
                });

                const deleteButton = document.createElement('button');
                deleteButton.textContent = '삭제';
                deleteButton.addEventListener('click', () => {
                    if (confirm(`친구 ${nickname}를 삭제하시겠습니까?`)) {
                        deleteFriend(friendLoginId);
                    }
                });

                li.appendChild(starIcon);
                li.appendChild(chatButton);
                li.appendChild(deleteButton);

                friendsList.appendChild(li);
            }
        }).catch(error => console.error('에러:', error));
}

function deleteFriend(friendLoginId) {
    fetch(`/friends/delete?currentUserLoginId=${encodeURIComponent(principalName)}&friendLoginId=${encodeURIComponent(friendLoginId)}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('네트워크 응답이 정상이 아닙니다.');
            }
        })
        .then(data => {
            alert(data);
            loadFriends();
        })
        .catch(error => console.error('친구 삭제 에러:', error));
}
