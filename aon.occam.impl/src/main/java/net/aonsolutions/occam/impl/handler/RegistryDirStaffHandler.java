package net.aonsolutions.occam.impl.handler;


import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.RegistryDirStaff;
import net.aonsolutions.occam.impl.AONContext;

class RegistryDirStaffHandler {
	
	private static final String RDIRSTAFF_REGISTRY_LABEL = "Registry";
	private static final String RDIRSTAFF_DOCUMENT_LABEL = "Documento";
	private static final String RDIRSTAFF_NAME_LABEL = "Name";

	private RegistryDirStaffHandler() {
		
	}
	
	private static SelectJoinStep<Record> select(AONContext ctx) {
		return ctx.getDslContext().select()
			.from(RDIR_STAFF);
	}

	static Stream<RegistryDirStaff> streamByRegistry(AONContext ctx, Integer registryId){
		return select(ctx)
			.where( RDIR_STAFF.REGISTRY.eq(registryId))
			.fetch()
			.stream()
			.map(new RegistryDirStaffFiller());
	}

	static RegistryDirStaff save(AONContext ctx, RegistryDirStaff rDirStaff) {
		ctx.checkWrite();
		RegistryDirStaffValidation.validate(ctx, rDirStaff);
		return (rDirStaff.getId() == null)
				?insert(ctx, rDirStaff)
				:update(ctx, rDirStaff);
	}
	
	private static RegistryDirStaff insert(AONContext ctx, RegistryDirStaff rDirStaff){
		Integer id = ctx.getDslContext().insertInto(RDIR_STAFF)
				.set(RDIR_STAFF.DOMAIN,rDirStaff.getDomain())
				.set(RDIR_STAFF.REGISTRY,rDirStaff.getRegistry())
				.set(RDIR_STAFF.DOCUMENT,rDirStaff.getDocument())
				.set(RDIR_STAFF.NAME,rDirStaff.getName())
				.set(RDIR_STAFF.SHAREHOLDER , AonEnumUtils.getByte( rDirStaff.isShareHolder() ))
				.set(RDIR_STAFF.REPRESENTATIVE , AonEnumUtils.getByte( rDirStaff.isRepresentative()))
				.set(RDIR_STAFF.DIRECTOR , AonEnumUtils.getByte( rDirStaff.isDirector()))
				.set(RDIR_STAFF.PERCENT_SHARE ,rDirStaff.getPercentShare())
				.set(RDIR_STAFF.SHARE_NUMBER ,rDirStaff.getShareNumber())
				.set(RDIR_STAFF.NOMINAL_VALUE ,rDirStaff.getNominalValue())
				.set(RDIR_STAFF.DUE_DATE , AonDateUtils.toSql( rDirStaff.getDueDate()))
				.set(RDIR_STAFF.REPRESENTATIVE_LABOR , AonEnumUtils.getByte( rDirStaff.isRepresentativeLabor()))
				.set(RDIR_STAFF.CHARGE_DESCRIPTION ,rDirStaff.getChargeDescription())
			.returning(RDIR_STAFF.ID)
			.fetchOne()
			.getValue(RDIR_STAFF.ID);
		rDirStaff.setId(id);
		ctx.log().info("INSERT RDIR_STAFF ( registry: "+ rDirStaff.getRegistry() +") id: " + rDirStaff.getId());
		return rDirStaff;
	}
	private static RegistryDirStaff update(AONContext ctx, RegistryDirStaff rDirStaff){
		int count = ctx.getDslContext().update(RDIR_STAFF)
			.set(RDIR_STAFF.DOMAIN,rDirStaff.getDomain())
			.set(RDIR_STAFF.REGISTRY,rDirStaff.getRegistry())
			.set(RDIR_STAFF.DOCUMENT,rDirStaff.getDocument())
			.set(RDIR_STAFF.NAME,rDirStaff.getName())
			.set(RDIR_STAFF.SHAREHOLDER , AonEnumUtils.getByte( rDirStaff.isShareHolder() ))
			.set(RDIR_STAFF.REPRESENTATIVE , AonEnumUtils.getByte( rDirStaff.isRepresentative()))
			.set(RDIR_STAFF.DIRECTOR , AonEnumUtils.getByte( rDirStaff.isDirector()))
			.set(RDIR_STAFF.PERCENT_SHARE ,rDirStaff.getPercentShare())
			.set(RDIR_STAFF.SHARE_NUMBER ,rDirStaff.getShareNumber())
			.set(RDIR_STAFF.NOMINAL_VALUE ,rDirStaff.getNominalValue())
			.set(RDIR_STAFF.DUE_DATE , AonDateUtils.toSql( rDirStaff.getDueDate()))
			.set(RDIR_STAFF.REPRESENTATIVE_LABOR , AonEnumUtils.getByte( rDirStaff.isRepresentativeLabor()))
			.set(RDIR_STAFF.CHARGE_DESCRIPTION ,rDirStaff.getChargeDescription())
			.where(RDIR_STAFF.ID.eq(rDirStaff.getId()))
			.execute();
		ctx.log().info("UPDATE RDIR_STAFF ( registry: "+ rDirStaff.getRegistry() +") id: " + rDirStaff.getId() + ". (" + count + " rows)");
		return rDirStaff;
	}
	
