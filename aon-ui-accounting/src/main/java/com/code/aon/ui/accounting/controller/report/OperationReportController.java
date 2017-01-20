package com.code.aon.ui.accounting.controller.report;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.report.OperationReport;
import com.code.aon.accounting.report.OperationReportManager;
import com.code.aon.accounting.report.OperationReportParams;
import com.code.aon.accounting.report.OperationReportTax;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;

public class OperationReportController implements IAccountingBookItem, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String ID = "id";
	private static final String DATE = "date";
	private static final String ACCOUNT = "cuenta";
	private static final String CONCEPT = "concepto";
	private static final String DOCUMENT_NUMBER = "numDoc";
	private static final String RDOCUMENT = "rdocument";
	private static final String RNAME = "rname";
	private static final String TAX_TYPE = "tax_type";
	private static final String TAXABLE_BASE = "taxable_base";
	private static final String PERCENTAGE = "percentage";
	private static final String QUOTA = "quota";
	private static final String SURCHARGE_PERCENTAGE = "surchargePercent";
	private static final String SURCHARGE_QUOTA = "surcharge";
	private static final String AMOUNT = "importe";
	
	private static final ReportColumnMetadata[] COLUMN_LABELS = new ReportColumnMetadata[]{
		new ReportColumnMetadata(ID,Types.INTEGER,ID,10)
		,new ReportColumnMetadata(DATE,Types.DATE,"Fecha",10)
		,new ReportColumnMetadata(ACCOUNT,Types.VARCHAR,"Cuenta",50)
		,new ReportColumnMetadata(CONCEPT,Types.VARCHAR,"Concepto",50)
		,new ReportColumnMetadata(DOCUMENT_NUMBER,Types.VARCHAR,"Nº Docum.",15)
		,new ReportColumnMetadata(RDOCUMENT,Types.VARCHAR,"NIF",15)
		,new ReportColumnMetadata(RNAME,Types.VARCHAR,"Titular",40)
		,new ReportColumnMetadata(TAX_TYPE,Types.VARCHAR,"Tipo Imp.",6)
		,new ReportColumnMetadata(TAXABLE_BASE,Types.DOUBLE,"B.Imp.",10)
		,new ReportColumnMetadata(PERCENTAGE,Types.VARCHAR,"Porc.",5)
		,new ReportColumnMetadata(QUOTA,Types.DOUBLE,"Cuota",10)
		,new ReportColumnMetadata(SURCHARGE_PERCENTAGE,Types.VARCHAR,"Porc.Rec.",10)
		,new ReportColumnMetadata(SURCHARGE_QUOTA,Types.DOUBLE,"Recargo",10)
		,new ReportColumnMetadata(AMOUNT,Types.DOUBLE,"Importe",10)
	};

	private OperationReportParams params;
	private DataScrollerState detailState;
	
	public OperationReportParams getParams() {
		if (params == null) {
			params = new OperationReportParams(AonUtil.getDomainName(),DomainManager.getCurrentDomain()); 
		}
		return params;
	}
	public void setParams(OperationReportParams params) {
		this.params = params;
	}
	
	public DataScrollerState getDetailState() {
		return detailState;
	}

	public void setDetailState(DataScrollerState detailState) {
		this.detailState = detailState;
	}

	public DataModel getDetailModel() {
		return getDetailState().getDirectModel();
	}

	public void setDetailModel(DataModel detailModel) {
		setDetailState(new DataScrollerState(detailModel, "operationReport"));
	}
	
	public void onReset(ActionEvent event) {
		initialize();
	}

	private void initialize() {
		params = null;
		
		Period period = null;
		try {
			period = AccountingPeriodUtil.getDefaultPeriod();
		} catch (ManagerBeanException e) {
			// nothing. Period null.
		}
		SecurityLevel securityLevel = AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL; 		
		getParams().initialize(period, securityLevel);
	}

	@Override
	public boolean isCoverVisible() {
		return getParams().isCoverVisible();
	}
	@Override
	public void setCoverVisible(boolean coverVisible) {
		getParams().setCoverVisible(coverVisible);
	}
	@Override
	public boolean isCounterVisible() {
		return getParams().isCounterVisible();
	}
	@Override
	public void setCounterVisible(boolean counterVisible) {
		getParams().setCounterVisible(counterVisible);
	}
	@Override
	public int getPageCounter() {
		return getParams().getPageCounter();
	}
	@Override
	public void setPageCounter(int pageCounter) {
		getParams().setPageCounter(pageCounter);
	}

	public void onSearch(ActionEvent event) {
		try {
			if (getParams().getPeriod() == null || getParams().getPeriod().getId() == null) {
				if (getParams().getFromDate() == null || getParams().getToDate() == null) {
					String msg = "Para la ejecución del informe, debe indicar un ejercicio contable o un rango de fechas.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				if (getParams().getToDate().before(getParams().getFromDate())) {
					String msg = "Rango de fechas incorrecto.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			OperationReportManager operationReportManager = new OperationReportManager();
			List<OperationReport> list = operationReportManager.getReport( getParams() );
			setDetailModel(new SerializableListDataModel(list));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public String onExcel() {
		try {
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "Listado Detallado";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();
			
			ReportMetadata metadata = getMetadata();
			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(IReportExporter.DEFAULT_NAME);
			exporter.exportHeader(metadata);
			List<OperationReport> list = (List<OperationReport>) getDetailModel().getWrappedData();
			double totalBalance = 0;
			HashMap<String, Double> resumen = new HashMap<String, Double>();
			for (OperationReport op : list) {
				exporter.startLine();
				int i = 0;
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getId() );
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getEntryDate() );
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getAccount() );
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getConcept() );
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getDocumentNumber() );
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getRdocument() );
				exporter.exportColumn(metadata.getColumns().get((i++)), op.getRname() );
				if (op.getTaxes() != null) {
					boolean first = true;
					for (OperationReportTax opt : op.getTaxes()) {
						if (!first) {
							exporter.endLine();
							exporter.startLine();
							for (i=0;i<7;i++){
								exporter.exportColumn(metadata.getColumns().get((i)), null );	
							}
						}
						String key = null;
						if(opt.getTaxType().equals("IVA")){
							if(opt.getPercentage() != 0)
								key = opt.getTaxType()+"-"+opt.getPercentage();
						} else if(opt.getTaxType().equals("IRPF")) {
							 key = opt.getTaxType();
						}
						
						if(key != null)
							if(resumen.containsKey(key)) 
								resumen.put(key, resumen.get(key) + opt.getQuota());
							else resumen.put(key, opt.getQuota());
						
						exporter.exportColumn(metadata.getColumns().get((i++)), opt.getTaxType() );
						exporter.exportColumn(metadata.getColumns().get((i++)), opt.getBase() );
						exporter.exportColumn(metadata.getColumns().get((i++)), String.format( "%.2f", opt.getPercentage()!=null?opt.getPercentage():0) );
						exporter.exportColumn(metadata.getColumns().get((i++)), opt.getQuota() );
						exporter.exportColumn(metadata.getColumns().get((i++)), String.format( "%.2f", opt.getSurchargePercentage()!=null?opt.getSurchargePercentage():0) );
						exporter.exportColumn(metadata.getColumns().get((i++)), opt.getSurchargeQuota() );
						first = false;
					}
				} else { 
					for (i=i;i<13;i++)
						exporter.exportColumn(metadata.getColumns().get((i)), null );	
				}
				exporter.exportColumn(metadata.getColumns().get((i++)), (op.getBalance()));
				totalBalance  = CommonUtil.round(totalBalance  + op.getBalance());
				exporter.endLine();
			}
			
			// rows for summary of tax
			for (String key : resumen.keySet()) {
				String[] k = key.split("-");
				int j = 0;
				exporter.startLine();
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), k[0]);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), k.length>1 ? k[1] : "");
				exporter.exportColumn(metadata.getColumns().get((j++)), resumen.get(key));
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.exportColumn(metadata.getColumns().get((j++)), null);
				exporter.endLine();
			}
			
			// row for total amounts
			int i = 0;
			exporter.startLine();
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), list
					.stream().filter(o -> o.getTotalBase() != null)
					.mapToDouble(OperationReport::getTotalBase).sum());
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), list
					.stream().filter(o -> o.getTotalQuota() != null)
					.mapToDouble(OperationReport::getTotalQuota).sum());
			exporter.exportColumn(metadata.getColumns().get((i++)), null);
			exporter.exportColumn(metadata.getColumns().get((i++)), list
					.stream().filter(o -> o.getTotalSurchargeQuota() != null)
					.mapToDouble(OperationReport::getTotalSurchargeQuota).sum());
			exporter.exportColumn(metadata.getColumns().get((i++)), totalBalance);
			exporter.endLine();
			
			exporter.endExport(output);
			output.flush();
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private ReportMetadata getMetadata() throws ReportException {
		ReportMetadata metadata = new ReportMetadata();
		for (ReportColumnMetadata rcm : COLUMN_LABELS) {
			metadata.getColumns().add(rcm);
		}
		return metadata;
	}
	
	public List<OperationReportTax> getCurrentTaxes() {
		if (getDetailModel() != null && getDetailModel().isRowAvailable()) {
			OperationReport op = (OperationReport) getDetailModel().getRowData();
			if (op != null) {
				return op.getTaxes();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AmortizationDetail> getPdfReport() throws ManagerBeanException {
		if (getDetailModel() != null ) {
			return (List<AmortizationDetail>) getDetailModel().getWrappedData();
		}
		return null;
	}
}
