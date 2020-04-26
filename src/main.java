import javax.swing.*;
import java.awt.event.*;

import java.sql.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class main extends JFrame implements ActionListener{
    /*
    Tu podajemy url do bazy danych
    */
    String url = "jdbc:sqlserver://localhost\\SQLEXPRESS:52333;database=Sklep;user=kwisniewski;password=temp123";
    Connection con;
    JButton zaloguj, zarejestruj_klient, bzamow;
    JTextField klientNazwiskoTF, login, tilosc;
    JPasswordField haslo;
    JTable tlista;
    JButton klientNazwisko;
    JFrame frame1;
    JMenuBar menu;
    JPanel mainPanel;
    JMenu Plik, Pomoc, SQL;
    JMenuItem Wyjscie, PodPomoc, Produkty, Klient;
    String typUzytkownika;

    List<String[]> lista=new ArrayList<String[]>();

    public main() throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        con = DriverManager.getConnection(this.url );
        setSize(400,300);
        setLocation(100,100);
        setResizable(false);
        setLayout(null);

        JLabel llogin = new JLabel("Login:");
        llogin.setBounds(90 , 30, 50, 30);

        login = new JTextField("Login");
        login.setBounds(150, 30, 100, 30);

        JLabel lhaslo = new JLabel("Haslo:");
        lhaslo.setBounds(90 , 70, 50, 30);

        haslo = new JPasswordField("Haslo");
        haslo.setBounds(150, 70, 100, 30);


        zaloguj = new JButton("Zaloguj");
        zaloguj.addActionListener(this);
        zaloguj.setBounds(75, 120, 100, 30);

        zarejestruj_klient = new JButton("Zarejestruj");
        zarejestruj_klient.addActionListener(this);
        zarejestruj_klient.setBounds(200, 120, 100, 30);

        mainPanel = new JPanel(null);
        mainPanel.add(llogin);
        mainPanel.add(login);
        mainPanel.add(lhaslo);
        mainPanel.add(haslo);
        mainPanel.add(zaloguj);
        mainPanel.add(zarejestruj_klient);

        llogin.setBounds(90 , 30, 50, 30);
        login.setBounds(150, 30, 100, 30);
        lhaslo.setBounds(90 , 70, 50, 30);
        haslo.setBounds(150, 70, 100, 30);
        zarejestruj_klient.setBounds(150, 70, 100, 30);
        zaloguj.setBounds(75, 120, 100, 30);



        setContentPane(mainPanel);

        klientNazwiskoTF = new JTextField("Imie i nazwisko klienta");
        klientNazwiskoTF.addActionListener(this);

        klientNazwisko = new JButton("Ok");
        klientNazwisko.addActionListener(this);

        menu = new JMenuBar();
        Plik=new JMenu("Plik");
        Pomoc=new JMenu("Pomoc");
        SQL = new JMenu("SQL");


        Wyjscie=new JMenuItem("Wyjście");
        Wyjscie.addActionListener(this);
        Wyjscie.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
        Plik.add(Wyjscie);


        PodPomoc = new JMenuItem("Pomoc");
        PodPomoc.addActionListener(this);
        Pomoc.add(PodPomoc);


        Produkty = new JMenuItem("Produkty");
        Produkty.addActionListener(this);
        SQL.add(Produkty);

        Klient = new JMenuItem("Klient");
        Klient.addActionListener(this);
        SQL.add(Klient);


        setJMenuBar(menu);
        menu.add(Plik);
        menu.add(Pomoc);
        menu.add(SQL);
        }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        main okno=new main();

        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        Object zrodlo=e.getSource();
        if (zrodlo == bzamow) {
            try {

                String sql = "insert into Zamowienie (ilosc, produkt, wartosc_brutto, data_zamowienia, data_realizacji) values (?, ?, ?, GETDATE(), null)";
                PreparedStatement psmt = con.prepareStatement(sql , PreparedStatement.RETURN_GENERATED_KEYS);
                psmt.setString(1, tilosc.getText());
                psmt.setInt(2, tlista.getSelectedRow() + 1);
                System.out.println(tlista.getSelectedRow());
                float cena_netto =Float.parseFloat((String) tlista.getValueAt(tlista.getSelectedRow(), 1));
                float vat = Float.parseFloat((String)tlista.getValueAt(tlista.getSelectedRow(), 2) );
                int ilosc = Integer.parseInt(tilosc.getText());
                System.out.println( cena_netto);
                System.out.println( vat);
                System.out.println( ilosc);

                float cena_brutto = cena_netto * (1+vat) * ilosc;

                psmt.setFloat(3, cena_brutto);
                if (psmt.executeUpdate() == 1) {
                    int id_zamowienia = -1;
                    ResultSet rs = psmt.getGeneratedKeys();
                    if (rs.next()) {
                         id_zamowienia = rs.getInt(1);
                    }
                    this.wyswietl_komunikat("<html>Towar zamowiono pomyslnie<br/>Twoj numer zamowienia to:  " + id_zamowienia + "</html>");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (zrodlo==zaloguj) {
            String slogin = login.getText();
            String shaslo = String.valueOf(haslo.getPassword());
            String komunikat = "";
            try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select haslo, typ from Uzytkownik where login = '"+ slogin +"'");
                rs.next();
                String resp = rs.getString(1);
                if (resp.equals(shaslo)) {
                    komunikat = "Zalogowano poprawnie";
                    typUzytkownika = rs.getString(2);
                    mainPanel = new JPanel(null);
                }else
                {
                    komunikat = "Podano zly login i/lub haslo";
                }

            } catch (SQLException ex) {
                if (ex.getMessage().equals("The result set has no current row.")) {
                    komunikat = "Podano zly login i/lub haslo";
                }else {
                    komunikat = "Wystapil nieznany blad";
                    ex.printStackTrace();
                }
            }
            finally {
                this.wyswietl_komunikat(komunikat);
            }

        }
        if (zrodlo == zarejestruj_klient) {
            frame1 = new rejestracja(con, "Klient");
        }

        if (zrodlo==Klient) {
            frame1 = new JFrame();
            frame1.setLayout(null);
            frame1.setLocation(200, 200);
            frame1.setSize(200, 200);
            klientNazwiskoTF.setBounds(10, 10, 150, 30);
            frame1.add(klientNazwiskoTF);
            klientNazwisko.setBounds(75, 75, 50, 25);
            frame1.add(klientNazwisko);

            frame1.setVisible(true);

        }
        if (zrodlo==Produkty || zrodlo==klientNazwisko)
        {
            frame1 =new JFrame();
            frame1.setLocation(200,200);
            try{

                //Zapytanie SQL
                Statement zapytanie2 = con.createStatement();
                String sql2;
                if (zrodlo==Produkty) {
                    sql2 = "select nazwa, cena_netto, vat, typ from Produkt";
                }
                else {
                    sql2 = "select top 1 ContactName as 'nazwisko klienta', OrderDate as 'data zamówienia', E.LastName as 'nazwisko pracownika' from Orders inner join Customers C2 on Orders.CustomerID = C2.CustomerID" +
                            " inner join Employees E on Orders.EmployeeID = E.EmployeeID where ContactName = '"
                            + this.klientNazwiskoTF.getText()  + "' order by OrderDate desc";
                }

                ResultSet rs = zapytanie2.executeQuery(sql2);
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
                lista = new ArrayList<String[]>();
                for (int i = 0; i < array.length; i++){
                    System.out.println(Arrays.toString(array[i]));
                }
                zapytanie2.close();

                //wygenerowanie tabeli
                tlista = new JTable(array,columns);
                JScrollPane sp;
                sp = new JScrollPane(tlista);
                mainPanel.removeAll();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.add(sp);

                JPanel bot_panel = new JPanel();
                bzamow = new JButton("Zamow");
                bzamow.addActionListener(this);

                tilosc = new JTextField("Ilosc");

                bot_panel.add(bzamow);
                bot_panel.add(tilosc);

                mainPanel.add(bot_panel);
                setLocation(200,50);
                setSize(300,400);
                setVisible(true);

            }
            catch(SQLException error_polaczenie) {
                System.out.println(error_polaczenie);}
        }
        if(zrodlo==Wyjscie) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            dispose();
        }
        if(zrodlo==PodPomoc) {
            this.wyswietl_komunikat("Nie ma pomocy");
        }
    }
    public void wyswietl_komunikat(String komunikat) {
        frame1 = new JFrame();
        frame1.setLocation(200,200);
        JLabel label = new JLabel(komunikat);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        frame1.add(label);
        frame1.setSize(200, 200);
        frame1.setVisible(true);
    }

}

