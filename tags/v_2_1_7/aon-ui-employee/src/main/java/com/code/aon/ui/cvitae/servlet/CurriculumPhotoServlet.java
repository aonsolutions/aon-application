package com.code.aon.ui.cvitae.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.registry.servlet.RegistryAttachmentServlet;

/**
 * Servlet class invoked whenever a field form needs a Registry Attachment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 29-jul-2005
 * @since 1.0
 * 
 */

public class CurriculumPhotoServlet extends RegistryAttachmentServlet {
	
	protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		try {
			Integer id = new Integer(req.getRequestURL().substring(req.getRequestURL().lastIndexOf("/") + 1, req.getRequestURL().lastIndexOf(".")));
			Criteria criteria = new Criteria();
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), id);
			criteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_DESCRIPTION), "PHOTO");
			List list = getManagerBean().getList(criteria);
			if (list.size() > 0) {
				RegistryAttachment ra = (RegistryAttachment) list.get(0);
				res.setContentType(ra.getMimeType().getName());
				res.setCharacterEncoding("ISO-8859-1"); //$NON-NLS-1$
				res.getOutputStream().write(ra.getData());
				res.flushBuffer();
			}
		} catch (Throwable th) {
			th.printStackTrace();
			throw new ServletException(th.getMessage(), th);
		}
	}
}
