package com.code.aon.ui.hyperview.controller.myfaces;

import javax.swing.tree.DefaultMutableTreeNode;

import com.code.aon.hyperview.player.IHyperViewPlayerNode;
import com.code.aon.hyperview.player.IHyperViewPlayerNodeFactory;

public class MyFacesHyperViewPlayerNodeFactory implements IHyperViewPlayerNodeFactory {

	public synchronized IHyperViewPlayerNode newTreeNode(String id, Object userObject) {
		return new MyFacesHyperViewPlayerNode(id, (DefaultMutableTreeNode) userObject);
	}

}
