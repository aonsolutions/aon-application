package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_deposit")
public interface INormalizedMemory extends RemoteService{

	public Map<String, String>  getSchema(String cif,String part,Integer domainId, Boolean textMode);
	
	public Integer initialize();
	
	public void updateSchema(String cif,Integer domainId, String key, String value);
	
	public Boolean isDigitalDeposit(Integer domainId);
	
	public Boolean isModify(String cif);
	
	public void clearSession(String cif);
	
	public void saveDeposit(String cif, Integer domainId, Boolean textMode);
	
	public Vector<MemoryTemplate> getDigitalDepositTemplates(Integer domainId);
	
	public MemoryTemplate createTextMemory(Integer domainId, String name);

	public void updateTexts(MemoryTemplate mt, Integer domainId, String cif);
	
	public Integer getParentDomain(Integer domainId);
	
	public Date getDate(String str);
	
	public void importSocietyValues(String document, Integer domainId);
	
	public void createD2Deposit(Integer domainId, Integer id, String name, String type);
	
	public void importAll(String type, String ejercicio,MemoryTemplate mt, Integer domainId, String cif);

	public void delete(Integer domainId, String document);

}
