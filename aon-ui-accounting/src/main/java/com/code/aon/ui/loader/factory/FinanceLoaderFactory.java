package com.code.aon.ui.loader.factory;

import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.BankAccount;
import com.code.aon.config.util.BankUtil;
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
		,new Column(VTO,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5,6})
		,new Column(VTO,"paisDocumento"					,2,2	,true	,null)
		,new Column(VTO,"documento"						,2,16	,true	,null)
		,new Column(VTO,"razonSocial"					,2,64	,true	,null)
		,new Column(VTO,"importe"						,1,10	,true	,null)
		,new Column(VTO,"concepto"						,2,64	,false	,null)
		,new Column(VTO,"fechaVto"						,3,32	,true	,null)
		,new Column(VTO,"formaPago"						,2,32	,false	,null)
		,new Column(VTO,"cuentaBanco"					,2,34	,false	,null)
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
			if (StringUtils.startsWith(cuenta, "430")) {
				LoadedCustomer loadedCustomer = loaded.getLoadedCustomer();
				Customer customer = (Customer) engine.ensureAonEntity(params, loadedCustomer);
				registry = customer.getRegistry();
			}
			if (StringUtils.startsWith(cuenta, "410")) {
				LoadedCreditor loadedCreditor = loaded.getLoadedCreditor();
				Creditor creditor = (Creditor) engine.ensureAonEntity(params, loadedCreditor);
				registry = creditor.getRegistry();
			}
			if (StringUtils.startsWith(cuenta, "400")) {
				LoadedSupplier loadedSupplier = loaded.getLoadedSupplier();
				Supplier supplier = (Supplier) engine.ensureAonEntity(params, loadedSupplier);
				registry = supplier.getRegistry();
			}
			if (registry == null) {
				throw new ManagerBeanException("No se ha encontrado cliente/proveedor/acreedor para este vto. Registry es nulo!");
			}
		}
		finance.setDueDate(loaded.getFechaVto());
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
			String ccc = StringUtils.replace(loaded.getCuentaBanco(), ".", "");
			String country = StringUtils.substring(ccc, 0, 2);
			if (!StringUtils.isNumeric(country)) {
				bankAccount.setCountry(Country.valueOf(country));
				bankAccount.setCheck(StringUtils.substring(ccc, 2, 4));
				bankAccount.setBban1(StringUtils.substring(ccc, 4, 8));
				bankAccount.setBban2(StringUtils.substring(ccc, 8, 12));
				bankAccount.setBban3(StringUtils.substring(ccc, 12, 16));
				bankAccount.setBban4(StringUtils.substring(ccc, 16, 20));
				bankAccount.setBban5(StringUtils.substring(ccc, 20, 24));
				bankAccount.setBban6(StringUtils.substring(ccc, 24, 28));
				bankAccount.setBban7(StringUtils.substring(ccc, 28, 32));
				bankAccount.setBban8(StringUtils.substring(ccc, 32, 34));
				if (!bankAccount.isValidIban()) {
					engine.log("WARNING. IBAN de " + loaded.getRazonSocial() + " incorrecto ("+loaded.getCuentaBanco()+")");
					bankAccount = null;
				}
			} else {
				bankAccount.setCountry(Country.ES);
				bankAccount.setBban1(StringUtils.substring(ccc, 0, 4));
				bankAccount.setBban2(StringUtils.substring(ccc, 4, 8));
				bankAccount.setBban3(StringUtils.substring(ccc, 8, 12));
				bankAccount.setBban4(StringUtils.substring(ccc, 12, 16));
				bankAccount.setBban5(StringUtils.substring(ccc, 16, 20));
				if (!bankAccount.isValidBban()) {
					engine.log("WARNING. CCC de "+ loaded.getRazonSocial() + " incorrecto ("+loaded.getCuentaBanco()+")");
					bankAccount = null;
				} else {
					bankAccount.setCheck(bankAccount.calculateIbanControlDigit());
				}
			}

			if (bankAccount != null) {
				finance.setBankAccount(bankAccount);
				BankUtil.fillBankAccountData(finance);
			}
		}
		finance.setPayment(loaded.getPago()==1);
		finance = (Finance) bean.insert(finance);
		return finance.getId();
	}
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
}
