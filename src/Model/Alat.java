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

    public String getNamaAlat() {
        return namaAlat;
    }

    public String getJenisAlat() {
        return jenisAlat;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public int getStok() {
        return stok;
    }

}
