package com.code.aon.faces.component.messages;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlMessages;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class MessagesHandler extends AonComponentHandler {
	
	private static final String SHOW_SUMMARY_ATTR_NAME = "showSummary";
	private static final String SHOW_DETAIL_ATTR_NAME = "showDetail";
	private static final String ERROR_CLASS_ATTR_NAME = "errorClass";
	private static final String FATAL_CLASS_ATTR_NAME = "fatalClass";
	private static final String INFO_CLASS_ATTR_NAME = "infoClass";

	private static final String ERROR_STYLE_CLASS = "aon-error-message";
	private static final String FATAL_STYLE_CLASS = "aon-fatal-error-message";
	private static final String INFO_STYLE_CLASS = "aon-info-message";
	private static final String SHOW_SUMMARY_VALUE = "false";
	private static final String SHOW_DETAIL_VALUE = "true";

	
	public MessagesHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		HtmlMessages messages = (HtmlMessages) instance;
		super.setAttributes(ctx, messages);
		if (! hasValue(ctx, ERROR_CLASS_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), messages, ERROR_CLASS_ATTR_NAME, ERROR_STYLE_CLASS );
		}
		if (! hasValue(ctx, FATAL_CLASS_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), messages, FATAL_CLASS_ATTR_NAME, FATAL_STYLE_CLASS );
		}
		if (! hasValue(ctx, INFO_CLASS_ATTR_NAME) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), messages, INFO_CLASS_ATTR_NAME, INFO_STYLE_CLASS );
		}
		if (! hasValue(ctx, SHOW_SUMMARY_ATTR_NAME) ) {
			UIComponentTagUtils.setBooleanProperty(ctx.getFacesContext(), messages, SHOW_SUMMARY_ATTR_NAME, SHOW_SUMMARY_VALUE );
		}
		if (! hasValue(ctx, SHOW_DETAIL_ATTR_NAME) ) {
			UIComponentTagUtils.setBooleanProperty(ctx.getFacesContext(), messages, SHOW_DETAIL_ATTR_NAME, SHOW_DETAIL_VALUE );
		}
	}
}