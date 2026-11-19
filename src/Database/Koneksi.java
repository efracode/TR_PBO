package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Koneksi {

    // Deklarasi Variabel Koneksi
    public static Connection con;
    public static Statement stm;

    // Konfigurasi untuk mmebuka koneksi database
    public void config() {
        try {
            String url = "jdbc:mysql://localhost/db_rental_alat";
            String user = "root";
            String pass = "";

            //Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //Koneksi
            con = DriverManager.getConnection(url, user, pass);
            stm = con.createStatement();

            System.out.println("Koneksi Berhasil!");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Koneksi Gagal!");
            System.out.println(e.getMessage());
        }
    }
}
