package com.mycompany.sisteminformasipenjualan;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {

    public static int    idUserLogin   = 0;
    public static String namaUserLogin = "";

    private JTextField    tfUsername;
    private JPasswordField tfPassword;
    private JButton        btnLogin, btnKeluar;

    // ── WARNA TEMA TERANG ──
    static final Color C_BG      = new Color(245, 247, 255);
    static final Color C_WHITE   = Color.WHITE;
    static final Color C_PRIMARY = new Color(67, 97, 238);
    static final Color C_ACCENT  = new Color(76, 201, 160);
    static final Color C_TEXT    = new Color(30, 30, 60);
    static final Color C_GRAY    = new Color(120, 130, 160);
    static final Color C_BORDER  = new Color(210, 215, 235);
    static final Color C_RED     = new Color(231, 76, 60);

    public LoginForm() {
        setTitle("Login - Toko Berkah Jaya");
        setSize(420, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(C_BG);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(C_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(36, 40, 36, 40)
        ));
        card.setPreferredSize(new Dimension(360, 460));

        // Logo
        JLabel logo = new JLabel("🏪", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Toko Berkah Jaya", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_PRIMARY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistem Point of Sale", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblUser = new JLabel("Username", SwingConstants.CENTER);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(C_TEXT);
        lblUser.setAlignmentX(CENTER_ALIGNMENT);

        tfUsername = new JTextField();
        tfUsername.setHorizontalAlignment(JTextField.CENTER);
        tfUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfUsername.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tfUsername.setMaximumSize(new Dimension(260, 38));
        tfUsername.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblPass = new JLabel("Password", SwingConstants.CENTER);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(C_TEXT);
        lblPass.setAlignmentX(CENTER_ALIGNMENT);

        tfPassword = new JPasswordField();
        tfPassword.setHorizontalAlignment(JTextField.CENTER);
        tfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfPassword.setBorder(new CompoundBorder(
            new LineBorder(C_BORDER, 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        tfPassword.setMaximumSize(new Dimension(260, 38));
        tfPassword.setAlignmentX(CENTER_ALIGNMENT);

        btnLogin = btn("MASUK", C_PRIMARY);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setMaximumSize(new Dimension(260, 42));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);

        btnKeluar = btn("Keluar", new Color(225, 229, 245));
        btnKeluar.setForeground(C_GRAY);
        btnKeluar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnKeluar.setMaximumSize(new Dimension(260, 36));
        btnKeluar.setAlignmentX(CENTER_ALIGNMENT);

        card.add(logo);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(18));
        card.add(sep);
        card.add(Box.createVerticalStrut(18));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(6));
        card.add(tfUsername);
        card.add(Box.createVerticalStrut(12));
        card.add(lblPass);
        card.add(Box.createVerticalStrut(6));
        card.add(tfPassword);
        card.add(Box.createVerticalStrut(18));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(8));
        card.add(btnKeluar);

        GridBagConstraints gbc = new GridBagConstraints();
        add(card, gbc);

        btnLogin.addActionListener(e -> login());
        btnKeluar.addActionListener(e -> System.exit(0));
        tfPassword.addActionListener(e -> login());
    }

    void login() {
        String user = tfUsername.getText().trim();
        String pass = new String(tfPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection con = Koneksi.getKoneksi();
        if (con == null) return;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM tb_user WHERE username=? AND password=?");
            ps.setString(1, user); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idUserLogin   = rs.getInt("id_user");
                namaUserLogin = rs.getString("nama_lengkap");
                new MenuUtama().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                tfPassword.setText("");
            }
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Tombol custom-painted agar warna selalu tampil di semua Look & Feel Windows ──
    JButton btn(String text, Color c) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(getBackground().darker());
                else if (getModel().isRollover()) g2.setColor(getBackground().brighter());
                else g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new EmptyBorder(10, 0, 10, 0));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        return b;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    } 
}