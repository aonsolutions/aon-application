package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.logging.Logger;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;

public class GlobalDAO {
	
	private static final Logger LOGGER = Logger.getLogger(GlobalDAO.class.getName());
	
	private static final String GLOBAL_DOMAIN_NAME = "global.aonsolutions.net";
	private static final int FAKE_GLOBAL_DOMAIN_ID = -1;

	protected static AONContext getGlobalAONContext(String user) {
		return AONContext.getAONContext(GLOBAL_DOMAIN_NAME, FAKE_GLOBAL_DOMAIN_ID , user); 
	}
	
	protected static Integer getGlobalDomain( AONContext ctx ) {
		return ctx.getDslContext()
			.select( DOMAIN.ID )
			.from(DOMAIN)
			.where(DOMAIN.NAME.eq( GLOBAL_DOMAIN_NAME ) )
			.fetch()
			.stream()
			.map( rec -> rec.getValue(DOMAIN.ID))
			.findFirst()
			.orElse(null);
	}
	
	public static Registry getRegistry( String user, String document ) {
		try ( AONContext ctx =  getGlobalAONContext(user) ) {
			final Integer globalDomain =  getGlobalDomain(ctx);
			return RegistryDAO.getStream(ctx, f -> 
						f.getDomainProperty().eq(globalDomain)
						.and(f.getDocumentProperty().eq(document)))
				.findFirst()
				.orElse(null);
			
		} catch (Throwable t) {
			LOGGER.severe("Con not read GLOBAL registries ("+ t.getMessage() +")");
			return null;
		}
	}
	
	public static Registry getRegistry( String user, Integer id ) {
		try ( AONContext ctx =  getGlobalAONContext(user) ) {
			final Integer globalDomain =  getGlobalDomain(ctx);
			return RegistryDAO.getStream(ctx, f -> 
						f.getDomainProperty().eq(globalDomain)
						.and(f.getIdProperty().eq(id)))
				.findFirst()
				.orElse(null);
			
		} catch (Throwable t) {
			LOGGER.severe("Con not read GLOBAL registries ("+ t.getMessage() +")");
			return null;
		}
	}
	
	public static Stream<RegistryMedia> getRegistryMediaStream( String user, Integer id ) {
		try ( AONContext ctx =  getGlobalAONContext(user) ) {
			final Integer globalDomain =  getGlobalDomain(ctx);
			return RegistryMediaDAO.getStream(ctx, f -> 
						f.getDomainProperty().eq(globalDomain)
						.and(f.getRegistryProperty().eq(id)));
		} catch (Throwable t) {
			LOGGER.severe("Con not read GLOBAL registries ("+ t.getMessage() +")");
			return null;
		}
	}
	
	public static Stream<RegistryAddress> getRegistryAddressStream( String user, Integer id ) {
		try ( AONContext ctx =  getGlobalAONContext(user) ) {
			final Integer globalDomain =  getGlobalDomain(ctx);
			return RegistryAddressDAO.getStream(ctx, f -> 
						f.getDomainProperty().eq(globalDomain)
						.and(f.getRegistryProperty().eq(id)));
		} catch (Throwable t) {
			LOGGER.severe("Con not read GLOBAL registries ("+ t.getMessage() +")");
			return null;
		}
	}

	public static Registry copyRegistry(AONContext ctx, Integer globalId) {
		 Registry registry = RegistryDAO.save(ctx, getRegistry(ctx.getUser(), globalId)
				.setId(null).setDomain(new Domain().setId(ctx.getDomainId())));

		getRegistryAddressStream(ctx.getUser(), globalId).forEach(address -> {
			RegistryAddressDAO.save(ctx, address.setId(null)
					.setDomain(ctx.getDomainId())
					.setRegistry(registry.getId()));
		});
		
		getRegistryMediaStream(ctx.getUser(), globalId).forEach(media -> {
			RegistryMediaDAO.save(ctx, media.setId(null)
					.setDomain(ctx.getDomainId())
					.setRegistry(registry.getId())
					.setRaddress(null));
		});	
		return registry;
	}
	
	
	
}

