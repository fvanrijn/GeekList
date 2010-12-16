package geeklist.servlet;

import geeklist.GeekList;
import geeklist.PMF;

import java.util.logging.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * ManageListServlet provides basic maintenance tasks for GeekLists.
 * GET: login, logout
 * POST: add, delete lists/items 
 *
 */
public class ManageListServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ManageListServlet.class.getName());

	/**
	 * doGet provides flow control to login/logout. 
	 * Mappings: /, /signout
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();

	    log.info("Entering ManageListServlet.doGet for URI: " +req.getRequestURI());
	    if (req.getRequestURI().equals("/signout")) {
	    	log.info("/signout: signing out and redirect to /");
			resp.sendRedirect(userService.createLogoutURL("/"));
		} else {
			if (user != null) {
				log.info("User " + user.getEmail() + " is logged in: redirect to geeklist.html");
				resp.sendRedirect("/geeklist.html");
			} else {
				log.info("No user found: redirect to login");
				resp.sendRedirect(userService.createLoginURL("/geeklist.html"));
			}
		}
	}
	
	/**
	 * doPost handles the listactions: add and delete for both items and lists.
	 * Mapping: /add/(listname)/[(itemname)], /delete/(listname)/[(itemname)]
	 * @todo upon completion, this method feels a little cluttered. Clean it up by splitting 
	 * 				actions (and fetching required paramters) into separate functions 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		log.info("Entering ManageListServlet.doPost for URI: " +req.getRequestURI());
		
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		String sAdminAccount = getServletContext().getInitParameter("admin");
		if (user.getEmail().equals(sAdminAccount)) {
			// parse action from request URI: /vote/<action>
			String sAction =  req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/")+1); 
			log.info("Action parsed: " +sAction);
			
			// get/prepare parameters
			String sListName = "";
			Date dClosedDate = new Date(); 
			String sItemName = "";
			String sItemAuthor = "";
			String sItemUrl = "";
			sListName = req.getParameter("listname");
			sItemName = req.getParameter("itemname");
			sItemAuthor = req.getParameter("itemauthor");
			sItemUrl = req.getParameter("itemurl");
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				dClosedDate = sdf.parse(req.getParameter("dateClosed"));
			} catch (NullPointerException npe) {
				// @todo add proper error handling 
			} catch (ParseException pe) {				
			}
			
			// get the GeekList in to be modified
			List<GeekList> lists = PMF.getLists(sListName);
			GeekList gl = null;
			if (lists.isEmpty()) {
				gl = new GeekList(sListName, dClosedDate);
			} else {
				// @todo just get the first, ignore any potential problems arising from having multiple items here
				gl = lists.get(0);
			}
			
			// add a new item or list
			if (sAction.equals("add")) {
				if ((sItemName == null) || sItemName.isEmpty()) {
					// create new list
					PMF.store(gl);
				} else {
					// add item to existing list
					gl.addItem(sItemName, sItemAuthor, sItemUrl);
					PMF.store(gl);
				}
			} else 
				
			// delete an item from a list or remove an entire list
			if (sAction.equals("delete")) {
				if ((sItemName == null) || sItemName.isEmpty()) {
					// delete list
					PMF.delete(gl);
				} else {
					// delete item
					gl.removeItem(sItemName);
				}
			} else {
				// unrecognized action
				log.warning("Unsupported operation \""+ sAction +"\"");
				throw new IllegalArgumentException("unsupported operation \""+ sAction +"\"");
			}
			
			resp.sendRedirect("/admin.html");

		} else {
			log.warning("Unauthorized access attempted.");
			resp.sendError(resp.SC_FORBIDDEN, "Access denied.");
		}
	}
}
