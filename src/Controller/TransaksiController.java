package Controller;

import Database.Koneksi;
import Model.Keranjang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

public class TransaksiController {

    private Connection getConnection() {
        Koneksi db = new Koneksi();
        db.config();
        return Koneksi.con;
    }

    public void readRiwayatTransaksi(DefaultTableModel dtm, String keyword) {
        dtm.setRowCount(0);
        Connection con = getConnection();
        if (con == null) {
            return;
        }

        String sql = "SELECT p.id_penyewaan, pl.nama, p.tanggal_sewa, p.tanggal_rencana_kembali, "
                + "p.tanggal_kembali, p.status, p.total_biaya, p.total_denda, "
                + "p.metode_pembayaran, p.jumlah_bayar, p.kembalian "
                + "FROM penyewaan p "
                + "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan "
                + "WHERE (? = '' OR pl.nama LIKE ? OR p.status LIKE ? OR CAST(p.id_penyewaan AS CHAR) LIKE ?) "
                + "ORDER BY p.id_penyewaan DESC";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            String kw = keyword == null ? "" : keyword.trim();
            String like = "%" + kw + "%";
            pst.setString(1, kw);
            pst.setString(2, like);
            pst.setString(3, like);
            pst.setString(4, like);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    dtm.addRow(new Object[]{
                        rs.getInt("id_penyewaan"),
                        rs.getString("nama"),
                        rs.getString("tanggal_sewa"),
                        rs.getString("tanggal_rencana_kembali"),
                        rs.getString("tanggal_kembali") == null ? "-" : rs.getString("tanggal_kembali"),
                        rs.getString("status"),
                        rs.getDouble("total_biaya"),
                        rs.getDouble("total_denda"),
                        rs.getString("metode_pembayaran"),
                        rs.getDouble("jumlah_bayar"),
                        rs.getDouble("kembalian")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal readRiwayatTransaksi: " + e.getMessage());
        }
    }

    public void readDetailTransaksi(DefaultTableModel dtm, int idPenyewaan) {
        dtm.setRowCount(0);
        Connection con = getConnection();
        if (con == null) {
            return;
        }

        String sql = "SELECT a.kode_alat, a.nama_alat, dp.jumlah_sewa, dp.harga_snapshot, "
                + "COALESCE(dk.jumlah_kembali, 0) AS jumlah_kembali, "
                + "COALESCE(dk.kondisi, '-') AS kondisi, "
                + "COALESCE(dk.catatan, '-') AS catatan "
                + "FROM detail_penyewaan dp "
                + "JOIN alat a ON dp.id_alat = a.id_alat "
                + "LEFT JOIN detail_pengembalian dk ON dp.id_detail_sewa = dk.id_detail_sewa "
                + "WHERE dp.id_penyewaan = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idPenyewaan);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    dtm.addRow(new Object[]{
                        rs.getString("kode_alat"),
                        rs.getString("nama_alat"),
                        rs.getInt("jumlah_sewa"),
                        rs.getDouble("harga_snapshot"),
                        rs.getInt("jumlah_kembali"),
                        rs.getString("kondisi"),
                        rs.getString("catatan")
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal readDetailTransaksi: " + e.getMessage());
        }
    }

