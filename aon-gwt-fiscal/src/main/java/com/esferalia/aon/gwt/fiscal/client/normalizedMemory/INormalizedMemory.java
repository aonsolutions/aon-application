package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_deposit")
public interface INormalizedMemory extends RemoteService{

	public Map<String, String>  getSchema(String part,Integer domainId);
	
	public Integer initialize();
	
	public void updateSchema(Integer domainId, String key, String value);
	
	public Boolean isDigitalDeposit(Integer domainId);
	
	public Boolean isModify(String cif);
	
	public void clearSession(String cif);
	
	public void saveDeposit(String cif, Integer domainId);
}
