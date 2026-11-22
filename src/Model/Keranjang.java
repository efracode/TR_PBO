package Model;

public class Keranjang {
    private int idAlat;
    private String kodeAlat;
    private String namaAlat;
    private double hargaSewa;
    private int jumlah;

    public Keranjang() {
    }

    public Keranjang(int idAlat, String kodeAlat, String namaAlat, double hargaSewa, int jumlah) {
        this.idAlat = idAlat;
        this.kodeAlat = kodeAlat;
        this.namaAlat = namaAlat;
        this.hargaSewa = hargaSewa;
        this.jumlah = jumlah;
    }

    public int getIdAlat() {
        return idAlat;
    }

    public void setIdAlat(int idAlat) {
        this.idAlat = idAlat;
    }

    public String getKodeAlat() {
        return kodeAlat;
    }

    public void setKodeAlat(String kodeAlat) {
        this.kodeAlat = kodeAlat;
    }

    public String getNamaAlat() {
        return namaAlat;
    }

    public void setNamaAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public void setHargaSewa(double hargaSewa) {
        this.hargaSewa = hargaSewa;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getSubtotal() {
        return hargaSewa * jumlah;
    }
}
