package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.jooq.Record;
import org.jooq.SelectOnConditionStep;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;

import net.aonsolutions.occam.api.model.Company;
import net.aonsolutions.occam.api.model.CompanyFull;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.RegistryHandler.RegistryFiller;

class CompanyHandler {
	
	private CompanyHandler() {
	}

	static class CompanyFiller extends Filler<Company> {
		@Override
		public Company apply(Record r) {
			return buildCompany(r, REGISTRY);
		}
		
		public static Company buildCompany(Record r, com.esferalia.aon.jooq.tables.Registry registry) {
			return RegistryFiller.build(r, registry, Company::new)
				.setActive(AonEnumUtils.getBoolean(r.getValue(COMPANY.ACTIVE)))
				.seteInvoice(AonEnumUtils.getBoolean(r.getValue(COMPANY.E_INVOICE)))
				.setSurcharge(AonEnumUtils.getBoolean(r.getValue(COMPANY.SURCHARGE)))
				.setVatAccrualPayment(AonEnumUtils.getBoolean(r.getValue(COMPANY.VAT_ACCRUAL_PAYMENT)))
				.setWithholding(AonEnumUtils.getBoolean(r.getValue(COMPANY.WITHHOLDING)));
		}
	}
	
	private static SelectOnConditionStep<Record> select(AONContext ctx) {
		return ctx.getDslContext()
			.select()
			.from(COMPANY)
			.join(REGISTRY).on(REGISTRY.ID.eq(COMPANY.REGISTRY))
		;
	}

	static Optional<Company> getByDomain(AONContext ctx, Integer domainId){
		return select(ctx)
			.where( COMPANY.DOMAIN.eq(domainId))
			.fetch()
			.stream()
			.map(new CompanyFiller())
			.findFirst()
		;
	}

	static Optional<CompanyFull> getFull(AONContext ctx, Integer domainId){
		return CompanyHandler.getByDomain(ctx, domainId)
			.map( c -> new CompanyFull().setRegistry(c) )
			.map( cf ->  RegistryHandler.fillChilds(ctx, cf))
			.map( cf -> {
				RegistryDirStaffHandler.streamByRegistry(ctx, cf.getId()).forEach( rds -> cf.addDirStaff(rds));
				return cf;
			});  
	}
	
	static CompanyFull save(AONContext ctx, CompanyFull companyFull) {
		ctx.checkWrite();
		companyFull.setRegistry(CompanyHandler.save(ctx, companyFull.getRegistry()));
		RegistryHandler.saveChilds(ctx, companyFull);
		return getFull(ctx, companyFull.getId())
			.orElseThrow(() -> new AonCoreException("No se pudo recuperar el dato grabado."));
	}
	
	static Company save(AONContext ctx, Company company) {
		ctx.checkWrite();
		boolean nullId = (company.getId() == null); 
		company = RegistryHandler.save(ctx, company);
		if (nullId) {
			CompanyValidation.validateInsert(ctx, company);
			insert(ctx, company);
		} else {
			update(ctx, company);			
		}
		return company;
	}
	
	private static Company insert(AONContext ctx, Company company){
		ctx.getDslContext().insertInto(COMPANY)
			.set(COMPANY.REGISTRY,company.getId())
			.set(COMPANY.DOMAIN,company.getDomain())
			.set(COMPANY.ACTIVE,AonEnumUtils.getByte(company.isActive()))
			.set(COMPANY.SURCHARGE,AonEnumUtils.getByte(company.isSurcharge()))
			.set(COMPANY.WITHHOLDING,AonEnumUtils.getByte(company.isWithholding()))
			.set(COMPANY.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(company.isVatAccrualPayment()))
			.set(COMPANY.E_INVOICE,AonEnumUtils.getByte(company.iseInvoice()))
			.execute();
		ctx.log().debug("INSERT COMPANY id: {0}",company.getId());		
		return company;
	}
	private static Company update(AONContext ctx, Company company){
		int count = ctx.getDslContext().update(COMPANY)
			.set(COMPANY.DOMAIN,company.getDomain())
			.set(COMPANY.ACTIVE,AonEnumUtils.getByte(company.isActive()))
			.set(COMPANY.SURCHARGE,AonEnumUtils.getByte(company.isSurcharge()))
			.set(COMPANY.WITHHOLDING,AonEnumUtils.getByte(company.isWithholding()))
			.set(COMPANY.VAT_ACCRUAL_PAYMENT,AonEnumUtils.getByte(company.isVatAccrualPayment()))
			.set(COMPANY.E_INVOICE,AonEnumUtils.getByte(company.iseInvoice()))
			.where(COMPANY.REGISTRY.eq(company.getId()))
			.execute();
		ctx.log().debug("UPDATE COMPANY id: {0}. ({1} rows)",company.getId(),count);		
		return company;
	}
	
	static class CompanyValidation {
		
		private CompanyValidation() {
		}
		
		public static final BiConsumer<Company,AONContext> EMPTY_DOMAIN = (company,ctx) -> {
			if (company.getDomain() == null)
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		public static final BiConsumer<Company,AONContext> DUPLICATED_ROW = (company,ctx) -> {
			getByDomain(ctx, company.getDomain())
				.ifPresent(c -> {throw new AonCoreException(AonError.DUPLICATED_COMPANY_ROW.getMessage());});
		};

		public static void validateInsert(AONContext ctx, Company company) throws AonCoreException{
			EMPTY_DOMAIN
				.andThen(DUPLICATED_ROW)
				.accept(company, ctx);
		}

	}

}
