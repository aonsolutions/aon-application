package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface ViewerResources extends ClientBundle {
	@Source("viewer.css")
	@CssResource.NotStrict
	ViewerCSS css();
}
