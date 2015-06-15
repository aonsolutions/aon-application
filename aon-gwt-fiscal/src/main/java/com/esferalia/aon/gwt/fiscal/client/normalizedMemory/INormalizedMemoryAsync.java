package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface INormalizedMemoryAsync {

	void getSchema(String part, Integer domainId,
			AsyncCallback<Map<String, String>> callback);

	void initialize(AsyncCallback<Integer> callback);

	void updateSchema(Integer domainId, String key, String value,
			AsyncCallback<Void> callback);

	void isDigitalDeposit(Integer domainId, AsyncCallback<Boolean> callback);

	void isModify(String cif, AsyncCallback<Boolean> callback);

	void clearSession(String cif, AsyncCallback<Void> callback);

	void saveDeposit(String cif, Integer domainId, AsyncCallback<Void> callback);

}
