package com.code.aon.ui.product.servlet;


import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.product.ItemAttachment;
import com.code.aon.ui.common.servlet.AttachmentServlet;

/**
 * Servlet class invoked whenever a field form needs a Registry Attachment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 25-ene-2005
 * @since 1.0
 */

public class ItemAttachmentServlet extends AttachmentServlet {

	/** The ManagerBean. */
	private IManagerBean bean;
	
	/**
	 * Gets the ItemAttachment ManagerBean.
	 * 
	 * @return the manager bean
	 */
	protected IManagerBean getManagerBean()  {
		try {
			if (this.bean == null) {
				this.bean = BeanManager.getManagerBean(ItemAttachment.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.bean;
	}
}
