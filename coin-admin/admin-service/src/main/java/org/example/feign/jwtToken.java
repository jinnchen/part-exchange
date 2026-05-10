package org.example.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class jwtToken {
    @JsonProperty("access_token")
    private String accessToken;
    private String scope;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private String expiresIn;
    private String username;
}
