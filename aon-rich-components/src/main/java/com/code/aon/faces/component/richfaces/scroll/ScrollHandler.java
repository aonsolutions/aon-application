package com.code.aon.faces.component.richfaces.scroll;

import java.net.URL;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.AonComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

import jakarta.el.VariableMapper;

public class ScrollHandler extends AonComponentHandler {

	public ScrollHandler(ComponentConfig config) {
		super(config);
	}
	
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
	}
	
	@Override
	protected void insertTemplate(FaceletContext ctx, UIComponent parent, URL template, VariableMapper newMapper) {
		super.insertTemplate(ctx, parent, template, newMapper);
	}
	
	
	
	

}
