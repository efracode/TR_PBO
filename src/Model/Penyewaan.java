package Model;

import java.math.BigDecimal;
import java.sql.Date;

public class Penyewaan {

    // Data penyewaan
    private int idPenyewaan;
    private int idPelanggan;
    private Date tanggalSewa;
    private Date tanggalRencanaKembali;
    private Date tanggalKembali;
    private String status;
    private BigDecimal totalBiaya;
    private BigDecimal totalDenda;
    private String metodePembayaran;
    private BigDecimal jumlahBayar;
    private BigDecimal kembalian;

    // Constructor kosong
    public Penyewaan() {
    }

    // Constructor lengkap
    public Penyewaan(int idPenyewaan, int idPelanggan, Date tanggalSewa,
                     Date tanggalRencanaKembali, Date tanggalKembali,
                     String status, BigDecimal totalBiaya,
                     BigDecimal totalDenda, String metodePembayaran,
                     BigDecimal jumlahBayar, BigDecimal kembalian) {

        this.idPenyewaan = idPenyewaan;
        this.idPelanggan = idPelanggan;
        this.tanggalSewa = tanggalSewa;
        this.tanggalRencanaKembali = tanggalRencanaKembali;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
        this.totalBiaya = totalBiaya;
        this.totalDenda = totalDenda;
        this.metodePembayaran = metodePembayaran;
        this.jumlahBayar = jumlahBayar;
        this.kembalian = kembalian;
    }

    // Getter & Setter ID penyewaan
    public int getIdPenyewaan() {
        return idPenyewaan;
    }

    public void setIdPenyewaan(int idPenyewaan) {
        this.idPenyewaan = idPenyewaan;
    }

    // Getter & Setter ID pelanggan
    public int getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    // Getter & Setter tanggal sewa
    public Date getTanggalSewa() {
        return tanggalSewa;
    }

    public void setTanggalSewa(Date tanggalSewa) {
        this.tanggalSewa = tanggalSewa;
    }

    // Getter & Setter tanggal rencana kembali
    public Date getTanggalRencanaKembali() {
        return tanggalRencanaKembali;
    }

    public void setTanggalRencanaKembali(Date tanggalRencanaKembali) {
        this.tanggalRencanaKembali = tanggalRencanaKembali;
    }

    // Getter & Setter tanggal kembali
    public Date getTanggalKembali() {
        return tanggalKembali;
    }

    public void setTanggalKembali(Date tanggalKembali) {
        this.tanggalKembali = tanggalKembali;
    }

    // Getter & Setter status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter & Setter total biaya
    public BigDecimal getTotalBiaya() {
        return totalBiaya;
    }

    public void setTotalBiaya(BigDecimal totalBiaya) {
        this.totalBiaya = totalBiaya;
    }

    // Getter & Setter total denda
    public BigDecimal getTotalDenda() {
        return totalDenda;
    }

    public void setTotalDenda(BigDecimal totalDenda) {
        this.totalDenda = totalDenda;
    }

    // Getter & Setter metode pembayaran
    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    // Getter & Setter jumlah bayar
    public BigDecimal getJumlahBayar() {
        return jumlahBayar;
    }

    public void setJumlahBayar(BigDecimal jumlahBayar) {
        this.jumlahBayar = jumlahBayar;
    }

    // Getter & Setter kembalian
    public BigDecimal getKembalian() {
        return kembalian;
    }

    public void setKembalian(BigDecimal kembalian) {
        this.kembalian = kembalian;
    }
}