import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;

public class Misc {
    public static JFrame generuj_komunikat(String komunikat) {
        JFrame f = new JFrame();
        f.setLocation(200, 200);
        JLabel label = new JLabel(komunikat);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        f.add(label);
        f.setSize(250, 200);
        f.setVisible(true);
        return f;
    }

    public static JTable stworz_liste(String sql, Connection con) throws SQLException {
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
        zapytanie2.close();

        //wygenerowanie tabeli
        return new JTable(array, columns);

    }
}
