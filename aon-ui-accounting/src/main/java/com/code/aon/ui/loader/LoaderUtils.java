package com.code.aon.ui.loader;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.Brand;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Segment;
import com.esferalia.aon.entity.IEntityAlias;

public class LoaderUtils {
	
	private AccountingUtil accountingUtil;
	private Account outputVatAccount;
	private Account inputVatAccount;
	private Account retentionAccount;

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	public Bank ensureBank(String bankCode, String banco) throws ManagerBeanException {
		IManagerBean bankBean = BeanManager.getManagerBean(Bank.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bankBean.getFieldName( IEntityAlias.BANK_CODE) , bankCode);
		List<ITransferObject> list = bankBean.getList(c);
		if (list != null && list.size() > 0) {
			return (Bank) list.get(0);	
		} 
		if (StringUtils.isNotBlank(banco)) {
			Bank bank = new Bank();
			bank.setCode(bankCode);
			bank.setName(banco);
			return (Bank) bankBean.insert(bank);
		}
		return null;
	}

	public PayMethod ensurePayMethod(String formaPago) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.PAY_METHOD_NAME) , formaPago);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (PayMethod) list.get(0);	
		} 
		PayMethod payMethod = new PayMethod();
		payMethod.setName(formaPago);
		payMethod.setType(PayMethodType.OTHER);
		return (PayMethod) bean.insert(payMethod);
	}

	public Account ensureAccount(String accountCode, String description) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.ACCOUNT_CODE) , accountCode);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (Account) list.get(0);	
		} 
		if (StringUtils.isBlank(description)) {
			description = "Cuenta: " + accountCode;
		}
		Account account = new Account();
		account.setCode(accountCode);
		account.setDescription(description);
		int l = 0;
		if (StringUtils.length(accountCode) == 9) {
			l = 4;
		} else if (StringUtils.length(accountCode) == 4) {
			l = 3;
		} if (StringUtils.length(accountCode) == 3) {
			l = 2;
		} if (StringUtils.length(accountCode) == 2) {
			l = 1;
		}
		if (l > 1) {
			String lowLevelAccount = StringUtils.substring(accountCode, 0,l); 
			ensureAccount(lowLevelAccount,description);
		}
		return (Account) bean.insert(account);
	}

	public Period getAccountPeriod(Date date) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Period.class);
		Criteria c = new Criteria();
		c.addGreaterThanOrEqualExpression(bean.getFieldName( IEntityAlias.PERIOD_INITIATION_DATE) , date);
		c.addLessThanOrEqualExpression(bean.getFieldName( IEntityAlias.PERIOD_DEADLINE) , date);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (Period) list.get(0);	
		} 
		return null;
	}
	
	public Account getOutputVatAccount() throws ManagerBeanException {
		if (outputVatAccount == null) {
			outputVatAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT);
		}
		return outputVatAccount;
	}

	public Account getInputVatAccount() throws ManagerBeanException {
		if (inputVatAccount == null) {
			inputVatAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);  
		}
		return inputVatAccount;
	}
	
	public Account getRetentionAccount() throws ManagerBeanException {
		if (retentionAccount == null) {
			retentionAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT);
		}
		return retentionAccount;
	}

	public Segment ensureSegment(String segmento) throws ManagerBeanException {
		IManagerBean bankBean = BeanManager.getManagerBean(Segment.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bankBean.getFieldName( IEntityAlias.SEGMENT_NAME) , segmento);
		List<ITransferObject> list = bankBean.getList(c);
		if (list != null && list.size() > 0) {
			return (Segment) list.get(0);	
		} 
		if (StringUtils.isNotBlank(segmento)) {
			Segment segment = new Segment();
			segment.setName(segmento);
			return (Segment) bankBean.insert(segment);
		}
		return null;
	}

	public Brand ensureBrand(String marca) throws ManagerBeanException {
		IManagerBean brandBean = BeanManager.getManagerBean(Brand.class);
		Criteria c = new Criteria();
		c.addEqualExpression(brandBean.getFieldName( IEntityAlias.BRAND_NAME) , marca);
		List<ITransferObject> list = brandBean.getList(c);
		if (list != null && list.size() > 0) {
			return (Brand) list.get(0);	
		} 
		if (StringUtils.isNotBlank(marca)) {
			Brand brand = new Brand();
			brand.setName(marca);
			return (Brand) brandBean.insert(brand);
		}
		return null;
	}

	public ProductCategory ensureProductCategory(String categoria) throws ManagerBeanException {
		IManagerBean brandBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria c = new Criteria();
		c.addEqualExpression(brandBean.getFieldName( IEntityAlias.PRODUCT_CATEGORY_NAME) , categoria);
		List<ITransferObject> list = brandBean.getList(c);
		if (list != null && list.size() > 0) {
			return (ProductCategory) list.get(0);	
		} 
		if (StringUtils.isNotBlank(categoria)) {
			ProductCategory pCategory = new ProductCategory();
			pCategory.setName(categoria);
			return (ProductCategory) brandBean.insert(pCategory);
		}
		return null;
	}

	public Tax ensureRetention(Double porcIva) throws ManagerBeanException {
		return ensureTax(porcIva,TaxType.RETENTION);
	}
	public Tax ensureVat(Double porcIva) throws ManagerBeanException {
		return ensureTax(porcIva,TaxType.VAT);
	}
	public Tax ensureTax(Double perc,TaxType type) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.TAX_TYPE) , type);
		c.addEqualExpression(bean.getFieldName( IEntityAlias.TAX_PERCENTAGE) , perc);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (Tax) list.get(0);	
		} 
		if (perc != null) {
			Tax tax = new Tax();
			tax.setType(type);
			tax.setPercentage(perc);
			tax.setName(type + " " + perc );
			return (Tax) bean.insert(tax);
		}
		return null;
	}
	
}
