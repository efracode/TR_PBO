package Model;

public class User {

    // Data user
    private int idUser;
    private String username;
    private String password;
    private String role;

    // Constructor kosong
    public User() {
    }

    // Constructor lengkap
    public User(int idUser, String username, String password, String role) {
        this.idUser = idUser;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter & Setter ID
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    // Getter & Setter username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter & Setter password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter & Setter role
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}