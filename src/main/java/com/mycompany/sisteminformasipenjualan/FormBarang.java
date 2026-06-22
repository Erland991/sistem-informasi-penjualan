package com.mycompany.sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormBarang extends JFrame {

    static final Color C_BG      = new Color(245, 247, 255);
    static final Color C_WHITE   = Color.WHITE;
    static final Color C_PRIMARY = new Color(67, 97, 238);
    static final Color C_GREEN   = new Color(3, 160, 98);
    static final Color C_RED     = new Color(231, 76, 60);
    static final Color C_TEXT    = new Color(30, 30, 60);
    static final Color C_GRAY    = new Color(120, 130, 160);
    static final Color C_BORDER  = new Color(210, 215, 235);

    JTextField tfId, tfNama, tfSatuan, tfHarga, tfStok;
    JComboBox<String> cbKategori;
    JButton btnBaru, btnSimpan, btnEdit, btnHapus, btnBatal;
    JTable table;
    DefaultTableModel model;
    boolean modeEdit = false;

    public FormBarang() {
        setTitle("Data Barang - Toko Berkah Jaya");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(10, 10));

        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel h = new JLabel("📦  Data Barang");
        h.setFont(new Font("Segoe UI", Font.BOLD, 16));
        h.setForeground(Color.WHITE);
        header.add(h, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ── FORM CARD ──
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(C_WHITE);
        formCard.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1),
            new EmptyBorder(16, 16, 12, 16)
        ));

        JLabel formTitle = new JLabel("Form Input Barang");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTitle.setForeground(C_PRIMARY);
        formTitle.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel fields = new JPanel(new GridLayout(3, 4, 10, 8));
        fields.setBackground(C_WHITE);

        tfId     = field(); tfNama = field();
        tfSatuan = field(); tfHarga = field(); tfStok = field();
        cbKategori = new JComboBox<>(new String[]{"1 - Elektronik","2 - Makanan & Minuman","3 - Peralatan Rumah","4 - Pakaian"});
        cbKategori.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        fields.add(lbl("ID Barang *")); fields.add(tfId);
        fields.add(lbl("Nama Barang *")); fields.add(tfNama);
        fields.add(lbl("Kategori")); fields.add(cbKategori);
        fields.add(lbl("Satuan")); fields.add(tfSatuan);
        fields.add(lbl("Harga Jual *")); fields.add(tfHarga);
        fields.add(lbl("Stok *")); fields.add(tfStok);

        // Tombol aksi
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(C_WHITE);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));
        btnBaru   = btn("🆕 Baru",   new Color(100,116,139));
        btnSimpan = btn("💾 Simpan", C_GREEN);
        btnEdit   = btn("✏ Edit",   C_PRIMARY);
        btnHapus  = btn("🗑 Hapus",  C_RED);
        btnBatal  = btn("✖ Batal",  new Color(180,180,180));
        btnRow.add(btnBaru); btnRow.add(btnSimpan);
        btnRow.add(btnEdit); btnRow.add(btnHapus); btnRow.add(btnBatal);

        JPanel formInner = new JPanel(new BorderLayout());
        formInner.setBackground(C_WHITE);
        formInner.add(formTitle, BorderLayout.NORTH);
        formInner.add(fields, BorderLayout.CENTER);
        formInner.add(btnRow, BorderLayout.SOUTH);
        formCard.add(formInner);

        // ── TABLE CARD ──
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(C_WHITE);
        tableCard.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        JLabel tblTitle = new JLabel("  Daftar Barang");
        tblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTitle.setForeground(C_TEXT);
        tblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
        tblTitle.setOpaque(true);
        tblTitle.setBackground(new Color(240, 244, 255));

        String[] cols = {"ID Barang","Nama Barang","Kategori","Satuan","Harga Jual","Stok"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));
        tableCard.add(tblTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        // ── LAYOUT ──
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(C_BG);
        center.setBorder(new EmptyBorder(10, 10, 10, 10));
        center.add(formCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Load & init
        loadData();
        setMode(false);

        // ── EVENTS ──
        btnBaru.addActionListener(e -> { clearForm(); setMode(true); tfId.setEditable(true); modeEdit = false; tfId.requestFocus(); });
        btnSimpan.addActionListener(e -> simpan());
        btnEdit.addActionListener(e -> {
            if (table.getSelectedRow() < 0) { alert("Pilih data dulu!", "Info"); return; }
            setMode(true); tfId.setEditable(false); modeEdit = true;
        });
        btnHapus.addActionListener(e -> hapus());
        btnBatal.addActionListener(e -> { clearForm(); setMode(false); });
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { isiForm(); }
        });
    }

    void loadData() {
        model.setRowCount(0);
        try (Connection con = Koneksi.getKoneksi()) {
            if (con == null) return;
            String sql = "SELECT b.*, k.nama_kategori FROM tb_barang b LEFT JOIN tb_kategori k ON b.id_kategori=k.id_kategori ORDER BY b.id_barang";
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id_barang"), rs.getString("nama_barang"),
                    rs.getString("nama_kategori"), rs.getString("satuan"),
                    "Rp " + String.format("%,.0f", rs.getDouble("harga_jual")),
                    rs.getInt("stok")
                });
            }
        } catch (SQLException ex) { alert("Error load data: " + ex.getMessage(), "Error"); }
    }

    void simpan() {
        if (tfId.getText().isEmpty() || tfNama.getText().isEmpty() || tfHarga.getText().isEmpty() || tfStok.getText().isEmpty()) {
            alert("Harap isi semua field yang wajib!", "Peringatan"); return;
        }
        try {
            double harga = Double.parseDouble(tfHarga.getText().replaceAll("[^0-9]",""));
            int stok = Integer.parseInt(tfStok.getText().trim());
            int katIdx = cbKategori.getSelectedIndex() + 1;
            Connection con = Koneksi.getKoneksi(); if (con == null) return;
            PreparedStatement ps;
            if (modeEdit) {
                ps = con.prepareStatement("UPDATE tb_barang SET id_kategori=?,nama_barang=?,satuan=?,harga_jual=?,stok=? WHERE id_barang=?");
                ps.setInt(1,katIdx); ps.setString(2,tfNama.getText()); ps.setString(3,tfSatuan.getText());
                ps.setDouble(4,harga); ps.setInt(5,stok); ps.setString(6,tfId.getText());
            } else {
                ps = con.prepareStatement("INSERT INTO tb_barang VALUES(?,?,?,?,?,?)");
                ps.setString(1,tfId.getText()); ps.setInt(2,katIdx); ps.setString(3,tfNama.getText());
                ps.setString(4,tfSatuan.getText()); ps.setDouble(5,harga); ps.setInt(6,stok);
            }
            ps.executeUpdate(); con.close();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan! ✓", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearForm(); setMode(false);
        } catch (NumberFormatException ex) { alert("Harga dan Stok harus angka!", "Error");
        } catch (SQLIntegrityConstraintViolationException ex) { alert("ID Barang sudah ada!", "Error");
        } catch (SQLException ex) { alert("Error: " + ex.getMessage(), "Error"); }
    }

    void hapus() {
        int row = table.getSelectedRow(); if (row < 0) { alert("Pilih data dulu!", "Info"); return; }
        int c = JOptionPane.showConfirmDialog(this, "Hapus barang \"" + model.getValueAt(row,1) + "\"?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection con = Koneksi.getKoneksi()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM tb_barang WHERE id_barang=?");
            ps.setString(1, (String)model.getValueAt(row,0)); ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data dihapus!", "Info", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearForm();
        } catch (SQLException ex) { alert("Gagal hapus (mungkin ada data transaksi terkait): " + ex.getMessage(), "Error"); }
    }

    void isiForm() {
        int row = table.getSelectedRow(); if (row < 0) return;
        tfId.setText((String)model.getValueAt(row,0));
        tfNama.setText((String)model.getValueAt(row,1));
        tfSatuan.setText((String)model.getValueAt(row,3));
        tfHarga.setText(model.getValueAt(row,4).toString().replace("Rp ","").replace(",",""));
        tfStok.setText(model.getValueAt(row,5).toString());
    }

    void clearForm() { tfId.setText(""); tfNama.setText(""); tfSatuan.setText("pcs"); tfHarga.setText(""); tfStok.setText(""); cbKategori.setSelectedIndex(0); table.clearSelection(); }
    void setMode(boolean aktif) { btnSimpan.setEnabled(aktif); btnBatal.setEnabled(aktif); btnEdit.setEnabled(!aktif); btnHapus.setEnabled(!aktif); tfId.setEnabled(aktif); tfNama.setEnabled(aktif); tfSatuan.setEnabled(aktif); tfHarga.setEnabled(aktif); tfStok.setEnabled(aktif); cbKategori.setEnabled(aktif); }
    void alert(String msg, String title) { JOptionPane.showMessageDialog(this, msg, title, title.equals("Error") ? JOptionPane.ERROR_MESSAGE : title.equals("Peringatan") ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE); }

    JTextField field() { JTextField f = new JTextField(); f.setFont(new Font("Segoe UI",Font.PLAIN,12)); f.setBorder(new CompoundBorder(new LineBorder(C_BORDER,1),new EmptyBorder(5,8,5,8))); return f; }
    JLabel lbl(String t) { JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI",Font.BOLD,11)); l.setForeground(C_TEXT); return l; }
    JButton btn(String t, Color c) {
        JButton b = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) g2.setColor(new Color(200, 205, 220));
                else if (getModel().isPressed()) g2.setColor(getBackground().darker());
                else if (getModel().isRollover()) g2.setColor(getBackground().brighter());
                else g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public void setEnabled(boolean b2) {
                super.setEnabled(b2);
                setForeground(b2 ? Color.WHITE : new Color(120, 130, 160));
            }
        };
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        return b;
    }
    void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI",Font.PLAIN,12)); t.setRowHeight(28);
        t.setGridColor(C_BORDER); t.setSelectionBackground(new Color(67,97,238,50)); t.setSelectionForeground(C_TEXT);
        t.getTableHeader().setBackground(new Color(240,244,255)); t.getTableHeader().setForeground(C_TEXT);
        t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,11));
        t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
    }
}