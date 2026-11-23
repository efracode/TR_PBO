package Model;

public class DetailPengembalian {

    // Data detail pengembalian
    private int idDetailPengembalian;
    private int idDetailSewa;
    private int idAlat;
    private int jumlahKembali;
    private String kondisi;
    private String catatan;

    // Constructor kosong
    public DetailPengembalian() {
    }

    // Constructor lengkap
    public DetailPengembalian(int idDetailPengembalian, int idDetailSewa,
                              int jumlahKembali, String kondisi, String catatan) {
        this.idDetailPengembalian = idDetailPengembalian;
        this.idDetailSewa = idDetailSewa;
        this.jumlahKembali = jumlahKembali;
        this.kondisi = kondisi;
        this.catatan = catatan;
    }

    // Getter & Setter ID detail pengembalian
    public int getIdDetailPengembalian() {
        return idDetailPengembalian;
    }

    public void setIdDetailPengembalian(int idDetailPengembalian) {
        this.idDetailPengembalian = idDetailPengembalian;
    }

    // Getter & Setter ID detail sewa
    public int getIdDetailSewa() {
        return idDetailSewa;
    }

    public void setIdDetailSewa(int idDetailSewa) {
        this.idDetailSewa = idDetailSewa;
    }

    // Getter & Setter ID alat
    public int getIdAlat() {
        return idAlat;
    }

    public void setIdAlat(int idAlat) {
        this.idAlat = idAlat;
    }

    // Getter & Setter jumlah kembali
    public int getJumlahKembali() {
        return jumlahKembali;
    }

    public void setJumlahKembali(int jumlahKembali) {
        this.jumlahKembali = jumlahKembali;
    }

    // Getter & Setter kondisi
    public String getKondisi() {
        return kondisi;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    // Getter & Setter catatan
    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}