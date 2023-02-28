/* 플레이리스트 페이지 - 홈 페이지로 이동 */
function getHomePage() {
    $.ajax({
        type: "GET",
        url: "/",
        data: { refresh : "true" },
        dataType: "text"
    })
        .done(function (result) {
            $("main").replaceWith(result);
        })
}

/* 플레이리스트 페이지 - 노래 수정 버튼 */
$(document).on('click', '.btn-videoId-modal', function() {
    var data = $(this).data('video-id');
    $("#youtubeUrlInput").val(data);
});


/* 노래 추가 모달 */
// 모달 창 종료시 검색 결과 삭제
$('#musicAddModal').on('hidden.bs.modal', function(e) {
    $(this).find('#youtubeSearchQuery')[0].value = null;
    $(this).find('#youtubeSearchResult')[0].replaceChildren();
});

/* 노래 추가 모달 - 검색 버튼 */
// 검색 결과 추가
function addYoutubeSearchResults(title, artist, thumbnail, videoId) {
    /*
        div id="youtubeSearchResult"

        <div class="music d-flex align-items-center border-top border-bottom mb-1 py-1" style="cursor: pointer;"
             data-bs-target="#musicAddSettingModal" data-bs-toggle="modal" data-bs-dismiss="modal">
            <div class="flex-shrink-0">
                <img class="musicThumbnailImg" th:src="@{https://i.ytimg.com/vi/nMWRL0aZ7SU/default.jpg}" data-thumbnail="주소">
            </div>
            <div class="flex-grow-1 d-flex flex-column align-items-start justify-content-center ms-3">
                <span class="addMusicTitle fw-bold fs-5 my-1" data-title="어쩔 수가 없나봐">어쩔 수가 없나봐</span>
                <span class="addMusicArtist fs-6 my-1" data-artist="김나영">김나영</span>
            </div>
        </div>
     */
    const music = document.createElement("div");
    music.classList.add("music", "d-flex", "align-items-center", "border-top", "border-bottom", "mb-1", "py-1")
    music.style.cursor = 'pointer';
    music.setAttribute("data-bs-target", "#musicAddSettingModal");
    music.setAttribute("data-bs-toggle", "modal");
    music.setAttribute("data-bs-dismiss", "modal");
    music.setAttribute("data-video-id", videoId);

    const musicThumbnail = document.createElement("div");
    musicThumbnail.classList.add("flex-shrink-0");
    const musicThumbnailImg = document.createElement("img");
    musicThumbnailImg.classList.add("addMusicThumbnail");
    musicThumbnailImg.src = thumbnail;
    musicThumbnailImg.setAttribute("data-thumbnail", thumbnail);
    musicThumbnail.appendChild(musicThumbnailImg);

    const musicInfo = document.createElement("div");
    musicInfo.classList.add("flex-grow-1", "d-flex", "flex-column", "align-items-start", "justify-content-center", "ms-3");
    const musicTitle = document.createElement("span");
    musicTitle.classList.add("addMusicTitle", "fw-bold", "fs-5", "my-1");
    musicTitle.textContent = title;
    musicTitle.setAttribute("data-title", title);
    const musicArtist = document.createElement("span");
    musicArtist.classList.add("addMusicArtist", "fs-6", "my-1");
    musicArtist.textContent = artist;
    musicArtist.setAttribute("data-artist", artist);
    musicInfo.appendChild(musicTitle);
    musicInfo.appendChild(musicArtist);

    music.appendChild(musicThumbnail);
    music.appendChild(musicInfo);

    document.getElementById("youtubeSearchResult").appendChild(music);
}

// 이전 검색 기록 삭제
function removeYoutubeSearchResults() {
    document.getElementById("youtubeSearchResult").replaceChildren();
}

function getYoutubeSearchList() {
    var params = {
        q : $("#youtubeSearchQuery").val()
    }

    $.ajax({
        type: "GET",
        url: "/playlist/search",
        data: params,
        dataType: "json"
    })
        .done(function (result) {
            // 1. 기존에 있던 검색 기록 삭제
            removeYoutubeSearchResults();

            // 2. 새로운 검색 기록 추가
            var musicList = result.body.data;
            for (var i = 0; i < musicList.length; i++) {
                var music = JSON.parse(JSON.stringify(musicList[i]));
                addYoutubeSearchResults(music.title, music.channelTitle, music.thumbnail, music.videoId);
            }
        })
        .fail(function (jqXHR) {
            console.log(jqXHR);
        })
        .always(function() {

        })
}

/* 노래 추가 세부설정 모달 */
// 모달 창 초기값 설정 (동적으로 생성된 페이지의 이벤트는 .on()을 사용해야 한다)

$(document).on('click', '.music', function() {
    // form 전송용
    var videoId = $(this).data('video-id');
    var thumbnail = $(this).children().children(".addMusicThumbnail").data('thumbnail');

    // 설정 데이터들
    var title = $(this).children().children(".addMusicTitle").data('title');
    var artist = $(this).children().children(".addMusicArtist").data('artist');

    $("#musicTitle").val(title);
    $("#musicArtist").val(artist);
})

// 모달 창 종료시 검색 결과 삭제
$('#musicAddSettingModal').on('hidden.bs.modal', function(e) {
    $(this).find('form')[0].reset();
});