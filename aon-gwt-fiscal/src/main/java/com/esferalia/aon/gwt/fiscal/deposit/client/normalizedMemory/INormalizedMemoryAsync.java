package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INormalizedMemoryAsync {

	void getAonData(String domainName, Integer domainId, String login, AsyncCallback<AonData> callback);

	void getSchema(AonData aonData, Company company, Integer year, Boolean textMode,
			AsyncCallback<Map<String, String>> callback);
	
	void getSchemaTextMode(AonData aonData, Integer id, AsyncCallback<Map<String, String>> callback);
	
	void getSchema(String cif, Integer domainId, Boolean textMode, Integer year, AsyncCallback<Map<String, String>> callback);

	void updateSchema(String cif, Integer domainId, String key, String value, Integer year,
			AsyncCallback<Void> callback);

	void isModify(String cif, Integer year, AsyncCallback<Boolean> callback);

	void clearSession(String cif, Integer year, AsyncCallback<Void> callback);

	void saveDeposit(AonData aonData, Map<String, String> deposit, Integer year, AsyncCallback<Void> callback);
	
	void saveDeposit(String cif, Integer domainId, D2Deposit d2Deposit, Boolean textMode, Integer year, AsyncCallback<Void> callback);

	void saveDeposit(String cif, Integer domainId, Boolean textMode, Integer year, AsyncCallback<Void> callback);

	void getDigitalDepositTemplates(Integer domainId, Integer year, AsyncCallback<Vector<MemoryTemplate>> callback);

	void createTextMemory(Integer domainId, String name, Integer year, AsyncCallback<MemoryTemplate> callback);

	
	void updateTexts(AonData aonData, MemoryTemplate mt, Integer domainId, String cif, Map<String, String> map, AsyncCallback<Map<String, String>> callback);
	
	void updateTexts(AonData aonData, MemoryTemplate mt, HashMap<D2DepositKey, Boolean> freeTextMap, Integer domainId, String cif,
			Map<String, String> map, AsyncCallback<Map<String, String>> callback);

	void getDate(String str, AsyncCallback<Date> callback);

	void importAll(AonData aonData, String type, String ejercicio, MemoryTemplate mt, String cif,
			Map<String, String> map, Integer year, AsyncCallback<Map<String, String>> callback);

	void delete(Integer domainId, String document, Integer year,
			AsyncCallback<Void> callback);

	void deleteFreeText(Integer domainId, Integer rattachId, Integer year, AsyncCallback<Void> callback);

	void calculate(AonData aonData, Map<String, String> map, Integer year, AsyncCallback<Map<String, String>> callback);

	void calculate(Map<String, String> map, Integer year, AsyncCallback<Map<String, String>> callback);

	void getMemoryFiles(AonData aonData, AsyncCallback<Vector<MemoryFiles>> callback);

	void deleteMemoryFile(AonData aonData, Integer id, AsyncCallback<Void> callback);

	void insertMemoryFile(AonData aonData, MemoryFiles mf, AsyncCallback<MemoryFiles> callback);

	void updateSchemaMemory(AonData aonData, Boolean bool, String key, Integer year, AsyncCallback<Void> callback);

	void isDigitalDeposit(Integer domainId, Integer year,
			AsyncCallback<Boolean> callback);

	void createD2Deposit(Integer domainId, Integer id, String name,
			String type, Integer year,
			AsyncCallback<Map<String, String>> callback);

	void getParentEnterprises(AonData aonData, String query,
			AsyncCallback<LinkedList<Enterprise>> callback);
	
	void getCompany(AonData aonData, AsyncCallback<Company> callback);

	void getDepositExercises(AonData aonData, AsyncCallback<String[]> callback);
	
	void getDepositExercises(Integer domainId, AsyncCallback<String[]> callback);

	void getDepositTemplates(AonData aonData, AsyncCallback<Vector<MemoryTemplate>> callback);
	
	// text mode

	void getTemplates(AonData aonData, AsyncCallback<Vector<MemoryTemplate>> callback);

	void saveDepositTextMode(AonData aonData, Map<String, String> deposit, Integer id, AsyncCallback<Void> callback);

	void createSchemaTextMode(AonData aonData, String name, AsyncCallback<Integer> callback);

	void deleteSchemaTextMode(AonData aonData, Integer id, AsyncCallback<Void> callback);
	
}
