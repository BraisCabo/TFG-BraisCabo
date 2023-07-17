package com.tfg.brais.LTI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class LtiInfo {
    @Value("${lti.clientId}")
    private String clientId;
    @Value("${lti.name}")
    private String name;
    @Value("${lti.externalPlatform}")
    private String externalPlatform;
    @Value("${lti.keySetUrl}")
    private String keySetURL;
    @Value("${lti.accessTokenUrl}")
    private String accessTokenURL;
    @Value("${lti.oidcAuthUrl}")
    private String oidcAuthUrl;
    @Value("${lti.algorithm}")
    private String algorithm;
    @Value("${lti.privateKey}")
    private String privateKey;
    @Value("${lti.publicKey}")
    private String publicKey;
    @Value("${lti.deploymentId}")
    private String deploymentId;
}
