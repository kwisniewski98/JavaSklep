import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Produkt extends JFrame implements ActionListener {
    JButton zarejetruj, btyp, bTypPotwierdz;
    Connection con;
    JFrame f, f2;
    int typ = -1;
    JTable ttyp;
    JTextField tnazwa, tcena, tvat;

    public Produkt(Connection con) {
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
        lvat.setBounds(10, 80, 60, 30);
        add(lvat);

        tvat = new JTextField();
        tvat.setBounds(100, 80, 120, 30);
        add(tvat);


        btyp = new JButton("Typ");
        btyp.setBounds(10, 120, 120, 30);
        btyp.addActionListener(this);
        add(btyp);

        zarejetruj = new JButton("Dodaj");
        zarejetruj.setBounds(75, 350, 150, 30);
        zarejetruj.addActionListener(this);
        add(zarejetruj);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        if (zrodlo == bTypPotwierdz) {
            this.typ = ttyp.getSelectedRow();
            f2.dispatchEvent(new WindowEvent(f2, WindowEvent.WINDOW_CLOSING));
        }
        if (zrodlo == btyp) {
            String sql = "Select * from Typ";

            try {
                JPanel panel = new JPanel();
                f2 = new JFrame();
                ttyp = Misc.stworz_liste(sql, con);
                JScrollPane sp;
                sp = new JScrollPane(ttyp);
                panel.removeAll();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(sp);

                JPanel bot_panel = new JPanel();
                bTypPotwierdz = new JButton("Potwierdz");
                bTypPotwierdz.addActionListener(this);

                bot_panel.add(bTypPotwierdz);

                panel.add(bot_panel);
                f2.setContentPane(panel);

                f2.setLocation(200, 50);
                f2.setSize(500, 400);
                f2.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (zrodlo == zarejetruj) {
            String komunikat = "";
            try {
                if (tnazwa.getText().equals("") || tcena.getText().equals("") ||
                        tvat.getText().equals("") || typ == -1) {
                    komunikat = "Wszystkie pola musza byc wypelnione";

                } else {

                    ResultSet rs;
                    System.out.println(typ);
                    String sql = "insert into Produkt values (?, ?, ?, ?)";
                    PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    psmt.setString(1, tnazwa.getText());
                    psmt.setString(2, tcena.getText());
                    psmt.setString(3, tvat.getText());
                    psmt.setInt(4, typ + 1);
                    int rowAffected = psmt.executeUpdate();
                    if (rowAffected == 1) {
                        rs = psmt.getGeneratedKeys();

                        int produktId = -1;
                        if (rs.next()) {
                            produktId = rs.getInt(1);
                        }
                        sql = "SELECT id from Oddzial";
                        psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        rs = psmt.executeQuery();
                        ArrayList<String> oddzialy_id = new ArrayList<>();
                        while (rs.next()) {
                            oddzialy_id.add(rs.getString(1));
                        }
                        String id;
                        for (Iterator<String> it = oddzialy_id.iterator(); it.hasNext(); ) {
                            id = it.next();
                                          sql = "insert into Stan values (?, 0, ?, null )";
                                          psmt = con.prepareStatement(sql);
                                          psmt.setString(1, id);
                                          psmt.setInt(2, produktId);
                                          psmt.executeUpdate();
                                   }

                                   komunikat = "Pomyslnie dodano Produkt";


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

