package xml;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class networkAnalysis {
	
	StringBuilder analysisString = new StringBuilder();
	
	Graph graph = null;
	Map<Integer, ArrayList<Integer>> analysisGraph = null;
	ArrayList<Integer> conArr = new ArrayList<>();
	
	//array of users IDs (keys)
	Set<Integer> key_Set = null;
	Integer[] key_Arr = null;
	
	int greaterFollowersCount = 0;
	int mostFreq = 0;
	int frequency = 0;
	
	public networkAnalysis(Graph graph) {
		this.graph = graph;
		
		analysisGraph = graph.getGraph();
		key_Set = analysisGraph.keySet();
		key_Arr = key_Set.toArray(new Integer[key_Set.size()]);
		
	}
	
	public String mostInfluencerUser() {
		
		analysisString.setLength(0);
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			int followerCount = analysisGraph.get(key_Arr[i-1]).size();
			greaterFollowersCount = Math.max(greaterFollowersCount, followerCount);
		}
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			if(greaterFollowersCount == analysisGraph.get(key_Arr[i-1]).size()) {
				analysisString.append(("The most influencer user is " + graph.getName(key_Arr[i-1]) + " of ID :  " + 
										key_Arr[i-1] + ". They have a follower count of " + greaterFollowersCount + " followers.\n"));
			}
		}
		
		return analysisString.toString();
	}
	
	public String mostActiveUser() {
		
		analysisString.setLength(0);
		Stack<Integer> stack = new Stack<Integer>();
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			for(int j = 0; j < analysisGraph.get(key_Arr[i-1]).size(); j++) {
				conArr.add(analysisGraph.get(key_Arr[i-1]).get(j));
			}	
		}
		
		for(int i = 0; i < conArr.size(); i++) {
			int count = 0;
			
			for(int j = 0; j < conArr.size(); j++) {
				if(conArr.get(i) == conArr.get(j)) {
					count++;
				}
			}
			mostFreq = conArr.get(i);
			
			if(count > frequency) {
				frequency = count;
				
				while(!stack.empty()) {
					stack.pop();
				}
				stack.push(mostFreq);
			}
			else if(count == frequency) {
				if(stack.search(mostFreq) == -1) {
					stack.push(mostFreq);
				}
			}
		}
		
		while(!stack.empty()) {
			analysisString.append(("The most active user is " + graph.getName(stack.peek()) + " of ID :  " + stack.peek()
					+ ". They are following " + frequency + " users.\n"));
			stack.pop();
		}
		
		return analysisString.toString();
	}
	
	public String suggestFollowers() {
		
		analysisString.setLength(0);
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			analysisString.append("User " + graph.getName(key_Arr[i-1]) + " of ID (" + key_Arr[i-1] + ") :  ");
			
			if(analysisGraph.get(key_Arr[i-1]).size() == 0) {
				analysisString.append("The user has no followers. \n");
			}
			else {
				for(int j = 0; j < analysisGraph.get(key_Arr[i-1]).size(); j++) {
					
					if(this.contain(key_Arr, analysisGraph.get(key_Arr[i-1]).get(j))) {
						int follower_Id = analysisGraph.get(key_Arr[i-1]).get(j);
						
						for(int k = 0; k < analysisGraph.get(follower_Id).size(); k++) {
							int followerOfFollowerID = analysisGraph.get(follower_Id).get(k);
							
							if( followerOfFollowerID == (key_Arr[i-1]) ) 
							{
								if(analysisGraph.get(follower_Id).size() == 1) {
									analysisString.append("Can't find more users for them to follow from user :  "
															+ graph.getName(follower_Id) + ". ");
								}
								else {
									continue;
								}
							}
							else if(analysisGraph.get(key_Arr[i-1]).contains(followerOfFollowerID)) {
								analysisString.append("Can't find more users for them to follow from user :  "
										+ graph.getName(follower_Id) + ". ");
							}
							
							else {
								analysisString.append("may like to follow user/s :  " + graph.getName(followerOfFollowerID)
														+ " of ID (" + followerOfFollowerID + ")");
								
								if(k == analysisGraph.get(follower_Id).size() - 1) {
									analysisString.append(".");
								}
								else{
									analysisString.append(", ");
								}
							}
						}
					}
				}
				analysisString.append("\n");
			}
		}
		return analysisString.toString();
	}

	public String mutualFollowers(String name1, String name2) {
		analysisString.setLength(0);
		int counter = 0;
		int id1, id2;
		
		if(this.isNumeric(name1)) {
			id1 = Integer.parseInt(name1);
		}
		else {
			id1 = graph.getId(name1);
		}
		if(this.isNumeric(name2)) {
			id2 = Integer.parseInt(name2);
		}
		else {
			id2 = graph.getId(name2);
		}
		
		analysisString.append("The mutual follower/s between " + graph.getName(id1) + " of ID (" + id1 + ") and " 
								+ graph.getName(id2) + " of ID (" + id2 + ") is/are: ");
		
		for(int i = 0; i < analysisGraph.get(id1).size(); i++) {
			for(int j = 0; j < analysisGraph.get(id2).size(); j++) {
				
				if(analysisGraph.get(id1).get(i) == analysisGraph.get(id2).get(j)) {
					analysisString.append(graph.getName( analysisGraph.get(id1).get(i) ) + ", ");
					counter++;
				}
			}
		}
		if(counter == 0) {
			analysisString.append("No mutual followers are found.");
		}
		
		return analysisString.toString();
	}
	
	public boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public boolean contain(Integer[] key_Arr2, int toCheckValue) {
		for (int element : key_Arr2) {
		    if (element == toCheckValue) {
		        return true;
		    }
		}
		return false;
	}
	
}
