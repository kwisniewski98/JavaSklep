import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Typ extends JFrame implements ActionListener {
    JButton zarejetruj;
    Connection con;
    JFrame f;
    JTextField tskrot, topis, tprzeznaczenie;
    public Typ(Connection con){
        this.con = con;
        setLayout(null);
        setResizable(false);
        setLocation(200, 200);
        setSize(250, 500);



        JLabel lskrot = new JLabel("skrot");
        lskrot.setBounds(10, 0 ,60, 30);
        add(lskrot);

         tskrot = new JTextField();
        tskrot.setBounds(100, 0, 120, 30);
        add(tskrot);

        JLabel lopis = new JLabel("opis");
        lopis.setBounds(10, 40 ,60, 30);
        add(lopis);

        topis = new JTextField();
        topis.setBounds(100, 40, 120, 30);
        add(topis);

        JLabel lprzeznaczenie = new JLabel("przeznaczenie");
        lprzeznaczenie.setBounds(10, 80 ,60, 30);
        add(lprzeznaczenie);

        tprzeznaczenie = new JTextField();
        tprzeznaczenie.setBounds(100, 80, 120, 30);
        add(tprzeznaczenie);

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
                       if (tskrot.getText().equals("") || topis.getText().equals("") ||
                               tprzeznaczenie.getText().equals("")) {
                           komunikat = "Wszystkie pola musza byc wypelnione";

                       } else {
                           String sql = "insert into Typ values (?, ?, ?)";
                           PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                           psmt.setString(1, tskrot.getText());
                           psmt.setString(2, topis.getText());
                           psmt.setString(3, tprzeznaczenie.getText());
                           int rowAffected = psmt.executeUpdate();
                           if (rowAffected == 1) {
                               komunikat = "Pomyslnie dodano Produkt";
                           }
                       }
                   }
                         catch(SQLException ex){
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

