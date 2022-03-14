package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.CssResource;

public interface AonProgressCSS extends CssResource {

	@ClassName("progress-bar")
	String progressBar();

	@ClassName("progress-bar-value")
	String progressBarValue();
	
	@ClassName("indeterminate")
	String indeterminate();

}
