package xml;

import java.util.*;
import java.util.regex.*;

public class Graph {
	Map<Integer, ArrayList<Integer>> graph = new HashMap<Integer, ArrayList<Integer>>();
	Map<Integer,String> idToName = new HashMap<Integer,String>();
	Map<String,Integer> nameToId = new HashMap<String,Integer>();
	
	public Graph() {
		
	}
	
	public void addUser(Integer name) {
		graph.put(name,new ArrayList<Integer>());
	}
	
	private void addname(Integer id,String name) {
		idToName.put(id,name);
	}
	
	public String getName(Integer id) {
		String name = idToName.get(id);
		if( name != null) {
			return name;
		}
		else {
			return "id not found";
		}
	}
	
	private void addId(String name,Integer id) {
		nameToId.put(name.toLowerCase(),id);
	}
	
	public Integer getId(String name) {
		Integer id = nameToId.get(name.toLowerCase());
		if( id != null) {
			return id;
		}
		else {
			return -1;
		}
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
			String name = jsonString.substring(nameMatcher.end()+4,jsonString.indexOf("\"",nameMatcher.end()+4));
			int userId=Integer.parseInt(jsonString.substring(jsonString.lastIndexOf(',',nameMatcher.start())-2,jsonString.lastIndexOf(',',nameMatcher.start())-1));
			this.addname(userId, name);
			this.addId(name, userId);
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
