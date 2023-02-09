package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Music;
import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.MusicDto;
import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubeSearchDto;
import com.mypli.myplaylist.exception.MusicNotFoundException;
import com.mypli.myplaylist.exception.PlaylistNotFoundException;
import com.mypli.myplaylist.service.MusicService;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.service.youtube.YoutubeSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final MusicService musicService;
    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;
    private final YoutubeSearchService youtubeSearchService;

    //TODO: model에 추가할 때 attributeName 어떻게 정할지 고민
    //TODO: 유튜브로 내보내기 구현

    /**
     * 플레이리스트 상세정보 페이지
     */
    @GetMapping("/{playlistId}")
    public String getPlaylist(@PathVariable String playlistId, Model model) {

        Playlist playlist = playlistService.findById(playlistId);

        PlaylistDto playlistDto = PlaylistDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .playlistImg(playlist.getPlaylistImg())
                .build();

        List<Music> musicList = musicService.findByPlaylistId(playlistDto.getPlaylistId());

        model.addAttribute("playlist", playlistDto);
        model.addAttribute("musicList", musicList);

        return "playlistDetail";
    }

    @PutMapping("/{playlistId}")
    public String updatePlaylist(@PathVariable String playlistId) {
        //TODO: PatchMapping도 생각해보자 (부분 덮어쓰기)
        //TODO: PlaylistName, 그리고 Playlist가 가진 Music들의 변경점을 반영해야함 (이거 카카오오븐에 쎠놨던거 참고)

        return "playlistDetail";
    }

    /**
     * 플레이리스트 상세정보 페이지 - 노래 추가
     * List는 비어있을 수 있음
     */
    @GetMapping("/search")
    public String openYoutubeSearchPopup(@RequestParam(value = "q", required = false) String searchQuery,
                                         Model model) {

        List<YoutubeSearchDto> youtubeSearchResultList = new ArrayList<>();
        if (searchQuery != null) youtubeSearchResultList = youtubeSearchService.getSearchResult(searchQuery);

        model.addAttribute("youtubeSearchResults", youtubeSearchResultList);

        return "youtube/searchPopup";
    }

    @PostMapping("/search")
    public String getYoutubeItems(@ModelAttribute("youtubeSearchResult") YoutubeSearchDto youtubeSearchDto,
                                  Model model) {
        //TODO: 선택한 youtubeItem을 Music에 추가하려고 하는데, playlistId가 필요함



        /*MusicDto musicDto = MusicDto.builder()
                .title(youtubeSearchDto.getTitle())
                .artist(youtubeSearchDto.getChannelTitle())
                .videoId(youtubeSearchDto.getVideoId())
                .musicImg(youtubeSearchDto.getThumbnail())
                .musicOrder();
        musicService.create();*/

        //TODO: 여기서 리턴을 하면 팝업창에 대해서 렌더링을 할텐데, 플레이리스트 페이지를 새로고침하려면 어떻게 해야할지?
        return "youtube/searchPopup";
    }

    /**
     * 플레이리스트 상세정보 페이지 - 노래 삭제
     */
    @DeleteMapping("/{musicId}")
    public String deleteMusic(@PathVariable String musicId,
                              RedirectAttributes redirectAttributes) {

        Long musicIdL = Long.parseLong(musicId);
        Music music = musicService.findById(musicIdL);
        Long playlistId = music.getPlaylist().getId();
        musicService.deleteById(musicIdL); //TODO: 플레이리스트의 musicList에서 지워지는지 확인(안해도 될것같긴 함)

        redirectAttributes.addAttribute("playlistId", playlistId);

        return "redirect:/{playlistId}";
    }
}
