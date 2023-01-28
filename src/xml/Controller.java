package xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	private boolean fileExist = false;
	StringBuilder str_in = new StringBuilder();  
    StringBuilder latestString = new StringBuilder();
    StringBuilder jsonString = new StringBuilder();
    TreeNode root = null;
	
	void init(Stage s) {
        stage=s;
    }
	
	public void chooseXMLFile(ActionEvent actionevent){
		FileChooser filechooser = new FileChooser();
        File file = filechooser.showOpenDialog(stage); 		//file path
        
        str_in = new StringBuilder();					//reinstantiation to reset the string when we choose another file
        latestString = new StringBuilder();				//reinstantiation to reset the string when we choose another file
        
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
                    
                    
                    String stringToBeParsed =  str_in.toString();
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
        		
        		//Creating a Text text to be displayed in the text flow output.
        		Text text = new Text(str_in.toString());
        		tfIn.getChildren().add(text);
        		
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
		char c;
		int i = 0;			//index of char in latestString
		int line = 1;
		char temp;
		c = latestString.charAt(i);
		temp = latestString.charAt(i);
		String tag = "";
		String data = "";
		Stack<String> openTags = new Stack<String>();	//stack lel opening tags
		System.out.println(latestString.length());
		while(i < latestString.length()) {
			
			if(i >= latestString.length()) break;
			
			//Section 1: open tag
			if(c == '<' && (char)latestString.charAt(i) != '/') { 
				//getting tag name
				while(latestString.charAt(i) != '>') {
					c = latestString.charAt(i++);
					temp = latestString.charAt(i);
					if((c != '<') && (c != '>') && (c != '/')) {
						tag += c;
					}
				}
				//pushing tag into stack
				openTags.push(tag);
				tag = "";
				c = latestString.charAt(++i);
				temp = latestString.charAt(i);
			}
			
			//Section 2: skip tabs and spaces and new lines
			while (c == '\n' || c == '\t' || c == '\r' || c == ' ') {
                c = latestString.charAt(i++);
                if (i >= latestString.length() ) break;
                if(c == '\n')
                	line++;
            }
            if (i >= latestString.length() ) break;

			//Section 3: data
			if(c != '<') {
				//skip data
				while (c != '<')
	            {
	                c = latestString.charAt(i++);
	                temp = latestString.charAt(i);
	                data += c;
	                if (i >= latestString.length() ) break;
	                if (c == '\n')
	                	line++;
	            }
				//First Case: open tag after data
				if(c == '<' && (char)latestString.charAt(i) != '/') {
					//data in the beginning
					if (openTags.isEmpty()) {
						System.out.print("Error in line ");
						System.out.print(line);
						System.out.println(": You can't write data in the beginning");
					}
					//data not in the beginning
					else {
						//open tag was a data tag
						if (openTags.peek().equals("name") || openTags.peek().equals("id") || openTags.peek().equals("body") || openTags.peek().equals("post")) {
							System.out.print("Error in line ");
							System.out.print(line - 1);
							System.out.print(": </");
							System.out.print(openTags.peek());
							System.out.println("> missing");
							//maybe handle error here
							openTags.pop();
						}
						//open tag isn't a data tag
						else {
							System.out.print("Error in line ");
							System.out.print(line - 1);
							System.out.println(": You can't write data here");
						}
						
					}
				}
				
				//Second Case: closing tag after data
				else if(c == '<'  && (char)latestString.charAt(i) == '/') {
					//getting the tag name
					while(latestString.charAt(i) != '>') {
						c = (char)latestString.charAt(i++);
						temp = (char)latestString.charAt(i);
						if((c != '<') && (c != '>') && (c != '/')) {
							tag += c;
						}
					}
					//if XML starts with missing opening data tag
					if (openTags.isEmpty()) {
						System.out.print("Error in line ");
						System.out.print(line - 1);
						System.out.print(": <");
						System.out.print(tag);
						System.out.println("> missing in the beginning");
						c = latestString.charAt(++i);
					}
					else {
						//correct closing after data
						if(tag.equals(openTags.peek())) {
							openTags.pop();
							tag = "";
							c = (char)latestString.charAt(++i);
						}
						//not correct closing after data
						else {
							//first case: two mismatch data tags and giving favor for last
							if ((tag.equals("name") || tag.equals("id") || tag.equals("body") || tag.equals("post"))
							&&(openTags.peek().equals("name") || openTags.peek().equals("id") || openTags.peek().equals("body") || openTags.peek().equals("post"))) {
								System.out.print("Error in line ");
								System.out.print(line);
								System.out.print(": <");
								System.out.print(tag);
								System.out.print("> instead of <");
								System.out.print(openTags.peek());
								System.out.println(">");
								openTags.pop();
								tag = "";
								c = (char)latestString.charAt(++i);
							}
							//second case: there's no opening data tag
							else if((tag.equals("name") || tag.equals("id") || tag.equals("body") || tag.equals("post"))
							&&!(openTags.peek().equals("name") || openTags.peek().equals("id") || openTags.peek().equals("body") || openTags.peek().equals("post"))){
								System.out.print("Error in line ");
								System.out.print(line);
								System.out.print(": <");
								System.out.print(tag);
								System.out.println("> missing in the beginning");
								tag = "";
								c = (char)latestString.charAt(++i);
							}
							//third case: there's no closing data tag
							else {
								System.out.print("Error in line ");
								System.out.print(line - 1);
								System.out.print(": </");
								System.out.print(openTags.peek());
								System.out.println("> missing");
								openTags.pop();						//we remove the data opening tag
								//there's no error
								if(tag.equals(openTags.peek())) {
									openTags.pop();
									tag = "";
									c = (char)latestString.charAt(++i);
								}
								//missing closing tags till we find it
								else if(!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
									while (!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
										System.out.print("Error in line ");
										System.out.print(line - 1);
										System.out.print(": </");
										System.out.print(openTags.peek());
										System.out.println("> missing");
										openTags.pop();
									}
									openTags.pop();
									tag = "";
									c = (char)latestString.charAt(++i);
								}
								else {
									System.out.println("extra");
									tag = "";
									c = (char)latestString.charAt(++i);
								}
							}
							
						}
					}
				}
			}
			
			//Section 3: closing tag
			if(c == '<'  && (char)latestString.charAt(i) == '/') {
				//getting the tag name
				while(latestString.charAt(i) != '>') {
					c = (char)latestString.charAt(i++);
					temp = (char)latestString.charAt(i);
					if((c != '<') && (c != '>') && (c != '/')) {
						tag += c;
					}
				}
				//closing tag in the beginning
				if (openTags.isEmpty()) {
					System.out.print("Error in line ");
					System.out.print(line - 1);
					System.out.print(": <");
					System.out.print(tag);
					System.out.println("> missing in the beginning");
					c = (char)latestString.charAt(++i);
				}
				else {
					//correct closing tag
					if(tag.equals(openTags.peek())) {
						openTags.pop();
						tag = "";
						c = (char)latestString.charAt(++i);
					}
					//error handling
					//missing closing tags till we find it
					else if(!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
						while (!tag.equals(openTags.peek()) && openTags.search(tag) != -1) {
							System.out.print("Error in line ");
							System.out.print(line - 1);
							System.out.print(": </");
							System.out.print(openTags.peek());
							System.out.println("> missing");
							openTags.pop();
						}
						if (tag.equals(openTags.peek())) {
							openTags.pop();
							tag = "";
						}
						c = (char)latestString.charAt(++i);
					}
					
					else {
						System.out.print("Error in line ");
						System.out.print(line);
						System.out.print(": </");
						System.out.print(tag);
						System.out.println("> might be missing an openning tag");
						tag = "";
						c = (char)latestString.charAt(++i);
					}
				}	
			}
		}
		while(!openTags.isEmpty()) {
			System.out.print("Error in line ");
			System.out.print(line - 1);
			System.out.print(": </");
			System.out.print(openTags.peek());
			System.out.println("> missing");
			openTags.pop();
		}
		System.out.println(line);
		System.out.println(latestString);
		//lw value n2so closing w b3do opening zwd closing 3latol
		 //lw 3ndk values yb2a closing 3latol lw closing l 7aga fel stack sbo w zwd lw m4 l7aga fel stack ems7 w 3dl	 
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
		 
	}
	 
	public void decompress(ActionEvent a) {
		 
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
}
