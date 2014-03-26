package com.code.aon.ui.fiscal.controller;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.StringUtils;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.TaxColumn;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.fiscal.vat.tax.VatTaxKeyEx;
import com.code.aon.fiscal.vat.tax.VatTaxKeyExComparator;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.report.dynamic.DynaElements;
import com.code.aon.report.dynamic.DynaReport;
import com.code.aon.report.jr.JRBeanCollectionDataSource;
import com.code.aon.report.jr.exporter.IJRExporterFactory;
import com.code.aon.report.jr.exporter.JRExporterFactoryManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class VatTaxSummaryReport implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer year;
	private VatTaxStatus status;
	private SecurityLevel securityLevel;
	private boolean accumulatedColumnVisible = true;
	private boolean declaredColumnVisible = true;
	private boolean resultColumnVisible = true;
	private boolean adjustColumnVisible = true;
	private boolean toDeclareColumnVisible = true;
	private FiscalParametersController fiscalParams;

	public VatTaxSummaryReport() {
		setSecurityLevel( AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL);
		String defYear = getFiscalParams().getDefaultYear();
		setYear( StringUtils.isEmpty(defYear)?null:Integer.parseInt(defYear) );
	}

	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}

	public VatTaxStatus getStatus() {
		return status;
	}
	public void setStatus(VatTaxStatus status) {
		this.status = status;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	
	public boolean isAccumulatedColumnVisible() {
		return accumulatedColumnVisible;
	}

	public void setAccumulatedColumnVisible(boolean accumulatedColumnVisible) {
		this.accumulatedColumnVisible = accumulatedColumnVisible;
	}

	public boolean isDeclaredColumnVisible() {
		return declaredColumnVisible;
	}

	public void setDeclaredColumnVisible(boolean declaredColumnVisible) {
		this.declaredColumnVisible = declaredColumnVisible;
	}

	public boolean isResultColumnVisible() {
		return resultColumnVisible;
	}

	public void setResultColumnVisible(boolean resultColumnVisible) {
		this.resultColumnVisible = resultColumnVisible;
	}

	public boolean isAdjustColumnVisible() {
		return adjustColumnVisible;
	}

	public void setAdjustColumnVisible(boolean adjustColumnVisible) {
		this.adjustColumnVisible = adjustColumnVisible;
	}

	public boolean isToDeclareColumnVisible() {
		return toDeclareColumnVisible;
	}

	public void setToDeclareColumnVisible(boolean toDeclareColumnVisible) {
		this.toDeclareColumnVisible = toDeclareColumnVisible;
	}

	
	public String onSummaryReport() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
			Criteria c = new Criteria();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), getYear());
			if (getStatus() != null) {
				c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_STATUS), getStatus());	
			}
			if (getSecurityLevel() != null) {
				c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_SECURITY_LEVEL), getSecurityLevel());
			}
			c.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD));
			c.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_KEY));
			c.addOrder(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_PERCENT));
			List<ITransferObject> list = bean.getList(c);
			VatTaxKeyExComparator comparator = new VatTaxKeyExComparator();
			TreeMap<VatTaxKeyEx, VatSummaryReport> map = new TreeMap<VatTaxKeyEx, VatSummaryReport>(comparator);
			List<Period> periods = new LinkedList<Period>();
			for (ITransferObject det : list) {
				VatTaxDetail d = (VatTaxDetail) det;
				if (!periods.contains(d.getVatTax().getPeriod())) {
					periods.add(d.getVatTax().getPeriod());
				}
				VatTaxKeyEx keyEx = new VatTaxKeyEx();
				keyEx.setKey(d.getKey());
				keyEx.setPercent(d.getPercent());
				VatSummaryReport report = map.get(keyEx);
				if (report == null) {
					report = new VatSummaryReport();
					report.setKeyEx(keyEx);
					map.put(keyEx,report);	
				}
				report.addVatTaxDetail(d);
			}
			DynaElements dyn = new DynaElements();
			DynaReport report = new DynaReport();
			report.addField("to", VatSummaryReport.class);
			AbstractColumn kc = dyn.getStringColumn("keyName", "Clave", 200);
			kc.setPrintRepeatedValues(false);
			report.addColumn(kc);
			report.addColumn(dyn.getNumberColumn("percent", "%"));
			
			Style styleOdd = new Style();
			styleOdd.setFont(DynaElements.DETAIL_FONT);
			styleOdd.setHorizontalAlign(HorizontalAlign.RIGHT);
			styleOdd.setPattern(DynaElements.NUMBER_PATTERN);
			styleOdd.setTextColor(Color.DARK_GRAY);

			Style styleEven = new Style();
			styleEven.setFont(DynaElements.DETAIL_FONT);
			styleEven.setHorizontalAlign(HorizontalAlign.RIGHT);
			styleEven.setPattern(DynaElements.NUMBER_PATTERN);
			
			int i = 1;
			for (Period period : periods) {
				AbstractColumn ac = null;
				if (isAccumulatedColumnVisible()) {
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.ACUMULADO,true), "Acum - B.I.");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.ACUMULADO,false), "Acum -Cuota");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
				}
				if (isDeclaredColumnVisible()) {
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.DECLARADO,true), "Decl - B.I.");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.DECLARADO,false), "Decl -Cuota");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
				}
				if (isResultColumnVisible()) {
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.RESULTADO,true), "Resu - B.I.");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.RESULTADO,false), "Resu -Cuota");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
				}
				if (isAdjustColumnVisible()) {
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.AJUSTE,true), "Ajst - B.I.");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.AJUSTE,false), "Ajst -Cuota");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
				}
				if (isToDeclareColumnVisible()) {
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.DECLARAR,true), "A Dcl - B.I.");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
					ac = dyn.getNumberColumn(new ColumnCustomExpression(period,TaxColumn.DECLARAR,false), "A Dcl -Cuota");
					ac.setStyle(i%2 == 0 ?styleOdd:styleEven);
					report.addColumn(ac);
				}
				++i;
			}
			report.getReport().setColspan(0, 2, " ", DynaElements.COLUMN_HEADER_STYLE);
			int columnsOffest = 2;
			int colSpan = 2 * ((isAccumulatedColumnVisible()?1:0) + (isDeclaredColumnVisible()?1:0) + (isResultColumnVisible()?1:0) + (isAdjustColumnVisible()?1:0) + (isToDeclareColumnVisible()?1:0));
			for (Period period : periods) {
				report.getReport().setColspan(columnsOffest, colSpan, period.getName(AonUtil.getCurrentLocale()), DynaElements.COLUMN_HEADER_STYLE);
				columnsOffest += colSpan;
			}
			//DynaReportManager drm = new DynaReportManager();
			toExcel(report,"Resumen-Decl-IVA", map.values());
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		return null;
	}
	
	private void toExcel(DynaReport dynaReport,String filename, Collection<?> c) {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, filename, MimeType.MIME_MS_EXCEL);
			dynaReport.getReport().setPrintColumnNames(true)
				.setIgnorePagination(true)
				.setMargins(0, 0, 0, 0);
			DynamicReport dr = dynaReport.getReport().build();
			dr.setReportName(filename);
			dr.setWhenNoDataStyle(DynaElements.DETAIL_STYLE);
			JRDataSource ds = new JRBeanCollectionDataSource(c);
			JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
			IJRExporterFactory fm = JRExporterFactoryManager.getJRExporterFactory(OutputFormat.XLS); 
			JRExporter exporter = fm.getJRExporter();
		    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out); 
			fm.fillJRParametersMap(null,exporter.getParameters());
			exporter.exportReport();
		} catch (IOException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (JRException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ReportException e) {
			e.printStackTrace();
			String msg = "No se pudo generar el listado";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
	}
		
	
	public class VatSummaryReport {
		private VatTaxKeyEx keyEx;
		private Map<Period, VatTaxDetail> map = new HashMap<Period, VatTaxDetail>();
		
		public VatTaxKeyEx getKeyEx() {
			return keyEx;
		}
		public void setKeyEx(VatTaxKeyEx keyEx) {
			this.keyEx = keyEx;
		}
		public String getKeyName() {
			return getKeyEx().getKey().getName( AonUtil.getCurrentLocale() );
		}
		public Double getPercent() {
			return getKeyEx().getKey().isPercentVisible()?getKeyEx().getPercent():null;
		}

		public Map<Period, VatTaxDetail> getMap() {
			return map;
		}
		public void setMap(Map<Period, VatTaxDetail> map) {
			this.map = map;
		}
		
		public void addVatTaxDetail( VatTaxDetail detail ) {
			Period period = detail.getVatTax().getPeriod();
			map.put(period, detail);			
		}
		public VatSummaryReport getTo() {
			return this;
		}
	}

	public static class KeyCustomExpression implements CustomExpression {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Locale locale;
		
		public KeyCustomExpression(Locale locale) {
			this.locale = locale;
		}
		
		@Override
		public String getClassName() {
			return Double.class.getName();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object evaluate(Map fields, Map variables, Map parameters) {
			VatSummaryReport to = (VatSummaryReport) fields.get("to");
			return to.getKeyEx().getKey().getName(locale);
		}
	}	

	public static class ColumnCustomExpression implements CustomExpression {
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		private Period period;
		private TaxColumn type;
		private boolean taxableBase;

		public ColumnCustomExpression(Period period, TaxColumn type, boolean taxableBase) {
			this.period = period;
			this.type = type;
			this.taxableBase = taxableBase; 
		}

		@Override
		public String getClassName() {
			return Double.class.getName();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object evaluate(Map fields, Map variables, Map parameters) {
			VatSummaryReport to = (VatSummaryReport) fields.get("to");
			if (to.getMap().containsKey(period)) {
				VatTaxDetail detail = to.getMap().get(period);
				VatTaxKey key = detail.getKey();
				Double value = null;
				if (taxableBase && key.isTaxableBaseVisible() && type == TaxColumn.ACUMULADO) {
					value = detail.getTaxableBaseAccumulated();
				} else if (taxableBase && key.isTaxableBaseVisible() && type == TaxColumn.DECLARADO) {
					value = detail.getTaxableBaseDeclared();
				} else if (taxableBase && key.isTaxableBaseVisible() && type == TaxColumn.RESULTADO) {
					value = detail.getTaxableBaseResult();
				} else if (taxableBase && key.isTaxableBaseVisible() && type == TaxColumn.AJUSTE) { 
					value = detail.getTaxableBaseAdjust();
				} else if (taxableBase && key.isTaxableBaseVisible() && type == TaxColumn.DECLARAR) {
					value = detail.getTaxableBase();
				} else if (!taxableBase && key.isQuotaVisible() && type == TaxColumn.ACUMULADO) {
					value = detail.getQuotaAccumulated();
				} else if (!taxableBase && key.isQuotaVisible() && type == TaxColumn.DECLARADO) {
					value = detail.getQuotaDeclared();
				} else if (!taxableBase && key.isQuotaVisible() && type == TaxColumn.RESULTADO) {
					value = detail.getQuotaResult();
				} else if (!taxableBase && key.isQuotaVisible() && type == TaxColumn.AJUSTE) { 
					value = detail.getQuotaAdjust();
				} else if (!taxableBase && key.isQuotaVisible() && type == TaxColumn.DECLARAR) {
					value = detail.getQuota();
				}
				return value;
			}
			return null;
		}
	}

}
