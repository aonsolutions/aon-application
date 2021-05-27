package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface AonGwtDocumentResources extends ClientBundle{

	@Source("aon-gwt-document.css")
	@CssResource.NotStrict
	AonGwtDocumentCSS css();
	
}
