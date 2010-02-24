package com.code.aon.faces.component.richfaces.dynamicInclude;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.DynamicFacelet;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public final class DynamicIncludeHandler extends TagHandler {

	private final TagAttribute src;

	/**
	 * @param config
	 */
	public DynamicIncludeHandler(TagConfig config) {
		super(config);
		this.src = this.getRequiredAttribute("src");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sun.facelets.FaceletHandler#apply(com.sun.facelets.FaceletContext,
	 * javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		String path = this.src.getValue(ctx);
		if (StringUtils.isBlank(path)) {
			return;
		}
		URL url = new URL( path );
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(new VariableMapperWrapper(orig));
		try {
			this.nextHandler.apply(ctx, null);
			Facelet facelet = DynamicFacelet.createFacelet(ctx, url);
			facelet.apply(ctx.getFacesContext(), parent);
		} finally {
			ctx.setVariableMapper(orig);
		}
	}
}