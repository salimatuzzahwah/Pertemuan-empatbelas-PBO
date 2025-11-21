/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pertemuan14;

/**
 *
 * @author Salimatuz Zahwah
 */
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.*;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import javax.persistence.*;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Salimatuz Zahwah
 */
public class DataKendaraan extends javax.swing.JFrame {

    private EntityManagerFactory emf;
    private EntityManager em;

    public void connect() {
        try {
            emf = Persistence.createEntityManagerFactory("pertemuan14PU");
            em = emf.createEntityManager();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Koneksi gagal: " + e.getMessage());
        }
    }

    String PlatNomorLama, IdPemilikLama, MerkLama, TahunLama;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DataKendaraan.class.getName());

    /**
     * Creates new form DataKendaraan
     */
    public DataKendaraan() {
        initComponents();
        connect();
        showTableKendaraan();
        showTablePemilik();
    }

    private void downloadkeCSVPemilik(JTable table, String filename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan sebagai CSV");
        fileChooser.setSelectedFile(new File(filename));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Tambahkan .csv jika belum ada
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {

                DefaultTableModel model = (DefaultTableModel) table.getModel();

                // Header CSV
                writer.write("ID Pemilik;Nama Pemilik;Alamat;No HP\n");

                for (int i = 0; i < model.getRowCount(); i++) {

                    String idPemilik = model.getValueAt(i, 0).toString();
                    String namaPemilik = model.getValueAt(i, 1).toString();
                    String alamat = model.getValueAt(i, 2).toString();
                    String noHp = model.getValueAt(i, 3).toString();

                    writer.write(
                            idPemilik + ";"
                            + namaPemilik + ";"
                            + alamat + ";"
                            + noHp + "\n"
                    );
                }

                JOptionPane.showMessageDialog(this,
                        "Data pemilik berhasil didownload ke: " + fileToSave.getAbsolutePath());

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal download data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void downloadkeCSV(JTable table, String filename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan sebagai CSV");
        fileChooser.setSelectedFile(new File(filename));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Pakai .csv
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {

                DefaultTableModel model = (DefaultTableModel) table.getModel();

                // Tulis header manual agar sesuai CSV final
                writer.write("Plat Nomor;ID Pemilik;Merk;Tahun\n");

                for (int i = 0; i < model.getRowCount(); i++) {

                    String platNomor = model.getValueAt(i, 0).toString();
                    String namaPemilik = model.getValueAt(i, 1).toString();  // di tabel → nama
                    String merk = model.getValueAt(i, 2).toString();
                    String tahun = model.getValueAt(i, 3).toString();

                    // Convert nama pemilik → ID Pemilik melalui database
                    TypedQuery<Pemilik> q = em.createQuery(
                            "SELECT p FROM Pemilik p WHERE LOWER(p.namaPemilik) = LOWER(:nama)",
                            Pemilik.class
                    );
                    q.setParameter("nama", namaPemilik);

                    String idPemilik = "";
                    try {
                        Pemilik p = q.getSingleResult();
                        idPemilik = p.getIdPemilik();   // INI YANG MAU DITULIS
                    } catch (Exception e) {
                        idPemilik = "UNKNOWN";
                    }

                    // tulis ke CSV
                    writer.write(
                            platNomor + ";"
                            + idPemilik + ";"
                            + merk + ";"
                            + tahun + "\n"
                    );
                }

                JOptionPane.showMessageDialog(this,
                        "Data berhasil didownload ke: " + fileToSave.getAbsolutePath());

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error saat mengdownload data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void showTablePemilik() {
        try {
            em.clear();

            List<Pemilik> hasil = em.createQuery(
            "SELECT p FROM Pemilik p ORDER BY p.idPemilik ASC", Pemilik.class
            ).getResultList();

            // Buat model untuk JTable
            
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Id Pemilik", "Nama Pemilik", "Alamat", "No Hp"}, 0
            );

            // Masukkan data ke model
            for (Pemilik rs : hasil) {
                model.addRow(new Object[]{
                    rs.getIdPemilik(),
                    rs.getNamaPemilik(),
                    rs.getAlamat(),
                    rs.getNoHp()
                });
            }

            // Tampilkan model ke JTable
            tb2.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil data: " + e.getMessage());
        }
    }

    public void showTableKendaraan() {
        try {
            em.clear();

           List<Kendaraan> hasil = em.createQuery(
            "SELECT k FROM Kendaraan k ORDER BY k.idPemilik ASC", Kendaraan.class
            ).getResultList();
           
            // Buat model untuk JTable
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Plat Nomor", "Nama Pemilik", "Merk", "Tahun"}, 0
            );

            // Masukkan data ke model
            for (Kendaraan rs : hasil) {
                model.addRow(new Object[]{
                    rs.getPlatNomor(),
                    rs.getIdPemilik().getNamaPemilik(),
                    rs.getMerk(),
                    rs.getTahun()

                });
            }

            // Tampilkan model ke JTable
            tb1.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tampil data: " + e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb1 = new javax.swing.JTable();
        btnInsert = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnupload = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnclose1 = new javax.swing.JButton();
        btnclose = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb2 = new javax.swing.JTable();
        btnInsert1 = new javax.swing.JButton();
        btnupload1 = new javax.swing.JButton();
        btnUpdate1 = new javax.swing.JButton();
        btnDelete1 = new javax.swing.JButton();
        btnclose2 = new javax.swing.JButton();
        btnclose3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DATA KENDARAAN");

        tb1.setBackground(new java.awt.Color(204, 204, 204));
        tb1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tb1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Plat Nomor", "Id Pemilik", "Merk", "Tahun"
            }
        ));
        tb1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tb1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tb1);

        btnInsert.setBackground(new java.awt.Color(0, 0, 0));
        btnInsert.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnInsert.setForeground(new java.awt.Color(255, 255, 255));
        btnInsert.setText("Insert");
        btnInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsertActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(0, 0, 0));
        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUpdate.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnupload.setBackground(new java.awt.Color(0, 0, 0));
        btnupload.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnupload.setForeground(new java.awt.Color(255, 255, 255));
        btnupload.setText("Upload");
        btnupload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnuploadActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(0, 0, 0));
        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnclose1.setBackground(new java.awt.Color(0, 0, 0));
        btnclose1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnclose1.setForeground(new java.awt.Color(255, 255, 255));
        btnclose1.setText("Cetak");
        btnclose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclose1ActionPerformed(evt);
            }
        });

        btnclose.setBackground(new java.awt.Color(0, 0, 0));
        btnclose.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnclose.setForeground(new java.awt.Color(255, 255, 255));
        btnclose.setText("Close");
        btnclose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncloseActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Download");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnInsert)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnupload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnclose1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnclose)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsert)
                    .addComponent(btnUpdate)
                    .addComponent(btnupload)
                    .addComponent(btnDelete)
                    .addComponent(btnclose1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnclose)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        jTabbedPane1.addTab("KENDARAAN", jPanel2);

        tb2.setBackground(new java.awt.Color(204, 204, 204));
        tb2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tb2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id Pemilik", "Nama Pemilik", "Alamat", "No Hp"
            }
        ));
        jScrollPane2.setViewportView(tb2);

        btnInsert1.setBackground(new java.awt.Color(0, 0, 0));
        btnInsert1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnInsert1.setForeground(new java.awt.Color(255, 255, 255));
        btnInsert1.setText("Insert");
        btnInsert1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInsert1ActionPerformed(evt);
            }
        });

        btnupload1.setBackground(new java.awt.Color(0, 0, 0));
        btnupload1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnupload1.setForeground(new java.awt.Color(255, 255, 255));
        btnupload1.setText("Upload");
        btnupload1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupload1ActionPerformed(evt);
            }
        });

        btnUpdate1.setBackground(new java.awt.Color(0, 0, 0));
        btnUpdate1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUpdate1.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdate1.setText("Update");
        btnUpdate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdate1ActionPerformed(evt);
            }
        });

        btnDelete1.setBackground(new java.awt.Color(0, 0, 0));
        btnDelete1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnDelete1.setForeground(new java.awt.Color(255, 255, 255));
        btnDelete1.setText("Delete");
        btnDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete1ActionPerformed(evt);
            }
        });

        btnclose2.setBackground(new java.awt.Color(0, 0, 0));
        btnclose2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnclose2.setForeground(new java.awt.Color(255, 255, 255));
        btnclose2.setText("Close");
        btnclose2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclose2ActionPerformed(evt);
            }
        });

        btnclose3.setBackground(new java.awt.Color(0, 0, 0));
        btnclose3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnclose3.setForeground(new java.awt.Color(255, 255, 255));
        btnclose3.setText("Cetak");
        btnclose3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclose3ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Download");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnInsert1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnupload1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnclose3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnclose2))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsert1)
                    .addComponent(btnUpdate1)
                    .addComponent(btnupload1)
                    .addComponent(btnDelete1)
                    .addComponent(btnclose3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnclose2)
                    .addComponent(jButton2))
                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("PEMILIK", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 517, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(158, 158, 158))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsertActionPerformed
        Insert1 dialog = new Insert1(this, true); // true = modal
        dialog.setLocationRelativeTo(this); // supaya muncul di tengah
        dialog.setVisible(true);

        showTableKendaraan();
        showTablePemilik();
    }//GEN-LAST:event_btnInsertActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        int row = tb1.getSelectedRow();
        if (row != -1) {
            String platNomor = tb1.getValueAt(row, 0).toString();
            String idPemilik = tb1.getValueAt(row, 1).toString();
            String merk = tb1.getValueAt(row, 2).toString();
            int tahun = Integer.parseInt(tb1.getValueAt(row, 3).toString());

            // Panggil dialog update kendaraan
            Update1 dialog = new Update1(this, true, platNomor, idPemilik, merk, tahun);
            dialog.setVisible(true);

            // Refresh tabel kendaraan
            showTableKendaraan();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diperbarui terlebih dahulu!");
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void tb1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tb1MouseClicked
        int row = tb1.getSelectedRow();

        if (row != -1) { // pastikan baris dipilih
            String platNomor = tb1.getValueAt(row, 0).toString();
            String idPemilik = tb1.getValueAt(row, 1).toString();
            String merk = tb1.getValueAt(row, 2).toString();

            int tahun = 0;
            Object tahunObj = tb1.getValueAt(row, 3);
            if (tahunObj != null) {
                try {
                    tahun = Integer.parseInt(tahunObj.toString());
                } catch (NumberFormatException e) {
                    tahun = 0; // fallback jika data tidak valid
                }
            }

            // Contoh: tampilkan data di console (atau simpan ke variabel global)
            System.out.println("Plat Nomor: " + platNomor);
            System.out.println("ID Pemilik: " + idPemilik);
            System.out.println("Merk: " + merk);
            System.out.println("Tahun: " + tahun);
        }
    }//GEN-LAST:event_tb1MouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = tb1.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil data dari JTable
            String plat_nomor = tb1.getValueAt(selectedRow, 0).toString(); // Primary Key
            String id_pemilik = tb1.getValueAt(selectedRow, 1).toString(); // Foreign Key
            String merk = tb1.getValueAt(selectedRow, 2).toString();
            String tahun = tb1.getValueAt(selectedRow, 3).toString(); // biar aman disimpan sebagai String

            // Kirim data ke dialog Delete1 (konfirmasi penghapusan)
            Delete1 dialog = new Delete1(this, true, plat_nomor, id_pemilik, merk, tahun);
            dialog.setLocationRelativeTo(this); // Supaya muncul di tengah layar
            dialog.setVisible(true);

            // Refresh data tabel setelah penghapusan
            showTableKendaraan();

        } else {
            JOptionPane.showMessageDialog(this, " Pilih data kendaraan yang ingin dihapus terlebih dahulu!");
        }

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnclose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclose1ActionPerformed
        try {
            String path = "C:\\Users\\Salimatuz Zahwah\\OneDrive\\Documents\\NetBeansProjects\\pertemuan12\\src\\pertemuan_12\\kendaraan.jasper";
            HashMap<String, Object> parameters = new HashMap<>();

            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/PBO_12", "postgres", "elma123");

            JasperPrint jprint = JasperFillManager.fillReport(path, parameters, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setSize(800, 600);
            jviewer.setLocationRelativeTo(this);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnclose1ActionPerformed

    private void btnuploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnuploadActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File filePilihan = jfc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(filePilihan))) {

                String line;
                br.readLine();

                em.getTransaction().begin();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data.length == 4) {
                        Kendaraan k = new Kendaraan();
                        k.setPlatNomor(data[0].trim());
                        String idPemilikStr = data[1].trim();
                        Pemilik pemilik = em.find(Pemilik.class, idPemilikStr);

                        if (pemilik == null) {
                            System.out.println("Pemilik tidak ditemukan: " + idPemilikStr);
                            continue; // skip baris ini
                        }
                        k.setIdPemilik(pemilik);
                        k.setMerk(data[2].trim());
                        k.setTahun(Integer.parseInt(data[3].trim()));

                        em.persist(k);
                    }
                }

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari CSV!");
                showTableKendaraan();
                showTablePemilik();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal upload: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }//GEN-LAST:event_btnuploadActionPerformed

    private void btnclose3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclose3ActionPerformed
        // TODO add your handling code here:
        try {
            String path = "C:\\Users\\Salimatuz Zahwah\\OneDrive\\Documents\\NetBeansProjects\\pertemuan12\\src\\pertemuan_12\\pemilik.jasper";
            HashMap<String, Object> parameters = new HashMap<>();

            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/PBO_12", "postgres", "elma123");

            JasperPrint jprint = JasperFillManager.fillReport(path, parameters, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setSize(800, 600);
            jviewer.setLocationRelativeTo(this);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnclose3ActionPerformed

    private void btncloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncloseActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btncloseActionPerformed

    private void btnclose2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclose2ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnclose2ActionPerformed

    private void btnUpdate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdate1ActionPerformed
        // TODO add your handling code here:
        int row = tb2.getSelectedRow();
        if (row != -1) {
            String idPemilik = tb2.getValueAt(row, 0).toString();
            String namaPemilik = tb2.getValueAt(row, 1).toString();
            String alamat = tb2.getValueAt(row, 2).toString();
            String nohp = tb2.getValueAt(row, 3).toString();

            // Panggil dialog update kendaraan
            Update2 dialog = new Update2(this, true, idPemilik, namaPemilik, alamat, nohp);
            dialog.setVisible(true);

            // Refresh tabel kendaraan
            showTablePemilik();
            showTableKendaraan();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diperbarui terlebih dahulu!");
        }
    }//GEN-LAST:event_btnUpdate1ActionPerformed

    private void btnDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete1ActionPerformed
        // TODO add your handling code here:
        int selectedRow = tb2.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil data dari JTable
            String id_pemilik = tb2.getValueAt(selectedRow, 0).toString(); // Primary Key
            String nama_pemilik = tb2.getValueAt(selectedRow, 1).toString();
            String alamat = tb2.getValueAt(selectedRow, 2).toString();
            String nohp = tb2.getValueAt(selectedRow, 3).toString(); // biar aman disimpan sebagai String

            // Kirim data ke dialog Delete1 (konfirmasi penghapusan)
            Delete2 dialog = new Delete2(this, true, id_pemilik, nama_pemilik, alamat, nohp);
            dialog.setLocationRelativeTo(this); // Supaya muncul di tengah layar
            dialog.setVisible(true);

            // Refresh data tabel setelah penghapusan
            showTablePemilik();
            showTableKendaraan();

        } else {
            JOptionPane.showMessageDialog(this, " Pilih data pemilik yang ingin dihapus terlebih dahulu!");
        }

    }//GEN-LAST:event_btnDelete1ActionPerformed

    private void btnInsert1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInsert1ActionPerformed
        // TODO add your handling code here:
        Insert2 dialog = new Insert2(this, true); // true = modal
        dialog.setLocationRelativeTo(this); // supaya muncul di tengah
        dialog.setVisible(true);

        showTablePemilik();
    }//GEN-LAST:event_btnInsert1ActionPerformed

    private void btnupload1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupload1ActionPerformed
        // TODO add your handling code here:
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File filePilihan = jfc.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(filePilihan))) {

                String line;
                br.readLine();

                em.getTransaction().begin();
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");

                    // CSV harus punya 4 kolom
                    if (data.length == 4) {

                        String idPemilik = data[0].trim();
                        String namaPemilik = data[1].trim();
                        String alamat = data[2].trim();
                        String noHp = data[3].trim();

                        // Cek apakah ID sudah ada di database → kalau ada update, kalau belum buat baru
                        Pemilik p = em.find(Pemilik.class, idPemilik);

                        if (p == null) {
                            p = new Pemilik();
                            p.setIdPemilik(idPemilik);
                        }

                        p.setNamaPemilik(namaPemilik);
                        p.setAlamat(alamat);
                        p.setNoHp(noHp);

                        em.persist(p);
                    }
                }

                em.getTransaction().commit();
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari CSV!");
              
                showTablePemilik();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal upload: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnupload1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        downloadkeCSV(tb1, "data_kendaraan.csv");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        downloadkeCSVPemilik(tb2, "data_pemilik.csv");
    }//GEN-LAST:event_jButton2ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new DataKendaraan().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDelete1;
    private javax.swing.JButton btnInsert;
    private javax.swing.JButton btnInsert1;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdate1;
    private javax.swing.JButton btnclose;
    private javax.swing.JButton btnclose1;
    private javax.swing.JButton btnclose2;
    private javax.swing.JButton btnclose3;
    private javax.swing.JButton btnupload;
    private javax.swing.JButton btnupload1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tb1;
    private javax.swing.JTable tb2;
    // End of variables declaration//GEN-END:variables
}
