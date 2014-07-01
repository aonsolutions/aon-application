package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;

public interface GWTResources extends ClientBundle {
	@NotStrict
	@Source("gwt.css")
	CssResource css();

	@Source("images/warn.png")
	ImageResource warn();

	@Source("images/aon-menuBar.png")
	ImageResource menuBar();

	@Source("images/aon-tabBar.png")
	ImageResource tabBar();

	@Source("images/checkyes.png")
	ImageResource checkYes();

	@Source("images/button.png")
	ImageResource button();

	@Source("images/public.png")
	ImageResource publiC();

	@Source("images/private.png")
	ImageResource privatE();

	@Source("images/protected.png")
	ImageResource protecteD();
}
