package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class AuthenticationServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		out.write(UtilJson.toJsonPair("error", "Please use POST"));
		pm.close();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String op = req.getParameter("op");
		if (op == null)
			op = "list";

		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();

		try {
			switch (op) {
			case "register":
				handleUserRegister(req, out, pm);
				break;
			case "login":
				handleUserLogin(req, out, pm);
				break;
			default:
				UtilJson.toJsonPair("error", "Unrecognized command");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write(UtilJson.toJsonPair("error", e.getMessage()));
		} finally {
			pm.close();
		}
	}
	
	private void handleUserLogin(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.authenticate(req, pm);
		out.write(UtilJson.toJson(user));
	}

	private void handleUserRegister(HttpServletRequest req, PrintWriter out, PersistenceManager pm) {
		User user = User.create(req.getParameter("username"), req.getParameter("password"), pm);
		out.write(UtilJson.toJson(user));
	}
}
