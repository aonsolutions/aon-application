package com.code.aon.fiscal.mod303;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.code.aon.AonVersion;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.Finance;
import com.code.aon.fiscal.FiscalActivity;
import com.code.aon.fiscal.FiscalActivityInfo;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.activity.Modules;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoType;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.IFiscalModelKey;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod303 implements IFiscalDeclaration, IMod303Declaration, Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private FiscalModel fiscalModel;
	private Map<Mod303Key,FiscalModelDetail> map;
	private List<Mod303Key> keysModel;
	private String domainName;
	
	public Mod303(String domainName) {
		this.domainName = domainName;
	}
	@Override
	public FiscalModelType getType() {
		return FiscalModelType.M303;
	}

	public void initializeDetails() throws ManagerBeanException {
		clearMap();
		Administration admin = fiscalModel.getAdministration();
		Period period = fiscalModel.getPeriod();
		
		boolean cacAdded = false;
		for (Mod303Key key : Mod303Key.values()) {
			if (key.accept(admin,period,fiscalModel.getYear()) 
					&& (key.getParentKey() == null || key.getParentKey() == Mod303Key.PBK)) {
				if (key == Mod303Key.CAC1 
				 || key == Mod303Key.CAC2
				 || key == Mod303Key.CAC3
				 || key == Mod303Key.CAC4
				 || key == Mod303Key.CAG1
				 || key == Mod303Key.CAG2
				 || key == Mod303Key.CAG3
				 || key == Mod303Key.CAG4) {
					if (!cacAdded) {
						addFiscalActivities();
						cacAdded = true;
					}
				} else {
					ensureDetail(key);	
				}
				
			}
		}
	}

	private void addFiscalActivities() throws ManagerBeanException {
		int year = fiscalModel.getYear();
		IManagerBean bean = BeanManager.getManagerBean(FiscalActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_YEAR), year);
		List<ITransferObject> list = bean.getList(criteria);
		int ac = 1;
		int ag = 1;
		for (ITransferObject to : list) {
			FiscalActivity fa = (FiscalActivity) to;
			if ( fa.isFarmer() ) {
				addFarmerActivity(fa,ag);
				ag++;
			} else {
				addActivity(fa,ac);
				ac++;
			}
		}
		
	}

    private void addActivity(FiscalActivity fa, int ac) throws ManagerBeanException {
    	// Los módulos salen de la base de datos de la agencia tributaria, y estan en castellano.
    	Locale locale = new Locale("es","es");
    	
    	FiscalModelDetail detail = new FiscalModelDetail();
    	detail.setFiscalModel(getHeader());
		detail.setDescription( fa.getEpigraph() + " - " + fa.getDescription() );
		detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac);
		String epi = fa.getEpigraph().replace(".", "");
		try {
			Integer e = Integer.parseInt(epi);
			detail.setAccumulatedAmount( e );
		} catch (NumberFormatException e) {
			throw new ManagerBeanException(fa.getEpigraph() + "no es un número válido");
		}
		addDetail(detail);
		if (!fiscalModel.isWithoutActivity()) {
	    	IManagerBean infoBean = BeanManager.getManagerBean(FiscalActivityInfo.class);
	    	Criteria criteria = new Criteria();
			criteria.addEqualExpression(infoBean.getFieldName(IEntityAlias.FISCAL_ACTIVITY_INFO_FISCAL_ACTIVITY_ID), fa.getId());
			List<ITransferObject> infos = infoBean.getList(criteria);
			int moduleNumber = 1;
			double seasonDays = 0;
			for (ITransferObject infoTo : infos) {
				FiscalActivityInfo info = (FiscalActivityInfo) infoTo;
				if (info.getType() == FiscalActivityInfoType.INFO) {
					if (info.getInfoKey() == FiscalActivityInfoKey.A03) {
						seasonDays = info.getDoubleValue();
					}
				} else if (info.getType() == FiscalActivityInfoType.VAT_MODULE) {
					detail = new FiscalModelDetail();
			    	detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "M" + moduleNumber + "U");
					detail.setDescription(info.getInfoKey().getName(locale));
					double unit = info.getDoubleValue();
					detail.setAccumulatedAmount(unit);
					detail.setDeclaredAmount(unit);
					addDetail(detail);
					
					detail = new FiscalModelDetail();
			    	detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "M" + moduleNumber + "I");
					detail.setDescription(info.getInfoKey().getName(locale));
					detail.setAccumulatedAmount(info.getBase());
					detail.setDeclaredAmount( unit == 0?0:info.getBase() / unit);
					addDetail(detail);
					
					detail = getDetail(Mod303Key.getKeyWithValue(Mod303Key.ACTIVITIES_PREFIX + ac + "C") );
					if (detail == null) {
						detail = new FiscalModelDetail();
						detail.setFiscalModel(getHeader());
						detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "C");
					}
					double c = CommonUtil.round( detail.getAccumulatedAmount() + info.getBase() );
					detail.setAccumulatedAmount( c );
					addDetail(detail);
					
					moduleNumber++;	
				} else {
					if (info.getType() == FiscalActivityInfoType.VAT_INFO) {
						if (info.getInfoKey() == FiscalActivityInfoKey.V03) {
							detail = new FiscalModelDetail();
					    	detail.setFiscalModel(getHeader());
							detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "D");
							detail.setAccumulatedAmount(info.getDoubleValue());
							addDetail(detail);
						} 
						if (!isLastPeriod()) {
							if (info.getInfoKey() == FiscalActivityInfoKey.V01) {
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "Z");
								double d = info.getDoubleValue();
								detail.setAccumulatedAmount(d);
								addDetail(detail);
								if (d > 0) { // Actividad de temporada
									detail = new FiscalModelDetail();
							    	detail.setFiscalModel(getHeader());
									detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "ZA");
									detail.setAccumulatedAmount(seasonDays);
									addDetail(detail);
								}
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "ZD");
								detail.setAccumulatedAmount(seasonDays>0?seasonDays:90);
								addDetail(detail);
							} else if (info.getInfoKey() == FiscalActivityInfoKey.V05) {
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "E");
								detail.setAccumulatedAmount(info.getDoubleValue());
								addDetail(detail);
							} else if (info.getInfoKey() == FiscalActivityInfoKey.V06) {
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "F");
								detail.setAccumulatedAmount(info.getDoubleValue());
								addDetail(detail);
							}
						} else {
							if (info.getInfoKey() == FiscalActivityInfoKey.V01) {
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "H");
								double d = info.getDoubleValue();
								detail.setAccumulatedAmount(d);
								addDetail(detail);
								if (d > 0) { // Actividad de temporada
									detail = new FiscalModelDetail();
							    	detail.setFiscalModel(getHeader());
									detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "HA");
									detail.setAccumulatedAmount(seasonDays);
									addDetail(detail);
								}
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "HD");
								detail.setAccumulatedAmount(seasonDays>0?seasonDays:90);
								addDetail(detail);
								
								detail = new FiscalModelDetail();
						    	detail.setFiscalModel(getHeader());
								detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "HT");
								double yearDays = getPreviousDays(fiscalModel,Mod303Key.ACTIVITIES_PREFIX + ac);
								detail.setAccumulatedAmount(seasonDays>0?seasonDays:yearDays);
								addDetail(detail);
							} 
						}
					}
				}
			}			
			if (isLastPeriod()) {
				Date fromDate = CommonUtil.getYearFirstDay(fiscalModel.getYear() );
				Date toDate = CommonUtil.getYearLastDay(fiscalModel.getYear() );
				VatTaxManager taxManager = new VatTaxManager( domainName );
				List<VatTaxDetail> vatDetails = taxManager.getVatTax(fiscalModel.getDomain(),fromDate, toDate );
				double g = 0.0;
				for (VatTaxDetail vatDetail : vatDetails) {
					if (vatDetail.getKey() == VatTaxKey.B1
					  || vatDetail.getKey() == VatTaxKey.B3
					  || vatDetail.getKey() == VatTaxKey.C1
					  || vatDetail.getKey() == VatTaxKey.D1
					  || vatDetail.getKey() == VatTaxKey.D3) {
						g = g + vatDetail.getQuotaAccumulated(); 
					}
					if (vatDetail.getKey() == VatTaxKey.EI || vatDetail.getKey() == VatTaxKey.PS) { 
						ensureDetail(Mod303Key.C59).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
						ensureDetail(Mod303Key.C82).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
					}
					if (vatDetail.getKey() == VatTaxKey.EX1 || vatDetail.getKey() == VatTaxKey.EX2) {
						ensureDetail(Mod303Key.C60).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
						ensureDetail(Mod303Key.C82).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
					}
					if (vatDetail.getKey() == VatTaxKey.XO) {
						ensureDetail(Mod303Key.C62).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
						ensureDetail(Mod303Key.C63).addAccumulatedAmount(vatDetail.getQuotaAccumulated());
					}
					if (vatDetail.getKey() == VatTaxKey.XI) {
						ensureDetail(Mod303Key.C74).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
						ensureDetail(Mod303Key.C75).addAccumulatedAmount(vatDetail.getQuotaAccumulated());
					}
					if (vatDetail.getKey() == VatTaxKey.OS) {
						ensureDetail(Mod303Key.C83).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
					}
					if (vatDetail.getKey() == VatTaxKey.A1) {
						ensureDetail(Mod303Key.C86).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
					}
					if (vatDetail.getKey() == VatTaxKey.EBI) {	// Ventas de inversion
						ensureDetail(Mod303Key.C87).addAccumulatedAmount(vatDetail.getTaxableBaseAccumulated());
					}
				}
				detail = getDetail(Mod303Key.getKeyWithValue(Mod303Key.ACTIVITIES_PREFIX + ac + "G") );
				if (detail == null) {
					detail = new FiscalModelDetail();
					detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "G");
				}
				detail.setAccumulatedAmount( g );
				addDetail(detail);

				detail = getDetail(Mod303Key.getKeyWithValue(Mod303Key.ACTIVITIES_PREFIX + ac + "I") );
				if (detail == null) {
					detail = new FiscalModelDetail();
					detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "I");
				}
				detail.setAccumulatedAmount( 0.0 );
				addDetail(detail);
				
				Modules modules = new Modules();
				detail = new FiscalModelDetail();
		    	detail.setFiscalModel(getHeader());
				detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "J");
				try {
					detail.setAccumulatedAmount(modules.getCuotaMin(fa.getEpigraph()));
				} catch (AonException e) {
					detail.setAccumulatedAmount(0.0);
				}
				addDetail(detail);

				detail = getDetail(Mod303Key.getKeyWithValue(Mod303Key.ACTIVITIES_PREFIX + ac + "K") );
				if (detail == null) {
					detail = new FiscalModelDetail();
					detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "K");
				}
				detail.setAccumulatedAmount( 0.0 );
				addDetail(detail);

				detail = getDetail(Mod303Key.getKeyWithValue(Mod303Key.ACTIVITIES_PREFIX + ac + "L") );
				if (detail == null) {
					detail = new FiscalModelDetail();
					detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "L");
				}
				detail.setAccumulatedAmount( 0.0 );
				addDetail(detail);

				detail = getDetail(Mod303Key.getKeyWithValue(Mod303Key.ACTIVITIES_PREFIX + ac + "M") );
				if (detail == null) {
					detail = new FiscalModelDetail();
					detail.setFiscalModel(getHeader());
					detail.setType(Mod303Key.ACTIVITIES_PREFIX + ac + "M");
				}
				detail.setAccumulatedAmount( 0.0 );
				addDetail(detail);
			}
		}
	}
    
	private double getPreviousDays(FiscalModel fiscalModel, String prefix) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("FiscalModelDetail.fiscalModel.year", fiscalModel.getYear());
		criteria.addLessThanExpression("FiscalModelDetail.fiscalModel.period", fiscalModel.getPeriod() );
		criteria.addEqualExpression("FiscalModelDetail.fiscalModel.model", FiscalModelType.M303 );
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_MODEL_DETAIL_TYPE), prefix + "ZD");
		List<ITransferObject> list = bean.getList(criteria);
		double t1 = 90;
		double t2 = 90;
		double t3 = 90;
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			if (detail.getFiscalModel().getPeriod() == Period.T1) {
				t1 = detail.getAmount();			
			} else if (detail.getFiscalModel().getPeriod() == Period.T2) {
				t2 = detail.getAmount();
			} else if (detail.getFiscalModel().getPeriod() == Period.T3) {
				t3 = detail.getAmount();
			}
		}
		return CommonUtil.round(t1+t2+t3);
	}
	private void addFarmerActivity(FiscalActivity fa, int ag) throws ManagerBeanException {
		FiscalModelDetail detail = new FiscalModelDetail();
		detail.setFiscalModel(getHeader());
		detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag);
		detail.setDescription( fa.getEpigraph() + " - " + fa.getDescription() );
		try {
			Integer e = Integer.parseInt(fa.getEpigraph());
			detail.setAccumulatedAmount( e );
		} catch (NumberFormatException e) {
			throw new ManagerBeanException(fa.getEpigraph() + "no es un número válido");
		}		
		addDetail(detail);
		
		detail = new FiscalModelDetail();
    	detail.setFiscalModel(getHeader());
		detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V1");
		double v1 = 0;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(domainName);
			SummaryProvider sp = new SummaryProvider();
			SummaryProviderParameters params = new SummaryProviderParameters(domainName);
			params.setAccountExpression( "70*|71*|72*|73*|75*|76*|77*|78*|79*" );
			params.setAccountLevel(5);
			if (!isLastPeriod()){
				params.setFromDate(getHeader().getPeriod().getStartDate(getHeader().getYear()));
				params.setToDate(getHeader().getPeriod().getDueDate(getHeader().getYear()));
			} else {
				params.setFromDate(CommonUtil.getYearFirstDay(getHeader().getYear()));
				params.setToDate(CommonUtil.getYearLastDay(getHeader().getYear()));
			}
			SummaryCollection sc = sp.getSummaryCollection(conn,params,false);
			v1 = CommonUtil.round(sc.getOpeningCredit() + sc.getCredit() - sc.getOpeningDebit() - sc.getDebit());
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
		detail.setAccumulatedAmount(v1);
		addDetail(detail);
		
		detail = new FiscalModelDetail();
    	detail.setFiscalModel(getHeader());
		detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V2");
		Modules modules = new Modules();
		double v2 = modules.getFarmerQuota(getHeader().getYear(), Integer.parseInt(fa.getEpigraph()));
		detail.setAccumulatedAmount( CommonUtil.round(v2 * 10000) );
		addDetail(detail);
		
		detail = new FiscalModelDetail();
    	detail.setFiscalModel(getHeader());
		detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V3");
		double v3 = CommonUtil.round(v1 * v2);
		detail.setAccumulatedAmount(v3);
		addDetail(detail);
		
		if (!isLastPeriod()){
			detail = new FiscalModelDetail();
	    	detail.setFiscalModel(getHeader());
			detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V4");
			double v4 = fa.getVatPercent();
			detail.setAccumulatedAmount(v4);
			addDetail(detail);
			
			detail = new FiscalModelDetail();
	    	detail.setFiscalModel(getHeader());
			detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V5");
			double v5 = CommonUtil.round(v3 * v4 / 100 );
			detail.setAccumulatedAmount(v5);
			addDetail(detail);
		} else {
			Date fromDate = CommonUtil.getYearFirstDay(fiscalModel.getYear() );
			Date toDate = CommonUtil.getYearLastDay(fiscalModel.getYear() );
			VatTaxManager taxManager = new VatTaxManager( domainName );
			List<VatTaxDetail> vatDetails = taxManager.getVatTax(fiscalModel.getDomain(),fromDate, toDate );
			double v6 = 0.0;
			for (VatTaxDetail vatDetail : vatDetails) {
				if (vatDetail.getKey() == VatTaxKey.B1
				  || vatDetail.getKey() == VatTaxKey.B3
				  || vatDetail.getKey() == VatTaxKey.C1
				  || vatDetail.getKey() == VatTaxKey.D1
				  || vatDetail.getKey() == VatTaxKey.D3) {
					v6 = v6 + vatDetail.getQuotaAccumulated(); 
				}
			}
			v6 = CommonUtil.round(v6 + (v3 * 1 / 100));
			detail = new FiscalModelDetail();
	    	detail.setFiscalModel(getHeader());
			detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V6");
			detail.setAccumulatedAmount(v6);
			addDetail(detail);
			
			detail = new FiscalModelDetail();
	    	detail.setFiscalModel(getHeader());
			detail.setType(Mod303Key.FARMING_ACTIVITIES_PREFIX + ag + "V7");
			double v7 = CommonUtil.round(v3 -  v6 );
			detail.setAccumulatedAmount(v7);
			addDetail(detail);
		}
		
	}
    
	@Override
    public void clearMap() {
    	map = null;
    }

    public Map<Mod303Key, FiscalModelDetail> getMap() {
		if (map == null) {
			map = new TreeMap<Mod303Key, FiscalModelDetail>();	
		}
		return map;
	}

	@Override
	public FiscalModel getHeader() {
		return fiscalModel;
	}
	@Override
	public void setHeader(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}
	public void setFiscalModel(FiscalModel fiscalModel) {
		this.fiscalModel = fiscalModel;
	}

	@Override
	public List<Mod303Key> getKeys() {
		if (keysModel == null) {
			keysModel = new LinkedList<Mod303Key>();
			for (Mod303Key key : getMap().keySet()) {
				if ((key.getParentKey() == null)
					|| (!isLastPeriod() && (key == Mod303Key.CAC1_F || key == Mod303Key.CAC2_F || key == Mod303Key.CAC3_F || key == Mod303Key.CAC4_F))
					|| (isLastPeriod() && (key == Mod303Key.CAC1_M || key == Mod303Key.CAC2_M || key == Mod303Key.CAC3_M || key == Mod303Key.CAC4_M))
					|| (!isLastPeriod() && (key == Mod303Key.CAG1_V5 || key == Mod303Key.CAG2_V5 || key == Mod303Key.CAG3_V5 || key == Mod303Key.CAG4_V5))
					|| (isLastPeriod() && (key == Mod303Key.CAG1_V7 || key == Mod303Key.CAG2_V7 || key == Mod303Key.CAG3_V7 || key == Mod303Key.CAG4_V7))
					) {
						keysModel.add(key);	
				}
			}
		}
		return keysModel;
	}
	
	@Override
	public Collection<FiscalModelDetail> getDetails() {
		return getMap().values();
	}
	
	@Override
	public FiscalModelDetail getDetail(IFiscalModelKey key) {
		return getMap().get(key);
	}
	
	@Override
	public FiscalModelDetail ensureDetail(IFiscalModelKey key) {
		FiscalModelDetail detail = getDetail(key);
		if (detail == null) {
			detail = new FiscalModelDetail();
			detail.setFiscalModel(getHeader());
			detail.setType(key.getValue());
			addDetail(detail);
		}
		return detail;
	}

	@Override
	public void calculate() throws AonException {
		Mod303CalculatorFactory factory = new Mod303CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod303Calculator calculator = factory.getCalculator( year , admin );
		calculator.calculate(this);
	}
	
	@Override
	public void addDetail(FiscalModelDetail detail) {
		Mod303Key key = Mod303Key.getKeyWithValue( detail.getType() );
		getMap().put(key, detail);
	}
	
	@Override
	public IFiscalModelKey getKey(String value) {
		return Mod303Key.getKeyWithValue(value);
	}
	
	@Override
	public Finance getFinance() {
		return fiscalModel!=null?fiscalModel.getFinance():null;
	}
	
	@Override
	public double getResult() {
		Mod303CalculatorFactory factory = new Mod303CalculatorFactory();
		int year = getHeader().getYear();
		Administration admin = getHeader().getAdministration(); 
		IMod303Calculator calculator = factory.getCalculator( year , admin );
		return calculator.getResult(this);
	}
	
	@Override
	public boolean isDeclarationNegativeAvailable() {
		return false;
	}

	@Override
	public boolean isToDeductDeclarationAvailable() {
		return false;
	}

	@Override
	public boolean isWithoutActivityDeclarationAvailable() {
		return true;
	}

	@Override
	public boolean isNegative() {
		return false;
	}
	@Override
	public boolean isCompensateDeclarationAvailable() {
		return true;
	}
	@Override
	public boolean isCompensate() {
		FiscalModelDetail detail = getDetail(Mod303Key.PBK);
		return (detail!=null && (getResult() < 0) && CommonUtil.round(detail.getAmount()) == 0);
	}
	
	@Override
	public boolean isPayback() {
		FiscalModelDetail detail = getDetail(Mod303Key.PBK);
		return (detail!=null && (getResult() < 0) && CommonUtil.round(detail.getAmount()) != 0);
	}
	@Override
	public boolean isPaybackDeclarationAvailable() {
		return isLastPeriod();
	}

	@Override
	public boolean isToDeduct() {
		return false;
	}
	public boolean isLastPeriod(){
		return (fiscalModel != null && (fiscalModel.getPeriod() == Period.T4 || fiscalModel.getPeriod() == Period.M12));
	}
	public double ensureAmount(Mod303Key key) {
		FiscalModelDetail detail = getDetail( key );
		return detail != null?detail.getAmount():0.0;
	}
	//****************************************************
	@Override
	public boolean isGeneralRegime() {
		return false;
	}
	@Override
	public int getDomain() {
		return getHeader().getDomain();
	}
	@Override
	public int getYear() {
		return getHeader().getYear();
	}
	@Override
	public Period getPeriod() {
		return getHeader().getPeriod();
	}
	@Override
	public Administration getAdministration() {
		return getHeader().getAdministration();
	}
	@Override
	public IBankAccountContainer getBankAccountContainer() {
		return getHeader().getFinance();
	}
	@Override
	public boolean isReplacement() {
		return getHeader().isReplacement();
	}
	@Override
	public boolean isComplementary() {
		return getHeader().isComplementary();
	}
	@Override
	public boolean isTaxRefundRegistry() {
		//		Artículo 30 Devoluciones al término de cada período de liquidación
		//		[ ... ]
		//		3. Serán inscritos en el registro, previa solicitud, los sujetos pasivos 
		//		   	en los que concurran los siguientes requisitos:
		//		[ ... ]
		//	    d) Que no realicen actividades que tributen en el régimen simplificado.
		//		[ ... ]
		return false;
	}
	@Override
	public double getProrata() {
		return 100.0;
	}
	
	public boolean contains(Mod303Key key) {
		return (getDetail(key) != null); 
	}
	public double getEnsuredAmount(Mod303Key key) {
		FiscalModelDetail detail = getDetail(key);
		if (detail != null) {
			return detail.getAmount();
		}
		return 0.0;
	}
}
