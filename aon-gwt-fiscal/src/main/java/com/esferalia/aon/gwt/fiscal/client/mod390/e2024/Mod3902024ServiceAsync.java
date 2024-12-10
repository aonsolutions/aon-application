package com.esferalia.aon.gwt.fiscal.client.mod390.e2024;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Mod3902024ServiceAsync {

	void get(Occam occam,Mod390 mod390, AsyncCallback<Mod3902024> callback);
	void save(Occam occam,Mod3902024 mod390, AsyncCallback<Mod3902024> callback);
	void delete(Occam occam,Mod3902024 mod390, AsyncCallback<Void> callback);
	void changeStatus(Occam occam, Mod3902024 mod390, FiscalStatus status, AsyncCallback<Mod3902024> callback);

}
