package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Company;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INormalizedMemoryAsync {

	void getAonData(String domainName, Integer domainId, String login, AsyncCallback<AonData> callback);

	void getSchema(AonData aonData, Company company, Integer year, Boolean textMode,
			AsyncCallback<Map<String, String>> callback);
	
	void getSchemaTextMode(AonData aonData, Integer id, AsyncCallback<Map<String, String>> callback);

	void saveDeposit(AonData aonData, Map<String, String> deposit, Integer year, AsyncCallback<Void> callback);
		
	void updateTexts(AonData aonData, MemoryTemplate mt, Map<String, String> map, AsyncCallback<Map<String, String>> callback);

	void getDate(String str, AsyncCallback<Date> callback);

	void importAll(AonData aonData, String type, String ejercicio, MemoryTemplate mt, String cif,
			Map<String, String> map, Integer year, AsyncCallback<Map<String, String>> callback);

	void calculate(AonData aonData, Map<String, String> map, Integer year, AsyncCallback<Map<String, String>> callback);

	void getMemoryFiles(AonData aonData, Integer year, AsyncCallback<Vector<MemoryFiles>> callback);

	void deleteMemoryFile(AonData aonData, Integer id, AsyncCallback<Void> callback);

	void updateSchemaMemory(AonData aonData, Boolean bool, String key, Integer year, AsyncCallback<Void> callback);

	
	void getCompany(AonData aonData, AsyncCallback<Company> callback);

	void getDepositExercises(AonData aonData, AsyncCallback<String[]> callback);
	
	void getDepositTemplates(AonData aonData, AsyncCallback<Vector<MemoryTemplate>> callback);
	
	void updateType(AonData aonData, Integer year, String type, AsyncCallback<Map<String, String>> callback);

	// text mode

	void getTemplates(AonData aonData, AsyncCallback<Vector<MemoryTemplate>> callback);

	void saveDepositTextMode(AonData aonData, Map<String, String> deposit, Integer id, AsyncCallback<Void> callback);

	void createSchemaTextMode(AonData aonData, String name, AsyncCallback<Integer> callback);

	void deleteSchemaTextMode(AonData aonData, Integer id, AsyncCallback<Void> callback);

	void reset(AonData aonData, Company company, Integer year, AsyncCallback<Map<String, String>> callback);

	
}
