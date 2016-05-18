package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TrackTrailsServlet extends HttpServlet {
	/***** doPost *****/
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		try {
			long id;
			try {
				id = Long.parseLong(req.getParameter("id") + "");
			} catch (NumberFormatException nfe) {
				id = -1L;
			}
			String title = req.getParameter("title");
			String comment = req.getParameter("comment");
			String active = req.getParameter("active");

			if (id < 0)
				throw new IllegalArgumentException("Invalid trail id");
			if (title == null || title.length() == 0)
				throw new IllegalArgumentException("Invalid course title");
			if (comment == null || comment.length() == 0)
				throw new IllegalArgumentException("Invalid course comment");

			Trail trail = new Trail();
			trail.setId(id);
			trail.setTitle(title);
			trail.setComment(comment);
			trail.setLastModified(System.currentTimeMillis());
			trail.setActive("1".equals(active) || "true".equalsIgnoreCase(active));
			pm.makePersistent(trail);

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
			long id = getLong(req, "id", -1L); // for getting one item
			long age = getLong(req, "age", -1L); // for getting all modified in
													// a certain # seconds

			if (id > 0) {
				Trail trail = Trail.load(id, pm);
				out.write(formatAsJson(trail));
			} else if (age > 0) {
				List<Trail> trails = Trail.loadRecent(age, pm);
				streamAsJson(out, trails);
			} else {
				List<Trail> trails = Trail.loadAll(pm);
				streamAsJson(out, trails);
			}
		} catch (JDOObjectNotFoundException ex) {
			out.write(UtilJson.toJsonPair("errormsg", "trail not found"));
		} finally {
			pm.close();
		}
	}
	
	private long getLong(HttpServletRequest req, String key, long dflt) {
		long rv;
		try {
			rv = Long.parseLong(req.getParameter(key) + "");
		} catch (NumberFormatException nfe) {
			rv = dflt;
		}
		return rv;
	}
	
	public static String formatAsJson(Trail trail) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("id", Long.toString(trail.getId()));
		obj.put("title", trail.getTitle());
		obj.put("comment", trail.getComment());
		obj.put("modified", Long.toString(trail.getLastModified()));
		obj.put("active", trail.isActive() ? "1" : "0");
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
