package xml;

import java.util.*;
import java.util.regex.*;

public class Graph {
	Map<Integer, ArrayList<Integer>> graph = new HashMap<Integer, ArrayList<Integer>>();
	
	public Graph() {
		
	}
	
	public void addUser(Integer name) {
		graph.put(name,new ArrayList<Integer>());
	}
	
	
	public ArrayList<Integer> getUserConnections(Integer name) {
		return graph.get(name);
	}
	
	public void connectUsers(Integer fromName,Integer toName) {
		graph.get(fromName).add(toName);
	}
	
	public Map<Integer, ArrayList<Integer>> getGraph(){
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
			int userId=Integer.parseInt(jsonString.substring(jsonString.lastIndexOf(',',nameMatcher.start())-2,jsonString.lastIndexOf(',',nameMatcher.start())-1));
			this.addUser(userId);
			int nameindex=nameMatcher.start();
			if(followersMatcher.find(nameindex)) {
				int followersindex=followersMatcher.start();
				if(endMatcher.find(followersindex)) {
					while(idMatcher.find(followersindex)) {
						if(idMatcher.start() > endMatcher.start()) {
						
							break;
						}
						int id = Integer.parseInt(jsonString.substring(idMatcher.end()+3,jsonString.indexOf("\"",idMatcher.end()+3)));
						this.connectUsers(userId,id);
						followersindex = idMatcher.start()+1;
					}
				}
			}
		}
	}
	
}
