package net.aonsolutions.aon.gwt.udapa.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ms/gwt_udapa")
public interface IUdapa extends RemoteService{
	
	public HashMap<String, String> getValues(String domainName, Integer domainId, Integer drId);

	public HashMap<String, String> getPaturpatValues(String domainName, Integer domainId, Integer drId);
	
	public HashMap<String, String> updateValue(String domainName, Integer domainId, Integer drId, String code, String value, HashMap<String, String> map);
	
	public void deleteQuality(String domainName, Integer domainId, Integer drId);
	
	public void updateIncomeDetail(String domainName, Integer domainId, Double price, Double quantity, Integer incomeDetailId);
}
