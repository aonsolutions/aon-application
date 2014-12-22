package com.esferalia.aon.gwt.common.client.css;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface AonResources extends ClientBundle, Images {
	
	@Source("aon.css")
	@CssResource.NotStrict
	AonCSS css();
	
	@Source("aonDataGrid.css")
	@CssResource.NotStrict
	AonDataGrid aonDataGrid();


	@Source("images/aon-icon-rowSelector.png")
	ImageResource aonIconRowSelector();

	@Source("images/aon-icon-trash.png")
	ImageResource aonIconTrash();

	@Source("images/aon-icon-delete.png")
	ImageResource aonIconDelete();

	@Source("images/aon-icon-check.png")
	ImageResource aonIconCheck();
	
	@Source("images/aon-icon-checked.png")
	ImageResource aonIconChecked();
	
	@Source("images/aon-icon-enterprise.png")
	ImageResource aonIconEnterprise();
	
	@Source("images/aon-icon-list-data.png")
	ImageResource aonListData();

	@Source("images/aon-timer.gif")
	ImageResource aonTimer();
	
	@Source("images/aon-aeat.png")
	ImageResource aonAeat();

	@Source("images/changed.png")
	ImageResource aonChanged();

	@Source("images/input-warn.png")
	ImageResource aonInputError();
	
	@Source("images/curly-lt.png")
	ImageResource aonCurlyLT();

	// ------------------------------------------------------------------------
	
		
}

