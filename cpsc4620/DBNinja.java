//Code by Sarvesh Dube ad Atharva Ranade
package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "Pickup";
	public final static String delivery = "Delivery";
	public final static String dine_in = "dine-in";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";





	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 */
		//o.getOrderType();
		Integer customer_ID=o.getCustID();
		String[] generated_ID= {"ID"};
		String intstm= " INSERT INTO pizzaorder "+ " (PizzaorderCost, PizzaorderType, PizzaorderTime, PizzaorderPrice, PizzaorderStatus, PizzaorderCustomerID)"+
					  " VALUES(0.00,'" + o.getOrderType()+"','"+ o.getDate()+"',0.00,"+o.getIsComplete()+","+ customer_ID+")";
		
		PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);
		int result = ppdstm.executeUpdate();
		if (result > 0) {
			try {
				ResultSet rset = ppdstm.getGeneratedKeys();
				if (rset.next()) {
					o.setOrderID(rset.getInt(1));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	
	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts to that bridge table and 
		 * instance of topping usage to that bridge table if you have't accounted
		 * for that somewhere else.
		 */
	
		try {
			String[] generatedId = { "ID" };
//			
			String insertStatement = "insert into pizza(PizzaPizzaorderID,PizzaBasepriceSize,PizzaBasepriceCrustType,PizzaState,PizzaCost,PizzaPrice)\n"
			        + "values\n" + "(" + p.getOrderID() + ",'" + p.getSize() + "' , '" + p.getCrustType()
					+ "' , 'In-Progress' , " + p.getBusPrice() + " , " + p.getCustPrice() + ")";
			
			PreparedStatement ppdstm = conn.prepareStatement(insertStatement, generatedId);
			Statement stm = conn.createStatement();
			
			int result = ppdstm.executeUpdate();
			
			if (result > 0) {
				ResultSet rset = ppdstm.getGeneratedKeys();
				if (rset.next()) {
					p.setPizzaID(rset.getInt(1));
				}
			}

		}catch (Exception e) {
			e.printStackTrace();
			}conn.close();
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static int getMaxPizzaID() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * A function I needed because I forgot to make my pizzas auto increment in my DB.
		 * It goes and fetches the largest PizzaID in the pizza table.
		 * You wont need to implement this function if you didn't forget to do that
		 */
		int maxOrderID=-1;
		try{String query = "SELECT * FROM pizza where PizzaID = (SELECT MAX(PizzaID) from pizza)";
		PreparedStatement ppdstm = conn.prepareStatement(query);
		ResultSet rset = ppdstm.executeQuery();
		
		while(rset.next())
			maxOrderID = Integer.parseInt(rset.getString("PizzaID"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return maxOrderID+1;
		
	}
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this function will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This function should 2 two things.
		 * We need to update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * and we need to add that instance of topping usage to the pizza-topping bridge if we haven't done that elsewhere
		 * Ideally, you should't let toppings go negative. If someone tries to use toppings that you don't have, just print
		 * that you've run out of that topping.
		 */
		
		try {
			connect_to_db();
			double size_amount = 0.0;
			if (p.getSize() == "Small") {
				size_amount = t.getPerAMT();
			} 
			else if (p.getSize() == "Medium") 
			{
				size_amount = t.getMedAMT();
			} 
			else if (p.getSize() == "Large") 
			{
				size_amount = t.getLgAMT();
			} 
			else if (p.getSize() == "XLarge") 
			{
				size_amount = t.getXLAMT();
			}
			String updstm=null;
			//System.out.println(t.getCurINVT());
			PreparedStatement ppdstm;
			if (isDoubled==true) {
		   //System.out.println(size_amount);
			
				if(t.getCurINVT()-size_amount>0) {
				updstm = "UPDATE topping SET ToppingCurrInvLevel = ToppingCurrInvLevel - " + 2*size_amount + " WHERE ToppingID= " + t.getTopID();
				ppdstm = conn.prepareStatement(updstm);
				ppdstm.executeUpdate();

				}
				else {
					System.out.println("Out of Inventory");
				}
			}
			else{
				if(t.getCurINVT()-size_amount>0) {
					updstm = "UPDATE topping SET ToppingCurrInvLevel = ToppingCurrInvLevel - " + size_amount + " WHERE ToppingID= " + t.getTopID();
					ppdstm = conn.prepareStatement(updstm);
					ppdstm.executeUpdate();

				}
				else {
				System.out.println("Out of Inventory");
			}
			
			
			



		} }catch (Exception e) {
			e.printStackTrace();
		}
		conn.close();
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Helper function I used to update the pizza-discount bridge table. 
		 * You might use this, you might not depending on where / how to want to update
		 * this table
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	


	
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This should add a customer to the database
		 */
		String f_name = c.getFName();
		String l_name = c.getLName();
		String phone_no = c.getPhone();
		Integer customer_Id = null;
		String[] generatedId = { "ID" };
		String intstm = "INSERT INTO customer(CustomerFName,CustomerLName,CustomerPhoneNumber) VALUES (?,?,?)";
		PreparedStatement ppdstm = conn.prepareStatement(intstm, generatedId);
		ppdstm.setString(1, f_name);
		ppdstm.setString(2, l_name);
		ppdstm.setString(3, phone_no);
		
		int result = ppdstm.executeUpdate();
		if (result > 0) {
			try {
				ResultSet rs = ppdstm.getGeneratedKeys();
				if (rs.next()) {
					customer_Id = rs.getInt(1);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
		}conn.close();
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void CompleteOrder(int o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to mark an order as complete in the DB. You may have a boolean field
		 * for this, or maybe a completed time timestamp. However you have it.
		 */
		 try {
			 String updstm = "UPDATE pizzaorder SET PizzaorderStatus = "+1+" WHERE PizzaorderID = " + o + " ;";
			 System.out.println(o);
			
			PreparedStatement ppdstm = conn.prepareStatement(updstm);
			
			ppdstm.executeUpdate();
			String status = "Complete";
			String updstmpizza = "UPDATE pizza SET PizzaState = '"+status+"' WHERE PizzaPizzaorderID = " + o + " ;";
		
			PreparedStatement  ppdstmpizza= conn.prepareStatement(updstmpizza);
			
			ppdstmpizza.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}conn.close();


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	
	
	public static void AddToInventory(int t, int toAdd) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Adds toAdd amount of topping to topping t.
		 */

		int topID= t;
		int add=toAdd;
		//System.out.println(t);
		//System.out.println(toAdd);
		
		String updateStatement = "UPDATE topping set ToppingCurrInvLevel = ToppingCurrInvLevel + "+add+" where ToppingID ="+topID+";";
		Statement stm=conn.createStatement();
		stm.executeUpdate(updateStatement);
		
	
		
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	

	public static void printInventory() throws SQLException, IOException {
		ArrayList<Topping> toppings = new ArrayList<Topping>();
		connect_to_db();
		
		/*
		 * I used this function to PRINT (not return) the inventory list.
		 * When you print the inventory (either here or somewhere else)
		 * be sure that you print it in a way that is readable.
		 * 
		 * 
		 * 
		 * The topping list should also print in alphabetical order
		 */
		try (Statement stm = conn.createStatement();
		        ResultSet rset = stm.executeQuery("SELECT * FROM topping ORDER BY ToppingName ;")) {
			
			while (rset.next()) {
				int topping_Id = rset.getInt("ToppingID");
				String topping_Name = rset.getString("ToppingName");
				Double topping_cost = rset.getDouble("ToppingCost");
				Double topping_price = rset.getDouble("ToppingPrice");
				Double topping_small = rset.getDouble("ToppingSmall");
				Double topping_medium = rset.getDouble("ToppingMedium");
				Double topping_large = rset.getDouble("ToppingLarge");
				Double topping_xlarge = rset.getDouble("ToppingXLarge");
				int minimum_inventorylevel = rset.getInt("ToppingMinInvLevel");
				int current_inventorylevel = rset.getInt("ToppingCurrInvLevel");
				
				toppings.add(new Topping(topping_Id, topping_Name, topping_small,topping_medium, topping_large,topping_xlarge,topping_cost,topping_price, minimum_inventorylevel, current_inventorylevel));
				
			}
			for(Topping topping:toppings)
			{
				System.out.printf("| %-15s | %-30s | %-17s|%n","Topping Id:"+topping.getTopID(),"Topping Name:"+topping.getTopName(),"Topping Inventory:"+topping.getCurINVT());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		conn.close();
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION		
	}
	
	
	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		ArrayList<Topping> toppings = new ArrayList<Topping>();
		connect_to_db();
		/*
		 * This function actually returns the toppings. The toppings
		 * should be returned in alphabetical order if you don't
		 * plan on using a printInventory function
		 */
		
		try (Statement stm = conn.createStatement();
		        ResultSet rset = stm.executeQuery("SELECT * FROM topping ORDER BY ToppingName ;")) {
			
			while (rset.next()) {
				int topping_Id = rset.getInt("ToppingID");
				String topping_Name = rset.getString("ToppingName");
				Double topping_cost = rset.getDouble("ToppingCost");
				Double topping_price = rset.getDouble("ToppingPrice");
				Double topping_small = rset.getDouble("ToppingSmall");
				Double topping_medium = rset.getDouble("ToppingMedium");
				Double topping_large = rset.getDouble("ToppingLarge");
				Double topping_xlarge = rset.getDouble("ToppingXLarge");
				int minimum_inventorylevel = rset.getInt("ToppingMinInvLevel");
				int current_inventorylevel = rset.getInt("ToppingCurrInvLevel");
				
				toppings.add(new Topping(topping_Id, topping_Name, topping_small,topping_medium, topping_large,topping_xlarge,topping_cost,topping_price, minimum_inventorylevel, current_inventorylevel));
				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}conn.close();
		return toppings;

		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
		return getCurrentOrders(null, null);
	}
	
	public static ArrayList<Order> getCurrentOrders(String date) throws SQLException, IOException{
		return getCurrentOrders(date, null);
		
	}
	
	public static ArrayList<Order> getCurrentOrders(int status)throws SQLException, IOException {
		return getCurrentOrders(null, status);}
		
	public static ArrayList<Order> getCurrentOrders(String date, Integer status) throws SQLException, IOException {
		ArrayList<Order> orders = new ArrayList<Order>();
		try {
		connect_to_db();
		
		/*
		 * This function should return an arraylist of all of the orders.
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Also, like toppings, whenever we print out the orders using menu function 4 and 5
		 * these orders should print in order from newest to oldest.
		 */

		
		String selectQ = "SELECT * FROM pizzaorder ";
		if(date!=null)
		{
			selectQ += " WHERE (PizzaorderTime >= '" + date + " 00:00:00') ";
		}
		else if (status != null) {
			selectQ += " WHERE PizzaorderStatus = " + status ;
		}
		selectQ += " ORDER BY PizzaorderTime DESC;";
		
		Statement stm= conn.createStatement();
		
		ResultSet rset = stm.executeQuery(selectQ);
		while (rset.next()) {
			int order_Id = rset.getInt("PizzaorderID");
			String order_Type = rset.getString("PizzaorderType");
			String order_Time = rset.getString("PizzaorderTime");
			Double order_Cost = rset.getDouble("PizzaorderCost");
			Double order_Price = rset.getDouble("PizzaorderPrice");
			int customer_Id = rset.getInt("PizzaorderCustomerID");
			int order_status = rset.getInt("PizzaorderStatus");
			
			
			orders.add(
			    new Order(order_Id, customer_Id, order_Type, order_Time, order_Cost, order_Price,order_status ));
			
		}
		}
	catch (Exception e) {
		e.printStackTrace();
	}
	finally {
		try {
			conn.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}conn.close();
	return orders;
}
		


		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	
	public static ArrayList<Order> sortOrders(ArrayList<Order> list)
	{
		/*
		 * This was a function that I used to sort my arraylist based on date.
		 * You may or may not need this function depending on how you fetch
		 * your orders from the DB in the getCurrentOrders function.
		 */
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
		
	}
	
	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		//Helper function I used to help sort my dates. You likely wont need these
		
		
		
		
		
		
		
		
		return false;
	}
	
	
	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}



	
	
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		
		
		// add code to get the base price (for the customer) for that size and crust pizza Depending on how
		// you store size & crust in your database, you may have to do a conversion
		//String Size = size;
		//String crust_type= crust;
		
		//System.out.println(Size+crust_type);
		try {
			Statement stm = conn.createStatement();
			String query = "SELECT * FROM baseprice WHERE BasepriceCrustType ='" + crust +"' AND BasepriceSize ='"+ size+ "';";
			ResultSet rset = stm.executeQuery(query);
			while (rset.next()) {
				bp = rset.getDouble("BasepricePrice");
			}
		}catch(Exception e){
			e.printStackTrace();
		}conn.close();
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}
public static Topping getToppingFromID(int toppingID) throws SQLException, IOException{
	connect_to_db();
	Topping t = null;
	try {
		
		Statement stm = conn.createStatement();
		int topId = toppingID;
		String query = "SELECT * FROM topping WHERE ToppingID ='"+toppingID+"';";
		ResultSet rset = stm.executeQuery(query);
		//while(rset.next()) {
		rset.next();
			String topping_name = rset.getString("ToppingName");
			Double topping_cost = rset.getDouble("ToppingCost");
			Double topping_price = rset.getDouble("ToppingPrice");
			Double topping_small = rset.getDouble("ToppingSmall");
			Double topping_medium = rset.getDouble("ToppingMedium");
			Double topping_Large = rset.getDouble("ToppingLarge");
			Double topping_Xlarge = rset.getDouble("ToppingXLarge");
			int topping_mininvlevel = rset.getInt("ToppingMinInvLevel");
			int topping_currinvlevel = rset.getInt("ToppingCurrInvLevel");
			t = new Topping(topId,topping_name,topping_small,topping_medium,topping_Large,topping_Xlarge,topping_price,topping_cost,topping_mininvlevel,topping_currinvlevel);
		//}
		
		
	}catch(Exception e){
		e.printStackTrace();
	}conn.close();
	return t;
}
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		
		 /**This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select CustomerFName, CustomerLName From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			ret = rset.getString(1) + " " + rset.getString(2);
		}
		conn.close();
		return ret;
	}
	
	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion
		try {
			Statement stm = conn.createStatement();
			String query = "SELECT * FROM baseprice WHERE BasepriceCrustType ='" + crust +"' AND BasepriceSize ='"+ size+ "';";
			ResultSet rset = stm.executeQuery(query);
			while (rset.next()) {
				bp = rset.getDouble("BasepriceCost");
			}
		}catch(Exception e){
			e.printStackTrace();
		}conn.close();
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
	}

	
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		//returns a list of all the discounts.
		try (Statement stm = conn.createStatement();
		        ResultSet rset = stm.executeQuery("SELECT * FROM discount;")) {
			
			System.out.println("Getting Discount list:");
			while (rset.next()) {
				Integer discs_Id = rset.getInt("DiscountID");
				String discs_Name = rset.getString("DiscountName");
				Boolean discs_Type = rset.getBoolean("DiscountType");// 1->percent 0-> dollars off
				Double discount_Amount = rset.getDouble("DiscountAmount");
				discs.add(new Discount(discs_Id, discs_Name, discount_Amount,discs_Type));
				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}conn.close();
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return discs;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/
		
		
		String query = "Select * From customer ;";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		
		while(rset.next())
		{
			int custID = rset.getInt("CustomerID");
			String Fname=rset.getString("CustomerFname");
			String Lname=rset.getString("CustomerLname");
			String Phone=rset.getString("CustomerPhoneNumber");
			custs.add(new Customer(custID,Fname,Lname,Phone));
		}
		
		
		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
				return custs;
			}

		
		
		
		
	
	public static int getNextOrderID() throws SQLException, IOException
	{
		/*
		 * A helper function I had to use because I forgot to make
		 * my OrderID auto increment...You can remove it if you
		 * did not forget to auto increment your orderID.
		 */
		connect_to_db();
		String query = "SELECT * FROM pizzaorder where PizzaorderID = (SELECT MAX(PizzaorderID) from pizzaorder)";
		PreparedStatement ppdstm = conn.prepareStatement(query);
		ResultSet rset = ppdstm.executeQuery();
		int max_OrderID=-1;
		while(rset.next())
			max_OrderID = Integer.parseInt(rset.getString("PizzaorderID"));

		conn.close();
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		
		return max_OrderID+1;
		
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		try {
			Statement stm = conn.createStatement();
			String query = "SELECT  * FROM ToppingPopularity";
			ResultSet rset = stm.executeQuery(query);
			System.out.printf("*************************************\n");
			System.out.println("	  Topping Popularity\n");
			System.out.printf("*************************************\n");
			System.out.printf("%-18s | %-5s |%n", "Topping", "ToppingCount");
			System.out.printf("-------------------------------------\n");
			while (rset.next()) {
				
				int topping_count = rset.getInt("Toppingcount");
				String topping_name = rset.getString("ToppingName");
				System.out.printf("%-18s | %-5s |%n", topping_name, topping_count);
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}conn.close();
	}
		
		/*
		 * Prints the ToppingPopularity view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print (other than that it should
		 * be in alphabetical order by name), just make sure it's readable.
		 */


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION


	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		try {
			Statement stm = conn.createStatement();
			
			String query = "SELECT * FROM ProfitByPizza";
			
			ResultSet rset = stm.executeQuery(query);
			System.out.printf("******************************************************\n");
			System.out.println("		  Profit By Pizza Report\n");
			System.out.printf("******************************************************\n");
			System.out.printf("%-10s | %-12s | %-10s| %-20s%n", "PizzaSize", "PizzaCrust", "Profit", "LastOrderDate");
			System.out.printf("------------------------------------------------------\n");
			while (rset.next()) {
				
				String pizza_size = rset.getString("PizzaSize");
				String pizza_crust = rset.getString("PizzaCrust");
				String last_Order_Date = rset.getString("LastOrderDate");
				Double profit = rset.getDouble("Profit");
				System.out.printf("%-10s | %-12s | %-10s| %-20s%n", pizza_size, pizza_crust, profit, last_Order_Date);
				
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}conn.close();
		
		/*
		 * Prints the ProfitByPizza view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		try {
			Statement stm = conn.createStatement();
			
			String query = "SELECT * FROM ProfitByOrderType";
			ResultSet rset = stm.executeQuery(query);
			System.out.printf("*****************************************************************************\n");
			System.out.println("			    Profit By Order\n");
			System.out.printf("*****************************************************************************\n");
			System.out.printf("%-13s | %-13s | %-18s| %-17s| %-8s%n", "Customer Type", "Order Month", "Total Order Price",
			    "Total Order Cost", "Profit");
			System.out.printf("-----------------------------------------------------------------------------\n");
			while (rset.next()) {
				
				String customer_type = rset.getString("CustomerType");
				String order_month = rset.getString("OrderMonth");
				Double total_order_price = rset.getDouble("TotalOrderPrice");
				Double total_order_cost = rset.getDouble("TotalOrderCost");
				Double profit = rset.getDouble("Profit");
				System.out.printf("%-13s | %-13s | %-18s| %-17s| %-8s%n", customer_type, order_month, total_order_price,
				    total_order_cost, profit);
		}}
		catch (Exception e) {
				e.printStackTrace();
			}conn.close();
	
		/*
		 * Prints the ProfitByOrderType view. Remember that these views
		 * need to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * I'm not picky about how they print, just make sure it's readable.
		 */
		
		
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
	}


	public static void updateOrder(Order o) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect_to_db();
		//Integer customer_ID=o.getCustID();
		String[] generated_ID= {"ID"};
		String intstm= " UPDATE pizzaorder SET PizzaorderType = '" +o.getOrderType()+"', PizzaorderCost = " +o.getBusPrice()+" , PizzaorderPrice = " +o.getCustPrice()+
							" WHERE PizzaorderID = " + o.getOrderID() ;
		
		PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);
		int result = ppdstm.executeUpdate();
		if (result > 0) {
			try {
				ResultSet rset = ppdstm.getGeneratedKeys();
				if (rset.next()) {
					o.setOrderID(rset.getInt(1));
				}
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}
		

	}


	public static void addDinein(DineinOrder dine) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect_to_db();
		try{
			Statement stm = conn.createStatement();
			String query= " INSERT INTO dinein VALUES ("+dine.getOrderID()+","+dine.getTableNum()+")";
		
		//PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);
		stm.executeUpdate(query);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();

		}


	public static void addPickup(PickupOrder pickup2) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect_to_db();
		try{
			Statement stm = conn.createStatement();
			String query= " INSERT INTO pickup VALUE ("+pickup2.getOrderID()+")";
		
		//PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);
		stm.executeUpdate(query);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}


	public static void addDelivery(DeliveryOrder delivery2) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect_to_db();
		try{
			String[] address = delivery2.getAddress().split("-");
			int zip = Integer.parseInt(address[2]);
			Statement stm = conn.createStatement();
			String query= " INSERT INTO delivery VALUE ("+delivery2.getOrderID()+",'"+address[0]+"','"+address[1]+"',"+zip+")";
		
		//PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);
		stm.executeUpdate(query);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}


	public static void addPizzaTopping(HashMap<Integer, Integer[]> pizza_topping_map) throws SQLException, IOException{
		// TODO Auto-generated method stub
		connect_to_db();
		try{
			Statement stm = conn.createStatement(); 
			String query1="";
			//String query2="";
			//String query3="";
				for(int j = 0;j<pizza_topping_map.size();j++) {
					 query1= " INSERT INTO pizzatopping (PizzatoppingPizzaID,PizzatoppingToppingID,PizzatoppingUsage) VALUES ("+pizza_topping_map.get(j)[0]+","+pizza_topping_map.get(j)[1]+","+pizza_topping_map.get(j)[2]+")";
					 //System.out.println(pizza_topping_map.get(j)[1]);
					 stm.executeUpdate(query1);

				}
			
			
		
		//PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}


	public static void addDiscountPizza(HashMap<Integer, Integer[]> discount_pizza_map) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect_to_db();
		try{
			Statement stm = conn.createStatement(); 
			String query1="";
			//String query2="";
			//String query3="";
				for(int j = 0;j<discount_pizza_map.size();j++) {
					 query1= " INSERT INTO pizzadiscount (PizzadiscountDiscountID,PizzadiscountPizzaID) VALUES ("+discount_pizza_map.get(j)[0]+","+discount_pizza_map.get(j)[1]+")";
					// System.out.println(discount_pizza_map.get(j)[1]);
					 stm.executeUpdate(query1);

				}
			
			
		
		//PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}


	public static void addDiscountOrder(HashMap<Integer, Integer[]> discount_order_map) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect_to_db();
		try{
			Statement stm = conn.createStatement(); 
			String query1="";
			//String query2="";
			//String query3="";
				for(int j = 0;j<discount_order_map.size();j++) {
					 query1= " INSERT INTO pizzaorderdiscount (PizzaorderdiscountDiscountID,PizzaorderdiscountPizzaorderID) VALUES ("+discount_order_map.get(j)[0]+","+discount_order_map.get(j)[1]+")";
					// System.out.println(discount_pizza_map.get(j)[1]);
					 stm.executeUpdate(query1);

				}
			
			
		
		//PreparedStatement ppdstm = conn.prepareStatement(intstm,generated_ID);

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		conn.close();
	}
	
}