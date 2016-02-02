package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.fiscal.deposit.shared.D2Deposit2014;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INormalizedMemoryAsync {

	//void getSchema(String cif, String part, Integer domainId, Boolean textMode,
		//	AsyncCallback<Map<String, String>> callback);

	void initialize(AsyncCallback<Integer> callback);

	void updateSchema(String cif, Integer domainId, String key, String value, Integer year,
			AsyncCallback<Void> callback);

	void isModify(String cif, Integer year, AsyncCallback<Boolean> callback);

	void clearSession(String cif, Integer year, AsyncCallback<Void> callback);

	void saveDeposit(String cif, Integer domainId, Boolean textMode, Integer year, AsyncCallback<Void> callback);

	void getDigitalDepositTemplates(Integer domainId, Integer year, AsyncCallback<Vector<MemoryTemplate>> callback);

	void createTextMemory(Integer domainId, String name,
			AsyncCallback<MemoryTemplate> callback);

	void updateTexts(MemoryTemplate mt, Integer domainId, String cif,
			Map<String, String> map, AsyncCallback<Map<String, String>> callback);

	void getParentDomain(Integer domainId, AsyncCallback<Integer> callback);

	void getDate(String str, AsyncCallback<Date> callback);

	void importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, Map<String, String> map,
			AsyncCallback<Map<String, String>> callback);

	void delete(Integer domainId, String document, Integer year,
			AsyncCallback<Void> callback);

	void getSchema(String cif, Integer domainId, Boolean textMode,
			Integer year, AsyncCallback<Map<String, String>> callback);

	void deleteFreeText(Integer domainId, Integer rattachId, Integer year, AsyncCallback<Void> callback);

	void saveDeposit(String cif, Integer domainId, D2Deposit2014 d2Deposit2014,
			Boolean textMode, Integer year, AsyncCallback<Void> callback);

	void calculate(Map<String, String> map,
			AsyncCallback<Map<String, String>> callback);

	void getMemoryFiles(Integer domainId, AsyncCallback<Vector<MemoryFiles>> callback);

	void deleteMemoryFile(Integer domainId, Integer id,
			AsyncCallback<Void> callback);

	void insertMemoryFile(Integer domainId, MemoryFiles mf,
			AsyncCallback<MemoryFiles> callback);

	void updateSchemaMemory(Boolean bool, Integer domainId, String key,
			Integer year, AsyncCallback<Void> callback);

	void viewer(Integer domainId, MemoryFiles mf, AsyncCallback<String> callback);

	void isDigitalDeposit(Integer domainId, Integer year,
			AsyncCallback<Boolean> callback);

	void createD2Deposit(Integer domainId, Integer id, String name,
			String type, Integer year,
			AsyncCallback<Map<String, String>> callback);

	void getParentEnterprises(String domainName, int domain, String query,
			AsyncCallback<ArrayList<Enterprise>> callback);

	void getDepositExercises(Integer domainId, AsyncCallback<String[]> callback);



}
