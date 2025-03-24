package com.fesi6.team1.study_group.domain.user.service;

import com.fesi6.team1.study_group.domain.user.dto.KakaoUserInfoDTO;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.apikey}")
    private String kakaoApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public String getKakaoToken(String code) {
        String accessToken = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(kakaoApiKey);
            sb.append("&redirect_uri=").append(kakaoRedirectUri);
            sb.append("&code=").append(code);

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            BufferedReader br = responseCode >= 200 && responseCode < 300
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder responseSb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseSb.append(line);
            }
            String result = responseSb.toString();

            JsonElement element = JsonParser.parseString(result);
            if (element.getAsJsonObject().has("access_token")) {
                accessToken = element.getAsJsonObject().get("access_token").getAsString();
            } else {
                System.err.println("Error response from Kakao: " + result);
                throw new RuntimeException("Failed to retrieve access token");
            }

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("accessToken = " + accessToken);
        return accessToken;
    }

    public KakaoUserInfoDTO getKakaoUserInfo(String accessToken) {
        KakaoUserInfoDTO userInfoDto = null;
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() >= 200 && conn.getResponseCode() < 300
                            ? conn.getInputStream()
                            : conn.getErrorStream()
            ));

            StringBuilder responseSb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseSb.append(line);
            }
            String result = responseSb.toString();
            System.out.println("Kakao User Info API Response: " + result);

            JsonElement element = JsonParser.parseString(result);
            Long socialId = element.getAsJsonObject().get("id").getAsLong();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            JsonObject profile = kakaoAccount.has("profile") ? kakaoAccount.get("profile").getAsJsonObject() : null;

            String email = kakaoAccount.has("email") ? kakaoAccount.get("email").getAsString() : null;
            String nickname = profile != null && profile.has("nickname") ? profile.get("nickname").getAsString() : null;
            String profileImageUrl = profile != null && profile.has("profile_image_url")
                    ? profile.get("profile_image_url").getAsString()
                    : null;

            userInfoDto = new KakaoUserInfoDTO(socialId, email, nickname, profileImageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfoDto;
    }
}