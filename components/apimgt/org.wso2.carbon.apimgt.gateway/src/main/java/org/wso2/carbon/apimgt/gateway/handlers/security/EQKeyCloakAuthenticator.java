package org.wso2.carbon.apimgt.gateway.handlers.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.RESTConstants;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.APIMgtGatewayConstants;
import org.wso2.carbon.apimgt.impl.APIConstants;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EQKeyCloakAuthenticator implements  Authenticator {

    private static final Log log = LogFactory.getLog(EQKeyCloakAuthenticator.class);

    @Override
    public void init(SynapseEnvironment env) {
        log.info("Initializing EQ Key Cloak Authenticator");
    }

    @Override
    public void destroy() {
        log.info("Destroying EQ Key Cloak Authenticator");
    }

    @Override
    public AuthenticationResponse authenticate(MessageContext synCtx) throws APIManagementException {

        AuthenticationResponse  authenticationResponse = new AuthenticationResponse(true, true, false, 0, "success");

        // TODO - implement logic validation here
        if (customValidation(synCtx)) {
            handleAuthentication(synCtx, getAuthorizationHeader(synCtx));
            return authenticationResponse;
        }

        return new AuthenticationResponse(false, false, true,
                APISecurityConstants.API_AUTH_INVALID_CREDENTIALS,
                APISecurityConstants.API_AUTH_INVALID_CREDENTIALS_MESSAGE);
    }


    // TODO
    boolean customValidation(MessageContext synCtx) {
        log.info("EQKeyCloakAuthenticator authenticate started");



        if(synCtx == null) {
            log.info("Authentication Failed, synCtx null");
        } else {
            log.info("Authentication Successful, synCtx not null");
        }
        Map headers = getTransportHeaders(synCtx);
        if(headers != null) {
            log.info("Authentication Successful, headers not null");
        } else {
            log.info("Authentication Failed, headers null");
        }
        String authHeader = getAuthorizationHeader(headers);
        // TODO - implement logic validation here
        if (authHeader.startsWith("userName")) {
            log.info("EQKeyCloakAuthenticator authenticate finished successfully");
            return true;
        }
        log.info("EQKeyCloakAuthenticator authenticate finished failure");
        return false;
    }

    @Override
    public String getChallengeString() {
        return "keycloak";
    }

    @Override
    public String getRequestOrigin() {
        return "keycloak";
    }

    @Override
    public int getPriority() {
        return 99999;
    }

    private Map getTransportHeaders(MessageContext messageContext) {
        return (Map) ((Axis2MessageContext) messageContext).getAxis2MessageContext().
                getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
    }

    private String getAuthorizationHeader(MessageContext messageContext) {
        return getAuthorizationHeader(getTransportHeaders(messageContext));
    }

    private String getAuthorizationHeader(Map headers) {
        if(headers == null) {
            log.info("Header map is null");
        }

        for (Object o : headers.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            log.info("key: " + key + " value: " + value);
        }

        return (String) headers.get("Authorization");
    }

    private void handleAuthentication(MessageContext messageContext, String authHeader) {

        //Using existing constant in Message context removing the additional constant in API Constants
        String clientIP = getClientIP(messageContext);

        //Create a dummy AuthenticationContext object with hard coded values for Tier and KeyType. This is because we cannot determine the Tier nor Key Type without subscription information..
        AuthenticationContext authContext = new AuthenticationContext();
        authContext.setAuthenticated(true);
        authContext.setTier(getTier(messageContext));
        //Since we don't have details on unauthenticated tier we setting stop on quota reach true
        authContext.setStopOnQuotaReach(getStopOnQuotaReach(messageContext));
        //Requests are throttled by the ApiKey that is set here. In an unauthenticated scenario, we will use the client's IP address for throttling.
        authContext.setApiKey(getAPIKey(messageContext));
        authContext.setKeyType(APIConstants.API_KEY_TYPE_PRODUCTION);
        //This name is hardcoded as anonymous because there is no associated user token
        authContext.setUsername(getUserNameFromToken(authHeader));
        authContext.setCallerToken(null);
        authContext.setApplicationName(null);
        authContext.setApplicationId(getClientIP(messageContext)); //Set clientIp as application ID in unauthenticated scenario
        authContext.setConsumerKey(null);

        Set<String> scopes = getScopesFromToken(authHeader);
        messageContext.setProperty(APIMgtGatewayConstants.SCOPES, scopes.toString());
        APISecurityUtils.setAuthenticationContext(messageContext, authContext, null);
    }

    // TODO
    boolean getStopOnQuotaReach(MessageContext messageContext) {
        return false;
    }

    //TODO
    String getAPIKey(MessageContext messageContext) {
        return getClientIP(messageContext);
    }

    // TODO
    String getTier(MessageContext messageContext) {
        return APIConstants.UNLIMITED_TIER;
    }

    // TODO
    String getClientIP(MessageContext messageContext) {
        String clientIP = "192.168.1.1";
        return clientIP;
    }
    // TODO
    boolean validateToken(String token) {
        return true;
    }

    // TODO
    boolean validateAuthorization(String token, MessageContext synCtx) {

        String apiContext = (String) synCtx.getProperty(RESTConstants.REST_API_CONTEXT);
        String apiVersion = (String) synCtx.getProperty(RESTConstants.SYNAPSE_REST_API_VERSION);
        return true;
    }

    // TODO
    String getUserNameFromToken(String token) {
        return APIConstants.END_USER_ANONYMOUS;
    }

    // TODO
    Set<String> getScopesFromToken(String token) {
        Set<String> scopes = new HashSet<String>();
        scopes.add("public-user");
        return scopes;
    }
}
