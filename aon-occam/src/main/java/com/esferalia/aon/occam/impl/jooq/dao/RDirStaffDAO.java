package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RDirStaffFilter;
import com.esferalia.aon.occam.api.model.Properties.RDirStaffProperties;
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RDirStaffDAO {
	
	public static final String RDIRSTAFF_REGISTRY_LABEL = "Registry";
	public static final String RDIRSTAFF_DOCUMENT_LABEL = "Documento";
	public static final String RDIRSTAFF_NAME_LABEL = "Name";

	private RDirStaffDAO() {
		
	}
	
	private static final RDirStaffPropertiesDAO RDIRSTAFF_PROPERTIES = new RDirStaffPropertiesDAO();
	protected static class RDirStaffPropertiesDAO implements RDirStaffProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RDirStaffFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RDirStaffFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.REGISTRY);}
		@Override public Property<Byte> getShareHolderProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.SHAREHOLDER);}
		@Override public Property<Byte> getRepresentativeProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.REPRESENTATIVE);}
		@Override public Property<Byte> getRepresentativeLaborProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.REPRESENTATIVE_LABOR);}
		@Override public Property<Byte> getDirectorProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.DIRECTOR);}
	
	}

	public static class RDirStaffFiller  implements Function<Record, RDirStaff> {

		@Override
		public RDirStaff apply(Record r) {
			return new RDirStaff()
					.setId(r.getValue(RDIR_STAFF.ID))
					.setRegistry(r.getValue(RDIR_STAFF.REGISTRY))
					.setDomain(r.getValue(RDIR_STAFF.DOMAIN))
					.setChargeDescription(r.getValue(RDIR_STAFF.CHARGE_DESCRIPTION))
					.setDirector(r.getValue(RDIR_STAFF.DIRECTOR) == 1)
					.setDocument(r.getValue(RDIR_STAFF.DOCUMENT))
					.setName(r.getValue(RDIR_STAFF.NAME))
					.setDueDate(r.getValue(RDIR_STAFF.DUE_DATE))
					.setNominalValue(r.getValue(RDIR_STAFF.NOMINAL_VALUE))
					.setPercentShare(r.getValue(RDIR_STAFF.PERCENT_SHARE))
					.setShareNumber(r.getValue(RDIR_STAFF.SHARE_NUMBER))
					.setRepresentative(r.getValue(RDIR_STAFF.REPRESENTATIVE) == 1)
					.setRepresentativeLabor(r.getValue(RDIR_STAFF.REPRESENTATIVE_LABOR) == 1)
					.setShareHolder(r.getValue(RDIR_STAFF.SHAREHOLDER) == 1);
		}
	}

	private static class RDirStaffAutoComplete {
		
		public static final BiConsumer<AONContext, RDirStaff> COMPLETE_MAIN_TYPE = (ctx, rDirStaff) -> {
		};
		
		public static void autoComplete(AONContext ctx, RDirStaff rDirStaff) throws AonCoreException {
			COMPLETE_MAIN_TYPE
				.accept(ctx, rDirStaff);
		}
	}
	
	private static class RDirStaffValidation {
		public static final BiConsumer<AONContext, RDirStaff> EMPTY_DOMAIN = (ctx,rDirStaff) -> {
			if (rDirStaff.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext,RDirStaff> EMPTY_REGISTRY = (ctx,rDirStaff) -> {
			if (rDirStaff.getRegistry() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(RDIRSTAFF_REGISTRY_LABEL)) ;
		};
		
		public static final BiConsumer<AONContext,RDirStaff> EMPTY_DOCUMENT = (ctx,rDirStaff) -> {
			if (AonStringUtils.isBlank(rDirStaff.getDocument())) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(RDIRSTAFF_DOCUMENT_LABEL)) ;
		};
		
		public static final BiConsumer<AONContext,RDirStaff> EMPTY_NAME = (ctx,rDirStaff) -> {
			if (AonStringUtils.isBlank(rDirStaff.getName())) 
				throw new AonCoreException(AonError.EMPTY_DATA.format(RDIRSTAFF_NAME_LABEL)) ;
		};

		public static final BiConsumer<AONContext,RDirStaff> OVERFLOW_DOCUMENT = (ctx,rDirStaff) -> {
			if (AonStringUtils.length(rDirStaff.getDocument()) > RDIR_STAFF.DOCUMENT.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format(RDIRSTAFF_DOCUMENT_LABEL, RDIR_STAFF.DOCUMENT.getDataType().length() ));
		};
		
		public static void validate(AONContext ctx, RDirStaff rDirStaff) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_REGISTRY)
				.andThen(EMPTY_DOCUMENT)
				.andThen(EMPTY_NAME)
				.andThen(OVERFLOW_DOCUMENT)
				.accept(ctx,rDirStaff);
		}
		
		public static void validateDeletion(AONContext ctx, Integer id) {
			// Implmentar en su momento.
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, RDirStaffFilter filter) {
		return ctx.getDslContext().select()
				.from(RDIR_STAFF)
				.where(RDIRSTAFF_PROPERTIES.getConditions(filter));
	}

	public static RDirStaff get(AONContext ctx, Integer id){
		return getStream(ctx, f -> f.getIdProperty().eq(id))
				.findFirst()
				.orElse(null);
	}
	
	public static Stream<RDirStaff> getStreamByRegistry(AONContext ctx, Integer registryId){
		return getStream(ctx, f -> f.getRegistryProperty().eq(registryId));
	}
	
	public static Stream<RDirStaff> getStream(AONContext ctx, RDirStaffFilter filter) {
		return select(ctx,filter)
			.fetch()
			.stream()
			.map(new RDirStaffFiller());
	}
	
	public static LinkedList<RDirStaff> getRepresentativeLabor(AONContext ctx, int domain) {
		Company company =  CompanyDAO.getCompany(ctx, domain);
		if (company != null && company.getId() != null) {
			return getStream(ctx, f -> f.getRegistryProperty().eq(company.getId())
						.and(f.getRepresentativeLaborProperty().eq((byte) 1)))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();
	}

	public static RDirStaff save(AONContext ctx, RDirStaff rDirStaff) {
		ctx.checkWrite();
		RDirStaffAutoComplete.autoComplete(ctx, rDirStaff);
		RDirStaffValidation.validate(ctx, rDirStaff);
		return (rDirStaff.getId() == null)
				?insert(ctx, rDirStaff)
				:update(ctx, rDirStaff);
	}
	
	private static RDirStaff insert(AONContext ctx, RDirStaff rDirStaff){
		Integer id = ctx.getDslContext().insertInto(RDIR_STAFF)
				.set(RDIR_STAFF.DOMAIN,rDirStaff.getDomain())
				.set(RDIR_STAFF.REGISTRY,rDirStaff.getRegistry())
				.set(RDIR_STAFF.DOCUMENT,rDirStaff.getDocument())
				.set(RDIR_STAFF.NAME,rDirStaff.getName())
				.set(RDIR_STAFF.SHAREHOLDER , AonEnumUtils.getByte( rDirStaff.getShareHolder() ))
				.set(RDIR_STAFF.REPRESENTATIVE , AonEnumUtils.getByte( rDirStaff.getRepresentative()))
				.set(RDIR_STAFF.DIRECTOR , AonEnumUtils.getByte( rDirStaff.getDirector()))
				.set(RDIR_STAFF.PERCENT_SHARE ,rDirStaff.getPercentShare())
				.set(RDIR_STAFF.SHARE_NUMBER ,rDirStaff.getShareNumber())
				.set(RDIR_STAFF.NOMINAL_VALUE ,rDirStaff.getNominalValue())
				.set(RDIR_STAFF.DUE_DATE , AonDateUtils.toSql( rDirStaff.getDueDate()))
				.set(RDIR_STAFF.REPRESENTATIVE_LABOR , AonEnumUtils.getByte( rDirStaff.getRepresentativeLabor()))
				.set(RDIR_STAFF.CHARGE_DESCRIPTION ,rDirStaff.getChargeDescription())
			.returning(RDIR_STAFF.ID)
			.fetchOne()
			.getValue(RDIR_STAFF.ID);
		rDirStaff.setId(id).setDirty(false);
		ctx.log().info("INSERT RDIR_STAFF ( registry: "+ rDirStaff.getRegistry() +") id: " + rDirStaff.getId());
		return rDirStaff;
	}
	private static RDirStaff update(AONContext ctx, RDirStaff rDirStaff){
		int count = ctx.getDslContext().update(RDIR_STAFF)
			.set(RDIR_STAFF.DOMAIN,rDirStaff.getDomain())
			.set(RDIR_STAFF.REGISTRY,rDirStaff.getRegistry())
			.set(RDIR_STAFF.DOCUMENT,rDirStaff.getDocument())
			.set(RDIR_STAFF.NAME,rDirStaff.getName())
			.set(RDIR_STAFF.SHAREHOLDER , AonEnumUtils.getByte( rDirStaff.getShareHolder() ))
			.set(RDIR_STAFF.REPRESENTATIVE , AonEnumUtils.getByte( rDirStaff.getRepresentative()))
			.set(RDIR_STAFF.DIRECTOR , AonEnumUtils.getByte( rDirStaff.getDirector()))
			.set(RDIR_STAFF.PERCENT_SHARE ,rDirStaff.getPercentShare())
			.set(RDIR_STAFF.SHARE_NUMBER ,rDirStaff.getShareNumber())
			.set(RDIR_STAFF.NOMINAL_VALUE ,rDirStaff.getNominalValue())
			.set(RDIR_STAFF.DUE_DATE , AonDateUtils.toSql( rDirStaff.getDueDate()))
			.set(RDIR_STAFF.REPRESENTATIVE_LABOR , AonEnumUtils.getByte( rDirStaff.getRepresentativeLabor()))
			.set(RDIR_STAFF.CHARGE_DESCRIPTION ,rDirStaff.getChargeDescription())
			.where(RDIR_STAFF.ID.eq(rDirStaff.getId()))
			.execute();
		ctx.log().info("UPDATE RDIR_STAFF ( registry: "+ rDirStaff.getRegistry() +") id: " + rDirStaff.getId() + ". (" + count + " rows)");
		rDirStaff.setDirty(false);
		return rDirStaff;
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		RDirStaffValidation.validateDeletion(ctx, id);
		int count = ctx.getDslContext().delete(RDIR_STAFF)
			.where(RDIR_STAFF.ID.eq(id))
			.execute();
		ctx.log().info("DELETE RDIR_STAFF id:" + id + " ("+count+" rows)");
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
	public static RDirStaff getRandom(AONContext ctx, RDirStaffFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RDirStaffFiller())
			.findFirst()
			.orElse(null);
	}
	

}
