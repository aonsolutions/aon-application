package net.aonsolutions.aon.gwt.commercial.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ICommercialAsync {

	void getAonData(String domainName, Integer domainId, AsyncCallback<AonData> callback);

}
