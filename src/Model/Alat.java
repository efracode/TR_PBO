package Model;

// double digunakan untuk menyimpan nilai harga/uang
// agar sesuai dengan tipe DECIMAL pada database

public class Alat {
    private int idAlat;
    private String kodeAlat;
    private String namaAlat;
    private String kategori;
    private double hargaSewa;
    private int stok;

    // Constructor kosong
    // Digunakan untuk membuat objek Alat tanpa mengisi data terlebih dahulu
    public Alat() {
    }

    // Constructor dengan parameter
    // Digunakan untuk membuat objek Alat sekaligus mengisi seluruh atribut
    public Alat(int idAlat, String kodeAlat, String namaAlat, String kategori,
                double hargaSewa, int stok) {
        this.idAlat = idAlat;
        this.kodeAlat = kodeAlat;
        this.namaAlat = namaAlat;
        this.kategori = kategori;
        this.hargaSewa = hargaSewa;
        this.stok = stok;
    }

    // Getter untuk mengambil nilai idAlat
    public int getIdAlat() {
        return idAlat;
    }

    // Setter untuk mengubah nilai idAlat
    public void setIdAlat(int idAlat) {
        this.idAlat = idAlat;
    }

    // Getter untuk mengambil nilai kodeAlat
    public String getKodeAlat() {
        return kodeAlat;
    }

    // Setter untuk mengubah nilai kodeAlat
    public void setKodeAlat(String kodeAlat) {
        this.kodeAlat = kodeAlat;
    }

    // Getter untuk mengambil nilai namaAlat
    public String getNamaAlat() {
        return namaAlat;
    }

    // Setter untuk mengubah nilai namaAlat
    public void setNamaAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }

    // Getter untuk mengambil nilai kategori
    public String getKategori() {
        return kategori;
    }

    // Setter untuk mengubah nilai kategori
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    // Getter untuk mengambil nilai hargaSewa
    public double getHargaSewa() {
        return hargaSewa;
    }

    // Setter untuk mengubah nilai hargaSewa
    public void setHargaSewa(double hargaSewa) {
        this.hargaSewa = hargaSewa;
    }

    // Getter untuk mengambil nilai stok
    public int getStok() {
        return stok;
    }

    // Setter untuk mengubah nilai stok
    public void setStok(int stok) {
        this.stok = stok;
    }
}