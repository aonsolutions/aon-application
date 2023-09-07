package com.code.aon.faces.component.richfaces.dynamicInclude;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import jakarta.el.ELException;
import jakarta.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.impl.DynamicFacelet;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

public final class DynamicIncludeHandler extends TagHandler {

	private final TagAttribute value;

	/**
	 * @param config
	 */
	public DynamicIncludeHandler(TagConfig config) {
		super(config);
		this.value = this.getRequiredAttribute("value");
	}

	private boolean isInclude( FaceletContext ctx ) {
		TagAttribute includeTag = getAttribute("include");
		if ( includeTag != null ) {
			return includeTag.getBoolean(ctx);
		}
		return true;
	}
	
	@Override
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		String template = this.value.getValue(ctx);
		if (StringUtils.isBlank(template)) {
			return;
		}
		if ( isInclude(ctx) ) {
			File file = File.createTempFile( "dynamicInclude", ".xhtml" );
			URL url = file.toURI().toURL();
			VariableMapper orig = ctx.getVariableMapper();
			ctx.setVariableMapper(new VariableMapperWrapper(orig));
			try {
				this.nextHandler.apply(ctx, null);
				FileUtils.writeStringToFile(file, template, "UTF-8");
				Facelet facelet = DynamicFacelet.createFacelet(ctx, url);
				facelet.apply(ctx.getFacesContext(), parent);
			} finally {
				ctx.setVariableMapper(orig);
				FileUtils.deleteQuietly(file);
			}			
		}
	}

}