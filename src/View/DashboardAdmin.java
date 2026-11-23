/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Controller.AlatController;
import Controller.TransaksiController;
import Controller.UserController;
import Model.Alat;
import Model.User;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author ACER
 */
public class DashboardAdmin extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardAdmin.class.getName());
    private final AlatController alatController;
    private final UserController userController;
    private final TransaksiController transaksiController;
    private final DefaultTableModel tableModel;
    private final DefaultTableModel tableModelUser;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final TableRowSorter<DefaultTableModel> rowSorterUser;

    /**
     * Creates new form DashboardAdmin
     */
    public DashboardAdmin() {
        this("Admin");
    }

    public DashboardAdmin(String username) {
        initComponents();
        String namaUser = (username == null || username.trim().isEmpty()) ? "Admin" : username;
        lblUser.setText("Admin: " + namaUser);

        alatController = new AlatController();
        transaksiController = new TransaksiController();
        userController = new UserController();

        tableModel = alatController.createTable();
        tblAlat.setModel(tableModel);
        tblAlat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAlat.setDefaultEditor(Object.class, null);

        tableModelUser = userController.createTable();
        tblUser.setModel(tableModelUser);
        tblUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUser.setDefaultEditor(Object.class, null);
        
        rowSorter = new TableRowSorter<>(tableModel);
        tblAlat.setRowSorter(rowSorter);
        
        rowSorterUser = new TableRowSorter<>(tableModelUser);
        tblUser.setRowSorter(rowSorterUser);

        tblRiwayatTransaksi.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadData();
        loadRiwayatTransaksi();
        loadUser();
    }

    private void loadData() {
        alatController.readAlat();
        lblTotalAlat.setText("Total Alat: " + tableModel.getRowCount());
    }
    
    private void loadUser() {
        userController.readUser();
    }

    private void loadRiwayatTransaksi() {
        DefaultTableModel dtm = (DefaultTableModel) tblRiwayatTransaksi.getModel();
        transaksiController.readRiwayatTransaksi(dtm, txtCariTransaksi.getText());
        ((DefaultTableModel) tblDetailTransaksi.getModel()).setRowCount(0);
    }

    private String getCellValue(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    private Alat buildAlatFromForm() {
        String idText = txtIdAlat.getText().trim();
        String kode = txtKodeAlat.getText().trim();
        String nama = txtNamaAlat.getText().trim();
        Object katObj = cbKategori.getEditor().getItem();
        String kategori = katObj == null ? "" : katObj.toString().trim();
        if (kategori.isEmpty() && cbKategori.getSelectedItem() != null) {
            kategori = cbKategori.getSelectedItem().toString().trim();
        }
        String hargaText = txtHargaSewa.getText().trim();
        String stokText = txtStok.getText().trim();

        if (idText.isEmpty() || kode.isEmpty() || nama.isEmpty()
                || kategori.isEmpty() || hargaText.isEmpty() || stokText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field wajib diisi!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            int idAlat = Integer.parseInt(idText);
            double hargaSewa = Double.parseDouble(hargaText);
            int stok = Integer.parseInt(stokText);

            if (hargaSewa < 0 || stok < 0) {
                JOptionPane.showMessageDialog(this,
                        "Harga sewa dan stok tidak boleh negatif!",
                        "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return new Alat(idAlat, kode, nama, kategori, hargaSewa, stok);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID Alat, Harga Sewa, dan Stok harus berupa angka yang valid!",
                    "Input Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
    
    private User buildUserFromForm() {
        String idText = txtIdUser.getText().trim();
        String username = txtUsernameUser.getText().trim();
        String password = txtPasswordUser.getText().trim();
        Object roleObj = cbRoleUser.getEditor().getItem();
        String role = roleObj == null ? "" : roleObj.toString().trim();
        if (role.isEmpty() && cbKategori.getSelectedItem() != null) {
            role = cbRoleUser.getSelectedItem().toString().trim();
        }
        
        if (idText.isEmpty() || username.isEmpty() || password.isEmpty()
                || role.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Semua field wajib diisi!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        try {
            int idUser = Integer.parseInt(idText);

            return new User(idUser, username, password, role);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID User harus berupa angka yang valid!",
                    "Input Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
    }

    private void resetForm() {
        txtIdAlat.setText("");
        txtKodeAlat.setText("");
        txtNamaAlat.setText("");
        cbKategori.setSelectedIndex(0);
        txtHargaSewa.setText("");
        txtStok.setText("");
        tblAlat.clearSelection();
        txtIdAlat.requestFocus();
    }
    
    private void resetFormUser(){
        txtIdUser.setText("");
        txtUsernameUser.setText("");
        txtPasswordUser.setText("");
        cbRoleUser.setSelectedIndex(0);
        tblUser.clearSelection();
        txtIdUser.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        lblTitle = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        tabAdmin = new javax.swing.JTabbedPane();
        panelAlat = new javax.swing.JPanel();
        lblIdAlat = new javax.swing.JLabel();
        txtIdAlat = new javax.swing.JTextField();
        lblKodeAlat = new javax.swing.JLabel();
        txtKodeAlat = new javax.swing.JTextField();
        lblNamaAlat = new javax.swing.JLabel();
        txtNamaAlat = new javax.swing.JTextField();
        lblKategori = new javax.swing.JLabel();
        cbKategori = new javax.swing.JComboBox<>();
        lblHargaSewa = new javax.swing.JLabel();
        txtHargaSewa = new javax.swing.JTextField();
        lblStok = new javax.swing.JLabel();
        txtStok = new javax.swing.JTextField();
        btnTambah = new javax.swing.JButton();
        btnUbah = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        lblCari = new javax.swing.JLabel();
        txtCari = new javax.swing.JTextField();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAlat = new javax.swing.JTable();
        lblTotalAlat = new javax.swing.JLabel();
        panelRiwayat = new javax.swing.JPanel();
        lblCariTransaksi = new javax.swing.JLabel();
        txtCariTransaksi = new javax.swing.JTextField();
        btnRefreshTransaksi = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRiwayatTransaksi = new javax.swing.JTable();
        lblDetailTitle = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetailTransaksi = new javax.swing.JTable();
        panelUser = new javax.swing.JPanel();
        lblIdUser = new javax.swing.JLabel();
        txtIdUser = new javax.swing.JTextField();
        lblUsernameUser = new javax.swing.JLabel();
        txtUsernameUser = new javax.swing.JTextField();
        lblPasswordUser = new javax.swing.JLabel();
        txtPasswordUser = new javax.swing.JPasswordField();
        lblRoleUser = new javax.swing.JLabel();
        cbRoleUser = new javax.swing.JComboBox<>();
        btnTambahUser = new javax.swing.JButton();
        btnUbahUser = new javax.swing.JButton();
        btnHapusUser = new javax.swing.JButton();
        btnResetUser = new javax.swing.JButton();
        lblCariUser = new javax.swing.JLabel();
        txtCariUser = new javax.swing.JTextField();
        btnRefreshUser = new javax.swing.JButton();
        jScrollPaneUser = new javax.swing.JScrollPane();
        tblUser = new javax.swing.JTable();
        lblTotalUser = new javax.swing.JLabel();

        jScrollPane4.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dashboard Admin");

        lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitle.setText("Dashboard Admin");

        lblUser.setText("Admin: admin");

        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        lblIdAlat.setText("ID Alat");

        lblKodeAlat.setText("Kode Alat");

        lblNamaAlat.setText("Nama Alat");

        lblKategori.setText("Kategori");

        cbKategori.setEditable(true);
        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Outdoor", "Tas", "Memasak", "Aksesoris", "Lainnya" }));

        lblHargaSewa.setText("Harga Sewa / Hari");

        lblStok.setText("Stok");

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnUbah.setText("Ubah");
        btnUbah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        lblCari.setText("Cari:");

        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCariKeyReleased(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        tblAlat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Kode Alat", "Nama Alat", "Kategori", "Harga Sewa (per hari)", "Stok"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAlat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAlatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblAlat);

        lblTotalAlat.setText("Total Alat: 0");

        javax.swing.GroupLayout panelAlatLayout = new javax.swing.GroupLayout(panelAlat);
        panelAlat.setLayout(panelAlatLayout);
        panelAlatLayout.setHorizontalGroup(
            panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlatLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIdAlat)
                    .addComponent(txtIdAlat)
                    .addComponent(lblKodeAlat)
                    .addComponent(txtKodeAlat)
                    .addComponent(lblNamaAlat)
                    .addComponent(txtNamaAlat)
                    .addComponent(lblKategori)
                    .addComponent(cbKategori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblHargaSewa)
                    .addComponent(txtHargaSewa)
                    .addComponent(lblStok)
                    .addComponent(txtStok)
                    .addGroup(panelAlatLayout.createSequentialGroup()
                        .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnTambah, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(btnHapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUbah, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(24, 24, 24)
                .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAlatLayout.createSequentialGroup()
                        .addComponent(lblCari)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefresh))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addGroup(panelAlatLayout.createSequentialGroup()
                        .addComponent(lblTotalAlat)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        panelAlatLayout.setVerticalGroup(
            panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAlatLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAlatLayout.createSequentialGroup()
                        .addComponent(lblIdAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblKodeAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtKodeAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblNamaAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNamaAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblKategori)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblHargaSewa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHargaSewa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStok)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTambah)
                            .addComponent(btnUbah))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHapus)
                            .addComponent(btnReset))
                        .addGap(0, 24, Short.MAX_VALUE))
                    .addGroup(panelAlatLayout.createSequentialGroup()
                        .addGroup(panelAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCari)
                            .addComponent(txtCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRefresh))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalAlat)))
                .addGap(12, 12, 12))
        );

        tabAdmin.addTab("Data Alat", panelAlat);

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

        tblRiwayatTransaksi.setModel(new javax.swing.table.DefaultTableModel(
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
        tblRiwayatTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRiwayatTransaksiMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblRiwayatTransaksi);

        lblDetailTitle.setText("Detail Item Transaksi (klik baris di atas):");

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
        jScrollPane3.setViewportView(tblDetailTransaksi);

        javax.swing.GroupLayout panelRiwayatLayout = new javax.swing.GroupLayout(panelRiwayat);
        panelRiwayat.setLayout(panelRiwayatLayout);
        panelRiwayatLayout.setHorizontalGroup(
            panelRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRiwayatLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addGroup(panelRiwayatLayout.createSequentialGroup()
                        .addComponent(lblCariTransaksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCariTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefreshTransaksi))
                    .addGroup(panelRiwayatLayout.createSequentialGroup()
                        .addComponent(lblDetailTitle)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        panelRiwayatLayout.setVerticalGroup(
            panelRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRiwayatLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelRiwayatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCariTransaksi)
                    .addComponent(txtCariTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefreshTransaksi))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDetailTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        tabAdmin.addTab("Riwayat Transaksi", panelRiwayat);

        lblIdUser.setText("ID User");

        lblUsernameUser.setText("Username");

        lblPasswordUser.setText("Password");

        lblRoleUser.setText("Role");

        cbRoleUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "admin", "user" }));

        btnTambahUser.setText("Tambah");
        btnTambahUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahUserActionPerformed(evt);
            }
        });

        btnUbahUser.setText("Ubah");
        btnUbahUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUbahUserActionPerformed(evt);
            }
        });

        btnHapusUser.setText("Hapus");
        btnHapusUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusUserActionPerformed(evt);
            }
        });

        btnResetUser.setText("Reset");
        btnResetUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetUserActionPerformed(evt);
            }
        });

        lblCariUser.setText("Cari:");

        btnRefreshUser.setText("Refresh");
        btnRefreshUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshUserActionPerformed(evt);
            }
        });

        tblUser.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Username", "Password", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUserMouseClicked(evt);
            }
        });
        jScrollPaneUser.setViewportView(tblUser);

        lblTotalUser.setText("Total User: 0");

        javax.swing.GroupLayout panelUserLayout = new javax.swing.GroupLayout(panelUser);
        panelUser.setLayout(panelUserLayout);
        panelUserLayout.setHorizontalGroup(
            panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUserLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblIdUser)
                    .addComponent(txtIdUser)
                    .addComponent(lblUsernameUser)
                    .addComponent(txtUsernameUser)
                    .addComponent(lblPasswordUser)
                    .addComponent(txtPasswordUser)
                    .addComponent(lblRoleUser)
                    .addComponent(cbRoleUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelUserLayout.createSequentialGroup()
                        .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnTambahUser, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(btnHapusUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnUbahUser, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                            .addComponent(btnResetUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(24, 24, 24)
                .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUserLayout.createSequentialGroup()
                        .addComponent(lblCariUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCariUser, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefreshUser))
                    .addComponent(jScrollPaneUser, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addGroup(panelUserLayout.createSequentialGroup()
                        .addComponent(lblTotalUser)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        panelUserLayout.setVerticalGroup(
            panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUserLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelUserLayout.createSequentialGroup()
                        .addComponent(lblIdUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblUsernameUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUsernameUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPasswordUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPasswordUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblRoleUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbRoleUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTambahUser)
                            .addComponent(btnUbahUser))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHapusUser)
                            .addComponent(btnResetUser))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelUserLayout.createSequentialGroup()
                        .addGroup(panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCariUser)
                            .addComponent(txtCariUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRefreshUser))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPaneUser, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblTotalUser)))
                .addGap(12, 12, 12))
        );

        tabAdmin.addTab("Data User", panelUser);

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
                    .addComponent(tabAdmin))
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
                .addComponent(tabAdmin)
                .addGap(16, 16, 16))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        Alat alat = buildAlatFromForm();
        if (alat == null) {
            return;
        }

        if (alatController.createAlat(alat)) {
            JOptionPane.showMessageDialog(this,
                    "Data alat berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal menambahkan data alat. Periksa ID/Kode alat atau koneksi database.",
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnUbahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahActionPerformed
        Alat alat = buildAlatFromForm();
        if (alat == null) {
            return;
        }

        if (alatController.updateAlat(alat)) {
            JOptionPane.showMessageDialog(this,
                    "Data alat berhasil diubah!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengubah data alat. Periksa ID alat atau koneksi database.",
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        String idText = txtIdAlat.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data alat pada tabel atau isi ID Alat terlebih dahulu!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAlat;
        try {
            idAlat = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID Alat harus berupa angka!",
                    "Input Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus alat dengan ID " + idAlat + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (alatController.deleteAlat(idAlat)) {
                JOptionPane.showMessageDialog(this,
                        "Data alat berhasil dihapus!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadData();
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data alat. Periksa ID alat atau koneksi database.",
                        "Gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetForm();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadData();
    }//GEN-LAST:event_btnRefreshActionPerformed

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

    private void tblAlatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAlatMouseClicked
        int viewRow = tblAlat.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = tblAlat.convertRowIndexToModel(viewRow);
            txtIdAlat.setText(getCellValue(modelRow, 0));
            txtKodeAlat.setText(getCellValue(modelRow, 1));
            txtNamaAlat.setText(getCellValue(modelRow, 2));
            cbKategori.setSelectedItem(getCellValue(modelRow, 3));
            txtHargaSewa.setText(getCellValue(modelRow, 4));
            txtStok.setText(getCellValue(modelRow, 5));
        }
    }//GEN-LAST:event_tblAlatMouseClicked

    private void txtCariKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyReleased
        String keyword = txtCari.getText().trim();
        if (keyword.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(keyword)));
        }
    }//GEN-LAST:event_txtCariKeyReleased

    private void txtCariTransaksiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariTransaksiKeyReleased
        loadRiwayatTransaksi();
    }//GEN-LAST:event_txtCariTransaksiKeyReleased

    private void btnRefreshTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshTransaksiActionPerformed
        loadRiwayatTransaksi();
    }//GEN-LAST:event_btnRefreshTransaksiActionPerformed

    private void tblRiwayatTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRiwayatTransaksiMouseClicked
        int row = tblRiwayatTransaksi.getSelectedRow();
        if (row >= 0) {
            int idPenyewaan = Integer.parseInt(tblRiwayatTransaksi.getValueAt(row, 0).toString());
            DefaultTableModel detailModel = (DefaultTableModel) tblDetailTransaksi.getModel();
            transaksiController.readDetailTransaksi(detailModel, idPenyewaan);
        }
    }//GEN-LAST:event_tblRiwayatTransaksiMouseClicked

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

    private void btnTambahUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahUserActionPerformed
        // TODO add your handling code here:
        User user = buildUserFromForm();
        if (user == null) {
            return;
        }

        if (userController.createUser(user)) {
            JOptionPane.showMessageDialog(this,
                    "Data user berhasil ditambahkan!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUser();
            resetFormUser();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal menambahkan data user. Periksa ID user atau koneksi database.",
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTambahUserActionPerformed

    private void btnUbahUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUbahUserActionPerformed
        // TODO add your handling code here:
        User user = buildUserFromForm();
        if (user == null) {
            return;
        }

        if (userController.updateUser(user)) {
            JOptionPane.showMessageDialog(this,
                    "Data user berhasil diupdate!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            loadUser();
            resetFormUser();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gagal mengupdate data user. Periksa ID user atau koneksi database.",
                    "Gagal",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnUbahUserActionPerformed

    private void btnHapusUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusUserActionPerformed
        // TODO add your handling code here:
        String idText = txtIdUser.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih data user pada tabel atau isi ID user terlebih dahulu!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUser;
        try {
            idUser = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID user harus berupa angka!",
                    "Input Tidak Valid",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus data user dengan ID " + idUser + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(idUser)) {
                JOptionPane.showMessageDialog(this,
                        "Data user berhasil dihapus!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
                loadUser();
                resetFormUser();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data user. Periksa ID user atau koneksi database.",
                        "Gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHapusUserActionPerformed

    private void btnResetUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetUserActionPerformed
        // TODO add your handling code here:
        txtIdUser.setText("");
        txtUsernameUser.setText("");
        txtPasswordUser.setText("");
        cbRoleUser.setSelectedIndex(0);
        tblUser.clearSelection();
        txtIdUser.requestFocus();
    }//GEN-LAST:event_btnResetUserActionPerformed

    private void btnRefreshUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshUserActionPerformed
        // TODO add your handling code here:
        loadUser();
    }//GEN-LAST:event_btnRefreshUserActionPerformed

    private void tblUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblUserMouseClicked
        // TODO add your handling code here:
        int row = tblUser.getSelectedRow();
        if (row >= 0) {
            txtIdUser.setText(tblUser.getValueAt(row, 0).toString());
            txtUsernameUser.setText(tblUser.getValueAt(row, 1).toString());
            txtPasswordUser.setText(tblUser.getValueAt(row, 2).toString());
            cbRoleUser.setSelectedItem(tblUser.getValueAt(row, 3).toString());
        }
    }//GEN-LAST:event_tblUserMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new DashboardAdmin().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnHapusUser;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRefreshTransaksi;
    private javax.swing.JButton btnRefreshUser;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnResetUser;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btnTambahUser;
    private javax.swing.JButton btnUbah;
    private javax.swing.JButton btnUbahUser;
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JComboBox<String> cbRoleUser;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPaneUser;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCari;
    private javax.swing.JLabel lblCariTransaksi;
    private javax.swing.JLabel lblCariUser;
    private javax.swing.JLabel lblDetailTitle;
    private javax.swing.JLabel lblHargaSewa;
    private javax.swing.JLabel lblIdAlat;
    private javax.swing.JLabel lblIdUser;
    private javax.swing.JLabel lblKategori;
    private javax.swing.JLabel lblKodeAlat;
    private javax.swing.JLabel lblNamaAlat;
    private javax.swing.JLabel lblPasswordUser;
    private javax.swing.JLabel lblRoleUser;
    private javax.swing.JLabel lblStok;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalAlat;
    private javax.swing.JLabel lblTotalUser;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblUsernameUser;
    private javax.swing.JPanel panelAlat;
    private javax.swing.JPanel panelRiwayat;
    private javax.swing.JPanel panelUser;
    private javax.swing.JTabbedPane tabAdmin;
    private javax.swing.JTable tblAlat;
    private javax.swing.JTable tblDetailTransaksi;
    private javax.swing.JTable tblRiwayatTransaksi;
    private javax.swing.JTable tblUser;
    private javax.swing.JTextField txtCari;
    private javax.swing.JTextField txtCariTransaksi;
    private javax.swing.JTextField txtCariUser;
    private javax.swing.JTextField txtHargaSewa;
    private javax.swing.JTextField txtIdAlat;
    private javax.swing.JTextField txtIdUser;
    private javax.swing.JTextField txtKodeAlat;
    private javax.swing.JTextField txtNamaAlat;
    private javax.swing.JPasswordField txtPasswordUser;
    private javax.swing.JTextField txtStok;
    private javax.swing.JTextField txtUsernameUser;
    // End of variables declaration//GEN-END:variables
}
