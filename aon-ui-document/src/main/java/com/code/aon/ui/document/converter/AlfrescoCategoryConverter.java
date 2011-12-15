package com.code.aon.ui.document.converter;

import static com.code.aon.ui.document.controller.IDocumentConstants.ALFRESCO_CATEGORY_GROUP_CONTROLLER_NAME;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.code.aon.document.AlfrescoCategory;
import com.code.aon.document.dao.AlfrescoCategoryDAO;
import com.code.aon.ui.document.controller.AlfrescoCategoryGroupController;
import com.code.aon.ui.util.AonUtil;

/**
 * 
 *
 */
public class AlfrescoCategoryConverter implements Converter {
	
	private static final String INVALID_ID = "----";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null) {
			AlfrescoCategoryGroupController acgc = (AlfrescoCategoryGroupController) AonUtil.getRegisteredBean(ALFRESCO_CATEGORY_GROUP_CONTROLLER_NAME);
			AlfrescoCategory ac = acgc.getCategory(value);
			if ( ac == null ) {
				ac = AlfrescoCategoryDAO.EMPTY_CATEGORY;
			}
			return ac;
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return INVALID_ID;
		}
		AlfrescoCategory ac = (AlfrescoCategory) value;
		return (ac.getId() != null) ? ac.getId().getUuid() : INVALID_ID;
	}

}