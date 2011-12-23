package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.FileStatus;
import com.esferalia.aon.ui.payroll.file.CertificateWriter;

public class Certifica2FileWizard extends Certifica2Factory implements Serializable {

	private static final long serialVersionUID = 6365122064043568318L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Certifica2FileWizard.class);

	private Date fromDate;
	private Date toDate;
	private FileStatus[] status;
	
	private int currentStep;
	private static final String[] STEPS = { 
		"certifica2FileWizard_step0", "certifica2FileWizard_step1", 
		"certifica2FileWizard_step2", "certifica2FileWizard_step3" };
	private FileOutput fileOutput;
	private CertificateWriter certificateWriter;
	private DataModel model;
	private DataModel selectedModel;
	private Certifica2Batch remesa;
	private List<RemesableCertificate> selectedRemesas;
	private List<Certifica2Batch> batchList;
	private List<Certifica2BatchDetail> detailList;
	
	public Certifica2Batch getRemesa() {
		return remesa;
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
	public FileStatus[] getStatus() {
		return status;
	}
	public void setStatus(FileStatus[] status) {
		this.status = status;
	}
	public void setRemesa(Certifica2Batch remesa) {
		this.remesa = remesa;
	}
	public List<RemesableCertificate> getSelectedRemesas() {
		return selectedRemesas;
	}
	public void setSelectedRemesas(List<RemesableCertificate> selectedRemesas) {
		this.selectedRemesas = selectedRemesas;
	}
	public List<Certifica2Batch> getBatchList() {
		return batchList;
	}
	public void setBatchList(List<Certifica2Batch> batchList) {
		this.batchList = batchList;
	}

	public List<Certifica2BatchDetail> getDetailList() {
		if(detailList==null){
			detailList = new ArrayList<Certifica2BatchDetail>();
		}
		return detailList;
	}

	public void setDetailList(List<Certifica2BatchDetail> detailList) {
		this.detailList = detailList;
	}

	private CertificateWriter getCertificateWriter() {
		if (certificateWriter == null) {
			certificateWriter = new CertificateWriter();
		}
		return certificateWriter;
	}
	
	public FileOutput getFileOutput() {
		return fileOutput;
	}

	public void setFileOutput(FileOutput fileOutput) {
		this.fileOutput = fileOutput;
	}

	public int getCurrentStep() {
		return this.currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}
	
	public DataModel getModel() {
		return model;
	}
	
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public DataModel getSelectedModel() {
		return selectedModel;
	}
	
	public void setSelectedModel(DataModel selectedModel) {
		this.selectedModel = selectedModel;
	}

	private void initializeRemesasModel() {
		model = new ListDataModel(transformList(getBatchList()));
	}
	
	protected void initializeRemesasModel(List<Certifica2Batch> list) {
		model = new ListDataModel(transformList(list));
	}
	
	private List<RemesableCertificate> transformList(List<Certifica2Batch> remesas) {
		List<RemesableCertificate> list = new ArrayList<RemesableCertificate>();
		for (Certifica2Batch remesa : remesas) {
			RemesableCertificate r = new RemesableCertificate();
			r.setCertificate(remesa);
			list.add(r);
		}
		return list;
	}

	// Action Listeners
	public void onNext(ActionEvent event) {
		if (getCurrentStep() == 0) {
			onSearch(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 1) {
			onValidate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 2) {
			onZipGenerate(event);
			setCurrentStep(getCurrentStep() + 1);
		} else if (getCurrentStep() == 3) {
			onFinish(event);
		} 
	}

	public void onPrevious(ActionEvent event) {
		setCurrentStep(getCurrentStep() - 1);
	}

	public String previous() {
		return STEPS[getCurrentStep()];
	}

	public String next() {
		return STEPS[getCurrentStep()];
	}

	public boolean isPreviousAvailable() {
		return (getCurrentStep() > 0);
	}

	public boolean isNextAvailable() {
		return (getCurrentStep() < 3);
	}
	
	@SuppressWarnings("unchecked")
	private void generateRemesasList() {
		setSelectedRemesas(null);
		List<RemesableCertificate> list = new LinkedList<RemesableCertificate>();
		for (RemesableCertificate remesable : (List<RemesableCertificate>) getModel().getWrappedData()) {
			if (remesable.isSelected()) {
				list.add(remesable);
				remesable.setRemesableList(generateRemesableEmpleadosList(getDetalleRemesaCertificados(remesable.getCertificate())));
			}
		}
		setSelectedRemesas(list);
		setSelectedModel(new ListDataModel(list));
	}
	
	private List<RemesableEmpleadoCertificate> generateRemesableEmpleadosList(List<Certifica2BatchDetail> list){
		List<RemesableEmpleadoCertificate> remesableList = new LinkedList<RemesableEmpleadoCertificate>();
		for(Certifica2BatchDetail d: list){
			RemesableEmpleadoCertificate remesable = new RemesableEmpleadoCertificate();
			remesable.setSuspensionCause(d.getSuspensionCause());
			remesable.setContract(d.getContract());
			
			remesableList.add(remesable);
		}
		return remesableList;
	}

	// ***************************************************
	public void onStart(ActionEvent event) {
		setCurrentStep(0);
		FileStatus[] status = {FileStatus.PENDING};
		setStatus(status);
		setFromDate(new Date());
	}

	private void onValidate(ActionEvent event) {
		generateRemesasList();
	}
	
	public void onDownloadDisk(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = getCertificateWriter().getCertificate().getFile();
			response.setContentType(MimeType.MIME_XML.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xml\";");

			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFileOutput().getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();

			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public boolean isDiskOk() {
		int errors = 0;
		if (getFileOutput() != null) {
			errors = getFileOutput().getErrors().size();
		}
		return (errors == 0);
	}

	private void onFinish(ActionEvent event) {
		onStart(event);
	}

	public void onSelect(ActionEvent event) {
		RemesableCertificate r = (RemesableCertificate) getSelectedModel().getRowData();
		r.setShowEmployees(!r.isShowEmployees());
	}
	
	@SuppressWarnings("unchecked")
	public void onSearch(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Certifica2Batch.class);
			Criteria criteria = new Criteria();
			if (getFromDate() != null) {
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATE),getFromDate());
			}
			if (getToDate() != null) {
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATE),getToDate());
			}
			if (!ArrayUtils.isEmpty(getStatus())) {
				String status = bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_STATUS);
				addEnumToCriteria(criteria, status, getStatus());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.CERTIFICA2BATCH_DATE), false);
			List<?> list = bean.getList(criteria);
			setBatchList((List<Certifica2Batch>) list);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} 
		initializeRemesasModel();
	}
	
	protected void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	
	
	public void onSelectAll(ActionEvent event) {
		processAll(true);
	}

	public void onDeselectAll(ActionEvent event) {
		processAll(false);
	}

	private void processAll(boolean selected) {
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			RemesableCertificate r = (RemesableCertificate) getModel().getRowData();
			r.setSelected(selected);
		}
	}
	
	public void onZipGenerate(ActionEvent event) {
		byte[] buf = new byte[1024];
		try {
			File file = File.createTempFile("aon-zip", ".ZIP");
			FileOutputStream fos = new FileOutputStream(file);
			ZipOutputStream out = new ZipOutputStream(fos);
			for(RemesableCertificate remesable: getSelectedRemesas()){
//				getDetalleRemesaCertificados(remesable.getCertificate());
				setFileOutput(getCertificateWriter().createCertificate(remesable.getCertificate(), getDetalleRemesaCertificados(remesable.getCertificate())));
				FileInputStream in = new FileInputStream(getFileOutput().getFile());
				out.putNextEntry(new ZipEntry(getCertificateWriter().getCertificate().getFile()+".xml"));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
				remesable.getCertificate().setStatus(FileStatus.GENERATED);
				accept(remesable.getCertificate());
				int delay=1;
				try {
					Thread.sleep( delay );
				} catch (InterruptedException e) {
					// NADA, que siga la ejecucion
				} 
			}
			out.close();
			FileOutput output = new FileOutput();
			output.setFile(file);
			output.setErrors(new ArrayList<Exception>());
			setFileOutput(output);
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			// No se lanza excepción, que vaya a la última página.
		} 
	}	
	
	public void onDownloadZip(ActionEvent event) {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "aon-out";
			response.setContentType(MimeType.MIME_ZIP.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".zip\";");

			ServletOutputStream output = response.getOutputStream();
			InputStream input = new FileInputStream(getFileOutput().getFile());
			int size = IOUtils.copy(input, output);
			if (size > 0) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			output.close();
			input.close();
			response.flushBuffer();
			faces.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
}
