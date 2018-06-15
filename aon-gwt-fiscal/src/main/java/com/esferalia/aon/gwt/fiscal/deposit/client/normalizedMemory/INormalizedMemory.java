package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_deposit")
public interface INormalizedMemory extends RemoteService{
	
	public Map<String, String> getSchema(AonData aonData, Company company, Integer year, Boolean textMode);
	
	public AonData getAonData(String domainName, Integer domainId, String login);

	public Map<String, String> getSchema(String cif, Integer domainId, Boolean textMode, Integer year);
	
	void updateSchema(String cif, Integer domainId, String key, String value, Integer year);
	
	public Map<String, String> reset(AonData aonData, Company company, Integer year);
	
	public Boolean isDigitalDeposit(Integer domainId, Integer year);
	
	public Boolean isModify(String cif, Integer year);
	
	public void clearSession(String cif, Integer year);
	
	public void saveDeposit(AonData aonData, Map<String, String> deposit, Integer year);
	
	public void saveDeposit(String cif, Integer domainId, Boolean textMode, Integer year);
	
	public void saveDeposit(String cif, Integer domainId, D2Deposit d2Deposit, Boolean textMode, Integer year);
	
	public Vector<MemoryTemplate> getDigitalDepositTemplates(Integer domainId, Integer year);
	
	public MemoryTemplate createTextMemory(Integer domainId, String name, Integer year);

	Map<String, String> updateTexts(AonData aonData, MemoryTemplate mt, Map<String, String> map);

	public Date getDate(String str);
			
	public Map<String, String> createD2Deposit(Integer domainId, Integer id, String name, String type, Integer year);
	
	Map<String, String> importAll(AonData aonData, String type, String ejercicio, MemoryTemplate mt, String cif,
			Map<String, String> map, Integer year);
	
	public void delete(AonData aonData, Integer year);

	public void deleteFreeText(Integer domainId, Integer rattachId, Integer year);
	
	public Map<String, String> calculate(AonData aonData, Map<String, String> map, Integer year);
	
	public Map<String, String> calculate(Map<String, String> map, Integer year);
	
	public Vector<MemoryFiles> getMemoryFiles(AonData aonData, Integer year);
	
	public void deleteMemoryFile(AonData aonData, Integer id);
	
	public MemoryFiles insertMemoryFile(AonData aonData, MemoryFiles mf);
	
	public void updateSchemaMemory(AonData aonData, Boolean bool, String key, Integer year);
	
	public LinkedList<Enterprise> getParentEnterprises(AonData aonData, String query);
	
	public Company getCompany(AonData aonData);
	
	public String[] getDepositExercises(AonData aonData);
	public String[] getDepositExercises(Integer domainId);

	public Vector<MemoryTemplate> getDepositTemplates(AonData aonData);
	
	public Map<String, String> updateType(AonData aonData, Integer year, String type);
	
	// text mode
	
	public Integer createSchemaTextMode(AonData aonData, String name);
	
	public void deleteSchemaTextMode(AonData aonData, Integer id);
	
	public Vector<MemoryTemplate> getTemplates(AonData aonData);

	public Map<String, String> getSchemaTextMode(AonData aonData, Integer id);
	
	public void saveDepositTextMode(AonData aonData, Map<String, String> deposit, Integer id);
}
