package com.code.aon.faces.component.richfaces.outputLabel;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.component.UIViewRoot;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class OutputLabelHandler extends AonComponentHandler implements IRichFacesTags {
	
	public static final String LABELS_MAP = "com.code.aon.faces.OutputLabel.map";

	public OutputLabelHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);		
		Map map = (Map) root.getAttributes().get(LABELS_MAP);
		if (map == null) {
			map = new HashMap();
			root.getAttributes().put(LABELS_MAP, map);
		}
		
		TagAttribute _for = getAttribute("for");
		if ( _for != null ) {
			TagAttribute value = getAttribute(VALUE);
			if (value != null) {
				map.put(_for.getValue(ctx), (UIOutput) c );					
			}
		}
	}

}
