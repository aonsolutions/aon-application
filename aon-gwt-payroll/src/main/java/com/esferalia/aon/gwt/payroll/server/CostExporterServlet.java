package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.salary.SalaryExpenseController;

@SuppressWarnings("serial")
public class CostExporterServlet extends HttpServlet {
	
	
	private static Map<String, OutputFormat> OUTPUT_FORMATS = 
			new HashMap<String, OutputFormat>(){
		{
			put("pdf", OutputFormat.PDF);
			put("xls", OutputFormat.XLS);
		}
	};
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String fileName = AonServletUtils.getFileName(requestURI);
		
		String fileNameParts [] = fileName.split("_");
		int month = Integer.parseInt(fileNameParts[0]);
		int year = Integer.parseInt(fileNameParts[1]);
		int enterpriseId = Integer.parseInt(fileNameParts[2]);
		int workplaceId = Integer.parseInt(fileNameParts[3]) ;
		
		try {
			ServletContext ctx = getServletContext();
			AonServletUtils.initFacesContext(ctx, req, resp);
			
			FacesContext fCtx = FacesContext.getCurrentInstance();
			fCtx.getViewRoot().setLocale(new Locale("es", "ES"));
			
			IManagerBean beanManager = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			Criteria criteria = new Criteria();
			
			Calendar calendar = Calendar.getInstance();
			calendar.set( Calendar.YEAR, year);
			calendar.set( Calendar.MONTH, month);
			calendar.set( Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
			calendar.set( Calendar.HOUR, 0);
			calendar.set( Calendar.MINUTE, 0);
			calendar.set( Calendar.SECOND, 0);
					
			Date startDate = calendar.getTime();
			
			calendar.set(Calendar.DAY_OF_MONTH, 
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = calendar.getTime();
			
			if ( workplaceId != 0  ){
				criteria.addEqualExpression(
						beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID ),
						workplaceId );
			}
			else {
				criteria.addEqualExpression(
						beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID ),
						enterpriseId );
			}
			
			criteria.addBetweenExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_END_DATE),
					startDate, 
					endDate );
			
			criteria.addOrder(beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
			criteria.addOrder(beanManager.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
			
			final List<ITransferObject> list = beanManager.getList(criteria);
						
			ReportManager reportManager = new StatelessReportManager();
			OutputFormat outputFormat = getOutputFormat(extension);
			reportManager.setOutputFormat(outputFormat);
			
			reportManager.setCollectionProvider(new ICollectionProvider() {
				@Override
				public Collection<ITransferObject> getCollection() {
					return list;
				}

				@Override
				public Collection<ITransferObject> getCollection(boolean forceRefresh)
						throws ManagerBeanException {
					return list;
				}
			});
			
			// Really I hate this spaghetti piece of code. 
			// For pass 'month' & 'year' to a report, we 
			// must put it in a controller ?????. 
			SalaryExpenseController controller = 
					(SalaryExpenseController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_EXPENSE_CONTROLLER_NAME);
			controller.setShowSalaryExpenseWindow(false);
			controller.setYear(year);
			controller.setMonth(Month.getMonthByValue(month));
			
			resp.setContentType(MimeType.getByExtension(extension).getName());
			OutputStream os = resp.getOutputStream();
			reportManager.execute(os, IPayrollConstants.COST_REPORT);
			os.close();
			
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
