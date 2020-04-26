import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Produkt extends JFrame implements ActionListener {
    JButton zarejetruj;
    Connection con;
    JFrame f;
    JTextField tnazwa, tcena, tvat, ttyp;
    public Produkt(Connection con){
        this.con = con;
        setLayout(null);
        setResizable(false);
        setLocation(200, 200);
        setSize(250, 500);



        JLabel lnazwa = new JLabel("nazwa");
        lnazwa.setBounds(10, 0 ,60, 30);
        add(lnazwa);

         tnazwa = new JTextField();
        tnazwa.setBounds(100, 0, 120, 30);
        add(tnazwa);

        JLabel lcena = new JLabel("cena netto");
        lcena.setBounds(10, 40 ,60, 30);
        add(lcena);

        tcena = new JTextField();
        tcena.setBounds(100, 40, 120, 30);
        add(tcena);

        JLabel lvat = new JLabel("vat");
        lvat.setBounds(10, 80 ,60, 30);
        add(lvat);

        tvat = new JTextField();
        tvat.setBounds(100, 80, 120, 30);
        add(tvat);

        JLabel ltyp = new JLabel("typ (Skrot)");
        ltyp.setBounds(10, 120 ,60, 30);
        add(ltyp);

        ttyp = new JTextField();
        ttyp.setBounds(100, 120, 120, 30);
        add(ttyp);

        zarejetruj = new JButton("Dodaj");
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
                       if (tnazwa.getText().equals("") || tcena.getText().equals("") ||
                               tvat.getText().equals("") || ttyp.getText().equals("") ) {
                           komunikat = "Wszystkie pola musza byc wypelnione";

                       }else {
                           String skrotId = "";

                           ResultSet rs;
                           String sql = "select id from Typ where skrot = '" + ttyp.getText() + "'";
                           Statement st = con.createStatement();
                           rs = st.executeQuery(sql);
                           if (rs.next()) {
                               skrotId = rs.getString(1);
                               sql = "insert into Produkt values (?, ?, ?, ?)";
                               PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                               psmt.setString(1, tnazwa.getText());
                               psmt.setString(2, tcena.getText());
                               psmt.setString(3, tvat.getText());
                               psmt.setString(4, skrotId);
                               int rowAffected = psmt.executeUpdate();
                               if (rowAffected == 1) {
                                   komunikat = "Pomyslnie dodano Produkt";
                               }
                           } else {
                                komunikat = "Nie znaleziono takiego typu";
                           }
                       }

                       } catch(SQLException ex){
                           ex.printStackTrace();
                       } finally{
                           f = new JFrame();
                           f.setLocation(200, 200);
                           JLabel label = new JLabel(komunikat);
                           label.setHorizontalAlignment(SwingConstants.CENTER);
                           label.setVerticalAlignment(SwingConstants.CENTER);
                           f.add(label);
                           f.setSize(250, 200);
                           f.setVisible(true);
                           this.setVisible(false);
                       }


                   }
    }
}

