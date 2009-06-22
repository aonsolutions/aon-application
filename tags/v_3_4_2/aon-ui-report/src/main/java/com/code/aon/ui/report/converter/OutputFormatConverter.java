package com.code.aon.ui.report.converter;

import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

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
    private static final Logger LOG = Logger.getLogger(OutputFormatConverter.class.getName());

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext ctx, UIComponent c, Object outputFormat)
            throws ConverterException {
        if (outputFormat == null) {
            return null;
        }
        if (!(outputFormat instanceof OutputFormat)) {
            LOG.warning(outputFormat + " is not a valid object!"); //$NON-NLS-1$
            throw new ConverterException(outputFormat + " is not a valid object!"); //$NON-NLS-1$
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
            LOG.warning(e.getMessage());
            throw new ConverterException(e.getMessage(), e);
        }
    }

}
