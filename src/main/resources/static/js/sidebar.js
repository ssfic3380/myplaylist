/* global bootstrap: false */
(() => {
    'use strict'
    const tooltipTriggerList = Array.from(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    tooltipTriggerList.forEach(tooltipTriggerEl => {
      new bootstrap.Tooltip(tooltipTriggerEl)
    })
})()

/* 사이드바 - 유튜브 플레이어 */
var yPlayer;
var playerHeight;
var playerWidth;
var vId;
var current;

var isEmpty = function(value) {
    if(value == "" || value == null || value == undefined || (value != null && typeof value == "object" && !Object.keys(value).length)) return true
    else return false
};

function loadYoutubeApi() {
    if (isEmpty(yPlayer) == false) {
        yPlayer.stopVideo();
        yPlayer.destroy();
        yPlayer = null;
        vId = document.getElementById('music1').dataset.videoId;
        current = 1;
        onYouTubeIframeAPIReady();
    }

    // 1. This code loads the IFrame Player API code asynchronously.
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";

    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

    playerHeight = firstScriptTag.parentElement.offsetHeight * 0.2;
    playerWidth = firstScriptTag.parentElement.offsetWidth;
}

// 2. This function creates an <iframe> (and YouTube player) after the API code downloads.
function onYouTubeIframeAPIReady() {
    yPlayer = new YT.Player('player', {
        height: playerHeight,
        width: playerWidth,
        videoId: vId,
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange,
            'onError': onPlayerError
        }
    });
}

function onPlayerError(event) {
    if (event.data == 150) {
        yPlayer.stopVideo();
    }
}

// 3. The API will call this function when the video player is ready.
function onPlayerReady(event) {
    yPlayer.setPlaybackRate(1);
    yPlayer.playVideo();
}

// 4. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
function onPlayerStateChange(event) {
    if (event.data == YT.PlayerState.ENDED) {
        setTimeout(onPlayerStateChange_execute, 2000);
    }
}

function onPlayerStateChange_execute() {
    var next = current + 1;
    if ( $('#music' + next).length) {
        $('#music' + next).trigger("click");
    } else {
        next = 1;
        $('#music' + next).trigger("click");
    }
}

function stopVideo() {
    yPlayer.stopVideo();
}

/* 현재 플레이리스트 - 노래 재생 */
$(document).on('click', '.list-group-item', function() {
    if ( $('#music' + current).length) {
        $('#music' + current).removeClass('active');
        $('#music' + current).removeAttr('aria-current');
    }

    vId = $(this).data('video-id');
    current = $(this).data('music-order');
    yPlayer.loadVideoById(vId);

    $(this).addClass('active');
    $(this).attr('aria-current', 'true');
});