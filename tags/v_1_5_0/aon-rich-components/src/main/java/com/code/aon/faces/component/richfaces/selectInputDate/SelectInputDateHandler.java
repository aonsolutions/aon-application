package com.code.aon.faces.component.richfaces.selectInputDate;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class SelectInputDateHandler extends AonAjaxInputHandler {

	private static final String INPUT_STYLE_CLASS = "inputClass";
	private static final String DATE_PATTERN_ATTRIBUTE = "datePattern";
	private static final String POPUP_DATE_FORMAT_ATTRIBUTE = "popupDateFormat";

	public SelectInputDateHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected String getInputStyleClass() {
		return INPUT_STYLE_CLASS;
	}

	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		// TODO: Revisar en futuras versiones de Rich Faces (>3.1.3)
		// El attributo "dataPattern" del componente no funciona bien si se le pasa un
		// Value Expression, pero pasandolo como literal no da problema
		fixDatePattern(ctx, (UIComponent) instance);
	}
	
	private void fixDatePattern(FaceletContext ctx, UIComponent c) {
		TagAttribute pattern = getAttribute(POPUP_DATE_FORMAT_ATTRIBUTE);
		if ( pattern != null ) {
			String value = pattern.getValue(ctx);
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), c, DATE_PATTERN_ATTRIBUTE, value);
		}
	}
	
}
