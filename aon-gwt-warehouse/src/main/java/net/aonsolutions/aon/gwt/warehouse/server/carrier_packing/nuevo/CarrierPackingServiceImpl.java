package net.aonsolutions.aon.gwt.warehouse.server.carrier_packing.nuevo;

import java.util.List;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.gwt.warehouse.client.carrier_packing.nuevo.CarrierPackingService;
import net.aonsolutions.aon.gwt.warehouse.shared.CarrierPackingFilter;

@WebServlet(name = "CarrierPacking gwt Servlet", urlPatterns = { "/aon_gwt_aio/ms/carrierPacking" })
public class CarrierPackingServiceImpl extends AonStatelessRemoteServiceServlet implements CarrierPackingService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public List<CarrierPacking> getCarrierPackingList(Occam occam, CarrierPackingFilter params) {
		return AON.getCarrierPackingList(occam, f -> carrierPackingFilter(f, params), carrierPackingOptions(params));
	}
	
	public Filter carrierPackingFilter(CarrierPackingProperties f, CarrierPackingFilter params) {
		Filter filter = f.getDomainProperty().eq(params.getDomain());

		if(params.getFrom() != null) {
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toTimestamp(params.getFrom())));
		}
		
		if(params.getTo() != null) {
			filter = filter.and(f.getIssueDateProperty().le(AonDateUtils.toTimestamp(params.getTo())));
		}
		
    	if(!AonStringUtils.isBlank(params.getValue())) {
    		filter = filter.and(
    				f.getSeriesProperty().like("%" + params.getValue() + "%")
    				.or(f.getDriverNameProperty().like("%" + params.getValue() + "%"))
    				.or(f.getDriverDocumentProperty().like("%" + params.getValue() + "%"))
    				.or(f.getCarrierReferenceProperty().like("%" + params.getValue() + "%"))
   				);
    	}
    	
    	return filter;
    }
	
	private Options carrierPackingOptions(CarrierPackingFilter params) {
		return new Options()
			.setPage(params.getPage())
			.setPerPage(params.getPerPage());
	}

}
