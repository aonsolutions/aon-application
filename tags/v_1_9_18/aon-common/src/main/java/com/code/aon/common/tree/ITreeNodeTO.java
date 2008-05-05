package com.code.aon.common.tree;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.tree.enumeration.TreeNodeType;

/**
 * This class represents a Transfer Object in a Tree.
 *  
 * @author Consulting & Development. 
 *
 */
public interface ITreeNodeTO extends ITransferObject{

	/**
	 * Return the node identifier.
	 * 
	 * @return The node identifier.
	 */
	public Integer getId();

	/**
	 * Return the parent.
	 * @return The parent.
	 */
	public ITreeNodeTO getParent();

	/**
	 * Assign the parent.
	 * 
	 * @param parent
	 */
	public void setParent( ITreeNodeTO parent);

	/**
	 * Return the label.
	 * 
	 * @return The label.
	 */
	public String getLabel();

	/**
	 * Return the node type. This can be a ROOT, GROUP or LEAF.
	 * 
	 * @return The node type. 
	 */
	public TreeNodeType getType();

	/**
	 * Assign the node type.
	 * @param type
	 */
	public void setType(TreeNodeType type);
	
	
}
