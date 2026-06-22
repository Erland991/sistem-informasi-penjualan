package com.mycompany.sisteminformasipenjualan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FormLaporan extends JFrame {

    static final Color C_BG      = new Color(245, 247, 255);
    static final Color C_WHITE   = Color.WHITE;
    static final Color C_PRIMARY = new Color(230, 126, 34);
    static final Color C_GREEN   = new Color(3, 160, 98);
    static final Color C_TEXT    = new Color(30, 30, 60);
    static final Color C_GRAY    = new Color(120, 130, 160);
    static final Color C_BORDER  = new Color(210, 215, 235);

    JTable table;
    DefaultTableModel model;
    JLabel lblTotal, lblJmlTx, lblRata;

    public FormLaporan() {
        setTitle("Laporan Penjualan - Toko Berkah Jaya");
        setSize(960, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setBorder(new EmptyBorder(12,20,12,20));
        JLabel h = new JLabel("📊  Laporan Penjualan");
        h.setFont(new Font("Segoe UI",Font.BOLD,16)); h.setForeground(Color.WHITE);
        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(Color.WHITE); btnRefresh.setForeground(C_PRIMARY);
        btnRefresh.setFont(new Font("Segoe UI",Font.BOLD,11)); btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(new EmptyBorder(6,12,6,12)); btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        header.add(h,BorderLayout.WEST); header.add(btnRefresh,BorderLayout.EAST);

        // STAT CARDS
        JPanel statRow = new JPanel(new GridLayout(1,3,14,0));
        statRow.setBackground(C_BG);
        statRow.setBorder(new EmptyBorder(14,14,8,14));

        // Array holder agar label "value" bisa diambil balik dengan aman (bukan lewat getComponent index)
        JLabel[] holder1 = new JLabel[1];
        JLabel[] holder2 = new JLabel[1];
        JLabel[] holder3 = new JLabel[1];

        JPanel c1 = statCard("Total Transaksi","0","transaksi tercatat", new Color(67,97,238), holder1);
        JPanel c2 = statCard("Total Pendapatan","Rp 0","semua waktu", C_GREEN, holder2);
        JPanel c3 = statCard("Rata-rata / Transaksi","Rp 0","per transaksi", C_PRIMARY, holder3);
        statRow.add(c1); statRow.add(c2); statRow.add(c3);

        lblJmlTx = holder1[0];
        lblTotal = holder2[0];
        lblRata  = holder3[0];

        // Rebuild north (header + statRow)
        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(C_BG);
        north.add(header, BorderLayout.NORTH);
        north.add(statRow, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);

        // TABLE
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(C_WHITE);
        tableCard.setBorder(new CompoundBorder(new LineBorder(C_BORDER,1),new EmptyBorder(0,0,0,0)));

        JLabel tblTitle = new JLabel("  Riwayat Transaksi Lengkap");
        tblTitle.setFont(new Font("Segoe UI",Font.BOLD,13)); tblTitle.setForeground(C_TEXT);
        tblTitle.setBorder(new EmptyBorder(10,10,10,10)); tblTitle.setOpaque(true);
        tblTitle.setBackground(new Color(255,248,240));

        String[] cols = {"No","Tanggal","ID Jual","Customer","Barang","Qty","Harga Satuan","Total Bayar"};
        model = new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        table = new JTable(model); styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));

        // Grand total bar
        JPanel grandBar = new JPanel(new FlowLayout(FlowLayout.RIGHT,16,10));
        grandBar.setBackground(new Color(255,248,240));
        grandBar.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));
        JLabel gtLbl = new JLabel("GRAND TOTAL:");
        gtLbl.setFont(new Font("Segoe UI",Font.BOLD,13)); gtLbl.setForeground(C_GRAY);
        JLabel gtVal = new JLabel("Rp 0");
        gtVal.setFont(new Font("Segoe UI",Font.BOLD,18)); gtVal.setForeground(C_GREEN);
        grandBar.add(gtLbl); grandBar.add(gtVal);

        tableCard.add(tblTitle,BorderLayout.NORTH);
        tableCard.add(scroll,BorderLayout.CENTER);
        tableCard.add(grandBar,BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(C_BG); center.setBorder(new EmptyBorder(0,14,14,14));
        center.add(tableCard);
        add(center,BorderLayout.CENTER);

        loadData(gtVal);
        btnRefresh.addActionListener(e -> loadData(gtVal));
    }

    void loadData(JLabel gtVal) {
        model.setRowCount(0);
        long grandTotal=0; int no=1;
        try(Connection con=Koneksi.getKoneksi()){
            if(con==null) return;
            String sql="SELECT p.*,c.nama_customer,b.nama_barang,b.harga_jual FROM tb_penjualan p LEFT JOIN tb_customer c ON p.id_customer=c.id_customer LEFT JOIN tb_barang b ON p.id_barang=b.id_barang ORDER BY p.id_jual";
            ResultSet rs=con.createStatement().executeQuery(sql);
            while(rs.next()){
                long total=(long)rs.getDouble("total_bayar");
                grandTotal+=total;
                model.addRow(new Object[]{no++,rs.getString("tgl_transaksi"),"TRX-"+String.format("%04d",rs.getInt("id_jual")),rs.getString("nama_customer"),rs.getString("nama_barang"),rs.getInt("jumlah_beli")+"x","Rp "+String.format("%,.0f",rs.getDouble("harga_jual")),"Rp "+String.format("%,.0f",(double)total)});
            }
        } catch(SQLException ex){JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage());}
        int jml=model.getRowCount();
        gtVal.setText("Rp "+String.format("%,.0f",(double)grandTotal));
        lblJmlTx.setText(String.valueOf(jml));
        lblTotal.setText("Rp "+String.format("%,.0f",(double)grandTotal));
        lblRata.setText(jml>0?"Rp "+String.format("%,.0f",(double)grandTotal/jml):"Rp 0");
    }

    // Tambahan parameter "valueHolder" agar label nilai bisa di-update tanpa getComponent(index) yang rawan salah urutan
    JPanel statCard(String label, String val, String sub, Color color, JLabel[] valueHolder){
        JPanel p=new JPanel(new BorderLayout(8,0));
        p.setBackground(C_WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(C_BORDER,1),new EmptyBorder(14,16,14,16)));
        JPanel bar=new JPanel(); bar.setBackground(color); bar.setPreferredSize(new Dimension(5,0));
        JPanel text=new JPanel(); text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS)); text.setBackground(C_WHITE);
        JLabel lLbl=new JLabel(label.toUpperCase()); lLbl.setFont(new Font("Segoe UI",Font.BOLD,10)); lLbl.setForeground(C_GRAY);
        JLabel lVal=new JLabel(val); lVal.setFont(new Font("Segoe UI",Font.BOLD,20)); lVal.setForeground(C_TEXT);
        JLabel lSub=new JLabel(sub); lSub.setFont(new Font("Segoe UI",Font.PLAIN,11)); lSub.setForeground(color);
        text.add(lLbl); text.add(Box.createVerticalStrut(4)); text.add(lVal); text.add(Box.createVerticalStrut(2)); text.add(lSub);
        p.add(bar,BorderLayout.WEST); p.add(text,BorderLayout.CENTER);
        valueHolder[0] = lVal;   // simpan referensi label nilai secara langsung & aman
        return p;
    }

    void styleTable(JTable t){t.setFont(new Font("Segoe UI",Font.PLAIN,12));t.setRowHeight(28);t.setGridColor(C_BORDER);t.setSelectionBackground(new Color(230,126,34,50));t.setSelectionForeground(C_TEXT);t.getTableHeader().setBackground(new Color(255,248,240));t.getTableHeader().setForeground(C_TEXT);t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,11));t.setShowHorizontalLines(true);t.setShowVerticalLines(false);}
}