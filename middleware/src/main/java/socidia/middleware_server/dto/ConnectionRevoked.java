package socidia.middleware_server.dto;

public class ConnectionRevoked {
   private String providerId;
   private String providerUserId;
   private String userId;

    public ConnectionRevoked() {
    }

    public ConnectionRevoked(String providerId, String providerUserId, String userId) {
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
