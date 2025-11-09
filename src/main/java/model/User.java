package model;

public class User {
    private int id;
    private String username;
    private String encryptedPassword;
    private String email;

    public User() {}

    public User(String username, String encryptedPassword, String email) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}