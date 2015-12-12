package com.csitguys.hike;

import java.io.IOException;
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
		//int id = 1;
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		PrintWriter out = response.getWriter();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		if (id != null) {
			User user = getUser(id);
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
    private User getUser(String emailAdress){
    	
    	Connection conn = null;
    	try {
    		conn = getConnection();
    		Statement stmnt =  conn.createStatement();
    		String sql = "SELECT * FROM user WHERE user.user_email_add = '" + emailAdress + "'" ;
    		ResultSet rs = stmnt.executeQuery(sql);
    		User user1, user2 = null;
    		user1 = new User();
			user1.id = 1;
			user1.emailAddress = "connection";
			user1.password = "connection";
			user1.userName = "connection";
    		while(rs.next()){
    			user2 = new User();
    			user2.id = rs.getInt("user_id");
    			user2.emailAddress = rs.getString("user_email_add");
    			user2.password = rs.getString("user_pw");
    			user2.userName = rs.getString("user_name");
    		}
    		rs.close();
    		conn.commit();
    		conn.close();
    		if(user2==null)
    			return user1;
    		else
    			return user2;
    	}catch (Exception e){
    		e.printStackTrace();
    		User user = new User();
    		user.id = 3;
    		user.emailAddress = e.getMessage();
    		user.userName = ((SQLException) e).getSQLState();
    		user.password = Integer.toString(((SQLException) e).getErrorCode());
    		return user;
    	}
		
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
