package com.code.aon.faces.component.richfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.richfaces.inputRichText.InputRichTextHandler;
import com.code.aon.faces.component.richfaces.lookup.inputText.LookupInputTextHandler;
import com.code.aon.faces.component.util.BasicComponentConfig;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

/**
 * The Class ConfirmButtonHandler.
 * 
 * @author atellitu
 */
public class RegionableInputHandler extends TagHandler implements IRichFacesTags {

	private static final String REGION_RENDERER_TYPE = "org.ajax4jsf.components.AjaxRegionRenderer";

	private static final String REGION_COMPONENT_TYPE = "org.ajax4jsf.AjaxRegion";
	
	private static final String LOOKUP_INPUT_TEXT_COMPONENT_TYPE = "com.code.aon.faces.HtmlLookupInputText";
	
	private static final String SELECT_INPUT_DATE_COMPONENT_TYPE = "com.code.aon.faces.SelectInputDate";

	private static final String INPUT_RICH_TEXT_COMPONENT_TYPE = "com.code.aon.faces.InputRichText";

	private ComponentConfig config;
	
	private AonAjaxInputHandler inputHandler;
	
	private ComponentHandler regionHandler;
	
	/**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public RegionableInputHandler(ComponentConfig config) {
		super( config );
		this.config = config;
	}
	
	private AonAjaxInputHandler newInputHandler() {
		String componentType = config.getComponentType();
		if ( LOOKUP_INPUT_TEXT_COMPONENT_TYPE.equals(componentType) ) {
			return new LookupInputTextHandler(config);
		} else if ( INPUT_RICH_TEXT_COMPONENT_TYPE.equals(componentType) ) {
			return new InputRichTextHandler(config);
		}
		return new AonAjaxInputHandler(config);
	}

	private TagHandler getMainHandler() {
		if ( this.inputHandler == null ) {
			inputHandler = newInputHandler();
			if ( this.inputHandler.isAjaxNeeded() ) {
				List<TagAttribute> attributes = new ArrayList<TagAttribute>();
				TagAttribute rendered = inputHandler.getRendered();
				if ( rendered != null ) {
					attributes.add(rendered);
				}
				BasicComponentConfig regionConfig = new BasicComponentConfig(config, attributes );
				regionConfig.setComponentType(REGION_COMPONENT_TYPE);
				regionConfig.setRendererType(REGION_RENDERER_TYPE);
				regionConfig.setNextHandler( inputHandler );
				this.regionHandler = new ComponentHandler(regionConfig);
			}
		}
		if ( this.inputHandler.isAjaxNeeded() ) {
			return regionHandler;
		}
		return this.inputHandler;
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
		getMainHandler().apply(ctx, parent);
	}

}