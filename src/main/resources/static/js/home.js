/* 메인 페이지 - 플레이리스트 페이지로 이동 */
function getPlaylistPage(playlistId) {
    console.log("getPlaylistPage(): " + token);

    $.ajax({
        type: "GET",
        url: "/playlist/" + playlistId,
        headers: {'Authorization': 'Bearer ' + token},
        dataType: "text"
    })
        .done(function (result) {
            $("main").replaceWith(result);
            $("#musicListTable tbody").tableDnD({
                onDrop: function (table, row) {
                    let rows = table.rows;
                    let musicIds = new Array();
                    for (let i = 0; i < rows.length; i++) {
                        musicIds.push(rows[i].id);
                    }

                    let playlistId = $("#playlistId").val();
                    let url = "/playlist/" + playlistId + "/music-order";
                    let params = {
                        playlistId : playlistId,
                        musicIds : musicIds
                    };

                    $.ajax({
                        type: "POST",
                        url: url,
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
            });
        })


}

/* 메인 페이지 - 플레이리스트 추가 */
function createPlaylist() {
    console.log("createPlaylist(): " + token);

    $.ajax({
        type: "POST",
        url: "/",
        headers: {'Authorization': 'Bearer ' + token},
        dataType: "json"
    })
        .done(function (result) {
            getPlaylistPage(result.body.playlistId);
        })
}