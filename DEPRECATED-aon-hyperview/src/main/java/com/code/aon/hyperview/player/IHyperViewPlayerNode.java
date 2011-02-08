package com.code.aon.hyperview.player;

import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.code.aon.hyperview.model.HyperViewNode;

public interface IHyperViewPlayerNode {

	String getIdentifier();
	
	void setDescription(String description);

	String getDescription();

	DefaultMutableTreeNode getDefinitionNode();

	HyperViewNode getHyperViewNode();

	Map<String, ContextItem> getParams();

	void setParams(Map<String, ContextItem> params);
	
    int getChildCount();
    
    List getChildren();
    
    boolean isDeployed();
    
    void setDeployed(boolean deployed);

    boolean isHyperCube();
    
    void setParent(IHyperViewPlayerNode parent);
    
    IHyperViewPlayerNode getParent();
}