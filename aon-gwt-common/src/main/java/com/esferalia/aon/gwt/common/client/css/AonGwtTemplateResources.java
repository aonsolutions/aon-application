package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface AonGwtTemplateResources extends ClientBundle{

	@Source("aon-gwt-template.css")
	@CssResource.NotStrict
	AonGwtTemplateCSS css();
	
}