    public void readSewaAktif(DefaultTableModel dtm) {
        dtm.setRowCount(0);
        Connection con = getConnection();
        if (con == null) {
            return;
        }

        String sql = "SELECT p.id_penyewaan, pl.nama, p.tanggal_sewa, p.tanggal_rencana_kembali, "
                + "p.total_biaya, p.status "
                + "FROM penyewaan p "
                + "JOIN pelanggan pl ON p.id_pelanggan = pl.id_pelanggan "
                + "WHERE p.status = 'DISEWA' "
                + "ORDER BY p.id_penyewaan DESC";

        try (Statement stm = con.createStatement(); ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                dtm.addRow(new Object[]{
                    rs.getInt("id_penyewaan"),
                    rs.getString("nama"),
                    rs.getString("tanggal_sewa"),
                    rs.getString("tanggal_rencana_kembali"),
                    rs.getDouble("total_biaya"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.out.println("Gagal readSewaAktif: " + e.getMessage());
        }
    }

    public void readDetailUntukKembali(DefaultTableModel dtm, int idPenyewaan) {
        dtm.setRowCount(0);
        Connection con = getConnection();
        if (con == null) {
            return;
        }

        String sql = "SELECT dp.id_detail_sewa, dp.id_alat, a.kode_alat, a.nama_alat, dp.jumlah_sewa "
                + "FROM detail_penyewaan dp "
                + "JOIN alat a ON dp.id_alat = a.id_alat "
                + "WHERE dp.id_penyewaan = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idPenyewaan);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int jml = rs.getInt("jumlah_sewa");
                    dtm.addRow(new Object[]{
                        rs.getInt("id_detail_sewa"),
                        rs.getInt("id_alat"),
                        rs.getString("kode_alat"),
                        rs.getString("nama_alat"),
                        jml,
                        jml,
                        "BAIK",
                        ""
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Gagal readDetailUntukKembali: " + e.getMessage());
        }
    }

    public void loadPelanggan(JComboBox<String> cb) {
        cb.removeAllItems();
        Connection con = getConnection();
        if (con == null) {
            return;
        }
        String sql = "SELECT id_pelanggan, nama, no_hp FROM pelanggan ORDER BY nama";
        try (Statement stm = con.createStatement(); ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                cb.addItem(rs.getInt("id_pelanggan") + " - " + rs.getString("nama") + " (" + rs.getString("no_hp") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Gagal loadPelanggan: " + e.getMessage());
        }
    }

    public void loadAlatTersedia(JComboBox<String> cb) {
        cb.removeAllItems();
        Connection con = getConnection();
        if (con == null) {
            return;
        }
        String sql = "SELECT id_alat, kode_alat, nama_alat, harga_sewa_per_hari, stok FROM alat WHERE stok > 0 ORDER BY nama_alat";
        try (Statement stm = con.createStatement(); ResultSet rs = stm.executeQuery(sql)) {
            while (rs.next()) {
                cb.addItem(rs.getInt("id_alat") + " | " + rs.getString("kode_alat") + " - "
                        + rs.getString("nama_alat") + " | Rp" + rs.getDouble("harga_sewa_per_hari")
                        + " | Stok:" + rs.getInt("stok"));
            }
        } catch (SQLException e) {
            System.out.println("Gagal loadAlatTersedia: " + e.getMessage());
        }
    }

    public boolean simpanPeminjaman(int idPelanggan, String tglSewa, String tglRencanaKembali,
            double totalBiaya, String metode, double jumlahBayar, double kembalian,
            List<Keranjang> items) {
        Connection con = getConnection();
        if (con == null || items == null || items.isEmpty()) {
            return false;
        }

        String sqlSewa = "INSERT INTO penyewaan "
                + "(id_pelanggan, tanggal_sewa, tanggal_rencana_kembali, status, total_biaya, total_denda, metode_pembayaran, jumlah_bayar, kembalian) "
                + "VALUES (?, ?, ?, 'DISEWA', ?, 0, ?, ?, ?)";
        String sqlDetail = "INSERT INTO detail_penyewaan (id_penyewaan, id_alat, jumlah_sewa, harga_snapshot) VALUES (?, ?, ?, ?)";
        String sqlStok = "UPDATE alat SET stok = stok - ? WHERE id_alat = ? AND stok >= ?";

        try {
            con.setAutoCommit(false);

            int idPenyewaan = -1;
            try (PreparedStatement pstSewa = con.prepareStatement(sqlSewa, Statement.RETURN_GENERATED_KEYS)) {
                pstSewa.setInt(1, idPelanggan);
                pstSewa.setString(2, tglSewa);
                pstSewa.setString(3, tglRencanaKembali);
                pstSewa.setDouble(4, totalBiaya);
                pstSewa.setString(5, metode);
                pstSewa.setDouble(6, jumlahBayar);
                pstSewa.setDouble(7, kembalian);
                pstSewa.executeUpdate();

                try (ResultSet keys = pstSewa.getGeneratedKeys()) {
                    if (keys.next()) {
                        idPenyewaan = keys.getInt(1);
                    }
                }
            }

            if (idPenyewaan <= 0) {
                con.rollback();
                return false;
            }

            try (PreparedStatement pstDetail = con.prepareStatement(sqlDetail);
                 PreparedStatement pstStok = con.prepareStatement(sqlStok)) {
                for (Keranjang item : items) {
                    int idAlat = item.getIdAlat();
                    double harga = item.getHargaSewa();
                    int jumlah = item.getJumlah();

                    pstStok.setInt(1, jumlah);
                    pstStok.setInt(2, idAlat);
                    pstStok.setInt(3, jumlah);
                    int updated = pstStok.executeUpdate();
                    if (updated == 0) {
                        con.rollback();
                        return false;
                    }

                    pstDetail.setInt(1, idPenyewaan);
                    pstDetail.setInt(2, idAlat);
                    pstDetail.setInt(3, jumlah);
                    pstDetail.setDouble(4, harga);
                    pstDetail.executeUpdate();
                }
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback error: " + ex.getMessage());
            }
            System.out.println("Gagal simpanPeminjaman: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    public boolean prosesPengembalian(int idPenyewaan, String tglKembali, double totalDenda,
            List<Object[]> details) {
        Connection con = getConnection();
        if (con == null || details == null || details.isEmpty()) {
            return false;
        }

        String sqlInsertKembali = "INSERT INTO detail_pengembalian (id_detail_sewa, jumlah_kembali, kondisi, catatan) VALUES (?, ?, ?, ?)";
        String sqlRestoreStok = "UPDATE alat SET stok = stok + ? WHERE id_alat = ?";
        String sqlUpdateSewa = "UPDATE penyewaan SET tanggal_kembali = ?, total_denda = ?, status = 'SELESAI' WHERE id_penyewaan = ?";

        try {
            con.setAutoCommit(false);

            try (PreparedStatement pstKembali = con.prepareStatement(sqlInsertKembali);
                 PreparedStatement pstStok = con.prepareStatement(sqlRestoreStok)) {
                for (Object[] row : details) {
                    int idDetailSewa = (int) row[0];
                    int idAlat = (int) row[1];
                    int jmlKembali = (int) row[2];
                    String kondisi = (String) row[3];
                    String catatan = (String) row[4];

                    pstKembali.setInt(1, idDetailSewa);
                    pstKembali.setInt(2, jmlKembali);
                    pstKembali.setString(3, kondisi);
                    pstKembali.setString(4, catatan);
                    pstKembali.executeUpdate();

                    if (!"HILANG".equalsIgnoreCase(kondisi) && jmlKembali > 0) {
                        pstStok.setInt(1, jmlKembali);
                        pstStok.setInt(2, idAlat);
                        pstStok.executeUpdate();
                    }
                }
            }

            try (PreparedStatement pstSewa = con.prepareStatement(sqlUpdateSewa)) {
                pstSewa.setString(1, tglKembali);
                pstSewa.setDouble(2, totalDenda);
                pstSewa.setInt(3, idPenyewaan);
                pstSewa.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback error: " + ex.getMessage());
            }
            System.out.println("Gagal prosesPengembalian: " + e.getMessage());
            return false;
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }
}
