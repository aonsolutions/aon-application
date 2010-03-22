package com.code.aon.ui.report.converter;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.report.OutputFormat;


/**
 * Suitable <code>javax.faces.convert.Converter</code> implementation for 
 * <code>com.code.aon.ui.report.OutputFormat</code> class.  
 *
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @since 1.0
 *  
 */
public class OutputFormatConverter implements Converter {

	/**
	 * Obtains a suitable <code>Logger</code>.
	 */
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputFormatConverter.class);

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext ctx, UIComponent c, Object outputFormat)
            throws ConverterException {
        if (outputFormat == null) {
            return null;
        }
        if (!(outputFormat instanceof OutputFormat)) {
            LOGGER.warn("{} is not a valid object!",outputFormat);
            throw new ConverterException(outputFormat + " is not a valid object!");
        }
        return outputFormat.toString();
    }

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext ctx, UIComponent c, String text)
            throws ConverterException {
        try {
            if (text == null || text.trim().equals("")) {
                return text;
            }
            return OutputFormat.get( text );
        } catch (NumberFormatException e) {
            LOGGER.warn(e.getMessage(),e);
            throw new ConverterException(e.getMessage(), e);
        }
    }

}
