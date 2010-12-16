package geeklist;

import com.google.appengine.api.datastore.Key;

import java.util.List;
import java.util.ArrayList;

import java.util.Date;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


/**
 * A simple representation of a list supporting voting on the items in the list and basic maintenance
 */
@PersistenceCapable
public class GeekList {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    /**
     * name of the list
     * @todo find an acceptable way to enforce unique names
     */
    @Persistent
    private String name;

    /**
     * date the GeekList 'closes'. Votes can not be cast on a closed list.
     */
    @Persistent
    private Date dateClosed;
    
    /**
     * options in the list user can vote on
     */
    @Persistent
    List<GeekListItem> items;

    /**
     * basic constructor
     * @param name
     * @param dateClosed
     */
    public GeekList(String name, Date dateClosed) {
        this.name = name;
        this.dateClosed = dateClosed;
        this.items = new ArrayList<GeekListItem>();
    }
 
    public Key getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
    
    /**
     * adds a new item to the list
     * @param item
     * @return the current numer of items (including the newly added)
     */
    public int addItem(GeekListItem item) {
    	getItems().add(item);
    	
    	return getItems().size();
    }
    
    /**
     * Adds a new item based on only the name of the item.
     * This is an overload for addItem(GeekListItem)
     * @param itemName 
     * @return the current numer of items (including the newly added)
     */
    public int addItem(String itemText, String itemAuthor, String itemUrl) {
    	return addItem(new GeekListItem(itemText, itemAuthor,  itemUrl));
    }
    
    /**
     * Removes an item from list. 
     * @param item
     * @return the current numer of items after the operation
     */
    public int removeItem(GeekListItem item) {
    	getItems().remove(item);
    	
    	return getItems().size();
    }
    
    /**
     * Removes an item from list. 
     * This is an overload for removeItem(GeekListItem) 
     * @param itemText The item to remove
     * @return the current numer of items after the operation
     */
    public int removeItem(String itemText) {
    	return removeItem(new GeekListItem(itemText, "", ""));    	
    }
    
    public List<GeekListItem> getItems() {
    	if (items == null) items = new ArrayList<GeekListItem>();
    	return items;
    }

    public Date getDateClosed() {
        return dateClosed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }
    
    /**
     * Retrieves a vote cast by a user. The user is specified biy email
     * @param email the user who's vote to check
     * @return the items the user specified by email voted on; null if the user did not vote
     * @todo using a User would be preferred. Email is used since GeekListItem only stores email Strings
     */
    public GeekListItem getVote(String email) {
    	for (int i=0; i<getItems().size(); i++) {
    		if (getItems().get(i).getVoters().indexOf(email) > -1) {
    			return getItems().get(i);
    		}
    	}
    	
    	return null;
    }
    
    /**
     * Adds vote to item from user email.
     * addVote(String, String) allows a single user to vote for an item more than once. 
     * @param item the item to vote on
     * @param email Identifies the voting user
     * @return the number of votes cast for item (including this one)
     * @todo using a User would be preferred. Email is used since GeekListItem only stores email Strings
     */
    public int addVote(String item, String email) {
    	// find item to vote on
    	for (int i=0; i<getItems().size(); i++) {
    		if (getItems().get(i).getTitle().equals(item)) {
    			// cast vote
    			return getItems().get(i).addVote(email);
    			
    		}
    	}
    	
    	// item not found, vote can not be cast: raise error
    	throw new IllegalArgumentException("item \"" +item+ "\" not found in GeekList \"" +getName()+ "\"");
    }
    
    /**
     * Provides a JSON formatted String representation of this GeekList
     * @return JSON formatted String representation
     * @todo Since the structure is so simple, I opted to just encode it manually, 
     * 		but a generic solution is preferable. Rewrite this to use a common json library  
     */
    public String toJSON() {
    	String result = "{"
    		+ "\"key\": \"" + getKey() + "\", "
    		+ "\"name\": \"" + getName() + "\", "
    		+ "\"dateClosed\": \"" + getDateClosed() + "\", "
    		+ "\"items\": [";
    		for (int i=0; i<getItems().size(); i++) {
    			result +=  getItems().get(i).toJSON();
    			if ((i+1) < getItems().size()) result += ", ";
    		}
    		result += "] "
    			+ "}";

    	return result;
    }
}

