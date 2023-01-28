package xml;

import java.util.*;
import java.util.regex.*;

public class Graph {
	Map<String, ArrayList<String>> graph = new HashMap<String, ArrayList<String>>();
	
	public Graph() {
		
	}
	
	public void addUser(String name) {
		graph.put(name,new ArrayList<String>());
	}
	
	
	public String getUserConnections(String name) {
		return graph.get(name).toString();
	}
	
	public void connectUsers(String fromName,String toName) {
		graph.get(fromName).add(toName);
	}
	
	public Map<String, ArrayList<String>> getGraph(){
		return graph;
	}
	
	public void parseToGraph(String jsonString) {
		Pattern namePattern = Pattern.compile("name");
		Matcher nameMatcher = namePattern.matcher(jsonString);
		Pattern idPattern = Pattern.compile("\"id\"");
		Matcher idMatcher = idPattern.matcher(jsonString);
		Pattern endPattern = Pattern.compile("}\n");
		Matcher endMatcher = endPattern.matcher(jsonString);
		Pattern followersPattern = Pattern.compile("followers");
		Matcher followersMatcher = followersPattern.matcher(jsonString);
		while(nameMatcher.find()) {
			String userId=jsonString.substring(jsonString.lastIndexOf(',',nameMatcher.start())-2,jsonString.lastIndexOf(',',nameMatcher.start())-1);
			this.addUser(userId);
			int nameindex=nameMatcher.start();
			if(followersMatcher.find(nameindex)) {
				int followersindex=followersMatcher.start();
				if(endMatcher.find(followersindex)) {
					while(idMatcher.find(followersindex)) {
						if(idMatcher.start() > endMatcher.start()) {
						
							break;
						}
						String id = jsonString.substring(idMatcher.end()+3,jsonString.indexOf("\"",idMatcher.end()+3));
						this.connectUsers(userId,id);
						followersindex = idMatcher.start()+1;
					}
				}
			}
		}
	}
}
