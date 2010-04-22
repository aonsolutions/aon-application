/*
 * Created on 02-sep-2006
 *
 */
package com.code.aon.common.tree;


/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 04-sep-2006
 * @since 1.0
 *
 */

public interface IMutableTreeNode extends ITreeNode {

    /**
     * Add the given child to the children of this node.
     * This will set this node as the parent of the child using {#setParent}.
     */
    void insert(IMutableTreeNode child);

    /**
     * Add the given child to the children of this node at index.
     * This will set this node as the parent of the child using {#setParent}.
     */
    void insert(IMutableTreeNode child, int index);

    /**
     * Remove the child at the given index.
     */
    void remove(int index);

    /**
     * Remove the given node.
     */
    void remove(IMutableTreeNode node);

    /**
     * Remove this node from its parent.
     */
    void removeFromParent();

    /**
     * Set the parent node.
     */
    void setParent(IMutableTreeNode parent);

    /**
     * Sets the user object of this node.
     */
    void setUserObject(Object object);

}
