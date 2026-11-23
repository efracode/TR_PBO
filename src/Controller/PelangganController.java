package Controller;

import Database.Koneksi;
import Model.Pelanggan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;


public class PelangganController {
    public Statement stm;
    public ResultSet res;
    public String sql;

    DefaultTableModel dtm = new DefaultTableModel();
    
    public PelangganController() {
        Koneksi db = new Koneksi();
        db.config();
        this.stm = db.stm;
    }
    
    public DefaultTableModel createTable() {
        this.dtm.addColumn("ID");
        this.dtm.addColumn("Nama Pelanggan");
        this.dtm.addColumn("Nomor HP");
        return this.dtm;
    }
    
    public void readPelanggan() {
        try {
            // Persiapkan tabel virtual
            this.dtm.getDataVector().removeAllElements();
            this.dtm.fireTableDataChanged();

            // Query
            this.sql = "SELECT * FROM pelanggan";

            // Jalankan
            this.res = this.stm.executeQuery(sql);

            // Masukkan hasil querynya ke tabel virtual
            while (res.next()) {
                Object[] obj = new Object[3];

                // Nama harus sama dengan yang di database
                obj[0] = res.getString("id_pelanggan");
                obj[1] = res.getString("nama");
                obj[2] = res.getString("no_hp");
                this.dtm.addRow(obj);
            }
        } catch (SQLException e) {
            System.out.println("Gagal query" + e);
        }
    }
        
        public boolean createPelanggan(Pelanggan pelanggan) {
            try {
                this.sql = "INSERT INTO pelanggan "
                        + "(id_pelanggan, nama, no_hp) "
                        + "VALUES ("
                        + pelanggan.getIdPelanggan() + ", "
                        + "'" + pelanggan.getNama().replace("'", "''") + "', "
                        + "'" + pelanggan.getNoHp().replace("'", "''")
                        + "')";
                this.stm.executeUpdate(sql);
                return true;
            } catch (SQLException e) {
                System.out.println("Gagal create: " + e.getMessage());
                return false;
            }
        }
        
        public boolean updatePelanggan(Pelanggan pelanggan) {
            try {
                this.sql = "UPDATE pelanggan "
                        + "SET "
                        + "nama = '" + pelanggan.getNama().replace("'", "''") + "', "
                        + "no_hp = '" + pelanggan.getNoHp().replace("'", "''")
                        + "' WHERE id_pelanggan = " + pelanggan.getIdPelanggan();
                this.stm.executeUpdate(sql);
                return true;
            } catch (SQLException e) {
                System.out.println("Gagal update: " + e.getMessage());
                return false;
            }
        }
        
        public boolean deletePelanggan(int a) {
            Pelanggan pel = new Pelanggan();
            pel.setIdPelanggan(a);

            try {
                this.sql = "DELETE FROM pelanggan WHERE id_pelanggan = " + pel.getIdPelanggan();
                this.stm.executeUpdate(sql);
                return true;
            } catch (SQLException e) {
                System.out.println("Gagal delete: " + e.getMessage());
                return false;
            }
        }
    }

