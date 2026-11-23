package Controller;

import Database.Koneksi;
import Model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class UserController {
    public Statement stm;
    public ResultSet res;
    public String sql;

    DefaultTableModel dtm = new DefaultTableModel();
    
    public UserController() {
        Koneksi db = new Koneksi();
        db.config();
        this.stm = db.stm;
    }

    public DefaultTableModel createTable() {
        this.dtm.addColumn("ID");
        this.dtm.addColumn("Username");
        this.dtm.addColumn("Password");
        this.dtm.addColumn("role");
        
        return this.dtm;
    }
    
    public void readUser() {
        try {
            // Persiapkan tabel virtual
            this.dtm.getDataVector().removeAllElements();
            this.dtm.fireTableDataChanged();

            // Query
            this.sql = "SELECT * FROM user";

            // Jalankan
            this.res = this.stm.executeQuery(sql);

            // Masukkan hasil querynya ke tabel virtual
            while (res.next()) {
                Object[] obj = new Object[4];

                // Nama harus sama dengan yang di database
                obj[0] = res.getString("id_user");
                obj[1] = res.getString("username");
                obj[2] = res.getString("password");
                obj[3] = res.getString("role");
                this.dtm.addRow(obj);
            }
        } catch (SQLException e) {
            System.out.println("Gagal query" + e);
        }
    }
    
    public boolean createUser(User user) {
        try {
            this.sql = "INSERT INTO user "
                    + "(id_user, username, password, role) "
                    + "VALUES ("
                    + user.getIdUser() + ", "
                    + "'" + user.getUsername().replace("'", "''") + "', "
                    + "'" + user.getPassword().replace("'", "''") + "', "
                    + "'" + user.getRole().replace("'", "''")
                    + "')";
            this.stm.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Gagal create: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateUser(User user) {
            try {
                this.sql = "UPDATE user "
                        + "SET "
                        + "username = '" + user.getUsername().replace("'", "''") + "', "
                        + "username = '" + user.getPassword().replace("'", "''") + "', "
                        + "role = '" + user.getRole().replace("'", "''")
                        + "' WHERE id_user = " + user.getIdUser();
                this.stm.executeUpdate(sql);
                return true;
            } catch (SQLException e) {
                System.out.println("Gagal update: " + e.getMessage());
                return false;
            }
        }
    
    public boolean deleteUser(int a) {
        User us = new User();
        us.setIdUser(a);

        try {
            this.sql = "DELETE FROM user WHERE id_user = " + us.getIdUser();
            this.stm.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Gagal delete: " + e.getMessage());
            return false;
        }
    }
}
