package com.esferalia.aon.occam.server.rawdoc;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;

public class RawdocUtils {
	
	public static Filter getFilter(RawdocProperties p, RawdocParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getNature() != null) {
			prop = prop.and(p.getNatureProperty().eq( params.getNature().value()));
		}
		if (params.getType() != null) {
			prop = prop.and(p.getTypeProperty().eq( params.getType().value()));
		}
		if (params.getStatus() != null) {
			if (params.getStatus() == RawdocStatus.INBOX) {
				Byte[] statuses = new Byte[] {RawdocStatus.INBOX.value(), RawdocStatus.PENDING.value()};
				prop = prop.and(p.getStatusProperty().in( statuses ));	
			} else {
				prop = prop.and(p.getStatusProperty().eq( params.getStatus().value()));
			}
		}
		return prop;
	}

}
