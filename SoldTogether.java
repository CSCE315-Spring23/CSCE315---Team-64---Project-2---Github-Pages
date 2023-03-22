import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

// currently, gui and window setup is neat, but the actual plots are still needed
// find a java import for plotting data, and also a function to actually form and normalize the data

public class SoldTogether
{
    private JFrame f;
    private Connection conn;
    private Clicker click;
    private JLabel recText;
    private int startDay=1;
    private int startMonth=1;
    private int startYear=1970;
    private int endDay=2;
    private int endMonth=2;
    private int endYear=1971;
    private HashMap<String, Smoothie> smoothieMenu;
    private HashMap<String, Integer> comboCount;
    private JPanel graphs;
    private HashMap<String, Double> IngredientCount;
    private HashMap<String, Double> ActualCount;
    private DefaultListModel<String> listModel;
    public SoldTogether(JFrame frame, Connection c)
    {
        smoothieMenu = new HashMap<String, Smoothie>();
        comboCount = new HashMap<String, Integer>();
        IngredientCount = new HashMap<String, Double>();
        ActualCount = new HashMap<String, Double>();

        conn = c;
        f = frame;
        click = new Clicker();
        f.getContentPane().removeAll();
        makeSalesFrame();
    }

    private void makeSalesFrame()
    {
        JPanel manPages = new JPanel();
        manPages.setSize(270, 505);
        manPages.setLocation(920, 10);
        manPages.setBorder(BorderFactory.createLineBorder(Color.black));
        manPages.setLayout(null);

        JLabel icon = new JLabel();
        icon.setIcon(new ImageIcon("./imgs/Icon.png"));
        icon.setSize(80, 80);
        icon.setLocation(95, 10);
        manPages.add(icon);

        JButton inv = new JButton("Inventory");
        inv.setSize(250, 35);
        inv.setLocation(10, 100);
        inv.addActionListener(click);
        inv.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        manPages.add(inv);

        JButton rpt = new JButton("Sales Report");
        rpt.setSize(250, 35);
        rpt.setLocation(10, 145);
        rpt.addActionListener(click);
        manPages.add(rpt);

        JButton excess = new JButton("Excess Report");
        excess.setSize(250, 35);
        excess.setLocation(10, 190);
        excess.addActionListener(click);
        manPages.add(excess);

        JButton Zrep = new JButton("Z report");
        Zrep.setSize(250, 35);
        Zrep.setLocation(10, 235);
        Zrep.addActionListener(click);
        manPages.add(Zrep);

        JButton Xrep = new JButton("X Report");
        Xrep.setSize(250, 35);
        Xrep.setLocation(10, 280);
        Xrep.addActionListener(click);
        manPages.add(Xrep);

        JButton together = new JButton("Sold Together Items");
        together.setSize(250, 35);
        together.setLocation(10, 325);
        together.addActionListener(click);
        manPages.add(together);

        JButton restock = new JButton("Restock");
        restock.setSize(250, 35);
        restock.setLocation(10, 370);
        restock.addActionListener(click);
        manPages.add(restock);

        JButton emp = new JButton("Employees");
        emp.setSize(250, 35);
        emp.setLocation(10, 415);
        emp.addActionListener(click);
        manPages.add(emp);

        JButton addItem = new JButton("New Item");
        addItem.setSize(250, 35);
        addItem.setLocation(10, 460);
        addItem.addActionListener(click);
        manPages.add(addItem);

        JButton server = new JButton("Server");
        server.setSize(250, 35);
        server.setLocation(10, 505);
        server.addActionListener(click);
        manPages.add(server);

        JPanel dateSelect = new JPanel();
        dateSelect.setSize(270, 480);
        dateSelect.setLocation(920, 540);
        dateSelect.setBorder(BorderFactory.createLineBorder(Color.black));
        dateSelect.setLayout(null);

        JButton startDate = new JButton("Start Date:");
        startDate.setSize(250, 30);
        startDate.setLocation(10, 10);
        startDate.addActionListener(click);
        dateSelect.add(startDate);

        JButton endDate = new JButton("End Date:");
        endDate.setSize(250, 30);
        endDate.setLocation(10, 40);
        endDate.addActionListener(click);
        dateSelect.add(endDate);

        String[] weekAb = {"M","T","W","Th","F","Sa","Su"};
        for (int i=0;i<7;i++)
        {
            JButton dayBut = new JButton(weekAb[i]);
            dayBut.setSize(35,30);
            dayBut.setMargin(new Insets(0, 0, 0, 0));
            dayBut.setLocation((10+i*35),70);
            dayBut.addActionListener(click);
            dateSelect.add(dayBut);
        }


        recText = new JLabel("Select Date Range:");
        recText.setSize(250, 420);
        recText.setLocation(10, 10);
        recText.setBorder(BorderFactory.createLineBorder(Color.black));
        recText.setHorizontalAlignment(SwingConstants.CENTER);
        recText.setVerticalAlignment(SwingConstants.CENTER);
        dateSelect.add(recText);

        graphs = new JPanel();
        graphs.setSize(900, 800);
        graphs.setLocation(0, 0);
        graphs.setBorder(BorderFactory.createLineBorder(Color.black));
        graphs.setLayout(null);

        JLabel bestText = new JLabel("Sold Together Items",SwingConstants.CENTER);
        bestText.setSize(600, 100);
        bestText.setLocation(200, 10);
        bestText.setFont(new Font("Serif", Font.PLAIN, 36));
        graphs.add(bestText);

        listModel = new DefaultListModel<String>();
        JList<String> displayList = new JList<String>(listModel);
        graphs.add(displayList);
        displayList.setLayout(null);
        displayList.setLocation(200, 110);
        displayList.setSize(new Dimension(400, 800));

        displayList.setVisible(true);

        loadList();

        f.add(dateSelect);
        f.add(manPages);

        f.add(graphs);
        recText.setText(startMonth+"/"+startDay+"/"+startYear+" - "+endMonth+"/"+endDay+"/"+endYear);

        f.setVisible(true);
        f.validate();
        f.repaint();
    }

