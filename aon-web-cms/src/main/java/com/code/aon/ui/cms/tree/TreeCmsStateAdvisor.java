package com.code.aon.ui.cms.tree;

import org.richfaces.component.UITree;

import org.richfaces.component.state.TreeStateAdvisor;

import org.richfaces.model.TreeRowKey;

public class TreeCmsStateAdvisor implements TreeStateAdvisor {

	public Boolean adviseNodeOpened(UITree tree) {
		if (!PostbackPhaseListener.isPostback()) {
			Object key = tree.getRowKey();
			TreeRowKey treeRowKey = (TreeRowKey) key;
			if (treeRowKey == null || treeRowKey.depth() <= 2) {
				return Boolean.TRUE;
			}
		}
		return null;
	}

	public Boolean adviseNodeSelected(UITree tree) {
		return null;
	}

}