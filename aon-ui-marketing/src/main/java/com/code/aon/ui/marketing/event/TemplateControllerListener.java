package com.code.aon.ui.marketing.event;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.enumeration.CSSUnit;
import com.code.aon.marketing.Template;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.TemplateController;

public class TemplateControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		TemplateController tc = (TemplateController) event.getController();
		resetWidthValue(tc);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		TemplateController tc = (TemplateController) event.getController();
		updateWidth(tc);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TemplateController tc = (TemplateController) event.getController();
		resetWidthValue(tc);
		initWidth(tc);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TemplateController tc = (TemplateController) event.getController();
		updateWidth(tc);
	}
	
	private void resetWidthValue( TemplateController tc ) {
		tc.setWidth(null);
		tc.setCssUnit(CSSUnit.PX);		
	}
	
	private void initWidth( TemplateController tc ) {
		Template template = (Template) tc.getTo();
		String width = template.getWidth();
		for( CSSUnit cssUnit : CSSUnit.values() ) {
			if ( StringUtils.contains(width, cssUnit.getValue()) ) {
				tc.setCssUnit(cssUnit);
				width = StringUtils.remove(width, cssUnit.getValue());
				break;
			}
		}
		if ( NumberUtils.isNumber(width) ) {
			tc.setWidth( NumberUtils.toDouble(width) );
		}
	}
	
	private void updateWidth( TemplateController tc ) {
		String width = null;
		Template template = (Template) tc.getTo();
		if ( tc.getWidth() != null ) {
			width = tc.getWidth() + tc.getCssUnit().getValue();
		}
		template.setWidth(width);
	}

}
