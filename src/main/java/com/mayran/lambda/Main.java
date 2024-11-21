package com.mayran.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.HashMap;
import java.util.Map;

public class Main implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final S3Client s3CLient = S3Client.builder().build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        String pathParameters = input.get("rawPath").toString();
        String shortUrlCode = pathParameters.replace("/", "");

        if(shortUrlCode == null || shortUrlCode.isEmpty()){
            throw new IllegalArgumentException("Input invalido! shortUrlCode é vazio.");
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket("url-shortener-lambda-mayran")
                .key(shortUrlCode + ".json")
                .build();

        ResponseInputStream<GetObjectResponse> s3ClientObject;

        try {
            s3ClientObject = s3CLient.getObject(request);
        }
        catch (RuntimeException e){
            throw new RuntimeException("Erro ao capturar objeto do S3" + e.getMessage() + e);
        }

        UrlData urlData;

        try {
            urlData = objectMapper.readValue(s3ClientObject, UrlData.class);
        }
        catch (Exception e){
            throw new RuntimeException("Erro ao transformar valor" + e.getMessage() + e);
        }

        long currentTime = System.currentTimeMillis() / 1000;

        if(currentTime < urlData.getExpirationTime()){
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 302);
            Map<String, Object> headers = new HashMap<>();
            headers.put("Location", urlData.getOriginalUrl());
            response.put("headers", headers);

            return response;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 410);
        response.put("body", "A URL expirou");

        return response;
    }
}