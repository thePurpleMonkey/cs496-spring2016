package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class CronJobServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handleRequest(resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handleRequest(resp);
	}

	private void handleRequest(HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			Stats stats = new Stats();
			
			List<Trail> hour_trails = Trail.getModifiedSince(3600, pm);	//3600 seconds = 1 hour
			List<Trail> day_trails = Trail.getModifiedSince(86400, pm);		//86400 seconds = 1 day
			List<Trail> week_trails = Trail.getModifiedSince(604800, pm);	//604800 seconds = 1 week
			List<Trail> total_trails = Trail.loadAll(pm);
			
//			streamAsJsonTrails(out, day_trails);
//			streamAsJsonTrails(out, week_trails);
//			streamAsJsonTrails(out, total_trails);

			int hour_count = 	hour_trails.size();
			int day_count = 	day_trails.size();
			int week_count =	week_trails.size();
			int total_count = 	total_trails.size();
			
			stats.setId(1L);
			stats.setHourCount(hour_count);
			stats.setDayCount(day_count);
			stats.setWeekCount(week_count);
			stats.setTotalCount(total_count);

			pm.makePersistent(stats);

//			streamAsJson(out, stats);
		} finally {
			pm.close();
		}
		resp.getWriter().println("success");
	}

	private void streamAsJson(PrintWriter out, Stats stats) {
		// you could also have GSON do this for you, if you like
		out.println('[');
		out.println(formatAsJson(stats));
		out.println(']');
	}

	public static String formatAsJson(Stats stats) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("id", 			Long.toString(stats.getId()));
		obj.put("hour_count", 	Integer.toString(stats.getHourCount()));
		obj.put("week_count",	Integer.toString(stats.getWeekCount()));
		obj.put("day_count", 	Integer.toString(stats.getDayCount()));
		obj.put("total_count", 	Integer.toString(stats.getTotalCount()));
		return UtilJson.toJsonObject(obj);
	}
	
//	private void streamAsJsonTrails(PrintWriter out, List<Trail> trails) {
//		// you could also have GSON do this for you, if you like
//		out.write('[');
//		for (int i = 0; i < trails.size(); i++) {
//			if (i > 0)
//				out.write(',');
//			out.write(formatAsJsonTrails(trails.get(i)));
//		}
//		out.write(']');
//	}
//
//	public static String formatAsJsonTrails(Trail trail) {
//		HashMap<String, String> obj = new HashMap<String, String>();
//		obj.put("id", trail.getId());
//		obj.put("owner_id", trail.getOwnerID());
//		obj.put("title", trail.getTitle());
//		obj.put("rating", Integer.toString(trail.getRating()));
//		obj.put("difficulty", Integer.toString(trail.getDifficulty()));
//		obj.put("comment", trail.getComment());
//		obj.put("modified", Long.toString(trail.getLastModified() != null ? trail.getLastModified().getTime() : 0L));
//		return UtilJson.toJsonObject(obj);
//	}



}