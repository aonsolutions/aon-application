package com.esferalia.aon.occam.server.rawdoc;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RawdocUtils {
	
	private RawdocUtils() {
		
	}
	public static Filter getFilter(RawdocProperties p, RawdocParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getNature() != null) {
			prop = prop.and(p.getNatureProperty().eq( params.getNature().value()));
		}
		if (params.getType() != null) {
			prop = prop.and(p.getTypeProperty().eq( params.getType().value()));
		}
		if (params.getStatus() != null) {
			prop = prop.and(p.getStatusProperty().eq( params.getStatus().value()));
		} else {
			Byte[] statuses = new Byte[] {
				 RawdocStatus.INBOX.value()
				,RawdocStatus.REJECTED.value()
				,RawdocStatus.TRASH.value()
				,RawdocStatus.PROCESSED.value()
			};
			prop = prop.and(p.getStatusProperty().in( statuses ));	
		}
		if ( AonStringUtils.isNotBlank(params.getQuery())) {
			String q = "%" + params.getQuery() + "%";
			prop = prop.and(p.getJsonProperty().like( q ));
		}
		return prop;
	}

}
