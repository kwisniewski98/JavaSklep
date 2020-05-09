import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Zapotrzebowanie extends JFrame implements ActionListener {
    JButton zarejetruj, bStan, bStanPotwierdz;
    Connection con;
    JFrame f, f2;
    int idStan = -1;
    JTable tStan;
    JTextField tilosc;
    public Zapotrzebowanie(Connection con){
        this.con = con;
        setLayout(null);
        setResizable(false);
        setLocation(200, 200);
        setSize(250, 500);



        JLabel lOddzial = new JLabel("Oddzial");
        lOddzial.setBounds(10, 0 ,60, 30);
        add(lOddzial);

        bStan= new JButton("Stan");
        bStan.setBounds(100, 0, 120, 30);
        add(bStan);
        bStan.addActionListener(this);


        JLabel lilosc = new JLabel("ilosc");
        lilosc.setBounds(10, 80 ,60, 30);
        add(lilosc);

        tilosc = new JTextField();
        tilosc.setBounds(100, 80, 120, 30);
        add(tilosc);

        zarejetruj = new JButton("Dodaj");
        zarejetruj.setBounds(75, 350, 150, 30);
        zarejetruj.addActionListener(this);
        add(zarejetruj);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        if (zrodlo == bStanPotwierdz) {
            idStan = tStan.getSelectedRow();
            f2.dispatchEvent(new WindowEvent(f2, WindowEvent.WINDOW_CLOSING));
        }

        if (zrodlo == bStan) {

            String sql = "select Oddzial.nazwa, ilosc , Produkt.nazwa from Stan inner join Oddzial on Stan.oddzial = Oddzial.id inner join Produkt on Stan.produkt = Produkt.id";

            try {
                JPanel panel = new JPanel();
                f2 = new JFrame();
                tStan = this.stworz_liste(sql);
                JScrollPane sp;
                sp = new JScrollPane(tStan);
                panel.removeAll();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(sp);

                JPanel bot_panel = new JPanel();
                bStanPotwierdz = new JButton("Potwierdz");
                bStanPotwierdz.addActionListener(this);

                bot_panel.add(bStanPotwierdz);

                panel.add(bot_panel);
                f2.setContentPane(panel);

                f2.setLocation(200,50);
                f2.setSize(500,400);
                f2.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        if (zrodlo == zarejetruj) {
            String komunikat = "";
                   try {
                       if (tilosc.getText().equals("")  ) {
                           komunikat = "Wszystkie pola musza byc wypelnione";

                       }else {
                           String sql = "insert into Zapotrzebowanie values (?, ?, null, 'Przyjeto', GETDATE())";
                           PreparedStatement psmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                           psmt.setInt(1, idStan+1);
                           psmt.setString(2, tilosc.getText());
                           int rowAffected = psmt.executeUpdate();
                           if (rowAffected == 1) {

                               komunikat = "Pomyslnie dodano Zapotrzebowanie";

                           } else {
                               komunikat = "Nie znaleziono takiego typu";
                           }
                       }

                       } catch (SQLException ex) {
                       ex.printStackTrace();
                   }

                     finally{
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
        List<String[]> lista=new ArrayList<String[]>();
        Statement zapytanie2 = con.createStatement();


        ResultSet rs = zapytanie2.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int ile_kolumn = rsmd.getColumnCount();

        String[] columns = new String[ile_kolumn];
        for (int i = 0; i < ile_kolumn; i++){
            columns[i] = rsmd.getColumnName(i+1);
        }
        //pobranie wybranych kolumn do jednej listy
        while(rs.next()) {
            String[] t= new String[ile_kolumn];
            for (int i = 0; i < ile_kolumn; i++){
                t[i] = rs.getString(i+1);
            }
            lista.add(t);
        }
        //konwersja listy do tablicy na potrzeby JTable
        String[][] array =new String[lista.size()][];
        for (int i=0;i<array.length;i++){
            String[] row=lista.get(i);
            array[i]=row;
        }
        zapytanie2.close();

        //wygenerowanie tabeli
        return new JTable(array,columns);

    }
}

