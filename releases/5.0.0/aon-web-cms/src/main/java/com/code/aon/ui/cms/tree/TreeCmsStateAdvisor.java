package com.code.aon.ui.cms.tree;

import org.richfaces.component.UITree;
import org.richfaces.component.state.TreeStateAdvisor;

public class TreeCmsStateAdvisor implements TreeStateAdvisor {

	public Boolean adviseNodeOpened(UITree tree) {
		if (!PostbackPhaseListener.isPostback()) {
			return Boolean.TRUE;
		}
		return null;
	}

	public Boolean adviseNodeSelected(UITree tree) {
		return null;
	}

}