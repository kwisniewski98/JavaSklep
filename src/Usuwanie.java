import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Usuwanie extends JFrame implements ActionListener, ItemListener {

    Connection con;

    JPanel main_panel, mid_panel, bot_panel;

    JComboBox<String> cblista;
    JScrollPane splista;
    JTable jtabela;
    String[] tabele;
    JButton busun;
    JFrame f;

    public Usuwanie(Connection con) {
        this.con = con;

        main_panel = new JPanel();
        main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
        setContentPane(main_panel);
        setLocation(200, 200);
        setSize(500, 500);
        tabele = new String[]{"Adres", "Oddzial", "Osoba", "Produkt", "Typ", "Uzytkownik", "Zamowienie", "Zapotrzebowanie"};
        cblista = new JComboBox<>(tabele);
        cblista.setSelectedIndex(0);
        cblista.addItemListener(this);
        JPanel top_panel = new JPanel();
        top_panel.add(cblista);
        main_panel.add(top_panel);
        setVisible(true);

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object item = e.getSource();
        if (item == cblista) {
            try {
                try {
                    remove(mid_panel);
                    remove(bot_panel);
                } catch (NullPointerException ex) {
                }

                mid_panel = new JPanel();
                bot_panel = new JPanel();
                String tabela = (String) cblista.getSelectedItem();
                System.out.println(tabela);
                String sql = "Select * from " + tabela;
                jtabela = this.stworz_liste(sql);
                splista = new JScrollPane(jtabela);
                mid_panel.add(splista);
                busun = new JButton("Usun");
                busun.addActionListener(this);

                bot_panel.add(busun);

                main_panel.add(mid_panel);
                main_panel.add(bot_panel);
                revalidate();
                repaint();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        if (zrodlo == busun) {
            String komunikat = "";
            int id = jtabela.getSelectedRow() + 1;
            String tabela = String.valueOf(cblista.getSelectedItem());
            String sql = "delete from " + tabela + " where id = '" + id + "'";
            try {
                PreparedStatement psmt = con.prepareStatement(sql);

                if (psmt.executeUpdate() == 1) {
                    komunikat = "Pomyślnie usunięto";
                } else {
                    komunikat = "Wystąpił nieznany błąd";
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                komunikat = "Wystąpił nieznany błąd";
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
        List<String[]> lista = new ArrayList<String[]>();
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
        zapytanie2.close();

        //wygenerowanie tabeli
        return new JTable(array, columns);

    }
}
