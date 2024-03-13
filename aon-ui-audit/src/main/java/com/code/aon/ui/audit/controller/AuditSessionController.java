package com.code.aon.ui.audit.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class AuditSessionController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditSessionController.class);
	
	private IControllerListener listener;
	
	public void onInit( ActionEvent event ) {
		ActionDeniedController denied = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		User user = UserUtils.getInstance().getLoggedUser();		
		Map<String, ApplicationCategory> deniedModules = denied.initEdit(user);
		denied.updateActionList(deniedModules);		
	}
	
	private DomainApplication getDomainApplication() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			Criteria criteria = new Criteria();
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_DOMAIN), DomainManager.getCurrentDomain());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), principal.getApplicationId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (DomainApplication) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}	
	
	public boolean isShowAudit() {
		DomainApplication da = getDomainApplication();
		if ( da != null ) {
			return (da.getAuditLevel() != AuditLevel.NONE);
		}
		return false;
	}

	public int getCount() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Session session = (Session) getModel().getRowData();
			IManagerBean bean = BeanManager.getManagerBean(ActionEntry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_ENTRY_SESSION_ID), session.getId());
			return bean.getCount(criteria);
		}
		return 0;
	}	

	@SuppressWarnings("unchecked")
	public void onRemoveSessions( ActionEvent event ) {
    	try {
    		String idAlias = getFieldName(IEntityAlias.SESSION_ID);
    		ProjectionList pl = new ProjectionList(Projection.property(idAlias));
    		List<Integer> ids = getManagerBean().getList(pl, getCriteria());
    		if (! ids.isEmpty() ) {
    			for( Integer id : ids ) {
    				ITransferObject to = getManagerBean().get(id);
    				getManagerBean().remove(to);
    			}
    		}
    		initializeModel();
		} catch (Throwable e) {
			LOGGER.error("Error removing sessions", e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public IControllerListener getListener() {
		if ( listener == null ) {
			this.listener = new DomainsFilter();
		}
		return listener;
	}	

	private static class DomainsFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				Criteria criteria = controller.getCriteria();
				DomainSwitcher dw = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
				if ( dw.isChildDomain() ) {
					criteria.setSkipDomainFilter(true);
					List<Integer> domains = new LinkedList<Integer>();
					domains.add(dw.getParentDomain());
					domains.add(dw.getDomainId());
					criteria.addInExpression("User.domain", domains);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering domain", e);
			}
		}
		
	}
	public void onExcel( ActionEvent event ) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Listado de accesos";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
	
			ExcelReportExporter exporter = new ExcelReportExporter("Accesos");	
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			exporter.exportHeader();
			for (ITransferObject to : list) {
				
				Session session = (Session) to;
				exporter.exportRow( session );
			}
			exporter.endExport(output);
			
			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException | ManagerBeanException e) {
			String msg = "Se ha producido un error inesperado durante la generación del informe. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private class ExcelReportExporter {
		
		protected SXSSFWorkbook workbook;
		protected SXSSFSheet sheet;
		protected XSSFCellStyle headerCellStyle;
	    private CellStyle dateCellStyle;
	    private int rowCount;
	    
	    public ExcelReportExporter(String name) {
		    workbook = new SXSSFWorkbook(1);
		    setSheet(  workbook.createSheet(name) );
		    
	    	DataFormat dataFormat = workbook.getCreationHelper().createDataFormat();
	    	
		    dateCellStyle = workbook.createCellStyle();
		    dateCellStyle.setDataFormat(dataFormat.getFormat("dd/MM/yyyy"));
		    dateCellStyle.setAlignment( HorizontalAlignment.CENTER );
	    	
		    headerCellStyle = (XSSFCellStyle) workbook.createCellStyle();
	    	headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
	    	headerCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
	    	headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
	    	headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    	headerCellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(240,240,240)));
	    	
	    }

		public void exportRow(Session session) {
			SXSSFRow row = sheet.createRow(rowCount++);
			int cellCount = 0;		

			Cell c0 = row.createCell(cellCount++);
			c0.setCellType(CellType.STRING);
			c0.setCellValue(session.getSessionId());
			
			Cell c1 = row.createCell(cellCount++);
			c1.setCellStyle(dateCellStyle);
			if ( session.getStartDate() != null ) {
				c1.setCellValue(session.getStartDate());
			}

			Cell c2 = row.createCell(cellCount++);
			c2.setCellStyle(dateCellStyle);
			if ( session.getEndDate() != null ) {
				c2.setCellValue(session.getEndDate());
			}
			
			String user = session.getUser() == null?"<Desconocido>":session.getUser().getName(); 
			Cell c3 = row.createCell(cellCount++);
			c3.setCellType(CellType.STRING);
			c3.setCellValue(user);

			Cell c4 = row.createCell(cellCount++);
			c4.setCellType(CellType.STRING);
			c4.setCellValue(session.getRemoteAddress());
		}

		public void setSheet(SXSSFSheet sheet) {
			this.sheet = sheet;
		}
		
		public void exportHeader() {
			SXSSFRow row = sheet.createRow(rowCount++);
			int cellCount = 0;		

			Cell c0 = row.createCell(cellCount);
			c0.setCellStyle(headerCellStyle);
			c0.setCellValue("ID Sesión");
			sheet.setColumnWidth(cellCount, 35 * 256);
			cellCount++;
			
			Cell c1 = row.createCell(cellCount);
			c1.setCellStyle(headerCellStyle);
			c1.setCellValue("Fecha Inicio");
			sheet.setColumnWidth(cellCount, 10 * 256);
			cellCount++;
			
			Cell c2 = row.createCell(cellCount);
			c2.setCellStyle(headerCellStyle);
			c2.setCellValue("Fecha Fin");
			sheet.setColumnWidth(cellCount, 10 * 256);
			cellCount++;
			
			Cell c3 = row.createCell(cellCount);
			c3.setCellStyle(headerCellStyle);
			c3.setCellValue("Usuario");
			sheet.setColumnWidth(cellCount, 15 * 256);
			cellCount++;
			
			Cell c5 = row.createCell(cellCount);
			c5.setCellStyle(headerCellStyle);
			c5.setCellValue("IP Remota");
			sheet.setColumnWidth(cellCount, 10 * 256);
			
		}

		public void endExport(OutputStream out) {
			try {
				workbook.write(out);
			} catch (IOException e) {
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}

	}
}
