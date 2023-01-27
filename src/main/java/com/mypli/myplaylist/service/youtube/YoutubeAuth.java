package com.mypli.myplaylist.service.youtube;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

@Slf4j
public class YoutubeAuth {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static Credential authorize(String accessToken) throws IOException {
        return new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
    }

    public static Credential authorize2(List<String> scopes) throws Exception {

        //List<String> scopes = Lists.newArrayList("https://www.googleapis.com/youtube/v3/playlists");

        //TODO: 이 authorize가 어떻게 작동하는지 알아봐야함
        // Load client secrets.
        Reader clientSecretReader = new InputStreamReader(YoutubeAuth.class.getResourceAsStream("/client_secrets.json"));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientId().startsWith("Enter ")) {
            log.error("Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential"
                    + "into myplaylist/src/main/resources/client_secrets.json");
            return null;
        }

        // Set up file credential store.
        FileCredentialStore credentialStore = new FileCredentialStore(
                new File(System.getProperty("user.home"), ".credentials/youtube-api.json"),
                JSON_FACTORY);

        // Set up authorization code flow.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialStore(credentialStore)
                .build();

        // Build the local server and bind it to port 9000
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(9000).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }
}