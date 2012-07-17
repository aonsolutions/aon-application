package com.code.aon.ui.loader;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;


public interface ILoaderIdCache {
	
	public void cacheId(String key, String identifier, Integer id);
	public Integer getAonId(String key, String identifier);
	public ITransferObject getAonEntity(String key, String identifier) throws AonException;

}
