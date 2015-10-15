package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Company;
import com.code.aon.config.BankAccount;
import com.code.aon.config.util.BankBic11;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedCompanyBank;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyBankLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(BAN,"descripcion"	,2,25	,false	,null)  // Descripción
		,new Column(BAN,"cuentaContable",2,9	,false	,null)  // Cuenta contable inmovilizado
		,new Column(BAN,"sufijo"	    ,2,3	,false	,null)  // Sufijo
		,new Column(BAN,"iban"			,2,34	,true	,null)  // IBAN completo
		,new Column(BAN,"bic"			,2,11	,false	,null)  // BIC
	};
	
	private ILoaderEngine engine;
	private Map<String, Column[]> columns;

	public CompanyBankLoaderFactory() {
	}
	
	public CompanyBankLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,BAN);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedCompanyBank.class);
	}

	@Override
	public String getKey() {
		return BAN;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedCompanyBank getTargetBean() {
		return new LoadedCompanyBank();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		
		// Obtener el registro de Company del dominio
		IManagerBean beanCompany = BeanManager.getManagerBean(Company.class);
		
		Criteria criteriaCompany = new Criteria();            
        criteriaCompany.addEqualExpression(beanCompany.getFieldName(IEntityAlias.COMPANY_DOMAIN), DomainManager.getCurrentDomain());
        List<ITransferObject> listCompany = beanCompany.getList(criteriaCompany);
        
        Company company = (Company) listCompany.get(0);
		
		LoadedCompanyBank loaded = (LoadedCompanyBank) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		
		// Si el registro ya existe (segun el iban) se actualiza, sino se añade
		Criteria criteria = new Criteria();            
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), company.getRegistry().getId());
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_BANK_ACCOUNT ), loaded.getIban() ); 
        
        List<ITransferObject> list = bean.getList(criteria);
        
        RegistryBank rb = null;
        if (list==null || list.size()==0) {
        	rb = new RegistryBank();        	
        }
        else {
        	rb = (RegistryBank) list.get(0);
        }
		rb.setRegistry(company.getRegistry());
		
		if (StringUtils.isNotBlank(loaded.getDescripcion()))
			rb.setAlias(loaded.getDescripcion());
		
		if (StringUtils.isNotBlank(loaded.getSufijo()))
			rb.setSufix(loaded.getSufijo());
		
		if (StringUtils.isNotBlank(loaded.getCuentaContable())) {
			LoaderUtils lu = new LoaderUtils();
			rb.setAccount( lu.ensureAccount(loaded.getCuentaContable(), loaded.getDescripcion()) );
		}
		
		rb.setBankAccount(ensureBankAccount(loaded.getIban(), engine));		
		rb.setBic(ensureBankBic(rb.getBankAccount(),loaded.getBic()));
		
		rb = (RegistryBank) bean.insertOrUpdate(rb);
		return rb.getId();		
	}
	
    // Devuelve el bic que se le pasa, si esta cumplimentado, o el bic correspondiente
    // al iban que se le pasa, si el bic esta vacio o es nulo
    private String ensureBankBic(BankAccount bankAccount, String bic) {
    	
    	if (StringUtils.isNotBlank(bic)) {
    		return bic;
    	}
    	else {
    		if (bankAccount != null) {
    			BankBic11 bicBank = BankBic11.getBankBic11(bankAccount.getBankCode());    			
    			return (bicBank==null?"":bicBank.getBic()); 
    		}
    		else return "";    		
    	}
    }
    
    // Se le pasa el IBAN completo y devuelve un objeto BankAccount con el contenido del IBAN
    private BankAccount ensureBankAccount(String iban, ILoaderEngine engine) {
    	
    	// Si esta vacio o es nulo, devuelve nulo
		if (StringUtils.isBlank(iban)) {
			return null;
		}
		
		// Crear el objeto y cumplimentar sus datos segun el iban que se le pasa (tambien podría ser un CCC)
		BankAccount bankAccount = new BankAccount();
		
		String ccc = StringUtils.replace(iban, ".", "");
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
				engine.log("IBAN incorrecto ("+iban+")");				
			}
		} else {
			bankAccount.setCountry(Country.ES);
			bankAccount.setBban1(StringUtils.substring(ccc, 0, 4));
			bankAccount.setBban2(StringUtils.substring(ccc, 4, 8));
			bankAccount.setBban3(StringUtils.substring(ccc, 8, 12));
			bankAccount.setBban4(StringUtils.substring(ccc, 12, 16));
			bankAccount.setBban5(StringUtils.substring(ccc, 16, 20));
			if (!bankAccount.isValidBban()) {
				engine.log("IBAN incorrecto ("+iban+")");
			}
			bankAccount.setCheck(bankAccount.calculateIbanControlDigit());
		}
		return bankAccount;
    }
	
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo)
			throws AonException {		
		return null;
	}
	
}
