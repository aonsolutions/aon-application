package com.esferalia.aon.gwt.employee.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

@SuppressWarnings("serial")
public class SalaryExporterServlet extends HttpServlet {
	
	
	private static Map<String, OutputFormat> OUTPUT_FORMATS = 
			new HashMap<String, OutputFormat>(){
		{
			put("pdf", OutputFormat.PDF);
		}
	};
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String salaryIdStr = AonServletUtils.getWithoutExtn(requestURI);
		int salaryId = Integer.parseInt(salaryIdStr);
		
		try {
			ServletContext ctx = getServletContext();
			AonServletUtils.initFacesContext(ctx, req, resp);
			
			IManagerBean beanManager = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_ID),
					salaryId);
			List<ITransferObject> list = beanManager.getList(criteria);
			com.esferalia.aon.payroll.Salary aonSalary = (com.esferalia.aon.payroll.Salary) list
					.get(0);

			ReportManager reportManager = new ReportManager();
			OutputFormat outputFormat = getOutputFormat(extension);
			reportManager.setOutputFormat(outputFormat);
			reportManager.setCollectionProvider(new SingleCollectionProvider(
					aonSalary));
			
			MimeType mimeType = MimeType.getByExtension(extension);
			resp.setContentType(mimeType.getName());
			
			OutputStream os = resp.getOutputStream();
			
			reportManager.execute(os, IPayrollConstants.SALARY_REPORT);
			
			os.flush();
			
		} catch (ReportException e) {
			throw new ServletException(e);
		}catch (ManagerBeanException e) {
			throw new ServletException(e);
		} finally{
			AonServletUtils.releaseFacesContext();
		}
		
		
	}
	
	private static OutputFormat getOutputFormat(String extension) {
		return OUTPUT_FORMATS.get(extension);
	}
	
}
