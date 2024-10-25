package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectOnConditionStep;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.occam.api.model.BankAccount;
import net.aonsolutions.occam.api.model.RegistryBank;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.AccountHandler.AccountFiller;

class RegistryBankHandler {

	private RegistryBankHandler() {
		
	}
	
	private static SelectOnConditionStep<Record> select(AONContext ctx) {
		return ctx.getDslContext().select()
				.from(RBANK)
				.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(RBANK.ACCOUNT));
	}

	static Stream<RegistryBank> streamByRegistry(AONContext ctx, Integer registryId) {
		return select(ctx)
			.where( RBANK.REGISTRY.eq( registryId ))
			.fetch()
			.stream()
			.map(new RegistryBankFiller())
		;
	}
	
	static class RegistryBankFiller extends Filler<RegistryBank> {

		@Override
		public RegistryBank apply(Record r) {
			return build(r);
		}
		
		public static RegistryBank build(Record r) {
			if (isNull(r, RBANK.ID)) return null;
			return new RegistryBank()
				.setId(getValue(r, RBANK.ID))
				.setRegistry(getValue(r, RBANK.REGISTRY))
				.setDomain(getValue(r, RBANK.DOMAIN))
				.setAccount( AccountFiller.build(r) )
				.setActive(getBoolean(r, RBANK.ACTIVE))
				.setAlias(getValue(r, RBANK.ALIAS))
				.setBankAccount(new BankAccount(r.getValue(RBANK.BANK_ACCOUNT)))
				.setRequisition(getValue(r, RBANK.REQUISITION))
				.setSepaMandateRef(getValue(r, RBANK.SEPA_MANDATE_REF))
				.setBic(getValue(r, RBANK.BIC))
				.setSuffix(getValue(r, RBANK.SUFIX))
				.setBalance(getValue(r, RBANK.BALANCE))
				.setAvailableBalance(getValue(r, RBANK.AVAILABLE_BALANCE))
				.setBalanceDate(getValue(r, RBANK.BALANCE_DATE))
			;
		}
		
	}
	
	static RegistryBank save(AONContext ctx, RegistryBank rbank) {
		RegistryBankValidation.validate(ctx, rbank);
		ctx.checkWrite();
		if(rbank.getId() != null && rbank.isDeleted()) { 
			delete(ctx, rbank.getId());
			return rbank;
		}
		return (rbank.getId() == null)
			? insert(ctx, rbank)
			: update(ctx, rbank);
	}

	private static RegistryBank insert(AONContext ctx, RegistryBank rbank){
		Integer id = ctx.getDslContext().insertInto(RBANK)
			.set(RBANK.DOMAIN,rbank.getDomain())
			.set(RBANK.REGISTRY,rbank.getRegistry())
			.set(RBANK.BANK_ACCOUNT, rbank.getBankAccount().map( ba ->  ba.getIban())
				.orElseThrow(() -> new AonCoreException(AonError.REGISTRY_BANK_INVALID.getMessage())))
			.set(RBANK.BIC, rbank.getBic())
			.set(RBANK.SUFIX, rbank.getSuffix())
			.set(RBANK.ALIAS, rbank.getAlias())
			.set(RBANK.ACTIVE, AonEnumUtils.getByte( rbank.isActive()))
			.set(RBANK.ACCOUNT, rbank.getAccount().map( ba ->  ba.getId()).orElse(null))
			.set(RBANK.REQUISITION, rbank.getRequisition())
			.set(RBANK.SEPA_MANDATE_REF, rbank.getSepaMandateRef())
			.returning(RBANK.ID)
			.fetchOne()
			.getValue(RBANK.ID);
		rbank.setId(id);
		ctx.log().debug("INSERT REGISTRY BANK ( registry: {0}) id: {1}",rbank.getRegistry(),rbank.getId());
		return rbank;
	}
	private static RegistryBank update(AONContext ctx, RegistryBank rbank){
		int count = ctx.getDslContext().update(RBANK)
			.set(RBANK.DOMAIN,rbank.getDomain())
			.set(RBANK.REGISTRY,rbank.getRegistry())
			.set(RBANK.BANK_ACCOUNT, rbank.getBankAccount().map( ba ->  ba.getIban())
				.orElseThrow(() -> new AonCoreException(AonError.REGISTRY_BANK_INVALID.getMessage())))
			.set(RBANK.BIC, rbank.getBic())
			.set(RBANK.SUFIX, rbank.getSuffix())
			.set(RBANK.ALIAS, rbank.getAlias())
			.set(RBANK.REQUISITION, rbank.getRequisition())
			.set(RBANK.SEPA_MANDATE_REF, rbank.getSepaMandateRef())
			.set(RBANK.ACTIVE, AonEnumUtils.getByte( rbank.isActive()))
			.set(RBANK.ACCOUNT, rbank.getAccount().map( ba ->  ba.getId()).orElse(null))
		.where(RBANK.ID.eq(rbank.getId()))
		.execute();
		ctx.log().debug("UPDATE REGISTRY BANK ( registry: {0}) id: {1}. ({2} rows)", rbank.getRegistry(), rbank.getId(),count);
		return rbank;
	}

	static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RBANK)
			.where(RBANK.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY BANK id: {0} ({1} rows)", id, count);
	}

	
	
	private class RegistryBankValidation {
		
		private RegistryBankValidation() {}
		
		private static final BiConsumer<AONContext, RegistryBank> NULL = (ctx, rb) -> {
			if (rb == null) throw new AonCoreException(AonError.REGISTRY_BANK_NULL.getMessage());
		};
		
		private static final BiConsumer<AONContext, RegistryBank> NULL_DOMAIN = (ctx, rb) -> {
			if (rb.getDomain() == null) throw new AonCoreException(AonError.REGISTRY_BANK_NULL_DOMAIN.getMessage());
		};
		
		/**
		 * Throws an exception if the rb's registry is null
		 */
		private static final BiConsumer<AONContext, RegistryBank> NULL_REGISTRY = (ctx, rb) -> {
			if (rb.getRegistry() == null) throw new AonCoreException(AonError.REGISTRY_BANK_NULL_REGISTRY.getMessage());
		};
		
		/**
		 * Throws an exception if the rb's bank account is empty or invalid
		 */
		private static final BiConsumer<AONContext, RegistryBank> INVALID_BANK_ACCOUNT = (ctx, rb) -> {
			if (rb.getBankAccount().isEmpty() || !rb.getBankAccount().get().isValidBankAccount()) 
				throw new AonCoreException(AonError.REGISTRY_BANK_INVALID.getMessage());
		};
		
		/**
		 * Throws an exception if the size of the rb's bic is invalid
		 */
		private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_BIC = (ctx, rb) -> {
			if (rb != null && rb.getBic() != null  && rb.getBic().length() > RBANK.BIC.getDataType().length())
				throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_BIC_SIZE.getMessage());
		};
		
		/**
		 * Throws an exception if the size of the rb's suffix is invalid
		 */
		private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_SUFFIX = (ctx, rb) -> {
			if (rb != null && rb.getSuffix() != null && rb.getSuffix().length() > RBANK.SUFIX.getDataType().length())
				throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_SUFFIX_SIZE.getMessage());
		};
		
		/**
		 * Throws an exception if the size of the rb's requisition is invalid
		 */
		private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_REQUISITION = (ctx, rb) -> {
			if (rb != null && rb.getRequisition() != null && rb.getRequisition().length() > RBANK.REQUISITION.getDataType().length())
				throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_REQUISITION_SIZE.getMessage());
		};
		
		/**
		 * Throws an exception if the size of the rb's SEPA mandate reference is invalid
		 */
		private static final BiConsumer<AONContext, RegistryBank> INVALID_SIZE_SEPA_MANDATE_REF = (ctx, rb) -> {
			if (rb != null && rb.getSepaMandateRef() != null && rb.getSepaMandateRef().length() > RBANK.SEPA_MANDATE_REF.getDataType().length())
				throw new AonCoreException(AonError.REGISTRY_BANK_INVALID_SEPA_MANDATE_REF_SIZE.getMessage());
		};
		
		public static void validate(AONContext ctx, RegistryBank rb) throws AonCoreException {
			NULL
			.andThen(NULL_DOMAIN)
			.andThen(NULL_REGISTRY)
			.andThen(INVALID_BANK_ACCOUNT)
			.andThen(INVALID_SIZE_BIC)
			.andThen(INVALID_SIZE_SUFFIX)
			.andThen(INVALID_SIZE_REQUISITION)
			.andThen(INVALID_SIZE_SEPA_MANDATE_REF)
			.accept(ctx, rb);
		}
	}
	
	
	// **********************************************************************************
	// **********************************************************************************
	// **********************************************************************************
/*	

	public static RegistryBank get(AONContext ctx, RegistryBankFilter filter){
		return select(ctx,filter).limit(1)
				.fetch().stream().map(new RegistryBankFiller())
				.findFirst().orElse(new RegistryBank());
	}
	
	public static RegistryBank get(AONContext ctx, Integer id) {
		return RegistryBankHandler.get(ctx, f -> f.getIdProperty().eq(id));
	}
	
	
	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RBANK)
			.where(RBANK.REGISTRY.eq(registry))
			.execute();
		ctx.log().debug("DELETE REGISTRY BANK registry: {0} ({1} rows)", registry, count);
		return count;
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static RegistryBank getRandom(AONContext ctx, RegistryBankFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryBankFiller())
			.findFirst()
			.orElse(null);
	}

*/
}