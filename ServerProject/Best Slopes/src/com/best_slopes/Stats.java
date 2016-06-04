package com.best_slopes;


import javax.jdo.PersistenceManager;
import java.util.List;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Stats {
	@PrimaryKey
	private Long id;	

	@Persistent 
	private Integer trail_count_week;
	
	@Persistent 
	private Integer trail_count_day;
	
	@Persistent 
	private Integer trail_count_hour;
	
	@Persistent 
	private Integer trail_count_total;
	
	/**** Set functions ****/
	
	public void setId(long id){
		this.id = id;
	}
	
	public void setWeekCount(Integer count){
		this.trail_count_week = count;
	}

	public void setDayCount(Integer count){
		this.trail_count_day = count;
	}

	public void setHourCount(Integer count){
		this.trail_count_hour = count;
	}

	public void setTotalCount(Integer count){
		this.trail_count_total = count;
	}
	

	/**** Get functions ****/
	
	public Long getId() {
		return id;
	}

	public Integer getWeekCount() {
		return trail_count_week != null ? trail_count_week.intValue() : 0;
	}

	public Integer getDayCount() {
		return trail_count_day != null ? trail_count_day.intValue() : 0;
	}

	public Integer getHourCount() {
		return trail_count_hour != null ? trail_count_hour.intValue() : 0;
	}

	public Integer getTotalCount() {
		return trail_count_total != null ? trail_count_total.intValue() : 0;
	}


	/**** Miscellaneous functions ****/

	@SuppressWarnings("unchecked")
	public static List<Stats> getStats(PersistenceManager pm) {
		//Queries on all trails with specified owner_id
		Query query = pm.newQuery(Stats.class);
		query.setOrdering("id");

		List<Stats> rv = (List<Stats>) query.execute();
//		rv.size(); // forces all records to load into memory
		
		query.closeAll();
		return rv;
	}

//	@SuppressWarnings("unchecked")
//	public static List<Trail> loadAll(PersistenceManager pm) {
//		Query query = pm.newQuery(Trail.class);
//		query.setOrdering("id");
//
//		List<Trail> rv = (List<Trail>) query.execute();
//		rv.size(); // forces all records to load into memory
//		return rv;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<Trail> getModifiedSince(long age, PersistenceManager pm) {
//		Long since = System.currentTimeMillis() - age * 1000;	//converts age to seconds
//		Date since_date = new Date(since);
//		
//		Query query = pm.newQuery(Trail.class, "modified >= :since_date");
//		query.setOrdering("modified");
//		
//		List<Trail> rv = (List<Trail>) query.execute(since_date);
//		rv.size(); // forces all records to load into memory
//		query.closeAll();
//		return rv;
//	}

}
