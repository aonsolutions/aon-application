package com.code.aon.ui.document.converter;

import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_CONTROLLER_NAME;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.code.aon.document.AlfrescoCategory;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.util.AonUtil;

/**
 * 
 *
 */
public class AlfrescoCategoryConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null) {
			EnterpriseDocumentController edc = (EnterpriseDocumentController) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_CONTROLLER_NAME);
			return edc.getCategoryManager().getCategory(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		AlfrescoCategory ac = (AlfrescoCategory) value;
		return ac.getName();
	}

}