package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;

import java.util.Optional;

import org.jooq.Record;
import org.jooq.SelectOnConditionStep;

import net.aonsolutions.occam.api.model.RegistryPayMethod;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.PayMethodHandler.PayMethodFiller;
import net.aonsolutions.occam.impl.handler.RegistryBankHandler.RegistryBankFiller;

class RegistryPayMethodHandler {
	
	private RegistryPayMethodHandler() {
	}
	
	static class RegistryPayMethodFiller extends Filler<RegistryPayMethod> {

		@Override
		public RegistryPayMethod apply(Record r) {
			return build(r);
		}
		
		public static RegistryPayMethod build(Record r) {
			return new RegistryPayMethod()
				.setId(getValue(r, RPAYMETHOD.ID))
				.setDomain(getValue(r, RPAYMETHOD.DOMAIN))
				.setRegistry(getValue(r, RPAYMETHOD.REGISTRY))
				.setPayMethod(PayMethodFiller.build(r) )
				.setRegistryBank(RegistryBankFiller.build(r))
				.setNumberOfPymnts(getValue(r, RPAYMETHOD.NUMBER_OF_PYMNTS))
				.setDaysToFirstPymnt(getValue(r, RPAYMETHOD.DAYS_TO_FIRST_PYMNT))
				.setDaysBetwenPymnts(getValue(r, RPAYMETHOD.DAYS_BETWEEN_PYMNTS))
				.setPymntDays(getValue(r, RPAYMETHOD.PYMNT_DAYS))
			;
		}
		
	}

	private static SelectOnConditionStep<Record> select(AONContext ctx) {
		return ctx.getDslContext().select()
			.from(RPAYMETHOD)
			.join(PAY_METHOD).on(PAY_METHOD.ID.eq(RPAYMETHOD.PAY_METHOD))
			.leftOuterJoin(RBANK).on(RBANK.ID.eq(RPAYMETHOD.RBANK))
		;
	}
	
	static Optional<RegistryPayMethod> getByRegistry(AONContext ctx, Integer registryId) {
		return select(ctx)
			.where(RPAYMETHOD.REGISTRY.eq(registryId))
			.fetch()
			.stream()
			.map(new RegistryPayMethodFiller())
			.findFirst()
		;
	}
	
