import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Oddzial extends JFrame implements ActionListener {
    JButton zarejetruj, manager;
    Connection con;
    JFrame f;
    JTextField tnazwa, ttyp, tnazwisko, twojewodztwo, tmiasto, tulica, tnr_budynku, tnr_lokalu;
    JPasswordField thaslo;
    JTable tlista;

    public Oddzial(Connection con) {
        this.con = con;

        setLayout(null);
        setResizable(false);
        setLocation(200, 200);
        setSize(250, 500);


        JLabel lnazwa = new JLabel("nazwa");
        lnazwa.setBounds(10, 0, 60, 30);
        add(lnazwa);

        tnazwa = new JTextField();
        tnazwa.setBounds(100, 0, 120, 30);
        add(tnazwa);

        JLabel ltyp = new JLabel("typ");
        ltyp.setBounds(10, 40, 60, 30);
        add(ltyp);

        ttyp = new JPasswordField();
        ttyp.setBounds(100, 40, 120, 30);
        add(ttyp);


        JLabel lwojewodztwo = new JLabel("Wojewodztwo");
        lwojewodztwo.setBounds(10, 80, 60, 30);
        add(lwojewodztwo);

        twojewodztwo = new JTextField();
        twojewodztwo.setBounds(100, 80, 120, 30);
        add(twojewodztwo);

        JLabel lmiasto = new JLabel("Miasto");
        lmiasto.setBounds(10, 120, 60, 30);
        add(lmiasto);

        tmiasto = new JTextField();
        tmiasto.setBounds(100, 120, 120, 30);
        add(tmiasto);

        JLabel lulica = new JLabel("Ulica");
        lulica.setBounds(10, 160, 60, 30);
        add(lulica);

        tulica = new JTextField();
        tulica.setBounds(100, 160, 120, 30);
        add(tulica);

        JLabel lnr_budynku = new JLabel("Nr budynku");
        lnr_budynku.setBounds(10, 200, 60, 30);
        add(lnr_budynku);

        tnr_budynku = new JTextField();
        tnr_budynku.setBounds(100, 200, 120, 30);
        add(tnr_budynku);

        JLabel lnr_lokalu = new JLabel("Nr lokalu");
        lnr_lokalu.setBounds(10, 240, 60, 30);
        add(lnr_lokalu);

        tnr_lokalu = new JTextField();
        tnr_lokalu.setBounds(100, 240, 120, 30);
        add(tnr_lokalu);

        manager = new JButton("Manager");
        manager.setBounds(10, 280, 60, 30);
        manager.addActionListener(this);
        add(manager);

        zarejetruj = new JButton("Zarejestruj");
        zarejetruj.setBounds(75, 350, 150, 30);
        zarejetruj.addActionListener(this);
        add(zarejetruj);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        if (zrodlo == manager) {
            f = new JFrame();
            f.setLocation(200, 200);
            try {
                tlista = this.stworz_liste("Select * from Osoba");
                JScrollPane sp = new JScrollPane(tlista);
                f.add(sp);
                f.setSize(200, 300);
                f.setVisible(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        }
        if (zrodlo == zarejetruj) {
            String komunikat = "";
            try {
                int manager_id = tlista.getSelectedRow();
                if (tnr_budynku.getText().equals("") || tnr_lokalu.getText().equals("") ||
                        tulica.getText().equals("") || tmiasto.getText().equals("") || twojewodztwo.getText().equals("") ||
                        ttyp.getText().equals("") || tnazwa.getText().equals("")) {
                    komunikat = "Wszystkie pola musza byc wypelnione";

                } else {
                    ResultSet rs;
                    int adresId = -1;
                    String sql = "insert into Adres values (?, ?, ?, ?, ?)";
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
                    sql = "insert into Oddzial values (?, ? ,? , ?)";
                    psmt = con.prepareStatement(sql);
                    psmt.setInt(1, adresId);
                    psmt.setInt(2, manager_id);
                    psmt.setString(3, tnazwa.getText());
                    psmt.setString(4, ttyp.getText());
                    psmt.executeUpdate();
                    komunikat = "Pomyslnie dodano oddzial";
                }
            } catch (SQLException throwables) {
                komunikat = "Wystąpił nieznany bład";
                throwables.printStackTrace();
            } finally {
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

    public JTable stworz_liste(String sql) throws SQLException {
        //Zapytanie SQL
        ArrayList<String[]> lista = new ArrayList<>();
        Statement zapytanie2 = con.createStatement();


        ResultSet rs = zapytanie2.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int ile_kolumn = rsmd.getColumnCount();

        String[] columns = new String[ile_kolumn];
        for (int i = 0; i < ile_kolumn; i++) {
            columns[i] = rsmd.getColumnName(i + 1);
        }
        //pobranie wybranych kolumn do jednej listy
        while (rs.next()) {
            String[] t = new String[ile_kolumn];
            for (int i = 0; i < ile_kolumn; i++) {
                t[i] = rs.getString(i + 1);
            }
            lista.add(t);
        }
        //konwersja listy do tablicy na potrzeby JTable
        String[][] array = new String[lista.size()][];
        for (int i = 0; i < array.length; i++) {
            String[] row = lista.get(i);
            array[i] = row;
        }
        for (int i = 0; i < array.length; i++) {
            System.out.println(Arrays.toString(array[i]));
        }
        zapytanie2.close();

        //wygenerowanie tabeli
        return new JTable(array, columns);

    }

}

