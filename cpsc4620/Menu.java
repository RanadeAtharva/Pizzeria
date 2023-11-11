//Code by Sarvesh Dube ad Atharva Ranade
package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import init.DBIniter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {
public static Connection connection;
	public static void main(String[] args) throws SQLException, IOException {
		 connection =DBConnector.make_connection();
	
		System.out.println("Welcome to Taylor's Pizzeria!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DBIniter.init();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);
		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			default:
				System.out.println("Enter a valid input");
				break;
			}
			
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}
	
	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");//done
		System.out.println("2. View Customers ");//done
		System.out.println("3. Enter a new Customer ");//done
		System.out.println("4. View orders");//done
		System.out.println("5. Mark an order as completed");//done
		System.out.println("6. View Inventory Levels");//done
		System.out.println("7. Add Inventory");//done
		System.out.println("8. View Reports");//done
		System.out.println("9. Exit\n\n");//done
		System.out.println("Enter your option: ");
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		/*
		 * EnterOrder should do the following:
		 * Ask if the order is for an existing customer -> If yes, select the customer. If no -> create the customer (as if the menu option 2 was selected).
		 * 
		 * Ask if the order is delivery, pickup, or dinein (ask for orderType specific information when needed)
		 * 
		 * Build the pizza (there's a function for this)
		 * 
		 * ask if more pizzas should be be created. if yes, go back to building your pizza. 
		 * 
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * apply the pizza to the order (including to the DB)
		 * 
		 * return to menu
		 */
		int customer_ID;
		Double price_cust = 0.0;
		Double price_bus = 0.0;
		
		Pizza p = new Pizza(0,"abc","xyz",0,"arc","abc",0.0,0.0);
		DineinOrder dine = new DineinOrder(0,0,"abc",0.0,0.0,0,0);
		PickupOrder pickup = new PickupOrder(0,0,"abc",0.0,0.0,0,0);
		DeliveryOrder delivery = new DeliveryOrder(0,0,"abc",0.0,0.0,0,"abc");
		
		ArrayList<Customer> Pizza =new ArrayList<Customer>();
		ArrayList<DineinOrder> Dinein =new ArrayList<DineinOrder>();
		ArrayList<PickupOrder> Pickup =new ArrayList<PickupOrder>();
		ArrayList<DeliveryOrder> Delivery =new ArrayList<DeliveryOrder>();
		ArrayList<Order> Order =new ArrayList<Order>();
		ArrayList<Pizza> Pizzas =new ArrayList<Pizza>();
		
		HashMap <Integer, Integer[]> Pizza_topping_map= new  HashMap <Integer, Integer[]>();
		HashMap <Integer, Integer[]> Discount_pizza_map= new  HashMap <Integer, Integer[]>();
		HashMap <Integer, Integer[]> Discount_order_map= new  HashMap <Integer, Integer[]>();
		
		DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime ldt=LocalDateTime.now();
		String time_stamp=dtf.format(ldt);
		
		System.out.println("Is this order for an Existing customer:y/n?");
		String response = reader.readLine();
		if("y".equals(response)||"Y".equals(response)) {
			System.out.println("Here's the list of existing customers");
			viewCustomers();
			System.out.println("\nWhich customer is this order for? Enter Id Number");
			String CustIdResponse = reader.readLine();
			int custidresponse = Integer.parseInt(CustIdResponse);
			customer_ID = custidresponse;
		}
			
		else {
			//System.out.println("Oh no");
			EnterCustomer();
			ArrayList<Customer> cust=new ArrayList<Customer>();
			cust=DBNinja.getCustomerList();
			customer_ID=cust.get(cust.size()-1).getCustID();
			System.out.println("Id for new customer is :"+ customer_ID);
			
		}
		
		System.out.println("Select the order type:\n1)-> Dine_in\n2)->Pick-up\n3)->Delivery");
		int OrderTypeResponse = Integer.parseInt(reader.readLine());
		int max_OrderID = DBNinja.getNextOrderID();
		String order_Type = null;
		Order o = new Order(0,customer_ID,"abc",time_stamp,0.0,0.0,0);
		DBNinja.addOrder(o);
		o.setOrderID(max_OrderID);
		//System.out.println(o);
		//o.setCustID(customer_ID);
		if(OrderTypeResponse==1) {
			System.out.println("Enter a Table Number:");
			int table_No = Integer.parseInt(reader.readLine());
			order_Type = "dine-in";
			//write code to actually update
			//setting all the attributes
			dine.setOrderID(max_OrderID);
			dine.setTableNum(table_No);
			//passing the objects into ArrayList
			//Dinein.add(dine);
			DBNinja.addDinein(dine);
		}
		else if(OrderTypeResponse==2) {
			order_Type="Pickup";
			//write code to actually update
			pickup.setOrderID(max_OrderID);
			Pickup.add(pickup);
			DBNinja.addPickup(pickup);
			
		}
		else if(OrderTypeResponse==3) {
			order_Type = "Delivery";
			System.out.println("Enter the customer's address(street address and City: (155)");
			String Cust_stadd= reader.readLine();
			System.out.println("Enter teh customer's state: (Short Forms)");
			String Cust_Stateadd = reader.readLine();
			System.out.println("Enter the customer's zipcode:");
			String Cust_Zip = reader.readLine();
			delivery.setOrderID(max_OrderID);
			delivery.setAddress(Cust_stadd +"-"+Cust_Stateadd +"-"+Cust_Zip);
			Delivery.add(delivery);
			DBNinja.addDelivery(delivery);
			//write code to actually update
		}
		else {
			System.out.println("Enter a Valid Input");
		}

		int Discpizzaiter=0;
		int Discorderiter=0;
		int iter=0;
		Boolean PizzaFlag = true;
		int Pizzaiter = 0;
		while(PizzaFlag) {
			
			System.out.println("Let's Build a Pizza\nWhat size is the pizza\n1)->Small\n2)->Medium\n3)->Large\n4)->XLarge\n Enter the corresponding choice");
			int SizeResponse= Integer.parseInt(reader.readLine());
			String size_response="";
			if (SizeResponse==1) {
				size_response="Small";
			}
			else if (SizeResponse==2) {
				size_response="Medium";
			}
			else if (SizeResponse==3) {
				size_response="Large";
			}
			else if(SizeResponse == 4){
				size_response="X-Large";
			}
			System.out.println("What crust is the pizza\n1)->Thin\n2)->Original\n3)->Pan\n4)->Gluten-Free\n Enter the corresponding choice");
			int CrustResponse= Integer.parseInt(reader.readLine());
			String crust_response="c";
			if (CrustResponse==1) {
				crust_response="Thin";
				
			}
			else if (CrustResponse==2) {
				crust_response="Original";
			}
			else if (CrustResponse==3) {
				crust_response="Pan";
			}
			else if(CrustResponse==4){
				crust_response="Gluten-Free";
			}
			Double cust_price = DBNinja.getBaseCustPrice(size_response,crust_response);
			Double bus_price = DBNinja.getBaseBusPrice(size_response, crust_response);
			//Order o = new Order(0,customer_ID,order_Type,time_stamp,cust_price,bus_price,0);
			
			
			o.setOrderType(order_Type);
			//o.setDate(time_stamp);
			o.setCustPrice(cust_price);
			o.setBusPrice(bus_price);
			
			
			//Pizza p = new Pizza(DBNinja.getMaxPizzaID()+1,size_response,crust_response,max_OrderID,"In-progress",time_stamp,cust_price,bus_price);
			p.setPizzaID(DBNinja.getMaxPizzaID());
			p.setSize(size_response);
			p.setCrustType(crust_response);
			p.setOrderID(max_OrderID);
			p.setPizzaState("In-progress");
			p.setPizzaDate(time_stamp);
			p.setCustPrice(cust_price);
			p.setBusPrice(bus_price);
			//System.out.println(p);
			//System.out.println(o);
			ViewInventoryLevels();
			int bool =0;
			
			ArrayList<Integer> random = new ArrayList<Integer>();
			
			while(bool!=-1){
				//Code for while loop
				System.out.println("Which Toppings do you want to add. Enter -1 to stop adding:");
				int ToppingResponse = Integer.parseInt(reader.readLine());
				int flag = ToppingResponse;
				
				if(ToppingResponse==-1) {
					bool=-1;
					ViewInventoryLevels();
					break;
				}
				else {
					System.out.println("Do you want to add extra toppings? y/n");
					String TopExtraResponse = reader.readLine();
					
					//ArrayList<Topping> top = new ArrayList<Topping>();
					//toppings =DBNinja.getInventory();
					
					if("Y".equals(TopExtraResponse)||"y".equals(TopExtraResponse)) {
					//extra topping code
						Topping t = DBNinja.getToppingFromID(ToppingResponse);
						//System.out.println(t);
						DBNinja.useTopping(p, t, true);
						//System.out.println(p.getCustPrice());
						if(t.getCurINVT()>0) {
							p.addToppings(t, true);}
						//System.out.println(p.getCustPrice());
						//random.add(p.getPizzaID());
						//random.add(t.getTopID());
						//random.add(1);
						int helperPizzaId = p.getPizzaID();
						int helperTopID = t.getTopID();
						if(t.getCurINVT()>0)
						Pizza_topping_map.put(iter, new Integer [] {helperPizzaId,helperTopID,1});
						iter++;
						
					}
					else if("N".equals(TopExtraResponse)||"n".equals(TopExtraResponse)){
					//else code
						Topping t = DBNinja.getToppingFromID(ToppingResponse);
						DBNinja.useTopping(p, t, false);
						if(t.getCurINVT()>0)
						p.addToppings(t, false);
						if(t.getCurINVT()>0)
						Pizza_topping_map.put(iter, new Integer [] {p.getPizzaID(),t.getTopID(),0});
						iter++;
					}
					else {
						System.out.println("Enter a Valid Input");
					}
				System.out.println("Printing Current Inventory List");
				ViewInventoryLevels();
				//p.setPizzaID(DBNinja.getMaxPizzaID()+iter);
				//iter++;
			}} ;
			//System.out.println(Pizza_topping_map.get(0)[0]);
			//Order o = new Order(8, 1, "dine-in", "2023-03-05 12:04:00", 14.00, 5.00, 1);
			//DBNinja.addOrder(o);
	//		Double Price=DBNinja.getBaseCustPrice("Small", "Original");
	//		System.out.println(Price);
	//		Double Price1=DBNinja.getBaseBusPrice("Small", "Original");
	//		System.out.println(Price1);
			
			
		//discount code
		//System.out.println(p);
		System.out.println("Do you want to add Discount to this Pizza? Enter y/n");
		String Discount_response = reader.readLine();
		if("Y".equals(Discount_response)||"y".equals(Discount_response)) {
			ArrayList<Discount> discs = new ArrayList<Discount>();
			discs = DBNinja.getDiscountList();
//			for (Discount op : discs) {
//				System.out.println(op);
//			}
			int DiscBool=0;
			
			
			while(DiscBool!=-1) {
				System.out.println("Which Discount do you want to apply(DiscountID): Enter -1 to stop adding Discounts");
				Discount d = null;
				int DiscResponse= Integer.parseInt(reader.readLine());
				
				double cust_pizza_price=p.getCustPrice();
			
				if(DiscResponse==-1) {
					DiscBool=-1;
					break;
				}
				else {
					for (Discount op:discs) {
						if(op.getDiscountID()==DiscResponse){
							d=op;
						}
					}
					if(d.isPercent()) {
						p.setCustPrice(cust_pizza_price-((cust_pizza_price*d.getAmount())/100));
					} else {
						p.setCustPrice(cust_pizza_price - d.getAmount());;
					}
	
					int helpervar = p.getPizzaID();
					Discount_pizza_map.put(Discpizzaiter, new Integer [] {d.getDiscountID(),helpervar});
					//System.out.println(Discount_pizza_map.get(0)[0]);
					//System.out.println(p);
					Discpizzaiter++;
				}
			}
			
			
			
		}
		else {
			
		}//else just in case
		price_cust=price_cust+p.getCustPrice();
		price_bus=price_bus+p.getBusPrice();
		o.setCustPrice(price_cust);
		o.setBusPrice(price_bus);
		DBNinja.addPizza(p);
		System.out.println("Do you want to add more pizzas? (y/n):");
		String addPizza = reader.readLine();
		if(addPizza.equals("N") || addPizza.equals("n"))
		PizzaFlag = false;
		Pizzas.add( new Pizza(p.getPizzaID(),p.getCrustType(),p.getSize(),p.getOrderID(),p.getPizzaState(),p.getPizzaDate(),p.getCustPrice(),p.getBusPrice()));
		Pizzaiter++;
	}
	System.out.println("Do you want to add Discount for your Order?(y/n)");
	String Order_disc_response = reader.readLine();
	if("Y".equals(Order_disc_response)||"y".equals(Order_disc_response)) {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		discs = DBNinja.getDiscountList();
		for (Discount op : discs) {
			System.out.println(op);
		}
		int DiscBool=0;
		int Disciter=0;
		
		while(DiscBool!=-1) {
			System.out.println("Which Discount do you want to apply(DiscountID): Enter -1 to stop adding Discounts");
			Discount d = null;
			int DiscOrderResponse= Integer.parseInt(reader.readLine());
			double cust_disc_price= o.getCustPrice();
			if(DiscOrderResponse==-1) {
				DiscBool=-1;
				break;
			}
			else {
				for (Discount op:discs) {
					if(op.getDiscountID()==DiscOrderResponse){
						d=op;
					}
				}
				if(d.isPercent()) {
					o.setCustPrice(cust_disc_price-((cust_disc_price*d.getAmount())/100));
				} else {
					o.setCustPrice(cust_disc_price - d.getAmount());;
				}


				Discount_order_map.put(Discorderiter, new Integer [] {d.getDiscountID(),o.getOrderID()});
				Discorderiter++;
				//System.out.println(Discount_order_map.get(0)[0]);
				//System.out.println(o);

			}
		}
	}
	//Order.add(o);
	DBNinja.updateOrder(o);
	
	
	
//	*******************************************************WAS USED TO CHECK FOR THE VALUES********************************************************************************8

	//System.out.println(Pizzas);
	//System.out.println(Pizzas.get(0));
	//System.out.println(Pizzas.get(1));
	//System.out.println(Dinein);
	//System.out.println(Pickup);
	//System.out.println(Delivery);
	//System.out.println(Order);
	//System.out.println("");
//	for(int i=0 ; i<Pizza_topping_map.size();i++) {
//		for(int j=0; j<3;j++) {
//			System.out.print(Pizza_topping_map.get(i)[j]);
//			System.out.print(" | ");
//			}}
//	System.out.println("");
//	for(int i=0 ; i<Discount_pizza_map.size();i++) {
//		for(int j=0; j<2;j++) {
//			System.out.print(Discount_pizza_map.get(i)[j]);
//			System.out.print(" | ");
//		}}
//	System.out.println("");
//	for(int i=0 ; i<Discount_order_map.size();i++) {
//		for(int j=0; j<2;j++) {
//			System.out.print(Discount_order_map.get(i)[j]);
//			System.out.print(" | ");
//			}}
	
	
	
//	*******************************************************WAS USED TO CHECK FOR THE VALUES********************************************************************************8
	
	
	
	
	//System.out.println(Pizza_map.get(0)[0]);
//	ArrayList<Customer> Pizza =new ArrayList<Customer>();
//	ArrayList<DineinOrder> Dinein =new ArrayList<DineinOrder>();
//	ArrayList<PickupOrder> Pickup =new ArrayList<PickupOrder>();
//	ArrayList<DeliveryOrder> Delivery =new ArrayList<DeliveryOrder>();
//	ArrayList<Order> Order =new ArrayList<Order>();
//	ArrayList<Pizza> Pizzas =new ArrayList<Pizza>();
//	HashMap <Integer, Integer[]> Pizza_topping_map= new  HashMap <Integer, Integer[]>();
//	HashMap <Integer, Integer[]> Discount_pizza_map= new  HashMap <Integer, Integer[]>();
//	HashMap <Integer, Integer[]> Discount_order_map= new  HashMap <Integer, Integer[]>();
	
	//add stuff to database with the help of arraylists and hashmaps
//	for (Pizza pizza: Pizzas) {
//		DBNinja.addPizza(pizza);
//	}
	//sql inserts for bridge tables
	DBNinja.addPizzaTopping(Pizza_topping_map);
	DBNinja.addDiscountPizza(Discount_pizza_map);
	DBNinja.addDiscountOrder(Discount_order_map);
	System.out.println("Finished adding order...Returning to menu...");
	}	
	
	public static void viewCustomers()
	{
		/*
		 * Simply print out all of the customers from the database. 
		 */
		ArrayList<Customer> custs = new ArrayList<Customer>();
		try {
			custs=DBNinja.getCustomerList();
			for(Customer cust:custs)
			{
				System.out.println(cust.toString());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}
	

	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException 
	{
		/*
		 * Ask what the name of the customer is. YOU MUST TELL ME (the grader) HOW TO FORMAT THE FIRST NAME, LAST NAME, AND PHONE NUMBER.
		 * If you ask for first and last name one at a time, tell me to insert First name <enter> Last Name (or separate them by different print statements)
		 * If you want them in the same line, tell me (First Name <space> Last Name).
		 * 
		 * same with phone number. If there's hyphens, tell me XXX-XXX-XXXX. For spaces, XXX XXX XXXX. For nothing XXXXXXXXXXXX.
		 * 
		 * I don't care what the format is as long as you tell me what it is, but if I have to guess what your input is I will not be a happy grader
		 * 
		 * Once you get the name and phone number (and anything else your design might have) add it to the DB
		 */
		Integer CustId = Types.NULL;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Customer's First Name:");
		String FName = reader.readLine();
		System.out.println("Enter Customer's Last Name:");
		String LName = reader.readLine();
		System.out.println("Enter Customer's Phone Number(xxx-xxx-xxxx)");
		String PhoneNo = reader.readLine();
		Customer c = new Customer(CustId,FName,LName,PhoneNo);
		DBNinja.addCustomer(c);

	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException 
	{
	/*
	 * This should be subdivided into two options: print all orders (using simplified view) and print all orders (using simplified view) since a specific date.
	 * 
	 * Once you print the orders (using either sub option) you should then ask which order I want to see in detail
	 * 
	 * When I enter the order, print out all the information about that order, not just the simplified view.
	 * 
	 */
		ArrayList<Order> orders = new ArrayList<Order>();
		
		try {
			orders =DBNinja.getCurrentOrders();
			for(Order order:orders)
			{
				System.out.println(order.toString());
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		/*All orders that are created through java (part 3, not the 7 orders from part 2) should start as incomplete
		 * 
		 * When this function is called, you should print all of the orders marked as complete 
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		ArrayList<Order> orders = new ArrayList<Order>();
		ArrayList<Order> orders1 = new ArrayList<Order>();
		//System.out.println("These are the Completed orders");
//		try {
//			System.out.println("");
//			orders =DBNinja.getCurrentOrders(1);
//			for(Order order:orders)
//			{
//				System.out.println(order.toString());
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		try {
			orders1 =DBNinja.getCurrentOrders(0);
			
			if (orders1.isEmpty()) 
			{
				System.out.println("All orders are complete");
			}
			else 
			{	System.out.println("These are the incomplete Orders:");
				for(Order order:orders1)
				{
					System.out.println(order.toString());
				}
				String option;
				
				System.out.println("Enter the orderID of the order would you like to mark as complete:");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				option =reader.readLine();
				int index=0;
				int option1= Integer.parseInt(option);
				for (int i =0;i<orders.size();i++) 
					{
						if(option1==orders.get(i).getOrderID())
						{
							 index=i;
						}
					}
				
				DBNinja.CompleteOrder(option1);
				System.out.println("Successfully marked order as complete");
				
			}
						
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	

	}

	// See the list of inventory and it's current level
	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		//print the inventory. I am really just concerned with the ID, the name, and the current inventory
		ArrayList<Topping> toppings = new ArrayList<Topping>();
		try {
			//toppings =DBNinja.getInventory();
			//for(Topping topping:toppings)
			//{
			//	System.out.println(topping.toString());
			//}
			DBNinja.printInventory();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		
		
		
		
		


	// Select an inventory item and add more to the inventory level to re-stock the
	// inventory
	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping they want to add more to and how much to add
		 */
		ViewInventoryLevels();
		System.out.println("Enter the Topping ID of the Topping you would like to Add:\n");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String Add =reader.readLine();
		int TopID=Integer.parseInt(Add);
		System.out.println("Enter the Amount of the Topping you would like to Add:\n");
		String amt=reader.readLine();
		int Amt=Integer.parseInt(amt);
		DBNinja.AddToInventory(TopID,Amt);
		
		
		
		
		
		
	}

	// A function that builds a pizza. Used in our add new order function
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper function for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Pizza ret = null;
		
		
		
		
		
		
		
		return ret;
	}
	
	private static int getTopIndexFromList(int TopID, ArrayList<Topping> tops)
	{
		/*
		 * This is a helper function I used to get a topping index from a list of toppings
		 * It's very possible you never need to use a function like this
		 * 
		 */
		int ret = -1;
		
		
		
		return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This function calls the DBNinja functions to print the three reports.
		 * 
		 * You should ask the user which report to print
		 */
		int choice;
		String choice1;
		System.out.println("Select the report you want to print:\n"+"1-> Topping Popularity\n"+"2-> Profit By Pizza\n"+"3-> Profit By Order Type\n");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		choice1=reader.readLine();
		choice = Integer.parseInt(choice1);
		switch(choice) {
		case 1: {
			DBNinja.printToppingPopReport();
			break;
		}
		case 2:{
			DBNinja.printProfitByPizzaReport();;
			break;
		}
		case 3:{
			DBNinja.printProfitByOrderType();
			break;
		}
		default:{
			System.out.println("Enter a Valid Input");
		}
		}
		
	}

}


//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
//DO NOT EDIT ANYTHING BELOW HERE, I NEED IT FOR MY TESTING DIRECTORY. IF YOU EDIT SOMETHING BELOW, IT BREAKS MY TESTER WHICH MEANS YOU DO NOT GET GRADED (0)

/*
CPSC 4620 Project: Part 3 â€“ Java Application Due: Thursday 11/30 @ 11:59 pm 125 pts

For this part of the project you will complete an application that will interact with your database. Much of the code is already completed, you will just need to handle the functionality to retrieve information from your database and save information to the database.
Note, this program does little to no verification of the input that is entered in the interface or passed to the objects in constructors or setters. This means that any junk data you enter will not be caught and will propagate to your database, if it does not cause an exception. Be careful with the data you enter! In the real world, this program would be much more robust and much more complex.

Program Requirements:

Add a new order to the database: You must be able to add a new order with pizzas to the database. The user interface is there to handle all of the input, and it creates the Order object in the code. It then calls DBNinja.addOrder(order) to save the order to the database. You will need to complete addOrder. Remember addOrder will include adding the order as well as the pizzas and their toppings. Since you are adding a new order, the inventory level for any toppings used will need to be updated. You need to check to see if there is inventory available for each topping as it is added to the pizza. You can not let the inventory level go negative for this project. To complete this operation, DBNinja must also be able to return a list of the available toppings and the list of known customers, both of which must be ordered appropropriately.

View Customers: This option will display each customer and their associated information. The customer information must be ordered by last name, first name and phone number. The user interface exists for this, it just needs the functionality in DBNinja

Enter a new customer: The program must be able to add the information for a new customer in the database. Again, the user interface for this exists, and it creates the Customer object and passes it to DBNinja to be saved to the database. You need to write the code to add this customer to the database. You do need to edit the prompt for the user interface in Menu.java to specify the format for the phone number, to make sure it matches the format in your database.

View orders: The program must be able to display orders and be sorted by order date/time from most recent to oldest. The program should be able to display open orders, all the completed orders or just the completed order since a specific date (inclusive) The user interface exists for this, it just needs the functionality in DBNinja

Mark an order as completed: Once the kitchen has finished prepping an order, they need to be able to mark it as completed. When an order is marked as completed, all of the pizzas should be marked as completed in the database. Open orders should be sorted as described above for option #4. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Inventory Levels: This option will display each topping and its current inventory level. The toppings should be sorted in alphabetical order. Again, the user interface exists for this, it just needs the functionality in DBNinja

Add Inventory: When the inventory level of an item runs low, the restaurant will restock that item. When they do so, they need to enter into the inventory how much of that item was added. They will select a topping and then say how many units were added. Note: this is not creating a new topping, just updating the inventory level. Make sure that the inventory list is sorted as described in option #6. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Reports: The program must be able to run the 3 profitability reports using the views you created in Part 2. Again, the user interface exists for this, it just needs the functionality in DBNinja

Modify the package DBConnector to contain your database connection information, this is the same information you use to connect to the database via MySQL Workbench. You will use DBNinja.connect_to_db to open a connection to the database. Be aware of how many open database connections you make and make sure the database is properly closed!
Your code needs to be secure, so any time you are adding any sort of parameter to your query that is a String, you need to use PreparedStatements to prevent against SQL injections attacks. If your query does not involve any parameters, or if your queries parameters are not coming from a String variable, then you can use a regular Statement instead.

The Files: Start by downloading the starter code files from Canvas. You will see that the user interface and the java interfaces and classes that you need for the assignment are already completed. Review all these files to familiarize yourself with them. They contain comments with instructions for what to complete. You should not need to change the user interface except to change prompts to the user to specify data formats (i.e. dashes in phone number) so it matches your database. You also should not need to change the entity object code, unless you want to remove any ID fields that you did not add to your database.

You could also leave the ID fields in place and just ignore them. If you have any data types that donâ€™t match (i.e. string size options as integers instead of strings), make the conversion when you pull the information from the database or add it to the database. You need to handle data type differences at that time anyway, so it makes sense to do it then instead of making changes to all of the files to handle the different data type or format.

The Menu.java class contains the actual user interface. This code will present the user with a menu of options, gather the necessary inputs, create the objects, and call the necessary functions in DBNinja. Again, you will not need to make changes to this file except to change the prompt to tell me what format you expect the phone number in (with or without dashes).

There is also a static class called DBNinja. This will be the actual class that connects to the database. This is where most of the work will be done. You will need to complete the methods to accomplish the tasks specified.

Also in DBNinja, there are several public static strings for different crusts, sizes and order types. By defining these in one place and always using those strings we can ensure consistency in our data and in our comparisons. You donâ€™t want to have â€œSMALLâ€� â€œsmallâ€� â€œSmallâ€� and â€œPersonalâ€� in your database so it is important to stay consistent. These strings will help with that. You can change what these strings say in DBNinja to match your database, as all other code refers to these public static strings.

Start by changing the class attributes in DBConnector that contain the data to connect to the database. You will need to provide your database name, username and password. All of this is available is available in the Chapter 15 lecture materials. Once you have that done, you can begin to build the functions that will interact with the database.

The methods you need to complete are already defined in the DBNinja class and are called by Menu.java, they just need the code. Two functions are completed (getInventory and getTopping), although for a different database design, and are included to show an example of connecting and using a database. You will need to make changes to these methods to get them to work for your database.

Several extra functions are suggested in the DBNinja class. Their functionality will be needed in other methods. By separating them out you can keep your code modular and reduce repeated code. I recommend completing your code with these small individual methods and queries. There are also additional methods suggested in the comments, but without the method template that could be helpful for your program. HINT, make sure you test your SQL queries in MySQL Workbench BEFORE implementing them in codeâ€¦it will save you a lot of debugging time!

If the code in the DBNinja class is completed correctly, then the program should function as intended. Make sure to TEST, to ensure your code works! Remember that you will need to include the MySQL JDBC libraries when building this application. Otherwise you will NOT be able to connect to your database.

Compiling and running your code: The starter code that will compile and â€œrunâ€�, but it will not do anything useful without your additions. Because so much code is being provided, there is no excuse for submitting code that does not compile. Code that does not compile and run will receive a 0, even if the issue is minor and easy to correct.

Help: Use MS Teams to ask questions. Do not wait until the last day to ask questions or get started!

Submission You will submit your assignment on Canvas. Your submission must include: â€¢ Updated DB scripts from Part 2 (all 5 scripts, in a folder, even if some of them are unchanged). â€¢ All of the class code files along with a README file identifying which class files in the starter code you changed. Include the README even if it says â€œI have no special instructions to shareâ€�. â€¢ Zip the DB Scripts, the class files (i.e. the application), and the README file(s) into one compressed ZIP file. No other formats will be accepted. Do not submit the lib directory or an IntellJ or other IDE project, just the code.

Testing your submission Your project will be tested by replacing your DBconnector class with one that connects to a special test server. Then your final SQL files will be run to recreate your database and populate the tables with data. The Java application will then be built with the new DBconnector class and tested.

No late submissions will be accepted for this assignment.*/

//INITNUM: 7251151