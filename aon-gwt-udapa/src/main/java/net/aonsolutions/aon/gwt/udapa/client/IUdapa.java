package net.aonsolutions.aon.gwt.udapa.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

@RemoteServiceRelativePath("gwt_udapa")
public interface IUdapa extends RemoteService{
	public HashMap<String, String> getValues();
	public HashMap<String, String> updateValue(QualitySheetCode code, String value, HashMap<String, String> map);
}
