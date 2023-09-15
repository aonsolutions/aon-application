package com.code.aon.ui.groupware.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.enumeration.DailyTrackingReportType;
import com.code.aon.groupware.report.dailyTracking.DailyTrackingReport;
import com.code.aon.groupware.report.dailyTracking.DailyTrackingReportEngine;
import com.code.aon.groupware.report.dailyTracking.DailyTrackingReportParams;
import com.code.aon.registry.Registry;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.groupware.controller.GroupWareCollectionsController;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class DailyTrackingReportController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DailyTrackingReportController.class);
	
	private DailyTrackingReportParams params;
	private boolean monitor;
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;
	
	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	public DailyTrackingReportParams getParams() {
		return params;
	}
	public void setParams(DailyTrackingReportParams params) {
		this.params = params;
	}

	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public void onResetReportSearch(ActionEvent event) {
		try {
			if (!isMonitor()) {
				TaskHolder taskHolder = getGroupwareUtils().getCurrentTaskHolder();
				if (taskHolder == null) {
					String msg = "No existe un operario vinculado a la cuenta de acceso. Cree un operario y vincule la cuenta de acceso.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			
			setParams( new DailyTrackingReportParams() );
			getParams().setRegistry( (Registry) BeanManager.getManagerBean(Registry.class).createNewTo());
			loadProjects(null);
			loadActivityTypes(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar los parámetros para el informe.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void loadProjects(Integer registryId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
		AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		projects = pcc.getProjects( registryId);
	}

	private void loadActivityTypes(Integer projectTypeId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
			AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		if (projectTypeId != null) {
			activityTypes = pcc.getActivityTypes( projectTypeId );
		} else {
			activityTypes = new LinkedList<SelectItem>();	
		}
	}

	public void onRegistryChanged(LookupChangeEvent event) {
		try {
			Registry registry = (Registry) event.getNewValue();
			if (registry == null || registry.getId() == null) {
				getParams().setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (getParams().getProject() != null && getParams().getProject().getProjectType() != null) {
				projectTypeId = getParams().getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar la lista de proyectos.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onChangeProject(ActionEvent event) {
		try {
			Registry registry = null;
			if (getParams().getProject() != null) {
				registry = getParams().getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			getParams().setRegistry(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (getParams().getProject() != null && getParams().getProject().getProjectType() != null) {
				projectTypeId = getParams().getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar registry.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}

	public List<SelectItem> getProjects() throws ManagerBeanException {
		if (projects == null) {
			projects = new LinkedList<SelectItem>();
		}
		return projects;
	}

	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		return activityTypes;
	}
	
	public List<SelectItem> getTaskHolderWorkgroups() throws ManagerBeanException {
		GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
			AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
		return gcc.getTaskHolderWorkgroups(getParams().getWorkGroup());
	}

	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	public Collection<?> getCollection() {
		DailyTrackingReportEngine engine = new DailyTrackingReportEngine();
		return engine.getReportCollection(getParams());
	}


	private boolean isExcelReport() { 
		return (DailyTrackingReportType.REPORT == getParams().getReportType());
	}
	public String getUserWarning() {
		if (isMonitor() 
			&& getParams().getWorkGroup() != null 
			&& getParams().getWorkGroup().getId() != null
		    && (getParams().getTaskHolder() == null 
		     || getParams().getTaskHolder().getId() == null)) {
			return "Ha indicado un grupo de usuarios, pero no se ha indicado un operario";
		}
		return "";
	}
	
	public String onReport() {
		try {
			String outcome = null;
			if (!isMonitor()) {
				getParams().setTaskHolder( getGroupwareUtils().getCurrentTaskHolder() );
			}
			if (isExcelReport()) {
				printExcelReport();
			} else {
				ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
				manager.setReportKey(getParams().getReportType().getReportKey());
				manager.setOutputFormat(OutputFormat.PDF);
				outcome = manager.onExecute();
			}
			return outcome;
		} catch (Throwable e) {
			String msg = "Error al ejecutar el listado. " + e.getMessage();
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}			
	}

	private void printExcelReport() {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			String filename = "PartesDeTrabajo";
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, filename, MimeType.MIME_MS_EXCEL);
			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(filename);
			
			HSSFFont font = exporter.createFont();
			font.setFontHeightInPoints((short) 8);

			HSSFFont boldFont = exporter.createFont();
			boldFont.setFontHeightInPoints((short) 8);
			boldFont.setBold(true);

			HSSFCellStyle headerCellStyle = exporter.createCellStyle();
		    headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
		    headerCellStyle.setAlignment(HorizontalAlignment.CENTER );
		    headerCellStyle.setFont(boldFont);
			exporter.addHeaderCell("Cliente", exporter.getWidth(40), headerCellStyle);
			exporter.addHeaderCell("Usuario", exporter.getWidth(30), headerCellStyle);
			exporter.addHeaderCell("Fecha", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Tipo Expediente", exporter.getWidth(25), headerCellStyle);
			exporter.addHeaderCell("Expediente", exporter.getWidth(45), headerCellStyle);
			exporter.addHeaderCell("Tipo Actividad", exporter.getWidth(25), headerCellStyle);
			exporter.addHeaderCell("Tipo Trabajo", exporter.getWidth(25), headerCellStyle);
			exporter.addHeaderCell("Duración (1)", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Duración (2)", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Costo Unitario", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Coste", exporter.getWidth(10), headerCellStyle);
			exporter.addHeaderCell("Comentarios", exporter.getWidth(100), headerCellStyle);
			HSSFCellStyle defaultStyle = exporter.createCellStyle();
			defaultStyle.setFont(font);

			HSSFCellStyle dateStyle = exporter.createCellStyle();
			dateStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DATE_PATTERN));
			dateStyle.setAlignment(HorizontalAlignment.LEFT );
			dateStyle.setFont(font);

			HSSFCellStyle amountStyle = exporter.createCellStyle();
			amountStyle.setFont(font);
			amountStyle.setAlignment(HorizontalAlignment.RIGHT );
			amountStyle.setDataFormat( exporter.getDataFormat().getFormat(ExcelReportExporter.DECIMAL_PATTERN));
			
			for ( Object obj : getCollection() ) {
				DailyTrackingReport bs = (DailyTrackingReport) obj;
				exporter.startLine();
				exporter.addStringCell( bs.getRegistryName() , defaultStyle );
				exporter.addStringCell( bs.getUserName() , defaultStyle );
				exporter.addDateCell( bs.getDate() , dateStyle );
				exporter.addStringCell( bs.getProjectTypeDescription() , defaultStyle );
				exporter.addStringCell( bs.getProjectName() , defaultStyle );
				exporter.addStringCell( bs.getActivityTypeDescription() , defaultStyle );
				exporter.addStringCell( bs.getJobTypeDescription() , defaultStyle );
				String hours = (bs.getHours() != 0?(bs.getHours() + " h. "):"") + bs.getMinutes() + " min.";
				exporter.addStringCell( hours , amountStyle );
				exporter.addDecimalCell( bs.getDuration() , amountStyle );
				exporter.addDecimalCell( bs.getCost() , amountStyle );
				exporter.addDecimalCell( bs.getAmount() , amountStyle );
				exporter.addStringCell( bs.getComments() , defaultStyle );
				exporter.endLine();
			}
			exporter.endExport(out);
			
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
	}

}
