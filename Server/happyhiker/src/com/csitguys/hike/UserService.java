package com.csitguys.hike;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.*;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@WebServlet("/user/*")
public class UserService extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL="jdbc:mysql://localhost/happyhiker";
    static final String USER = "test";
    static final String PASS = "";
   //	 static String str = DriverUtilities.makeURL("localhost", "happyhiker", "vendor");
    
    {
    	try {
    		
		// Register JDBC driver
         Class.forName("com.mysql.jdbc.Driver");
         
    	} catch (Exception e){
    		e.printStackTrace();
    	}

    }
    public UserService(){
    	super();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
    	response.setContentType("text/plain"); 
		String id = parseURL( request.getRequestURI()) ;
		PrintWriter out = response.getWriter();
		//out.println(id);
		int index = id.indexOf(':');
		//out.println(Integer.toString(index));
		String emailAddress = null;
		String password = null;
		if(index>=0){
			emailAddress = id.substring(0,index);
			password = id.substring(index+1);
		}
		//out.println(emailAddress + "       " + password);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		if (emailAddress != null) {
			User user = getUser(emailAddress, password);
			if (user == null){
				//out.println("the user is null");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			else {
				//out.println("user is not null");
				gson.toJson(user, out);
			}
		} else {
			//
		}
		out.close();
		
    }
    @Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// insert a new User
		InputStreamReader in = new InputStreamReader(request.getInputStream());
		response.setContentType("text/plain");
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		User user = gson.fromJson(in,  User.class);
		int responseCode = insertUser(user);
		if(responseCode<1){
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		}
	}
    private User getUser(String emailAdress, String password){
    	
    	Connection conn = null;
    	try {
    		conn = getConnection();
    		Statement stmnt =  conn.createStatement();
    		String sql = "SELECT * FROM user WHERE user.user_email_add = '" + emailAdress + "'" ;
    		ResultSet rs = stmnt.executeQuery(sql);
    		User user = null;
    	
    		while(rs.next()){
    			user = new User();
    			user.id = rs.getInt("user_id");
    			user.emailAddress = rs.getString("user_email_add");
    			user.password = rs.getString("user_pw");
    			user.userName = rs.getString("user_name");
    		}
    		rs.close();
    		conn.commit();
    		conn.close();
    		if(user.password.compareTo(password)==0){
    			user.password = "good";
    		}else
    			user.password = "bad"; 
    		return user;
    	}catch (Exception e){
    		e.printStackTrace();
    		User userErr = null;
    		return userErr;
    	}
		
    }
    private int insertUser(User user){
		Connection conn = null;
		int responseCode = -1;
		try {
			 conn = getConnection();
	         // Execute SQL query
			 responseCode = insertUser(conn, user);
	         conn.commit();
	         conn.close();
		} catch (Exception e ){
			e.printStackTrace();
		} 
		return responseCode;
	}
	
	private int insertUser(Connection conn, User user){
		int responseCode = -1;
		try {
	         // Execute SQL query
	         Statement stmt = conn.createStatement();
	         String sql = "INSERT INTO user (`user_id`, `user_email_add`, `user_pw`, `user_name`)"
	        		+ "VALUES (NULL, '" + user.emailAddress + "', '"+ user.password + "', '" 
	        		+ user.userName + "')";
	         responseCode = stmt.executeUpdate(sql);
		} catch (Exception e ){
			e.printStackTrace();
		} 
		return responseCode;
	}
	
    private Connection getConnection() throws Exception {

        // Open a connection
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        conn.setAutoCommit(false); 
        return conn;
	}
    private String parseURL(String url) throws ServletException {
		StringTokenizer st = new StringTokenizer(url, "/"); 
		st.nextToken();  // skip over project name
		st.nextToken();  // skip over "user"  
		if (st.hasMoreTokens()){
			// get the {id} convert to int.
			return  st.nextToken();
		} else {
		   return null;  // there is no {id} 
		}
	}
}
