package com.code.aon.ui.loader.factory;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccountEntryDetail;
import com.code.aon.ui.loader.pojo.LoadedInvoiceDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(DET,"factura"			,0,6	,true	,null)
		,new Column(DET,"linea"				,0,6	,true	,null)
		,new Column(DET,"articulo"			,2,15	,true	,null)
		,new Column(DET,"concepto"			,2,64	,true	,null)
		,new Column(DET,"cantidad"			,1,16	,true	,null)
		,new Column(DET,"precio"			,1,16	,true	,null)
		,new Column(DET,"descuentos"		,2,16	,false	,null)
		,new Column(DET,"baseImponible"		,1,17	,true	,null)
		,new Column(DET,"porcentajeIva"		,1,6	,true	,null)
		,new Column(DET,"cuotaIva"			,1,16	,true	,null)
		,new Column(DET,"re"				,1,6	,true	,null)
		,new Column(DET,"cuotaRe"			,1,16	,false	,null)
		,new Column(DET,"porcentajeIrpf"	,1,6	,true	,null)
		,new Column(DET,"cuotaIrpf"			,1,17	,false	,null)
		,new Column(DET,"tipoDeduccionIva"	,0,1	,false	,new int[] {0,1,2})
		,new Column(DET,"tipoIrpf"			,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(DET,"cuenta"			,2,9	,false	,null)
		,new Column(DET,"cuentaIva"			,2,9	,false	,null)
		,new Column(DET,"cuentaIrpf"		,2,9	,false	,null)
	};
	
	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	private LoaderUtils loaderUtils;
	private AccountingUtil accountingUtil;
	
	public InvoiceDetailLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	private LoaderUtils getLoaderUtils() {
		if (loaderUtils == null) {
			loaderUtils = new LoaderUtils(); 
		}
		return loaderUtils;
	}

	public AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil(); 
		}
		return accountingUtil;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,DET);
	}
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedInvoiceDetail.class);
	}

	@Override
	public String getKey() {
		return DET;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedInvoiceDetail getTargetBean() {
		return new LoadedInvoiceDetail();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetail.class);
		return bean.get(id);
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		// TODO not needed yet
		return null;
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedInvoiceDetail loaded = (LoadedInvoiceDetail) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetail.class);
		InvoiceDetail detail = new InvoiceDetail();
		
		Invoice invoice = (Invoice) engine.getAonEntity(ILoaderFactory.FRA, loaded.getFactura().toString());
		if (invoice  == null) {
			throw new AonException("La factura con identificador " + loaded.getFactura() + " no existe.");
		}
		detail.setInvoice(invoice);
		detail.setLine(loaded.getLinea());
		if (StringUtils.isBlank(loaded.getArticulo())) {
			detail.setSource(InvoiceSource.ACCOUNT);	

			StringBuilder sb = new StringBuilder();
			sb.append("Fra. Nº: ");
			sb.append(invoice.getReferenceCode());
			sb.append(" del ");
			sb.append(params.getDateFormatter().format(invoice.getIssueDate()));
			detail.setDescription(sb.toString());
		} else {
			Item item = obtainItem( params, loaded, invoice );
			detail.setItem(item);
			if ( StringUtils.isEmpty( loaded.getConcepto() )) {
				detail.setDescription(StringUtils.join(new String[]{item.getProduct().getName(),item.getDescription()}," "));
			} else {
				detail.setDescription( loaded.getConcepto() ); 
			}
			detail.setSource(InvoiceSource.DIRECT_INVOICE);
		}
		detail.setPrice(loaded.getPrecio());
		if (StringUtils.isNotBlank(loaded.getDescuentos())) {
			detail.setDiscountExpression(new DiscountExpression(loaded.getDescuentos()));
		} else {
			detail.setDiscountExpression(new DiscountExpression("0.0"));
		}
		detail.setQuantity(loaded.getCantidad());
		detail.setTaxableBase(loaded.getBaseImponible());
		detail.setWorkPlace(params.getWorkPlace());
		// No actualiza la linea.
		detail.setUpdateEnabled(false);
		// No actualiza los totales. 
		detail.getInvoice().setUpdateEnabled(false);
		//
		detail = (InvoiceDetail) bean.insert(detail);

		if (detail.getSource() == InvoiceSource.ACCOUNT) {
			String prefix = (invoice.getType() == InvoiceType.SALES) ? "N/Fra" : "S/Fra";
			if (invoice.getTotal() < 0) {
				prefix += " " + "ABONO";
			}
			prefix += ": ";
			String concept = StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 32); 
			Account balancingAccount = getBalancingAccount(invoice);

			AccountEntry entry = getAccountEntry( invoice ); 
			LoadedAccountEntryDetail loadedAccountEntryDetail = loaded.getLoadedAccountEntryDetail(entry.getType());
			loadedAccountEntryDetail.setDocumento(invoice.getDocumentNumber());
			loadedAccountEntryDetail.setConcepto( concept );
			loadedAccountEntryDetail.setEntry(entry);
			if (balancingAccount != null) {
				loadedAccountEntryDetail.setContrapartida(balancingAccount.getCode());	
			}
			AccountEntryDetail aed = (AccountEntryDetail) engine.get(params, loadedAccountEntryDetail);
			if (aed == null) {
				Integer aedId = engine.insertAonEntity(params, loadedAccountEntryDetail);
				aed =  (AccountEntryDetail) engine.get(APU, aedId);	
			} else {
				aed = mergeAccountEntryDetail(aed,loadedAccountEntryDetail);
			}

			IManagerBean idaBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			InvoiceDetailAccount ida = new InvoiceDetailAccount();
			ida.setInvoiceDetail(detail);
			ida.setAccount(aed.getAccount());
			idaBean.insert(ida);
			
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			InvoiceTax invoiceTax = new InvoiceTax();
			invoiceTax.setInvoiceDetail(detail);
			invoiceTax.setPercentage(loaded.getPorcentajeIva());
			invoiceTax.setQuota(loaded.getCuotaIva());
			if (!invoice.isSurcharge() && loaded.getRe() != null && loaded.getRe() > 0) {
				invoice.setDefaultTaxInfo(false);
				invoice.setSurcharge(true);
			}
			invoiceTax.setSurcharge(loaded.getRe() != null?loaded.getRe():0.0);
			invoiceTax.setSurchargeQuota(loaded.getCuotaRe()!=null?loaded.getCuotaRe():0.0);
			invoiceTax.setTaxType(TaxType.VAT);
			invoiceTax.setVatDeductionType(loaded.getVatDeductionType());
			invoiceTax = (InvoiceTax) invoiceTaxBean.insert(invoiceTax);

			boolean ignoreTaxFree = !invoice.isSales() && (invoice.isIntracommunity() || invoice.isOtherISP());
			if (!invoice.isVatFree() || ignoreTaxFree) {
				LoadedAccountEntryDetail vatAccountEntryDetail = loaded.getVATLoadedAccountEntryDetail(entry.getType());
				vatAccountEntryDetail.setDocumento(invoice.getDocumentNumber());
				vatAccountEntryDetail.setEntry(entry);
				vatAccountEntryDetail.setConcepto(concept);
				Account vatAccount = null;
				if (StringUtils.isBlank(vatAccountEntryDetail.getCuenta())) {
					vatAccount = (entry.getType() == AccountEntryType.SALES_INVOICE)?getLoaderUtils().getOutputVatAccount():getLoaderUtils().getInputVatAccount();
					vatAccountEntryDetail.setCuenta( vatAccount.getCode() );
					vatAccountEntryDetail.setDescripcionCuenta( vatAccount.getDescription() );
				} else {
					vatAccount = getLoaderUtils().ensureAccount( vatAccountEntryDetail.getCuenta(), "IVA" );
				}
				if (balancingAccount != null) {
					vatAccountEntryDetail.setContrapartida(balancingAccount.getCode());	
				}
				AccountEntryDetail vatAed = (AccountEntryDetail) engine.get(params, vatAccountEntryDetail);
				if (vatAed == null) {
					if ( vatAccountEntryDetail.hasSaldo()) {
						engine.insertAonEntity(params, vatAccountEntryDetail);
						
						IManagerBean itaBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
						InvoiceTaxAccount ita = new InvoiceTaxAccount();
						ita.setInvoiceTax(invoiceTax);
						ita.setAccount(vatAccount);
						itaBean.insert(ita);
						
					}
				} else {
					vatAed = mergeAccountEntryDetail(vatAed,vatAccountEntryDetail);
				}
				if (ignoreTaxFree) {
					AccountEntryType type = entry.getType()==AccountEntryType.SALES_INVOICE?AccountEntryType.PURCHASE_INVOICE:AccountEntryType.SALES_INVOICE;
					LoadedAccountEntryDetail vatAccountEntryDetail2 = loaded.getVATLoadedAccountEntryDetail(type);
					vatAccountEntryDetail2.setDocumento(invoice.getDocumentNumber());
					vatAccountEntryDetail2.setEntry(entry);
					vatAccountEntryDetail2.setConcepto(concept);
					vatAccountEntryDetail2.setCuenta(null);
					if (StringUtils.isBlank(vatAccountEntryDetail2.getCuenta())) {
						Account acc = (type == AccountEntryType.SALES_INVOICE)?getLoaderUtils().getOutputVatAccount():getLoaderUtils().getInputVatAccount();
						vatAccountEntryDetail2.setCuenta( acc.getCode() );
						vatAccountEntryDetail2.setDescripcionCuenta( acc.getDescription() );
					}
					if (balancingAccount != null) {
						vatAccountEntryDetail2.setContrapartida(balancingAccount.getCode());	
					}
					AccountEntryDetail vatAed2 = (AccountEntryDetail) engine.get(params, vatAccountEntryDetail2);
					if (vatAed2 == null) {
						if ( vatAccountEntryDetail2.hasSaldo()) {
							engine.insertAonEntity(params, vatAccountEntryDetail2);	
						}
					} else {
						vatAed2 = mergeAccountEntryDetail(vatAed2,vatAccountEntryDetail2);
					}
				}
			}
			
			if (!invoice.isRetentionFree()) {
				if (loaded.getPorcentajeIrpf() != null && loaded.getPorcentajeIrpf() > 0) {
					if (!invoice.isWithholding()) {
						invoice.setDefaultTaxInfo(false);
						invoice.setWithholding(true);
					}
					invoiceTax = new InvoiceTax();
					invoiceTax.setInvoiceDetail(detail);
					invoiceTax.setPercentage(loaded.getPorcentajeIrpf());
					invoiceTax.setQuota(loaded.getCuotaIrpf());
					invoiceTax.setSurcharge(0);
					invoiceTax.setTaxType(TaxType.RETENTION);
					invoiceTax.setWithholdingType(loaded.getWithholdingType());
					invoiceTaxBean.insert(invoiceTax);


					LoadedAccountEntryDetail retentionAccountEntryDetail = loaded.getRetentionLoadedAccountEntryDetail(entry.getType());
					retentionAccountEntryDetail.setDocumento(invoice.getDocumentNumber());
					retentionAccountEntryDetail.setEntry(entry);
					retentionAccountEntryDetail.setConcepto(concept);
					if (balancingAccount != null) {
						retentionAccountEntryDetail.setContrapartida(balancingAccount.getCode());	
					}
					Account retentionAccount = null;
					if (StringUtils.isEmpty(retentionAccountEntryDetail.getCuenta())) {
						retentionAccount = getLoaderUtils().getRetentionAccount();
						if (retentionAccount == null) {
							throw new ManagerBeanException("No existe una cuenta de retención definida en los parámetros contables");
						}
						retentionAccountEntryDetail.setCuenta( retentionAccount.getCode() );
						retentionAccountEntryDetail.setDescripcionCuenta( retentionAccount.getDescription() );
					}
					AccountEntryDetail retentionAed = (AccountEntryDetail) engine.get(params, retentionAccountEntryDetail);
					if (retentionAed == null) {
						engine.insertAonEntity(params, retentionAccountEntryDetail);

						IManagerBean itaBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
						InvoiceTaxAccount ita = new InvoiceTaxAccount();
						ita.setInvoiceTax(invoiceTax);
						ita.setAccount(retentionAccount);
						itaBean.insert(ita);

					} else {
						retentionAed = mergeAccountEntryDetail(retentionAed,retentionAccountEntryDetail);
					}
				}
			}
		}
		return detail.getId();
	}	

	private Account getBalancingAccount(Invoice invoice) throws ManagerBeanException {
		if (invoice.getType() == InvoiceType.SALES) {
			return ((Customer)BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId())).getAccount();
		} else if (invoice.getType() == InvoiceType.PURCHASE) {
			return ((Supplier)BeanManager.getManagerBean(Supplier.class).get(invoice.getRegistry().getId())).getAccount();
		} else if (invoice.getType() == InvoiceType.EXPENSES || invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
			return ((Creditor)BeanManager.getManagerBean(Creditor.class).get(invoice.getRegistry().getId())).getAccount();
		}
		return null;
	}

	private AccountEntryDetail mergeAccountEntryDetail(AccountEntryDetail aed, LoadedAccountEntryDetail loadedAccountEntryDetail) throws ManagerBeanException {
		IManagerBean aedBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		double debit = (loadedAccountEntryDetail.getDebe() != null)?loadedAccountEntryDetail.getDebe():0.0;
		double credit = (loadedAccountEntryDetail.getHaber() != null)?loadedAccountEntryDetail.getHaber():0.0;
		double balance = CommonUtil.round(debit - credit + aed.getDebit() - aed.getCredit()); 
		if (balance > 0) {
			aed.setDebit(balance);
		} else {
			aed.setDebit(balance);
		}
		return (AccountEntryDetail) aedBean.update(aed);				
	}

	private Item obtainItem(LoaderParams params,LoadedInvoiceDetail loaded, Invoice invoice) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), loaded.getArticulo());
		List<ITransferObject> list = itemBean.getList(criteria);
		Item item = null;
		if (list.isEmpty()) {
			Product product = new Product();
			product.setCode(loaded.getArticulo());
			product.setName(loaded.getConcepto());
			product.setVat( obtainTax( params, TaxType.VAT, loaded ) );
			if (loaded.getPorcentajeIrpf() != null && loaded.getPorcentajeIrpf() != 0) {
				product.setRetention(obtainTax( params,  TaxType.RETENTION, loaded ) );
			}
			product.setCategory( params.getCategory() );
			product.setStatus(ProductStatus.ACTIVE);
			product.setType(ProductType.COMMERCIAL_PRODUCT);
			if ( StringUtils.isNotEmpty( loaded.getCuenta() ) ) {
				Account account = getLoaderUtils().ensureAccount(loaded.getCuenta(), loaded.getConcepto());
				if (invoice.isSales()) {
					product.setSalesAccount(account);	
				} else {
					product.setPurchaseAccount(account);
				}
			}
			item = new Item();
			item.setProduct(product);
			item.setPrice( loaded.getPrecio() );
			item.setStatus(ProductStatus.ACTIVE);
			item = (Item) itemBean.insert(item);
		} else {
			item = (Item) list.get(0);	
		}
		return item;
	}

	private Tax obtainTax(LoaderParams params,TaxType type, LoadedInvoiceDetail loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_TYPE), type);
		if (type == TaxType.VAT) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), loaded.getPorcentajeIva());
			if ( loaded.getRe() != null && loaded.getRe() != 0) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_SURCHARGE), loaded.getRe());	
			}
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), loaded.getPorcentajeIrpf());
		}
		List<ITransferObject> list = bean.getList(criteria);
		Tax tax = null;
		if (list.isEmpty()) {
			tax = new Tax();
			tax.setType(type);
			try {
				tax.setStartDate(params.getDateFormatter().parse("01/01/2000"));
			} catch (ParseException e) {
				/// nothing
			}
			tax.setPercentage((type == TaxType.VAT)?loaded.getPorcentajeIva():loaded.getPorcentajeIrpf() );
			tax.setName((type == TaxType.VAT)?"IVA " + loaded.getPorcentajeIva():"IRPF " + loaded.getPorcentajeIrpf() );
			tax.setSurcharge( (type == TaxType.VAT)?loaded.getRe(): 0.0 );
			tax.setVatDeductionType((type == TaxType.VAT)?loaded.getVatDeductionType(): null );
			tax.setWithholdingType((type == TaxType.VAT)?null:loaded.getWithholdingType());
			tax = (Tax) bean.insert(tax);
		} else {
			tax = (Tax) list.get(0);
		}
		return tax;
	}
	
	private AccountEntry getAccountEntry(Invoice invoice) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID) , invoice.getId());
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return ((AccountEntryInvoice) list.get(0)).getAccountEntry();	
		} 
		return null;
	}

	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
	
}
