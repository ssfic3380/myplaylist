package com.mypli.myplaylist.service.youtube;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.dto.PlaylistDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistDto;
import com.mypli.myplaylist.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubePlaylistsService {

    private final MemberService memberService;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube youtube;

    @Value("${app.youtube.clientId}") private String CLIENT_ID;
    @Value("${app.youtube.clientSecret}") private String CLIENT_SECRET;
    @Value("${app.youtube.tokenUri}") private String TOKEN_URI;

    public List<YoutubePlaylistDto> getPlaylists(String socialId) {

        List<YoutubePlaylistDto> youtubePlaylistDtoList = new ArrayList<>();

        try {

            Member member = memberService.findBySocialId(socialId);
            String accessToken = "";
            String refreshToken = "";
            if (member != null) {
                accessToken = member.getSocialAccessToken();
                refreshToken = member.getSocialRefreshToken();
            } else {
                log.error("Cannot find member from JwtAccessToken");
                return youtubePlaylistDtoList;
            }

            log.info("[{}] Fetching Youtube Playlists", socialId);

            Credential credential = authorize(accessToken, refreshToken);

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("my-playlist").build();

            YouTube.Playlists.List playlistsRequest = youtube.playlists().list("snippet");
            playlistsRequest.setMine(true);
            playlistsRequest.setFields("nextPageToken," +
                    "items(id,snippet/title,snippet/thumbnails/standard/url)");

            String nextToken = "";
            List<Playlist> playlistsList = new ArrayList<>();
            do {
                playlistsRequest.setPageToken(nextToken);
                PlaylistListResponse playlistResult = playlistsRequest.execute();

                playlistsList.addAll(playlistResult.getItems());

                nextToken = playlistResult.getNextPageToken();
            } while (nextToken != null);

            if (playlistsList != null) {
                makeDtoList(playlistsList.iterator(), youtubePlaylistDtoList);
            }

        } catch (GoogleJsonResponseException e) {
            log.error("유튜브 API 서비스 에러가 발생했습니다.", e);
        } catch (IOException e) {
            log.error("유튜브 API에서 IOException이 발생했습니다.", e);
        } catch (Throwable t) {
            log.error("유튜브 API에서 에러가 발생했습니다.", t);
        }

        return youtubePlaylistDtoList;
    }

    public String insertPlaylist(String socialId, PlaylistDto playlistDto) throws IOException {

        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(playlistDto.getPlaylistName());
        playlistSnippet.setDescription("마플리(My Playlist)에서 만든 플레이리스트");

        PlaylistStatus playlistStatus = new PlaylistStatus();
        playlistStatus.setPrivacyStatus("private");

        Playlist youtubePlaylist = new Playlist();
        youtubePlaylist.setSnippet(playlistSnippet);
        youtubePlaylist.setStatus(playlistStatus);

        YouTube.Playlists.Insert playlistInsertCommand = youtube.playlists().insert("snippet,status", youtubePlaylist);
        Playlist playlistInserted = playlistInsertCommand.execute();

        return playlistInserted.getId();
    }

    private void makeDtoList(Iterator<Playlist> iteratorPlaylists, List<YoutubePlaylistDto> youtubePlaylistDtoList) {

        if (!iteratorPlaylists.hasNext()) log.error("검색 결과가 없습니다.");

        while (iteratorPlaylists.hasNext()) {

            Playlist singlePlaylist = iteratorPlaylists.next();

            String playlistId = singlePlaylist.getId();
            String title = singlePlaylist.getSnippet().getTitle();
            String thumbnail = singlePlaylist.getSnippet().getThumbnails().getStandard().getUrl();

            YoutubePlaylistDto currentItem = YoutubePlaylistDto.builder()
                    .playlistId(playlistId)
                    .title(title)
                    .thumbnail(thumbnail)
                    .build();

            youtubePlaylistDtoList.add(currentItem);
        }
    }

    private Credential authorize(String accessToken, String refreshToken) {

        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(JSON_FACTORY)
                .setTransport(HTTP_TRANSPORT)
                .setClientAuthentication(new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET))
                .setTokenServerEncodedUrl(TOKEN_URI)
                .build();

        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);

        return credential;
    }

    /*public String get(String socialId, List<YoutubePlaylistDto> youtubePlaylistDtoList, String pageToken) {

        String nextPageToken = "";

        try {

            Member member = memberService.findBySocialId(socialId);
            String accessToken = "";
            if(member != null) accessToken = member.getSocialAccessToken();
            else {
                log.error("Cannot find member from JwtAccessToken");
                return "";
            }

            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("youtube-playlist-get").build();

            YouTube.Playlists.List playlists = youtube.playlists().list("snippet");
            playlists.setMine(true);
            playlists.setPageToken(pageToken);
            playlists.setFields("nextPageToken," +
                    "items(kind,id,snippet/title,snippet/thumbnails/default/url)");

            PlaylistListResponse response = playlists.execute();
            List<Playlist> playlistList = response.getItems();
            nextPageToken = response.getNextPageToken();

            if (playlistList != null) {
                makeDtoList(playlistList.iterator(), youtubePlaylistDtoList);
            }

        } catch (GoogleJsonResponseException e) {
            log.error("유튜브 API 서비스 에러가 발생했습니다.", e);
        } catch (IOException e) {
            log.error("유튜브 API에서 IOException이 발생했습니다.", e);
        } catch (Throwable t) {
            log.error("유튜브 API에서 에러가 발생했습니다.", t);
        }

        return nextPageToken;
    }

    private void makeDtoList(Iterator<Playlist> iteratorSearchResults, List<YoutubePlaylistDto> youtubePlaylistDtoList) {

        if (!iteratorSearchResults.hasNext()) log.error("검색 결과가 없습니다.");

        while (iteratorSearchResults.hasNext()) {

            Playlist singlePlaylist = iteratorSearchResults.next();

            log.info("singlePlaylist = {}", singlePlaylist);

            if (singlePlaylist.getKind().equals("youtube#playlist")) {
                String id = singlePlaylist.getId();
                String title = singlePlaylist.getSnippet().getTitle();
                String thumbnail = singlePlaylist.getSnippet().getThumbnails().getDefault().getUrl();

                YoutubePlaylistDto currentItem = YoutubePlaylistDto.builder()
                        .id(id)
                        .title(title)
                        .thumbnail(thumbnail)
                        .build();

                youtubePlaylistDtoList.add(currentItem);
            }
        }
    }*/
}
