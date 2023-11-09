package com.esferalia.aon.occam.impl.jooq.validation;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryAddressValidation {
	
	public static final String ADDRESS_REGISTRY_LABEL = "Registry";
	public static final String ADDRESS_NUMBER_LABEL = "N\u00fcmero";
	public static final String ADDRESS_ZIP_LABEL = "C\u00F3digo postal";

	private RegistryAddressValidation() {}
	
	public static final BiConsumer<AONContext,RegistryAddress> EMPTY_DOMAIN = (ctx,registryAddress) -> {
		if (registryAddress.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
	};
	
	public static final BiConsumer<AONContext,RegistryAddress> EMPTY_REGISTRY = (ctx,registryAddress) -> {
		if (registryAddress.getRegistry() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format(ADDRESS_REGISTRY_LABEL)) ;
	};
	
	public static final BiConsumer<AONContext,RegistryAddress> OVERFLOW_NUMBER = (ctx,registryAddress) -> {
		if (AonStringUtils.length(registryAddress.getNumber()) > RADDRESS.NUMBER.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( ADDRESS_NUMBER_LABEL, RADDRESS.NUMBER.getDataType().length() ));
	};
	
	public static final BiConsumer<AONContext,RegistryAddress> OVERFLOW_ZIP = (ctx,registryAddress) -> {
		if (AonStringUtils.length(registryAddress.getZip()) > RADDRESS.ZIP.getDataType().length() )
			throw new AonCoreException(AonError.INVALID_LENGTH.format( ADDRESS_ZIP_LABEL, RADDRESS.ZIP.getDataType().length() ));
	};
	
	public static final ObjIntConsumer<AONContext> CHECK_INVOICE = (ctx,registryAddressId) -> {
		Integer invoiceId = ctx.getDslContext().select(INVOICE.ID)
				.from(INVOICE)
				.where(INVOICE.RADDRESS.eq(registryAddressId))
				.fetch()
				.stream()
				.map (rec -> rec.getValue(INVOICE.ID))
				.findAny()
				.orElse(null);
		
		if (invoiceId != null) {
			throw new AonCoreException(AonError.DELETE_RADDRESS_INVOICE.getMessage());
		}
	};
	
	public static void validate(AONContext ctx, RegistryAddress registryAddress) throws AonCoreException {
			EMPTY_DOMAIN
			.andThen(EMPTY_REGISTRY)
			.andThen(OVERFLOW_NUMBER)
			.andThen(OVERFLOW_ZIP)
			.accept(ctx,registryAddress);
	}
	
	public static void validateDeletion(AONContext ctx, Integer id) {
		CHECK_INVOICE
			.accept(ctx,id);
	}
}