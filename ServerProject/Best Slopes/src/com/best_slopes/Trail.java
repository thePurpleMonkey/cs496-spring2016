package com.best_slopes;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class Trail {
	@PrimaryKey
	private String id;				//change to string

	@Persistent 
	private Long owner_id;
	
	@Persistent
	private String title;

	@Persistent
	private Integer rating;

	@Persistent
	private Integer difficulty;		
	
	@Persistent
	private Text comment;
	


	public String getId() {
		return id != null ? id : "";
	}

	public long getOwnerID(){
		return owner_id != null ? owner_id.longValue() : -1L;
	}

	public String getTitle() {
		return title != null ? title : "";
	}

	public int getRating() {
		return rating!= null ? rating.intValue() : -1;
	}
	
	public int getDifficulty(){
		return difficulty != null ? difficulty.intValue() : -1;
	}
	
	public String getComment() {
		return comment != null ? comment.getValue() : "";
	}

	public void setId(String id) {
		this.id = new String(id != null ? id : "");
	}

	public void setOwnerID(long id){
		this.owner_id = id;
	}
	
	public void setTitle(String title) {
		this.title = title != null ? title : "";
	}

	public Integer setRating(int rating) {
		return this.rating = rating;
	}
	
	public void setDifficutly(int diff){
		this.difficulty = diff;
	}
	public void setComment(String comment) {
		this.comment = new Text(comment != null ? comment : "");
	}

	public static Trail load(long id, PersistenceManager pm) {
		return pm.getObjectById(Trail.class, id);
	}

	@SuppressWarnings("unchecked")
	public static List<Trail> loadAll(PersistenceManager pm) {
		Query query = pm.newQuery(Trail.class);
		query.setOrdering("id");

		List<Trail> rv = (List<Trail>) query.execute();
		rv.size(); // forces all records to load into memory
		return rv;
	}

	@SuppressWarnings("unchecked")
	public static List<Trail> loadOwnerTrails(long id, PersistenceManager pm) {
		//Queries on all trails with specified owner_id
		Query query = pm.newQuery(Trail.class, "owner_id == :id");
		
		List<Trail> rv = (List<Trail>) query.execute(id);
		rv.size(); // forces all records to load into memory
		query.closeAll();
		return rv;
	}
}
