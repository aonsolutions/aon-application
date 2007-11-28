/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package es.code.cdr.ui.components.tree;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Enumeration;

/**
 * <p>The TreeBean class is the backing bean for the Tree Component showcase
 * demonstration. It is used to store and display the selected tree node</p>
 *
 * @see NodeUserObject
 * @since 0.3.0
 */
public class TreeBean {

    // tree default model, used as a value for the tree component
    private DefaultTreeModel model;
    private DefaultMutableTreeNode rootTreeNode;

    // label count increases one for each new node
    private int labelCount = 0;

    // selected node name
    private String selectedNode = "";

    // object reference used to delete and copy the node
    private NodeUserObject selectedNodeObject = null;

    // list of components the user can add to the tree
    private SelectItem[] componentList = {
            new SelectItem(new Integer(1), "outputText"),
            new SelectItem(new Integer(2), "inputText"),
            new SelectItem(new Integer(3), "commandButton")
    };

    // backing for the drop down of components
    private Integer componentToAdd = new Integer(1);

    /**
     * Gets the component list.
     *
     * @return the component list
     */
    public SelectItem[] getComponentList() {
        return componentList;
    }

    /**
     * Sets the component list.
     *
     * @param componentList the new component list
     */
    public void setComponentList(SelectItem[] componentList) {
        this.componentList = componentList;
    }

    /**
     * Gets the component to add index.
     *
     * @return the component to add index
     */
    public Integer getComponentToAdd() {
        return componentToAdd;
    }

    /**
     * Sets the component to add index.
     *
     * @param componentToAdd the new component to add index
     */
    public void setComponentToAdd(Integer componentToAdd) {
        this.componentToAdd = componentToAdd;
    }

    /**
     * Gets the tree node.
     *
     * @return the tree node
     */
    public NodeUserObject getSelectedNodeObject() {
        return selectedNodeObject;
    }

    /**
     * Sets the tree node.
     *
     * @param selectedNodeObject the new tree node
     */
    public void setSelectedNodeObject(NodeUserObject selectedNodeObject) {
        this.selectedNodeObject = selectedNodeObject;
        componentToAdd = selectedNodeObject.getComponentType();
    }

