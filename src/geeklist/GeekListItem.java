package geeklist;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.Key;
import java.util.List;
import java.util.ArrayList;


@PersistenceCapable
public class GeekListItem {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
	private String title;
    
    @Persistent
	private String author;
    
    @Persistent
	private String url;
    
    /**
     * contains the email addresses of all users who have voted for this item
     * @todo Using the email String value to identify a user is very loosely coupled. A better solution would 
     *			be to use a List<User>, create a Vote class or even a Voter class to easily retrieve 
     *			the votes a user has cast.
     */
    @Persistent
    private List<String> voters;
	
    public GeekListItem(String title, String author, String url) {
    	this.title = title;
    	this.author = author;
    	this.url = url;
    	
    	voters = new ArrayList<String>();
    }
    
    public Key getKey() {
    	return key;
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() { 
    	return title;
    }
    
    public void setAuthor(String author) {
    	this.author = author;
    }
    
    public String getAuthor() { 
    	return author;
    }
    
    public void setUrl(String url) {
    	this.url = url;
    }
    
    public String getUrl() { 
    	return url;
    }
    
    public int addVote(String voter) {
    	voters.add(voter);
    	
    	return getVoteCount(); 
    }
    
    public List<String> getVoters() {
    	return voters;
    }
    
    public int getVoteCount() {
    	return voters.size();
    }
    
    @Override public String toString() {
    	return getTitle();
    }

    /**
     * Provides a JSON formatted String representation of this GeekListItem
     * @return JSON formatted String representation
     * @todo Since the structure is so simple, I opted to just encode it manually, 
     * 		but a generic solution is preferable. Rewrite this to use a common json library  

     */
    public String toJSON() {
    	String result = "{"
    		+ "\"key\": \"" + getKey() + "\", "
    		+ "\"title\": \"" + getTitle() + "\", "
    		+ "\"author\": \"" + getAuthor() + "\", "
    		+ "\"url\": \"" + getUrl() + "\", "
    		+ "\"voteCount\": \"" + getVoteCount() + "\", "
    		+ "\"voters\": [";
    		for (int i=0; i<getVoters().size(); i++) {
    			result += "\"" + getVoters().get(i) + "\"";
    			if ((i+1) < getVoters().size()) {
    				result += ", ";
    			}
    		}
    		result += "] "
    			+ "}";
    	return result;
    }
    
    /**
     * @return true if the title attribute of the items match
     */
    @Override public boolean equals( Object that ) {
    	return this.getTitle().equals(((GeekListItem)that).getTitle());
    }
}