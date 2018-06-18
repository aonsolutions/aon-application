package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwt_deposit")
public interface INormalizedMemory extends RemoteService{
	
	public Map<String, String> getSchema(AonData aonData, Company company, Integer year, Boolean textMode);
	
	public AonData getAonData(String domainName, Integer domainId, String login);
		
	public Map<String, String> reset(AonData aonData, Company company, Integer year);
	
	public void saveDeposit(AonData aonData, Map<String, String> deposit, Integer year);
	
	Map<String, String> updateTexts(AonData aonData, MemoryTemplate mt, Map<String, String> map);

	public Date getDate(String str);
				
	Map<String, String> importAll(AonData aonData, String type, String ejercicio, MemoryTemplate mt, String cif,
			Map<String, String> map, Integer year);
		
	public Map<String, String> calculate(AonData aonData, Map<String, String> map, Integer year);
		
	public Vector<MemoryFiles> getMemoryFiles(AonData aonData, Integer year);
	
	public void deleteMemoryFile(AonData aonData, Integer id);
		
	public void updateSchemaMemory(AonData aonData, Boolean bool, String key, Integer year);
	
	
	public Company getCompany(AonData aonData);
	
	public String[] getDepositExercises(AonData aonData);

	public Vector<MemoryTemplate> getDepositTemplates(AonData aonData);
	
	public Map<String, String> updateType(AonData aonData, Integer year, String type);
	
	// text mode
	
	public Integer createSchemaTextMode(AonData aonData, String name);
	
	public void deleteSchemaTextMode(AonData aonData, Integer id);
	
	public Vector<MemoryTemplate> getTemplates(AonData aonData);

	public Map<String, String> getSchemaTextMode(AonData aonData, Integer id);
	
	public void saveDepositTextMode(AonData aonData, Map<String, String> deposit, Integer id);
}
