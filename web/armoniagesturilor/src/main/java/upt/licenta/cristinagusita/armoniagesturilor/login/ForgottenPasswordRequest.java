package upt.licenta.cristinagusita.armoniagesturilor.login;

public class ForgottenPasswordRequest {
    private String email;
    private String password;
    private String confirmPassword;

    public ForgottenPasswordRequest() {
    }

    public ForgottenPasswordRequest(String email, String password, String confirmPassword) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
