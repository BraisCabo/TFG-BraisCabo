package com.tfg.brais.LTI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class LtiInfo {
    @Value("${lti.clientId:#{null}}")
    private String clientId;
    @Value("${lti.name:#{null}}")
    private String name;
    @Value("${lti.externalPlatform:#{null}}")
    private String externalPlatform;
    @Value("${lti.keySetUrl:#{null}}")
    private String keySetURL;
    @Value("${lti.accessTokenUrl:#{null}}")
    private String accessTokenURL;
    @Value("${lti.oidcAuthUrl:#{null}}")
    private String oidcAuthUrl;
    @Value("${lti.algorithm:#{null}}")
    private String algorithm;
    @Value("${lti.privateKey:#{null}}")
    private String privateKey;
    @Value("${lti.publicKey:#{null}}")
    private String publicKey;
    @Value("${lti.deploymentId:#{null}}")
    private String deploymentId;
}
