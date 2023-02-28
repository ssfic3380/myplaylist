/* 메인 페이지 - 플레이리스트 페이지로 이동 */
function getPlaylistPage(playlistId) {
    $.ajax({
        type: "GET",
        url: "/playlist/" + playlistId,
        dataType: "text"
    })
        .done(function (result) {
            $("main").replaceWith(result);
        })
}