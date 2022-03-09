package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.css.AonProgressCSS;
import com.esferalia.aon.gwt.common.client.css.AonProgressResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;

public class AonProgress extends ResizeComposite implements ProvidesResize {

	
	interface Binder extends UiBinder<HTMLPanel, AonProgress> {}

	private static final Binder binder = GWT.create(Binder.class);
	public static final AonProgressCSS CSS = GWT.<AonProgressResources> create(AonProgressResources.class).css();

	
	@UiField HTMLPanel progressBar;
	
	@UiField HTMLPanel progressBarValue;
	
	private double maxValue;

	public AonProgress() {
		GWT.<AonProgressResources> create(AonProgressResources.class).css().ensureInjected();
		initWidget(binder.createAndBindUi(this));
		
		maxValue = 100.0;
	}	
	
	
	public void setIndeterminate(boolean value) {
		if(value)
			progressBarValue.addStyleName(CSS.indeterminate());
		else 
			progressBarValue.removeStyleName(CSS.indeterminate());
	}
	
	public void setMax(double maxValue) {
		this.maxValue = maxValue;
	}
	
	public double getMax() {
		return maxValue;
	}
	
	public void setColor(String hex) {
		if(hex!=null)
			progressBarValue.getElement().getStyle().setBackgroundColor(hex);
	}
	
	public void setValue(double value) {
		if(value <= maxValue)
			progressBarValue.setWidth(String.valueOf( (value * 100) / maxValue  ));
	}

}
