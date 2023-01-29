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
import com.mypli.myplaylist.dto.MusicDto;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemsDto;
import com.mypli.myplaylist.repository.MemberRepository;
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
public class YoutubePlaylistItemsService {

    private final MemberRepository memberRepository;

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static YouTube youtube;

    @Value("${app.youtube.clientId}") private String CLIENT_ID;
    @Value("${app.youtube.clientSecret}") private String CLIENT_SECRET;
    @Value("${app.youtube.tokenUri}") private String TOKEN_URI;

    public List<YoutubePlaylistItemsDto> getPlaylistItems(String socialId, String youtubePlaylistId) {

        List<YoutubePlaylistItemsDto> youtubePlaylistItemsDtoList = new ArrayList<>();

        try {

            Member member = memberRepository.findBySocialId(socialId);
            String accessToken = "";
            String refreshToken = "";
            if (member != null) {
                accessToken = member.getSocialAccessToken();
                refreshToken = member.getSocialRefreshToken();
            } else {
                log.error("Cannot find member from JwtAccessToken");
                return youtubePlaylistItemsDtoList;
            }

            Credential credential = authorize(accessToken, refreshToken);

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("my-playlist").build();

            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list("snippet");
            playlistItemsRequest.setPlaylistId(youtubePlaylistId);
            playlistItemsRequest.setFields("nextPageToken," +
                    "items(snippet/title,snippet/videoOwnerChannelTitle,snippet/resourceId/videoId,snippet/thumbnails/default/url)");

            String nextToken = "";
            List<PlaylistItem> playlistItemList = new ArrayList<>();
            do {
                playlistItemsRequest.setPageToken(nextToken);
                PlaylistItemListResponse playlistItemsResult = playlistItemsRequest.execute();

                playlistItemList.addAll(playlistItemsResult.getItems());

                nextToken = playlistItemsResult.getNextPageToken();
            } while (nextToken != null);

            if (playlistItemList != null) {
                makeDtoList(playlistItemList.iterator(), youtubePlaylistItemsDtoList);
            }

        } catch (GoogleJsonResponseException e) {
            log.error("유튜브 API 서비스 에러가 발생했습니다.", e);
        } catch (IOException e) {
            log.error("유튜브 API에서 IOException이 발생했습니다.", e);
        } catch (Throwable t) {
            log.error("유튜브 API에서 에러가 발생했습니다.", t);
        }

        return youtubePlaylistItemsDtoList;
    }

    public String insertPlaylistItem(String socialId, String youtubePlaylistId, MusicDto musicDto) throws IOException {

        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(musicDto.getVideoId());

        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setTitle(musicDto.getTitle());
        playlistItemSnippet.setPlaylistId(youtubePlaylistId);
        playlistItemSnippet.setResourceId(resourceId);

        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        YouTube.PlaylistItems.Insert playlistItemsInsertCommand = youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
        PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();

        return returnedPlaylistItem.getId();
    }

    private void makeDtoList(Iterator<PlaylistItem> iteratorPlaylistItems, List<YoutubePlaylistItemsDto> youtubePlaylistItemsDtoList) {

        if (!iteratorPlaylistItems.hasNext()) log.error("검색 결과가 없습니다.");

        while (iteratorPlaylistItems.hasNext()) {

            PlaylistItem singlePlaylistItem = iteratorPlaylistItems.next();

            String title = singlePlaylistItem.getSnippet().getTitle();
            String artist = (String) singlePlaylistItem.getSnippet().get("videoOwnerChannelTitle");
            String videoId = singlePlaylistItem.getSnippet().getResourceId().getVideoId();
            String thumbnail = singlePlaylistItem.getSnippet().getThumbnails().getDefault().getUrl();

            YoutubePlaylistItemsDto currentItem = YoutubePlaylistItemsDto.builder()
                    .videoId(videoId)
                    .title(title)
                    .artist(artist)
                    .thumbnail(thumbnail)
                    .build();

            youtubePlaylistItemsDtoList.add(currentItem);
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

    /*public String get(String socialId, String playlistId, List<YoutubePlaylistItemsDto> youtubePlaylistItemsDtoList, String pageToken) {

        String nextPageToken = "";

        try {

            Member member = memberRepository.findBySocialId(socialId);
            String accessToken = "";
            if(member != null) accessToken = member.getSocialAccessToken();
            else {
                log.error("Cannot find member from JwtAccessToken");
                return "";
            }

            Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName("youtube-playlist-items-get").build();

            YouTube.PlaylistItems.List playlistItems = youtube.playlistItems().list("snippet");
            playlistItems.setPlaylistId(playlistId);
            playlistItems.setPageToken(pageToken);
            playlistItems.setFields("nextPageToken," +
                    "items(kind,snippet/title,snippet/videoOwnerChannelTitle,snippet/resourceId/videoId,snippet/thumbnails/default/url)");

            PlaylistItemListResponse response = playlistItems.execute();
            List<PlaylistItem> playlistItemList = response.getItems();
            nextPageToken = response.getNextPageToken();

            if (playlistItemList != null) {
                makeDtoList(playlistItemList.iterator(), youtubePlaylistItemsDtoList);
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

    private void makeDtoList(Iterator<PlaylistItem> iteratorSearchResults, List<YoutubePlaylistItemsDto> youtubePlaylistItemsDtoList) {

        if (!iteratorSearchResults.hasNext()) log.error("검색 결과가 없습니다.");

        while (iteratorSearchResults.hasNext()) {

            PlaylistItem singlePlaylistItem = iteratorSearchResults.next();

            log.info("singlePlaylistItem = {}", singlePlaylistItem);

            if (singlePlaylistItem.getKind().equals("youtube#playlistItem")) {
                String title = singlePlaylistItem.getSnippet().getTitle();
                String artist = (String) singlePlaylistItem.getSnippet().get("videoOwnerChannelTitle");
                String videoId = singlePlaylistItem.getSnippet().getResourceId().getVideoId();
                String thumbnail = singlePlaylistItem.getSnippet().getThumbnails().getDefault().getUrl();

                YoutubePlaylistItemsDto currentItem = YoutubePlaylistItemsDto.builder()
                        .title(title)
                        .artist(artist)
                        .videoId(videoId)
                        .thumbnail(thumbnail)
                        .build();

                youtubePlaylistItemsDtoList.add(currentItem);

            }
        }
    }*/
}