	static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		RegistryDirStaffValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(RDIR_STAFF)
			.where(RDIR_STAFF.ID.eq(id))
			.execute();
		ctx.log().info("DELETE RDIR_STAFF id:" + id + " ("+count+" rows)");
	}

	static class RegistryDirStaffFiller extends Filler<RegistryDirStaff> {

		@Override
		public RegistryDirStaff apply(Record r) {
			return new RegistryDirStaff()
				.setId(getValue(r, RDIR_STAFF.ID))
				.setRegistry(getValue(r, RDIR_STAFF.REGISTRY))
				.setDomain(getValue(r, RDIR_STAFF.DOMAIN))
				.setChargeDescription(getValue(r, RDIR_STAFF.CHARGE_DESCRIPTION))
				.setDirector(getBoolean(r, RDIR_STAFF.DIRECTOR))
				.setDocument(getValue(r, RDIR_STAFF.DOCUMENT))
				.setName(getValue(r, RDIR_STAFF.NAME))
				.setDueDate(getValue(r, RDIR_STAFF.DUE_DATE))
				.setNominalValue(getValue(r, RDIR_STAFF.NOMINAL_VALUE))
				.setPercentShare(getValue(r, RDIR_STAFF.PERCENT_SHARE))
				.setShareNumber(getValue(r, RDIR_STAFF.SHARE_NUMBER))
				.setRepresentative(getBoolean(r, RDIR_STAFF.REPRESENTATIVE))
				.setRepresentativeLabor(getBoolean(r, RDIR_STAFF.REPRESENTATIVE_LABOR))
				.setShareHolder(getBoolean(r, RDIR_STAFF.SHAREHOLDER));
		}
	}

	private static class RegistryDirStaffValidation {
		private static final BiConsumer<AONContext, RegistryDirStaff> EMPTY_DOMAIN = (ctx,rDirStaff) -> {
			if (rDirStaff.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final BiConsumer<AONContext,RegistryDirStaff> EMPTY_REGISTRY = (ctx,rDirStaff) -> {
			if (rDirStaff.getRegistry() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(RDIRSTAFF_REGISTRY_LABEL)) ;
		};
		
		private static final BiConsumer<AONContext,RegistryDirStaff> EMPTY_DOCUMENT = (ctx,rDirStaff) -> {
			if (AonStringUtils.isBlank(rDirStaff.getDocument())) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(RDIRSTAFF_DOCUMENT_LABEL)) ;
		};
		
		private static final BiConsumer<AONContext,RegistryDirStaff> EMPTY_NAME = (ctx,rDirStaff) -> {
			if (AonStringUtils.isBlank(rDirStaff.getName())) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(RDIRSTAFF_NAME_LABEL)) ;
		};

		private static final BiConsumer<AONContext,RegistryDirStaff> OVERFLOW_DOCUMENT = (ctx,rDirStaff) -> {
			if (AonStringUtils.length(rDirStaff.getDocument()) > RDIR_STAFF.DOCUMENT.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format(RDIRSTAFF_DOCUMENT_LABEL, RDIR_STAFF.DOCUMENT.getDataType().length() ));
		};
		
		private static void validate(AONContext ctx, RegistryDirStaff rDirStaff) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_REGISTRY)
				.andThen(EMPTY_DOCUMENT)
				.andThen(EMPTY_NAME)
				.andThen(OVERFLOW_DOCUMENT)
				.accept(ctx,rDirStaff);
		}
		
		private static void validateDeletion(AONContext ctx, Integer id) {
			// Implmentar en su momento.
		}
	}
/*
	public static RegistryDirStaff get(AONContext ctx, Integer id){
		return getStream(ctx, f -> f.getIdProperty().eq(id))
				.findFirst()
				.orElse(null);
	}
	
	public static LinkedList<RegistryDirStaff> getRepresentativeLabor(AONContext ctx, int domain) {
		Company company =  CompanyDAO.getCompany(ctx, domain);
		if (company != null && company.getId() != null) {
			return getStream(ctx, f -> f.getRegistryProperty().eq(company.getId())
						.and(f.getRepresentativeLaborProperty().eq((byte) 1)))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();
	}

	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RDIR_STAFF)
			.where(RDIR_STAFF.REGISTRY.eq(registry))
			.execute();
		ctx.log().info("DELETE RDIR_STAFF registry:" + registry + " ("+count+" rows)");
		return count;
	}
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	public static RegistryDirStaff getRandom(AONContext ctx, RegistryDirStaffFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryDirStaffFiller())
			.findFirst()
			.orElse(null);
	}
*/	

}
