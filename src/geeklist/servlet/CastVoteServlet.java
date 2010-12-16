package geeklist.servlet;

import geeklist.GeekList;
import geeklist.PMF;

import java.io.IOException;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


/**
 * CastVoteServlet handles voting for a specific listitem.  
 */
public class CastVoteServlet extends HttpServlet {
	/**
	 * doGet - handles voting 
	 * Mapping: /vote/(listname)/(itemname)
	 * @todo add nicer errorhandling (instead of bluntly throwing Exceptions)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
	
		if (user != null) {
			resp.setContentType("text/x-json");
		  
			// parse request: /vote/<list>/<item>
			String itemName =  req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/")+1);
			String listName = req.getRequestURI().substring("/vote/".length(), req.getRequestURI().indexOf(itemName)-1);

			// handle it
			if (!listName.isEmpty() && !itemName.isEmpty()) {
				List<GeekList> geeklists = PMF.getLists(listName);
				
				if (!geeklists.isEmpty()) {
					GeekList gl = geeklists.get(0); 
					
					// check if the list is open for voting
					if (gl.getDateClosed().compareTo(new Date()) < 0) {
						resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "list closed");
					} else
					
					// check if the user has already voted on this
					if (gl.getVote(user.getEmail()) != null) {
						resp.sendError(resp.SC_METHOD_NOT_ALLOWED, "you already voted on this list");
					} else {
			  
						gl.addVote(itemName, user.getEmail());
						PMF.store(gl);
					}
				} else {
					throw new IllegalArgumentException("list not found \"" +listName+ "\"");
				}
			}
		  
		} else {
			resp.sendError(resp.SC_FORBIDDEN, "Access denied, please log in first.");
		}
	}
}
