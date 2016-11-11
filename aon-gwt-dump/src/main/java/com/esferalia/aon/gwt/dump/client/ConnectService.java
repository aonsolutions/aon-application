package com.esferalia.aon.gwt.dump.client;

import java.util.List;

import com.esferalia.aon.gwt.dump.shared.Progress;
import com.esferalia.aon.gwt.dump.shared.Task;
import com.esferalia.aon.watson.error.AonCoreException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("dump")
public interface ConnectService extends RemoteService {

	List<com.esferalia.aon.gwt.dump.shared.Domain> getAvailableDomains() throws AonCoreException;
	Task dumpDomain(String domain);
	Progress process(Integer idTask, int i);
	Boolean cancelDownload (Integer idTask);
	List<Task> getTaskPending();
	Boolean eraseDownload(Integer idTask);

}
