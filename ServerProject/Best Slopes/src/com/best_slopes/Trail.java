package com.best_slopes;

import java.util.Date;
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
	private String id;	

	@Persistent 
	private String owner_id;
	
	@Persistent
	private String title;

	@Persistent
	private Integer rating;

	@Persistent
	private Integer difficulty;		
	
	@Persistent
	private Text comment;
	
	@Persistent
	private Date modified;

	/**** Get functions ****/
	
	public String getId() {
		return id != null ? id : "";
	}

	public String getOwnerID(){
		return owner_id != null ? owner_id : "";
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

	public Date getLastModified() {
		return modified;
	}

	/**** Set functions ****/
	
	public void setId(String id) {
		this.id = new String(id != null ? id : "");
	}

	public void setOwnerID(String own_id){
		this.owner_id = new String(own_id != null ? own_id : "");
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

	public void setLastModified(Date modified) {
		this.modified = modified != null ? modified : new Date();
	}

	/**** Miscellaneous functions ****/
	
	public static Trail load(long id, PersistenceManager pm) {
		return pm.getObjectById(Trail.class, id);
	}

	@SuppressWarnings("unchecked")
	public static List<Trail> loadAll(PersistenceManager pm) {
		Query query = pm.newQuery(Trail.class);
		query.setOrdering("id");

		List<Trail> rv = (List<Trail>) query.execute();
		rv.size(); // forces all records to load into memory
		query.closeAll();
		return rv;
	}

	@SuppressWarnings("unchecked")
	public static List<Trail> getModifiedSince(long age, PersistenceManager pm) {
		Long since = System.currentTimeMillis() - age * 1000;	//converts age to seconds
		Date since_date = new Date(since);
		
		Query query = pm.newQuery(Trail.class, "modified >= :since_date");
		query.setOrdering("modified");
		
		List<Trail> rv = (List<Trail>) query.execute(since_date);
		rv.size(); // forces all records to load into memory
		query.closeAll();
		return rv;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Trail> loadOwnersTrails(String owner_id_key, PersistenceManager pm) {
		//Queries on all trails with specified owner_id
		Query query = pm.newQuery(Trail.class, "owner_id == :owner_id_key");
		
		List<Trail> rv = (List<Trail>) query.execute(owner_id_key);
		rv.size(); // forces all records to load into memory
		query.closeAll();
		return rv;
	}
}
