package com.code.aon.faces.component.inputFile;

import java.util.EventObject;

import com.code.aon.faces.component.AonComponentHandler;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class InputFileHandler extends AonComponentHandler{

	@Override
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		m.addRule( new MethodRule("progressListener", null, new Class[] {EventObject.class}) );
		return m;
	}
	
	public InputFileHandler(ComponentConfig config) {
		super(config);
	}
}