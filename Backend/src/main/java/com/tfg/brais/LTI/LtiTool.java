package com.tfg.brais.LTI;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uoc.elc.lti.tool.Deployment;
import edu.uoc.elc.lti.tool.Key;
import edu.uoc.elc.lti.tool.KeySet;
import edu.uoc.elc.lti.tool.Registration;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.ToolBuilders;
import edu.uoc.elc.lti.tool.Deployment.DeploymentBuilder;
import edu.uoc.elc.lti.tool.Key.KeyBuilder;
import edu.uoc.elc.lti.tool.KeySet.KeySetBuilder;
import edu.uoc.elc.lti.tool.Registration.RegistrationBuilder;
import edu.uoc.lti.accesstoken.AccessTokenRequestBuilder;
import edu.uoc.lti.accesstoken.JSONAccessTokenRequestBuilderImpl;
import edu.uoc.lti.claims.ClaimAccessor;
import edu.uoc.lti.clientcredentials.ClientCredentialsTokenBuilder;
import edu.uoc.lti.deeplink.DeepLinkingTokenBuilder;
import edu.uoc.lti.jwt.claims.JWSClaimAccessor;
import edu.uoc.lti.jwt.client.JWSClientCredentialsTokenBuilder;
import edu.uoc.lti.jwt.deeplink.JWSTokenBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LtiTool {

    @Autowired
    private LtiInfo ltiInfo;

    private MyOidcLaunchSession oidcLaunchSession;
    private Registration registration;
    private ToolBuilders toolBuilders;
    private ClaimAccessor claimAccessor;
    private String kid;
    private String state;
    private String nonce;
    private Tool tool;

    @PostConstruct
    public void init() {
        this.registration = generateRegistration();
        this.toolBuilders = getBuilders();
        this.claimAccessor = generateClaimAccessor();
        this.oidcLaunchSession = new MyOidcLaunchSession();
        this.kid = "id";
    }

    private ToolBuilders getBuilders() {
        final Key key = registration.getKeySet().getKeys().get(0);
        DeepLinkingTokenBuilder deepLinkingTokenBuilder = new JWSTokenBuilder(key.getPublicKey(), key.getPrivateKey(),
                key.getAlgorithm());
        ClientCredentialsTokenBuilder clientCredentialsTokenBuilder = new JWSClientCredentialsTokenBuilder(
                key.getPublicKey(), key.getPrivateKey(), key.getAlgorithm());
        AccessTokenRequestBuilder accessTokenRequestBuilder = new JSONAccessTokenRequestBuilderImpl();
        return new ToolBuilders(clientCredentialsTokenBuilder, accessTokenRequestBuilder, deepLinkingTokenBuilder);
    }

    public void setState(String state) {
        try {
            oidcLaunchSession.clear();
            this.state = state;
        } catch (Exception e) {
            this.state = state;
        }
    }

    public void setNonce(String nonce) {
        try {
            oidcLaunchSession.clear();
            this.nonce = nonce;
        } catch (Exception e) {
            this.nonce = nonce;
        }
    }

    private Registration generateRegistration() {
        RegistrationBuilder builder = Registration.builder();
        builder.id("1");
        builder.clientId(this.ltiInfo.getClientId());
        builder.name(this.ltiInfo.getName());
        builder.platform(this.ltiInfo.getExternalPlatform());
        builder.keySetUrl(this.ltiInfo.getKeySetURL());
        builder.accessTokenUrl(this.ltiInfo.getAccessTokenURL());
        builder.oidcAuthUrl(this.ltiInfo.getOidcAuthUrl());
        KeyBuilder keyBuilder = Key.builder();
        keyBuilder.algorithm("RSA");
        keyBuilder.privateKey(this.ltiInfo.getPrivateKey());
        keyBuilder.publicKey(this.ltiInfo.getPublicKey());
        keyBuilder.id("id");
        List<Key> keys = new ArrayList<>();
        keys.add(keyBuilder.build());
        KeySetBuilder keySetBuilder = KeySet.builder();
        keySetBuilder.keys(keys);
        keySetBuilder.id("default");
        keyBuilder.id("default");
        builder.keySet(keySetBuilder.build());
        DeploymentBuilder deploymentBuilder = Deployment.builder();
        deploymentBuilder.deploymentId(this.ltiInfo.getDeploymentId());
        List<Deployment> deployments = new ArrayList<>();
        deployments.add(deploymentBuilder.build());
        builder.deployments(deployments);
        return builder.build();
    }

    private ClaimAccessor generateClaimAccessor() {
        return new JWSClaimAccessor(this.ltiInfo.getKeySetURL());
    }

    private MyOidcLaunchSession generateLaunchSession(HttpServletRequest request) {
        MyOidcLaunchSession session = new MyOidcLaunchSession();
        session.init(request);
        session.setDeploymentId(this.ltiInfo.getDeploymentId());
        session.setClientId(this.ltiInfo.getClientId());
        session.setTargetLinkUri(request.getRequestURL().toString());
        session.setState(state);
        session.setNonce(nonce);
        return session;
    }

    public Tool getTool(HttpServletRequest request) {
        this.oidcLaunchSession = generateLaunchSession(request);
        this.tool = new Tool(registration, claimAccessor, oidcLaunchSession, toolBuilders, kid);
        return this.tool;
    }

}
