import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Edytowanie extends JFrame implements ActionListener, ItemListener {

    Connection con;

    JPanel main_panel, mid_panel, bot_panel;

    JComboBox<String> cblista;
    JScrollPane splista;
    JTable jtabela;
    String[] tabele;
    ArrayList<String> kolumny;
    JButton bedytuj;
    JFrame f;

    public Edytowanie(Connection con) {
        this.con = con;
        kolumny = new ArrayList<>();
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
                String sql = "Select * from " + tabela;
                jtabela = Misc.stworz_liste(sql, con);
                splista = new JScrollPane(jtabela);
                mid_panel.add(splista);
                bedytuj = new JButton("Edytuj");
                bedytuj.addActionListener(this);

                bot_panel.add(bedytuj);

                main_panel.add(mid_panel);
                main_panel.add(bot_panel);
                revalidate();
                repaint();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql);
                ResultSetMetaData rsmd = rs.getMetaData();
                int numberOfColumns = rsmd.getColumnCount();
                kolumny = new ArrayList<>();
                for (int i = 1; i < numberOfColumns + 1; i++) {
                    String columnName = rsmd.getColumnName(i);
                    kolumny.add(columnName);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object zrodlo = e.getSource();
        if (zrodlo == bedytuj) {
            if (jtabela.isEditing()) {
                jtabela.getCellEditor().stopCellEditing();
            }
            int iloscKolumt = jtabela.getColumnCount();
            ArrayList<String> wartosci = new ArrayList<>();
            int wiersz = jtabela.getSelectedRow();
            String komunikat = "";
            for (int i = 0; i < iloscKolumt; i++) {
                wartosci.add((String) jtabela.getValueAt(wiersz, i));
            }
            String tabela = (String) cblista.getSelectedItem();
            String sql = "update " + tabela + " set ";
            String kolumna;
            int i = 1;
            String id = wartosci.get(0);
            kolumny.remove(0);
            for (Iterator<String> it = kolumny.iterator(); it.hasNext(); ) {
                kolumna = it.next();
                System.out.println(kolumna);

                System.out.println(wartosci.get(i));
                sql += kolumna + " = '" + wartosci.get(i) + "' ,";
                i++;

            }
            sql = sql.substring(0, sql.length() - 1);
            sql += " where id = " + id;
            System.out.println(sql);
            try {
                PreparedStatement psmt = con.prepareStatement(sql);

                if (psmt.executeUpdate() == 1) {
                    komunikat = "Pomyślnie edytowano";
                } else {
                    komunikat = "Wystąpił nieznany błąd";
                }
            } catch (SQLException throwables) {
                if (throwables.getErrorCode() == 547) {
                    String[] wyjatek = throwables.getMessage().split(" ");
                    komunikat = "<html>Wystąpił konfilkt z tabela " +
                            wyjatek[wyjatek.length - 3].replaceAll(",", "") +
                            " kolumną " + wyjatek[wyjatek.length - 1].replaceAll("", "") +
                            "<br> Należy najpierw usunąć połączony rekord</html>";
                } else {
                    throwables.printStackTrace();
                    komunikat = "Wystąpił nieznany błąd";

                }
            } finally {
                f = Misc.generuj_komunikat(komunikat);
            }
        }
    }

}
