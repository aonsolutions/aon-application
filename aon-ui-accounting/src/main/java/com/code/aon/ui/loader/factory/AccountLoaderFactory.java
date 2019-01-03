package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccount;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(PGC,"id"			,0,6	,false	,null)
		,new Column(PGC,"cuenta"		,2,9	,true	,null)
		,new Column(PGC,"descripcion"	,2,128	,true	,null)
		,new Column(PGC,"alias"			,2,328	,false  ,null)
	};
	
	private Map<String, Column[]> columns;
	private IManagerBean accountBean;
	private ILoaderEngine engine;
	
	public AccountLoaderFactory() {
	}
	public AccountLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	private IManagerBean getBean() throws ManagerBeanException {
		if (accountBean == null) {
			accountBean = BeanManager.getManagerBean(Account.class);
		}
		return accountBean;
	}
	
	@Override
	public boolean accept(String key) {
		if (StringUtils.equals(key,PGC)) {
			return true;	
		}
		return false;
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAccount.class);
	}

	@Override
	public String getKey() {
		return PGC;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAccount getTargetBean() {
		return new LoadedAccount();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		return getBean().get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		String codeAlias = getBean().getFieldName(IEntityAlias.ACCOUNT_CODE);
		LoadedAccount loaded = (LoadedAccount) loadedPojo;
		Account account = new Account();
		account.setDescription(loaded.getDescripcion());
		account.setAlias(loaded.getAlias());
		
		String code4 = AonStringUtils.length(loaded.getCuenta())>4?AonStringUtils.substring(loaded.getCuenta(),0,4):null;
		String code3 = AonStringUtils.length(loaded.getCuenta())>3?AonStringUtils.substring(loaded.getCuenta(),0,3):null;
		String code2 = AonStringUtils.length(loaded.getCuenta())>2?AonStringUtils.substring(loaded.getCuenta(),0,2):null;
		String code1 = AonStringUtils.length(loaded.getCuenta())>1?AonStringUtils.substring(loaded.getCuenta(),0,1):null;

		Criteria c = new Criteria();
		if (code4 != null) {
			c.addEqualExpression(codeAlias, code4);
			if (getBean().getCount(c) != 1) {
				Account account4 = new Account();
				account4.setCode(code4);
				account4.setDescription(loaded.getDescripcion());
				account4.setAlias(loaded.getAlias());
				getBean().insert(account4);			
				engine.log("Cuenta de nivel 4 creada ["+ account4.getFullDescription() +"]");
			}
		}
		if (code3 != null) {
			c = new Criteria();
			c.addEqualExpression(codeAlias, code3);
			if (getBean().getCount(c) != 1) {
				Account account3 = new Account();
				account3.setCode(code3);
				account3.setDescription(loaded.getDescripcion());
				account3.setAlias(loaded.getAlias());
				getBean().insert(account3);			
				engine.log("Cuenta de nivel 3 creada ["+ account3.getFullDescription() +"]");
			}
		}
		if (code2 != null) {
			c = new Criteria();
			c.addEqualExpression(codeAlias, code2);
			if (getBean().getCount(c) != 1) {
				Account account2 = new Account();
				account2.setCode(code2);
				account2.setDescription(loaded.getDescripcion());
				account2.setAlias(loaded.getAlias());
				getBean().insert(account2);			
				engine.log("Cuenta de nivel 2 creada ["+ account2.getFullDescription() +"]");
			}
		}
		if (code1 != null) {
			c = new Criteria();
			c.addEqualExpression(codeAlias, code1);
			if (getBean().getCount(c) != 1) {
				Account account1 = new Account();
				account1.setCode(code1);
				account1.setDescription(loaded.getDescripcion());
				account1.setAlias(loaded.getAlias());
				getBean().insert(account1);
				engine.log("Cuenta de nivel 1 creada ["+ account1.getFullDescription() +"]");
			}
		}
		
		account.setCode(loaded.getCuenta());
		c = new Criteria();
		c.addEqualExpression(codeAlias, loaded.getCuenta());
		List<ITransferObject> list = getBean().getList(c);
		if (list != null && list.size() >0 ) {
			account = (Account) list.get(0);
			if (params.isUpdateAccountDescription()) {
				int currentDomain = DomainManager.getCurrentDomain();
				if (account.getDomain() == currentDomain) {
					account.setDescription(loaded.getDescripcion());		
					account = (Account) getBean().update(account);
				} else {
					engine.log("La cuenta es de dominio superior, no se puede modificar ["+ account.getFullDescription() +"]");	
				}
			} else {
				engine.log("Cuenta ya existente, se ignora la del fichero ["+ account.getFullDescription() +"]");
			}
		} else {
			account = (Account) getBean().insert(account);
		}
		return account.getId();
	}
	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccount loaded = (LoadedAccount) loadedPojo;
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getBean().getFieldName(IEntityAlias.ACCOUNT_CODE), loaded.getCuenta());
		List<ITransferObject> list = getBean().getList(criteria); 
		if ( list.size() > 0 ) {
			return (Account) list.get(0);
		}
		return null;
	}
	
	@Override
	public void validate(LoaderParams params) throws AonException {
		
	}
	
}
