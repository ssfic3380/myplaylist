/* 플레이리스트 페이지 - tableDnD 설정 */
function setTableDnD() {
    $("#musicListTable tbody").tableDnD({
        onDrop: function (table, row) {
            let rows = table.rows;
            let musicIds = new Array();
            for (let i = 0; i < rows.length; i++) {
                musicIds.push(rows[i].id);
                rows[i].firstChild.nextSibling.textContent = (i+1).toString();
            }

            let playlistId = $("#playlistId").val();
            let url = "/playlist/" + playlistId + "/order";
            let params = {
                musicIds : musicIds
            };

            $.ajax({
                type: "PATCH",
                url: url,
                contentType:'application/json;charset=UTF-8',
                headers: {'Authorization': 'Bearer ' + token},
                data: JSON.stringify(params),
                dataType: "JSON"
            })
                .done(function (result) {
                    setTableDnD();
                })
                .fail(function (jqXHR) {
                    console.log(jqXHR);
                })
                .always(function() {

                })
        }
    });
}


/* 플레이리스트 페이지 - 홈 페이지로 이동 */
function getHomePage() {
    console.log("getHomePage(): " + token);

    $.ajax({
        type: "GET",
        url: "/",
        headers: {'Authorization': 'Bearer ' + token},
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
    var buttonId = $(this).attr('id');

    $("#youtubeUrlInput").val(data);
    $("#youtubeUrlButtonId").val(buttonId);
});


/* 유튜브 URL 변경 모달 - 확인 버튼 */
function updateUrl() {
    let newUrl = $("#youtubeUrlInput").val();
    let buttonId = $("#youtubeUrlButtonId").val();

    $("#" + buttonId).data('video-id', newUrl);

    console.log("updateUrl(): " + token);
    let playlistId = $("#playlistId").val();
    let url = "/playlist/" + playlistId + "/music";
    let params = {
        playlistId : playlistId,
        musicId : $("#" + buttonId).parents("tr").data("music-id"),
        title : $("#" + buttonId).parents("tr").children().children(".musicTitle").text(),
        artist : $("#" + buttonId).parents("tr").children().children(".musicArtist").text(),
        album : $("#" + buttonId).parents("tr").children().children(".musicAlbum").text(),
        videoId : newUrl,
        musicOrder : $("#" + buttonId).parents("tr").children(".musicOrder").text()
    }

    $.ajax({
        type: "POST",
        url: url,
        headers: {'Authorization': 'Bearer ' + token},
        data: JSON.stringify(params),
        contentType:'application/json;charset=UTF-8',
        dataType: "text"
    })
        .done(function (result) {
            $("main").replaceWith(result);
            setTableDnD();
        })
        .fail(function (jqXHR) {
            console.log(jqXHR);
        })
        .always(function() {

        })
}


/* 플레이리스트 페이지 - 현재 재생중인 플레이리스트 변경 */
document.write('<script src="/js/sidebar.js"></script>');
function changePlaylist() {

    console.log("changePlaylist(): " + token);
    var params = {
        playlistId : $("#playlistId").val()
    }

    $.ajax({
        type: "GET",
        url: "/playlist/current",
        headers: {'Authorization': 'Bearer ' + token},
        data: params,
        dataType: "text"
    })
        .done(function (result) {
            $("aside").empty();
            $("aside").replaceWith(result);

            loadYoutubeApi();
        })
        .fail(function (jqXHR) {
            console.log(jqXHR);
        })
        .always(function() {

        })
}


/* 플레이리스트 페이지 - 플레이리스트 이름, 노래 정보들 변경 */
$(function() {
    $(document).on('focus', '.editable', function() {
        $(this).css("border", "1px solid");
    });
    $(document).on('blur', '.editable',function() {
        $(this).css("border", "initial");

        let playlistId = $("#playlistId").val();
        if ($(this).hasClass("playlistName") === true) {

            if ($(this).text() !== $(this).data("default")) {

                console.log("updatePlaylist(): " + token);
                let url = "/playlist/" + playlistId;
                let params = {
                    playlistName: $(this).text()
                }

                $.ajax({
                    type: "PATCH",
                    url: url,
                    contentType:'application/json;charset=UTF-8',
                    headers: {'Authorization': 'Bearer ' + token},
                    data: JSON.stringify(params),
                    dataType: "JSON"
                })
                    .done(function (result) {
                        setTableDnD();
                    })
                    .fail(function (jqXHR) {
                        console.log(jqXHR);
                    })
                    .always(function () {

                    })
            }

        } else if ($(this).hasClass("playlistName") === false) {

            if ($(this).text() !== $(this).data("default")) {

                console.log("updateMusic(): " + token);
                let musicId = $(this).parents("tr").data("music-id")
                let url = "/playlist/" + playlistId + "/" + musicId;
                let params = {
                    title : $(this).parents("tr").children().children(".musicTitle").text(),
                    artist : $(this).parents("tr").children().children(".musicArtist").text(),
                    album : $(this).parents("tr").children().children(".musicAlbum").text(),
                    videoId : $(this).parents("tr").children().children(".musicVideoId").data("video-id"),
                    musicOrder : $(this).parents("tr").children(".musicOrder").text()
                }

                $.ajax({
                    type: "PATCH",
                    url: url,
                    contentType:'application/json;charset=UTF-8',
                    headers: {'Authorization': 'Bearer ' + token},
                    data: JSON.stringify(params),
                    dataType: "JSON"
                })
                    .done(function (result) {
                        setTableDnD();
                    })
                    .fail(function (jqXHR) {
                        console.log(jqXHR);
                    })
                    .always(function() {

                    })
            }
        }
    });
    $(document).on('keypress', '.editable', function(e) {
        if (e.which === 13) {
            e.preventDefault();
            $(this).blur();
        }
    });
});


/* 플레이리스트 추가 모달 */
// 모달 창 종료시 검색 결과 삭제
$(document).on('hidden.bs.modal', '#playlistAddModal', function(e) {
    $(this).find('#youtubePlaylistResult')[0].replaceChildren();
});

// 플레이리스트 검색 결과 추가
function addYoutubePlaylistResults(youtubePlaylistId, title, thumbnail) {
    /*
        <div class="youtubePlaylist d-flex align-items-center border-top border-bottom mb-1 py-1" onclick="getYoutubePlaylist()" style="cursor: pointer;">
            <div class="flex-shrink-0">
                <img th:src="@{https://i.ytimg.com/vi/nMWRL0aZ7SU/default.jpg}">
            </div>
            <div class="flex-grow-1 ms-3 fw-bold fs-4">
                여행
            </div>
        </div>
     */
    const playlist = document.createElement("div");
    playlist.classList.add("youtubePlaylist", "d-flex", "align-items-center", "border-top", "border-bottom", "mb-1", "py-1")
    playlist.style.cursor = 'pointer';
    playlist.setAttribute("data-youtube-playlist-id", youtubePlaylistId);
    playlist.setAttribute("data-bs-dismiss", "modal");

    const playlistThumbnail = document.createElement("div");
    playlistThumbnail.classList.add("flex-shrink-0");
    const playlistThumbnailImg = document.createElement("img");
    playlistThumbnailImg.classList.add("addPlaylistThumbnail");
    playlistThumbnailImg.src = thumbnail;
    playlistThumbnailImg.style.width = '120px';
    playlistThumbnailImg.style.height = '90px';
    playlistThumbnailImg.setAttribute("data-thumbnail", thumbnail);
    playlistThumbnail.appendChild(playlistThumbnailImg);

    const playlistTitle = document.createElement("div");
    playlistTitle.classList.add("addPlaylistTitle", "flex-grow-1", "ms-3", "fw-bold", "fs-4");
    playlistTitle.textContent = title;
    playlistTitle.setAttribute("data-title", title);

    playlist.appendChild(playlistThumbnail);
    playlist.appendChild(playlistTitle);

    document.getElementById("youtubePlaylistResult").appendChild(playlist);
}

// 모달 창이 켜지면 바로 플레이리스트 검색
$(document).on('shown.bs.modal', '#playlistAddModal', function(e) {
    console.log("getYoutubePlaylist(): " + token);

    $.ajax({
        type: "GET",
        url: "/playlist/youtube/playlists",
        headers: {'Authorization': 'Bearer ' + token},
        dataType: "json"
    })
        .done(function (result) {
            // 새로운 검색 기록 추가
            var playlistList = result.body.data;
            for (var i = 0; i < playlistList.length; i++) {
                var playlist = JSON.parse(JSON.stringify(playlistList[i]));
                addYoutubePlaylistResults(playlist.playlistId, playlist.title, playlist.thumbnail);
            }
        })
        .fail(function (jqXHR) {
            console.log(jqXHR);
        })
        .always(function() {

        })
});

// 플레이리스트 선택 시 현재 플레이리스트에 노래 추가
$(document).on('click', '.youtubePlaylist', function() {
    console.log("postAddPlaylist(): " + token);

    var mOrder = $("#musicListTableBody").children().last().children('th').data('music-order') + 1;
    if (!mOrder) mOrder = 1;

    var params = {
        playlistId : $("#playlistId").val(),
        youtubePlaylistId : $(this).data('youtube-playlist-id'),
        youtubePlaylistName : $(this).children(".addPlaylistTitle").data('title'),
        youtubePlaylistImg : $(this).children().children(".addPlaylistThumbnail").data('thumbnail'),
        lastMusicOrder : mOrder
    }

    $.ajax({
        type: "POST",
        url: "/playlist/youtube/playlists",
        headers: {'Authorization': 'Bearer ' + token},
        data: params,
        dataType: "text"
    })
        .done(function (result) {
            $("main").replaceWith(result);
            setTableDnD();
        })
        .fail(function (jqXHR) {
            console.log(jqXHR);
        })
        .always(function() {

        })


})


/* 노래 추가 모달 */
// 모달 창 종료시 검색 결과 삭제
$(document).on('hidden.bs.modal', '#musicAddModal', function(e) {
    $(this).find('#youtubeSearchQuery')[0].value = null;
    $(this).find('#youtubeSearchResult')[0].replaceChildren();
});

/* 노래 추가 모달 - 검색 버튼 */
// 노래 검색 결과 추가
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
    console.log("getYoutubeSearchList(): " + token);

    var params = {
        q : $("#youtubeSearchQuery").val()
    }

    $.ajax({
        type: "GET",
        url: "/playlist/youtube/search",
        headers: {'Authorization': 'Bearer ' + token},
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

    $("#musicVideoId").val(videoId);
    $("#musicThumbnail").val(thumbnail);
    $("#musicTitle").val(title);
    $("#musicArtist").val(artist);
})

// 확인 버튼 클릭시
function postAddMusic() {
    console.log("postAddMusic(): " + token);

    var mOrder = $("#musicListTableBody").children().last().children('th').data('music-order') + 1;
    if (!mOrder) mOrder = 1;

    var params = {
        title : $("#musicTitle").val(),
        artist : $("#musicArtist").val(),
        album : $("#musicAlbum").val(),
        videoId : $("#musicVideoId").val(),
        musicImg : $("#musicThumbnail").val(),
        musicOrder : mOrder,
        playlistId : $("#playlistId").val()
    }

    $.ajax({
        type: "POST",
        url: "/playlist/youtube/search",
        headers: {'Authorization': 'Bearer ' + token},
        data: params,
        dataType: "text"
    })
        .done(function (result) {
            $("main").replaceWith(result);
            setTableDnD();
        })
        .fail(function (jqXHR) {
            console.log(jqXHR);
        })
        .always(function() {

        })
}

// 모달 창 종료시 이전 입력 기록 삭제
$(document).on('hidden.bs.modal', '#musicAddSettingModal', function(e) {
    $(this).find('form')[0].reset();
});


