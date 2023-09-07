package net.aonsolutions.aon.gwt.warehouse.client;

import java.util.List;

import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import net.aonsolutions.aon.gwt.warehouse.shared.ElaborationParams;

@RemoteServiceRelativePath("ms/elaboration")
public interface ElaborationService extends RemoteService {
	
	List<Elaboration> getElaborations(Occam occam, ElaborationParams params);

}
