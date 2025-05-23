package com.fesi6.team1.study_group.global.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseAnonKey;

    @Value("${supabase.storage.bucket.profile}")
    private String profileBucket;
    @Value("${supabase.storage.bucket.meetup}")
    private String meetupBucket;
    @Value("${supabase.storage.bucket.review}")
    private String reviewBucket;

    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadProfileImage(MultipartFile file) throws Exception {
        return uploadFile(file, profileBucket);
    }
    public String uploadMeetupImage(MultipartFile file) throws Exception {
        return uploadFile(file, meetupBucket);
    }
    public String uploadReviewImage(MultipartFile file) throws Exception {
        return uploadFile(file, reviewBucket);
    }

    public String uploadFile(MultipartFile file, String bucket) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String fileName = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        
        if (originalFilename != null && !originalFilename.trim().isEmpty()) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                extension = originalFilename.substring(lastDotIndex);
            }
        }
        
        fileName = fileName + (extension.isEmpty() ? ".tmp" : extension);

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replace("+", "%20");
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + encodedFileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseAnonKey);
        headers.set("apikey", supabaseAnonKey);
        
        // ContentType이 null일 경우 기본값 설정
        String contentType = file.getContentType();
        headers.setContentType(MediaType.valueOf(contentType != null ? contentType : "application/octet-stream"));
        headers.set("Cache-Control", "3600");
        headers.set("x-upsert", "true");  // 동일한 경로에 파일이 있을 경우 덮어쓰기

        HttpEntity<byte[]> entity = new HttpEntity<>(file.getBytes(), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + encodedFileName;
        } else {
            throw new RuntimeException("Supabase Storage 업로드 실패: " + response.getStatusCode());
        }
    }

    public void deleteFile(String fileUrl) {
        // fileUrl 예시: https://.../storage/v1/object/public/bucket/filename
        String prefix = supabaseUrl + "/storage/v1/object/public/";
        if (!fileUrl.startsWith(prefix)) return;
        String path = fileUrl.substring(prefix.length());
        String url = supabaseUrl + "/storage/v1/object/" + path;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseAnonKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }

    public String getPublicUrl(String bucket, String fileName) {
        if (fileName == null || fileName.isEmpty()) return null;
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + encodedFileName;
        } catch (Exception e) {
            throw new RuntimeException("파일 이름 인코딩 실패", e);
        }
    }
} 