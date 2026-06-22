package com.mycompany.sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormCustomer extends JFrame {

    static final Color C_BG      = new Color(245, 247, 255);
    static final Color C_WHITE   = Color.WHITE;
    static final Color C_PRIMARY = new Color(114, 9, 183);
    static final Color C_GREEN   = new Color(3, 160, 98);
    static final Color C_RED     = new Color(231, 76, 60);
    static final Color C_TEXT    = new Color(30, 30, 60);
    static final Color C_GRAY    = new Color(120, 130, 160);
    static final Color C_BORDER  = new Color(210, 215, 235);

    JTextField tfId, tfNama, tfAlamat, tfTelp;
    JButton btnBaru, btnSimpan, btnEdit, btnHapus, btnBatal;
    JTable table;
    DefaultTableModel model;
    boolean modeEdit = false;

    public FormCustomer() {
        setTitle("Data Customer - Toko Berkah Jaya");
        setSize(860, 530);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel h = new JLabel("👥  Data Customer");
        h.setFont(new Font("Segoe UI", Font.BOLD, 16));
        h.setForeground(Color.WHITE);
        header.add(h); add(header, BorderLayout.NORTH);

        // FORM CARD
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(C_WHITE);
        formCard.setBorder(new CompoundBorder(new LineBorder(C_BORDER,1), new EmptyBorder(16,16,12,16)));

        JLabel ft = new JLabel("Form Input Customer");
        ft.setFont(new Font("Segoe UI",Font.BOLD,13)); ft.setForeground(C_PRIMARY);
        ft.setBorder(new EmptyBorder(0,0,10,0));

        JPanel fields = new JPanel(new GridLayout(2, 4, 10, 8));
        fields.setBackground(C_WHITE);
        tfId = field(); tfNama = field(); tfAlamat = field(); tfTelp = field();

        fields.add(lbl("ID Customer *")); fields.add(tfId);
        fields.add(lbl("Nama Customer *")); fields.add(tfNama);
        fields.add(lbl("Alamat")); fields.add(tfAlamat);
        fields.add(lbl("No. Telepon")); fields.add(tfTelp);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(C_WHITE);
        btnRow.setBorder(new EmptyBorder(12,0,0,0));
        btnBaru   = btn("🆕 Baru",   new Color(100,116,139));
        btnSimpan = btn("💾 Simpan", C_GREEN);
        btnEdit   = btn("✏ Edit",   C_PRIMARY);
        btnHapus  = btn("🗑 Hapus",  C_RED);
        btnBatal  = btn("✖ Batal",  new Color(150,150,150));
        btnRow.add(btnBaru); btnRow.add(btnSimpan); btnRow.add(btnEdit); btnRow.add(btnHapus); btnRow.add(btnBatal);

        JPanel fi = new JPanel(new BorderLayout()); fi.setBackground(C_WHITE);
        fi.add(ft,BorderLayout.NORTH); fi.add(fields,BorderLayout.CENTER); fi.add(btnRow,BorderLayout.SOUTH);
        formCard.add(fi);

        // TABLE
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(C_WHITE);
        tableCard.setBorder(new LineBorder(C_BORDER,1));

        JLabel tblTitle = new JLabel("  Daftar Customer");
        tblTitle.setFont(new Font("Segoe UI",Font.BOLD,13)); tblTitle.setForeground(C_TEXT);
        tblTitle.setBorder(new EmptyBorder(10,10,10,10)); tblTitle.setOpaque(true);
        tblTitle.setBackground(new Color(245,240,255));

        String[] cols = {"ID Customer","Nama Customer","Alamat","No. Telepon"};
        model = new DefaultTableModel(cols,0) { public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model); styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));
        tableCard.add(tblTitle,BorderLayout.NORTH); tableCard.add(scroll,BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0,10));
        center.setBackground(C_BG); center.setBorder(new EmptyBorder(10,10,10,10));
        center.add(formCard,BorderLayout.NORTH); center.add(tableCard,BorderLayout.CENTER);
        add(center,BorderLayout.CENTER);

        loadData(); setMode(false);

        btnBaru.addActionListener(e -> { clearForm(); setMode(true); tfId.setEditable(true); modeEdit=false; tfId.requestFocus(); });
        btnSimpan.addActionListener(e -> simpan());
        btnEdit.addActionListener(e -> { if(table.getSelectedRow()<0){alert("Pilih data dulu!","Info");return;} setMode(true); tfId.setEditable(false); modeEdit=true; });
        btnHapus.addActionListener(e -> hapus());
        btnBatal.addActionListener(e -> { clearForm(); setMode(false); });
        table.addMouseListener(new MouseAdapter(){ public void mouseClicked(MouseEvent e){isiForm();} });
    }

    void loadData() {
        model.setRowCount(0);
        try (Connection con=Koneksi.getKoneksi()) {
            if(con==null) return;
            ResultSet rs=con.createStatement().executeQuery("SELECT * FROM tb_customer ORDER BY id_customer");
            while(rs.next()) model.addRow(new Object[]{rs.getString("id_customer"),rs.getString("nama_customer"),rs.getString("alamat"),rs.getString("telepon")});
        } catch(SQLException ex){alert("Error: "+ex.getMessage(),"Error");}
    }

    void simpan() {
        if(tfId.getText().isEmpty()||tfNama.getText().isEmpty()){alert("ID dan Nama wajib diisi!","Peringatan");return;}
        try (Connection con=Koneksi.getKoneksi()) {
            if(con==null) return;
            PreparedStatement ps;
            if(modeEdit){ ps=con.prepareStatement("UPDATE tb_customer SET nama_customer=?,alamat=?,telepon=? WHERE id_customer=?"); ps.setString(1,tfNama.getText()); ps.setString(2,tfAlamat.getText()); ps.setString(3,tfTelp.getText()); ps.setString(4,tfId.getText()); }
            else { ps=con.prepareStatement("INSERT INTO tb_customer VALUES(?,?,?,?)"); ps.setString(1,tfId.getText()); ps.setString(2,tfNama.getText()); ps.setString(3,tfAlamat.getText()); ps.setString(4,tfTelp.getText()); }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Data berhasil disimpan! ✓","Sukses",JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearForm(); setMode(false);
        } catch(SQLIntegrityConstraintViolationException ex){alert("ID Customer sudah ada!","Error");
        } catch(SQLException ex){alert("Error: "+ex.getMessage(),"Error");}
    }

    void hapus() {
        int row=table.getSelectedRow(); if(row<0){alert("Pilih data dulu!","Info");return;}
        int c=JOptionPane.showConfirmDialog(this,"Hapus customer \""+model.getValueAt(row,1)+"\"?","Konfirmasi",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(c!=JOptionPane.YES_OPTION) return;
        try(Connection con=Koneksi.getKoneksi()){ PreparedStatement ps=con.prepareStatement("DELETE FROM tb_customer WHERE id_customer=?"); ps.setString(1,(String)model.getValueAt(row,0)); ps.executeUpdate(); JOptionPane.showMessageDialog(this,"Data dihapus!"); loadData(); clearForm(); } catch(SQLException ex){alert("Gagal hapus: "+ex.getMessage(),"Error");}
    }

    void isiForm() { int row=table.getSelectedRow(); if(row<0)return; tfId.setText((String)model.getValueAt(row,0)); tfNama.setText((String)model.getValueAt(row,1)); tfAlamat.setText(model.getValueAt(row,2)!=null?model.getValueAt(row,2).toString():""); tfTelp.setText(model.getValueAt(row,3)!=null?model.getValueAt(row,3).toString():""); }
    void clearForm() { tfId.setText(""); tfNama.setText(""); tfAlamat.setText(""); tfTelp.setText(""); table.clearSelection(); }
    void setMode(boolean a) { btnSimpan.setEnabled(a); btnBatal.setEnabled(a); btnEdit.setEnabled(!a); btnHapus.setEnabled(!a); tfId.setEnabled(a); tfNama.setEnabled(a); tfAlamat.setEnabled(a); tfTelp.setEnabled(a); }
    void alert(String m,String t){JOptionPane.showMessageDialog(this,m,t,t.equals("Error")?JOptionPane.ERROR_MESSAGE:t.equals("Peringatan")?JOptionPane.WARNING_MESSAGE:JOptionPane.INFORMATION_MESSAGE);}
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
        b.setBorder(new EmptyBorder(7,14,7,14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        return b;
    }

    void styleTable(JTable t){t.setFont(new Font("Segoe UI",Font.PLAIN,12));t.setRowHeight(28);t.setGridColor(C_BORDER);t.setSelectionBackground(new Color(114,9,183,50));t.setSelectionForeground(C_TEXT);t.getTableHeader().setBackground(new Color(245,240,255));t.getTableHeader().setForeground(C_TEXT);t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,11));t.setShowHorizontalLines(true);t.setShowVerticalLines(false);}
}