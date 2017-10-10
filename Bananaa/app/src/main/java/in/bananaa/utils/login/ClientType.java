package in.bananaa.utils.login;

public enum ClientType {
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK");
    private String client;

    private ClientType(String client) {
        this.client = client;
    }

    public String getClient() {
        return client;
    }
}
