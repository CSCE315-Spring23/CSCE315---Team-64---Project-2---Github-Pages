import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// make sure all capabilities of a typical manager is possible here
// what can the manager do right now? what should they be able to do in total?

public class Manager {
    // Define private global GUI components
    private Connection conn;
    private Clicker click;
    private JFrame frame;
    private JPanel panel;
    private JPanel scroll_panel;
    private JScrollPane items;
    private JLabel recText;
	int sz = 0;

    private ArrayList<JPanel> item_panels;
    private ArrayList<ArrayList<JTextField>> item_list;
    private ArrayList<ArrayList<JLabel>> item_labels;
    private ArrayList<ArrayList<JButton>> item_buttons;

    public Manager(JFrame f, Connection c) {
        // Clear out the frame and repaint it
        conn = c;
        frame = f;
        sz=1000;
        click = new Clicker();
        frame.getContentPane().removeAll();
        fill_lists();
        make_inventory();
    }
    
    public Manager(JFrame f, Connection c, int sz) {
        // Clear out the frame and repaint it
        conn = c;
        frame = f;
        click = new Clicker();
        this.sz = sz;
        frame.getContentPane().removeAll();
        fill_lists();
        make_inventory();
    }

    private void fill_lists() {
        // Initialize data vectors
        item_panels = new ArrayList<JPanel>();
        item_labels = new ArrayList<ArrayList<JLabel>>();
        item_list = new ArrayList<ArrayList<JTextField>>();
        item_buttons = new ArrayList<ArrayList<JButton>>();

        try {
            // Make query
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM items ORDER BY item_id");

            // Get next line in query
            while (rs.next()) {
                // Initialize subvectors and its respective JPanel
                item_panels.add(new JPanel());
                item_list.add(new ArrayList<JTextField>());
                item_labels.add(new ArrayList<JLabel>());
                item_buttons.add(new ArrayList<JButton>());

                // Retrieve last components
                JPanel new_panel = item_panels.get(item_panels.size() - 1);
                ArrayList<JTextField> new_item = item_list.get(item_list.size() - 1);
                ArrayList<JLabel> new_labels = item_labels.get(item_labels.size() - 1);
                ArrayList<JButton> new_buttons = item_buttons.get(item_buttons.size() - 1);

                // Set parameters for the new JPanel
                new_panel.setSize(200, 200);
                new_panel.setBorder(BorderFactory.createLineBorder(Color.black));
                new_panel.setLayout(null);

                // Add components to subpanel
                new_item.add(new JTextField(rs.getString("item_id")));
                new_item.add(new JTextField(rs.getString("item_quantitylbs")));
                new_item.add(new JTextField(rs.getString("item_ppp")));
                new_item.get(0).setLocation(70, 10);
                new_item.get(1).setLocation(70, 60);
                new_item.get(2).setLocation(70, 110);

                new_labels.add(new JLabel("Name:   "));
                new_labels.add(new JLabel("lbs: "));
                new_labels.add(new JLabel("Price: $"));
                new_labels.get(0).setLocation(10, 10);
                new_labels.get(1).setLocation(10, 60);
                new_labels.get(2).setLocation(10, 110);

                new_buttons.add(new JButton("Update"));
                new_buttons.add(new JButton("Delete"));
                new_buttons.get(0).setLocation(10, 160);
                new_buttons.get(1).setLocation(105, 160);

                // Add components to JPanel
                for (JTextField jf : new_item) { new_panel.add(jf); jf.setSize(120, 30); }
                for (JLabel jl : new_labels)   { new_panel.add(jl); jl.setSize(50, 30); }
                for (JButton jb : new_buttons) { 
                    new_panel.add(jb);
                    jb.setSize(85, 30);
                    jb.addActionListener(new Clicker(rs.getString("item_name"), Integer.parseInt(rs.getString("item_id")))); 
                }
            }
            System.out.println(item_list.size());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Bad query");
        }
    }

