package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class TrackTrailsServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//		resp.setContentType("text/plain");
//		resp.getWriter().println("F, world");
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		out.write(UtilJson.toJsonPair("errormsg", "course not found"));
//		out.write("suck");
		pm.close();
		
	}
	
	/*
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
			String description = req.getParameter("description");
			String active = req.getParameter("active");

			if (id < 0)
				throw new IllegalArgumentException("Invalid course id");
			if (title == null || title.length() == 0)
				throw new IllegalArgumentException("Invalid course title");
			if (description == null || description.length() == 0)
				throw new IllegalArgumentException("Invalid course description");

			pm.makePersistent(course);

			out.write(formatAsJson(course));
		} catch (IllegalArgumentException iae) {
			out.write(UtilJson.toJsonPair("errormsg", iae.getMessage()));
		} finally {
			pm.close();
		}
	}
	*/
	
}
