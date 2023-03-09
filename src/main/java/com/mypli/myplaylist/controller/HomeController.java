package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Playlist;
import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemDto;
import com.mypli.myplaylist.service.PlaylistService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistItemsService;
import com.mypli.myplaylist.service.youtube.YoutubePlaylistsService;
import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.service.MemberService;
import com.mypli.myplaylist.utils.HeaderUtils;
import com.mypli.myplaylist.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PlaylistService playlistService;
    private final YoutubePlaylistsService youtubePlaylistsService;
    private final YoutubePlaylistItemsService youtubePlaylistItemsService;

    /**
     * 메인페이지
     */
    @GetMapping("/")
    public String showAllPlaylists(@RequestParam(value = "refresh", required = false) String refresh,
                                   HttpServletRequest request, HttpServletResponse response, Principal principal, Model model) {
        //마플리 홈페이지 (유저의 모든 플레이리스트 보여주기)

        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null) {
            String accessToken = (String) flashMap.get("token");
            HeaderUtils.setAccessToken(response, accessToken);
            model.addAttribute("token", accessToken);
        }

        //if (principal == null) return "redirect:/oauth2/login"; //TODO: 일단 RefreshToken 만료시 무조건 다시 로그인
        
        String socialId = null;
        if (principal != null) socialId = principal.getName(); //TODO: 하지만 비로그인을 위해서 조금 고민해봐야 함

        List<Playlist> playlists = new ArrayList<>();
        if (socialId != null) playlists = playlistService.findBySocialId(socialId);
        model.addAttribute("playlistList", playlists);

        if (refresh != null && refresh.equals("true")) return "home :: main";
        else return "home";
    }

    /**
     * 메인페이지 - 플레이리스트 추가 (누르면 바로 새 플레이리스트의 상세페이지로 이동)
     */
    @PostMapping("/")
    public String createPlaylist(Principal principal,
                                 RedirectAttributes redirectAttributes) {
        //"추가" 버튼을 클릭했을 경우

        String socialId = principal.getName();

        PlaylistDto playlistDto = PlaylistDto.builder()
                .playlistName("새 플레이리스트")
                .build();
        Long playlistId = playlistService.create(socialId, playlistDto);

        redirectAttributes.addAttribute("playlistId", playlistId);

        return "redirect:/playlist/{playlistId}";
    }

    /**
     * 메인페이지 - 유튜브에서 불러오기
     */
    @GetMapping("/youtube/playlists")
    public String getYoutubePlaylists(Principal principal, Model model) {
        //"유튜브에서 불러오기" 버튼을 클릭했을 경우 (팝업창 띄우기는 프론트에서 처리)

        String socialId = principal.getName();

        List<YoutubePlaylistDto> youtubePlaylistResultList = youtubePlaylistsService.getPlaylists(socialId);
        model.addAttribute("youtubePlaylists", youtubePlaylistResultList);

        //TODO: 프론트에서 띄운 유튜브 플레이리스트 목록 팝업창으로 return해야함 (아마 youtube 디렉토리에 importPopup이라는 이름으로 HTML 만들지 않을까?)
        return "playlist";
    }

    @PostMapping("/youtube/playlists")
    public String createPlaylistFromYoutube(Principal principal,
                                            @ModelAttribute("youtubePlaylist") YoutubePlaylistDto youtubePlaylistDto,
                                            RedirectAttributes redirectAttributes) {
        //"유튜브에서 불러오기" -> 유튜브 플레이리스트 하나를 선택했을 경우

        String socialId = principal.getName();

        List<YoutubePlaylistItemDto> youtubePlaylistItemResultList = youtubePlaylistItemsService.getPlaylistItems(socialId, youtubePlaylistDto.getPlaylistId());
        Long playlistId = playlistService.importFromYoutube(socialId, youtubePlaylistDto, youtubePlaylistItemResultList);

        redirectAttributes.addAttribute("playlistId", playlistId);

        return "redirect:/playlist/{playlistId}";
    }




    @GetMapping("/home")
    public String home(Model model,
                       HttpServletRequest request,
                       Principal principal) {

        if(principal != null) log.info("principal.getName() = {}", principal.getName());
/*        String socialId = SecurityUtils.getCurrentMemberId();
        log.info("home socialId = {}", socialId);
        if (socialId.equals("anonymousUser")) {
            log.info("need refresh");
            String returnUrl = "home";
            redirectAttributes.addAttribute("returnUrl", returnUrl);
            return "redirect:/oauth2/refresh?returnUrl={returnUrl}";
        }*/

        String jwtAccessToken = "";
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if(flashMap != null) jwtAccessToken = (String) flashMap.get("token");

        String refreshToken = CookieUtils.getCookie(request, "refresh-token")
                        .map(cookie -> cookie.getValue())
                        .orElse(null);

        model.addAttribute("jwtAccessToken", jwtAccessToken);
        model.addAttribute("refreshToken", refreshToken);

        return "home";
    }
}
