package net.aonsolutions.aon.gwt.udapa.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

public interface IUdapaAsync {
	void getValues(String domainName, Integer domainId, Integer drId, AsyncCallback<HashMap<String, String>> callback);
	void updateValue(String domainName, Integer domainId, Integer drId, QualitySheetCode code, String value, HashMap<String, String> map, AsyncCallback<HashMap<String, String>> callback);
}