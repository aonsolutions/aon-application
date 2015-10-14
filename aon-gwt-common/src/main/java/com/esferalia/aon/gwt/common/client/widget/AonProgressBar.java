package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.aria.client.Roles;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;


public class AonProgressBar extends SimplePanel{
	String text;
	Label label;
	Integer progress;
	
	public AonProgressBar() {
		
		super();
		Roles.getProgressbarRole().set(getElement());
		setAriaValuenow(0);
		setAriaValuemin(0);
		setAriaValuemax(100);
		setLabel(new Label("0%"));
		setProgress(0);
		add(getLabel());
		addStyleName(AON.AON_CSS.aonProgressBar());
		addStyleName(AON.AON_CSS.aonProgressBarStriped());
		addStyleName(AON.AON_CSS.active());
	}
	
	public void setAriaValuenow(Integer valuenow){
		Roles.getProgressbarRole().setAriaValuenowProperty(getElement(), valuenow);
	}
	
	public Integer getAriaValuenow(){
		String valuenow = Roles.getProgressbarRole().getAriaValuenowProperty(getElement());
		return Integer.parseInt(valuenow);
	}
	
	public void setAriaValuemin(Integer valuemin){
		Roles.getProgressbarRole().setAriaValueminProperty(getElement(), valuemin);
	}
	
	public Integer getAriaValuemin(){
		String valuemin = Roles.getProgressbarRole().getAriaValueminProperty(getElement());
		return Integer.parseInt(valuemin);
	}
	
	public void setAriaValuemax(Integer valuemax){
		Roles.getProgressbarRole().setAriaValuemaxProperty(getElement(), valuemax);
	}
	
	public Integer getAriaValuemax(){
		String valuemax = Roles.getProgressbarRole().getAriaValuemaxProperty(getElement());
		return Integer.parseInt(valuemax);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;	
		getLabel().setText(text);
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
		setText(label.getText());
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
		setWidth(progress+"%");
		setText(progress+"%");
		setAriaValuenow(progress);
	}
	
	
	


}
