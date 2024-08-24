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
    document.querySelector(".settings-container").style.marginLeft = "250px";
    document.querySelector(".settings-container").style.width = "calc(100% - 250px)";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0px";
    document.querySelector(".settings-container").style.marginLeft = "50px";
    document.querySelector(".settings-container").style.width = "calc(100% - 0px)";
}

//여기서부터 settings_main_script

function showSettings(setting) {
    const items = document.querySelectorAll('.settings-list-item');
    items.forEach(item => item.classList.remove('active'));

    event.currentTarget.classList.add('active');

    // 설정에 따라 적절한 함수 호출
    switch(setting) {
        case 'friend':
            showFriendSettings();
            break;
        case 'language':
            showLanguageSettings();
            break;
        case 'display':
            showDisplaySettings();
            break;
        case 'sound':
            showSoundSettings();
            break;
    }
}

function showFriendSettings() {
    const settingsMain = document.getElementById('settingsMain');
    settingsMain.innerHTML = `
        <div class="friend-settings">
            <h2><img src="../assets/친구.svg" id="main_assets">친구</h2>
            <!-- 친구 설정 관련 내용 -->
        </div>
    `;
}

function showLanguageSettings() {
    const settingsMain = document.getElementById('settingsMain');
    settingsMain.innerHTML = `
        <div class="language-settings">
            <h2><img src="../assets/언어.svg" id="main_assets">언어</h2>
            <div class="item">
                <span id="title_item">언어</span>
                <div class="selector">
                    <select class="dropdown">
                        <option>한국어</option>
                        <option>English(US)</option>
                        <option>中文</option>
                        <option>日本語</option>
                        <option>Русский</option>
                    </select>
                </div>
                <p id="explain">사용자 인터페이스에서 사용되는 언어를 변경합니다.</p>
            </div>
        </div>
    `;
}

function showDisplaySettings() {
    const settingsMain = document.getElementById('settingsMain');
    settingsMain.innerHTML = `
        <div class="display-settings">
            <h2><img src="../assets/디스플레이.svg" id="main_assets">디스플레이</h2>
            <div class="item">
                <span id="title_item">글씨 크기</span>
                <div class="size-slider">
                    <span class="small">가</span>
                    <input type="range" min="1" max="100" value="50" class="slider" id="fontSizeSlider">
                    <span class="big">가</span>
                </div>
                <p id="explain">사용자 인터페이스에서 사용되는 글씨 크기를 변경합니다.</p>
            </div>
            <div class="item">
                <span id="title_item">폰트</span>
                <div class="selector">
                    <select class="dropdown">
                        <option value="inter">Inter</option>
                        <option value="bold">Bold</option>
                        <!-- 다른 폰트 옵션들 추가 -->
                    </select>
                </div>
                <p id="explain">사용자 인터페이스에서 사용되는 폰트를 변경합니다.</p>
            </div>
        </div>
    `;
}

function showSoundSettings() {
    const settingsMain = document.getElementById('settingsMain');
    settingsMain.innerHTML = `
        <div class="sound-settings">
            <h2><img src="../assets/사운드.svg" id="main_assets">사운드</h2>
            <!-- 사운드 설정 관련 내용 -->
        </div>
    `;
}

// 페이지 로드 시 settings-main을 비웁니다.
window.onload = function() {
    openNav();
    document.getElementById('settingsMain').innerHTML = '';
}