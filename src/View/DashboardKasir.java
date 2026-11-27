/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Controller.PelangganController;
import Controller.TransaksiController;
import Model.DetailPengembalian;
import Model.Keranjang;
import Model.Pelanggan;
import Model.Penyewaan;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author ACER
 */
public class DashboardKasir extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardKasir.class.getName());
    private static final double DENDA_PER_HARI = 10000;
    private static final double DENDA_HILANG_RUSAK = 100000;
    private final TransaksiController transaksiController;
    private final PelangganController pelangganController;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final List<Keranjang> keranjangList = new ArrayList<>();
    private int selectedSewaId = -1;
    private String selectedRencanaKembali = "";

    /**
     * Creates new form DashboardKasir
     */
    public DashboardKasir() {
        this("user");
    }

    public DashboardKasir(String username) {
        initComponents();
        String namaUser = (username == null || username.trim().isEmpty()) ? "user" : username;
        lblUser.setText("Kasir: " + namaUser);

        transaksiController = new TransaksiController();
        pelangganController = new PelangganController();
        
        tblPelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAlat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSewaAktif.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableModel = pelangganController.createTable();
        tblPelanggan.setModel(tableModel);
        tblPelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPelanggan.setDefaultEditor(Object.class, null);
        
        rowSorter = new TableRowSorter<>(tableModel);
        tblPelanggan.setRowSorter(rowSorter);

        JComboBox<String> cbKondisiEditor = new JComboBox<>(new String[]{"BAIK", "RUSAK", "BURUK", "HILANG"});
        tblDetailKembali.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(cbKondisiEditor));
        tblDetailKembali.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                hitungDendaOtomatis();
            }
        });

        loadData();
        resetFormSewa();
        updatePembayaranMode();
        loadMasterData();
        loadSewaAktif();
        loadTransaksi();
    }
    
    private void loadData() {
        pelangganController.readPelanggan();
        lblTotalPelanggan.setText("Total Pelanggan: " + tableModel.getRowCount());
    }

    private void loadMasterData() {
        transaksiController.loadPelanggan(cbPelanggan);
        DefaultTableModel alatModel = (DefaultTableModel) tblAlat.getModel();
        transaksiController.loadAlatTersedia(alatModel);
    }

    private void loadSewaAktif() {
        selectedSewaId = -1;
        selectedRencanaKembali = "";
        DefaultTableModel dtm = (DefaultTableModel) tblSewaAktif.getModel();
        transaksiController.readSewaAktif(dtm);
        ((DefaultTableModel) tblDetailKembali.getModel()).setRowCount(0);
        txtTglKembali.setText(LocalDate.now().toString());
        txtDenda.setText("0");
    }

    private void hitungDendaOtomatis() {
        if (selectedRencanaKembali == null || selectedRencanaKembali.isEmpty()) {
            txtDenda.setText("0");
            return;
        }
        try {
            LocalDate tglRencana = LocalDate.parse(selectedRencanaKembali);
            LocalDate tglKembali = LocalDate.parse(txtTglKembali.getText().trim());
            long terlambat = ChronoUnit.DAYS.between(tglRencana, tglKembali);

            int totalItem = 0;
            int totalHilangRusak = 0;
            DefaultTableModel detailModel = (DefaultTableModel) tblDetailKembali.getModel();
            for (int i = 0; i < detailModel.getRowCount(); i++) {
                int jmlSewa = Integer.parseInt(detailModel.getValueAt(i, 4).toString());
                int jmlKembali = Integer.parseInt(detailModel.getValueAt(i, 5).toString());
                String kondisi = detailModel.getValueAt(i, 6) == null ? "BAIK" : detailModel.getValueAt(i, 6).toString();

                totalItem += jmlSewa;
                if ("HILANG".equalsIgnoreCase(kondisi) || "RUSAK".equalsIgnoreCase(kondisi)) {
                    totalHilangRusak += (jmlKembali > 0 ? jmlKembali : jmlSewa);
                }
            }

            double dendaTelat = terlambat > 0 ? totalItem * terlambat * DENDA_PER_HARI : 0;
            double dendaKondisi = totalHilangRusak * DENDA_HILANG_RUSAK;
            txtDenda.setText(String.valueOf(dendaTelat + dendaKondisi));
        } catch (Exception e) {
            txtDenda.setText("0");
        }
    }

    private void loadTransaksi() {
        DefaultTableModel dtm = (DefaultTableModel) tblTransaksi.getModel();
        transaksiController.readRiwayatTransaksi(dtm, txtCariTransaksi.getText());
        ((DefaultTableModel) tblDetailTransaksi.getModel()).setRowCount(0);
    }

    private long hitungLamaHari() {
        try {
            LocalDate tglSewa = LocalDate.parse(txtTglSewa.getText().trim());
            LocalDate tglRencana = LocalDate.parse(txtTglRencanaKembali.getText().trim());
            long days = ChronoUnit.DAYS.between(tglSewa, tglRencana);
            return Math.max(1, days);
        } catch (Exception e) {
            return 1;
        }
    }

    private void refreshKeranjangTable() {
        DefaultTableModel model = (DefaultTableModel) tblKeranjang.getModel();
        model.setRowCount(0);
        for (Keranjang item : keranjangList) {
            model.addRow(new Object[]{
                item.getIdAlat(),
                item.getKodeAlat(),
                item.getNamaAlat(),
                item.getHargaSewa(),
                item.getJumlah(),
                item.getSubtotal()
            });
        }
    }

    private void updatePembayaranMode() {
        String metode = cbMetode.getSelectedItem() == null ? "Cash" : cbMetode.getSelectedItem().toString();
        boolean isCash = "Cash".equalsIgnoreCase(metode);
        txtBayar.setEditable(isCash);
        if (!isCash) {
            txtBayar.setText(txtTotalBiaya.getText());
            txtKembalian.setText("0");
        }
    }

    private void hitungTotalDanKembalian() {
        double totalPerHari = 0;
        for (Keranjang item : keranjangList) {
            totalPerHari += item.getSubtotal();
        }
        double totalBiaya = totalPerHari * hitungLamaHari();
        txtTotalBiaya.setText(String.valueOf(totalBiaya));

        String metode = cbMetode.getSelectedItem() == null ? "Cash" : cbMetode.getSelectedItem().toString();
        if (!"Cash".equalsIgnoreCase(metode)) {
            txtBayar.setText(String.valueOf(totalBiaya));
            txtKembalian.setText("0");
            return;
        }

        try {
            double bayar = Double.parseDouble(txtBayar.getText().trim());
            txtKembalian.setText(String.valueOf(bayar - totalBiaya));
        } catch (NumberFormatException e) {
            txtKembalian.setText("0");
        }
    }

    private void resetFormSewa() {
        LocalDate today = LocalDate.now();
        txtTglSewa.setText(today.toString());
        txtTglRencanaKembali.setText(today.plusDays(1).toString());
        txtJumlah.setText("1");
        txtTotalBiaya.setText("0");
        txtBayar.setText("");
        txtKembalian.setText("0");
        cbMetode.setSelectedIndex(0);
        txtBayar.setEditable(true);
        keranjangList.clear();
        tblAlat.clearSelection();
        refreshKeranjangTable();
    }
    
    private Pelanggan buildPelangganFromForm() {
        String idText = txtIdPelanggan.getText().trim();
        String nama = txtNamaPelanggan.getText().trim();
        String noHp = txtNoHpPelanggan.getText().trim();

        if (idText.isEmpty() || noHp.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field wajib diisi!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        try {
            int idPelanggan = Integer.parseInt(idText);
            
            return new Pelanggan(idPelanggan, nama, noHp);
            
        } catch (NumberFormatException e){
            JOptionPane.showMessageDialog(this,
                    "ID Pelanggan harus berupa angka yang valid!",
                    "Input Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
    }
    
    private void resetForm() {
        txtIdPelanggan.setText("");
        txtNamaPelanggan.setText("");
        txtNoHpPelanggan.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        tabKasir = new javax.swing.JTabbedPane();
        panelPeminjaman = new javax.swing.JPanel();
        lblPelanggan = new javax.swing.JLabel();
        cbPelanggan = new javax.swing.JComboBox<>();
        lblTglSewa = new javax.swing.JLabel();
        txtTglSewa = new javax.swing.JTextField();
        lblTglRencana = new javax.swing.JLabel();
        txtTglRencanaKembali = new javax.swing.JTextField();
        lblPilihAlat = new javax.swing.JLabel();
        jScrollPaneAlat = new javax.swing.JScrollPane();
        tblAlat = new javax.swing.JTable();
        lblJumlah = new javax.swing.JLabel();
        txtJumlah = new javax.swing.JTextField();
        btnTambahItem = new javax.swing.JButton();
        btnHapusItem = new javax.swing.JButton();
        jScrollPaneKeranjang = new javax.swing.JScrollPane();
        tblKeranjang = new javax.swing.JTable();
        lblTotalBiaya = new javax.swing.JLabel();
        txtTotalBiaya = new javax.swing.JTextField();
        lblMetode = new javax.swing.JLabel();
        cbMetode = new javax.swing.JComboBox<>();
        lblBayar = new javax.swing.JLabel();
        txtBayar = new javax.swing.JTextField();
        lblKembalian = new javax.swing.JLabel();
        txtKembalian = new javax.swing.JTextField();
        btnResetSewa = new javax.swing.JButton();
        btnSimpanSewa = new javax.swing.JButton();
        panelPengembalian = new javax.swing.JPanel();
        lblAktifTitle = new javax.swing.JLabel();
        btnRefreshAktif = new javax.swing.JButton();
        jScrollPaneAktif = new javax.swing.JScrollPane();
        tblSewaAktif = new javax.swing.JTable();
        lblDetailKembaliTitle = new javax.swing.JLabel();
        jScrollPaneKembali = new javax.swing.JScrollPane();
        tblDetailKembali = new javax.swing.JTable();
        lblTglKembali = new javax.swing.JLabel();
        txtTglKembali = new javax.swing.JTextField();
        lblDenda = new javax.swing.JLabel();
        txtDenda = new javax.swing.JTextField();
        btnProsesKembali = new javax.swing.JButton();
        panelTransaksi = new javax.swing.JPanel();
        lblCariTransaksi = new javax.swing.JLabel();
        txtCariTransaksi = new javax.swing.JTextField();
        btnRefreshTransaksi = new javax.swing.JButton();
        jScrollPaneTransaksi = new javax.swing.JScrollPane();
        tblTransaksi = new javax.swing.JTable();
        lblDetailTransaksiTitle = new javax.swing.JLabel();
        jScrollPaneDetailTransaksi = new javax.swing.JScrollPane();
        tblDetailTransaksi = new javax.swing.JTable();
        panelPelanggan = new javax.swing.JPanel();
        lblIdPelanggan = new javax.swing.JLabel();
        txtIdPelanggan = new javax.swing.JTextField();
        lblNamaPelanggan = new javax.swing.JLabel();
        txtNamaPelanggan = new javax.swing.JTextField();
        lblNoHpPelanggan = new javax.swing.JLabel();
        txtNoHpPelanggan = new javax.swing.JTextField();
        btnTambahPelanggan = new javax.swing.JButton();
        btnUbahPelanggan = new javax.swing.JButton();
        btnHapusPelanggan = new javax.swing.JButton();
        btnResetPelanggan = new javax.swing.JButton();
        lblCariPelanggan = new javax.swing.JLabel();
        txtCariPelanggan = new javax.swing.JTextField();
        btnRefreshPelanggan = new javax.swing.JButton();
        jScrollPanePelanggan = new javax.swing.JScrollPane();
        tblPelanggan = new javax.swing.JTable();
        lblTotalPelanggan = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard Kasir");

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setText("Dashboard Kasir");

        lblUser.setText("Kasir: user");

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        lblPelanggan.setText("Pelanggan");

        lblTglSewa.setText("Tanggal Sewa (yyyy-MM-dd)");

        txtTglSewa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTglSewaKeyReleased(evt);
            }
        });

        lblTglRencana.setText("Rencana Kembali (yyyy-MM-dd)");

        txtTglRencanaKembali.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTglRencanaKembaliKeyReleased(evt);
            }
        });

        lblPilihAlat.setText("Pilih Alat");

        tblAlat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Alat", "Kode", "Nama Alat", "Harga/Hari", "Stok"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneAlat.setViewportView(tblAlat);

        lblJumlah.setText("Jumlah");

        txtJumlah.setText("1");

        btnTambahItem.setText("Tambah Item");
        btnTambahItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahItemActionPerformed(evt);
            }
        });

        btnHapusItem.setText("Hapus Item");
        btnHapusItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusItemActionPerformed(evt);
            }
        });

        tblKeranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Alat", "Kode", "Nama Alat", "Harga/Hari", "Jumlah", "Subtotal/Hari"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneKeranjang.setViewportView(tblKeranjang);

        lblTotalBiaya.setText("Total Biaya");

        txtTotalBiaya.setEditable(false);
        txtTotalBiaya.setText("0");

        lblMetode.setText("Metode");

        cbMetode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash", "QRIS", "Transfer" }));
        cbMetode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbMetodeActionPerformed(evt);
            }
        });

        lblBayar.setText("Jumlah Bayar");

        txtBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBayarKeyReleased(evt);
            }
        });

        lblKembalian.setText("Kembalian");

        txtKembalian.setEditable(false);
        txtKembalian.setText("0");

        btnResetSewa.setText("Reset");
        btnResetSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetSewaActionPerformed(evt);
            }
        });

        btnSimpanSewa.setText("Simpan Peminjaman");
        btnSimpanSewa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanSewaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPeminjamanLayout = new javax.swing.GroupLayout(panelPeminjaman);
        panelPeminjaman.setLayout(panelPeminjamanLayout);
        panelPeminjamanLayout.setHorizontalGroup(
            panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeminjamanLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
                    .addGroup(panelPeminjamanLayout.createSequentialGroup()
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPelanggan)
                            .addComponent(cbPelanggan, 0, 240, Short.MAX_VALUE)
                            .addComponent(lblTglSewa)
                            .addComponent(txtTglSewa)
                            .addComponent(lblTglRencana)
                            .addComponent(txtTglRencanaKembali))
                        .addGap(24, 24, 24)
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPeminjamanLayout.createSequentialGroup()
                                .addComponent(lblPilihAlat)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPaneAlat, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblJumlah)
                            .addComponent(txtJumlah)
                            .addComponent(btnTambahItem, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                            .addComponent(btnHapusItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelPeminjamanLayout.createSequentialGroup()
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTotalBiaya)
                            .addComponent(txtTotalBiaya, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMetode)
                            .addComponent(cbMetode, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBayar)
                            .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKembalian)
                            .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnResetSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSimpanSewa, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        panelPeminjamanLayout.setVerticalGroup(
            panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeminjamanLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelPeminjamanLayout.createSequentialGroup()
                        .addComponent(lblPelanggan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTglSewa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTglSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTglRencana)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTglRencanaKembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelPeminjamanLayout.createSequentialGroup()
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPilihAlat)
                            .addComponent(lblJumlah))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelPeminjamanLayout.createSequentialGroup()
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnTambahItem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnHapusItem)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPaneAlat, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneKeranjang, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalBiaya)
                    .addComponent(lblMetode)
                    .addComponent(lblBayar)
                    .addComponent(lblKembalian))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPeminjamanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalBiaya, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMetode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBayar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetSewa)
                    .addComponent(btnSimpanSewa))
                .addGap(12, 12, 12))
        );

        tabKasir.addTab("Peminjaman", panelPeminjaman);

        lblAktifTitle.setText("Daftar Penyewaan Aktif (Status: DISEWA) - klik baris untuk memilih:");

        btnRefreshAktif.setText("Refresh");
        btnRefreshAktif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAktifActionPerformed(evt);
            }
        });

        tblSewaAktif.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Sewa", "Pelanggan", "Tgl Sewa", "Rencana Kembali", "Total Biaya", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSewaAktif.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSewaAktifMouseClicked(evt);
            }
        });
        jScrollPaneAktif.setViewportView(tblSewaAktif);

        lblDetailKembaliTitle.setText("Detail Barang Dikembalikan (ubah Jml Kembali / Kondisi / Catatan di tabel):");

        tblDetailKembali.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Detail", "ID Alat", "Kode", "Nama Alat", "Jml Sewa", "Jml Kembali", "Kondisi", "Catatan"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneKembali.setViewportView(tblDetailKembali);

        lblTglKembali.setText("Tanggal Kembali:");

        txtTglKembali.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTglKembaliKeyReleased(evt);
            }
        });

        lblDenda.setText("Total Denda:");

        txtDenda.setText("0");

        btnProsesKembali.setText("Proses Pengembalian");
        btnProsesKembali.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProsesKembaliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPengembalianLayout = new javax.swing.GroupLayout(panelPengembalian);
        panelPengembalian.setLayout(panelPengembalianLayout);
        panelPengembalianLayout.setHorizontalGroup(
            panelPengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPengembalianLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneAktif, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
                    .addComponent(jScrollPaneKembali)
                    .addGroup(panelPengembalianLayout.createSequentialGroup()
                        .addComponent(lblAktifTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefreshAktif))
                    .addGroup(panelPengembalianLayout.createSequentialGroup()
                        .addComponent(lblDetailKembaliTitle)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelPengembalianLayout.createSequentialGroup()
                        .addComponent(lblTglKembali)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDenda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDenda, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnProsesKembali, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        panelPengembalianLayout.setVerticalGroup(
            panelPengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPengembalianLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAktifTitle)
                    .addComponent(btnRefreshAktif))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneAktif, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDetailKembaliTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneKembali, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelPengembalianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTglKembali)
                    .addComponent(txtTglKembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDenda)
                    .addComponent(txtDenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnProsesKembali))
                .addGap(12, 12, 12))
        );

        tabKasir.addTab("Pengembalian", panelPengembalian);

        lblCariTransaksi.setText("Cari (Nama / Status / ID):");

        txtCariTransaksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariTransaksiKeyReleased(evt);
            }
        });

        btnRefreshTransaksi.setText("Refresh");
        btnRefreshTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshTransaksiActionPerformed(evt);
            }
        });

        tblTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Sewa", "Pelanggan", "Tgl Sewa", "Rencana Kembali", "Tgl Kembali", "Status", "Total Biaya", "Denda", "Metode", "Bayar", "Kembalian"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTransaksiMouseClicked(evt);
            }
        });
        jScrollPaneTransaksi.setViewportView(tblTransaksi);

        lblDetailTransaksiTitle.setText("Detail Item Transaksi (klik baris di atas):");

        tblDetailTransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Alat", "Nama Alat", "Jumlah Sewa", "Harga/Hari", "Jumlah Kembali", "Kondisi", "Catatan"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPaneDetailTransaksi.setViewportView(tblDetailTransaksi);

        javax.swing.GroupLayout panelTransaksiLayout = new javax.swing.GroupLayout(panelTransaksi);
        panelTransaksi.setLayout(panelTransaksiLayout);
        panelTransaksiLayout.setHorizontalGroup(
            panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransaksiLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
                    .addComponent(jScrollPaneDetailTransaksi)
                    .addGroup(panelTransaksiLayout.createSequentialGroup()
                        .addComponent(lblCariTransaksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCariTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefreshTransaksi))
                    .addGroup(panelTransaksiLayout.createSequentialGroup()
                        .addComponent(lblDetailTransaksiTitle)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        panelTransaksiLayout.setVerticalGroup(
            panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTransaksiLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelTransaksiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCariTransaksi)
                    .addComponent(txtCariTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefreshTransaksi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDetailTransaksiTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDetailTransaksi, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        tabKasir.addTab("Lihat Transaksi", panelTransaksi);

        lblIdPelanggan.setText("ID Pelanggan");

        lblNamaPelanggan.setText("Nama");

        lblNoHpPelanggan.setText("No HP");

        btnTambahPelanggan.setText("Tambah");
        btnTambahPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahPelangganActionPerformed(evt);
            }
        });

        btnUbahPelanggan.setText("Ubah");
        btnUbahPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahPelangganActionPerformed(evt);
            }
        });

        btnHapusPelanggan.setText("Hapus");
        btnHapusPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusPelangganActionPerformed(evt);
            }
        });

        btnResetPelanggan.setText("Reset");
        btnResetPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetPelangganActionPerformed(evt);
            }
        });

        lblCariPelanggan.setText("Cari:");

        txtCariPelanggan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariPelangganKeyReleased(evt);
            }
        });

        btnRefreshPelanggan.setText("Refresh");
        btnRefreshPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshPelangganActionPerformed(evt);
            }
        });

        tblPelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nama", "No HP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPelanggan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPelangganMouseClicked(evt);
            }
        });
        jScrollPanePelanggan.setViewportView(tblPelanggan);

        lblTotalPelanggan.setText("Total Pelanggan: 0");

        javax.swing.GroupLayout panelPelangganLayout = new javax.swing.GroupLayout(panelPelanggan);
        panelPelanggan.setLayout(panelPelangganLayout);
        panelPelangganLayout.setHorizontalGroup(
            panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPelangganLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIdPelanggan)
                    .addComponent(txtIdPelanggan)
                    .addComponent(lblNamaPelanggan)
                    .addComponent(txtNamaPelanggan)
                    .addComponent(lblNoHpPelanggan)
                    .addComponent(txtNoHpPelanggan)
                    .addGroup(panelPelangganLayout.createSequentialGroup()
                        .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnTambahPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(btnHapusPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUbahPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(btnResetPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(24, 24, 24)
                .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPelangganLayout.createSequentialGroup()
                        .addComponent(lblCariPelanggan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCariPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefreshPelanggan))
                    .addComponent(jScrollPanePelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGroup(panelPelangganLayout.createSequentialGroup()
                        .addComponent(lblTotalPelanggan)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        panelPelangganLayout.setVerticalGroup(
            panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPelangganLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPelangganLayout.createSequentialGroup()
                        .addComponent(lblIdPelanggan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNamaPelanggan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNamaPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNoHpPelanggan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNoHpPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTambahPelanggan)
                            .addComponent(btnUbahPelanggan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHapusPelanggan)
                            .addComponent(btnResetPelanggan))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelPelangganLayout.createSequentialGroup()
                        .addGroup(panelPelangganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCariPelanggan)
                            .addComponent(txtCariPelanggan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRefreshPelanggan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPanePelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalPelanggan)))
                .addGap(12, 12, 12))
        );

        tabKasir.addTab("Data Pelanggan", panelPelanggan);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLogout))
                    .addComponent(tabKasir))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitle)
                    .addComponent(lblUser)
                    .addComponent(btnLogout))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabKasir)
                .addGap(16, 16, 16))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new Login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnTambahItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahItemActionPerformed
        if (tblAlat.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada alat yang tersedia!");
            return;
        }

        int row = tblAlat.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih alat pada tabel terlebih dahulu!");
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
            return;
        }

        int idAlat = Integer.parseInt(tblAlat.getValueAt(row, 0).toString());
        String kode = tblAlat.getValueAt(row, 1).toString();
        String nama = tblAlat.getValueAt(row, 2).toString();
        double harga = Double.parseDouble(tblAlat.getValueAt(row, 3).toString());
        int stok = Integer.parseInt(tblAlat.getValueAt(row, 4).toString());

        for (Keranjang item : keranjangList) {
            if (item.getIdAlat() == idAlat) {
                int newJumlah = item.getJumlah() + jumlah;
                if (newJumlah > stok) {
                    JOptionPane.showMessageDialog(this, "Stok tidak mencukupi! Maksimal: " + stok);
                    return;
                }
                item.setJumlah(newJumlah);
                refreshKeranjangTable();
                hitungTotalDanKembalian();
                return;
            }
        }

        if (jumlah > stok) {
            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi! Maksimal: " + stok);
            return;
        }

        keranjangList.add(new Keranjang(idAlat, kode, nama, harga, jumlah));
        refreshKeranjangTable();
        hitungTotalDanKembalian();
    }//GEN-LAST:event_btnTambahItemActionPerformed

    private void btnHapusItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusItemActionPerformed
        int row = tblKeranjang.getSelectedRow();
        if (row >= 0 && row < keranjangList.size()) {
            keranjangList.remove(row);
            refreshKeranjangTable();
            hitungTotalDanKembalian();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih item di tabel keranjang terlebih dahulu!");
        }
    }//GEN-LAST:event_btnHapusItemActionPerformed

    private void btnResetSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetSewaActionPerformed
        resetFormSewa();
    }//GEN-LAST:event_btnResetSewaActionPerformed

    private void btnSimpanSewaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanSewaActionPerformed
        if (cbPelanggan.getSelectedItem() == null || keranjangList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan dan tambahkan minimal 1 alat!");
            return;
        }

        String tglSewa = txtTglSewa.getText().trim();
        String tglRencana = txtTglRencanaKembali.getText().trim();
        try {
            LocalDate.parse(tglSewa);
            LocalDate.parse(tglRencana);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format tanggal harus yyyy-MM-dd!");
            return;
        }

        double totalBiaya = Double.parseDouble(txtTotalBiaya.getText().trim());
        double bayar;
        try {
            bayar = Double.parseDouble(txtBayar.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar harus berupa angka!");
            return;
        }

        if (bayar < totalBiaya) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar kurang dari total biaya!");
            return;
        }

        double kembalian = bayar - totalBiaya;
        int idPelanggan = Integer.parseInt(cbPelanggan.getSelectedItem().toString().split(" - ")[0].trim());
        String metode = cbMetode.getSelectedItem().toString();

        Penyewaan penyewaan = new Penyewaan();
        penyewaan.setIdPelanggan(idPelanggan);
        penyewaan.setTanggalSewa(Date.valueOf(tglSewa));
        penyewaan.setTanggalRencanaKembali(Date.valueOf(tglRencana));
        penyewaan.setStatus("DISEWA");
        penyewaan.setTotalBiaya(BigDecimal.valueOf(totalBiaya));
        penyewaan.setTotalDenda(BigDecimal.ZERO);
        penyewaan.setMetodePembayaran(metode);
        penyewaan.setJumlahBayar(BigDecimal.valueOf(bayar));
        penyewaan.setKembalian(BigDecimal.valueOf(kembalian));

        if (transaksiController.simpanPeminjaman(penyewaan, keranjangList)) {
            JOptionPane.showMessageDialog(this, "Peminjaman berhasil disimpan!");
            resetFormSewa();
            loadMasterData();
            loadSewaAktif();
            loadTransaksi();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan peminjaman!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSimpanSewaActionPerformed

    private void txtBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBayarKeyReleased
        hitungTotalDanKembalian();
    }//GEN-LAST:event_txtBayarKeyReleased

    private void txtTglSewaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTglSewaKeyReleased
        hitungTotalDanKembalian();
    }//GEN-LAST:event_txtTglSewaKeyReleased

    private void txtTglRencanaKembaliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTglRencanaKembaliKeyReleased
        hitungTotalDanKembalian();
    }//GEN-LAST:event_txtTglRencanaKembaliKeyReleased

    private void cbMetodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbMetodeActionPerformed
        updatePembayaranMode();
        hitungTotalDanKembalian();
    }//GEN-LAST:event_cbMetodeActionPerformed

    private void btnRefreshAktifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAktifActionPerformed
        loadSewaAktif();
    }//GEN-LAST:event_btnRefreshAktifActionPerformed

    private void tblSewaAktifMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSewaAktifMouseClicked
        int row = tblSewaAktif.getSelectedRow();
        if (row >= 0) {
            selectedSewaId = Integer.parseInt(tblSewaAktif.getValueAt(row, 0).toString());
            selectedRencanaKembali = tblSewaAktif.getValueAt(row, 3).toString();
            DefaultTableModel detailModel = (DefaultTableModel) tblDetailKembali.getModel();
            transaksiController.readDetailUntukKembali(detailModel, selectedSewaId);
            hitungDendaOtomatis();
        }
    }//GEN-LAST:event_tblSewaAktifMouseClicked

    private void txtTglKembaliKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTglKembaliKeyReleased
        hitungDendaOtomatis();
    }//GEN-LAST:event_txtTglKembaliKeyReleased

    private void btnProsesKembaliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProsesKembaliActionPerformed
        if (selectedSewaId <= 0 || tblDetailKembali.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih penyewaan aktif pada tabel terlebih dahulu!");
            return;
        }

        if (tblDetailKembali.isEditing()) {
            tblDetailKembali.getCellEditor().stopCellEditing();
        }

        String tglKembali = txtTglKembali.getText().trim();
        try {
            LocalDate.parse(tglKembali);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format tanggal kembali harus yyyy-MM-dd!");
            return;
        }

        double denda;
        try {
            denda = Double.parseDouble(txtDenda.getText().trim());
            if (denda < 0) {
                JOptionPane.showMessageDialog(this, "Denda tidak boleh negatif!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Total denda harus berupa angka!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblDetailKembali.getModel();
        List<DetailPengembalian> details = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            int idDetailSewa = Integer.parseInt(model.getValueAt(i, 0).toString());
            int idAlat = Integer.parseInt(model.getValueAt(i, 1).toString());
            int jmlSewa = Integer.parseInt(model.getValueAt(i, 4).toString());
            int jmlKembali = Integer.parseInt(model.getValueAt(i, 5).toString());
            String kondisi = model.getValueAt(i, 6) == null ? "BAIK" : model.getValueAt(i, 6).toString();
            String catatan = model.getValueAt(i, 7) == null ? "" : model.getValueAt(i, 7).toString();

            if (jmlKembali < 0 || jmlKembali > jmlSewa) {
                JOptionPane.showMessageDialog(this, "Jumlah kembali tidak valid pada baris " + (i + 1));
                return;
            }
            DetailPengembalian detail = new DetailPengembalian();
            detail.setIdDetailSewa(idDetailSewa);
            detail.setIdAlat(idAlat);
            detail.setJumlahKembali(jmlKembali);
            detail.setKondisi(kondisi);
            detail.setCatatan(catatan);
            details.add(detail);
        }

        Penyewaan penyewaan = new Penyewaan();
        penyewaan.setIdPenyewaan(selectedSewaId);
        penyewaan.setTanggalKembali(Date.valueOf(tglKembali));
        penyewaan.setTotalDenda(BigDecimal.valueOf(denda));
        penyewaan.setStatus("SELESAI");

        if (transaksiController.prosesPengembalian(penyewaan, details)) {
            JOptionPane.showMessageDialog(this, "Pengembalian berhasil diproses!");
            loadMasterData();
            loadSewaAktif();
            loadTransaksi();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memproses pengembalian!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnProsesKembaliActionPerformed

    private void txtCariTransaksiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariTransaksiKeyReleased
        loadTransaksi();
    }//GEN-LAST:event_txtCariTransaksiKeyReleased

    private void btnRefreshTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshTransaksiActionPerformed
        loadTransaksi();
    }//GEN-LAST:event_btnRefreshTransaksiActionPerformed

    private void tblTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTransaksiMouseClicked
        int row = tblTransaksi.getSelectedRow();
        if (row >= 0) {
            int idPenyewaan = Integer.parseInt(tblTransaksi.getValueAt(row, 0).toString());
            DefaultTableModel detailModel = (DefaultTableModel) tblDetailTransaksi.getModel();
            transaksiController.readDetailTransaksi(detailModel, idPenyewaan);
        }
    }//GEN-LAST:event_tblTransaksiMouseClicked

    private void btnTambahPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahPelangganActionPerformed
        // TODO: hubungkan ke PelangganController
        Pelanggan pelanggan = buildPelangganFromForm();
        if (pelanggan == null) {
            return;
        }

        if (pelangganController.createPelanggan(pelanggan)) {
            JOptionPane.showMessageDialog(this,
                    "Data pelanggan berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal menambahkan data pelanggan. Periksa ID pelanggan atau koneksi database.",
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahPelangganActionPerformed

    private void btnUbahPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahPelangganActionPerformed
        // TODO: hubungkan ke PelangganController
        Pelanggan pelanggan = buildPelangganFromForm();
        if (pelanggan == null) {
            return;
        }

        if (pelangganController.updatePelanggan(pelanggan)) {
            JOptionPane.showMessageDialog(this,
                    "Data pelanggan berhasil diupdate!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengupdate data pelanggan. Periksa ID pelanggan atau koneksi database.",
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
        
    }//GEN-LAST:event_btnUbahPelangganActionPerformed

    private void btnHapusPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusPelangganActionPerformed
        // TODO: hubungkan ke PelangganController
        String idText = txtIdPelanggan.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data pelanggan pada tabel atau isi ID pelanggan terlebih dahulu!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPelanggan;
        try {
            idPelanggan = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID pelanggan harus berupa angka!",
                    "Input Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data pelanggan dengan ID " + idPelanggan + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (pelangganController.deletePelanggan(idPelanggan)) {
                JOptionPane.showMessageDialog(this,
                        "Data pelanggan berhasil dihapus!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data pelanggan. Periksa ID pelanggan atau koneksi database.",
                        "Gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusPelangganActionPerformed

    private void btnResetPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetPelangganActionPerformed
        txtIdPelanggan.setText("");
        txtNamaPelanggan.setText("");
        txtNoHpPelanggan.setText("");
        tblPelanggan.clearSelection();
    }//GEN-LAST:event_btnResetPelangganActionPerformed

    private void txtCariPelangganKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariPelangganKeyReleased
        String keyword = txtCariPelanggan.getText().trim();
        if (keyword.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
        }
    }//GEN-LAST:event_txtCariPelangganKeyReleased

    private void btnRefreshPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshPelangganActionPerformed
        // TODO: hubungkan ke PelangganController
        loadData();
    }//GEN-LAST:event_btnRefreshPelangganActionPerformed

    private void tblPelangganMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPelangganMouseClicked
        int row = tblPelanggan.getSelectedRow();
        if (row >= 0) {
            txtIdPelanggan.setText(tblPelanggan.getValueAt(row, 0).toString());
            txtNamaPelanggan.setText(tblPelanggan.getValueAt(row, 1).toString());
            txtNoHpPelanggan.setText(tblPelanggan.getValueAt(row, 2).toString());
        }
    }//GEN-LAST:event_tblPelangganMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new DashboardKasir().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapusItem;
    private javax.swing.JButton btnHapusPelanggan;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProsesKembali;
    private javax.swing.JButton btnRefreshAktif;
    private javax.swing.JButton btnRefreshPelanggan;
    private javax.swing.JButton btnRefreshTransaksi;
    private javax.swing.JButton btnResetPelanggan;
    private javax.swing.JButton btnResetSewa;
    private javax.swing.JButton btnSimpanSewa;
    private javax.swing.JButton btnTambahItem;
    private javax.swing.JButton btnTambahPelanggan;
    private javax.swing.JButton btnUbahPelanggan;
    private javax.swing.JComboBox<String> cbMetode;
    private javax.swing.JComboBox<String> cbPelanggan;
    private javax.swing.JScrollPane jScrollPaneAktif;
    private javax.swing.JScrollPane jScrollPaneAlat;
    private javax.swing.JScrollPane jScrollPaneDetailTransaksi;
    private javax.swing.JScrollPane jScrollPaneKembali;
    private javax.swing.JScrollPane jScrollPaneKeranjang;
    private javax.swing.JScrollPane jScrollPanePelanggan;
    private javax.swing.JScrollPane jScrollPaneTransaksi;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAktifTitle;
    private javax.swing.JLabel lblBayar;
    private javax.swing.JLabel lblCariPelanggan;
    private javax.swing.JLabel lblCariTransaksi;
    private javax.swing.JLabel lblDenda;
    private javax.swing.JLabel lblDetailKembaliTitle;
    private javax.swing.JLabel lblDetailTransaksiTitle;
    private javax.swing.JLabel lblIdPelanggan;
    private javax.swing.JLabel lblJumlah;
    private javax.swing.JLabel lblKembalian;
    private javax.swing.JLabel lblMetode;
    private javax.swing.JLabel lblNamaPelanggan;
    private javax.swing.JLabel lblNoHpPelanggan;
    private javax.swing.JLabel lblPelanggan;
    private javax.swing.JLabel lblPilihAlat;
    private javax.swing.JLabel lblTglKembali;
    private javax.swing.JLabel lblTglRencana;
    private javax.swing.JLabel lblTglSewa;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalBiaya;
    private javax.swing.JLabel lblTotalPelanggan;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPanel panelPelanggan;
    private javax.swing.JPanel panelPeminjaman;
    private javax.swing.JPanel panelPengembalian;
    private javax.swing.JPanel panelTransaksi;
    private javax.swing.JTabbedPane tabKasir;
    private javax.swing.JTable tblAlat;
    private javax.swing.JTable tblDetailKembali;
    private javax.swing.JTable tblDetailTransaksi;
    private javax.swing.JTable tblKeranjang;
    private javax.swing.JTable tblPelanggan;
    private javax.swing.JTable tblSewaAktif;
    private javax.swing.JTable tblTransaksi;
    private javax.swing.JTextField txtBayar;
    private javax.swing.JTextField txtCariPelanggan;
    private javax.swing.JTextField txtCariTransaksi;
    private javax.swing.JTextField txtDenda;
    private javax.swing.JTextField txtIdPelanggan;
    private javax.swing.JTextField txtJumlah;
    private javax.swing.JTextField txtKembalian;
    private javax.swing.JTextField txtNamaPelanggan;
    private javax.swing.JTextField txtNoHpPelanggan;
    private javax.swing.JTextField txtTglKembali;
    private javax.swing.JTextField txtTglRencanaKembali;
    private javax.swing.JTextField txtTglSewa;
    private javax.swing.JTextField txtTotalBiaya;
    // End of variables declaration//GEN-END:variables
}
