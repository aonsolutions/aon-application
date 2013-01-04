package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccountInvoice;
import com.code.aon.ui.loader.pojo.LoadedInvoiceDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountInvoiceLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(FRA_CTB,"id"				,0,8	,true	,null)
		,new Column(FRA_CTB,"serie"				,2,5	,false	,null)
		,new Column(FRA_CTB,"numero"			,0,8	,false	,null)
		,new Column(FRA_CTB,"referencia"		,2,32	,false	,null)
		,new Column(FRA_CTB,"idTitular"			,0,6	,false	,null)
		,new Column(FRA_CTB,"cuenta"			,2,9	,false	,null)
		,new Column(FRA_CTB,"documento"			,2,16	,true	,null)
		,new Column(FRA_CTB,"tipoDocumento"		,0,1	,true	,new int[] {0,1,2,3,4,5})
		,new Column(FRA_CTB,"paisDocumento"		,2,2	,true	,null)
		,new Column(FRA_CTB,"razonSocial"		,2,128	,true	,null)
		,new Column(FRA_CTB,"fechaFactura"		,3,10	,true	,null)
		,new Column(FRA_CTB,"fechaIva"			,3,10	,false	,null)
		,new Column(FRA_CTB,"tipo"				,0,1	,true	,new int[] {0,1,2,3})
		,new Column(FRA_CTB,"inversion"			,0,1	,false	,new int[] {0,1})
		,new Column(FRA_CTB,"transaccion"		,0,1	,false	,new int[] {0,1,2,3})
		,new Column(FRA_CTB,"comentario"		,2,256	,false	,null)
		,new Column(FRA_CTB,"baseImponible1"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva1"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva1"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re1"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA1"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE1"			,1,17	,false	,null)
		,new Column(FRA_CTB,"baseImponible2"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva2"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva2"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re2"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA2"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE2"			,1,17	,false	,null)
		,new Column(FRA_CTB,"baseImponible3"	,1,17	,false	,null)
		,new Column(FRA_CTB,"iva3"				,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoDeduccionIva3"	,0,1	,false	,new int[] {0,1,2})
		,new Column(FRA_CTB,"re3"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIVA3"			,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaRE3"			,1,17	,false	,null)
		,new Column(FRA_CTB,"irpf"				,1,17	,false	,null)
		,new Column(FRA_CTB,"cuotaIRPF"			,1,17	,false	,null)
		,new Column(FRA_CTB,"tipoIrpf"			,0,1	,false	,new int[] {0,1,2,3,4})
		,new Column(FRA_CTB,"totalFactura"		,1,17	,false	,null)
		,new Column(FRA_CTB,"articulo"			,2,15	,false	,null)
		,new Column(FRA_CTB,"concepto"			,2,64	,false	,null)
		,new Column(FRA_CTB,"cuentaExplotacion"	,2,9	,false	,null)
		,new Column(FRA_CTB,"cuentaIva"			,2,9	,false	,null)
		,new Column(FRA_CTB,"cuentaIrpf"		,2,9	,false	,null)
		,new Column(FRA_CTB,"fechaVto"			,3,10	,false	,null)
		,new Column(FRA_CTB,"formaPago"			,2,32	,false	,null)
		,new Column(FRA_CTB,"cuentaBanco"		,2,23	,false	,null)
	};
	
	private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public AccountInvoiceLoaderFactory() {
	}
	public AccountInvoiceLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,FRA_CTB);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAccountInvoice.class);
	}

	@Override
	public String getKey() {
		return FRA_CTB;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAccountInvoice getTargetBean() {
		return new LoadedAccountInvoice();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		return insertInvoice(params,loadedPojo);
	}

	private Integer insertInvoice(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountInvoice loaded = (LoadedAccountInvoice) loadedPojo;

		Integer invoiceId = engine.insertAonEntity(params, loaded.getLoadedInvoice());
		for (LoadedInvoiceDetail detail : loaded.getLoadedInvoiceDetails()) {
			engine.insertAonEntity(params, detail);	
		}
		engine.insertAonEntity(params, loaded.getLoadedFinance());
		return invoiceId; 
		
/*		
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = new Invoice();
		InvoiceType type = InvoiceType.values()[loaded.getTipo()];
		if (type == InvoiceType.SALES ) {
			Series series = ensureInvoiceSeries(params,loaded.getSerie() ); 
			invoice.setSeries( series.getCode() );
			invoice.setNumber( loaded.getNumero() );
		} else {
			invoice.setReferenceCode(StringUtils.abbreviate(loaded.getReferencia(),16));
		}
		invoice.setType(type);
		Registry registry = obtainRegistry(params, type , loaded);
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(loaded.getDocumento());
		invoice.setRegistryDocumentType(DocumentType.values()[loaded.getTipoDocumento()]);
		invoice.setRegistryDocumentCountry(Country.valueOf( loaded.getPaisDocumento()));
		invoice.setRegistryName(loaded.getRazonSocial());
		invoice.setIssueDate(loaded.getFechaFactura());
		invoice.setTaxDate(loaded.getFechaIva());
		invoice.setInvestment( loaded.isInvestment() );
		invoice.setTransaction(loaded.getInvoiceTransactionType());
		invoice.setComments(loaded.getComentario());
		Date now = new Date();
		invoice.setRemarks("Importada de fichero " + params.getDateFormatter().format(now) + " - " + params.getTimeFormatter().format(now));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setTaxableBase(loaded.getTotalBaseImponible());
		invoice.setVatQuota(loaded.getTotalCuotaIVA());
		invoice.setRetentionQuota(loaded.getCuotaIRPF()==null?0:loaded.getCuotaIRPF());
		invoice.setTotal(loaded.getTotalFactura());
		invoice.setUpdateEnabled(false);
		invoice = (Invoice) bean.insert(invoice);
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			invoice.setStatus(InvoiceStatus.SCORED);
		}
		invoice = (Invoice) bean.insert(invoice);
		if (invoice.getStatus() == InvoiceStatus.SCORED) {
			// TODO Crear factory para apuntes.
			insertInvoiceAccountEntry(params,invoice, loaded);
		} 
		return invoice.getId();
*/		
	}
/*
	private Series ensureInvoiceSeries(LoaderParams params,String serie) throws ManagerBeanException {
		if (StringUtils.isNotBlank(serie)) {
			IManagerBean bean = BeanManager.getManagerBean(Series.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_CODE), serie);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), params.getScope().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if ( list.size() > 0 ) {
				Series series = (Series) list.get(0);
				return series;
			}
			Series series = new Series();
			series.setCode(serie);
			series.setDescription(serie);
			series.setInvoice(true);
			series.setActive(true);
			series.setScope( params.getScope() );
			series.setSecurityLevel( params.getSecurityLevel() );
			series = (Series) bean.insert(series);
			return series;
		}
		return null;
	}

	private void insertInvoiceAccountEntry(LoaderParams params,Invoice invoice,LoadedAccountInvoice loaded) throws AonException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);

		LoadedAccountEntry loadedAccountEntry = loaded.getLoadedAccountEntry();
		Integer entryId =  engine.insertAonEntity(params, loadedAccountEntry);
		AccountEntry entry = (AccountEntry) engine.get(ASI, entryId); 
		
		AccountEntryInvoice aei = new AccountEntryInvoice();
		aei.setAccountEntry(entry);
		aei.setInvoice(invoice);
		invoiceBean.insert(aei);
		
		String prefix = (invoice.getType() == InvoiceType.SALES) ? "N/Fra" : "S/Fra";
		if (invoice.getTotal() < 0) {
			prefix += " " + "ABONO";
		}
		prefix += ": ";
		String concept = StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 32); 
		
		LoadedAccountEntryDetail loadedAccountEntryDetail = loaded.getLoadedAccountEntryDetail();
		loadedAccountEntryDetail.setDocumento(invoice.getDocumentNumber());
		loadedAccountEntryDetail.setEntry( entry );
		loadedAccountEntryDetail.setConcepto( concept );
		engine.ensureAonEntity(params, loadedAccountEntryDetail);
	}

	private Registry obtainRegistry(LoaderParams params,InvoiceType type, LoadedAccountInvoice loaded) throws AonException {
		IRegistry r = null;
		if ( type == InvoiceType.SALES ) {
			LoadedCustomer loadedCustomer = loaded.getLoadedCustomer();
			Customer customer = (Customer) engine.ensureAonEntity(params,loadedCustomer);
			r = customer.getRegistry();
		} else if ( type == InvoiceType.PURCHASE ) {
			LoadedSupplier loadedSupplier = loaded.getLoadedSupplier();
			Supplier supplier = (Supplier) engine.ensureAonEntity(params,loadedSupplier);
			r = supplier.getRegistry();
		} else if ( type == InvoiceType.EXPENSES || type == InvoiceType.UNDEDUCTIBLE) {
			LoadedCreditor loadedCreditor= loaded.getLoadedCreditor();
			Creditor creditor = (Creditor) engine.ensureAonEntity(params,loadedCreditor);
			r = creditor.getRegistry();
		}
		return r.getRegistry();
	}
*/	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountInvoice loaded = (LoadedAccountInvoice) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_TYPE), loaded.getInvoiceType());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_SERIES), loaded.getSerie());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_NUMBER), loaded.getNumero());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), loaded.getFechaFactura());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			return (Invoice) list.get(0);
		}
		return null;
	}
}
