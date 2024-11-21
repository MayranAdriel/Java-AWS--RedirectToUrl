package com.mayran.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.Map;

public class Main implements RequestHandler<Map<String, Object>, Map<String, String>> {

    private final S3Client s3CLient = S3Client.builder().build();

    @Override
    public Map<String, String> handleRequest(Map<String, Object> input, Context context) {

        String pathParameters = input.get("rawPath").toString();
        String shortUrlCode = pathParameters.replace("/", "");

        if(shortUrlCode == null || shortUrlCode.isEmpty()){
            throw new IllegalArgumentException("Input invalido! shortUrlCode é vazio.");
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket("url-shortener-lambda-mayran")
                .key(shortUrlCode + ".json")
                .build();

        try {
            ResponseInputStream<GetObjectResponse> s3ClientObject = s3CLient.getObject(request);
        }
        catch (RuntimeException e){
            throw new RuntimeException("Erro ao capturar objeto do S3" + e.getMessage() + e);
        }

        return Map.of();
    }
}