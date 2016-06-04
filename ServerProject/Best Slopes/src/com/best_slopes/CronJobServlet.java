package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
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

//			Stats.streamAsJson(out, stats);		//used for debugging, will output the data as JSON to Webpage.
		} finally {
			pm.close();
		}
		resp.getWriter().println("success");
	}

}