    /**
     * Deletes the selected tree node. The node object reference is set to null
     * so that the delete and copy buttons will be disabled.
     *
     * @param event that fired this method
     * @see #isDeleteDisabled(), isCopyDisabled()
     */
    public void deleteSelectedNode(ActionEvent event) {
        // can't delete the root node; this check is a failsafe in case
        // the delete method is somehow activated despite the button being disabled
        if (selectedNodeObject != null && !selectedNode.equals("Node 1")) {

            // get the node we want to delete
            NodeUserObject nodeToDelete = selectedNodeObject;

            // update the selected state, try to set it to parent of deleted
            // node if possible.
            if (selectedNodeObject.getWrapper().getParent() != null){
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)selectedNodeObject.getWrapper().getParent();
                selectedNodeObject = (NodeUserObject)node.getUserObject();
                selectedNode = selectedNodeObject.getLabel();
            }else{
                selectedNode = "";
                selectedNodeObject = null;
            }

            // delete the node.
            nodeToDelete.deleteNode(event);
            // update the icons for the newly changes state.
            updateNodeIcon(selectedNodeObject);
        }


    }
    
    /**
     * changes the value for the selected node, this method is activated
     * when the user changed the information for a input text box.
     *
     *@param the ValueChangeEvent which holds the value
     *
     */
    public void changedValue(ValueChangeEvent vce) {
        if(selectedNodeObject != null) {
            Object nvalue = vce.getNewValue();
            if(nvalue instanceof String) {
                selectedNodeObject.setValue((String)nvalue);
                
            }
        }
    }
    
    /**
     * changes the component type that is defined by the users selection
     *
     *@param the ValueChangeEvent which holds the desired component
     */
    public void componentTypeChanged(ValueChangeEvent vce) {
        if (selectedNodeObject != null) {
            Object nvalue = vce.getNewValue();
            if (nvalue instanceof Integer) {
                selectedNodeObject.setComponentType((Integer) nvalue);
            }
        }
    }

    /**
     * Copies the selected node in the tree.
     *
     * @param event that fired this method
     */
    public void copySelectedNode(ActionEvent event) {
        if (selectedNodeObject != null)
            selectedNodeObject.copyNode();
    }
    
    /**
     * Adds a new node to the tree in determined position, either below a non 
     * root node or under the root node.
     *
     *@param event that fired this method
     */
    public void addSelectedNode(ActionEvent event) {

        // new object to add 
        DefaultMutableTreeNode treeNode;
        NodeUserObject node;

        // Add the new node under the currently selected.
        if (selectedNodeObject != null){
            // copies node and adds it under the selected node
            node = selectedNodeObject.copyNode();
            // update containing component
            node.setComponentType(componentToAdd);
            // set selected to newly added node.
            selectedNodeObject = node;
            selectedNode = node.getLabel();
            selectedNodeObject.setValue(node.generateValues());
        }
        // otherwise create a new node below the root node.
        else{
            treeNode = new DefaultMutableTreeNode();
            node = new NodeUserObject(treeNode, this);
            node.setComponentType(componentToAdd);
            treeNode.setUserObject(node);

            selectedNodeObject = node;
            selectedNode = node.getLabel();
            selectedNodeObject.setValue(node.generateValues());
            // add the new node to the end of the tree
            rootTreeNode.add(treeNode);
        }

        // update the icons for the newly changes state.
        updateNodeIcon(node);
    }
    
    /**
     * Updates the node icon in the UI to reflect changes to the node
     *
     *@param the node object to change
     */
    private void updateNodeIcon(NodeUserObject node){

        Enumeration children = rootTreeNode.depthFirstEnumeration();
        DefaultMutableTreeNode tmpNode;
        Object tmp;
        while (children.hasMoreElements()){
            tmp = children.nextElement();
            if (tmp instanceof DefaultMutableTreeNode){
                tmpNode = (DefaultMutableTreeNode)tmp;
                if (tmpNode.isLeaf()){
                    ((NodeUserObject)(tmpNode.getUserObject())).setLeaf(true);
                } else{
                    ((NodeUserObject)(tmpNode.getUserObject())).setLeaf(false);
                }
            }

        }

    }

    /**
     * Determines whether the delete button is disabled. The delete button
     * should be disabled if the node that was previously selected was deleted
     * or if no node is otherwise selected. The root node is a special case and
     * cannot be deleted.
     *
     * @return the disabled status of the delete button
     */
    public boolean isDeleteDisabled() {
        //can't delete the root node
        return (selectedNode == null || selectedNode.equals("Node 1") ||
                selectedNodeObject == null);
    }

    /**
     * Determines whether the copy button is disabled. This should only occur
     * when there is no node selected, which occurs at initialization and when a
     * node is deleted.
     *
     * @return the disabled status of the copy button
     */
    public boolean isCopyDisabled() {
        return (selectedNode == null || selectedNodeObject == null);
    }

    /**
     * Construction the default tree structure by combining tree nodes.
     */
    public TreeBean() {
        rootTreeNode = new DefaultMutableTreeNode();
        NodeUserObject rootObject = new NodeUserObject(rootTreeNode, this);
        rootTreeNode.setUserObject(rootObject);

        model = new DefaultTreeModel(rootTreeNode);

        for (int i = 0; i < 3; i++) {
            DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode();
            NodeUserObject branchObject = new NodeUserObject(branchNode, this);
            branchNode.setUserObject(branchObject);
            branchObject.setLeaf(true);
            rootTreeNode.add(branchNode);

        }
    }

    /**
     * Gets the tree's default model.
     *
     * @return tree model.
     */
    public DefaultTreeModel getModel() {
        return model;
    }

    /**
     * Sets the tree's default model.
     *
     * @param model new default tree model
     */
    public void setModel(DefaultTreeModel model) {
        this.model = model;
    }

    /**
     * Gets the selected node display text.
     *
     * @return selected node display text.
     */
    public String getSelectedNode() {
        return selectedNode;
    }

    /**
     * Sets the selected node. This changes a local instance variable used for
     * display, it does not directly change the tree node state.
     *
     * @param selectedNode selected node text.
     */
    public void setSelectedNode(String selectedNode) {
        this.selectedNode = selectedNode;
    }

    /**
     * Increment the label count and return it.
     *
     * @return the new label count
     */
    public int getIncreasedLabelCount() {
        return ++labelCount;
    }
}
