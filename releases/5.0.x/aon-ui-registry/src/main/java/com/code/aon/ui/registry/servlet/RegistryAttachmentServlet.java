package com.code.aon.ui.registry.servlet;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.servlet.AttachmentServlet;

/**
 * Servlet class invoked whenever a field form needs a Registry Attachment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 29-jul-2005
 * @since 1.0
 */
public class RegistryAttachmentServlet extends AttachmentServlet {

	private static final long serialVersionUID = 7864119079272432129L;
	
	/** The bean. */
	private IManagerBean bean;
	
	/**
	 * Gets the RegistryAttachment manager bean.
	 * 
	 * @return the manager bean
	 * @throws ManagerBeanException 
	 */
	protected IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.bean == null ) {
			this.bean = BeanManager.getManagerBean(RegistryAttachment.class);
		}
		return this.bean;
	}
}
