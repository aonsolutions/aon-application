package com.code.aon.ui.marketing.controller;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.esferalia.aon.entity.IEntityAlias;

public class NewsController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsController.class.getName());
	
	private IControllerListener documentFilter;
	
	private List<MimeType> getImageMimeTypes() {
		List<MimeType> list = new LinkedList<MimeType>();
		for( MimeType mimeType : MimeType.values() ) {
			if ( mimeType.getName().startsWith("image/") ) {
				list.add(mimeType);
			}
		}
		return list;
	}
	
	public IControllerListener getDocumentFilter() {
		if ( this.documentFilter == null ) {
			this.documentFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						String alias = controller.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_MIME_TYPE);
						controller.getCriteria().addInExpression(alias, getImageMimeTypes());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering documents", e);
					}
				}
			};
		}
		return this.documentFilter;
	}

}
