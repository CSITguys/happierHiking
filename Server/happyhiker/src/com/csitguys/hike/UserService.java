package com.csitguys.hike;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

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
    static final String USER = "root";
    static final String PASS = "win4dowU";
    
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
		//int id = parseURL( request.getRequestURI()) ;
		int id = 1;
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		PrintWriter out = response.getWriter();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		if (id == 1) {
			User user = getUser("test@test.com");
			if (user == null)
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			else 
				gson.toJson(user, out);
		} else {
			//CrimeList crimes = getAllCrimes();
			//gson.toJson(crimes, out);
		}
		out.close();
		
    }
    private User getUser(String emailAdress){
    	
    	Connection conn = null;
    	try {
    		conn = getConnection();
    		Statement stmnt =  conn.createStatement();
    		String sql = "SELECT * FROM user where user_email_add = " + emailAdress;
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
    		return user;
    	}catch (Exception e){
    		e.printStackTrace();
    		return null;
    	}
		
    }
    private Connection getConnection() throws Exception {

        // Open a connection
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        conn.setAutoCommit(false); 
        return conn;
	}
}
