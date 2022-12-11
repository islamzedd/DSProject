package xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
    StringBuilder stringNoTabs = new StringBuilder();
    StringBuilder tempMinifiedString = new StringBuilder();
	
	void init(Stage s) {
        stage=s;
    }
	
	public void chooseXMLFile(ActionEvent actionevent){
		FileChooser filechooser = new FileChooser();
        File file = filechooser.showOpenDialog(stage); 		//file path
        
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
        			//then removes all tabs and empty lines and store it in stringNoTabs to be used later by other functions.
                    while ((line = reader.readLine()) != null) {
                	    str_in.append(line).append("\n");
                        if(line.isEmpty())
                        	continue;
                        line = line.replaceAll("  ","");
                        stringNoTabs.append(line).append("\n");
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
		
		//Check if a file has been chosen then clears the text flow output box.
		if(fileExist){
			
			tfOut.getChildren().clear();
			tfOut.setStyle(" -fx-border-color: Yellow;");			//using a yellow color to highlight usage.
			StringBuilder minifiedString = new StringBuilder();		
			
			//Iterate over the length of stringNoTabs removing all \n in the file to be written in one line. then stores it in minifiedString.
			for(int i = 0; i < stringNoTabs.length(); i++){
				if(stringNoTabs.charAt(i) != '\n')
					minifiedString.append(stringNoTabs.charAt(i));
			}
			
			//Creating a Text t to be displayed in the text flow output.
			Text t = new Text(minifiedString.toString());
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
	 
	public void convertJson(ActionEvent a){
		 
	}
	 
	public void compress(ActionEvent a) {
		 
	}
	 
	public void decompress(ActionEvent a) {
		 
	}
	 
	public void save(ActionEvent action){
	 
	}	 
}
