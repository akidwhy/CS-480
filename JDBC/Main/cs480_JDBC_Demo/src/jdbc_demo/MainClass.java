package jdbc_demo;

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//imports
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//main class
public class MainClass {
	//start of main method
	public static void main(String args[]) throws Exception {
		//calls the createTable fucntion which generates tables asa desired
		createTable();

		//gets the location of the transfile
		//this must be changed based off of the location of that file. In my case I had it on my desktop
		String filepath = "C:\\Users\\akidw\\Desktop\\transfile.txt";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader bReader = null;
		
		try {
			//establishes connection with db
			Connection con = getConnection();

			fis = new FileInputStream(filepath);
			isr = new InputStreamReader(fis);
			bReader = new BufferedReader(isr);

			String line = null;
			String[] strProduct = null;

			//this line skips the first line of the text file which is *****transfile***** and this is not needed
			bReader.readLine();

			while (true) {
				//everytime this loops through, it creates a new array list
				ArrayList<String> listResult = new ArrayList<String>();
				//sets the line in the text file to the String line
				line = bReader.readLine();
				//checks if the textfile is empty or at the end
				if (line == null || line.isEmpty()) {
					//if it is then exit the while loop
					break;
				}
				else {
					//create a temp string that will hold the line from the text file
					String tempLine = line;
					//this puts each word from the line into a String array
					String[] words = tempLine.split("\\s+");
					strProduct = line.split(" ");
					//set the number of words to equal length of the string
					int lengthofLine = strProduct.length;
					//itterator created for the use of for loops
					int i;
					//this for loop creates an array list of size lenghtofLine and puts each string from strProduct into it. May not be necessary
					for (i = 0; i < lengthofLine; i++) {
						listResult.add(i, strProduct[i]);
					}

					//based off of the size of the array list, there can only be specific transaction codes
					//since we do not need to check the validity of the content of the transfile, this is okay
					//if the size of the list is 1, then the only possible transaction codes are 3 and 6
					if (listResult.size() == 1) {
						//if the transaction code is 3, simply get the average using a query statement
						if (Integer.parseInt(listResult.get(0)) == 3) {
							Statement st = con.createStatement();
							st = con.createStatement();
							ResultSet rs = st.executeQuery("SELECT AVG(price) FROM parts");
							//this prints out the average
							while (rs.next()) {
								String name = rs.getString(1);
								System.out.println("Average price:" + name);
							}
						}
					}

					//if the size of the list is 2, then possible transaction codes are 1, 4, and 5
					if (listResult.size() == 2) {
						//if the transaction code is 1, then first check to see if there exists an exact pid in parts
						if (Integer.parseInt(listResult.get(0)) == 1) {
							int pid = Integer.parseInt(listResult.get(1));
							PreparedStatement statement = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + pid + "' ");
							//set the resulting values into a ResultSet
							ResultSet result = statement.executeQuery();
							//if the resultset is empty, this means that the pid does not exist and there is nothing to delete
							if(!result.next()){
								System.out.println("error");
							}
							//else, if it does exist, then delete the pid from, wherever it is seen
							else{
								PreparedStatement st = con.prepareStatement("DELETE FROM parts WHERE pid = '" + pid + "';");
								PreparedStatement st2 = con.prepareStatement("DELETE FROM subpart_of WHERE pid = '" + pid + "';");
								PreparedStatement st3 = con.prepareStatement("DELETE FROM subpart_of WHERE pmid = '" + pid + "';");
								//must delete the table subpart_of first because it has a foreign key of parts
								st3.executeUpdate();
								st2.executeUpdate();
								st.executeUpdate();
								System.out.println("done");
							}
						}
						//transaction code 4
						//NOTE: this gets the name of the immediate subpart only and not the subpart of the immediate subparts and so on
						if(Integer.parseInt(listResult.get(0)) == 4){
							//get the pid that we're searching for
							int pid = Integer.parseInt(listResult.get(1));
							//get all the pid from subpart_of where pmid equals the pid from the transfile
							PreparedStatement statement = con.prepareStatement("SELECT pid FROM subpart_of" + " WHERE pmid = '" + pid + "' ");
							ResultSet result = statement.executeQuery();
							//if the result set has something then get the name of the pid and print it out
							while(result.next()){
								PreparedStatement statement2 = con.prepareStatement("SELECT pid, pname FROM parts" + " WHERE pid = '" + result.getInt("pid") + "' ");
								ResultSet result2 = statement2.executeQuery();
								while(result2.next()){
									System.out.print(result2.getString(2) + " ");
								}
							}
							//creates a new line
							System.out.println();
						}

						//transaction code 5
						//NOTE: this gets the average of only the immediate subpart and not the subpart of the subparts and so on
						if(Integer.parseInt(listResult.get(0)) == 5){
							//gets the pid that we need to get the average of all subparts of the pid
							int pid = Integer.parseInt(listResult.get(1));
							PreparedStatement statement = con.prepareStatement("SELECT pid FROM subpart_of" + " WHERE pmid = '" + pid + "' ");
							ResultSet result = statement.executeQuery();
							//innitialize variables to hold total count and size
							float count = 0;
							float size = 0;
							//AVG query statement is not really working so i had to manually get the average
							while(result.next()){
								PreparedStatement statement2 = con.prepareStatement("SELECT AVG(price) FROM parts" + " WHERE pid = '" + result.getInt(1) + "' ");
								ResultSet rs = statement2.executeQuery();
								if(rs.next()){
									count = count + rs.getFloat(1);
									size++;
								}
							}
							//manually get the average
							System.out.println("Average price of parts that are subparts of the given pid: " + count/size);
						}
					}

					//if the size of the list is 4, this means that the transaction code can only be a 2
					if (listResult.size() == 4) {
						//the transaction code is 2
						if (Integer.parseInt(listResult.get(0)) == 2) {
							String temp = listResult.get(1);
							//get all pid from parts where the pid equals the pid that is given
							PreparedStatement statement = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + temp + "' ");
							ResultSet result = statement.executeQuery();
							//makes sure that there isnt a duplicate
							if(!result.next()){
								PreparedStatement inserted = con.prepareStatement("INSERT INTO parts (pid, pname, price) VALUES ('" + listResult.get(1) + "', '" + listResult.get(2) + "', '" + listResult.get(3) + "')");
								inserted.executeUpdate();
								System.out.println("done1");
							}
							//or else create another prepared statement
							//NOTE: this may be redundant and not needed, i forgot my chain of thought for this part but it works :)
							else {
								PreparedStatement statement2 = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + temp + "' ");
								ResultSet result2 = statement2.executeQuery();
								//if there is something in the reult2
								while (result2.next()) {
									String temp2 = (result2.getString("pid"));
									//this does something and im not sure what it does and why
									if (!temp2.equals(temp)) {
										PreparedStatement inserted = con.prepareStatement("INSERT INTO parts (pid, pname, price) VALUES ('" + listResult.get(1) + "', '" + listResult.get(2) + "', '" + listResult.get(3) + "')");
										inserted.executeUpdate();
										System.out.println("done2");
									} else {
										System.out.println("error3");
									}
								}
							}

						}
					}
					//this checks if the input line has five or more items in the line
					if (listResult.size() >= 5) {
						//the transaction code can only be a 2
						if (Integer.parseInt(listResult.get(0)) == 2) {
							String temp = listResult.get(1);
							PreparedStatement statement = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + temp + "' ");
							ResultSet result = statement.executeQuery();
							//first checks if there is a duplicate pid, if there isnt then continue on
							if(!result.next()){
								PreparedStatement inserted = con.prepareStatement("INSERT INTO parts (pid, pname, price) VALUES ('" + listResult.get(1) + "', '" + listResult.get(2) + "', '" + listResult.get(3) + "')");
								inserted.executeUpdate();
								//this does something were it checks the mid and makes sure that there is a pid in the parts table before inserting into the subpart_of table
								for (i = 4; i < listResult.size(); i++) {
									String temp2 = listResult.get(i);
									PreparedStatement statement3 = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + listResult.get(i) + "' ");
									ResultSet result3 = statement3.executeQuery();
									//if the mid is not a pid in parts table then check the next inputted mid
									if(!result3.next()){
										continue;
									}
									//insert the mid and pid into the subpart_of
									PreparedStatement inserted2 = con.prepareStatement("INSERT INTO subpart_of (pid, pmid) VALUES ('" + listResult.get(1) + "', '" + listResult.get(i) + "')");
									inserted2.executeUpdate();
								}
								System.out.println("done3");
							}
							//again im not sure what this part is exactly doing. I believe it is needed and also it works
							else{
								PreparedStatement statement2 = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + temp + "' ");
								ResultSet result2 = statement2.executeQuery();

								while (result2.next()) {
									String temp2 = (result2.getString("pid"));
									if (!temp2.equals(temp)) {
										PreparedStatement inserted = con.prepareStatement("INSERT INTO parts (pid, pname, price) VALUES ('" + listResult.get(1) + "', '" + listResult.get(2) + "', '" + listResult.get(3) + "')");
										inserted.executeUpdate();
										for (i = 4; i < listResult.size(); i++) {
											String temp3 = listResult.get(i);
											PreparedStatement statement4 = con.prepareStatement("SELECT pid FROM parts" + " WHERE pid = '" + listResult.get(i) + "' ");
											ResultSet result4 = statement4.executeQuery();

											if(!result4.next()){
												continue;
											}
											PreparedStatement inserted2 = con.prepareStatement("INSERT INTO subpart_of (pid, pmid) VALUES ('" + listResult.get(1) + "', '" + listResult.get(i) + "')");
											inserted2.executeUpdate();
										}
										System.out.println("done4");
									}
									else {
										System.out.println("error4");
									}
								}
							}
						}
					}
				}
			}
			//This entire chunck of code is for dropping tables
			Statement stmt = null;
			stmt = con.createStatement();
			String sql = "DROP TABLE parts";
			String sql2 = "DROP TABLE subpart_of";
			stmt.executeUpdate(sql2);
			stmt.executeUpdate(sql);
		}
		catch(Exception e) {
			System.out.println("Read file error");
			e.printStackTrace();
		}finally {
			//closes everything
			try {
				bReader.close();
				isr.close();
				fis.close();
			}catch(IOException e){
				e.printStackTrace();
			}	
		} 

	}//end main


