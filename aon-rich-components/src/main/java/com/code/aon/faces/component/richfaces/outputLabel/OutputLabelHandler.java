package com.code.aon.faces.component.richfaces.outputLabel;

import static com.code.aon.faces.controller.IRichConstants.LABELS_MAP;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class OutputLabelHandler extends AonComponentHandler implements IRichFacesTags {
	
	public OutputLabelHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		Map map = (Map) FaceletUtil.getRequestValue(ctx, LABELS_MAP);		
		if (map == null) {
			map = new HashMap();
			FaceletUtil.putRequestValue(ctx, LABELS_MAP, map);
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
