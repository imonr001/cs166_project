/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.text.Format;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.management.StringValueExp;

import java.util.ArrayList;
import java.lang.Math;
import java.nio.Buffer;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

   public static String user_name = "";
   public static String user_id = "";
   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in Customer");
            System.out.println("3. Log in Manager");
            System.out.println("4. Log in Admin");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            String authorisedMgr = null;
            String authorisedAdmin = null;

            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 3: authorisedMgr = LogInMGR(esql); break;
               case 4: authorisedAdmin = LogInAdmin(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");
                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
            if (authorisedMgr != null) {
               boolean usermenu = true;
               while(usermenu) {
                 System.out.println("MAIN MENU");
                 System.out.println("---------");
               //   System.out.println("1. View Stores within 30 miles");
               //   System.out.println("2. View Product List");
               //   System.out.println("3. Place a Order");
               //   System.out.println("4. View 5 recent orders");
 
                 //the following functionalities basically used by managers
                 System.out.println("1. Update Product");
                 System.out.println("2. View 5 recent Product Updates Info");
                 System.out.println("3. View 5 Popular Items");
                 System.out.println("4. View 5 Popular Customers");
                 System.out.println("5. Place Product Supply Request to Warehouse");
                 System.out.println("6. View managed stores and products"); 
                 
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                  //   case 1: viewStores(esql); break;
                  //   case 2: viewProducts(esql); break;
                  //   case 3: placeOrder(esql); break;
                  //   case 4: viewRecentOrders(esql); break;
                    case 1: updateProduct(esql); break;
                    case 2: viewRecentUpdates(esql); break;
                    case 3: viewPopularProducts(esql); break;
                    case 4: viewPopularCustomers(esql); break;
                    case 5: placeProductSupplyRequests(esql); break;
                    case 6: viewManagerStoresAndOrders(esql); break;
                    case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
             if (authorisedAdmin != null) {
               boolean usermenu = true;
               while(usermenu) {
                 System.out.println("MAIN MENU");
                 System.out.println("---------");
                 System.out.println("1. View User");
                 System.out.println("2. View All Users");
                 System.out.println("2. View Product");
                 System.out.println("3. View 5 Recent Updates on Products");
                 System.out.println("4. Update User");
                 System.out.println("5. Update Product");
                 System.out.println("6. Delete User");
                 System.out.println("7. Delete Product");
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                    case 1: viewUser(esql); break;
                    case 2: viewAllUsers(esql);break;
                    case 3: viewProducts(esql); break;
                    case 4: viewRecentUpdates(esql);
                    case 5: updateUserAdmin(esql); break;
                    case 6: updateProductAdmin(esql); break;
                    case 7: deleteUser(esql); break;
                    case 8: deleteProduct(esql); break;
                     case 20: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
             
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
         String queryUserID = String.format("SELECT userid FROM USERS u WHERE u.name = '%s' AND u.password = '%s'", name, password);
         List<List<String>> id = esql.executeQueryAndReturnResult(queryUserID);
         setUser(name, id.get(0));


	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println ("User does not exist!");
         return null;
      }
   }//end

   public static String LogInMGR(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'AND type = 'manager'", name, password);
         int userNum = esql.executeQuery(query);
         String queryMgrID = String.format("SELECT userid FROM USERS u WHERE u.name = '%s' AND u.password = '%s' AND type = 'manager'", name, password);
         List<List<String>> id = esql.executeQueryAndReturnResult(queryMgrID);
         setUser(name, id.get(0));

	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println("User does not exist!");
         return null;
      }
   }//end
   public static String LogInAdmin(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'AND type = 'admin'", name, password);
         int userNum = esql.executeQuery(query);
         String queryAdminID = String.format("SELECT userid FROM USERS u WHERE u.name = '%s' AND u.password = '%s' AND type = 'admin'", name, password);
         List<List<String>> id = esql.executeQueryAndReturnResult(queryAdminID);
         setUser(name, id.get(0));

	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println ("User does not exist!");
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {
      try{   
         String query = String.format("select s.storeID, s.name, calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) as dist from users u, store s where u.userID = %s and calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) <= 30",Retail.getUserID());
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

   public static void viewProducts(Retail esql) {
     try{
         System.out.print("\tEnter store number: ");
         String storeNumber = in.readLine();  
         try{
         Integer.parseInt(storeNumber);
         
         }catch (NumberFormatException e) {
            System.out.println("ERROR: Input is not a number");
         }   
           String query = String.format(" select p.* from product p join store s on s.storeid = p.storeid AND p.storeid = '%s'", storeNumber);
           esql.executeQueryAndPrintResult(query); 
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }

   public static void placeOrder(Retail esql) {
      try{
         System.out.print("\tEnter store ID: $");
          String storeID = in.readLine();
         System.out.print("\tEnter product name: $");
         String productName = in.readLine();
         System.out.print("\tEnter number of units: $");
         String numberOFUnits = in.readLine();

	 String checkIfQuantityValid = String.format("SELECT P.numberOfUnits FROM Product P, Store S WHERE S.storeID = '%s' AND P.productName = '%s' AND P.storeID = '%s'", storeID, productName, storeID);


         String queryUpdateProduct = String.format("UPDATE product set numberofunits = numberofunits - '%s' WHERE storeid = '%s' and productname = '%s' and product.storeid IN (select s.storeID as dist from users u, store s where u.userID = '%s' and s.storeID = '%s' and calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) <= 30); ",numberOFUnits,storeID,productName,Retail.getUserID(),storeID);
         String queryOrderProduct = String.format("insert into orders(ordernumber,customerid,storeid,productname,unitsordered,ordertime) VALUES (DEFAULT,'%s','%s','%s','%s',now()::timestamptz(0))",Retail.getUserID(),storeID,productName,numberOFUnits);
         String checkIfStoreInRange =String.format("select s.storeID from users u, store s where u.userID = '%s'  and s.storeid = '%s' and calculate_distance(u.latitude, u.longitude, s.latitude, s.longitude) <= 30;",Retail.getUserID(),storeID);
         
          int check = esql.executeQuery(checkIfStoreInRange);
         if(check > 0){
	    List<List<String>> retrieveQuantity = esql.executeQueryAndReturnResult(checkIfQuantityValid);
	    int quantity = Integer.parseInt(retrieveQuantity.get(0).get(0));

	    if (quantity < Integer.valueOf(numberOFUnits)) {
		System.out.println("Not enough inventory to order " + numberOFUnits + " products. The store current has " + quantity + " units available.");
	    }
	    else {
            	esql.executeUpdate(queryOrderProduct);
		esql.executeUpdate(queryUpdateProduct);
            	System.out.print("\nOrder has been placed!\n");
	    }
         }
         else{
            System.out.print("\nThis store is not in range!\n");
         }
      }catch(Exception e){
	System.err.println("This store does not contain this product"); 
      }
   }

   public static void viewRecentOrders(Retail esql) {
   //    System.out.println("In viewRecentOrders");

	//    String checkIfMGR = String.format("SELECT U.userID FROM Users U WHERE U.userID = %s AND type = 'manager'", Retail.getUserID());
	//    String query = String.format("SELECT S.storeID, S.name, O.productName, O.unitsOrdered, O.orderTime FROM Users U, Orders O, Store S WHERE U.userID = %s AND O.customerID = %s AND S.storeID = O.storeID", Retail.getUserID(), Retail.getUserID());
	//    try {
	// 	int check = esql.executeQuery(checkIfMGR);
	// 	System.out.println(check);
	// 	esql.executeQueryAndPrintResult(query);
	//    }
	//    catch (Exception e) {
	// 	System.err.println(e.getMessage());
	// }
   String query = String.format("SELECT S.storeID, S.name, O.productName, O.unitsOrdered, O.orderTime FROM Users U, Orders O, Store S WHERE U.userID = '%s' AND O.customerID = '%s' AND S.storeID = O.storeID ORDER BY O.orderTime::timestamp DESC limit 5", Retail.getUserID(), Retail.getUserID()).trim();
	try {
		esql.executeQueryAndPrintResult(query.trim());
	}
	catch (Exception e) {
		System.err.println(e.getMessage());
	}

   }
   public static void updateProduct(Retail esql) {
      try {
         System.out.print("\tEnter store ID: $");
         String storeID = in.readLine();
         String queryStore = String.format("select s.storeid From store s join users u on u.userid = s.managerid WHERE (s.managerid = '%s' AND u.name = '%s' AND s.storeid = '%s');",Retail.getUserID(),Retail.getName(),storeID);
         List<List<String>> store = esql.executeQueryAndReturnResult(queryStore);
         List<List<String>> checkList = new ArrayList<List<String>>();
         List<String> l = new ArrayList<>();
         l.add(storeID);
         checkList.add(0,l);

         // String updateProduct = "";
         
         if(store.equals(checkList)==true){
         System.out.print("\tPlease Enter the name of the Product that you would like to update: $");
         String productName = in.readLine();
         System.out.print("\tPlease Enter the number of unites that you would like to update it to: $");
         String numberOfUnits = in.readLine();
         System.out.print("\tPlease Enter the new price per unit: $");
         String pricePerUnit = in.readLine();
         String updateProduct = String.format("UPDATE product set numberofunits = numberofunits + '%s', priceperunit = '%s' WHERE storeid = '%s' and productname = '%s' and product.storeid in (select s.storeID from users u, store s where u.userID = '%s'); ",numberOfUnits,pricePerUnit,storeID,productName,Retail.getUserID());
          esql.executeUpdate(updateProduct);
          String productUpdateInsert = String.format("insert into productupdates (updatenumber,managerid,storeid,productname,updatedon) VALUES( DEFAULT, '%s','%s','%s',now()::timestamptz(0));",Retail.getUserID(),storeID,productName);
          esql.executeUpdate(productUpdateInsert);
         }else{
            System.out.println("\nStore is not mangaged by this manager!\n \nPlease make a new selection\n");
         }
      } catch (Exception e) {
         System.err.println (e.getMessage());
      } 
   }
   public static void viewRecentUpdates(Retail esql) {
      try {
         //List<List<String>> storeID = new ArrayList<List<String>>();
         //String findCurrentStore = String.format("select s.storeid FROM store s JOIN users u on s.managerid = u.userid and u.name = '%s' order by dateestablished DESC limit 1;",Retail.getName());
         //storeID = esql.executeQueryAndReturnResult(findCurrentStore);
         //String viewLastFiveUpdates = String.format("select * from productupdates p where p.managerid = '%s' and p.storeid = '%s' order by updatedon DESC LIMIT 5;",Retail.getUserID(),storeID.get(0).get(0));
         String viewLastFiveUpdates = String.format("select p.storeid,p.managerid,p.productname,p.updatenumber,p.updatedon from productupdates p where p.managerid = '%s' order by updatedon DESC LIMIT 5;",Retail.getUserID());
         esql.executeQueryAndPrintResult(viewLastFiveUpdates);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
    
   }
   public static void viewManagerStoresAndOrders(Retail esql) {
      String query = String.format("SELECT o.*,c.name from store s join users m on m.userid = s.managerid join orders o on o.storeid = s.storeid join users c on c.userid = o.customerid and m.userid = '%s' ORDER BY O.orderTime::timestamp DESC", Retail.getUserID());
      try {
         System.out.println(Retail.getUserID());
         esql.executeQueryAndPrintResult(query);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }
   public static void viewPopularProducts(Retail esql) {
      String query = String.format("SELECT O.productName, SUM(O.unitsOrdered) as Total_amount FROM Orders O, Store S WHERE S.managerID = '%s' AND S.storeID = O.storeID GROUP BY O.productName ORDER BY SUM(O.unitsOrdered) DESC limit 5", Retail.getUserID());
	
	try {
		esql.executeQueryAndPrintResult(query);
	}
	catch(Exception e) {
		System.err.println(e.getMessage());
	}
   }
   public static void viewPopularCustomers(Retail esql) {
      String query = String.format("SELECT U.name, COUNT(O.orderNumber) FROM Orders O, Users U, Store S WHERE S.managerID = '%s' AND O.storeID = S.storeID AND O.customerID = U.userID GROUP BY U.name ORDER BY COUNT(O.orderNumber) DESC limit 5", Retail.getUserID());
	try {
	esql.executeQueryAndPrintResult(query);
	}
	catch(Exception e) {
		System.err.println(e.getMessage());
	}
   }
   public static void placeProductSupplyRequests(Retail esql) {
   try {
      System.out.print("\tEnter store ID: $");
      String storeID = in.readLine();
      System.out.print("\tPlease Enter the name of the Product name: $");
      String productName = in.readLine();
      System.out.print("\tPlease Enter the number of unites needed: $");
      String numberOfUnitesNeeded = in.readLine();
      System.out.print("\tPlease Enter the warehouse ID number: $");
      String warehouseID = in.readLine();

      String insertRequest = String.format("INSERT INTO productsupplyrequests (requestnumber,managerid,warehouseid,storeid,productname,unitsrequested) VALUES (DEFAULT,'%s','%s','%s','%s','%s');",Retail.getUserID(),warehouseID,storeID,productName,numberOfUnitesNeeded);
      String productUpdate = String.format("UPDATE product set numberofunits = numberofunits + '%s' WHERE storeid = '%s' and productname = '%s' and product.storeid in (select s.storeID from users u, store s where u.userID = '%s');",numberOfUnitesNeeded,storeID,productName,Retail.getUserID());
      esql.executeUpdate(insertRequest);
      esql.executeUpdate(productUpdate);
      System.out.print("\nRequest for "+productName+ " " + "was successful!\n");

   } catch (Exception e) {
      System.err.println(e.getMessage());

   }
   }
   public static void viewUser(Retail esql) {
      try {
      System.out.print("Please enter the name of the user: $");
      String user = in.readLine();
      String query = String.format("select s.userid,TRIM(TRAILING ' ' from s.name)as Name,s.type from users s where s.name = '%s';",user);
      esql.executeQueryAndPrintResult(query);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }
   public static void deleteUser(Retail esql) {
      try {
      
         System.out.print("Please enter the name of the user you would like to delete: $");
         String user = in.readLine();
         String query = String.format("DELETE FROM users u WHERE u.name = '%s' AND u.type != 'admin';",user);
         esql.executeQueryAndPrintResult(query);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }
   public static void deleteProduct(Retail esql) {
      try {
         System.out.print("Please enter the name of the product you would like to delete: ");
         String product = in.readLine();
         System.out.print("Please enter the store ID from of the product you would like to delete: ");
         String storeid = in.readLine();
         String query = String.format("DELETE FROM product p WHERE p.productname = '%s' AND p.storeid = '%s';",product,storeid);
         esql.executeQueryAndPrintResult(query);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }
   public static void updateUserAdmin(Retail esql) {
      try {
         System.out.print("\tPlease Enter the name of the User that you would like to update: $");
         String userName = in.readLine();
         System.out.print("\tPlease Enter the ID of the user that you would like to update: $");
         String userID = in.readLine();
         System.out.print("\tPlease Enter the new role of the user: ");
         String typeOfUser = in.readLine();
         System.out.print("\tPlease Enter the new password of the user: ");
         String password = in.readLine();
         String updateUser = String.format("UPDATE users set type = '%s', password = '%s' WHERE users IN (select u from users u where u.name = '%s' and u.userid = '%s');",typeOfUser,password,userName,userID);
         esql.executeUpdate(updateUser);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }
   public static void viewAllUsers(Retail esql) {
      try {
         String viewAllUsers = String.format("select u.type,u.userid,u.name from users u ORDER by type ASC;");
         esql.executeQueryAndPrintResult(viewAllUsers);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }
   public static void updateProductAdmin(Retail esql) {
      try {
         System.out.print("\tPlease Enter the name of the Product that you would like to update: $");
         String productName = in.readLine();
         System.out.print("\tPlease Enter the store id of the Product that you would like to update: $");
         String storeID = in.readLine();
         System.out.print("\tPlease Enter the number of unites that you would like to update it to: $");
         String numberOfUnits = in.readLine();
         System.out.print("\tPlease Enter the new price per unit: $");
         String pricePerUnit = in.readLine();
         String updateProduct = String.format("UPDATE product set numberofunits = numberofunits + '%s', priceperunit = '%s' WHERE storeid = '%s' and productname = '%s'; ",numberOfUnits,pricePerUnit,storeID,productName);
         esql.executeUpdate(updateProduct);
         String productUpdateInsert = String.format("insert into productupdates (updatenumber,managerid,storeid,productname,updatedon) VALUES( DEFAULT, '%s','%s','%s',now()::timestamptz(0));",Retail.getUserID(),storeID,productName);
         esql.executeUpdate(productUpdateInsert);
      }
      catch (Exception e) {
         System.err.println(e.getMessage());
      }	
   }

   public static void setUser(String name, List<String> id){
      user_name =  name;
      user_id = id.get(0);
   }

   public static  String getName(){
      return user_name;
   }

   public static  String getUserID(){
      return user_id;
   }

}//end Retail

