import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;

import java.sql.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class Glowne extends JFrame implements ActionListener{
    /*
    Tu podajemy url do bazy danych
    */
    int id_osoba;
    String url = "jdbc:sqlserver://localhost\\SQLEXPRESS:52333;database=Sklep;user=kwisniewski;password=temp123";
    Connection con;
    JButton zaloguj, zarejestruj_klient, bzamow, btypy, bpotwierdz_zamowienie;
    JTextField klientNazwiskoTF, login, tilosc;
    JPasswordField haslo;
    String typ = "";
    JTable tlista, ttypy;
    JButton klientNazwisko;
    JFrame frame1;
    JMenuBar menu;
    JPanel mainPanel;
    JMenu Plik, Pomoc, Klientm, Pracownik;
    JMenuItem Wyjscie, PodPomoc, Produkty, Klient,
            Oddzialy, Status, DodajProdukt, DodajTyp, DodajZapotrzebowanie;
    String typUzytkownika;

    List<String[]> lista=new ArrayList<String[]>();

    public Glowne() throws ClassNotFoundException, SQLException {
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
        zarejestruj_klient.setBounds(200, 120, 100, 30);
        zaloguj.setBounds(75, 120, 100, 30);



        setContentPane(mainPanel);

        klientNazwiskoTF = new JTextField("Imie i nazwisko klienta");
        klientNazwiskoTF.addActionListener(this);

        klientNazwisko = new JButton("Ok");
        klientNazwisko.addActionListener(this);

        menu = new JMenuBar();
        Plik=new JMenu("Plik");
        Pomoc=new JMenu("Pomoc");
        Klientm = new JMenu("Klient");

        menu.add(Klientm);

        Wyjscie=new JMenuItem("Wyjście");
        Wyjscie.addActionListener(this);
        Wyjscie.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
        Plik.add(Wyjscie);


        PodPomoc = new JMenuItem("Pomoc");
        PodPomoc.addActionListener(this);
        Pomoc.add(PodPomoc);


        Produkty = new JMenuItem("Produkty");
        Produkty.addActionListener(this);

        Klient = new JMenuItem("Klient");
        Klient.addActionListener(this);


        setJMenuBar(menu);
        menu.add(Plik);
        menu.add(Pomoc);
        }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Glowne okno=new Glowne();

        okno.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        okno.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        Object zrodlo=e.getSource();
        if (zrodlo == DodajZapotrzebowanie){
            frame1 = new Zapotrzebowanie(con);
        }
        if (zrodlo == DodajTyp) {
            frame1 = new Typ(con);
        }
        if (zrodlo == DodajProdukt){
            frame1 = new Produkt(con);
        }
        if (zrodlo == bpotwierdz_zamowienie) {
            String id = (String) tlista.getValueAt(tlista.getSelectedRow(), 0);
            try {
                String sql = "UPDATE Zamowienie set status = 'Zakonczone', data_realizacji = GETDATE() where id = '" + id + "'";
                System.out.println(id);
                Statement st = con.createStatement();
                st.executeUpdate(sql);
                wyswietl_komunikat("Potwierdzono otrzymanie zamowienia nr: " + id);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        if (zrodlo == btypy)
        {
            String sql = "select skrot, opis, przeznaczenie from Typ";
            try {
                ttypy = stworz_liste(sql);
                JScrollPane sp;
                sp = new JScrollPane(ttypy);
                frame1 = new JFrame();
                ttypy.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
                    public void valueChanged(ListSelectionEvent event) {
                        typ = (String) ttypy.getValueAt(ttypy.getSelectedRow(), 0);
                        System.out.println(typ);
                        Produkty.doClick();
                    }
                });
                frame1.add(sp);
                frame1.setLocation(200,50);
                frame1.setSize(300,400);
                frame1.setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

        if (zrodlo == Status){

            String sql ="select Zamowienie.id as 'nr. zamowienia' ,Zamowienie.ilosc, Produkt.nazwa, status, data_zamowienia" +
                    " from Zamowienie inner join Stan on Zamowienie.produkt = Stan.id" +
                    " inner join Produkt on Produkt.id = Stan.produkt where Zamowienie.osoba = '" + id_osoba + "'";
            System.out.println(sql);
            try {
                tlista = this.stworz_liste(sql);
                JScrollPane sp;
                sp = new JScrollPane(tlista);
                mainPanel.removeAll();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.add(sp);

                JPanel bot_panel = new JPanel();
                bpotwierdz_zamowienie = new JButton("Potwierdz otrzymanie zamowienia");
                bpotwierdz_zamowienie.addActionListener(this);
                bot_panel.add(bpotwierdz_zamowienie);

                mainPanel.add(bot_panel);
                setLocation(200,50);
                setSize(300,400);
                setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        if (zrodlo == Oddzialy) {
            try {
                tlista = this.stworz_liste( "Select nazwa, typ, wojewodztwo, miasto, ulica," +
                        " concat(nr_budynku,'/', nr_lokalu ) as 'nr budynku / nr mieszkania'" +
                        "from Oddzial inner join Adres on Adres.id = Oddzial.Adres ");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            JScrollPane sp;
            sp = new JScrollPane(tlista);
            mainPanel.removeAll();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(sp);
            setLocation(200,50);
            setSize(500,400);
            setVisible(true);
        }

        if (zrodlo == bzamow) {
            try {

                String produkt_nazwa = (String)tlista.getValueAt(tlista.getSelectedRow(), 0);
                String oddzial_nazwa = (String)tlista.getValueAt(tlista.getSelectedRow(), 5);
                String sql = "Select S.id, S.ilosc from Produkt inner join Stan S on Produkt.id = S.produkt " +
                        "inner join Oddzial O on S.oddzial = O.id where Produkt.Nazwa = '" + produkt_nazwa + "' and O.nazwa = '"
                        + oddzial_nazwa +"'";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                rs.next();
                String id = rs.getString(1);
                int ilosc_stan = rs.getInt(2);
                int ilosc = Integer.parseInt(tilosc.getText());

                if (ilosc_stan >= ilosc) {

                    sql = "UPDATE Stan set Stan.ilosc='" + String.valueOf (ilosc_stan - ilosc) + "' where id ='" + id +"'";
                    st.executeUpdate(sql);
                    sql = "insert into Zamowienie (osoba, ilosc, produkt, wartosc_brutto, data_zamowienia, data_realizacji, status)" +
                            " values (?, ?, ?,?,   GETDATE(), null, 'Przyjeto')";
                    PreparedStatement psmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    psmt.setInt(1, id_osoba);
                    psmt.setString(2, tilosc.getText());
                    psmt.setString(3, id);
                    System.out.println(tlista.getSelectedRow());
                    float cena_netto = Float.parseFloat((String) tlista.getValueAt(tlista.getSelectedRow(), 1));
                    float vat = Float.parseFloat((String) tlista.getValueAt(tlista.getSelectedRow(), 2));
                    System.out.println(cena_netto);
                    System.out.println(vat);
                    System.out.println(ilosc);

                    float cena_brutto = cena_netto * (1 + vat) * ilosc;

                    psmt.setFloat(4, cena_brutto);
                    if (psmt.executeUpdate() == 1) {
                        int id_zamowienia = -1;
                        rs = psmt.getGeneratedKeys();
                        if (rs.next()) {
                            id_zamowienia = rs.getInt(1);
                        }
                        this.wyswietl_komunikat("<html>Towar zamowiono pomyslnie<br/>Twoj numer zamowienia to:  " + id_zamowienia + "</html>");
                    }
                }
                else {
                    this.wyswietl_komunikat("Nie mamy takiej ilosci na stanie");
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
                ResultSet rs = st.executeQuery("select haslo, typ, osoba from Uzytkownik where login = '"+ slogin +"'");
                rs.next();
                String resp = rs.getString(1);
                if (resp.equals(shaslo)) {
                    komunikat = "Zalogowano poprawnie";
                    typUzytkownika = rs.getString(2);
                    id_osoba = rs.getInt(3);
                    this.zaloguj(typUzytkownika);
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
            frame1 = new Rejestracja(con, "Klient");
        }

        if (zrodlo==Produkty)
        {
            frame1 =new JFrame();
            frame1.setLocation(200,200);
            try{
                String sql = "select Produkt.nazwa, cena_netto, vat, Typ.skrot as 'Typ', Stan.ilosc as 'ilosc', Oddzial.nazwa as 'oddzial'" +
                        " from Produkt inner join Typ on Typ.id = Produkt.typ" +
                        " inner join Stan on Stan.produkt = Produkt.id  inner join Oddzial on Stan.oddzial = Oddzial.id";
                if (!typ.equals("")) {
                    sql += " where Typ.skrot = '" + typ+"'";
                }

                tlista = this.stworz_liste(sql);
                JScrollPane sp;
                sp = new JScrollPane(tlista);
                mainPanel.removeAll();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                mainPanel.add(sp);

                JPanel bot_panel = new JPanel();
                bzamow = new JButton("Zamow");
                bzamow.addActionListener(this);

                btypy = new JButton("Typy");
                btypy.addActionListener(this);
                bot_panel.add(btypy);

                tilosc = new JTextField("Ilosc");

                bot_panel.add(bzamow);
                bot_panel.add(tilosc);

                mainPanel.add(bot_panel);
                setLocation(200,50);
                setSize(500,400);
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

    public void zaloguj(String typUzytkownika){
        Produkty = new JMenuItem("Produkty");
        Produkty.addActionListener(this);
        Klientm.add(Produkty);

        Oddzialy = new JMenuItem("Oddzialy");
        Oddzialy.addActionListener(this);
        Klientm.add(Oddzialy);

        Status = new JMenuItem("Status zamówienia");
        Status.addActionListener(this);
        Klientm.add(Status);

        if (typUzytkownika.equals("Sprzedawca")){
            Pracownik = new JMenu("Pracownik");
            menu.add(Pracownik);

            DodajProdukt = new JMenuItem("Dodaj produkt");
            DodajProdukt.addActionListener(this);
            Pracownik.add(DodajProdukt);

            DodajTyp = new JMenuItem("Dodaj typ");
            DodajTyp.addActionListener(this);
            Pracownik.add(DodajTyp);

            DodajZapotrzebowanie = new JMenuItem("Dodaj Zapotrzebowanie");
            DodajZapotrzebowanie.addActionListener(this);
            Pracownik.add(DodajZapotrzebowanie);

            menu.revalidate();
            menu.repaint();

        }


    }
    public JTable stworz_liste(String sql) throws SQLException {
        //Zapytanie SQL
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
        lista = new ArrayList<String[]>();
        for (int i = 0; i < array.length; i++){
            System.out.println(Arrays.toString(array[i]));
        }
        zapytanie2.close();

        //wygenerowanie tabeli
        return new JTable(array,columns);

    }

}

