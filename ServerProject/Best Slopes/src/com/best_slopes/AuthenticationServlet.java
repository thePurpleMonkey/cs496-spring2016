package com.best_slopes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class AuthenticationServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		resp.setContentType("application/json");
		PrintWriter out = resp.getWriter();
		PersistenceManager pm = PMF.getPMF().getPersistenceManager();
		
		out.write("OMG I'M A NEW SERVLET\n");
		out.write(UtilJson.toJsonPair("errormsg", "course not found"));
		pm.close();
		
	}
	
}
