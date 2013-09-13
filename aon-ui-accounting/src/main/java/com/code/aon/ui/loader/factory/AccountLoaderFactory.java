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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccount;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(PGC,"id"			,0,6	,false	,null)
		,new Column(PGC,"cuenta"		,2,9	,true	,null)
		,new Column(PGC,"descripcion"	,2,128	,true	,null)
		,new Column(PGC,"alias"			,2,328	,false  ,null)
	};
	
	private Map<String, Column[]> columns;

	public AccountLoaderFactory() {
	}
	public AccountLoaderFactory(ILoaderEngine engine) {
	}

	public Map<String, Column[]> getColumns() {
		return columns;
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
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAccount loaded = (LoadedAccount) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Account account = new Account();
		account.setCode(loaded.getCuenta());
		account.setDescription(loaded.getDescripcion());
		account.setAlias(loaded.getAlias());
		account = (Account) bean.insert(account);
		return account.getId();
	}

	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccount loaded = (LoadedAccount) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), loaded.getCuenta());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			return (Account) list.get(0);
		}
		return null;
	}
	
	@Override
	public void validate(LoaderParams params) throws AonException {
		
	}
	
}
