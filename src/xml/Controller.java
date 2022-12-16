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
		
	}
	
	public void addXMLFormat(ActionEvent action){
		 
	}
	 
	public void minify(ActionEvent action){
		StringBuilder minifiedString = new StringBuilder();				//reinstantiation to reset the string when we minify again or another file
		//Check if a file has been chosen then clears the text flow output box.
		if(fileExist){
			
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
			
			//Iterate over the length of latestString removing all \n in the file to be written in one line. then stores it in minifiedString.
			for(int i = 0; i < latestString.length(); i++){
				if(latestString.charAt(i) != '\n')
					minifiedString.append(latestString.charAt(i));
			}
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(minifiedString.toString());
			tfOut.getChildren().add(t);
			latestString = minifiedString;
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
