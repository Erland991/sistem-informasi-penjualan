package com.mycompany.sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormTransaksi extends JFrame {

    static final Color C_BG      = new Color(245, 247, 255);
    static final Color C_WHITE   = Color.WHITE;
    static final Color C_PRIMARY = new Color(3, 160, 98);
    static final Color C_BLUE    = new Color(67, 97, 238);
    static final Color C_RED     = new Color(231, 76, 60);
    static final Color C_TEXT    = new Color(30, 30, 60);
    static final Color C_GRAY    = new Color(120, 130, 160);
    static final Color C_BORDER  = new Color(210, 215, 235);

    JComboBox<String> cbCustomer, cbBarang;
    JTextField tfHarga, tfJumlah, tfTotal, tfTanggal;
    JButton btnSimpan, btnBaru;
    JTable table;
    DefaultTableModel model;

    // Simpan id asli dari combo
    java.util.List<String> listIdCustomer = new java.util.ArrayList<>();
    java.util.List<String> listIdBarang   = new java.util.ArrayList<>();
    java.util.List<Double> listHargaBarang = new java.util.ArrayList<>();

    public FormTransaksi() {
        setTitle("Transaksi Penjualan - Toko Berkah Jaya");
        setSize(960, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setBorder(new EmptyBorder(12,20,12,20));
        JLabel h = new JLabel("🧾  Transaksi Penjualan");
        h.setFont(new Font("Segoe UI",Font.BOLD,16)); h.setForeground(Color.WHITE);
        header.add(h); add(header, BorderLayout.NORTH);

        // FORM CARD
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(C_WHITE);
        formCard.setBorder(new CompoundBorder(new LineBorder(C_BORDER,1),new EmptyBorder(16,16,12,16)));

        JLabel ft = new JLabel("Form Transaksi Baru");
        ft.setFont(new Font("Segoe UI",Font.BOLD,13)); ft.setForeground(C_PRIMARY);
        ft.setBorder(new EmptyBorder(0,0,10,0));

        JPanel fields = new JPanel(new GridLayout(3, 4, 10, 8));
        fields.setBackground(C_WHITE);

        tfTanggal = field(); tfTanggal.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); tfTanggal.setEditable(false); tfTanggal.setBackground(new Color(240,244,255));
        cbCustomer = new JComboBox<>(); cbCustomer.setFont(new Font("Segoe UI",Font.PLAIN,12));
        cbBarang   = new JComboBox<>(); cbBarang.setFont(new Font("Segoe UI",Font.PLAIN,12));
        tfHarga    = field(); tfHarga.setEditable(false); tfHarga.setBackground(new Color(240,244,255));
        tfJumlah   = field(); tfJumlah.setText("1");
        tfTotal    = field(); tfTotal.setEditable(false); tfTotal.setBackground(new Color(230,255,245));
        tfTotal.setForeground(new Color(3,130,80)); tfTotal.setFont(new Font("Segoe UI",Font.BOLD,13));

        fields.add(lbl("Tanggal")); fields.add(tfTanggal);
        fields.add(lbl("Customer *")); fields.add(cbCustomer);
        fields.add(lbl("Barang *")); fields.add(cbBarang);
        fields.add(lbl("Harga Satuan")); fields.add(tfHarga);
        fields.add(lbl("Jumlah Beli *")); fields.add(tfJumlah);
        fields.add(lbl("Total Bayar")); fields.add(tfTotal);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
        btnRow.setBackground(C_WHITE); btnRow.setBorder(new EmptyBorder(12,0,0,0));
        btnBaru   = btn("🆕 Baru",   C_BLUE);
        btnSimpan = btn("💾 Simpan Transaksi", C_PRIMARY);
        btnRow.add(btnBaru); btnRow.add(btnSimpan);

        JPanel fi = new JPanel(new BorderLayout()); fi.setBackground(C_WHITE);
        fi.add(ft,BorderLayout.NORTH); fi.add(fields,BorderLayout.CENTER); fi.add(btnRow,BorderLayout.SOUTH);
        formCard.add(fi);

        // TABLE
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(C_WHITE);
        tableCard.setBorder(new LineBorder(C_BORDER,1));

        JLabel tblTitle = new JLabel("  Riwayat Transaksi");
        tblTitle.setFont(new Font("Segoe UI",Font.BOLD,13)); tblTitle.setForeground(C_TEXT);
        tblTitle.setBorder(new EmptyBorder(10,10,10,10)); tblTitle.setOpaque(true);
        tblTitle.setBackground(new Color(240,255,248));

        String[] cols = {"ID Jual","Tanggal","Customer","Barang","Qty","Total Bayar"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model); styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));
        tableCard.add(tblTitle,BorderLayout.NORTH); tableCard.add(scroll,BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0,10));
        center.setBackground(C_BG); center.setBorder(new EmptyBorder(10,10,10,10));
        center.add(formCard,BorderLayout.NORTH); center.add(tableCard,BorderLayout.CENTER);
        add(center,BorderLayout.CENTER);

        loadComboCustomer(); loadComboBarang(); loadRiwayat();

        // EVENT: Saat barang dipilih → harga otomatis muncul (ActionListener JComboBox)
        cbBarang.addActionListener(e -> {
            int idx = cbBarang.getSelectedIndex() - 1;
            if (idx >= 0 && idx < listHargaBarang.size()) {
                double harga = listHargaBarang.get(idx);
                tfHarga.setText(String.format("%.0f", harga));
                hitungTotal();
            } else {
                tfHarga.setText(""); tfTotal.setText("");
            }
        });

        tfJumlah.addKeyListener(new KeyAdapter(){
            public void keyReleased(KeyEvent e){ hitungTotal(); }
        });

        btnBaru.addActionListener(e -> clearForm());
        btnSimpan.addActionListener(e -> simpan());
    }

    void loadComboCustomer() {
        cbCustomer.removeAllItems(); listIdCustomer.clear();
        cbCustomer.addItem("-- Pilih Customer --");
        try (Connection con=Koneksi.getKoneksi()) {
            if(con==null) return;
            ResultSet rs=con.createStatement().executeQuery("SELECT * FROM tb_customer ORDER BY id_customer");
            while(rs.next()){ listIdCustomer.add(rs.getString("id_customer")); cbCustomer.addItem(rs.getString("id_customer")+" - "+rs.getString("nama_customer")); }
        } catch(SQLException ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
    }

    void loadComboBarang() {
        cbBarang.removeAllItems(); listIdBarang.clear(); listHargaBarang.clear();
        cbBarang.addItem("-- Pilih Barang --");
        try (Connection con=Koneksi.getKoneksi()) {
            if(con==null) return;
            ResultSet rs=con.createStatement().executeQuery("SELECT * FROM tb_barang ORDER BY id_barang");
            while(rs.next()){
                listIdBarang.add(rs.getString("id_barang"));
                listHargaBarang.add(rs.getDouble("harga_jual"));
                cbBarang.addItem(rs.getString("id_barang")+" - "+rs.getString("nama_barang")+" (Stok: "+rs.getInt("stok")+")");
            }
        } catch(SQLException ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
    }

    void loadRiwayat() {
        model.setRowCount(0);
        try (Connection con=Koneksi.getKoneksi()) {
            if(con==null) return;
            String sql="SELECT p.id_jual,p.tgl_transaksi,c.nama_customer,b.nama_barang,p.jumlah_beli,p.total_bayar FROM tb_penjualan p LEFT JOIN tb_customer c ON p.id_customer=c.id_customer LEFT JOIN tb_barang b ON p.id_barang=b.id_barang ORDER BY p.id_jual DESC";
            ResultSet rs=con.createStatement().executeQuery(sql);
            while(rs.next()) model.addRow(new Object[]{"TRX-"+String.format("%04d",rs.getInt("id_jual")),rs.getString("tgl_transaksi"),rs.getString("nama_customer"),rs.getString("nama_barang"),rs.getInt("jumlah_beli")+"x","Rp "+String.format("%,.0f",rs.getDouble("total_bayar"))});
        } catch(SQLException ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
    }

    void hitungTotal() {
        try {
            double harga = Double.parseDouble(tfHarga.getText().trim());
            int jumlah   = Integer.parseInt(tfJumlah.getText().trim());
            tfTotal.setText("Rp " + String.format("%,.0f", harga * jumlah));
        } catch(Exception ex){ tfTotal.setText(""); }
    }

    void simpan() {
        if(cbCustomer.getSelectedIndex()==0){JOptionPane.showMessageDialog(this,"Pilih customer dulu!","Peringatan",JOptionPane.WARNING_MESSAGE);return;}
        if(cbBarang.getSelectedIndex()==0){JOptionPane.showMessageDialog(this,"Pilih barang dulu!","Peringatan",JOptionPane.WARNING_MESSAGE);return;}
        int jumlah;
        try{ jumlah=Integer.parseInt(tfJumlah.getText().trim()); if(jumlah<=0) throw new Exception(); }
        catch(Exception ex){JOptionPane.showMessageDialog(this,"Jumlah beli harus angka lebih dari 0!","Error",JOptionPane.ERROR_MESSAGE);return;}

        String idCustomer = listIdCustomer.get(cbCustomer.getSelectedIndex()-1);
        String idBarang   = listIdBarang.get(cbBarang.getSelectedIndex()-1);

        try (Connection con=Koneksi.getKoneksi()) {
            if(con==null) return;
            // Cek stok
            PreparedStatement psStok=con.prepareStatement("SELECT stok,harga_jual,satuan FROM tb_barang WHERE id_barang=?");
            psStok.setString(1,idBarang);
            ResultSet rs=psStok.executeQuery();
            if(!rs.next()){JOptionPane.showMessageDialog(this,"Barang tidak ditemukan!","Error",JOptionPane.ERROR_MESSAGE);return;}
            int stokAda=rs.getInt("stok");
            double harga=rs.getDouble("harga_jual");
            String satuan=rs.getString("satuan");

            // VALIDASI BISNIS: cek stok mencukupi
            if(stokAda < jumlah){
                JOptionPane.showMessageDialog(this,
                    "⚠ Stok tidak mencukupi!\nStok tersedia: "+stokAda+" "+satuan+"\nJumlah diminta: "+jumlah+" "+satuan,
                    "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = harga * jumlah;
            String tgl   = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // INSERT INTO tb_penjualan
            PreparedStatement psInsert=con.prepareStatement("INSERT INTO tb_penjualan(tgl_transaksi,id_customer,id_barang,jumlah_beli,total_bayar,id_user) VALUES(?,?,?,?,?,?)");
            psInsert.setString(1,tgl); psInsert.setString(2,idCustomer); psInsert.setString(3,idBarang);
            psInsert.setInt(4,jumlah); psInsert.setDouble(5,total); psInsert.setInt(6,LoginForm.idUserLogin);
            psInsert.executeUpdate();

            // UPDATE tb_barang SET stok = stok - jumlah WHERE id_barang = ?
            PreparedStatement psUpdate=con.prepareStatement("UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?");
            psUpdate.setInt(1,jumlah); psUpdate.setString(2,idBarang);
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "✅ Transaksi berhasil!\n\nCustomer : "+cbCustomer.getSelectedItem().toString().split(" - ")[1]+"\nBarang   : "+cbBarang.getSelectedItem().toString().split(" - ")[1]+"\nJumlah   : "+jumlah+" "+satuan+"\nTotal    : Rp "+String.format("%,.0f",total),
                "Transaksi Sukses", JOptionPane.INFORMATION_MESSAGE);

            clearForm(); loadComboBarang(); loadRiwayat();
        } catch(SQLException ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }

    void clearForm(){ cbCustomer.setSelectedIndex(0); cbBarang.setSelectedIndex(0); tfHarga.setText(""); tfJumlah.setText("1"); tfTotal.setText(""); }

    JTextField field(){JTextField f=new JTextField();f.setFont(new Font("Segoe UI",Font.PLAIN,12));f.setBorder(new CompoundBorder(new LineBorder(C_BORDER,1),new EmptyBorder(5,8,5,8)));return f;}
    JLabel lbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,11));l.setForeground(C_TEXT);return l;}

    // ── Tombol custom-painted agar warna selalu tampil di semua Look & Feel Windows ──
    JButton btn(String t,Color c){
        JButton b = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) g2.setColor(new Color(210, 213, 225));
                else if (getModel().isPressed()) g2.setColor(getBackground().darker());
                else if (getModel().isRollover()) g2.setColor(getBackground().brighter());
                else g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            public void setEnabled(boolean e) {
                super.setEnabled(e);
                setForeground(e ? Color.WHITE : new Color(130, 138, 160));
            }
        };
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8,16,8,16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        return b;
    }

    void styleTable(JTable t){t.setFont(new Font("Segoe UI",Font.PLAIN,12));t.setRowHeight(28);t.setGridColor(C_BORDER);t.setSelectionBackground(new Color(3,160,98,50));t.setSelectionForeground(C_TEXT);t.getTableHeader().setBackground(new Color(240,255,248));t.getTableHeader().setForeground(C_TEXT);t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,11));t.setShowHorizontalLines(true);t.setShowVerticalLines(false);}
}
