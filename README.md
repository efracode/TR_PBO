databse name = rental_outdoor

CREATE TABLE alat (
    id_alat INT AUTO_INCREMENT PRIMARY KEY,
    nama_alat VARCHAR(100) NOT NULL,
    jenis_alat VARCHAR(50) NOT NULL,
    harga_sewa DOUBLE NOT NULL,
    stok INT NOT NULL
);