    private void make_inventory() {
        // Panels
        panel = new JPanel();
        scroll_panel = new JPanel();
        items = new JScrollPane();
        items.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        items.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.setLayout(null);
        scroll_panel.setLayout(null);
        panel.add(items);
		int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50;

        items.setSize(900, height);
        items.setLocation(10, 10);

        // Inventory
        for (int i = 0; i < item_panels.size(); i++) {
            int c = i % 4;
            int r = i / 4;

            item_panels.get(i).setLocation(20 + (220 * c), 20 + (220 * r));
            scroll_panel.add(item_panels.get(i));
        }

        int max_size = 20 + 220 * (item_panels.size() / 4 + 1);
        scroll_panel.setPreferredSize(new Dimension(900, max_size));
        items.setViewportView(scroll_panel);

        // Side
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
        
        JButton restock = new JButton("Restock Report");
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


        JPanel recs = new JPanel();
        recs.setSize(270, 480);
        recs.setLocation(920, 525);
        recs.setBorder(BorderFactory.createLineBorder(Color.black));
        recs.setLayout(null);

        JButton getRecs = new JButton("Get Recommendations");
        getRecs.setSize(250, 30);
        getRecs.setLocation(10, 10);
        getRecs.addActionListener(click);
        recs.add(getRecs);

        recText = new JLabel("Recommendations will go here.");
        recText.setSize(250, 420);
        recText.setLocation(10, 50);
        recText.setBorder(BorderFactory.createLineBorder(Color.black));
        recText.setHorizontalAlignment(SwingConstants.CENTER);
        recText.setVerticalAlignment(SwingConstants.CENTER);
        recs.add(recText);

        // Frame
        frame.add(recs);
        frame.add(manPages);
        frame.add(panel);
        frame.setVisible(true);
        frame.validate();
        frame.repaint();
    }

