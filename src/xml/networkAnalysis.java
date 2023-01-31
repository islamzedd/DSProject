package xml;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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
	int activeMeter = 0;
	
	public networkAnalysis(Graph graph) {
		this.graph = graph;
		
		analysisGraph = graph.getGraph();
		key_Set = analysisGraph.keySet();
		key_Arr = key_Set.toArray(new Integer[key_Set.size()]);
		
	}
	
	public String mostInfluencerUser() {
		
		analysisString.setLength(0);
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			int followerCount = analysisGraph.get(i).size();
			greaterFollowersCount = Math.max(greaterFollowersCount, followerCount);
		}
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			if(greaterFollowersCount == analysisGraph.get(i).size()) {
				analysisString.append(("The most influencer user is " + graph.getName(key_Arr[i-1]) + " with a follower count of " + greaterFollowersCount + ".\n"));
			}
		}
		
		return analysisString.toString();
	}
	
	public String mostActiveUser() {
		
		analysisString.setLength(0);
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			for(int j = 0; j < analysisGraph.get(i).size(); j++) {
				conArr.add(analysisGraph.get(i).get(j));
			}	
		}
		
		for(int i = 0; i < conArr.size(); i++) {
			int count = 0;
			
			for(int j = 0; j < conArr.size(); j++) {
				if(conArr.get(i) == conArr.get(j)) {
					count++;
				}
			}
			
			if(count > frequency) {
				frequency = count;
				mostFreq = conArr.get(i);
			}
		}
		
		analysisString.append(("The most active user is " + graph.getName(mostFreq) + ", they are following " + frequency + " users.\n"));
		
		return analysisString.toString();
	}
	
	public String suggestFollowers() {
		
		analysisString.setLength(0);
		
		for(int i = 1; i <= analysisGraph.size(); i++) {
			analysisString.append("User " + graph.getName(key_Arr[i-1]) + " :  ");
			//System.out.println("size: " + analysisGraph.get(i).size());
			
			for(int j = 0; j < analysisGraph.get(i).size(); j++) {
				int follower_Id = analysisGraph.get(i).get(j);
				//System.out.println(id);
				
				for(int k = 0; k < analysisGraph.get(follower_Id).size(); k++) {
					int followerOfFollowerID = analysisGraph.get(follower_Id).get(k);
					
					if( graph.getName(followerOfFollowerID).equals( graph.getName(key_Arr[i-1]) ) ) {
						if(analysisGraph.get(follower_Id).size() == 1) {
							analysisString.append("Can't find more users for them to follow from user :  "
													+ graph.getName(follower_Id) + ". ");
						}
						else {
							continue;
						}
					}
					
					else {
						analysisString.append("may like to follow users :  " + graph.getName(followerOfFollowerID));
						
						if(k == analysisGraph.get(follower_Id).size() - 1) {
							analysisString.append(".");
						}
						else{
							analysisString.append(", ");
						}
					}
				}
				
			}
			analysisString.append("\n");
		}
		
		return analysisString.toString();
	}

	public String mutualFollowers(String name1, String name2) {
		analysisString.setLength(0);
		int counter = 0;
		
		int id1 = graph.getId(name1);
		int id2 = graph.getId(name2);
		
		analysisString.append("The mutual follower/s between " + name1 + " and " + name2 + " is/are: ");
		
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
	
}
