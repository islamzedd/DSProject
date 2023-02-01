package xml;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class postFinder {
	
	String text = "";
	LinkedList<LinkedList<String>> posts = new LinkedList<LinkedList<String>>();
	
	public postFinder(String text) {
		this.text = text;
	}

	public LinkedList<LinkedList<String>> searchByTopic(String topic) {
		Pattern namePattern = Pattern.compile("<name>");
		Matcher nameMatcher = namePattern.matcher(text);
		Pattern topicPattern = Pattern.compile("<topic>");
		Matcher topicMatcher = topicPattern.matcher(text);
		Pattern endPattern = Pattern.compile("</posts>");
		Matcher endMatcher = endPattern.matcher(text);
		while(nameMatcher.find()) {
			String name = text.substring(text.indexOf(">",nameMatcher.start())+1,text.indexOf("<",nameMatcher.end())).strip();
			if(endMatcher.find(nameMatcher.start())) {
				int nameIndex = nameMatcher.end();
				while(topicMatcher.find(nameIndex)) {
					if(topicMatcher.start() > endMatcher.start()) {
						break;
					}
					String topicFound =  text.substring(text.indexOf(">",topicMatcher.start())+1,text.indexOf("<",topicMatcher.end())).strip();
					if(topicFound.toLowerCase().contains(topic.toLowerCase())) {
						LinkedList<String> postFound = new LinkedList<String>();
						postFound.add(name);
						String body = text.substring(text.lastIndexOf("<body>",topicMatcher.start())+6,text.lastIndexOf("</body>",topicMatcher.start()));
						postFound.add(body);
						postFound.add(topicFound);
						posts.add(postFound);
					}
					nameIndex = topicMatcher.start()+1;
				}
			}
		}
		return posts;
	}
	
	public LinkedList<LinkedList<String>> searchByWord(String word) {
		Pattern namePattern = Pattern.compile("<name>");
		Matcher nameMatcher = namePattern.matcher(text);
		Pattern bodyPattern = Pattern.compile("<body>");
		Matcher bodyMatcher = bodyPattern.matcher(text);
		Pattern topicPattern = Pattern.compile("<topic>");
		Matcher topicMatcher = topicPattern.matcher(text);
		Pattern endPattern = Pattern.compile("</posts>");
		Matcher endMatcher = endPattern.matcher(text);
		Pattern topicendPattern = Pattern.compile("</topics>");
		Matcher topicendMatcher = topicendPattern.matcher(text);
		while(nameMatcher.find()) {
			String name = text.substring(text.indexOf(">",nameMatcher.start())+1,text.indexOf("<",nameMatcher.end())).strip();
			if(endMatcher.find(nameMatcher.start())) {
				int nameIndex = nameMatcher.end();
				while(bodyMatcher.find(nameIndex)) {
					if(bodyMatcher.start() > endMatcher.start()) {
						break;
					}
					String bodyFound =  text.substring(text.indexOf(">",bodyMatcher.start())+1,text.indexOf("<",bodyMatcher.end())).strip();
					if(bodyFound.toLowerCase().contains(word.toLowerCase())) {
						LinkedList<String> postFound = new LinkedList<String>();
						postFound.add(name);
						postFound.add(bodyFound);
						int bodyIndex = bodyMatcher.start();
						if(topicendMatcher.find(bodyMatcher.start())) {
							while(topicMatcher.find(bodyIndex)) {
								if(topicMatcher.start()>topicendMatcher.start()) {
									break;
								}
								String topicFound =  text.substring(text.indexOf(">",topicMatcher.start())+1,text.indexOf("<",topicMatcher.end())).strip();
								postFound.add(topicFound);
								bodyIndex=topicMatcher.start()+1;
							}
						}
						posts.add(postFound);
						System.out.println(postFound.toString());
					}
					nameIndex = bodyMatcher.start()+1;
				}
			}
		}
		return posts;
	}
	
}
