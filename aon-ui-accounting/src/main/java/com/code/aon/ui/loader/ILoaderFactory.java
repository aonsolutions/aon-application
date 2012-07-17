package com.code.aon.ui.loader;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.ui.loader.pojo.ILoadedPojo;

public interface ILoaderFactory<E extends ILoadedPojo> {
	
	public boolean accept( String key);

	public String getKey();
	public Column[] getSupportedColumns();
	
	public E getTargetBean();
	public Integer insert(LoaderParams params,E loaded) throws AonException;
	
	public ITransferObject get(Integer id) throws AonException; 

}
