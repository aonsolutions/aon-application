package net.aonsolutions.aon.gwt.warehouse.server;

import java.util.List;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationProperties;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.gwt.warehouse.client.ElaborationService;
import net.aonsolutions.aon.gwt.warehouse.shared.ElaborationParams;

@WebServlet(name = "Elaboration gwt Servlet", urlPatterns = { "/aon_gwt_aio/ms/elaboration" })
public class ElaborationServiceImpl extends AonStatelessRemoteServiceServlet implements ElaborationService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public List<Elaboration> getElaborations(Occam occam, ElaborationParams params) {
		return AON.getElaborationList(occam, f -> elaborationFilter(f, params), elaborationOptions(params));
	}
	
	public Filter elaborationFilter(ElaborationProperties f, ElaborationParams params) {
		Filter filter =  f.getDomainProperty().eq(params.getDomain());

		if(params.getFrom() != null) {
			filter = filter.and(f.getDateProperty().ge(AonDateUtils.toTimestamp(params.getFrom())));
		}
		
		if(params.getTo() != null) {
			filter = filter.and(f.getDateProperty().le(AonDateUtils.toTimestamp(params.getTo())));
		}
		
    	if(!AonStringUtils.isBlank(params.getValue())) {
    		filter = filter.and(f.getDescriptionProperty().like("%" + params.getValue() + "%"));
    	}
    	
    	return filter;
    }
	
	private Options elaborationOptions(ElaborationParams params) {
		return new Options()
			.setPage(params.getPage())
			.setPerPage(params.getPerPage());
	}

}
