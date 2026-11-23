package Model;

public class Pelanggan {

    // Data pelanggan
    private int idPelanggan;
    private String nama;
    private String noHp;

    // Constructor kosong
    public Pelanggan() {
    }

    // Constructor lengkap
    public Pelanggan(int idPelanggan, String nama, String noHp) {
        this.idPelanggan = idPelanggan;
        this.nama = nama;
        this.noHp = noHp;
    }

    // Getter & Setter ID
    public int getIdPelanggan() {
        return idPelanggan;
    }

    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }

    // Getter & Setter nama
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter & Setter nomor HP
    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }
}