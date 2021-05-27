package com.esferalia.aon.gwt.dump.client;

import java.util.List;

import com.esferalia.aon.gwt.dump.shared.Parameters;
import com.esferalia.aon.gwt.dump.shared.Progress;
import com.esferalia.aon.gwt.dump.shared.Task;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ConnectServiceAsync {

	void getAvailableDomains(AsyncCallback<List<com.esferalia.aon.gwt.dump.shared.Domain>> callback);

	void dumpDomain(String domain, Parameters parameters, AsyncCallback<Task> callback);

	void process(Integer idTask, int i, AsyncCallback<Progress> asyncCallback);

	void cancelDownload(Integer idTask, AsyncCallback<Boolean> callback);

	void getTaskPending(AsyncCallback<List<Task>> callback);

	void eraseDownload(Integer idTask, AsyncCallback<Boolean> callback);

	void getDomainPermission(AsyncCallback<Integer> callback);

}
