package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCreditor;
import com.code.aon.ui.loader.pojo.LoadedCustomer;
import com.code.aon.ui.loader.pojo.LoadedFinance;
import com.code.aon.ui.loader.pojo.LoadedSupplier;

public class FinanceLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(VTO,"id"							,0,6	,true	,null)
		,new Column(VTO,"factura"						,0,6	,false	,null)
		,new Column(VTO,"pago"							,0,1	,true	,new int[] {0,1})
		,new Column(VTO,"idTitular"						,0,6	,false	,null)
		,new Column(VTO,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
		,new Column(VTO,"paisDocumento"					,2,2	,true	,null)
		,new Column(VTO,"documento"						,2,16	,true	,null)
		,new Column(VTO,"razonSocial"					,2,64	,true	,null)
		,new Column(VTO,"importe"						,1,10	,true	,null)
		,new Column(VTO,"concepto"						,2,64	,false	,null)
		,new Column(VTO,"fechaVto"						,3,32	,true	,null)
		,new Column(VTO,"formaPago"						,2,32	,false	,null)
		,new Column(VTO,"cuentaBanco"					,2,23	,false	,null)
		,new Column(VTO,"cuenta"						,2,9	,false	,null)
	};

	private Map<String, Column[]> columns;
	private LoaderUtils loaderUtils;
	
	private ILoaderEngine engine;
	
	public FinanceLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}
	public FinanceLoaderFactory() {
	}

	private LoaderUtils getLoaderUtils() {
		if (loaderUtils == null) {
			loaderUtils = new LoaderUtils();
		}
		return loaderUtils;
	}
	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,VTO);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedFinance.class);
	}

	@Override
	public String getKey() {
		return VTO;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedFinance getTargetBean() {
		return new LoadedFinance();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		return bean.get(id);
	}
	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		// TODO not needed yet
		return null;
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedFinance loaded = (LoadedFinance) loadedPojo;
		if (loaded.getImporte() != 0) {
			return insertFinance(engine,params,loaded);	
		}
		return null;
	}

	public Integer insertFinance(ILoaderEngine engine,LoaderParams params,LoadedFinance loaded) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Finance finance  = new Finance();
		Registry registry = null;
		if (loaded.getFactura() != null) {
			Invoice invoice = (Invoice) engine.getAonEntity(ILoaderFactory.FRA, loaded.getFactura().toString());
			if (invoice  == null) {
				throw new AonException("La factura con identiicador " + loaded.getFactura() + " no existe.");
			}
			finance.setInvoice(invoice);
			registry = invoice.getRegistry();
			if (StringUtils.isBlank(loaded.getConcepto())) {
				loaded.setConcepto(invoice.getDocumentNumber());
			}
		} else {
			String cuenta = loaded.getCuenta();
			if (StringUtils.startsWith(cuenta, "400")) {
				LoadedCustomer loadedCustomer = loaded.getLoadedCustomer();
				Customer customer = (Customer) engine.ensureAonEntity(params, loadedCustomer);
				registry = customer.getRegistry();
			}
			if (StringUtils.startsWith(cuenta, "410")) {
				LoadedCreditor loadedCreditor = loaded.getLoadedCreditor();
				Creditor creditor = (Creditor) engine.ensureAonEntity(params, loadedCreditor);
				registry = creditor.getRegistry();
			}
			if (StringUtils.startsWith(cuenta, "430")) {
				LoadedSupplier loadedSupplier = loaded.getLoadedSupplier();
				Supplier supplier = (Supplier) engine.ensureAonEntity(params, loadedSupplier);
				registry = supplier.getRegistry();
			}
			if (registry == null) {
				throw new ManagerBeanException("Registry es nulo!");
			}
		}
		finance.setRegistry(registry);
		finance.setScope(params.getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		if (loaded.getTipoDocumento() == null) {
			finance.setRegistryDocumentType(finance.getRegistry().getDocumentType());
		} else {
			finance.setRegistryDocumentType(loaded.getDocumentType());
		}
		if (StringUtils.isBlank(loaded.getPaisDocumento())) {
			finance.setRegistryDocumentCountry(finance.getRegistry().getDocumentCountry());
		} else {
			finance.setRegistryDocumentCountry(loaded.getDocumentCountry());
		}
		if (StringUtils.isBlank(loaded.getDocumento())) {
			finance.setRegistryDocument(finance.getRegistry().getDocument());
		} else {
			finance.setRegistryDocument(loaded.getDocumento());
		}
		if (StringUtils.isBlank(loaded.getDocumento())) {
			finance.setRegistryName(finance.getRegistry().getName());
		} else {
			finance.setRegistryName(loaded.getRazonSocial());
		}
		finance.setAmount(loaded.getImporte());
		finance.setConcept(loaded.getConcepto());
		if (StringUtils.isNotBlank(loaded.getFormaPago())) {
			finance.setPayMethod(getLoaderUtils().ensurePayMethod(loaded.getFormaPago()));	
		}
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			BankAccount bankAccount = new BankAccount();
			String[] ccc = StringUtils.split(loaded.getCuentaBanco(),".");
			if (ccc.length != 4) {
				throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
			}
			bankAccount.setEntity(ccc[0]);
			bankAccount.setOffice(ccc[1]);
			bankAccount.setControl(ccc[2]);
			bankAccount.setAccount(ccc[3]);
			if (bankAccount.isValid()) {
				finance.setBankAccount(bankAccount);
				Bank bank = getLoaderUtils().ensureBank(ccc[0],null );
				finance.setBank(bank);
			} else {
				engine.log("WARNING. La cuenta de banco " + bankAccount.toString() + " no es válida.");
			}
		}
		finance.setPayment(loaded.getPago()==1);
		finance = (Finance) bean.insert(finance);
		return finance.getId();
	}
}
