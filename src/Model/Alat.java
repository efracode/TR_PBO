package Model;

public class Alat {
    private int idAlat;
    private String namaAlat;
    private String jenisAlat;
    private double hargaSewa;
    private int stok;

    public Alat() {
    }

    public Alat(int idAlat, String namaAlat, String jenisAlat,
                double hargaSewa, int stok) {
        this.idAlat = idAlat;
        this.namaAlat = namaAlat;
        this.jenisAlat = jenisAlat;
        this.hargaSewa = hargaSewa;
        this.stok = stok;
    }

    public int getIdAlat() {
        return idAlat;
    }

    public void setIdAlat(int idAlat) {
        this.idAlat = idAlat;
    }

    public String getNamaAlat() {
        return namaAlat;
    }

    public void setNamaAlat(String namaAlat) {
        this.namaAlat = namaAlat;
    }

    public String getJenisAlat() {
        return jenisAlat;
    }

    public void setJenisAlat(String jenisAlat) {
        this.jenisAlat = jenisAlat;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public void setHargaSewa(double hargaSewa) {
        this.hargaSewa = hargaSewa;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
}
