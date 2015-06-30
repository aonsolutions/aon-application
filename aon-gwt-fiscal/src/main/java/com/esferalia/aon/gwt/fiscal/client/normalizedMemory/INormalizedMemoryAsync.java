package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Date;
import java.util.Map;
import java.util.Vector;

import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INormalizedMemoryAsync {

	void getSchema(String cif, String part, Integer domainId, Boolean textMode,
			AsyncCallback<Map<String, String>> callback);

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
			AsyncCallback<Void> callback);

	void getParentDomain(Integer domainId, AsyncCallback<Integer> callback);

	void getDate(String str, AsyncCallback<Date> callback);

	void importSocietyValues(String document, Integer domainId,
			AsyncCallback<Void> callback);

	void createD2Deposit(Integer domainId, Integer id, String name, String type,
			AsyncCallback<Void> callback);

	void importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, AsyncCallback<Void> callback);

	void delete(Integer domainId, String document, AsyncCallback<Void> callback);



}
