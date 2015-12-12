

import java.sql.*;

public class UserTest {
	
	//private static final long serialVersionUID = 1L;
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL="jdbc:mysql://localhost/happyhiker";
    static final String USER = "test";
    static final String PASS = "";
    
    public static void main(String[] Args) {
    	try {
    		
		// Register JDBC driver
         Class.forName("com.mysql.jdbc.Driver").newInstance();
         
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	Connection conn = null;
    	
    	try{
    		conn = DriverManager.getConnection("jdbc:mysql://localhost/happyhiker"
    				+ "user=root&password=win4dowu");
    		Statement stmnt =  conn.createStatement();
    		String sql = "SELECT * FROM user";
    		ResultSet rs = stmnt.executeQuery(sql);
    	}catch(SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	}

    }
}
