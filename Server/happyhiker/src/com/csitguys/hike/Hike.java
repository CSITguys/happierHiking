package com.csitguys.hike;

public class Hike {
	int id;
	String name;
	LatLong start_latlng;
	LatLong end_latlng;
	int rating;
	int difficulty;	
	
	public Hike(int id, String name, int rating, int difficulty){
		this.id = id;
		this.name = name;
		this.rating = rating;
		this.difficulty = difficulty;
	}

	public Hike() {
		//do nothing
	}
}
