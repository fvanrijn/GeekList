package geeklist.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.List;

import geeklist.GeekList;
import geeklist.PMF;

/**
 * Returns an overview of lists or a specific list in detail. 
 * Mapping: /list/[(listname)]
 */
public class ViewListServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		if (user != null) {
		  resp.setContentType("text/plain");
		  
		  // parse URI to determine if we're displaying an overview or list details
		  String requestedList = (req.getRequestURI().length() > "/list/".length() ? req.getRequestURI().substring("/list/".length()) : "");
		  if (!requestedList.isEmpty()) {
			  resp.getWriter().print(getList(requestedList));
		  } else {
			  resp.getWriter().print(getOverview());
		  }
		} else {
			resp.sendError(resp.SC_FORBIDDEN, "Access denied.");
		}
	}
	
	/**
	 * Fetches a GeekList specified by name
	 * @todo catch non-existing lists
	 * @param name
	 * @return a JSON formatted String with the list details
	 */
	private String getList(String name) {
		List<GeekList> geeklists = PMF.getLists(name);

		String result = "";
		if (!geeklists.isEmpty()) {
			// @todo ignore name is not the primary key and this may yield >1 lists for now
			result = geeklists.get(0).toJSON();
		}
		return result;
	}
	
	/**
	 * Getter for a JSON String of listnames
	 * @return
	 */
	private String getOverview() {
		List<GeekList> geeklists = PMF.getLists();

		String result = "";
		if (!geeklists.isEmpty()) {
			result = "{ \"lists\": [ ";
			for (int i=0; i<geeklists.size(); i++) {
				result += "\"" + geeklists.get(i).getName() + "\"";
			//	result += geeklists.get(i).toJSON();
				if ((i+1) < geeklists.size()) {
					result += ", ";
				}
			}
			result += " ] }";
		}
		return result;
	}
}
