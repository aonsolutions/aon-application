package com.code.aon.ui.company.servlet;

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

public class CompanyImageServlet extends RegistryAttachmentServlet {

	/**
	 * Retrieves the required RegistryAttachment from the database
	 * 
	 * @param req the req
	 * @param res the res
	 * 
	 * @throws IOException the IO exception
	 * @throws ServletException the servlet exception
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException {
		try {
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			String path = req.getServletPath();
	        int slash = path.lastIndexOf("/");
	        int dot = path.lastIndexOf(".");
	        String id = path.substring(slash + 1, dot);
			Criteria criteria = new Criteria();
			criteria.addExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID), id);
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