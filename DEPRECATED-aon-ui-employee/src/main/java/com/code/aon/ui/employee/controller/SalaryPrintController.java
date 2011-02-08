package com.code.aon.ui.employee.controller;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Salary;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;

public class SalaryPrintController implements ICollectionProvider, IEmployeeConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryPrintController.class);
	
	private static final String SALARIES_ZIP_NAME = "nominas";
	
	private SalaryController controller;
	
	private Enterprise enterprise;
	
	private List<SelectItem> workPlaces;
	
	private boolean showWorkPlaces;
	
	private WorkPlace workPlace;
	
	private Date fromDate;
	
	private Date toDate;
	
	private Set<Integer> checks = new HashSet<Integer>();
	
	public SalaryPrintController() {
		controller = (SalaryController) AonUtil.getRegisteredBean(ICompanyConstants.SALARY_CONTROLLER_NAME);
	}

	public void onInit( ActionEvent event ) throws ManagerBeanException {
		EnterpriseController ec = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		this.enterprise = (Enterprise) ec.getTo();		
		loadWorkPlaces( enterprise );
		clearFilters();
		resetCriteria();
		controller.initializeModel();
	}

	public void onClearFilter( ActionEvent event ) throws ManagerBeanException {
		clearFilters();
		resetCriteria();
		controller.initializeModel();
	}
	
	public void onFilter( ActionEvent event ) throws ManagerBeanException {
		resetCriteria();
		Criteria criteria = controller.getCriteria();
		if ( fromDate != null ) {
			String alias = controller.getFieldName(IEmployeeAlias.SALARY_START_DATE);
			criteria.addGreaterThanOrEqualExpression(alias, fromDate);
		}
		if ( toDate != null ) {
			String alias = controller.getFieldName(IEmployeeAlias.SALARY_START_DATE);
			criteria.addLessThanOrEqualExpression(alias, toDate);
		}
		if ((getWorkPlace() != null) && (getWorkPlace().getId() != null)) {
			String alias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ID);
			criteria.addEqualExpression(alias, getWorkPlace().getId());			
		}		
		controller.initializeModel();
	}
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		String id = controller.getFieldName(IEmployeeAlias.SALARY_ID);
		ProjectionList pl = new ProjectionList( Projection.property(id) );
		List<Integer> list = controller.getManagerBean().getList(pl, controller.getCriteria());
		checks.clear();
		checks.addAll( list );
	}

	public void checkNone(ActionEvent event) {
		clearChecked();
	}

	private void clearFilters() {
		this.fromDate = null;
		this.toDate = null;
		this.workPlace = new WorkPlace();
	}
	
	private void resetCriteria() throws ManagerBeanException {
		clearChecked();
		controller.clearCriteria();
		Criteria criteria = controller.getCriteria();
		String alias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(alias, enterprise.getId());		
	}

	public boolean isRowChecked() throws ManagerBeanException {
		Salary to = (Salary) controller.getModel().getRowData();
		return checks.contains(to.getId());
	}

	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		Integer id = ((Salary) controller.getModel().getRowData()).getId();		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
	}

	public Set<Integer> getChecked() {
		return checks;
	}

	public void clearChecked() {
		checks = new HashSet<Integer>();
	}
	
	public boolean isSelectionEmpty() {
		return this.checks.isEmpty();
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public boolean isShowWorkPlaces() {
		return showWorkPlaces;
	}

	public List<SelectItem> getWorkPlaces() {
		return workPlaces;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public void loadWorkPlaces( Enterprise enterprise) throws ManagerBeanException {
		this.workPlaces = null;
		this.showWorkPlaces = false;
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		String enterpriseId = bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID);
		criteria.addEqualExpression(enterpriseId, enterprise.getId());
		criteria.addOrder(bean.getFieldName(ICompanyAlias.WORK_PLACE_DESCRIPTION));
		if ( bean.getCount(criteria) > 1 ) {
			List<ITransferObject> list = bean.getList(criteria);
			this.workPlaces = new LinkedList<SelectItem>();
			for (ITransferObject to : list) {
				WorkPlace workPlace = (WorkPlace)to;
				workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
			}
			this.showWorkPlaces = true;
		}
	}	

	@Override
	public Collection<ITransferObject> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Collection<ITransferObject> getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		IManagerBean bean = this.controller.getManagerBean();
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		for( Integer id : checks ) {
			l.add( bean.get(id) );
		}
		return l;
	}

	public String onPrint() {
		ReportManager reportManager = new ReportManager();
		reportManager.setReportKey(CURRENT_SALARY_REPORT);
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( this );
		return reportManager.onExecute();	
	}
	
	private void writeSalariesZip( File file, Collection<Salary> collection ) throws IOException, ReportException {
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for (Salary salary : collection) {
			String fileName = controller.getFileName(salary) + "." + MimeType.MIME_PDF.getExtension();
       		zipOut.putNextEntry(new ZipEntry(fileName));
       		controller.writeReport(salary, zipOut);
        	zipOut.closeEntry();
        }	
		IOUtils.closeQuietly(zipOut);
	}
	
	private AonFile getSalariesZipFile( Collection<Salary> salaries ) throws IOException, ReportException {
		File file = File.createTempFile( SALARIES_ZIP_NAME, "." + MimeType.MIME_ZIP.getExtension() );
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeSalariesZip( file, salaries );
		IOUtils.closeQuietly(out);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( SALARIES_ZIP_NAME + "." + MimeType.MIME_ZIP.getExtension() );
		return aonFile;
	}		

	@SuppressWarnings("unchecked")
	public void onSendByEmail( ActionEvent event ) {
		try {
			Collection<?> list = getCollection();
			Collection<Salary> salaries = (Collection<Salary>) list;
			MessageController messageController = controller.initMail( enterprise, salaries );
			messageController.addAttachment( getSalariesZipFile(salaries) );
			messageController.setShowNewMessageWindow(true);
		} catch (Throwable e) {
			LOGGER.error(">>>> onSendByEmail ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
}