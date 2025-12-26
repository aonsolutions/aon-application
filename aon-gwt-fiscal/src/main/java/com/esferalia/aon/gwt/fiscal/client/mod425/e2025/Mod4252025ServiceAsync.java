package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod4252025ServiceAsync {

	void get(Occam occam, Mod390 mod425, AsyncCallback<Mod4252025> callback);
	void save(Occam occam, Mod4252025 mod425, AsyncCallback<Mod4252025> callback);
	void delete(Occam occam,Mod4252025 mod425, AsyncCallback<Void> callback);
	void changeStatus(Occam occam, Mod4252025 mod425, FiscalStatus status, AsyncCallback<Mod4252025> callback);

}
