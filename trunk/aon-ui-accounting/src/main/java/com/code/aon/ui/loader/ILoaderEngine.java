package com.code.aon.ui.loader;


import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.ui.loader.pojo.ILoadedPojo;


public interface ILoaderEngine {
	
	public ITransferObject get(String key, Integer aonId) throws AonException;
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException;
	public ITransferObject getAonEntity(String key, String identifier) throws AonException;

	public Integer insertAonEntity(LoaderParams params, ILoadedPojo loadedPojo) throws AonException;
	public ITransferObject ensureAonEntity(LoaderParams params, ILoadedPojo loadedPojo) throws AonException;

	public void log( String msg  );
}
