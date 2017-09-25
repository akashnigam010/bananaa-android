package in.bananaa.utils.login;

public enum LoginMethod {
    FACEBOOK("facebook"),
    GOOGLE("google");

    private String name;

    private LoginMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
