import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Rejestracja extends JFrame implements ActionListener {
    JButton zarejetruj;
    Connection con;
    JFrame f;
    String typ;
    JTextField tlogin, timie, tnazwisko, twojewodztwo, tmiasto, tulica, tnr_budynku, tnr_lokalu;
    JPasswordField thaslo;
    public Rejestracja(Connection con, String typ){
        this.con = con;
        this.typ = typ;
        setLayout(null);
        setResizable(false);
        setLocation(200, 200);
        setSize(250, 500);



        JLabel llogin = new JLabel("Login");
        llogin.setBounds(10, 0 ,60, 30);
        add(llogin);

         tlogin = new JTextField();
        tlogin.setBounds(100, 0, 120, 30);
        add(tlogin);

        JLabel lhaslo = new JLabel("Haslo");
        lhaslo.setBounds(10, 40 ,60, 30);
        add(lhaslo);

        thaslo = new JPasswordField();
        thaslo.setBounds(100, 40, 120, 30);
        add(thaslo);

        JLabel limie = new JLabel("Imie");
        limie.setBounds(10, 80 ,60, 30);
        add(limie);

        timie = new JTextField();
        timie.setBounds(100, 80, 120, 30);
        add(timie);

        JLabel lnazwisko = new JLabel("Nazwisko");
        lnazwisko.setBounds(10, 120 ,60, 30);
        add(lnazwisko);

        tnazwisko = new JTextField();
        tnazwisko.setBounds(100, 120, 120, 30);
        add(tnazwisko);

        JLabel lwojewodztwo = new JLabel("Wojewodztwo");
        lwojewodztwo.setBounds(10, 160 ,60, 30);
        add(lwojewodztwo);

        twojewodztwo = new JTextField();
        twojewodztwo.setBounds(100, 160, 120, 30);
        add(twojewodztwo);

        JLabel lmiasto = new JLabel("Miasto");
        lmiasto.setBounds(10, 200 ,60, 30);
        add(lmiasto);

        tmiasto = new JTextField();
        tmiasto.setBounds(100, 200, 120, 30);
        add(tmiasto);

        JLabel lulica = new JLabel("Ulica");
        lulica.setBounds(10, 240 ,60, 30);
        add(lulica);

        tulica = new JTextField();
        tulica.setBounds(100, 240, 120, 30);
        add(tulica);

        JLabel lnr_budynku = new JLabel("Nr budynku");
        lnr_budynku.setBounds(10, 280 ,60, 30);
        add(lnr_budynku);

        tnr_budynku = new JTextField();
        tnr_budynku.setBounds(100, 280, 120, 30);
        add(tnr_budynku);

        JLabel lnr_lokalu = new JLabel("Nr lokalu");
        lnr_lokalu.setBounds(10, 320 ,60, 30);
        add(lnr_lokalu);

        tnr_lokalu = new JTextField();
        tnr_lokalu.setBounds(100, 320, 120, 30);
        add(tnr_lokalu);

        zarejetruj = new JButton("Zarejestruj");
        zarejetruj.setBounds(75, 350, 150, 30);
        zarejetruj.addActionListener(this);
        add(zarejetruj);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        if (zrodlo == zarejetruj) {
            String komunikat = "";
                   try {
                       if (tnr_budynku.getText().equals("") || tnr_lokalu.getText().equals("") ||
                               tulica.getText().equals("") || tmiasto.getText().equals("") || twojewodztwo.getText().equals("") ||
                               timie.getText().equals("") || tnazwisko.getText().equals("") || tlogin.getText().equals("") ||
                               String.valueOf(thaslo.getPassword()).equals("")) {
                           komunikat = "Wszystkie pola musza byc wypelnione";

                       }else {
                           int adresId = -1;
                           int osobaId = -1;

                           ResultSet rs;
                           String sql = "select * from Uzytkownik where login = '" + tlogin.getText() + "'";
                           Statement st = con.createStatement();
                           rs = st.executeQuery(sql);
                           if (rs.next()) {
                               komunikat = "Taki uzytkownik juz istnieje";
                           } else {

                               sql = "insert into Adres values (?, ?, ?, ?, ?)";
                               PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                               psmt.setString(1, tnr_lokalu.getText());
                               psmt.setString(2, tnr_budynku.getText());
                               psmt.setString(3, tulica.getText());
                               psmt.setString(4, tmiasto.getText());
                               psmt.setString(5, twojewodztwo.getText());
                               int rowAffected = psmt.executeUpdate();
                               if (rowAffected == 1) {
                                   rs = psmt.getGeneratedKeys();
                                   if (rs.next()) {
                                       adresId = rs.getInt(1);
                                   }
                               }
                               sql = "insert into Osoba values (?, ?, ?)";
                               psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                               psmt.setString(1, timie.getText());
                               psmt.setString(2, tnazwisko.getText());
                               psmt.setInt(3, adresId);
                               rowAffected = psmt.executeUpdate();
                               if (rowAffected == 1) {
                                   rs = psmt.getGeneratedKeys();
                                   if (rs.next()) {
                                       osobaId = rs.getInt(1);
                                   }
                               }
                               sql = "insert into Uzytkownik values (?, ?, ?, ?)";
                               psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                               psmt.setString(1, tlogin.getText());
                               psmt.setString(2, String.valueOf(thaslo.getPassword()));
                               psmt.setString(3, typ);
                               psmt.setInt(4, osobaId);
                               psmt.executeUpdate();
                               komunikat = "Pomyslnie dodano uzytkownika";
                           }
                       }

                       } catch(SQLException ex){
                           ex.printStackTrace();
                       } finally{
                            f = Misc.generuj_komunikat(komunikat);
                       }


                   }
    }
}

