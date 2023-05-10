package net.aonsolutions.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.api.filter.UserFacade.UserBuilderFactory;
import net.aonsolutions.occam.api.filter.UserFacade.UserFilter;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.dao.SecurityDAO;

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
	// ******************************** [USER]
	public static Optional<User> getUser(Occam occam, UserFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return SecurityDAO.get(ctx, filter);
		}
	}
	public static Optional<User> getUser(Occam occam, UserFilter filter, UserBuilderFactory factory) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return SecurityDAO.get(ctx, filter,  factory);
		}
	}
	public static Stream<User> getUsers(Occam occam, UserFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return SecurityDAO.getStream(ctx, filter);
		}
	}
	public static Stream<User> getUsers(Occam occam, UserFilter filter, UserBuilderFactory factory) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return SecurityDAO.getStream(ctx, filter,  factory);
		}
	}
	
}
