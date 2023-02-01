package xml;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {
	private Stage stage;
	@FXML
	private TextFlow tfOut = new TextFlow();
	@FXML
	private TextFlow tfIn = new TextFlow();
	
	@FXML TextField wordSearch;
	@FXML TextField topicSearch;
	@FXML TextField firstUser;
	@FXML TextField secondUser;
	private boolean fileExist = false;
	StringBuilder str_in = new StringBuilder();  
    StringBuilder latestString = new StringBuilder();
    StringBuilder jsonString = new StringBuilder();
    StringBuilder errorString = new StringBuilder();
    StringBuilder latestStringCopy = new StringBuilder();
    StringBuilder searchStringXML = new StringBuilder();
    TreeNode root = null;
    Graph graph = null;
	
	void init(Stage s) {
        stage=s;
    }
	
	public void chooseXMLFile(ActionEvent actionevent){
		FileChooser filechooser = new FileChooser();
        File file = filechooser.showOpenDialog(stage); 		//file path
        
        str_in = new StringBuilder();					//reinstantiation to reset the string when we choose another file
        latestString = new StringBuilder();				//reinstantiation to reset the string when we choose another file
        latestStringCopy = new StringBuilder(); 		//reinstantiation to reset the string when we choose another file
        tfOut.getChildren().clear();
        tfIn.getChildren().clear();
        tfOut.setStyle(" -fx-border-color: Yellow;");
        tfIn.setStyle(" -fx-border-color: Yellow;");

        if(file != null){
        	
        	fileExist = true;
        	String filename = file.getName();
           
        	boolean checkxmlfile = filename.contains(".xml")||filename.contains(".txt");
        	if(checkxmlfile){

        		//try catch block is needed for FileReader to work.
        		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

        			String line;
        			
        			//read each line in the file and stores it in str_in.
        			//then removes all tabs and empty lines and store it in latestString to be used later by other functions.
                    while ((line = reader.readLine()) != null) {
                	    str_in.append(line).append("\n");
                        if(line.isEmpty())
                        	continue;
                        line = line.replaceAll("  ","");
                        latestString.append(line).append("\n");
                    }
                    
                    
                    try {
                        String stringToBeParsed =  str_in.toString();
                        TreeNode parent = new TreeNode(null,null,-1,null);
                        XMLTree xmlTree = new XMLTree(parent);
                        root=xmlTree.parseXML(stringToBeParsed,0,parent);
                        root.closingBracket = "}";
                        }
                        catch(Exception e) {
                        	tfOut.getChildren().clear();
                			tfOut.setStyle(" -fx-border-color: Yellow;");
                			Text t = new Text("This file is too corrupt please click \"Check Errors\" to fix it first \n"
                					+ "then you can perform operations on it");
                			tfOut.getChildren().add(t);
                        }
        		}
        		
        	    catch (IOException e) {
        	    	Alert alert = new Alert(AlertType.ERROR);
        	    	alert.setTitle("ERROR");
        	    	String s = e.getMessage();
        	    	alert.setContentText(s);
        	    	alert.show();
        	    }
        		
        		//Creating a Text text to be displayed in the text flow output.
        		Text text = new Text(str_in.toString());
        		tfIn.getChildren().add(text);
        		
    			latestStringCopy =latestStringCopy.append(latestString);
    			searchStringXML = searchStringXML.append(latestString);
    			graph = populateGraph();
        	}
        	
        	//throw an error if the file is not .xml or .txt
        	else {
        		Alert alert = new Alert(AlertType.ERROR);
        		alert.setTitle("ERROR");
        		String s ="Please Enter XML File";
        		alert.setContentText(s);
                alert.show(); 
        	}
        }
	}
	 
	public void checkXMLErrors(ActionEvent action){
		if(fileExist){
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");
			char c;
			int i = 0;			//index of char c in latestString
			int line = 1;
			boolean error = false;
			latestString.setLength(0);
			errorString.setLength(0);
			latestString.append(latestStringCopy);
			c = latestString.charAt(i);
			String tag = "";
			String data = "";
			Stack<String> openTags = new Stack<String>();	//stack openning tags
			Queue<String> correctTags = new LinkedList<String>();	//stack errors
			while(i < latestString.length()) {
				
				if(i >= latestString.length()) break;
				
				//Section 1: open tag
				if(c == '<' && (char)latestString.charAt(i) != '/') { 
					//getting tag name
					while(latestString.charAt(i) != '>') {
						c = latestString.charAt(i++);
						if((c != '<') && (c != '>') && (c != '/')) {
							tag += c;
						}
					}
					//pushing tag into stack
					openTags.push(tag);
					correctTags.add("o" + tag); //O(n^2)
					tag = "";
					c = latestString.charAt(++i);
					i++;
				}
				
				//Section 2: skip tabs and spaces and new lines
				while (c == '\n' || c == '\t' || c == '\r' || c == ' ') {
	                if (i >= latestString.length() ) break;
	                if(c == '\n')
	                	line++;
	                c = latestString.charAt(i++);
	            }
	            if (i >= latestString.length() ) break;
	
				//Section 3: data
				if(c != '<') {
					//skip data and store it for future use
					data = "";
					while (c != '<')
		            {	
		                if (c != '<' && c != '>')
		                	data += c;
		                c = latestString.charAt(i++);
		                if (i >= latestString.length() ) break;
		                if (c == '\n')
		                	line++;
		            }
					//First Case: open tag after data
					if(c == '<' && (char)latestString.charAt(i) != '/') {
						error = true;
						//data in the beginning
						if (openTags.isEmpty()) {
							errorString.append("Error in line ");
							errorString.append(line);
							errorString.append(": You can't write data in the beginning\n");
							data = "";
						}
						//data not in the beginning
						else {
							//open tag was a data tag
							if (openTags.peek().equals("name") || openTags.peek().equals("id") || openTags.peek().equals("body") ||
								openTags.peek().equals("topic")) {
								errorString.append("Error in line ");
								errorString.append(line - 1);
								errorString.append(": </");
								errorString.append(openTags.peek());
								errorString.append("> missing\n");
								//Error handling
								correctTags.add("d" + data);
								correctTags.add("/" + openTags.peek());
								tag = "";
								openTags.pop();
							}
							//open tag isn't a data tag
							else {
								errorString.append("Error in line ");
								errorString.append(line - 1);
								errorString.append(": You can't write data here\n");
								data = "";
							}
							
						}
					}
					
					//Second Case: closing tag after data
					else if(c == '<'  && (char)latestString.charAt(i) == '/') {
						//getting the tag name
						while(latestString.charAt(i) != '>') {
							c = (char)latestString.charAt(i++);
							if((c != '<') && (c != '>') && (c != '/')) {
								tag += c;
							}
						}
						//if XML starts with missing opening data tag (not needed actually)
						if (openTags.isEmpty()) {
							error = true;
							errorString.append("Error in line ");
							errorString.append(line - 1);
							errorString.append(": <");
							errorString.append(tag);
							errorString.append("> missing in the beginning\n");
							c = latestString.charAt(++i);
						}
						else {
							//correct closing after data
							if(tag.equals(openTags.peek())) {
								correctTags.add("d" + data);
								correctTags.add("/" + openTags.peek());
								openTags.pop();
								tag = "";
								c = latestString.charAt(++i);
								i++;
							}
							//not correct closing after data
							else {
								error = true;
								//first case: two mismatch data tags and giving favor for first
								if ((tag.equals("name") || tag.equals("id") || tag.equals("body") || tag.equals("topic"))
								&&(openTags.peek().equals("name") || openTags.peek().equals("id") || openTags.peek().equals("body") || openTags.peek().equals("topic"))) {
									errorString.append("Error in line ");
									errorString.append(line);
									errorString.append(": </");
									errorString.append(openTags.peek());
									errorString.append("> instead of </");
									errorString.append(tag);
									errorString.append(">\n");
									correctTags.add("d" + data);
									correctTags.add("/" + openTags.peek());
									openTags.pop();
									tag = "";
									c = latestString.charAt(++i);
									i++;
								}
								//second case: there's no opening data tag
								else if((tag.equals("name") || tag.equals("id") || tag.equals("body") || tag.equals("topic"))
								&&!(openTags.peek().equals("name") || openTags.peek().equals("id") || openTags.peek().equals("body") || openTags.peek().equals("topic"))){
									errorString.append("Error in line ");
									errorString.append(line);
									errorString.append(": <");
									errorString.append(tag);
									errorString.append("> missing in the beginning\n");
									correctTags.add("o" + tag);
									correctTags.add("d" + data);
									correctTags.add("/" + tag);
									tag = "";
									c = latestString.charAt(++i);
									i++;
								}
								//third case: there's no closing data tag
								else {
									errorString.append("Error in line ");
									errorString.append(line - 1);
									errorString.append(": </");
									errorString.append(openTags.peek());
									errorString.append("> missing\n");
									correctTags.add("d" + data);
									correctTags.add("/" + openTags.peek());
									openTags.pop();						//we remove the data opening tag
									//there's no error
									if(tag.equals(openTags.peek())) {
										correctTags.add("/" + openTags.peek()); //just add the closing bcz openning is there already
										openTags.pop();
										tag = "";
										c = (char)latestString.charAt(++i);
										i++;
									}
									//missing closing tags till we find it
									else if(!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
										while (!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
											errorString.append("Error in line ");
											errorString.append(line - 1);
											errorString.append(": </");
											errorString.append(openTags.peek());
											errorString.append("> missing\n");
											correctTags.add("/" + tag);
											openTags.pop();
										}
										correctTags.add("/" + tag);
										openTags.pop();
										tag = "";
										c = (char)latestString.charAt(++i);
										i++;
									}
									else {
										errorString.append("extra");
										tag = "";
										c = (char)latestString.charAt(++i);
										i++;
									}
								}
								
							}
						}
					}
				}
				
				//Section 3: closing tag
				if(c == '<'  && latestString.charAt(i) == '/') {
					//getting the tag name
					while(latestString.charAt(i) != '>') {
						c = latestString.charAt(i++);
						if((c != '<') && (c != '>') && (c != '/')) {
							tag += c;
						}
					}
					//first openning tag missing (for now as there might be more). there might be more of it
					if (openTags.isEmpty()) {
						error = true;
						errorString.append("Error in line ");
						errorString.append(line - 1);
						errorString.append(": <");
						errorString.append(tag);
						errorString.append("> missing in the beginning\n");
						c = latestString.charAt(++i);
						i++;
						correctTags.add("/" + tag); //handle this missing openning tag in the beginning in validation
					}
					else {
						//correct closing tag
						if(tag.equals(openTags.peek())) {
							correctTags.add("/" + tag);
							openTags.pop();
							tag = "";
							c = latestString.charAt(++i);
							i++;
						}
						//error handling
						//missing closing tags till we find it
						else if(!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
							error = true;
							while (!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
								errorString.append("Error in line ");
								errorString.append(line - 1);
								errorString.append(": </");
								errorString.append(openTags.peek());
								errorString.append("> missing");
								correctTags.add("/" + openTags.peek());
								openTags.pop();
							}
							if (tag.equals(openTags.peek())) {
								correctTags.add("/" + tag);
								openTags.pop();
								tag = "";
							}
							c = latestString.charAt(++i);
							i++;
						}
						
						else {
							error = true;
							errorString.append("Error in line ");
							errorString.append(line);
							errorString.append(": </");
							errorString.append(tag);
							errorString.append("> might be missing an openning tag\n");
							tag = "";
							c = latestString.charAt(++i);
							i++;
						}
					}	
				}
			}
			while(!openTags.isEmpty()) {
				error = true;
				errorString.append("Error in line ");
				errorString.append(line - 1);
				errorString.append(": </");
				errorString.append(openTags.peek());
				errorString.append("> missing\n");
				correctTags.add("/" + tag);
				openTags.pop();
			}
			
			//XML file correction and pretifying
			if (!error) {
				Text t = new Text("No Errors Found");
				tfOut.getChildren().add(t);
			}
			else {
				StringBuilder correctString = new StringBuilder();
				String correctLine = new String();
				String element = new String();
				int tagSize = correctTags.size();
				for (i = 0; i < tagSize; i++) {	
					correctLine = correctTags.peek();
					//if it's an openning tag
					if (correctLine.charAt(0) == 'o') {
						element = correctLine.substring(1);
						correctString.append("<" + element + ">");
						correctTags.remove();
					}
					
					//if it's data
					else if(correctLine.charAt(0) == 'd') {
						element = correctLine.substring(1);
						correctString.append(element);
						correctTags.remove();
					}
					
					//if it's closing tag
					else if(correctLine.charAt(0) == '/') {
						element = correctLine.substring(1);
						correctString.append("</" + element + ">\n");
						correctTags.remove();
					}
				}
				
				//re-render the tree with the correct xml
	            TreeNode parent = new TreeNode(null,null,-1,null);
	            XMLTree xmlTree = new XMLTree(parent);
	            try {
					root=xmlTree.parseXML(correctString.toString(),0,parent);
				} catch (IOException e) {
					Alert alert = new Alert(AlertType.ERROR);
	    	    	alert.setTitle("ERROR");
	    	    	String s = e.getMessage();
	    	    	alert.setContentText(s);
	    	    	alert.show();
				}
	            root.closingBracket = "}";
				
	            //show the correct xml
	            latestString.setLength(0);
	            prettify(root.children.get(0));
	            searchStringXML.setLength(0);
	            searchStringXML.append(latestString);
				errorString.append("\n" + "\n"+ "Error Correction" + "\n" + latestString);
				Text t = new Text(errorString.toString());
				tfOut.getChildren().add(t);
			}
		}
		
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();
		}
	}
	
	public void addXMLFormat(ActionEvent action){
		if(fileExist){
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
		
			latestString.setLength(0);
			prettify(root.children.get(0));
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(latestString.toString());
			tfOut.getChildren().add(t);
		}
		//Give an error when there is no file path chosen.
		else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();
		} 
	}
	
	private void prettify(TreeNode node) {
		if(node.children.size() > 0) {
			for(int i=0 ;i< node.depth;i++) {
				latestString.append("\t");
			}
			latestString.append("<" + node.name + ">\n");
			for(TreeNode child : node.children) {
				prettify(child);
			}
			for(int i=0 ;i< node.depth;i++) {
				latestString.append("\t");
			}
			latestString.append("</" + node.name + ">\n");
		}
		else {
			for(int i=0 ;i< node.depth;i++) {
				latestString.append("\t");
			}
			latestString.append("<" + node.name + ">" + node.value + "</" + node.name + ">\n");
		}
	}
	 
	public void minify(ActionEvent action){				
		//Check if a file has been chosen then clears the text flow output box.
		if(fileExist){
			
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
			
			latestString.setLength(0);
			mini(root.children.get(0));
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(latestString.toString());
			tfOut.getChildren().add(t);
		}
		
		//Give an error when there is no file path chosen.
		else{
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	private void mini(TreeNode node){
		if(node.children.size() > 0) {
			latestString.append("<" + node.name + ">");
			for(TreeNode child : node.children) {
				mini(child);
			}
			latestString.append("</" + node.name + ">");
		}
		else {
			latestString.append("<" + node.name + ">" + node.value + "</" + node.name + ">");
		}
	}
	 
	public void convertJson(ActionEvent a){
		if(fileExist){
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
		
			jsonString.append("{\n");
			preorderTraverse(root.children.get(0));
			jsonString.append("}\n");
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(jsonString.toString());
			tfOut.getChildren().add(t);
			latestString.setLength(0);
			latestString.append(jsonString);
			jsonString.setLength(0);
		}
		//Give an error when there is no file path chosen.
		else{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	private boolean hasSiblingsWithSameName(TreeNode node) {
		for(TreeNode sibling : node.parent.children) {
			if(sibling != node && node.name.compareTo(sibling.name) == 0) {
				return true;
			}
		}
		return false;
	}
	
	private void preorderTraverse(TreeNode node) {
		if(node==null) {
			return;
		}
		if(node.children.size()==0 && !hasSiblingsWithSameName(node)) {
			for(int i=0 ;i< node.depth+1;i++) {
				jsonString.append("\t");
			}
			if(node.parent.children.indexOf(node) == node.parent.children.size()-1)
				jsonString.append("\""+node.name+"\": \"" + node.value+"\"\n");
			else
				jsonString.append("\""+node.name+"\": \"" + node.value+"\",\n");
			
		}
		else if(node.children.size()==0 && hasSiblingsWithSameName(node) && !node.visited) {
			for(int i=0 ;i< node.depth+1;i++) {
				jsonString.append("\t");
			}
			jsonString.append("\""+node.name+"\": [ \"" + node.value+"\",\n");
			for(int i=0;i<node.parent.children.size();i++) {
				TreeNode sibling = node.parent.children.get(i);
				if(sibling != node && node.name.compareTo(sibling.name) == 0) {
					for(int j=0 ;j< node.depth+1;j++) {
						jsonString.append("\t");
					}
					if(i==(node.parent.children.size()-1))
						jsonString.append("\""+sibling.value+"\"\n");
					else
						jsonString.append("\""+sibling.value+"\",\n");
					sibling.visited=true;
				}
			}
			for(int i=0 ;i< node.depth+1;i++) {
				jsonString.append("\t");
			}
			jsonString.append("]\n");
		}
		else if(node.children.size()>0 && !hasSiblingsWithSameName(node)) {
			for(int i=0 ;i< node.depth+1;i++) {
				jsonString.append("\t");
			}
			jsonString.append("\""+node.name+"\": ");
			jsonString.append("{\n");
			if(node.parent.children.indexOf(node) == node.parent.children.size()-1)
				node.closingBracket="}";
			else
				node.closingBracket="},";
		}
		else if(node.children.size()>0 && hasSiblingsWithSameName(node)){

			if(!node.visited) {
				for(int i=0 ;i< node.depth+1;i++) {
					jsonString.append("\t");
				}
				jsonString.append("\""+node.name+"\": [\n");
			}
			for(TreeNode sibling : node.parent.children) {
				if(sibling != node && node.name.compareTo(sibling.name) == 0) {
					sibling.visited=true;
				}
			}
			for(int i=0 ;i< node.depth+2;i++) {
				jsonString.append("\t");
			}
			jsonString.append("{\n");
			for(TreeNode sibling : node.children) {
				if(sibling != node && node.name.compareTo(sibling.name) == 0) {
					for(int i=0 ;i< node.depth+1;i++) {
						jsonString.append("\t");
					}
					preorderTraverse(sibling);
					sibling.visited=true;
				}
			}
			if(node.parent.children.indexOf(node) == node.parent.children.size()-1)
				node.closingBracket="}";
			else
				node.closingBracket="},";
			if(node.parent.parent.children.indexOf(node.parent) == node.parent.parent.children.size()-1)
				node.parent.closingBracket="]";
			else
				node.parent.closingBracket="],";
		}
		
		for(TreeNode child : node.children) {
			preorderTraverse(child);
		}
		if(node.parent.children.indexOf(node) == node.parent.children.size()-1){
			for(int i=0 ;i< node.depth+1;i++) {
				jsonString.append("\t");
			}
			jsonString.append(node.parent.closingBracket + "\n");
		}
	}
	 
	public void compress(ActionEvent a) {
		String compStr = Encoder.Encode(latestString.toString());
		if (!fileExist) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String message ="Please Choose XML File First";
			alert.setContentText(message);
			alert.show();
			return;
		}
		
		FileChooser filechooser = new FileChooser();
		File file = filechooser.showSaveDialog(stage);
		if (file == null || compStr == null) {
			return;
		}
		
		if (latestString.toString().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String message ="There is a Problem in The compression process.";
			alert.setContentText(message);
            alert.show();
            return;
		}
		
		Encoder.writeBytes(file, compStr);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Compress Data");
		String message ="Data Successfully Compressed.";
		alert.setContentText(message);
		alert.show();
	}
	 
	public void decompress(ActionEvent a) {
		FileChooser filechooser = new FileChooser();
		tfOut.setStyle("-fx-border-color: Yellow;");
		tfIn.setStyle("-fx-border-color: Yellow;");
		File file = filechooser.showOpenDialog(stage);
		
		tfOut.getChildren().clear();
		tfIn.getChildren().clear();
		fileExist = true;
		
		String decodedString = Encoder.readAndDecodeFile(file);
		//str_in.append(decodedString);
		//latestString.append(decodedString);
		
		try {
			String stringToBeParsed =  decodedString.toString();
	        TreeNode parent = new TreeNode(null,null,-1,null);
	        XMLTree xmlTree = new XMLTree(parent);
	        root=xmlTree.parseXML(stringToBeParsed,0,parent);
	        root.closingBracket = "}";
		}
		catch (IOException e) {
	    	Alert alert = new Alert(AlertType.ERROR);
	    	alert.setTitle("ERROR");
	    	String s = e.getMessage();
	    	alert.setContentText(s);
	    	alert.show();
	    }
		
		Text t = new Text(decodedString);
		t.setFill(Color.BLACK);
		tfIn.getChildren().add(t);
	}
	 
	public void save(ActionEvent action){
		if(fileExist){
			FileChooser filechooser = new FileChooser();
			File file = filechooser.showSaveDialog(stage);
			
			if(file != null && latestString.toString() != null) {
				if(!latestString.toString().isEmpty())
					savecontent(file, latestString.toString());
				
				else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR");
					String s ="Please Make Changes in the XML File First";
					alert.setContentText(s);
					alert.show();
				}
			}
 
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Choose XML File First";
			alert.setContentText(s);
			alert.show();
		}
	}
	
	public void mostInfluencer(ActionEvent action){
		//Check if a file has been chosen then clears the text flow output box.
		if(fileExist){
					
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
					
					
			networkAnalysis analysis = new networkAnalysis(graph);
			String displayedString = analysis.mostInfluencerUser();
					
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(displayedString);
			tfOut.getChildren().add(t);
		}
				
		//Give an error when there is no file path chosen.
		else{
					
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	public void mostActive(ActionEvent action){
		//Check if a file has been chosen then clears the text flow output box.
		if(fileExist){
							
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
							
							
			networkAnalysis analysis = new networkAnalysis(graph);
			String displayedString = analysis.mostActiveUser();
							
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(displayedString);
			tfOut.getChildren().add(t);
		}
						
			//Give an error when there is no file path chosen.
		else{
							
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	public void suggestFollowers(ActionEvent action){
		//Check if a file has been chosen then clears the text flow output box.
		if(fileExist){
									
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
									
									
			networkAnalysis analysis = new networkAnalysis(graph);
			String displayedString = analysis.suggestFollowers();
									
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(displayedString);
			tfOut.getChildren().add(t);
		}
								
		//Give an error when there is no file path chosen.
		else{
									
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	public void searchByWord(ActionEvent a) {
		if(fileExist){
			//System.out.println(wordSearch.getText());
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
			if(wordSearch.getText().length()==0) {
				return;
			}
			postFinder pf = new postFinder(searchStringXML.toString());
			LinkedList<LinkedList<String>> posts = pf.searchByWord(wordSearch.getText());
			StringBuilder postsData = new StringBuilder();
			if(posts.size()==0) {
				postsData.append("No posts found");
			}
			for(LinkedList<String> post : posts) {
				postsData.append("This was published by: "+post.get(0)+"\n");
				postsData.append("topics:");
				for(int i=2;i<post.size();i++) {
					postsData.append(" "+post.get(i)+",");
				}
				postsData.deleteCharAt(postsData.length()-1);
				postsData.append("\nbody:\n");
				postsData.append(post.get(1));
				postsData.append("\n==================================\n");

			}
			
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(postsData.toString());
			tfOut.getChildren().add(t);
		}
		
		//Give an error when there is no file path chosen.
		else{
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	public void searchByTopic(ActionEvent a) {
		
		if(fileExist){	
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
			if(topicSearch.getText().length()==0) {
				return;
			}
			postFinder pf = new postFinder(searchStringXML.toString());
			LinkedList<LinkedList<String>> posts = pf.searchByTopic(topicSearch.getText());
			StringBuilder postsData = new StringBuilder();
			if(posts.size()==0) {
				postsData.append("No posts found");
			}
			for(LinkedList<String> post : posts) {
				postsData.append("This was published by: "+post.get(0)+"\n");
				postsData.append("topics:");
				for(int i=2;i<post.size();i++) {
					postsData.append(" "+post.get(i)+",");
				}
				postsData.deleteCharAt(postsData.length()-1);
				postsData.append("\nbody:\n");
				postsData.append(post.get(1));
				postsData.append("\n==================================\n");

			}
			
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(postsData.toString());
			tfOut.getChildren().add(t);
		}
		
		//Give an error when there is no file path chosen.
		else{
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}
	
	public void drawGraph(ActionEvent a) {
		Map<Integer,ArrayList<Integer>> map = graph.getGraph();
		StringBuilder digraph=new StringBuilder();
		for(Integer key: map.keySet()) {
			ArrayList<Integer> values = map.get(key);
			for(Integer value : values) {
				digraph.append(value+"->"+key+";");
			}
		}
        GraphViz.createDotGraph(digraph.toString(), "DotGraph");
        try
        {
        //constructor of file class having file as argument
        File file = new File("./output.png");
        if(!Desktop.isDesktopSupported())//check if Desktop is supported by Platform or not
        {
        System.out.println("not supported");
        return;
        }
        Desktop desktop = Desktop.getDesktop();
        if(file.exists())         //checks file exists or not
        desktop.open(file);              //opens the specified file
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
	}
	
	public void findMutualFollowers(ActionEvent a) {
		if(fileExist){	
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
			
			if(firstUser.getText().length()==0 || secondUser.getText().length()==0) {
				return;
			}
			
			//get names of first and second user.
			String name1 = firstUser.getText();
			String name2 = secondUser.getText();
			
			networkAnalysis analysis = new networkAnalysis(graph);
			String displayedString = analysis.mutualFollowers(name1, name2);
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(displayedString.toString());
			tfOut.getChildren().add(t);
		}
		
		//Give an error when there is no file path chosen.
		else{
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ERROR");
			String s ="Please Enter XML File First";
			alert.setContentText(s);
			alert.show();

		}
	}

	void savecontent(File file,String latestString) {
		try {
			PrintWriter p = new PrintWriter(file);
			p.write(latestString);
			p.close();
		} 
		catch (FileNotFoundException ex) {
			Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	Graph populateGraph() {
		Graph graph = new Graph();
		jsonString.append("{\n");
		preorderTraverse(root.children.get(0));
		jsonString.append("}\n");
		graph.parseToGraph(jsonString.toString());
		jsonString.setLength(0);		
		System.out.println(graph.getGraph().toString());
		return graph;
	}
	
}
