package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import org.mortbay.log.Log;

import com.google.appengine.api.datastore.Query.FilterOperator;

import javax.jdo.Query;

@SuppressWarnings("serial")
public class TrackTrailsServlet extends HttpServlet {
	static String ID_SEPERATOR = "___";
	
	/***** doPost *****/
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		try {
//			long owner_id;
			Integer delete_trail;

			try {
				delete_trail = Integer.parseInt(req.getParameter("delete_trail") + "");
			} catch (NumberFormatException nfe) {
				delete_trail = -1;
			}

//			try {
//				owner_id = Long.parseLong(req.getParameter("owner_id") + "");
//			} catch (NumberFormatException nfe) {
//				owner_id = -1L;
//			}
			
			String 	id = req.getParameter("id"); 
			String 	owner_id = req.getParameter("owner_id"); 
			String[] split_array = id.split(ID_SEPERATOR);		//
			String parsed_id = split_array[1];
			
//			Log.debug(parsed_id);
			Integer rating = Integer.parseInt(req.getParameter("rating") + "");
			Integer difficulty = Integer.parseInt(req.getParameter("difficulty") + "");
			String 	title = req.getParameter("title");
			String 	comment = req.getParameter("comment");
			


			if (delete_trail < 0){
				out.write("Delete was null");
			}
			
			if (id == null || id.length() == 0)
				throw new IllegalArgumentException("Invalid trail id");
			if (Integer.parseInt(parsed_id) < 0)
				throw new IllegalArgumentException("Invalid trail id, currently negative");
			if (owner_id == null || owner_id.length() == 0)
				throw new IllegalArgumentException("Invalid owner id");
			if (rating < 0){
				throw new IllegalArgumentException("Invalid rating value");
			}
			if (difficulty < 0){
				throw new IllegalArgumentException("Invalid difficulty value");
			}
			if (title == null || title.length() == 0)
				throw new IllegalArgumentException("Invalid course title");
			if (comment == null || comment.length() == 0)
				throw new IllegalArgumentException("Invalid course comment");

			Trail trail = new Trail();
			
			//deletes trail if specified
			if(delete_trail == 1){
				Query q = pm.newQuery(Trail.class);
				q.setFilter("id == idParam");
				q.declareParameters("String idParam");
				q.deletePersistentAll(id);
				
			}
			else{
				trail.setId(id);
				trail.setOwnerID(owner_id);
				trail.setTitle(title);
				trail.setRating(rating);
				trail.setDifficutly(difficulty);
				trail.setComment(comment);
				pm.makePersistent(trail);
			}
			out.write(formatAsJson(trail));
		} catch (IllegalArgumentException iae) {
			out.write(UtilJson.toJsonPair("errormsg", iae.getMessage()));
		} finally {
			pm.close();
		}
	}
	
	/***** doGet *****/
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		try {
//			long id = 	getLong(req, "id", -1); 			// for getting one item
			String owner_id = req.getParameter("owner_id");	//for getting all trails from one owner

//			if (id > 0) {
//				Trail trail = Trail.load(id, pm);
//				out.write(formatAsJson(trail));
//			} 
			if (owner_id != null){
				List<Trail> trails = Trail.loadOwnersTrails(owner_id, pm);
				streamAsJson(out, trails);
			}
			else {
				List<Trail> trails = Trail.loadAll(pm);
				streamAsJson(out, trails);
			}
		} catch (JDOObjectNotFoundException ex) {
			out.write(UtilJson.toJsonPair("errormsg", "trail not found"));
		} finally {
			pm.close();
		}
	}
	
//	private long getLong(HttpServletRequest req, String key, long dflt) {
//		long rv;
//		try {
//			rv = Long.parseLong(req.getParameter(key) + "");
//		} catch (NumberFormatException nfe) {
//			rv = dflt;
//		}
//		return rv;
//	}
	
	public static String formatAsJson(Trail trail) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("id", trail.getId());
		obj.put("owner_id", trail.getOwnerID());
		obj.put("title", trail.getTitle());
		obj.put("rating", Integer.toString(trail.getRating()));
		obj.put("difficulty", Integer.toString(trail.getDifficulty()));
		obj.put("comment", trail.getComment());
		return UtilJson.toJsonObject(obj);
	}

	private void streamAsJson(PrintWriter out, List<Trail> trails) {
		// you could also have GSON do this for you, if you like
		out.write('[');
		for (int i = 0; i < trails.size(); i++) {
			if (i > 0)
				out.write(',');
			out.write(formatAsJson(trails.get(i)));
		}
		out.write(']');
	}

}
