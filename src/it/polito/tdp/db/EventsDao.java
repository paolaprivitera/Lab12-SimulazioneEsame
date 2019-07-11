package it.polito.tdp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.model.Event;


public class EventsDao {
	
	public List<Event> listAllEventsByDate(Integer anno, Integer mese, Integer giorno){
		String sql = "SELECT * FROM events WHERE Year(reported_date) = ? "
				+ "AND Month(reported_date) = ? AND Day(reported_date) = ?" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			st.setInt(2, mese);
			st.setInt(3, giorno);
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Year> getYears() {
		String sql = "SELECT DISTINCT YEAR(reported_date) AS year " + 
				"FROM EVENTS " + 
				"GROUP BY year";
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Year> years = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					years.add(Year.of(res.getInt("year")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return years;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}

	}

	public List<Integer> getVertici(Year anno) {
		String sql = "SELECT DISTINCT district_id " + 
				"FROM EVENTS " +
				"WHERE YEAR(reported_date) = ?";
		List<Integer> distretti = new LinkedList<Integer>();
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					distretti.add(res.getInt("district_id"));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return distretti;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public Double getLatMedia(Year anno, Integer v1) {
		
		String sql = "SELECT AVG(geo_lat) AS lat " + 
				"FROM EVENTS " + 
				"WHERE YEAR(reported_date) = ? AND district_id = ?";
		
		double mediaLat = 0.0;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			st.setInt(2, v1);
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					mediaLat = res.getDouble("lat");
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return mediaLat;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public Double getLonMedia(Year anno, Integer v1) {
		
		String sql = "SELECT AVG(geo_lon) AS lon " + 
				"FROM EVENTS " + 
				"WHERE YEAR(reported_date) = ? AND district_id = ?";
		
		double mediaLon = 0.0;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno.getValue());
			st.setInt(2, v1);
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					mediaLon = res.getDouble("lon");
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return mediaLon;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public Integer getDistrettoMin(Integer anno) {
		String sql = "SELECT district_id, COUNT(*) AS cnt " + 
				"FROM EVENTS " + 
				"WHERE YEAR(reported_date) = ? " + 
				"GROUP BY district_id " + 
				"ORDER BY cnt asc " + 
				"LIMIT 1";
		int distrettoMin = 0;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			ResultSet res = st.executeQuery() ;
			
			if(res.next()) {
				try {
					distrettoMin = res.getInt("district_id");
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return distrettoMin;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}

}
