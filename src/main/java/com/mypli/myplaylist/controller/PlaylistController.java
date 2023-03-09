package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Music;
import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.AddMusicDto;
import com.mypli.myplaylist.dto.CreateMusicDto;
import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.service.MusicService;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final MusicService musicService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;

    //TODO: 유튜브로 내보내기 구현

    /**
     * 플레이리스트 상세정보 페이지
     */
    @GetMapping("/{playlistId}")
    public String getPlaylist(@PathVariable Long playlistId, Principal principal, Model model) {

        if (principal == null) return "redirect:/oauth2/login";

        Playlist playlist = playlistService.findById(playlistId);

        PlaylistDto playlistDto = PlaylistDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .playlistImg(playlist.getPlaylistImg())
                .build();

        List<Music> musicList = musicService.findByPlaylistId(playlistDto.getPlaylistId());

        model.addAttribute("playlist", playlistDto);
        model.addAttribute("musicList", musicList);

        return "playlist :: main";
    }

    /**
     * 플레이리스트 상세정보 페이지 - 변경사항 저장 (렌더링용 html 만들고 거기에 model넣어서 ajax로 일부 교체)
     */
    @PutMapping("/{playlistId}")
    public String updatePlaylist(@PathVariable String playlistId) {
        //TODO: PatchMapping도 생각해보자 (부분 덮어쓰기)
        //TODO: PlaylistName, 그리고 Playlist가 가진 Music들의 변경점을 반영해야함 (이거 카카오오븐에 쎠놨던거 참고)

        return "playlistDetail";
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

        return "redirect:/playlist/{playlistId}";
    }

    /**
     * 유튜브에서 불러오기 모달 - 유튜브 플레이리스트의 노래를 현재 플레이리스트에 추가
     */
    @PostMapping("/youtube/playlists")
    public String addPlaylistFromYoutube(@ModelAttribute AddMusicDto addMusicDto, Principal principal, RedirectAttributes redirectAttributes) {
        //"유튜브에서 불러오기" -> 유튜브 플레이리스트 하나를 선택했을 경우

        String socialId = "";
        if (principal == null) return "redirect:/oauth2/login";
        else socialId = principal.getName();

        List<YoutubePlaylistItemDto> youtubePlaylistItemResultList = youtubePlaylistItemsService.getPlaylistItems(socialId, addMusicDto.getYoutubePlaylistId());

        Long playlistId = musicService.importFromYoutube(socialId, addMusicDto, youtubePlaylistItemResultList);

        redirectAttributes.addAttribute("playlistId", playlistId);

        return "redirect:/playlist/{playlistId}";
    }

    /**
     * 노래 추가 세부설정 모달 - 노래 추가
     */
    @PostMapping("/search")
    public String addMusic(@ModelAttribute CreateMusicDto createMusicDto, Principal principal, Model model) {

        //socialId는 권한 체크용
        String socialId = "";
        if (principal == null) return "redirect:/oauth2/login";
        else socialId = principal.getName();

        Long newMusicId = musicService.create(socialId, createMusicDto);
        Long playlistId = musicService.findPlaylistIdById(newMusicId);

        List<Music> musicList = musicService.findByPlaylistId(playlistId);
        model.addAttribute("musicList", musicList);

        return "fragments/playlistTable";
    }

    /**
     * 플레이리스트 상세정보 페이지 - 현재 재생중인 플레이리스트 변경
     */
    @GetMapping("/current")
    public String changePlaylist(@RequestParam Long playlistId, Model model) {

        log.info("{}", playlistId);

        Playlist playlist = playlistService.findById(playlistId);

        PlaylistDto playlistDto = PlaylistDto.builder()
                .playlistId(playlist.getId())
                .playlistName(playlist.getPlaylistName())
                .playlistImg(playlist.getPlaylistImg())
                .build();

        List<Music> musicList = musicService.findByPlaylistId(playlistDto.getPlaylistId());

        model.addAttribute("playlist", playlistDto);
        model.addAttribute("musicList", musicList);

        return "fragments/sidebar";
    }
}
