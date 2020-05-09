import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
                jtabela = Misc.stworz_liste(sql, con);
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
                f = Misc.generuj_komunikat(komunikat);
            }
        }
    }

}
