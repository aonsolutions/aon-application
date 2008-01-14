package com.code.aon.faces.component.richfaces;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;

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

	private TagHandler getMainHandler(FaceletContext ctx, UIComponent component) {
		if ( this.inputHandler == null ) {
			inputHandler = new AonAjaxInputHandler(config);
			if ( this.inputHandler.isAjaxNeeded() ) {
				List<TagAttribute> list = Collections.emptyList();
				BasicComponentConfig regionConfig = new BasicComponentConfig(config, list );
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
		getMainHandler(ctx, parent).apply(ctx, parent);
	}

}