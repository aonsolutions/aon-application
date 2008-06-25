/**
 * 
 */
package com.code.aon.ui.facelet;

import javax.faces.component.UIComponent;

import org.apache.myfaces.custom.schedule.ScheduleMouseEvent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MethodRule;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.html.HtmlComponentHandler;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 26/12/2006
 *
 */
public class ScheduleComponentHandler extends HtmlComponentHandler {

	private static final String RENDER_ZERO_LENGTH_ENTRIES_ATTRIBUTE_NAME = "renderZeroLengthEntries";
	private static final String MOUSE_LISTENER_METHOD_ATTRIBUTE_NAME = "mouseListener";
	private static final String EXPAND_TO_FIT_ENTRIES_ATTRIBUTE_NAME = "expandToFitEntries";

	public ScheduleComponentHandler(ComponentConfig tagConfig) {
		super(tagConfig);
	}
	
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset m = super.createMetaRuleset(type);
		Class[] clazz = new Class[] {ScheduleMouseEvent.class};
		m.addRule( new MethodRule(MOUSE_LISTENER_METHOD_ATTRIBUTE_NAME, null, clazz) );
		return m;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onComponentCreated(FaceletContext context, UIComponent component, UIComponent parent) {
		if (getAttribute(RENDER_ZERO_LENGTH_ENTRIES_ATTRIBUTE_NAME) != null) {
			component.getAttributes().put(RENDER_ZERO_LENGTH_ENTRIES_ATTRIBUTE_NAME, getAttribute(RENDER_ZERO_LENGTH_ENTRIES_ATTRIBUTE_NAME).getObject(context));
		}
		if (getAttribute(EXPAND_TO_FIT_ENTRIES_ATTRIBUTE_NAME) != null) {
			component.getAttributes().put(EXPAND_TO_FIT_ENTRIES_ATTRIBUTE_NAME, getAttribute(EXPAND_TO_FIT_ENTRIES_ATTRIBUTE_NAME).getObject(context));
		}
	}
	
}
