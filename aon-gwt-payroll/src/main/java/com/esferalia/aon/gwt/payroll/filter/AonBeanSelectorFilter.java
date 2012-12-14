package com.esferalia.aon.gwt.payroll.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.payroll.server.AonServletUtils;

public class AonBeanSelectorFilter implements Filter {

	public static final String ID_PARAM = "%s_id";
	public static final String CONTROLLER_PARAM = "controller";

	private ServletContext context;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		AonServletUtils.initFacesContext(context, (HttpServletRequest) req,
				(HttpServletResponse) resp);

		try {
			doLoad(req, resp);
		} finally {
			AonServletUtils.releaseFacesContext();
		}

		chain.doFilter(req, resp);

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		context = config.getServletContext();
	}

	private void doLoad(ServletRequest req, ServletResponse resp)
			throws IOException, ServletException {

		String names[] = req.getParameterValues(CONTROLLER_PARAM);
		if (names == null) {
			return;
		}

		for (String name : names) {

			BasicController controller = (BasicController) AonUtil
					.getRegisteredBean(name);
			String id = req.getParameter(String.format(ID_PARAM, name));

			if (id != null && controller != null) {
				try {
					controller.onLoad(null, Integer.parseInt(id), null, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
