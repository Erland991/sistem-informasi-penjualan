/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sisteminformasipenjualan;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MenuUtama extends JFrame {

    static final Color C_BG      = new Color(245, 247, 255);
    static final Color C_WHITE   = Color.WHITE;
    static final Color C_PRIMARY = new Color(67, 97, 238);
    static final Color C_TEXT    = new Color(30, 30, 60);
    static final Color C_GRAY    = new Color(120, 130, 160);
    static final Color C_BORDER  = new Color(210, 215, 235);

    public MenuUtama() {
        setTitle("Menu Utama - Toko Berkah Jaya");
        setSize(720, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout());

        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_PRIMARY);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTitle = new JLabel("🏪  TOKO BERKAH JAYA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        userInfo.setBackground(C_PRIMARY);
        JLabel lblUser = new JLabel("👤 " + LoginForm.namaUserLogin + "  ");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(new Color(200, 210, 255));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnLogout.setBorder(new EmptyBorder(5, 12, 5, 12));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setOpaque(true);
        userInfo.add(lblUser); userInfo.add(btnLogout);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── SUBTITLE ──
        JLabel lblSub = new JLabel("Pilih menu di bawah ini untuk memulai", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(C_GRAY);
        lblSub.setBorder(new EmptyBorder(18, 0, 4, 0));
        add(lblSub, BorderLayout.CENTER);

        // ── GRID MENU ──
        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setBackground(C_BG);
        grid.setBorder(new EmptyBorder(10, 30, 30, 30));

        grid.add(menuCard("📦", "Data Barang",   "Kelola produk & stok",  new Color(67,97,238),   () -> new FormBarang().setVisible(true)));
        grid.add(menuCard("👥", "Data Customer", "Kelola pelanggan",       new Color(114,9,183),  () -> new FormCustomer().setVisible(true)));
        grid.add(menuCard("🧾", "Transaksi",     "Proses penjualan",       new Color(3,160,98),   () -> new FormTransaksi().setVisible(true)));
        grid.add(menuCard("📊", "Laporan",       "Laporan penjualan",      new Color(230,126,34), () -> new FormLaporan().setVisible(true)));
        grid.add(menuCard("⚙️", "Pengaturan",   "Info sistem",            new Color(100,116,139), () -> JOptionPane.showMessageDialog(this,
                "Sistem POS Toko Berkah Jaya\nVersi 1.0\n\nUser: " + LoginForm.namaUserLogin, "Info", JOptionPane.INFORMATION_MESSAGE)));
        grid.add(menuCard("🚪", "Keluar",        "Logout dari sistem",     new Color(231,76,60),  () -> {
            int c = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { new LoginForm().setVisible(true); dispose(); }
        }));

        // Wrap grid below subtitle
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(C_BG);
        center.add(lblSub, BorderLayout.NORTH);
        center.add(grid, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ── FOOTER ──
        JLabel footer = new JLabel("© 2026 Toko Berkah Jaya  |  Sistem POS", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(C_GRAY);
        footer.setBorder(new EmptyBorder(0, 0, 12, 0));
        add(footer, BorderLayout.SOUTH);

        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { new LoginForm().setVisible(true); dispose(); }
        });
    }

    JPanel menuCard(String icon, String title, String sub, Color color, Runnable action) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(C_WHITE);
        p.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(18, 16, 18, 16)
        ));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Top color bar
        JPanel bar = new JPanel();
        bar.setBackground(color);
        bar.setPreferredSize(new Dimension(0, 5));

        JLabel ico = new JLabel(icon, SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(C_TEXT);

        JLabel s = new JLabel(sub, SwingConstants.CENTER);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        s.setForeground(C_GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(C_WHITE);
        ico.setAlignmentX(CENTER_ALIGNMENT);
        t.setAlignmentX(CENTER_ALIGNMENT);
        s.setAlignmentX(CENTER_ALIGNMENT);
        textPanel.add(ico);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(t);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(s);

        p.add(bar, BorderLayout.NORTH);
        p.add(textPanel, BorderLayout.CENTER);

        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { action.run(); }
            public void mouseEntered(MouseEvent e) {
                p.setBackground(new Color(240, 244, 255));
                textPanel.setBackground(new Color(240, 244, 255));
            }
            public void mouseExited(MouseEvent e) {
                p.setBackground(C_WHITE);
                textPanel.setBackground(C_WHITE);
            }
        });
        return p;
    }
}