    private void delete_item(String name) {
        // Update in database
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "DELETE FROM items WHERE item_name = '" + name + "';";
            stmt.executeQuery(sqlStatement); 
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(null,"Bad query");
            //System.out.println(ex);
        }
    }

    // private void update_item(String name, String new_name, String new_quantity, String new_ppp) {
    //     // Update in database
    //     try {
    //         Statement stmt = conn.createStatement();
    //         String sqlStatement = "UPDATE items SET item_ppp = " + new_ppp + " WHERE item_name = '" + name + "';";
    //         ResultSet result = stmt.executeQuery(sqlStatement); 
    //     } catch (Exception ex) {}
    //     try {
    //         Statement stmt = conn.createStatement();
    //         String sqlStatement1 = "UPDATE items SET item_quantitylbs = " + new_quantity + " WHERE item_name = '" + name + "';";
    //         ResultSet result1 = stmt.executeQuery(sqlStatement1);
    //     } catch (Exception ex) {}
    //     try {
    //         Statement stmt = conn.createStatement();
    //         String sqlStatement2 = "UPDATE items SET item_name = '" + new_name + "' WHERE item_name = '" + name + "';";
    //         ResultSet result2 = stmt.executeQuery(sqlStatement2);
    //     } catch (Exception ex) {}
    // }

    // // function that grabs the max id number in the items table
    // private int maxItemsId() { 
    //     // Statement stmt = null;
    //     // try {
    //     //     stmt = conn.createStatement();
    //     // } catch (SQLException e1) {
    //     //     e1.printStackTrace();
    //     // }

    //     // Statement stmt = conn.createStatement();
    //     // // get max id currently in database, then +1 it
    //     // ResultSet rs2 = stmt.executeQuery("SELECT MAX(item_id) FROM items;");
    //     // rs2.next();
    //     // int maxid = rs2.getInt("max") + 1;
    //     // return maxid;
    // }
    
    /**
    Updates an item's information in the database.
    @param name the current name of the item
    @param new_name the new name of the item
    @param new_quantity the new quantity of the item
    @param new_ppp the new price per pound of the item
    */

    private void update_item(int id, String name, String new_name, String new_quantity, String new_ppp) {
        // Update in database
        // sql command if u want to manually try: UPDATE items SET item_ppp = 999.0 WHERE item_id = 73
        //                                        UPDATE items SET item_quantitylbs = 999.0 WHERE item_id = 73
        //                                        UPDATE items SET item_name = vinaysucks WHERE item_id = 73

        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = "UPDATE items SET item_ppp = " + new_ppp + " WHERE item_id = " + id + ";";
            ResultSet result = stmt.executeQuery(sqlStatement);
        } catch (Exception ex) {}
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement1 = "UPDATE items SET item_quantitylbs = " + new_quantity + " WHERE item_id = " + id + ";";
            ResultSet result1 = stmt.executeQuery(sqlStatement1);
        } catch (Exception ex) {}
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement2 = "UPDATE items SET item_name = '" + new_name + "' WHERE item_id = " + id + ";";
            ResultSet result2 = stmt.executeQuery(sqlStatement2);
        } catch (Exception ex) {}

        // try {
        //     Statement stmt = conn.createStatement();
        //     String sqlStatement = "UPDATE items SET item_ppp = " + new_ppp + " WHERE item_name = '" + name + "';";
        //     ResultSet result = stmt.executeQuery(sqlStatement);
        // } catch (Exception ex) {}
        // try {
        //     Statement stmt = conn.createStatement();
        //     String sqlStatement1 = "UPDATE items SET item_quantitylbs = " + new_quantity + " WHERE item_name = '" + name + "';";
        //     ResultSet result1 = stmt.executeQuery(sqlStatement1);
        // } catch (Exception ex) {}
        // try {
        //     Statement stmt = conn.createStatement();
        //     String sqlStatement2 = "UPDATE items SET item_name = '" + new_name + "' WHERE item_name = '" + name + "';";
        //     ResultSet result2 = stmt.executeQuery(sqlStatement2);
        // } catch (Exception ex) {}
    }

    private void get_recs() {
        String s = "";

        // Iterate through the item list and add string if count < 50
        for (ArrayList<JTextField> item : item_list) {
            double lbs = Double.parseDouble(item.get(1).getText());

            if (lbs < 50.0) {
                s += "Low on " + item.get(0).getText() + "\n";
            }
        }

        recText.setText("<html>" + s.replaceAll("\n", "<br/>") + "<html>");
    }

    private class Clicker implements ActionListener {
        private String name;
        private int id;

        // Define constructor so that clickers are unique if needed
        public Clicker(String n, int i) {
            name = n;
            id = i;
        }
        public Clicker() {name = ""; id = -1;}

        public void actionPerformed(ActionEvent e) {
            String s = e.getActionCommand();

            // Handle case when delete/update buttons are clicked, getting their ID
            if (!name.equals("")) {
            	System.out.println(item_list.size());
                ArrayList<JTextField> values = item_list.get(id-1);
                String new_name = values.get(0).getText();
                String quantity = values.get(1).getText();
                String ppp = values.get(2).getText();

                if (s.equals("Delete")) {
                    delete_item(name);
                }
                else if (s.equals("Update")) {
                    update_item(id, name, new_name, quantity, ppp);
                    name = new_name;
                }
                Manager m = new Manager(frame, conn);
            }
            // Refresh page
            else if (s.equals("Inventory")) {
                Manager m = new Manager(frame, conn);
            }
            // Swap to sales page
            else if (s.equals("Sales Report")) {
                SalesReport sr = new SalesReport(frame, conn);
            }
            // Swap to employees page
            else if (s.equals("Employees")) {
                // Go to Employees page
                Employee employee = new Employee(frame, conn);
            }
            // Swap to server page
            else if (s.equals("Server")) {
	            GUI start = new GUI(frame, conn);
            }
            // Reset recommendations panel
            else if (s.equals("Get Recommendations")) {
                get_recs();
            } 
            else if (s.equals("New Item")) {
            	Statement stmt = null;
				try {
					stmt = conn.createStatement();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					// System.out.println("INSERT INTO items " + "VALUES (" + 72 + ", " + 5 + ", '" + "new item" + "', " + 30.0 + ");");

                    // get max id currently in database, then +1 it
                    ResultSet rs2 = stmt.executeQuery("SELECT MAX(item_id) FROM items;");
                    rs2.next();
                    int maxid = rs2.getInt("max") + 1;
                    // System.out.println(maxid);

                    // int maxid = maxItemsId();
					stmt.executeUpdate("INSERT INTO items " + "VALUES (" + maxid + ", " + 0 + ", '" + "new item" + "', " + 50.0 + ");");
                    Manager m = new Manager(frame, conn, sz + 1);
          
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            
            // for some reason this isn't working?
            } else if (s.equals("Excess Report")) {
            	ExcessReport ExcessReport1 = new ExcessReport(frame, conn);
            } else if (s.equals("Restock Report")) {
            	RestockReport RestockReport1 = new RestockReport(frame, conn);
            } else if (s.equals("Sold Together Items")) {
                SoldTogether stogether = new SoldTogether(frame, conn);
            }
        }
    }
}