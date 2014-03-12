package com.code.aon.ui.fiscal.controller.mod311;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DISK_ERROR;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.AonVersion;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.tax.model.MOD311.MOD311Format;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod311Key;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.fiscal.controller.activity.FiscalActivityController;
import com.code.aon.ui.fiscal.controller.model.FiscalModelController;
import com.code.aon.ui.fiscal.file.MOD311Writer;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod311Controller extends FiscalModelController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean payBack;
	private boolean activityPanelVisible;
	private Mod311Key selectedKey;
	
	public boolean isActivityPanelVisible() {
		return activityPanelVisible;
	}

	public void setActivityPanelVisible(boolean activityPanelVisible) {
		this.activityPanelVisible = activityPanelVisible;
	}
	

	public Mod311Key getSelectedKey() {
		return selectedKey;
	}

	public void setSelectedKey(Mod311Key selectedKey) {
		this.selectedKey = selectedKey;
	}

	@Override
	protected FiscalModelType getModelType() {
		return 	FiscalModelType.M311;
	}
	
	public boolean isPayBack() {
		return payBack;
	}
	public void setPayBack(boolean payBack) {
		this.payBack = payBack;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		checkFiscalActivity(FiscalModelType.M311);
		super.onEditSearch(event);	
	}

	@Override
	public boolean isDifEnabled() {
		return false;
	}
	
	public void onCreateDisk(ActionEvent event) {
		try { 
			MOD311Writer mod311Writer = new MOD311Writer();
			FiscalModel fm = (FiscalModel) getTo();
			List<FiscalModel> list = new LinkedList<FiscalModel>();
			list.add(fm);
			MOD311Format format = MOD311Format.getFormat(fm.getAdministration(), fm.getYear());
			setFileOutput( mod311Writer.createMOD311(list, format) );
		    if (getFileOutput() != null) {
		    	if (getFileOutput().getErrors().size() > 0) {
		    		AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DISK_ERROR);
		        }
		    }
		    if (isAeatValidable()) {
		    	validateAeatFile();	
		    }
		} catch (IllegalArgumentException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}

	@Override
	public MimeType getMimeType() {
		FiscalModel fm = (FiscalModel) getTo();
		MOD311Format format = MOD311Format.getFormat(fm.getAdministration(), fm.getYear());
		return format.getMimeType();
	}	

	@Override
	protected void accept() {
		FiscalModelDetail detail = getDeclaration().getDetail(Mod311Key.PBK );
		if (detail != null) {
			detail.setAccumulatedAmount((isPayBack())?getDeclaration().getResult():0.0);
		}
		super.accept();
	}
	
	public void onShowFinalizePanel(ActionEvent event) {
		setPayBack(true);
		super.onShowFinalizePanel(event);
	}
	
	protected boolean mustCreateFinance(FiscalModel to) {
		if (getDeclaration().getResult() < 0 && !isPayBack()) {
			return false;
		}
		return super.mustCreateFinance(to);
	}
	
	public void onShowActivities(ActionEvent event) {
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			String key = ctx.getExternalContext().getRequestParameterMap().get("key");
			setSelectedKey(Mod311Key.valueOf(key) );
			setActivityPanelVisible(true);
			FiscalModelDetail detail = getDeclaration().getDetail(getSelectedKey());
			String description = detail.getDescription();
			String epi = StringUtils.substringBefore(description, "-");
			epi = StringUtils.trim(epi);
			
			FiscalActivityController controller = (FiscalActivityController) AonUtil.getRegisteredBean("fiscalActivity");
			Criteria c = new Criteria();
			IManagerBean bean = controller.getManagerBean();
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), getDeclaration().getHeader().getYear());
			c.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_EPIGRAPH), epi);
			controller.setCriteria(c);
			controller.onSearch(event);
			controller.getModel().setRowIndex(0);
			controller.onSelect(null);
			
			List<FiscalActivityInfo> list = controller.getM311List();
			FiscalActivity fa = controller.getFiscalActivity();
			if (!fa.isFarmer()) {
				double value = 0;
				FiscalActivityInfo inf = null;
				for (FiscalActivityInfo info : list ) {
					if (info.getInfoKey() == FiscalActivityInfoKey.X01) {
						value  = info.getDoubleValue();
						inf = info;
						break;
					}
				}
				if (value == 0) {
					int year = getDeclaration().getHeader().getYear();
					Date fromDate = getDeclaration().getHeader().getPeriod().getStartDate( year );
					Date toDate = getDeclaration().getHeader().getPeriod().getDueDate( year );
					VatTaxManager taxManager = new VatTaxManager(AonUtil.getDomainName());
					List<VatTaxDetail> vatDetails = taxManager.getVatTax(detail.getDomain(),fromDate, toDate );
					double x02 = 0;
					for (VatTaxDetail vatDetail : vatDetails) {
						if (vatDetail.getKey() == VatTaxKey.CP || vatDetail.getKey() == VatTaxKey.GT ) {
							x02 = x02 + vatDetail.getQuotaAccumulated(); 
						}					
					}
					inf.setDoubleValue(x02);
					controller.onChangeM311(event);				
				}
			} else {
				double y01Value = 0;
				double y04Value = 0;
				FiscalActivityInfo y01Info = null;
				FiscalActivityInfo y04Info = null;
				for (FiscalActivityInfo info : list ) {
					if (info.getInfoKey() == FiscalActivityInfoKey.Y04) {
						y04Value  = info.getDoubleValue();
						y04Info = info;
					}
					if (info.getInfoKey() == FiscalActivityInfoKey.Y01) {
						y01Value  = info.getDoubleValue();
						y01Info = info;
					}
				}
				if (y01Value == 0 && y04Value == 0) {
					int year = getDeclaration().getHeader().getYear();
					Date fromDate = CommonUtil.getYearFirstDay(year);
					Date toDate = CommonUtil.getYearLastDay(year);
					if (y01Value == 0) {
						SummaryProvider sp = new SummaryProvider();
						SummaryProviderParameters params = new SummaryProviderParameters(AonUtil.getDomainName());
						params.setAccountExpression( "7*" );
						params.setAccountLevel(5);
						params.setFromDate(fromDate);
						params.setToDate(toDate);
						SummaryCollection sc = sp.getSummaryCollection(params,false);
						double y01 = CommonUtil.round(sc.getOpeningCredit() + sc.getCredit() - sc.getOpeningDebit() - sc.getDebit());
						y01Info.setDoubleValue(y01);
					}
					if (y04Value == 0) {
						VatTaxManager taxManager = new VatTaxManager(AonUtil.getDomainName());
						List<VatTaxDetail> vatDetails = taxManager.getVatTax(detail.getDomain(),fromDate, toDate );
						double y04 = 0;
						for (VatTaxDetail vatDetail : vatDetails) {
							if (vatDetail.getKey() == VatTaxKey.CP || vatDetail.getKey() == VatTaxKey.GT ) {
								y04 = y04 + vatDetail.getQuotaAccumulated(); 
							}					
						}
						y04Info.setDoubleValue(y04);
					}
				}
				controller.onChangeM311(event);				
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
	public void onHideActivityPanel(ActionEvent event) {
		setActivityPanelVisible(false);
	}
	
	public void onAcceptActivity(ActionEvent event) {
		try {
			if ( getDeclaration().getHeader().getStatus() == FiscalModelStatus.PENDING) {
				FiscalActivityController controller = (FiscalActivityController) AonUtil.getRegisteredBean("fiscalActivity");
				controller.accept(event);
				List<FiscalActivityInfo> list = controller.getM311List();
				double value = 0;
				FiscalActivity fa = controller.getFiscalActivity();
				if (!fa.isFarmer()) {
					for (FiscalActivityInfo info : list ) {
						if (info.getInfoKey() == FiscalActivityInfoKey.X11) {
							value  = info.getDoubleValue();
						}
					}
				} else {
					for (FiscalActivityInfo info : list ) {
						if (info.getInfoKey() == FiscalActivityInfoKey.Y08) {
							value  = info.getDoubleValue();
						}
					}
				}
				FiscalModelDetail detail = getDeclaration().getDetail(getSelectedKey());
				detail.setAccumulatedAmount(value);
				getDeclaration().calculate();
			}
			onHideActivityPanel(event);
		} catch (Throwable e) {
			AonUtil.addErrorMessage(e.getMessage()); 
			throw new AbortProcessingException(e.getMessage(),e);
		}
	}
}
