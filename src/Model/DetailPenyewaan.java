package Model;

import java.math.BigDecimal;

public class DetailPenyewaan {

    // Data detail penyewaan
    private int idDetailSewa;
    private int idPenyewaan;
    private int idAlat;
    private int jumlahSewa;
    private BigDecimal hargaSnapshot;

    // Constructor kosong
    public DetailPenyewaan() {
    }

    // Constructor lengkap
    public DetailPenyewaan(int idDetailSewa, int idPenyewaan, int idAlat,
                           int jumlahSewa, BigDecimal hargaSnapshot) {
        this.idDetailSewa = idDetailSewa;
        this.idPenyewaan = idPenyewaan;
        this.idAlat = idAlat;
        this.jumlahSewa = jumlahSewa;
        this.hargaSnapshot = hargaSnapshot;
    }

    // Getter & Setter ID detail sewa
    public int getIdDetailSewa() {
        return idDetailSewa;
    }

    public void setIdDetailSewa(int idDetailSewa) {
        this.idDetailSewa = idDetailSewa;
    }

    // Getter & Setter ID penyewaan
    public int getIdPenyewaan() {
        return idPenyewaan;
    }

    public void setIdPenyewaan(int idPenyewaan) {
        this.idPenyewaan = idPenyewaan;
    }

    // Getter & Setter ID alat
    public int getIdAlat() {
        return idAlat;
    }

    public void setIdAlat(int idAlat) {
        this.idAlat = idAlat;
    }

    // Getter & Setter jumlah sewa
    public int getJumlahSewa() {
        return jumlahSewa;
    }

    public void setJumlahSewa(int jumlahSewa) {
        this.jumlahSewa = jumlahSewa;
    }

    // Getter & Setter harga snapshot
    public BigDecimal getHargaSnapshot() {
        return hargaSnapshot;
    }

    public void setHargaSnapshot(BigDecimal hargaSnapshot) {
        this.hargaSnapshot = hargaSnapshot;
    }
}