    private void loadList() {
        try {
            conn = null;
            String teamNumber = "team_64";
            String dbName = "csce315331_" + teamNumber;
            String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
            dbSetup myCredentials = new dbSetup();

            //Connecting to the database
            try {
                conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);
            }

            Statement stmt = conn.createStatement();
            String sqlStatement2 = "SELECT * FROM items;";
            ResultSet result2 = stmt.executeQuery(sqlStatement2);
            while (result2.next() ) {
                String name = result2.getString("item_name");
                Double amount = result2.getDouble("item_quantitylbs");

                ActualCount.put(name, amount);

            }

            System.out.println(ActualCount.size());

            String sqlStatement1 = "SELECT * FROM smoothies;";
            ResultSet result1 = stmt.executeQuery(sqlStatement1);
            while (result1.next() ) {
                String name = result1.getString("sm_name");
                int id = result1.getInt("sm_id");
                double price = result1.getDouble("sm_price");
                price = BigDecimal.valueOf(price)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
                String ingredients = result1.getString("sm_ingredients"); //Retrieving all the smoothies from the database and instantiating objects
                String[] split1 = ingredients.split(",");
                ArrayList<String> ingredientsList = new ArrayList<String>(Arrays.asList(split1));
                Smoothie smoothie = new Smoothie(id, name, price, ingredientsList);

                smoothieMenu.put(smoothie.getName(), smoothie); //dictionary to check if a smoothie is on the menu based on its name, used for search bar
                System.out.println(smoothie.getName());

            }


            String sqlStatement = "SELECT * FROM transactions;";
            ResultSet result = stmt.executeQuery(sqlStatement);
            while (result.next()) {
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
                String date = result.getString("trans_date");
                String before = Integer.toString(startMonth) + "/" + Integer.toString(startDay) + "/" + Integer.toString(startYear);
                String after = Integer.toString(endMonth) + "/" + Integer.toString(endDay) + "/" + Integer.toString(endYear);
                //System.out.println(date + " " + before + " " + after);
                Date d =  sdf2.parse(date);
                Date startDay1 = sdf2.parse(before);
                Date endDay1 = sdf2.parse(after);


                if (!d.after(endDay1) && !d.before(startDay1)) {
                    String name = result.getString("sm_name");
                    String nameIng = name.replaceAll(" ","");
                    nameIng=nameIng.toLowerCase();
                    if (nameIng.equals("thehulkstrawberry")) nameIng="hulkstrawberry";
                    System.out.println("IN RANGE "+name+" "+nameIng);
                    if (smoothieMenu.containsKey(nameIng)) {
                        ArrayList<String> ingredients = smoothieMenu.get(nameIng).getIngredients();
                        /*for (int i = 0; i < ingredients.size(); i++) {
                            System.out.print("sdf");
                            if (IngredientCount.containsKey(ingredients.get(i))) {
                                Double count = IngredientCount.get(ingredients.get(i));
                                IngredientCount.put(ingredients.get(i), count + 1);
                                System.out.println(IngredientCount.get(ingredients.get(i)));
                            } else {
                                IngredientCount.put(ingredients.get(i), 1.0);
                            }
                        }*/
                        for (int i = 0; i < ingredients.size(); i++)
                        {
                            for (int n = 0; n < ingredients.size(); n++)
                            {
                                if (i==n) continue;
                                String ingpairkey = ingredients.get(i)+", "+ingredients.get(n);
                                if (comboCount.containsKey(ingpairkey))
                                {
                                    comboCount.put(ingpairkey,(comboCount.get(ingpairkey)+1));
                                }
                                else comboCount.put(ingpairkey,1);
                            }
                        }
                    } else {
                        System.out.println("Smoothie not found");
                    }
                } else {
                    continue;
                }

            }

        } catch (Exception e){
            JOptionPane.showMessageDialog(null,"Error accessing Database.");
            System.out.println(e.getClass().getCanonicalName());
        }

