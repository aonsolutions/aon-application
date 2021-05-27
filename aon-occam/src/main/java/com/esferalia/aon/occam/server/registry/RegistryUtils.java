package com.esferalia.aon.occam.server.registry;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CreditorProperties;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.Properties.SupplierProperties;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryUtils {

	public static Filter getFilter(RegistryProperties p, RegistryParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getId() != null) {
			prop = prop.and(p.getIdProperty().eq(params.getId()));
		}
		if (params.getDocumentType() != null) {
			prop = prop.and(p.getDocumentTypeProperty().eq(params.getDocumentType().value()));
		}
		if (params.getDocumentCountry() != null) {
			prop = prop.and(p.getDocumentCountryProperty().eq(params.getDocumentCountry().getIso2()));
		}
		if (AonStringUtils.isNotEmpty(params.getDocument())) {
			prop = prop.and(p.getDocumentProperty().like(AonStringUtils.SQLlike(params.getDocument())));
		}
		if (AonStringUtils.isNotEmpty(params.getName())) {
			prop = prop.and(p.getNameProperty().like(AonStringUtils.SQLlike(params.getName())));
		}
		if (AonStringUtils.isNotEmpty(params.getAlias())) {
			prop = prop.and(p.getAliasProperty().like(AonStringUtils.SQLlike(params.getAlias())));
		}
		if (!params.hasConfidentialityRole()) {
			prop = prop.and(p.getSecurityLevelProperty().eq(SecurityLevel.OFFICIAL.value()));
		} else {
			if (params.getSecurityLevel() != null) {
				prop = prop.and(p.getSecurityLevelProperty().eq(params.getSecurityLevel().value()));
			}
		}
		return prop;
	}

	public static Filter getFilter(CreditorProperties p, RegistryParams params) {
		Filter prop = getFilter( (RegistryProperties) p, params);
		Filter[] statusFilters = new Filter[]{
			(params.isActive()?p.getStatusProperty().eq(RegistryStatus.ACTIVE.value()):null),
			(params.isInactive()?p.getStatusProperty().eq(RegistryStatus.INACTIVE.value()):null),
			(params.isBlocked()?p.getStatusProperty().eq(RegistryStatus.BLOCKED.value()):null),
		}; 
		Filter statusFilter = null;
		for (Filter f : statusFilters) {
			if (f != null) {
				statusFilter = (statusFilter == null) ? f : statusFilter.or(f);
			}
		}
		if (statusFilter != null) {
			prop = prop.and(statusFilter);
		}
		return prop;
	}

	public static Filter getFilter(CustomerProperties p, RegistryParams params) {
		Filter prop = getFilter( (RegistryProperties) p, params);
		Filter[] statusFilters = new Filter[]{
			(params.isActive()?p.getStatusProperty().eq(RegistryStatus.ACTIVE.value()):null),
			(params.isInactive()?p.getStatusProperty().eq(RegistryStatus.INACTIVE.value()):null),
			(params.isBlocked()?p.getStatusProperty().eq(RegistryStatus.BLOCKED.value()):null),
		}; 
		Filter statusFilter = null;
		for (Filter f : statusFilters) {
			if (f != null) {
				statusFilter = (statusFilter == null) ? f : statusFilter.or(f);
			}
		}
		if (statusFilter != null) {
			prop = prop.and(statusFilter);
		}
		return prop;
	}
	
	public static Filter getFilter(SupplierProperties p, RegistryParams params) {
		Filter prop = getFilter( (RegistryProperties) p, params);
		Filter[] statusFilters = new Filter[]{
			(params.isActive()?p.getStatusProperty().eq(RegistryStatus.ACTIVE.value()):null),
			(params.isInactive()?p.getStatusProperty().eq(RegistryStatus.INACTIVE.value()):null),
			(params.isBlocked()?p.getStatusProperty().eq(RegistryStatus.BLOCKED.value()):null),
		}; 
		Filter statusFilter = null;
		for (Filter f : statusFilters) {
			if (f != null) {
				statusFilter = (statusFilter == null) ? f : statusFilter.or(f);
			}
		}
		if (statusFilter != null) {
			prop = prop.and(statusFilter);
		}
		return prop;
	}
	
}
