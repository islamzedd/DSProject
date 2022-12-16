package xml;

import java.io.IOException;

public class XMLTree {
	private TreeNode root;
	
    public XMLTree(TreeNode root)
    {
        this.root = root;
    }
    
    public TreeNode parseXML(String stringToBeParsed,int n, TreeNode node) throws IOException {
    	char c;
    	
        while (n <stringToBeParsed.length() )
        {
        	//get character at index and increment index
            c = (char)stringToBeParsed.charAt(n++);
            
            //constantly check if we reached the end of the file each time n is incremented
            //this will repeat often
            if (n > stringToBeParsed.length() ) break;
            
            //if its an opening tag
            if (c == '<' && stringToBeParsed.charAt(n) != '/' ) 
            {
            	//create a node for it and add it as a child to the root
                String name = "";
                String value = "";
                TreeNode child = new TreeNode(name,value, node.depth + 1, node);
                node.children.add(child);
                c = (char) stringToBeParsed.charAt(n++);
                if (n > stringToBeParsed.length() ) break;
                
                //until you reach the end of the opening tag
                while (c != '>')
                {
                	//skip white space
                    if (c == '\r' || c == '\t' || c == '\n')
                    {
                        c = (char)stringToBeParsed.charAt(n++);
                        if (n > stringToBeParsed.length() ) break;
                        continue;
                    }
                    
                    //append characters to tag name
                    name += c;
                    c = (char)stringToBeParsed.charAt(n++);
                    if (n > stringToBeParsed.length() ) break;
                }
                //set the tag name
                child.name=name;
                
                //skip white space between opening tag and text of tag
                while (stringToBeParsed.charAt(n) == '\n' || stringToBeParsed.charAt(n) == '\t' || stringToBeParsed.charAt(n) == '\r' || stringToBeParsed.charAt(n) == ' ')
                {
                    c = (char)stringToBeParsed.charAt(n++);
                    if (n > stringToBeParsed.length() ) break;
                }
                
                //if you reach another tag recursively parse it
                if (stringToBeParsed.charAt(n) == '<')
                {
                    parseXML(stringToBeParsed,n ,child);
                    break;
                }
                else
                {
                    c = (char)stringToBeParsed.charAt(n++);
                    if (n > stringToBeParsed.length() ) break;
                    
                    //until we reach a tag
                    while (c != '<')
                    {
                    	
                    	//skip white space
                        if (c == '\r' || c == '\t' ||c == '\n' ||
                        		(c == ' ' && stringToBeParsed.charAt(n) == ' ') ||
                        		(c == ' ' && stringToBeParsed.charAt(n) == '<'))
                        {
                            c = (char)stringToBeParsed.charAt(n++);
                            if (n > stringToBeParsed.length() ) break;
                            continue;
                        }
                        
                        //append to the tag value
                        value += c;
                        c = (char)stringToBeParsed.charAt(n++);
                        if (n > stringToBeParsed.length() ) break;
                    }
                    
                    /*as soon as you reach another tag set the child value to the current value string
                    be it closing or opening where closing means xml is correct and opening indicates
                    a missing tag probably*/
                    child.value=value;
                }

            }
            
            //if its a closing tag
            else if (c == '<' && stringToBeParsed.charAt(n) == '/')
            {
                //go back to parent and recursively parse it starting after the closing tag
            	//so that other children can be added to the tree left to right
                parseXML(stringToBeParsed, n,node.parent);
                break;
            }
        }
    return root;
    }
}
