/*
 * Author: Chaitanya Malladi
 * Class for the tree structure
 */
package assignment2;
import java.util.ArrayList;

public class Tree {
	String node;
	String parent;
	ArrayList<Tree> children = new ArrayList<Tree>();
	
	//is leaf?
	public boolean isLeaf(){
		if(children.size()==0) return true;
		return false;
	}
	
	//Function to add element to tree
	public void addChild(String parent, Tree child){
		this.findNode(parent).children.add(child);
		this.parent = parent;
	}
	
	//Find a node in the tree
	//Using DFS
	public Tree findNode(String node) {
		if(this.node.equals(node)){
			return this;
		}
		else {
			for(int i = 0;i<this.children.size();i++) {
				if(this.children.get(i).findNode(node)!=null)
					return this.children.get(i).findNode(node);
			}
		}
		return null;
	}

	//Create tree with "node" at the root
	public Tree(String node){
		this.node = node;
	}
	
}
