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

import com.icesoft.faces.component.tree.IceUserObject;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * <p>The <code>NodeUserObject</code> represents a nodes user object.  This
 * particular IceUserobject implementation stores extra information on how many
 * times the parent node is clicked on.  It is also responsible for copying and
 * deleting its self.</p>
 * <p/>
 * <p>In this example pay particularly close attention to the
 * <code>wrapper</code> instance variable on IceUserObject.  The
 * <code>wrapper</code> allows for direct manipulations of the parent tree.
 * </p>
 *
 * @since 1.0
 */
public class NodeUserObject extends IceUserObject {

    // treebean pointer - used to store selected node
    private TreeBean treeBean;

    // message bundle references
    private static String nodeToolTip;

    // index of the component type
    private Integer componentType = new Integer(1);

    // actual node label
    private String label;
    
    // value of the component
    private String value;
    
    
    /**
     * Load resource bundle for displaying proper node labels.
     */
    static {
        Locale locale =
                FacesContext.getCurrentInstance().getViewRoot().getLocale();
        // assign a default local if the faces context has none; shouldn't happen
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        ResourceBundle messages = ResourceBundle.getBundle(
                "es.code.cdr.ui.i18n.menu",//"com.icesoft.icefaces.samples.showcase.resources.messages",
                locale);

        // assign labels
        try {
            if (messages != null) {
                nodeToolTip =
                        "Node Tool Tip";//messages.getString("components.tree.node.tooltip");
            }
        } catch (MissingResourceException e) {
            // eat the error
        }
    }

    /**
     * Creates a new <code>NodeUserObject</code> object.  Default image states
     * are set as well as the expansion of all branch leafs.
     *
     * @param wrapper parent tree node which wrapps this object
     * @param treeBeanPointer callback to parent TreeBean class.
     */
    public NodeUserObject(DefaultMutableTreeNode wrapper,
                          TreeBean treeBeanPointer) {
        super(wrapper);

        treeBean = treeBeanPointer;
        label = generateLabel();
        value = generateValues();

        setLeafIcon("/css/iceCss/images/aon-icon/aon-icon-folder-contracted.png");
        setBranchContractedIcon("/css/iceCss/images/aon-icon/aon-icon-folder-contracted.png");
        setBranchExpandedIcon("/css/iceCss/images/aon-icon/aon-icon-folder-expanded.png");

        setText(label);
        setTooltip(nodeToolTip);
        setExpanded(true);
    }

	/**
     * Generates a label for the node based on an incrementing int.
     *
     * @return the generated label (eg. 'Node 5')
     */
    private String generateLabel() {
        return "Node " + treeBean.getIncreasedLabelCount();
    }

    /**
     * Returns true if this node is the root of the tree. The root is the only
     * node in the tree with a null parent; every tree has exactly one root.
     *
     * @return true if this node is the root of its tree.
     */
    public boolean isRootNode() {
        return getWrapper().isRoot();
    }

    /**
     * Deletes this not from the parent tree.
     *
     * @param event that fired this method
     */
    public void deleteNode(ActionEvent event) {
        ((DefaultMutableTreeNode) getWrapper().getParent())
                .remove(getWrapper());
    }

    /**
     * Copies this node and adds it as a child node.
     *
     * @return the newly created node copy. 
     */
    public NodeUserObject copyNode() {
        DefaultMutableTreeNode clonedWrapper = new DefaultMutableTreeNode();
        NodeUserObject clonedUserObject =
                new NodeUserObject(clonedWrapper, treeBean);
        NodeUserObject originalUserObject =
                (NodeUserObject) getWrapper().getUserObject();
        clonedUserObject.setAction(originalUserObject.getAction());
        clonedUserObject.setBranchContractedIcon(
                originalUserObject.getBranchContractedIcon());
        clonedUserObject.setBranchExpandedIcon(
                originalUserObject.getBranchExpandedIcon());
        clonedUserObject.setExpanded(originalUserObject.isExpanded());
        clonedUserObject.setLeafIcon(originalUserObject.getLeafIcon());
        clonedUserObject.setTooltip(nodeToolTip);
        clonedWrapper.setUserObject(clonedUserObject);
        getWrapper().insert(clonedWrapper, 0);

        return clonedUserObject;
    }

    /**
     * Returns the label associated with the node.
     *
     * @return the label of the node
     */
    public String getLabel() {
        return label;
    }

    /**
     * Registers a user click with this object and updates the selected node in
     * the TreeBean.
     *
     * @param event that fired this method
     */
    public void nodeClicked(ActionEvent event) {
        treeBean.setSelectedNode(this.label);
        treeBean.setSelectedNodeObject(this);
        
    }

    /**
     * Gets the component type.
     *
     * @return the component type
     */
    public Integer getComponentType() {
        return componentType;
    }

    /**
     * Sets the component type.
     *
     * @param componentType the new component type
     */
    public void setComponentType(Integer componentType) {
        this.componentType = componentType;
    }
     
    /**
     * gets the information that is stored in each node.
     *
     * @return the string value 
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * sets the value which is the information stored in the node.
     *
     * @param value to be stored
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    
    /**
     * generates default names for the value attribute depending on the 
     * component type
     *
     * @return the string value
     */
    public String generateValues()
    {
	  return "label";              
    }
    
    
}