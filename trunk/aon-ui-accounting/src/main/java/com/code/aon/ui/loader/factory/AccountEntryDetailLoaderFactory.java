package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedAccountEntryDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryDetailLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
		 new Column(APU,"id"						,0,6	,true	,null)
		,new Column(APU,"asiento"					,0,6	,true	,null)
		,new Column(APU,"fechaAsiento"				,3,10	,true	,null)
		,new Column(APU,"cuenta"					,2,9	,true	,null)
		,new Column(APU,"descripcionCuenta"			,2,256	,false	,null)
		,new Column(APU,"concepto"					,2,256	,true	,null)
		,new Column(APU,"documento"					,2,256	,false	,null)
		,new Column(APU,"debe"						,1,16	,true	,null)
		,new Column(APU,"haber"						,1,16	,true	,null)
		,new Column(APU,"contrapartida"				,2,9	,false	,null)
		,new Column(APU,"descripcionContrapartida"	,2,256	,false	,null)
	};
	
	private LoaderUtils loaderUtils;
	private Map<String, Column[]> columns;
	private ILoaderEngine engine;
	public AccountEntryDetailLoaderFactory() {
	}
	public AccountEntryDetailLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
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
		return StringUtils.equals(obj,APU);
	}

	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedAccountEntryDetail.class);
	}

	@Override
	public String getKey() {
		return APU;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedAccountEntryDetail getTargetBean() {
		return new LoadedAccountEntryDetail();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountEntryDetail loaded = (LoadedAccountEntryDetail) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
		AccountEntryDetail detail = new AccountEntryDetail();
		AccountEntry entry = null;
		if ( loaded.getEntry() != null) {
			entry = loaded.getEntry();	
		} else {
			entry = (AccountEntry) engine.ensureAonEntity(params, loaded.getLoadedAccountEntry());
		}
		detail.setAccountEntry(entry);
		Account account = getLoaderUtils().ensureAccount(loaded.getCuenta(), loaded.getDescripcionCuenta());
		detail.setAccount(account);
		detail.setConcept(loaded.getConcepto());
		detail.setDocumentNumber(loaded.getDocumento());
		if (loaded.getDebe() != null && loaded.getDebe() != 0.0) {
			detail.setDebit(loaded.getDebe());	
		}
		if (loaded.getHaber() != null && loaded.getHaber() != 0.0) {
			detail.setCredit(loaded.getHaber());
		}
		Account balancingAccount = null;
		if (StringUtils.isNotEmpty(loaded.getContrapartida())) {
			balancingAccount = getLoaderUtils().ensureAccount(loaded.getContrapartida(), loaded.getDescripcionContrapartida());	
		}
		detail.setBalancingAccount(balancingAccount);
		detail = (AccountEntryDetail) bean.insert(detail);
		return detail.getId();
	}

	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedAccountEntryDetail loaded = (LoadedAccountEntryDetail) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		if (loaded.getEntry() != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), loaded.getEntry().getId());	
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_CODE), loaded.getCuenta());
			List<ITransferObject> list = bean.getList(criteria); 
			if ( list.size() > 0 ) {
				return (AccountEntryDetail) list.get(0);
			}
		}
		return null;
	}
	
}
