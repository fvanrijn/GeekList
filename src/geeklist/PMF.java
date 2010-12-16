package geeklist;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;




/**
 * Static helper for GeekList management. Handles a data store IO.
 */
public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    private static PersistenceManager get() {
        return pmfInstance.getPersistenceManager();
    }
    
    /**
     * Stores a GeekList to the Java Data Store
     * @param gl
     * @todo contemplate if we should integrate this in the GeekList itself. 
     * 		It seems a little strange having this method seperated from it and is not very OO.
     */
    public static void store(GeekList gl) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(gl);
    	if (pm == null) pm = get();
    	try {
    		pm.makePersistent(gl);
    	} finally {
    		pm.close();
    	}
    }
    
    public static void delete(GeekList gl) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(gl);
    	if (pm == null) pm = get();
    	try {
    		pm.deletePersistent(gl);
    	} finally {
    		pm.close();
    	}
    }
    
    /**
     * Gets all GeekLists in the datastore
     * @return List<GeekList> holding all lists in the datastore
     */
    public static List<GeekList> getLists() {
    	return getLists("");
    }
    
    /**
     * Get all GeekLists in the datastore with a matching name attribute
     * @param name
     * @return List<GeekList> holding matching lists
     */
   	public static List<GeekList> getLists(String name) {
		Query query = get().newQuery(GeekList.class);
		if (!name.isEmpty()) {
			query.setFilter("name == pName");
			query.declareParameters("String pName");
		}

		return (List<GeekList>)query.execute(name);
   	}
}