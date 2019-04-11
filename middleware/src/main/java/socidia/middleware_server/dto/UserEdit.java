package socidia.middleware_server.dto;

import javax.validation.constraints.Email;

public class UserEdit {
    private String username;
    private String email;
    private String password;
    private String phoneNum;

    public UserEdit() {
    }

    public UserEdit(String username, @Email String email, String password, String phoneNum) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
