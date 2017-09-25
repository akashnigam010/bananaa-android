package in.bananaa.utils.login;

public class LoginDetails {
    private LoginMethod loginMethod;
    private Boolean isLoggedIn = false;
    private Boolean isSkippedLogin = false;

    public LoginDetails(Boolean isSkippedLogin, Boolean isLoggedIn, LoginMethod loginMethod) {
        this.loginMethod = loginMethod;
        this.isLoggedIn = isLoggedIn;
        this.isSkippedLogin = isSkippedLogin;
    }

    public LoginMethod getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(LoginMethod loginMethod) {
        this.loginMethod = loginMethod;
    }

    public Boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Boolean isSkippedLogin() {
        return isSkippedLogin;
    }

    public void setSkippedLogin(Boolean skippedLogin) {
        isSkippedLogin = skippedLogin;
    }
}
