package com.esferalia.aon.gwt.dump.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.dump.shared.Domain;
import com.esferalia.aon.gwt.dump.shared.Progress;
import com.esferalia.aon.gwt.dump.shared.Task;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ConnectServiceAsyncDecorator implements ConnectServiceAsync {

	private ConnectServiceAsync csa;

	public ConnectServiceAsyncDecorator(ConnectServiceAsync connectServiceAsync) {
		this.csa = connectServiceAsync;
	}

	@Override
	public void getAvailableDomains(AsyncCallback<List<Domain>> callback) {
		AON.start();
		csa.getAvailableDomains(new AsyncCallbackWrapper<List<Domain>>(callback));
	}
	
	public void dumpDomain (String domain, AsyncCallback<Task> callback){
		AON.start();
		csa.dumpDomain(domain,
				new AsyncCallbackWrapper<Task>(callback));
	}

	@Override
	public void process(Integer idTask, int i, AsyncCallback<Progress> callback) {
		AON.start();
		csa.process(idTask, i,
				new AsyncCallbackWrapper<Progress>(callback));
		
	}

	@Override
	public void cancelDownload(Integer idTask, AsyncCallback<Boolean> callback) {
		AON.start();
		csa.cancelDownload(idTask,
				new AsyncCallbackWrapper<Boolean>(callback));
		
	}

	@Override
	public void getTaskPending(AsyncCallback<List<Task>> callback) {
		AON.start();
		csa.getTaskPending(new AsyncCallbackWrapper<List<Task>>(callback));
		
	}

	@Override
	public void eraseDownload(Integer idTask, AsyncCallback<Boolean> callback) {
		AON.start();
		csa.eraseDownload(idTask, new AsyncCallbackWrapper<Boolean>(callback));
		
	}

}
