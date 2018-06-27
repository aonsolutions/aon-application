package net.aonsolutions.aon.gwt.udapa.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IUdapaAsync {

	void getValues(String domainName, Integer domainId, Integer drId, AsyncCallback<HashMap<String, String>> callback);
	
	void getPaturpatValues(String domainName, Integer domainId, Integer drId, AsyncCallback<HashMap<String, String>> callback);
	
	void updateValue(String domainName, Integer domainId, Integer drId, String code, String value, HashMap<String, String> map, AsyncCallback<HashMap<String, String>> callback);

	void deleteQuality(String domainName, Integer domainId, Integer drId, AsyncCallback<Void> callback);
	
	void updateIncomeDetail(String domainName, Integer domainId, Double price, Double quantity, Integer incomeDetailId, AsyncCallback<Void> callback);
}