        comboCount.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .forEach(k -> listModel.addElement(k.getKey() + ": " + k.getValue()));
        /*for (Map.Entry<String,Integer> mapElement : comboCount.entrySet()) {
            String name = mapElement.getKey();
            int count = mapElement.getValue();

            listModel.addElement(name+": "+count);*/

            /*if (IngredientCount.containsKey(name)) {
                if (count / 10 > IngredientCount.get(name)) {
                    listModel.addElement(name);
                }
            } else {
                listModel.addElement(name);
            }*/

        /*}*/
        System.out.println(listModel.size());
        graphs.revalidate();
        graphs.repaint();
        f.validate();
        f.repaint();
    }

    private class Clicker implements ActionListener {
        private int id;

        public Clicker(int d) {id = d - 1;}
        public Clicker() {id = -1;}

        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();

            if (s.equals("Inventory")) {
                Manager m = new Manager(f, conn);
            }
            else if (s.equals("Sales Report")) {
                SalesReport sr = new SalesReport(f, conn);
            }
            else if (s.equals("Employees")) {
                Employee ee = new Employee(f, conn);
            }
            else if (s.equals("Server")) {
                GUI start = new GUI(f, conn);
            }
            else if (s.equals("Start Date:"))
            {
                JTextField dayField = new JTextField(5);
                JTextField monthField = new JTextField(5);
                JTextField yearField = new JTextField(5);

                JPanel dateEntry = new JPanel();
                dateEntry.add(new JLabel("Day:"));
                dateEntry.add(dayField);
                dateEntry.add(Box.createHorizontalStrut(15));
                dateEntry.add(new JLabel("Month:"));
                dateEntry.add(monthField);
                dateEntry.add(Box.createHorizontalStrut(15));
                dateEntry.add(new JLabel("Year:"));
                dateEntry.add(yearField);

                int result = JOptionPane.showConfirmDialog(null, dateEntry,
                        "Enter Day, Month, Year as Integers", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    startDay = Integer.parseInt(dayField.getText());
                    startMonth = Integer.parseInt(monthField.getText());
                    startYear = Integer.parseInt(yearField.getText());
                    recText.setText(startMonth+"/"+startDay+"/"+startYear+" - "+endMonth+"/"+endDay+"/"+endYear);
                    listModel.clear();
                    loadList();
                }
            }
            else if (s.equals("End Date:"))
            {
                JTextField dayField = new JTextField(5);
                JTextField monthField = new JTextField(5);
                JTextField yearField = new JTextField(5);

                JPanel dateEntry = new JPanel();
                dateEntry.add(new JLabel("Day:"));
                dateEntry.add(dayField);
                dateEntry.add(Box.createHorizontalStrut(15));
                dateEntry.add(new JLabel("Month:"));
                dateEntry.add(monthField);
                dateEntry.add(Box.createHorizontalStrut(15));
                dateEntry.add(new JLabel("Year:"));
                dateEntry.add(yearField);

                int result = JOptionPane.showConfirmDialog(null, dateEntry,
                        "Enter Day, Month, Year as Integers", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    endDay = Integer.parseInt(dayField.getText());
                    endMonth = Integer.parseInt(monthField.getText());
                    endYear = Integer.parseInt(yearField.getText());
                    recText.setText(startMonth+"/"+startDay+"/"+startYear+" - "+endMonth+"/"+endDay+"/"+endYear);
                    listModel.clear();
                    loadList();
                }
            }
            else if (s.equals("Restock Report")) {
                RestockReport RestockReport1 = new RestockReport(f, conn);
            }
        }
    }
}