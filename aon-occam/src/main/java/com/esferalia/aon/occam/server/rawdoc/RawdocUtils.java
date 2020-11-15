package com.esferalia.aon.occam.server.rawdoc;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.RawdocProperties;
import com.esferalia.aon.occam.api.model.RawdocParams;

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
			prop = prop.and(p.getStatusProperty().eq( params.getStatus().value()));
		}
		return prop;
	}

}
