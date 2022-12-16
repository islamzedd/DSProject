package xml;

import java.util.ArrayList;

public class TreeNode {
	public String name;
	public String value = "";
	public TreeNode parent;
	public int depth;
	public ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	public String closingBracket;
	public boolean visited;

	public TreeNode(String name, String value, int depth, TreeNode parent)
    {
        this.name = name;
        this.value = value;
        this.depth = depth;
        this.parent = parent;
    }
}