//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
	//this fucntion creates tables and does not return anything
	private static void createTable() throws Exception{
		try {
			//establishes the connection
			Connection con = getConnection();
			//creates the table pid
			PreparedStatement createPartsTable = con.prepareStatement("CREATE TABLE IF NOT EXISTS parts(pid int NOT NULL, pname char(20), price int, PRIMARY KEY(pid))");
			//executes the statement to acctually create it
			createPartsTable.executeUpdate();
			//creates the table subpart_of
			PreparedStatement createSubpartofTable = con.prepareStatement("CREATE TABLE IF NOT EXISTS subpart_of(pid int, pmid int, FOREIGN KEY(pid) REFERENCES parts(pid))");
			//executes the creation of that table
			createSubpartofTable.executeUpdate();
			
		}//end try
		finally {
			System.out.println("Function Complete");
		}//end finally
	}//end createTable


//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
	//this function starts the connection with a database and returns that connection
	private static Connection getConnection() throws Exception{
		try {
			//calls the jdbc driver
			String driver = "com.mysql.jdbc.Driver";
			//sets the database connection address
			String url = "jdbc:mysql://localhost:3306/transaction?useSSL=false";
			//sets the username
			String username = "root";
			//sets the password
			String password = "037907";
			Class.forName(driver);
			//creates the connection
			Connection con = DriverManager.getConnection(url, username, password);
			return con;
		}//end try
		catch (Exception e) {
			System.out.println(e);
		}//end catch
		return null;
	}//end getConnection()

}//end MainClass