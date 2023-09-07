package com.code.aon.ui.accounting.controller;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class AccountAppParamsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String DATE_PATTERN = "dd/MM/yyyy";
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat(DATE_PATTERN);

	private static Map<String, String> DEFAULT_PARAMETERS;
	static {
		
		
		DEFAULT_PARAMETERS = new HashMap<String, String>();
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_SALES_ACC.getValue(),"700000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_PURCHASE_ACC.getValue(),"600000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC.getValue(),"477000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_PAID_VAT_ACC.getValue(),"472000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_PAID_RET_ACC.getValue(),"473000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_CHARGED_RET_ACC.getValue(),"475100000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_CASH_ACC.getValue(),"570000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_FINAN_EXPENSES_ACC.getValue(),"669000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_PREPAYMENT_ACC.getValue(),"555900000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_ASSET_LOST_ACC.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_ASSET_PROFIT_ACC.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_VAT_PERCENT.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_RETENTION_PERCENT.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_INVOICE_SERIES.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_PERIOD.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_OPERATIONS_DEADLINE.getValue(),null);
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEADLINE_INCL_RECTIFICATIONS.getValue(),"false");
		
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_SALARY_ACC.getValue(),"640000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_SALARY_IK_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC.getValue(),"465000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_SALARY_CHARGED_RET_ACC.getValue(),"475100000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_SALARY_CHARGED_RET_IK_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_ALLOWANCE_ACC.getValue(),"629000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_COMPENSATION_ACC.getValue(),"641000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC.getValue(),"476000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC.getValue(),"642000000");
		DEFAULT_PARAMETERS.put(AppParam.ACC_SALARY_DED_ADV_PAYMENT_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.ACC_SALARY_DED_SEIZE_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.ACC_SALARY_DED_IN_KIND_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.ACC_SALARY_DED_OTHER_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEF_DUA_VAT_ACC.getValue(), "472000000" );
		DEFAULT_PARAMETERS.put(AppParam.ACC_DEF_DUA_DUTY_ACC.getValue(), null );
		
		DEFAULT_PARAMETERS.put(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC.getValue(), null );
		DEFAULT_PARAMETERS.put(AppParam.OCR_DEFAULT_ITEM.getValue(), null );

	}
	
	private Map<String, ApplicationParameter> parameters;

	private Date accOperationsDeadline;
	private Account accDefaultSalaryAccount;
	private Account accDefaultSalaryIKAccount;
	private Account accDefaultAllowanceAccount;
	private Account accDefaultCompensationAccount;
	private Account accSalaryChargedRetAccount;
	private Account accSalaryChargedRetIKAccount;
	private Account accDefaultSocialInsuranceAccount;
	private Account accDefaultPendingSalaryAccount;
	private Account accDefaultCompanySocInsAccount;
	private Account accSalaryDedAdvPaymentAccount;
	private Account accSalaryDedSeizeAccount;
	private Account accSalaryDedInKindAccount;
	private Account accSalaryDedOtherAccount;
	
	private Account accVatNegativeAdjustAccount;
	
	private Account accDefDuaVatAccount;
	private Account accDefDuaDutyAccount;
	
	private Item ocrDefaultItem;

	public Map<String, ApplicationParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, ApplicationParameter> parameters) {
		this.parameters = parameters;
	}

	public void onAccept(ActionEvent event) throws ManagerBeanException{
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Collection<ApplicationParameter>params = parameters.values();
		for(ApplicationParameter param : params){
			managerBean.update(param);
		}
		loadParameters();
		AonUtil.addInfoMessage("Los parámetros se guardaron correctamente.");		
	}
	
	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
		} catch (ManagerBeanException e) {
			String msg = "No se pueden cargar los parámetros contables";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void loadParameters() throws ManagerBeanException{
		parameters = new TreeMap<String, ApplicationParameter>();
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		IManagerBean managerBean = BeanManager.getManagerBean(ApplicationParameter.class);
		List<ITransferObject> list = managerBean.getList(null);
		Iterator<ITransferObject> iter = list.iterator();
		while (iter.hasNext()) {
			ApplicationParameter appParam = (ApplicationParameter) iter.next();
			parameters.put(appParam.getName(), appParam);
		}
		Set<String> keys = DEFAULT_PARAMETERS.keySet();
		for (String key : keys) {
			if (!parameters.containsKey(key)) {
				ApplicationParameter p = new ApplicationParameter();
				p.setName(key);
				String value = DEFAULT_PARAMETERS.get(key);
				System.out.println(
						key+"="+AppParam.ACC_OPERATIONS_DEADLINE.getValue()
												
						);
				
				if (StringUtils.endsWith(key, "_ACC")) {
					Criteria c = new Criteria();
					c.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), value);
					Iterator<ITransferObject> accounts = bean.getList(c).iterator();
					if (accounts.hasNext()) {
						Account account = (Account) accounts.next();
						value = account.getId().toString();
					} else {
						value = null;
					}
				}
				p.setValue(value);
				p = (ApplicationParameter) managerBean.insert(p);
				parameters.put(p.getName(), p);
			}
		}
		initializeAccOperationsDeadline();
		initializeAccDefaultSalaryAccount();
		initializeAccDefaultSalaryIKAccount();
		initializeAccDefaultAllowanceAccount();
		initializeAccDefaultCompensationAccount();
		initializeAccSalaryChargedRetAccount();
		initializeAccSalaryChargedRetIKAccount();
		initializeAccDefaultSocialInsuranceAccount();
		initializeAccDefaultPendingSalaryAccount();
		initializeAccDefaultCompanySocInsAccount();
		initializeAccSalaryDedAdvPaymentAccount();
		initializeAccSalaryDedSeizeAccount();
		initializeAccSalaryDedInKindAccount();
		initializeAccSalaryDedOtherAccount();
		initializeAccVatNegativeAdjustAccount();
		initializeAccDefDuaVatAccount();
		initializeAccDefDuaDutyAccount();
		
		try {
			Item defaultOcrItem = initializeItem(AppParam.OCR_DEFAULT_ITEM);
			if (defaultOcrItem == null || defaultOcrItem.getId() == null) {
				defaultOcrItem = new Item();
				defaultOcrItem.setProduct( new Product() );
			}
			setOcrDefaultItem( defaultOcrItem );
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Artículo OCR por defecto no válido.");
			setAccDefDuaDutyAccount( new Account() );	
		}
	}
	
	public ApplicationParameter getParameter(AppParam param) throws ManagerBeanException {
		if (parameters == null || parameters.isEmpty()) {
			loadParameters();	
		}
		return parameters.get(param.getValue()); 		
	}

	private void initializeAccOperationsDeadline() {
		String value = parameters.get(AppParam.ACC_OPERATIONS_DEADLINE.getValue()).getValue();
		setAccOperationsDeadline(null);
		if (StringUtils.isNotEmpty(value)) {
			try {
				setAccOperationsDeadline( FORMATTER.parse(value) );
			} catch (ParseException e) {
				AonUtil.addErrorMessage("Formato de fecha de operaciones incorrecto. (\"" + value + "\")");
				setAccOperationsDeadline( null);
			}
		}
	}
	public Date getAccOperationsDeadline() {
		return accOperationsDeadline;
	}
	public void setAccOperationsDeadline(Date accOperationsDeadline) {
		this.accOperationsDeadline = accOperationsDeadline;
		ApplicationParameter appParam = parameters.get(AppParam.ACC_OPERATIONS_DEADLINE.getValue());
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(AppParam.ACC_OPERATIONS_DEADLINE.getValue());
		}
		appParam.setValue(accOperationsDeadline==null?null:FORMATTER.format(accOperationsDeadline));
		parameters.put(AppParam.ACC_OPERATIONS_DEADLINE.getValue(), appParam);
	}

	private Item initializeItem(AppParam param) throws ManagerBeanException {
		Item item = new Item();
		IManagerBean bean = BeanManager.getManagerBean(Item.class);		
		String value = parameters.get(param.getValue()).getValue();
		if (StringUtils.isNotEmpty(value)) {
			try {
				item = (Item) bean.get(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				// nothing. Appears empty.
			}
		}
		return item;	
	}
	private void putItem(AppParam param,Item item) {
		ApplicationParameter appParam = parameters.get(param.getValue());
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(param.getValue());
		}
		appParam.setValue(item == null || item.getId() == null
				?null
				:item.getId().toString());
		parameters.put(param.getValue(), appParam);
	}

	private Account initializeAccount(AppParam param) throws ManagerBeanException {
		Account account = new Account();
		IManagerBean bean = BeanManager.getManagerBean(Account.class);		
		String value = parameters.get(param.getValue()).getValue();
		if (StringUtils.isNotEmpty(value)) {
			try {
				account = (Account) bean.get(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				// nothing. Appears empty.
			}
		}
		return account;	
	}
	private void putAccount(AppParam param,Account account) {
		ApplicationParameter appParam = parameters.get(param.getValue());
		if (appParam == null) {
			appParam = new ApplicationParameter();
			appParam.setName(param.getValue());
		}
		appParam.setValue(account == null || account.getId() == null
				?null
				:account.getId().toString());
		parameters.put(param.getValue(), appParam);
	}

	private void initializeAccDefaultSalaryAccount() {
		try {
			setAccDefaultSalaryAccount( initializeAccount(AppParam.ACC_DEFAULT_SALARY_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultSalaryAccount(  new Account() );	
		}
	}
	public Account getAccDefaultSalaryAccount() {
		return accDefaultSalaryAccount;
	}
	public void setAccDefaultSalaryAccount(Account accDefaultSalaryAccount) {
		this.accDefaultSalaryAccount = accDefaultSalaryAccount;
		putAccount(AppParam.ACC_DEFAULT_SALARY_ACC,accDefaultSalaryAccount);
	}

	private void initializeAccDefaultSalaryIKAccount() {
		try {
			setAccDefaultSalaryIKAccount( initializeAccount(AppParam.ACC_DEFAULT_SALARY_IK_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultSalaryIKAccount( new Account() );	
		}
	}
	public Account getAccDefaultSalaryIKAccount() {
		return accDefaultSalaryIKAccount;
	}
	public void setAccDefaultSalaryIKAccount(Account accDefaultSalaryIKAccount) {
		this.accDefaultSalaryIKAccount = accDefaultSalaryIKAccount;
		putAccount(AppParam.ACC_DEFAULT_SALARY_IK_ACC,accDefaultSalaryIKAccount);
	}
	
	private void initializeAccDefaultAllowanceAccount() {
		try {
			setAccDefaultAllowanceAccount( initializeAccount(AppParam.ACC_DEFAULT_ALLOWANCE_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultAllowanceAccount( new Account() );	
		}
	}
	public Account getAccDefaultAllowanceAccount() {
		return accDefaultAllowanceAccount;
	}
	public void setAccDefaultAllowanceAccount(Account accDefaultAllowanceAccount) {
		this.accDefaultAllowanceAccount = accDefaultAllowanceAccount;
		putAccount(AppParam.ACC_DEFAULT_ALLOWANCE_ACC,accDefaultAllowanceAccount);
	}
	
	private void initializeAccDefaultCompensationAccount() {
		try {
			setAccDefaultCompensationAccount( initializeAccount(AppParam.ACC_DEFAULT_COMPENSATION_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultCompensationAccount( new Account() );	
		}
	}
	public Account getAccDefaultCompensationAccount() {
		return accDefaultCompensationAccount;
	}
	public void setAccDefaultCompensationAccount(Account accDefaultCompensationAccount) {
		this.accDefaultCompensationAccount = accDefaultCompensationAccount;
		putAccount(AppParam.ACC_DEFAULT_COMPENSATION_ACC,accDefaultCompensationAccount);
	}
	
	private void initializeAccSalaryChargedRetAccount() {
		try {
			setAccSalaryChargedRetAccount( initializeAccount(AppParam.ACC_SALARY_CHARGED_RET_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccSalaryChargedRetAccount( new Account() );	
		}
	}
	public Account getAccSalaryChargedRetAccount() {
		return accSalaryChargedRetAccount;
	}
	public void setAccSalaryChargedRetAccount(Account accSalaryChargedRetAccount) {
		this.accSalaryChargedRetAccount = accSalaryChargedRetAccount;
		putAccount(AppParam.ACC_SALARY_CHARGED_RET_ACC,accSalaryChargedRetAccount);
	}
	
	private void initializeAccSalaryChargedRetIKAccount() {
		try {
			setAccSalaryChargedRetIKAccount( initializeAccount(AppParam.ACC_SALARY_CHARGED_RET_IK_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccSalaryChargedRetIKAccount( new Account() );	
		}
	}
	public Account getAccSalaryChargedRetIKAccount() {
		return accSalaryChargedRetIKAccount;
	}
	public void setAccSalaryChargedRetIKAccount(Account accSalaryChargedRetIKAccount) {
		this.accSalaryChargedRetIKAccount = accSalaryChargedRetIKAccount;
		putAccount(AppParam.ACC_SALARY_CHARGED_RET_IK_ACC,accSalaryChargedRetIKAccount);
	}
	
	private void initializeAccDefaultSocialInsuranceAccount() {
		try {
			setAccDefaultSocialInsuranceAccount( initializeAccount(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultSocialInsuranceAccount( new Account() );	
		}
	}
	public Account getAccDefaultSocialInsuranceAccount() {
		return accDefaultSocialInsuranceAccount;
	}
	public void setAccDefaultSocialInsuranceAccount(
			Account accDefaultSocialInsuranceAccount) {
		this.accDefaultSocialInsuranceAccount = accDefaultSocialInsuranceAccount;
		putAccount(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC,accDefaultSocialInsuranceAccount);
	}
	
	private void initializeAccDefaultPendingSalaryAccount() {
		try {
			setAccDefaultPendingSalaryAccount( initializeAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultPendingSalaryAccount( new Account() );	
		}
	}
	public Account getAccDefaultPendingSalaryAccount() {
		return accDefaultPendingSalaryAccount;
	}
	public void setAccDefaultPendingSalaryAccount(
			Account accDefaultPendingSalaryAccount) {
		this.accDefaultPendingSalaryAccount = accDefaultPendingSalaryAccount;
		putAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC,accDefaultPendingSalaryAccount);
	}
	
	private void initializeAccDefaultCompanySocInsAccount() {
		try {
			setAccDefaultCompanySocInsAccount( initializeAccount(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefaultCompanySocInsAccount( new Account() );	
		}
	}
	public Account getAccDefaultCompanySocInsAccount() {
		return accDefaultCompanySocInsAccount;
	}
	public void setAccDefaultCompanySocInsAccount(
			Account accDefaultCompanySocInsAccount) {
		this.accDefaultCompanySocInsAccount = accDefaultCompanySocInsAccount;
		putAccount(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC,accDefaultCompanySocInsAccount);
	}

	private void initializeAccSalaryDedAdvPaymentAccount() {
		try {
			setAccSalaryDedAdvPaymentAccount( initializeAccount(AppParam.ACC_SALARY_DED_ADV_PAYMENT_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccSalaryDedAdvPaymentAccount( new Account() );	
		}
	}
	public Account getAccSalaryDedAdvPaymentAccount() {
		return accSalaryDedAdvPaymentAccount;
	}
	public void setAccSalaryDedAdvPaymentAccount(
			Account accSalaryDedAdvPaymentAccount) {
		this.accSalaryDedAdvPaymentAccount = accSalaryDedAdvPaymentAccount;
		putAccount(AppParam.ACC_SALARY_DED_ADV_PAYMENT_ACC,accSalaryDedAdvPaymentAccount);
	}
	
	private void initializeAccSalaryDedSeizeAccount() {
		try {
			setAccSalaryDedSeizeAccount( initializeAccount(AppParam.ACC_SALARY_DED_SEIZE_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccSalaryDedSeizeAccount( new Account() );	
		}
	}
	public Account getAccSalaryDedSeizeAccount() {
		return accSalaryDedSeizeAccount;
	}
	public void setAccSalaryDedSeizeAccount(
			Account accSalaryDedSeizeAccount) {
		this.accSalaryDedSeizeAccount = accSalaryDedSeizeAccount;
		putAccount(AppParam.ACC_SALARY_DED_SEIZE_ACC,accSalaryDedSeizeAccount);
	}
	
	private void initializeAccSalaryDedInKindAccount() {
		try {
			setAccSalaryDedInKindAccount( initializeAccount(AppParam.ACC_SALARY_DED_IN_KIND_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccSalaryDedInKindAccount( new Account() );	
		}
	}
	public Account getAccSalaryDedInKindAccount() {
		return accSalaryDedInKindAccount;
	}
	public void setAccSalaryDedInKindAccount(
			Account accSalaryDedInKindAccount) {
		this.accSalaryDedInKindAccount = accSalaryDedInKindAccount;
		putAccount(AppParam.ACC_SALARY_DED_IN_KIND_ACC,accSalaryDedInKindAccount);
	}

	private void initializeAccSalaryDedOtherAccount() {
		try {
			setAccSalaryDedOtherAccount( initializeAccount(AppParam.ACC_SALARY_DED_OTHER_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccSalaryDedOtherAccount( new Account() );	
		}
	}
	public Account getAccSalaryDedOtherAccount() {
		return accSalaryDedOtherAccount;
	}
	public void setAccSalaryDedOtherAccount(
			Account accSalaryDedOtherAccount) {
		this.accSalaryDedOtherAccount = accSalaryDedOtherAccount;
		putAccount(AppParam.ACC_SALARY_DED_OTHER_ACC,accSalaryDedOtherAccount);
	}
	
	private void initializeAccVatNegativeAdjustAccount() {
		try {
			setAccVatNegativeAdjustAccount( initializeAccount(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccVatNegativeAdjustAccount( new Account() );	
		}
	}
	public Account getAccVatNegativeAdjustAccount() {
		return accVatNegativeAdjustAccount;
	}
	public void setAccVatNegativeAdjustAccount(
			Account accVatNegativeAdjustAccount) {
		this.accVatNegativeAdjustAccount = accVatNegativeAdjustAccount;
		putAccount(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC,accVatNegativeAdjustAccount);
	}

	private void initializeAccDefDuaVatAccount() {
		try {
			setAccDefDuaVatAccount( initializeAccount(AppParam.ACC_DEF_DUA_VAT_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefDuaVatAccount( new Account() );	
		}
	}
	public Account getAccDefDuaVatAccount() {
		return accDefDuaVatAccount;
	}
	public void setAccDefDuaVatAccount(Account accDefDuaVatAccount) {
		this.accDefDuaVatAccount = accDefDuaVatAccount;
		putAccount(AppParam.ACC_DEF_DUA_VAT_ACC,accDefDuaVatAccount);
	}
	
	private void initializeAccDefDuaDutyAccount() {
		try {
			setAccDefDuaDutyAccount( initializeAccount(AppParam.ACC_DEF_DUA_DUTY_ACC));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Cuenta por defecto no válida.");
			setAccDefDuaDutyAccount( new Account() );	
		}
	}
	public Account getAccDefDuaDutyAccount() {
		return accDefDuaDutyAccount;
	}
	public void setAccDefDuaDutyAccount(Account accDefDuaDutyAccount) {
		this.accDefDuaDutyAccount = accDefDuaDutyAccount;
		putAccount(AppParam.ACC_DEF_DUA_DUTY_ACC,accDefDuaDutyAccount);
	}

	public Item getOcrDefaultItem() {
		return ocrDefaultItem;
	}

	public void setOcrDefaultItem(Item ocrDefaultItem) {
		this.ocrDefaultItem = ocrDefaultItem;
		putItem(AppParam.OCR_DEFAULT_ITEM,ocrDefaultItem);
	}
	
	
}