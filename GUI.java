import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;
import java.util.*;

/*
  TODO:
  1) Change credentials for your own team's database
  2) Change SQL command to a relevant query that retrieves a small amount of data
  3) Create a JTextArea object using the queried data
  4) Add the new object to the JPanel p
*/

public class GUI {
    private JFrame f;
    private Connection conn;
    private DefaultListModel<String> listModel = new DefaultListModel<String>();
    private JList<String> displayList = new JList<String>(listModel);
    public HashMap<String, ArrayList<Smoothie>> smoothieDict;
    public HashMap<String, Smoothie> smoothieMenu;
    private double checkoutSum = 0.00;
	private int newidrand = 1; // will inc with every new smoothie, don't keep this in final submission
    
    public GUI(JFrame f, Connection c) {
    	this.conn = c;
    	this.f = f;
    	this.f.getContentPane().removeAll();
    	this.intializeGUI();
    }

	public void intializeGUI()
	{
		  String[] main_ingredients = new String[] {"strawberries", "blueberries", "mangoes", "bananas", "cocoa", "kale", ""}; 
		  smoothieDict = new HashMap<String, ArrayList<Smoothie>>(); //Dictionary that maps main_ingredients to smoothies with that ingredient
		  smoothieMenu = new HashMap<String, Smoothie>(); //Dictionary that maps smoothie name to smoothie object		  
		  ArrayList<Smoothie> checkout = new ArrayList<Smoothie>(); //list that stores the current smoothies in the checkout bin
		  
		  for (int i = 0; i < main_ingredients.length; i++) {
			  smoothieDict.put(main_ingredients[i], new ArrayList<Smoothie>()); //initializing dictionary
		  }	
		  
		  /**
		   * Reading the smoothies in from the database and instantiating smoothie instances. Dictionaries above are filled with this data.
		   */
		 
		  try {
		    Statement stmt = conn.createStatement();
		    String sqlStatement = "SELECT * FROM smoothies;";
		    ResultSet result = stmt.executeQuery(sqlStatement); 
		    while (result.next()) {
		       String name = result.getString("sm_name");
		       int id = result.getInt("sm_id");
		       double price = result.getDouble("sm_price");
		       price = BigDecimal.valueOf(price)
					    .setScale(2, RoundingMode.HALF_UP)
					    .doubleValue();
		       String ingredients = result.getString("sm_ingredients"); //Retrieving all the smoothies from the database and instantiating objects
		       String[] split1 = ingredients.split(",");
		       ArrayList<String> ingredientsList = new ArrayList<String>(Arrays.asList(split1));
		       Smoothie smoothie = new Smoothie(id, name, price, ingredientsList);
		       
		       smoothieMenu.put(smoothie.getName(), smoothie); //dictionary to check if a smoothie is on the menu based on its name, used for search bar
		       
		       for (int i = 0; i < ingredientsList.size(); i++) {
				   if (smoothieDict.containsKey(ingredientsList.get(i))) {
					   smoothieDict.get(ingredientsList.get(i)).add(smoothie); //Categorizing smoothies by key ingredients
				   }
		       }        
		    }
		     
		  } catch (Exception e){
		    JOptionPane.showMessageDialog(null,"Error accessing Database.");
		  }
		  
		  // create a new frame
		  f.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		  int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50; //Sets frame to fullscreen
		  int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 50;
		 
		  JPanel p = new JPanel();
		  JPanel p1 = new JPanel();
		  JTextField searchBar = new JTextField("");
		  searchBar.setBackground(Color.LIGHT_GRAY); 
		  searchBar.setFont(new Font("Sans Serif", Font.BOLD, 14)); 
		  searchBar.setLocation(0,  0);
		  searchBar.setSize(new Dimension(500, 40)); //initializing search bar and panels : p1 is the scrollable panel and p is the main panel
		  
		  JLabel errorMessage = new JLabel("");
		  errorMessage.setLocation(800, 0);
		  errorMessage.setSize(new Dimension(300, 40));
		  
		  JButton clear = new JButton("Clear");
		  JButton buy = new JButton("Checkout" + ": $" + checkoutSum);
		  clear.setFont(new Font("Arial", Font.BOLD, 12)); //initializing clear and checkout buttons for shopping cart
		  clear.setBounds((int)(width-width * .2), height-200, (int)(width * .1), 200); 
		  buy.setFont(new Font("Arial", Font.BOLD, 12));
		  buy.setBounds((int)(width-width * .1), height-200, (int)(width * .1), 200);
		  
		  //ActionListener for adding a smoothie to the checkout bin through the search bar
		  
		  ActionListener addAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String smoothieName = searchBar.getText();
					if (smoothieMenu.containsKey(smoothieName)) {
						Smoothie smoothie1 = smoothieMenu.get(smoothieName);
						checkout.add(smoothie1);
						checkoutSum += smoothie1.getPrice();
			            buy.setText("Checkout" + ": $" + checkoutSum);
						listModel.addElement(smoothie1.toString() + "  $" + smoothie1.getPrice()); //Search bar submit button
						errorMessage.setForeground(Color.green);
						errorMessage.setText("Success!");
						p.revalidate();
						p.repaint();
					} else {
						// errorMessage.setForeground(Color.red);
						// errorMessage.setText("There is no Smoothie with that name!");
						// instead, we want to make a new smoothie and then put it in the cart
						// sql command to add a new smoothie type:

						// new_sm_id is the highest id + 1
						// SELECT MAX(sm_id) AS highest
						// FROM smoothies
						// try {
							// Statement stmt = conn.createStatement();
							// String sqlStatement = "SELECT MAX(sm_id) FROM smoothies;";
							// ResultSet result = stmt.executeQuery(sqlStatement); 
							// String highidx = result.getString("sm_id");
						// 	System.out.println(highidx);
						// 	// while(result.next()) {
						// 	// 	String highidx = result.getString("sm_id");
						// 	// 	System.out.println(highidx);
						// 	// }
						// } catch (Exception ex) { System.out.println(ex); }

						// INSERT INTO smoothies (sm_id, sm_name, sm_price, sm_ingredients)
						// VALUES (new_sm_id, smoothieName, randprice, "cocoa")
						int newidrandbruh = 1000000 + newidrand;
						newidrand++; 
						String randingre = "cocoa";
						ArrayList<String> arrrr = new ArrayList<String>();
						arrrr.add(randingre);
						Smoothie randSmooth = new Smoothie(newidrandbruh, smoothieName, (double)newidrandbruh, arrrr);
						smoothieMenu.put(smoothieName, randSmooth); // simply put into hashmap so next time it is searched, it can find it
						try {
							Statement stmt = conn.createStatement();
							String sqlStatement = "INSERT INTO smoothies " + "VALUES (" + newidrandbruh + ", '" + smoothieName + "', " + (double)newidrandbruh + ", '" + randingre + "');";
							ResultSet result = stmt.executeQuery(sqlStatement); 
						} catch (Exception exx) { System.out.println(exx);} 
						
					}
				}
		  };
		  
		  JButton searchButton = new JButton("Add Smoothie");
		  searchButton.addActionListener(addAction); //initializing button for adding a smoothie through the search bar
		  searchButton.setLocation(500, 0);
		  searchButton.setSize(250, 40);
		  	  
		  p.add(searchBar);
		  p.add(searchButton);
		  p.add(errorMessage);
		  
		  JScrollPane scroll = new JScrollPane();
		  scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		  scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //scrolling settings
		  p.setLayout(null);
		  scroll.setLocation(0, 40);
		  scroll.setSize((int)(width-width * .2), height-60);
		  p.add(scroll);
		 
		  p.add(displayList); //displayList : list that displays names of all the smoothies in the checkout bin
		  displayList.setLayout(null);
		  displayList.setVisible(true);
		  displayList.setBounds((int)(width-width * .2), 0, (int)(width-width * .2), height-200);   
		  
		  /*
		   * Looping through the main_ingredients array and retrieving the smoothies with that ingredient. Displays buttons for those smoothies
		   * in the row of the main ingredient, which can be used to add that specific smoothie to the checkout bin. 
		   * 
		   */
		  
		  for (int i = 0; i < main_ingredients.length; i++) {
			  JLabel label = new JLabel(main_ingredients[i].toUpperCase());
			  label.setBounds(40, 400 * i, 250, 400);
			  label.setFont(new Font("Sans Serif", Font.BOLD, 20));
			  ArrayList<Smoothie> smoothieList = smoothieDict.get(main_ingredients[i]);
			  for (int j = 0; j < smoothieList.size(); j++) {
				  Smoothie smoothie1 = smoothieList.get(j);
				  ImageIcon buttonIMG = new ImageIcon("./imgs/" + smoothie1.getName() + ".png");
				  String buttonText = smoothie1.toString() + "\n$" + smoothie1.getPrice();
				  JButton button = new JButton("<html>" + buttonText.replaceAll("\\n", "<br>") + "</html>"); //Adding the smoothies to the scrollable GUI
			      button.setVerticalTextPosition(SwingConstants.BOTTOM);
			      button.setHorizontalTextPosition(SwingConstants.CENTER);
				  button.setFont(new Font("Sans Serif", Font.BOLD, 12));
				  button.setBounds(250 + 250 * j, 400 * i, 250, 400);
				  ActionListener buttonListener = new ActionListener() { //ActionListener that adds smoothie to the checkout bin
		            @Override				
		            public void actionPerformed(ActionEvent e) {
		              checkout.add(smoothie1);
		              checkoutSum += smoothie1.getPrice();
		              buy.setText("Checkout" + ": $" + checkoutSum);
		              listModel.addElement(smoothie1.toString() + "  $" + smoothie1.getPrice());
		              p.revalidate();
		              p.repaint();
		                      
		            }
				  };
				  button.setIcon(buttonIMG);
				  button.addActionListener(buttonListener);
				  p1.add(button);
				  
			  }
			  p1.add(label);
		  }
		  p1.setPreferredSize(new Dimension(10000, (main_ingredients.length-1) * 400));
		  
		  scroll.setViewportView(p1);
		  p1.setLayout(null);	
		 
		  /*
		   * ActionListener that clears the checkout bin
		   */
		  ActionListener clearAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					listModel.removeAllElements();	
					checkout.clear();
					checkoutSum = 0;
					buy.setText("Checkout" + ": $" + checkoutSum);
				}
		  };
		  
		  /*
		   * ActionListener that checkouts the smoothies and updates the database inventory and transactions. 
		   */
		  
		  ActionListener buyAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					listModel.removeAllElements();	
				    JOptionPane.showMessageDialog(null,"Purchase of" + " $" + checkoutSum + " made.");
					checkoutSum = 0;
					buy.setText("Checkout" + ": $" + checkoutSum);
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");  
					DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("EEEE");  

					Calendar c  = Calendar.getInstance();
				    LocalDateTime now = LocalDateTime.now();  
				    
					String trans_date = dtf.format(now); 
					String trans_dayofweek = dtf1.format(now);
					
					
					for (int i = 0; i < checkout.size(); i++) {
						Smoothie smoothie1 = checkout.get(i);
						String sm_name = smoothie1.getName();
						Double trans_price = smoothie1.getPrice();
						String trans_size = "small";
						Double trans_cost = 0.0;
						Statement stmt = null;
						try {
							stmt = conn.createStatement();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							ResultSet rs2 = stmt.executeQuery("SELECT MAX(trans_id) FROM transactions;");
		                    rs2.next();
		                    int maxid = rs2.getInt("max") + 1;
							stmt.executeUpdate("INSERT INTO transactions " + "VALUES (" + maxid + 1 + ", '" + trans_date + "', '" + trans_dayofweek + "', '" + trans_size + "', " + trans_price + ", " + trans_cost + ")");
							
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						for (int j = 0; j < smoothie1.getIngredients().size(); j++) {
							
							System.out.println(smoothie1.getIngredients().get(j));
					        String sqlStatement1 = "SELECT * FROM items;";
					        ResultSet result;
					        double count = 0;
							try {
								result = stmt.executeQuery(sqlStatement1);
								while (result.next()) {
								     String name = result.getString("item_name");
								     if (name.equals(smoothie1.getIngredients().get(j))) {
								    	 count = result.getDouble("item_quantitylbs") - 1.0;
								     }
								}
							} catch (SQLException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							} 
					        
						    String sqlStatement = "UPDATE items SET item_quantitylbs = " + Double.toString(count) + " WHERE item_name = '" + smoothie1.getIngredients().get(j)+"';";
						    try {
								stmt.execute(sqlStatement);
								
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					}
					checkout.clear();
				}

		  };
		  
		  clear.addActionListener(clearAction);
		  buy.addActionListener(buyAction);
		  p.add(buy);
		  p.add(clear);
		  
		  f.add(p);
		
		  // button to manager
		  ActionListener pageAction = new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		      Manager m = new Manager(f, conn);
		    }
		  };
		
		  JButton gotoManager = new JButton("M");
		  gotoManager.setSize(50, 50);
		  gotoManager.setLocation(10, 40);
		  gotoManager.addActionListener(pageAction); //switch to manager side UI
		  p1.add(gotoManager);
		  p1.revalidate();
		  p1.repaint();

		  f.setVisible(true);	
		  f.validate();
          f.repaint();
	}
}
