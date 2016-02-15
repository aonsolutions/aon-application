package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.fiscal.deposit.shared.D2Deposit2014;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_deposit")
public interface INormalizedMemory extends RemoteService{

	//public Map<String, String>  getSchema(String cif,String part,Integer domainId, Boolean textMode);
	
	public Map<String, String> getSchema(String cif,
			Integer domainId, Boolean textMode, Integer year);
	
	public Integer initialize();
	
	void updateSchema(String cif, Integer domainId, String key, String value, Integer year);
	
	public Boolean isDigitalDeposit(Integer domainId, Integer year);
	
	public Boolean isModify(String cif, Integer year);
	
	public void clearSession(String cif, Integer year);
	
	public void saveDeposit(String cif, Integer domainId, Boolean textMode, Integer year);
	
	public void saveDeposit(String cif, Integer domainId, D2Deposit2014 d2Deposit2014, Boolean textMode, Integer year);
	
	public Vector<MemoryTemplate> getDigitalDepositTemplates(Integer domainId, Integer year);
	
	public MemoryTemplate createTextMemory(Integer domainId, String name);

	public Map<String, String> updateTexts(MemoryTemplate mt, Integer domainId, String cif, Map<String, String> map);
	
	public Integer getParentDomain(Integer domainId);
	
	public Date getDate(String str);
			
	public Map<String, String> createD2Deposit(Integer domainId, Integer id, String name, String type, Integer year);
	
	public Map<String, String> importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, Map<String, String> map);
	
	public void delete(Integer domainId, String document, Integer year);

	public void deleteFreeText(Integer domainId, Integer rattachId, Integer year);
	
	public Map<String, String> calculate(Map<String, String> map);
	
	public Vector<MemoryFiles> getMemoryFiles(Integer domainId);
	
	public void deleteMemoryFile(Integer domainId, Integer id);
	
	public MemoryFiles insertMemoryFile(Integer domainId, MemoryFiles mf);
	
	public void updateSchemaMemory(Boolean bool, Integer domainId, String key, Integer year);
	
	public LinkedList<Enterprise> getParentEnterprises(String domainName, int domain, String query);
	
	public String[] getDepositExercises(Integer domainId);
}
