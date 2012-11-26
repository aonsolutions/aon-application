package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
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
import com.code.aon.ui.loader.pojo.LoadedAccountEntry;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(ASI,"id"			,0,6	,true	,null)
		,new Column(ASI,"diaro"			,0,6	,true	,null)
		,new Column(ASI,"fecha"			,3,10	,true	,null)
		,new Column(ASI,"comentario"	,2,256	,false	,null)
	};
	
	private Map<String, Column[]> columns;

	public AccountEntryLoaderFactory() {
	}
	public AccountEntryLoaderFactory(ILoaderEngine engine) {
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}
	
	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,ASI);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAccountEntry.class);
	}

	@Override
	public String getKey() {
		return ASI;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAccountEntry getTargetBean() {
		return new LoadedAccountEntry();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountEntry loaded = (LoadedAccountEntry) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
		AccountEntry entry = new AccountEntry();
		entry.setType(loaded.getEntryType() != null?loaded.getEntryType():AccountEntryType.MANUAL);
		entry.setJournal(loaded.getDiario());
		entry.setEntryDate(loaded.getFecha());
		entry.setAccountPeriod( params.getAccountPeriod() );
		entry.setComments(loaded.getComentario());
		entry.setSecurityLevel(params.getSecurityLevel());
		entry = (AccountEntry) bean.insert(entry);
		return entry.getId();
	}

	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountEntry loaded = (LoadedAccountEntry) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_JOURNAL), loaded.getDiario());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENTRY_DATE), loaded.getFecha());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			return (AccountEntry) list.get(0);
		}
		return null;
	}
	
}
