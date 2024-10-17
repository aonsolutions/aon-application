package com.code.aon.faces.component.richfaces.outputText;

import static com.code.aon.faces.component.richfaces.lookup.ILookupConstants.ALIAS;

import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlColumn;

import com.code.aon.faces.component.AonComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class OutputTextHandler extends AonComponentHandler {

	public OutputTextHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		super.onComponentPopulated(ctx, c, parent);
		
		try {
			TagAttribute valueTag = getAttribute("value");
			if ( valueTag != null && parent instanceof HtmlColumn column) {
				column.getAttributes().put(ALIAS, valueTag.getValue() );
			}
		} catch ( Exception e ) {
		}
	}

}