	/*
		
	private static final RegistryPayMethodPropertiesDAO RPAYMETHOD_PROPERTIES = new RegistryPayMethodPropertiesDAO();
	
	protected static class RegistryPayMethodPropertiesDAO implements RegistryPayMethodProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RegistryPayMethodFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryPayMethodFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.REGISTRY);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.PAY_METHOD);}
		@Override public Property<Integer> getRBankProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.RBANK);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.PYMNT_DAYS);}
	}

	
	public static class RegistryPayMethodAutoComplete {
	
		public static final BiConsumer<AONContext,RegistryPayMethod> COMPLETE_PAY_METHOD = (ctx, rpm) -> {
			if(rpm.getPayMethod().getId() == null) {
				PayMethodType pmt = rpm.getPayMethod().getType() != null
						? rpm.getPayMethod().getType() : PayMethodType.OTHER;
				PayMethod pm = PayMethodDAO.save(ctx, rpm.getPayMethod()
						.setDomain(rpm.getDomain())
						.setName(pmt.getDescription())
						.setType(pmt));
				rpm.setPayMethod(pm);
			}
		};

		public static void autoComplete(AONContext ctx, RegistryPayMethod rpm) {
			COMPLETE_PAY_METHOD
				.accept(ctx, rpm);
		}

	}

	
	private static class RegistryPayMethodValidation {
		
		private RegistryPayMethodValidation() {
	
		}
		
		public static final BiConsumer<AONContext,RegistryPayMethod> EMPTY_DOMAIN = (ctx, rpaymethod) -> {
			if (rpaymethod.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<AONContext,RegistryPayMethod> EMPTY_REGISTRY = (ctx, rpaymethod) -> {
			if (rpaymethod.getRegistry() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("registry")) ;
		};
		
		public static final BiConsumer<AONContext,RegistryPayMethod> EMPTY_PAY_METHOD = (ctx, rpaymethod) -> {
			if (rpaymethod.getPayMethod() == null) 
				throw new AonCoreException(AonError.EMPTY_DATA.format("pay method")) ;
		};
		
	
		

		public static void validate(AONContext ctx, RegistryPayMethod rpaymethod) throws AonCoreException {
				EMPTY_DOMAIN
				.andThen(EMPTY_REGISTRY)
				.andThen(EMPTY_PAY_METHOD)
				.accept(ctx,rpaymethod);
		}
		
	}

    private static SelectConditionStep<Record> select(AONContext ctx, RegistryPayMethodFilter filter) {
        return ctx.getDslContext().select()
                .from(RPAYMETHOD)
                .where(RPAYMETHOD_PROPERTIES.getConditions(filter));
    }
	
	public static RegistryPayMethod get(AONContext ctx, RegistryPayMethodFilter filter, Options... options){
        if(options.length > 0 && options[0].isFull())
            return getFull(ctx, filter);
	    return select(ctx,filter).limit(1)
				.fetch().stream().map(new RegistryPayMethodFiller())
				.findFirst().orElse(new RegistryPayMethod());
	}
	
	   public static RegistryPayMethod getFull(AONContext ctx, RegistryPayMethodFilter filter){
	       return selectFull(ctx,filter).limit(1)
                .fetch().stream().map(new RegistryPayMethodFiller())
                .findFirst().orElse(new RegistryPayMethod());
    }
	public static RegistryPayMethod save(AONContext ctx, RegistryPayMethod rpaymethod) {
		ctx.checkWrite();
		if(rpaymethod.getId() != null && rpaymethod.isRemoved()) { 
			delete(ctx, rpaymethod.getId());
			return rpaymethod;
		}
		if(!rpaymethod.isDirty()) return rpaymethod;
		RegistryPayMethodAutoComplete.autoComplete(ctx, rpaymethod);
		RegistryPayMethodValidation.validate(ctx, rpaymethod);
		return (rpaymethod.getId() == null)
				? insert(ctx, rpaymethod)
				: update(ctx, rpaymethod);
	}
	
	private static RegistryPayMethod insert(AONContext ctx, RegistryPayMethod rpaymethod){
		Integer id = ctx.getDslContext().insertInto(RPAYMETHOD)
			.set(RPAYMETHOD.DOMAIN, rpaymethod.getDomain())
			.set(RPAYMETHOD.REGISTRY, rpaymethod.getRegistry())
			.set(RPAYMETHOD.PAY_METHOD, rpaymethod.getPayMethod().getId())
			.set(RPAYMETHOD.RBANK, rpaymethod.getRbank().getId())
			.set(RPAYMETHOD.NUMBER_OF_PYMNTS, rpaymethod.getNumberOfPymnts())
			.set(RPAYMETHOD.DAYS_TO_FIRST_PYMNT, rpaymethod.getDaysToFirstPymnt())
			.set(RPAYMETHOD.DAYS_BETWEEN_PYMNTS, rpaymethod.getDaysBetwenPymnts())
			.set(RPAYMETHOD.PYMNT_DAYS, rpaymethod.getPymntDays())			
			.returning(RADDRESS.ID)
			.fetchOne()
			.getValue(RADDRESS.ID);
		rpaymethod.setId(id).setDirty(false);
		ctx.log().debug("INSERT REGISTRY PAY METHOD ( registry: {0}) id: {1}", rpaymethod.getRegistry(), rpaymethod.getId());
		return rpaymethod;
	}
	
	private static RegistryPayMethod update(AONContext ctx, RegistryPayMethod rpaymethod){
		int count = ctx.getDslContext().update(RPAYMETHOD)
			.set(RPAYMETHOD.DOMAIN, rpaymethod.getDomain())
			.set(RPAYMETHOD.REGISTRY, rpaymethod.getRegistry())
			.set(RPAYMETHOD.PAY_METHOD, rpaymethod.getPayMethod().getId())
			.set(RPAYMETHOD.RBANK, rpaymethod.getRbank().getId())
			.set(RPAYMETHOD.NUMBER_OF_PYMNTS, rpaymethod.getNumberOfPymnts())
			.set(RPAYMETHOD.DAYS_TO_FIRST_PYMNT, rpaymethod.getDaysToFirstPymnt())
			.set(RPAYMETHOD.DAYS_BETWEEN_PYMNTS, rpaymethod.getDaysBetwenPymnts())
			.set(RPAYMETHOD.PYMNT_DAYS, rpaymethod.getPymntDays())
			.where(RPAYMETHOD.ID.eq(rpaymethod.getId()))
			.execute();
		ctx.log().debug("UPDATE REGISTRY PAY METHOD ( registry: {0}) id: {1}. ({2} rows)", rpaymethod.getRegistry(), rpaymethod.getId(),count);
		rpaymethod.setDirty(false);
		return rpaymethod;
	}
	
	public static void delete(AONContext ctx, Integer id){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RPAYMETHOD)
			.where(RPAYMETHOD.ID.eq(id))
			.execute();
		ctx.log().debug("DELETE REGISTRY PAY METHOD id: {0} ({1} rows)",id,count);
	}
	
	public static int deleteByRegistry(AONContext ctx, Integer registry){
		ctx.checkWrite();
		int count = ctx.getDslContext().delete(RPAYMETHOD)
			.where(RPAYMETHOD.REGISTRY.eq(registry))
			.execute();
		ctx.log().debug("DELETE REGISTRY PAY METHOD registry: {0} ({1} rows)",registry,count);
		return count;
	}
	
	public static int delete(AONContext ctx, RegistryPayMethodFilter filter){
		ctx.checkWrite();
		return ctx.getDslContext().delete(RPAYMETHOD)
			.where(RPAYMETHOD_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	
	public static RegistryPayMethod getRandom(AONContext ctx, RegistryPayMethodFilter filter) {
		return select(ctx,filter)
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new RegistryPayMethodFiller())
			.findFirst()
			.orElse(null);
	}
	
*/
}
