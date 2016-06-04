package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class StatsServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		handleRequest(resp);
	}

	private void handleRequest(HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();

		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		try {
			List<Stats> stats = Stats.getStats(pm);
			
			streamAsJson(out, stats.get(0));
		} finally {
			pm.close();
		}
	}

	private void streamAsJson(PrintWriter out, Stats stats) {
		// you could also have GSON do this for you, if you like
		out.println('[');
		out.println(formatAsJson(stats));
		out.println(']');
	}

	public static String formatAsJson(Stats stats) {
		HashMap<String, String> obj = new HashMap<String, String>();
		obj.put("stats_id", 	Long.toString(stats.getId()));
		obj.put("hour_count", 	Integer.toString(stats.getHourCount()));
		obj.put("week_count", 	Integer.toString(stats.getWeekCount()));
		obj.put("day_count", 	Integer.toString(stats.getDayCount()));
		obj.put("total_count", 	Integer.toString(stats.getTotalCount()));
		return UtilJson.toJsonObject(obj);
	}
	
}