package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsMod347.FS_MOD347;
import static com.esferalia.aon.jooq.tables.FsMod349.FS_MOD349;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel180.FS_MODEL180;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMatrixDAO {
	
	public static final String PARAM_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	public static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";
	
	public static FiscalModelMatrix getModelsPanel(AONContext ctx, int domain,int year,int user) {
		FiscalModelMatrix matrix = new FiscalModelMatrix();
		Stream.concat(
				getConfiguratedModels(ctx, domain, year, user)
				,getModels(ctx, domain, year, user)) 
				.forEach( item -> matrix.add(item));
		return matrix;
	}
	public static LinkedList<IFiscalModel> getAllModels(AONContext ctx, int domain,int year,int user) {
		LinkedList<IFiscalModel> list = new LinkedList<IFiscalModel>();
		getModels(ctx, domain, year, user) 
			.forEach( item -> list.add(item));
		return list;
		
	}
	public static LinkedList<IFiscalModel> getAllModels(AONContext ctx, int domain,int user) {
		LinkedList<IFiscalModel> list = new LinkedList<IFiscalModel>();
		getModels(ctx, domain, Integer.MIN_VALUE, user) 
			.forEach( item -> list.add(item));
		return list;
		
	}
	
	private static Stream<IFiscalModel> getConfiguratedModels(AONContext ctx, int domain,int year,int user) {
		return fillConfiguratedModels(ctx, domain, user);
	}
	private static Stream<IFiscalModel> getModels(AONContext ctx, int domain,int year,int user) {
		return Stream.concat(
				Stream.concat(
				Stream.concat(
				Stream.concat(
				Stream.concat(
				Stream.concat(
				Stream.concat(
				 fillModel303(ctx, domain, year, user)
				,fillFiscalModel(ctx, domain, year, user))
				,fillModel347(ctx, domain, year, user))
				,fillModel349(ctx, domain, year, user))
				,fillModel180(ctx, domain, year, user))
				,fillModel190(ctx, domain, year, user))
				,fillModel390(ctx, domain, year, user))
				,fillModel200(ctx, domain, year, user))
			.sorted( (a,b) -> {
				int ret = Integer.compare(a.getYear(), b.getYear());
				if (ret == 0) ret = Integer.compare(a.getDomain(), b.getDomain());
				if (ret == 0) ret = Integer.compare(a.getModel().ordinal(), b.getModel().ordinal());
				if (ret == 0) {
					if (a.getAdministration() == null && b.getAdministration() == null) return 0;
					if (a.getAdministration() != null && b.getAdministration() == null) return -1;
					if (a.getAdministration() == null && b.getAdministration() != null) return 1;
					return Integer.compare(a.getAdministration().ordinal(), b.getAdministration().ordinal()); 
				}
				return ret;
					});
		
	}
	
	private static Stream<IFiscalModel> fillModel303(AONContext ctx, int domain, int year,int user) {
		
			return ctx.getDslContext()
				.select(FS_VAT.YEAR ,FS_VAT.STATUS, FS_VAT.PERIOD,FS_VAT_DECLARATION.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_VAT)
				.join(DOMAIN).on( FS_VAT.DOMAIN.equal(DOMAIN.ID))
				.leftOuterJoin(FS_VAT_DECLARATION).on( FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(FS_VAT.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
							.from(USER_SCOPE)
							.where(USER_SCOPE.USER_ID.equal(user))
							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
							)))
					.and(year==Integer.MIN_VALUE
							?FS_VAT.YEAR.equal(FS_VAT.YEAR)
							:FS_VAT.YEAR.equal(year)		
						)
				.orderBy(FS_VAT.PERIOD)
				.fetch()
				.map( rec -> { FiscalModel item = 
					new FiscalModel()
						.setDomain(rec.getValue(DOMAIN.ID))
						.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
						.setYear(rec.getValue(FS_VAT.YEAR))
						.setPeriod( Period.values()[rec.getValue(FS_VAT.PERIOD)])
						.setStatus(rec.getValue(FS_VAT_DECLARATION.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
						.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION)))
						;
					FiscalModelType model = FiscalModelType.M303_RG;
					if (item.getPeriod() == Period.YEAR ) {
						model = FiscalModelType.M390_HF;
					}
					item.setModel( model );
					return (IFiscalModel) item;
				} )
			.stream();
	}
	
 	 private static Stream<IFiscalModel> fillFiscalModel(AONContext ctx, int domain, int year,int user) {
 		return ctx.getDslContext()
			.select(FS_MODEL.ID,FS_MODEL.YEAR, FS_MODEL.STATUS, FS_MODEL.DOCUMENT, FS_MODEL.NAME,FS_MODEL.SURNAME
					,FS_MODEL.PERIOD, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION ,DOMAIN.ID,DOMAIN.DESCRIPTION)
			.from(FS_MODEL)
			.join(DOMAIN).onKey()
			.where(FS_MODEL.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MODEL.YEAR.equal(FS_MODEL.YEAR)
						:FS_MODEL.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
								.from(USER_SCOPE)
								.where(USER_SCOPE.USER_ID.equal(user))
								.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
								)))
				.and(FS_MODEL.MODEL.ne("3O3"))
 			.orderBy(FS_MODEL.YEAR)
			.fetch()
			.map( rec -> (IFiscalModel) new FiscalModel()
				.setId(rec.getValue(FS_MODEL.ID))
				.setDomain(rec.getValue(DOMAIN.ID))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setDocument(rec.getValue(FS_MODEL.DOCUMENT))
				.setYear(rec.getValue(FS_MODEL.YEAR))
				.setName(AonStringUtils.trim(AonStringUtils.isEmpty(rec.getValue(FS_MODEL.NAME))
							?AonStringUtils.EMPTY
							:(rec.getValue(FS_MODEL.NAME) + AonStringUtils.SPACE + rec.getValue(FS_MODEL.SURNAME))
						))
				.setPeriod(Period.values()[rec.getValue(FS_MODEL.PERIOD)])
				.setModel( "303".equals(rec.getValue(FS_MODEL.MODEL))?FiscalModelType.M303_RS:FiscalModelType.valueOf("M" + rec.getValue(FS_MODEL.MODEL) ) )
				.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MODEL.ADMINISTRATION)))
				.setStatus(rec.getValue(FS_MODEL.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
					)
			.stream();
	}
	
	private static Stream<IFiscalModel> fillModel349(AONContext ctx, int domain, int year, int user ) {
		return ctx.getDslContext()
				.select(FS_MOD349.YEAR,FS_MOD349.STATUS, FS_MOD349.YEAR, FS_MOD349.PERIOD,FS_MOD349.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MOD349)
				.join(DOMAIN).onKey()
				.where(FS_MOD349.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MOD349.YEAR.equal(FS_MOD349.YEAR)
						:FS_MOD349.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MOD349.YEAR)
				.fetch()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MOD349.ADMINISTRATION)))
					.setPeriod(Period.values()[rec.getValue(FS_MOD349.PERIOD)])
					.setYear(rec.getValue(FS_MOD349.YEAR))
					.setModel( FiscalModelType.M349)
					.setStatus(rec.getValue(FS_MOD349.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
					)
				.stream();
	}

 	 private static Stream<IFiscalModel> fillModel347(AONContext ctx, int domain, int year, int user ) {
		return ctx.getDslContext()
				.select(FS_MOD347.STATUS, FS_MOD347.YEAR,FS_MOD347.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MOD347)
				.join(DOMAIN).onKey()
				.where(FS_MOD347.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MOD347.YEAR.equal(FS_MOD347.YEAR)
						:FS_MOD347.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MOD347.YEAR)
				.fetch()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MOD347.ADMINISTRATION)))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setPeriod(Period.YEAR)
					.setYear(rec.getValue(FS_MOD347.YEAR))
					.setModel( FiscalModelType.M347)
					.setStatus(rec.getValue(FS_MOD347.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
				)
				.stream();
	}

	private static Stream<IFiscalModel> fillModel180(AONContext ctx, int domain, int year, int user) {
		return ctx.getDslContext()
				.select(FS_MODEL180.STATUS, FS_MODEL180.YEAR,FS_MODEL180.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL180)
				.join(DOMAIN).onKey()
				.where(FS_MODEL180.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MODEL180.YEAR.equal(FS_MODEL180.YEAR)
						:FS_MODEL180.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL180.YEAR)
				.fetch()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MODEL180.ADMINISTRATION)))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setModel( FiscalModelType.M180)
					.setYear(rec.getValue(FS_MODEL180.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(rec.getValue(FS_MODEL180.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
				)
				.stream();
	}

	private static Stream<IFiscalModel> fillModel190(AONContext ctx, int domain, int year, int user) {
		return ctx.getDslContext()
				.select(FS_MODEL190.STATUS, FS_MODEL190.YEAR,FS_MODEL190.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL190)
				.join(DOMAIN).onKey()
				.where(FS_MODEL190.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MODEL190.YEAR.equal(FS_MODEL190.YEAR)
						:FS_MODEL190.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL190.YEAR)
				.fetch()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MODEL190.ADMINISTRATION)))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setModel( FiscalModelType.M190)
					.setPeriod(Period.YEAR)
					.setYear(rec.getValue(FS_MODEL190.YEAR))
					.setStatus(rec.getValue(FS_MODEL190.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
				)
				.stream();
	}

	private static Stream<IFiscalModel> fillModel390(AONContext ctx, int domain, int year, int user) {
		return ctx.getDslContext()
				.select(FS_MODEL390.STATUS, FS_MODEL390.YEAR,FS_MODEL390.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL390)
				.join(DOMAIN).onKey()
				.where(FS_MODEL390.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MODEL390.YEAR.equal(FS_MODEL390.YEAR)
						:FS_MODEL390.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL390.YEAR)
				.fetch()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MODEL390.ADMINISTRATION)))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setModel( FiscalModelType.M390)
					.setPeriod(Period.YEAR)
					.setYear(rec.getValue(FS_MODEL390.YEAR))
					.setStatus(rec.getValue(FS_MODEL390.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
				)
				.stream();
	}

	private static Stream<IFiscalModel> fillModel200(AONContext ctx, int domain, int year, int user) {
		return ctx.getDslContext()
				.select(FS_MODEL200.ID,FS_MODEL200.YEAR,FS_MODEL200.ADMINISTRATION,FS_MODEL200.COMPLEMENTARY,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL200)
				.join(DOMAIN).onKey()
				.where(FS_MODEL200.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(year==Integer.MIN_VALUE
						?FS_MODEL200.YEAR.equal(FS_MODEL200.YEAR)
						:FS_MODEL200.YEAR.equal(year)		
					)
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL200.YEAR)
				.fetch()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setId( rec.getValue(FS_MODEL200.ID) )
					.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MODEL200.ADMINISTRATION)))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setModel( FiscalModelType.M200)
					.setPeriod(Period.YEAR)
					.setYear(rec.getValue(FS_MODEL200.YEAR))
					.setComplementary(rec.getValue(FS_MODEL200.COMPLEMENTARY)==1)
					.setStatus(FiscalStatus.PENDING) // TODO ??
				)
				.stream();
	}

	public static Stream<IFiscalModel> fillConfiguratedModels(AONContext ctx, int domain, int user) {
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		return ctx.getDslContext()
			.select(APP_PARAM.NAME,APP_PARAM.VALUE,DOMAIN.ID,DOMAIN.DESCRIPTION)
			.from(APP_PARAM)
			.join(DOMAIN).onKey()
			.where(APP_PARAM.DOMAIN.equal(domain))
				.or(DOMAIN.PARENT.equal(domain))
				.and(APP_PARAM.NAME.like(PARAM_PREFIX_LIKE))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(user))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
							)))
			.orderBy(APP_PARAM.NAME)
			.fetch()
			.map( rec ->
					AonStringUtils.containsAny(rec.getValue(APP_PARAM.VALUE), "YQM")
						? (IFiscalModel) new FiscalModel()
							.setDomain(rec.getValue(DOMAIN.ID))
							.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
							.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY))
							.setModel( FiscalModelType.valueOf(AonStringUtils.substringAfter(rec.getValue(APP_PARAM.NAME),PARAM_PREFIX)) )
							.setStatus( FiscalStatus.MISSING)
							.setPeriod("Y".equals(rec.getValue(APP_PARAM.VALUE))
										?Period.YEAR
										:"Q".equals(rec.getValue(APP_PARAM.VALUE))
											?Period.T1
											:Period.M01
									  )
						:null
					
				 )
			.stream()
			.filter( item ->  item != null );
	}
	
}
