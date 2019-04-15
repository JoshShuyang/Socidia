package socidia.middleware_server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import socidia.middleware_server.model.User;
import socidia.middleware_server.model.UserSocialAccountConnection;
import socidia.middleware_server.repository.UserRepository;
import socidia.middleware_server.repository.UserSocialAccountConnectionRepository;
import org.json.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacebookService {
    @Autowired
    ConnectionFactoryRegistry registry;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserSocialAccountConnectionRepository userSocialAccountConnectionRepository;

    public String createFacebookAuthorizationURL(){

        FacebookConnectionFactory connectionFactory = (FacebookConnectionFactory)registry.getConnectionFactory("facebook");
        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("https://localhost:8443/middleware/facebook");
        params.setScope("email");
        return oauthOperations.buildAuthorizeUrl(params);
    }

    public boolean createFacebookAccessToken(String code) {
        FacebookConnectionFactory connectionFactory = (FacebookConnectionFactory)registry.getConnectionFactory("facebook");
        AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, "https://localhost:8443/middleware/facebook", null);
        Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);

        //String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        //User user = userRepository.findByEmail(username).get();
        User user = userRepository.findByEmail("na.yue@sjsu.edu").get();
        String prividerId = connection.getKey().getProviderId();
        String providerUserId = connection.getKey().getProviderUserId();
        String accessToken = accessGrant.getAccessToken();
        String secret = null;
        String refreshToken = null;
        long expireTime = accessGrant.getExpireTime();
        boolean deleted = false;
        UserSocialAccountConnection userSocialAccountConnection = new UserSocialAccountConnection(user, prividerId, providerUserId, accessToken, secret, refreshToken, expireTime, deleted);

        UserSocialAccountConnection res = userSocialAccountConnectionRepository.save(userSocialAccountConnection);

        return res == null ? false : true;
    }

    //curl -i -X DELETE \
    //        "https://graph.facebook.com/v3.2/{facebook_userId}/permissions?access_token={access_token}"

    public boolean revokePermission(String providerId, String providerUserId, String userId) {
        List<UserSocialAccountConnection> userSocialAccountConnectionList = userSocialAccountConnectionRepository.findByProviderUserId(providerId, providerUserId, userId);
        UserSocialAccountConnection target = new UserSocialAccountConnection();
        if (userSocialAccountConnectionList.size() > 0) {
            target = userSocialAccountConnectionList.get(0);
        }
        final String urlPartial = "https://graph.facebook.com/v3.2/{facebook_userId}/permissions";

        Map<String, String> pathVariable = new HashMap<String, String>();
        pathVariable.put("facebook_userId", providerUserId);

        URI uri = UriComponentsBuilder.fromUriString(urlPartial)
                .buildAndExpand(pathVariable)
                .toUri();
        uri = UriComponentsBuilder
                .fromUri(uri)
                .queryParam("access_token", target.getAccessToken())
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        JSONObject obj = new JSONObject(res);

        if (obj.getBoolean("success") ) {
            target.setDeleted(true);
            userSocialAccountConnectionRepository.save(target);
            return true;
        }
        return false;
    }

}
