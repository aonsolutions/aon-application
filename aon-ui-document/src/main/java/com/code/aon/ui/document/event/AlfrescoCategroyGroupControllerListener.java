package com.code.aon.ui.document.event;

import static com.code.aon.ui.document.controller.IDocumentConstants.ALFRESCO_CATEGORY_CONTROLLER_NAME;

import com.code.aon.document.AlfrescoCategory;
import com.code.aon.ui.document.controller.AlfrescoCategoryController;
import com.code.aon.ui.document.controller.AlfrescoCategoryGroupController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AlfrescoCategroyGroupControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		initSubCategories(event);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		initSubCategories(event);
	}

	@Override
	public void afterModelSearched(ControllerEvent event) throws ControllerListenerException {
		resetCategories(event);
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		AlfrescoCategoryGroupController acgc = (AlfrescoCategoryGroupController) event.getController();
		acgc.initializeModel();
	}

	private void initSubCategories(ControllerEvent event) {
		AlfrescoCategory category = (AlfrescoCategory) event.getController().getTo();
		AlfrescoCategoryController acc = (AlfrescoCategoryController) AonUtil.getRegisteredBean(ALFRESCO_CATEGORY_CONTROLLER_NAME);
		acc.getAlfrescoDAO().setPath(category.getSearhPath());
		acc.initializeModel();		
	}

	private void resetCategories(ControllerEvent event) {
		AlfrescoCategoryGroupController acgc = (AlfrescoCategoryGroupController) event.getController();
		acgc.resetCategories();
	}
	
}