package com.mypli.myplaylist.service.youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.mypli.myplaylist.dto.youtube.YoutubePlaylistItemsDto;
import com.mypli.myplaylist.dto.youtube.YoutubeSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class YoutubeSearchService {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
    private static YouTube youtube;

    @Value("${app.youtube.apiKey}")
    private String apiKey;

    public List<YoutubeSearchDto> getSearchResult(String queryTerm) {

        List<YoutubeSearchDto> youtubeSearchDtoList = new ArrayList<>();

        try {

            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("my-playlist").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");
            search.setKey(apiKey);
            search.setQ(queryTerm);
            search.setType("video");
            search.setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                makeDtoList(searchResultList.iterator(), youtubeSearchDtoList);
            }

        } catch (GoogleJsonResponseException e) {
            log.error("유튜브 API 서비스에서 에러가 발생했습니다.", e);
        } catch (IOException e) {
            log.error("유튜브 API에서 IOException이 발생했습니다.", e);
        } catch (Throwable t) {
            log.error("유튜브 API에서 에러가 발생했습니다.", t);
        }

        return youtubeSearchDtoList;
    }

    private void makeDtoList(Iterator<SearchResult> iteratorSearchResults, List<YoutubeSearchDto> youtubeSearchDtoList) {

        if (!iteratorSearchResults.hasNext()) log.error("검색 결과가 없습니다.");

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleSearchResult = iteratorSearchResults.next();

            String videoId = singleSearchResult.getId().getVideoId();
            String title = singleSearchResult.getSnippet().getTitle();
            String channelTitle = singleSearchResult.getSnippet().getChannelTitle();
            String thumbnail = singleSearchResult.getSnippet().getThumbnails().getDefault().getUrl();

            YoutubeSearchDto currentItem = YoutubeSearchDto.builder()
                    .videoId(videoId)
                    .title(title)
                    .channelTitle(channelTitle)
                    .thumbnail(thumbnail)
                    .build();

            youtubeSearchDtoList.add(currentItem);
        }
    }
}
