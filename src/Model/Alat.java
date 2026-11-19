package Model;

public class Alat {
    private int idAlat;
    private String namaAlat;
    private String kodeAlat;
    private String kategori;
    private double hargaSewa;
    private int stok;

    public Alat() {
    }

    public Alat(int idAlat, String namaAlat, String kodeAlat, String kategori,
                double hargaSewa, int stok) {
        this.idAlat = idAlat;
        this.namaAlat = namaAlat;
        this.kodeAlat = kodeAlat;
        this.kategori = kategori;
        this.hargaSewa = hargaSewa;
        this.stok = stok;
    }

    public int getIdAlat() {
        return idAlat;
    }
    
    public void setIdAlat(int idAlat){
        this.idAlat = idAlat;
    }

    public String getNamaAlat() {
        return namaAlat;
    }

    public String getKodeAlat() {
        return kodeAlat;
    }
    
    public String getKategori() {
        return kategori;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public int getStok() {
        return stok;
    }

}
