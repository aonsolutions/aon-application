package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.fiscal.shared.D2Deposit2014;
import com.esferalia.aon.gwt.fiscal.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INormalizedMemoryAsync {

	//void getSchema(String cif, String part, Integer domainId, Boolean textMode,
		//	AsyncCallback<Map<String, String>> callback);

	void initialize(AsyncCallback<Integer> callback);

	void updateSchema(String cif, Integer domainId, String key, String value,
			AsyncCallback<Void> callback);

	void isDigitalDeposit(Integer domainId, AsyncCallback<Boolean> callback);

	void isModify(String cif, AsyncCallback<Boolean> callback);

	void clearSession(String cif, AsyncCallback<Void> callback);

	void saveDeposit(String cif, Integer domainId, Boolean textMode,
			AsyncCallback<Void> callback);

	void getDigitalDepositTemplates(Integer domainId,
			AsyncCallback<Vector<MemoryTemplate>> callback);

	void createTextMemory(Integer domainId, String name,
			AsyncCallback<MemoryTemplate> callback);

	void updateTexts(MemoryTemplate mt, Integer domainId, String cif,
			Map<String, String> map, AsyncCallback<Map<String, String>> callback);

	void getParentDomain(Integer domainId, AsyncCallback<Integer> callback);

	void getDate(String str, AsyncCallback<Date> callback);

	void createD2Deposit(Integer domainId, Integer id, String name,
			String type, AsyncCallback<Map<String, String>> callback);

	void importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, Map<String, String> map,
			AsyncCallback<Map<String, String>> callback);

	void delete(Integer domainId, String document, AsyncCallback<Void> callback);

	void getSchema(String cif, Integer domainId, Boolean textMode,
			AsyncCallback<Map<String, String>> callback);

	void deleteFreeText(Integer domainId, Integer rattachId,
			AsyncCallback<Void> callback);

	void saveDeposit(String cif, Integer domainId, D2Deposit2014 d2Deposit2014,
			Boolean textMode, AsyncCallback<Void> callback);

	void calculate(Map<String, String> map,
			AsyncCallback<Map<String, String>> callback);

	void getMemoryFiles(Integer domainId, AsyncCallback<Vector<MemoryFiles>> callback);

	void deleteMemoryFile(Integer domainId, Integer id,
			AsyncCallback<Void> callback);

	void insertMemoryFile(Integer domainId, MemoryFiles mf,
			AsyncCallback<MemoryFiles> callback);

	void updateSchemaMemory(Boolean bool, Integer domainId, String key,
			AsyncCallback<Void> callback);

	void viewer(Integer domainId, MemoryFiles mf, AsyncCallback<String> callback);



}
