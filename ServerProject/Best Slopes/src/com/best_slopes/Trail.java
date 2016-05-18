package com.best_slopes;

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
	private Long id;

	@Persistent 
	private Long owner_id;
	
	@Persistent
	private String title;

	@Persistent
	private Text comment;

	@Persistent
	private long modified;

	@Persistent
	private Boolean active;

	public boolean isActive() {
		return active != null ? active.booleanValue() : false;
	}

	public long getOwnerID(){
		return owner_id != null ? owner_id.longValue() : -1L;
	}

	public long getLastModified() {
		return modified;
	}

	public String getComment() {
		return comment != null ? comment.getValue() : "";
	}

	public long getId() {
		return id != null ? id.longValue() : -1L;
	}

	public String getTitle() {
		return title != null ? title : "";
	}

	public void setLastModified(long modified) {
		this.modified = modified;
	}

	public void setComment(String comment) {
		this.comment = new Text(comment != null ? comment : "");
	}

	public void setOwnerID(long id){
		this.owner_id = id;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title != null ? title : "";
	}

	public static Trail load(long id, PersistenceManager pm) {
		return pm.getObjectById(Trail.class, id);
	}

	@SuppressWarnings("unchecked")
	public static List<Trail> loadRecent(long age, PersistenceManager pm) {
		// count back a certain number of seconds
		long since = System.currentTimeMillis() - age * 1000;
		Query query = pm.newQuery(Trail.class, "modified >= :since && active == :ac");
		query.setOrdering("modified");
		List<Trail> rv = (List<Trail>) query.execute(since, true);
		rv.size(); // forces all records to load into memory
		query.closeAll();
		return rv;
	}

	@SuppressWarnings("unchecked")
	public static List<Trail> loadAll(PersistenceManager pm) {
		Query query = pm.newQuery(Trail.class);
		query.setOrdering("id");

		List<Trail> rv = (List<Trail>) query.execute();
		rv.size(); // forces all records to load into memory
		return rv;
	}

}
