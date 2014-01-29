package com.esferalia.aon.gwt.fiscal.client.css;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface AonResources extends ClientBundle {
	
	@Source("aon.css")
	AonCSS css();

	@Source("../images/aon-icon-rowSelector.png")
	ImageResource aonIconRowSelector();

	@Source("../images/aon-icon-trash.png")
	ImageResource aonIconTrash();

	@Source("../images/aon-icon-delete.png")
	ImageResource aonIconDelete();

	@Source("../images/aon-icon-check.png")
	ImageResource aonIconCheck();
	
	@Source("../images/aon-icon-checked.png")
	ImageResource aonIconChecked();
	
	@Source("../images/aon-icon-enterprise.png")
	ImageResource aonIconEnterprise();
	
	@Source("../images/aon-icon-list-data.png")
	ImageResource aonListData();

	@Source("../images/aon-timer.gif")
	ImageResource aonTimer();
	
	@Source("../images/aon-aeat.png")
	ImageResource aonAeat();

}

