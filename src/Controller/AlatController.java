package Controller;

import Database.Koneksi;
import Model.Alat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

public class AlatController {
    public Statement stm;
    public ResultSet res;
    public String sql;

    DefaultTableModel dtm = new DefaultTableModel();

    public AlatController() {
        Koneksi db = new Koneksi();
        db.config();
        this.stm = db.stm;
    }
    
    public DefaultTableModel createTable() {
        this.dtm.addColumn("ID");
        this.dtm.addColumn("Kode Alat");
        this.dtm.addColumn("Nama Alat");
        this.dtm.addColumn("kategori");
        this.dtm.addColumn("Harga Sewa (per hari)");
        this.dtm.addColumn("Stok");
        return this.dtm;
    }
    
    public void readAlat() {
        try {
            // Persiapkan tabel virtual
            this.dtm.getDataVector().removeAllElements();
            this.dtm.fireTableDataChanged();

            // Query
            this.sql = "SELECT * FROM alat";

            // Jalankan
            this.res = this.stm.executeQuery(sql);

            // Masukkan hasil querynya ke tabel virtual
            while (res.next()) {
                Object[] obj = new Object[6];

                // Nama harus sama dengan yang di database
                obj[0] = res.getString("id_alat");
                obj[1] = res.getString("kode_alat");
                obj[2] = res.getString("nama_alat");
                obj[3] = res.getString("kategori");
                obj[4] = res.getString("harga_sewa_per_hari");
                obj[5] = res.getString("stok");
                this.dtm.addRow(obj);
            }
        } catch (SQLException e) {
            System.out.println("Gagal query" + e);
        }
    }
    
    public boolean createAlat (Alat alat){
        try{
            this.sql = "INSERT INTO alat "
                    + "(id_alat, kode_alat, nama_alat, kategori, harga_sewa_per_hari, stok) "
                    + "VALUES ("
                    + alat.getIdAlat() + ", "
                    + "'" + alat.getKodeAlat().replace("'", "''") + "', "
                    + "'" + alat.getNamaAlat().replace("'", "''") + "', "
                    + "'" + alat.getKategori().replace("'", "''") + "', "
                    + alat.getHargaSewa() + ", "
                    + alat.getStok()
                    + ")";
                    this.stm.executeUpdate(sql);
                    return true;
        }catch (SQLException e){
            System.out.println("Gagal create: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateAlat (Alat alat){
        try{
            this.sql = "UPDATE alat "
                    + "SET "
                    + "kode_alat = '" + alat.getKodeAlat().replace("'", "''") + "', "
                    + "nama_alat = '" + alat.getNamaAlat().replace("'", "''") + "', "
                    + "kategori = '" + alat.getKategori().replace("'", "''") + "', "
                    + "harga_sewa_per_hari = " + alat.getHargaSewa() + ", "
                    + "stok = " + alat.getStok() + " "
                    + "WHERE id_alat = " + alat.getIdAlat();
                    this.stm.executeUpdate(sql);
                    return true;
        }catch (SQLException e){
            System.out.println("Gagal update: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteAlat (int a){
        Alat al = new Alat();
        al.setIdAlat(a);
        
        try {
            this.sql = "DELETE FROM alat WHERE id_alat = " + al.getIdAlat();
            this.stm.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println("Gagal delete: " + e.getMessage());
            return false;
        }
    }
    
    
}
