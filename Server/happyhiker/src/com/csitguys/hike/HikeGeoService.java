package com.csitguys.hike;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@WebServlet("/geohike/*")
public class HikeGeoService extends HttpServlet{

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
	public HikeGeoService(){
		super();
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		response.setContentType("text/plain");
		String id = parseURL( request.getRequestURI());
		int index = id.indexOf('&');
		int indexFetchOneHike = id.indexOf('$');
		boolean fetchOneHIke= false;
		boolean getCoords = false;

		LatLong latlng;
		PrintWriter out = response.getWriter();
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		//gets a single hike detail	
		if(indexFetchOneHike>=0){
			StringTokenizer st = new StringTokenizer(id,"$");
			
			LatLong start = getLatLong(st.nextToken());
			LatLong end = getLatLong(st.nextToken());
			Hike hike = getHike(start, end);
			gson.toJson(hike,out);

		}else{
			//returns multiple hikes either as a list of geo points
			if(index>=0){
				latlng = getLatLong(id.substring(0, index));
				getCoords = true;
			} else {
				latlng = getLatLong(id);
			}

			
			HikeList hikes  = getAllHikes(latlng.lat, latlng.lng);
			
			HikesLists hikeslists = new HikesLists();
			hikeslists.list = new ArrayList<>();
				
			if(getCoords){
				
				for(Hike h : hikes.list){
					
					HikeGeoList hkg = getPoints(h);
					hikeslists.list.add(hkg);
				}
				gson.toJson(hikeslists,out);
			}else{
				gson.toJson(hikes,out);
			}
		}
		out.close();
	}

	private LatLong getLatLong(String str){

		LatLong l = new LatLong();
		int index = str.indexOf(':');
		if(index>=0){
			l.lat = Double.parseDouble(str.substring(0,index));
			l.lng = Double.parseDouble(str.substring(index+1));
		}
		return l;

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
		st.nextToken();  // skip over "hik"  
		if (st.hasMoreTokens()){
			// get the {id} convert to int.
			return  st.nextToken();
		} else {
			return null;  // there is no {id} 
		}
	}
	private Hike getHike(LatLong start, LatLong end){
		//logic is that a hike is unique if it has a unique start and end
		try{
			String sql = "SELECT hike_id FROM hikecoord where X(hikecoord_geo) = " + start.lat + " AND Y(hikecoord_geo) = " + start.lng;
			
			Connection conn = getConnection();
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery(sql);
			Hike hike = new Hike();
			int id  =0;
			while(rs.next()){
				id = rs.getInt("hike_id");
				

			}
			rs.close();
			conn.commit();
			conn.close();
			Connection conn2 = getConnection();
			Statement stmnt2 = conn2.createStatement();
			String sql2 = "Select * FROM hikes WHERE hike_id = " +id;
			ResultSet rs2 = stmnt2.executeQuery(sql2);
			while(rs2.next()){
				hike.id = rs2.getInt("hike_id");
				hike.name = rs2.getString("hike_name");
				hike.difficulty = rs2.getInt("hike_difficulty");
				hike.rating = rs2.getInt("hike_rating") ;
			}
			
			rs2.close();
			conn2.commit();
			conn2.close();
			return hike;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}	
	}
	private HikeList getHikes(double lat,double lng){

		Connection conn = null;
		try{
			conn = getConnection();
			Statement stmnt = conn.createStatement();
			String sql = "SELECT * FROM hikes";
			ResultSet rs = stmnt.executeQuery(sql);
			HikeList hl = new HikeList();
			hl.list = new ArrayList<>(); 
			while(rs.next()){
				Hike hike = new Hike();
				hike.id = rs.getInt("hike_id");
				hike.name = rs.getString("hike_name");
				//hike.start_latlng = (LatLng) rs.getObject("hike_start");
				//hike.end_latlng = 
				hike.difficulty = rs.getInt("hike_difficulty");
				hike.rating = rs.getInt("hike_rating") ;
				hl.list.add(hike);

			}
			rs.close();
			conn.commit();
			conn.close();
			return hl;

		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		//return hike;
	}


	//Returns all hikes within a given radius of a Lat Long point
	private HikeList getAllHikes(double lat, double lng){

		String sql = "SELECT hike_id, hike_start,hike_difficulty,hike_rating,hike_end , hike_name, r," +
				"units * DEGREES( ACOS(" +
				"COS(RADIANS(latpoint))" + 
				"* COS(RADIANS(X(hike_start)))" +
				"* COS(RADIANS(longpoint) - RADIANS(Y(hike_start)))" +
				"+ SIN(RADIANS(latpoint))" +
				"* SIN(RADIANS(X(hike_start))))) AS distance" +
				" FROM hikes JOIN (SELECT "+ lat + " AS latpoint, " + lng +" AS longpoint, " +
				"30.0 AS r, 69.0 AS units) AS p ON (1=1)" +
				"WHERE MbrContains(GeomFromText (" +
				"CONCAT('LINESTRING(',latpoint-(r/units),' '," +
				"longpoint-(r /(units* COS(RADIANS(latpoint)))),',', latpoint+(r/units) ,' ',"+
				"longpoint+(r /(units * COS(RADIANS(latpoint)))),')')),  hike_start)";

		try{
			Connection conn = getConnection();
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery(sql);
			HikeList hl = new HikeList();
			hl.list = new ArrayList<>(); 
			while(rs.next()){
				Hike hike = new Hike();
				hl.list.add(hike);
				hike.id = rs.getInt("hike_id");
				hike.name = rs.getString("hike_name");
				hike.difficulty = rs.getInt("hike_difficulty");
				hike.rating = rs.getInt("hike_rating") ;

			}
			rs.close();
			conn.commit();
			conn.close();
			return hl;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}	
	}
	private HikeGeoList getPoints(Hike h){
		String sql = "SELECT X(hikecoord_geo) AS lat, Y(hikecoord_geo) AS lng FROM hikecoord WHERE hike_id = "+h.id+" ORDER BY hikecoord_order ASC";
		try{
			Connection conn = getConnection();
			Statement stmnt = conn.createStatement();
			ResultSet rs = stmnt.executeQuery(sql);
			HikeGeoList hgl = new HikeGeoList();
			hgl.list = new ArrayList<>();
			while(rs.next()){
				LatLong l = new LatLong();
				l.lat = rs.getDouble("lat");
				l.lng = rs.getDouble("lng");
				hgl.list.add(l);
			}
			rs.close();
			conn.commit();
			conn.close();
			return hgl;
		} catch (Exception e){
			return null;
		}
	}
	private String getHikeSQL(LatLong start, LatLong end){
		String sql = "SELECT * FROM hikes WHERE X(hike_start) = " + start.lat + 
				" AND Y(hike_start) = " + start.lng + " AND X(hike_end) = " + end.lat + 
				" AND Y(hike_end) = " + end.lng;
		return sql;
	}



}