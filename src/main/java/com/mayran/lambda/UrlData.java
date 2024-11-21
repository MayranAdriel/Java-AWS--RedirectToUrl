package com.mayran.lambda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UrlData {
    String originalUrl;
    long expirationTime;
}
