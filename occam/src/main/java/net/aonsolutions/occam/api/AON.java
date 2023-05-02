package net.aonsolutions.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.dao.DomainDAO;

public class AON {

	private AON() {
	}
	// ******************************** [DOMAIN]
	public static Optional<Domain> getDomain(Occam occam, DomainFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return DomainDAO.get(ctx, filter);
		}
	}
	public static Optional<Domain> getDomain(Occam occam, DomainFilter filter, DomainBuilderFactory factory) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return DomainDAO.get(ctx, filter,  factory);
		}
	}
	public static Stream<Domain> getDomains(Occam occam, DomainFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return DomainDAO.getStream(ctx, filter);
		}
	}
	public static Stream<Domain> getDomains(Occam occam, DomainFilter filter, DomainBuilderFactory factory) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return DomainDAO.getStream(ctx, filter,  factory);
		}
	}
}
