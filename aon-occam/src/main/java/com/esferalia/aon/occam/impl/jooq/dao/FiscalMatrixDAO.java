package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsMod347.FS_MOD347;
import static com.esferalia.aon.jooq.tables.FsMod349.FS_MOD349;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel180.FS_MODEL180;
import static com.esferalia.aon.jooq.tables.FsModel184.FS_MODEL184;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel193.FS_MODEL193;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMatrixDAO {
	private static String ID = "ID";
	private static String YEAR = "YEAR";
	private static String STATUS = "STATUS";
	private static String DOCUMENT = "DOCUMENT";
	private static String NAME = "NAME";
	private static String SURNAME = "SURNAME";
	private static String PERIOD = "PERIOD";
	private static String MODEL = "MODEL";
	private static String ADMINISTRATION = "ADMINISTRATION";
	private static String COMPLEMENTARY = "COMPLEMENTARY";
	private static String REPLACEMENT = "REPLACEMENT";
	private static String DOMAIN_ID = "DOMAIN_ID";
	private static String DOMAIN_NAME = "DOMAIN_NAME";
	
	
	public static final String PARAM_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	public static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";
	
	public static LinkedList<IFiscalModel> getModelsPanel(AONContext ctx, int domain,FiscalMatrixParams params,int user) {
		
		FiscalModelType modelType = FiscalModelType.safeValueOf( params.getModel());
		return 
				Stream.concat(
						(params.isMadeModelsVisible()
							?getModels(ctx, domain, params, user)
							:Stream.empty())
						,(params.isConfiguredVisible()
							?getConfiguratedModels(ctx, domain, params, user)
							:Stream.empty())
			)
			.filter(mod -> modelType == null || mod.getModel() == modelType)
			.filter(mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
			.sorted( (a,b) -> {
				int ret = AonStringUtils.compare( a.getDomainName(), b.getDomainName());
				if (ret == 0) ret = Integer.compare(a.getModel().ordinal(), b.getModel().ordinal());
				if (ret == 0) {
					if (a.getAdministration() == null && b.getAdministration() == null) return 0;
					if (a.getAdministration() == null && b.getAdministration() != null) return -1;
					if (a.getAdministration() != null && b.getAdministration() == null) return 1;
					return Integer.compare(a.getAdministration().ordinal(), b.getAdministration().ordinal()); 
				}
				return ret;					
			})
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static Stream<IFiscalModel> getConfiguratedModels(AONContext ctx, int domain,FiscalMatrixParams params,int user) {
		return fillConfiguratedModels(ctx, domain, user, params);
	}
	
	private static Stream<IFiscalModel> getModels(AONContext ctx, int domain,FiscalMatrixParams params,int user) {
		return 
				Stream.concat(
					Stream.concat(
						Stream.concat(
							Stream.concat(
								Stream.concat(
									Stream.concat(
										Stream.concat(
											Stream.concat(
													Stream.concat(
															 fillOldModel303(ctx, domain, params, user)
															,fillFiscalModel(ctx, domain, params, user))
													,fillModel347(ctx, domain, params, user))
											,fillModel349(ctx, domain, params, user))
										,fillModel180(ctx, domain, params, user))
									,fillModel184(ctx, domain, params, user))
								,fillModel190(ctx, domain, params, user))
							,fillModel193(ctx, domain, params, user))
						,fillModel390(ctx, domain, params, user))
					,fillModel200(ctx, domain, params, user))
				;
		
	}
	
	public static Stream<IFiscalModel> fillConfiguratedModels(AONContext ctx, int domain, int user,FiscalMatrixParams params) {
		FiscalParameters fiscalParams = AppParamDAO.getFiscalParameters(ctx);
		return ctx.getDslContext()
			.select( APP_PARAM.NAME
					,APP_PARAM.VALUE
					,DOMAIN.ID
					,DOMAIN.DESCRIPTION)
			.from(APP_PARAM)
			.join(DOMAIN).on( APP_PARAM.DOMAIN.equal(DOMAIN.ID))
			.where(DOMAIN.PARENT.equal(domain))
				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
				.and(APP_PARAM.NAME.like(PARAM_PREFIX_LIKE))
			.orderBy(APP_PARAM.NAME)
			.fetch()
			.stream()
			.filter(rec -> AonStringUtils.containsAny(rec.getValue(APP_PARAM.VALUE), "YQM"))
			.map( rec -> new FiscalModel()
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setAdministration(fiscalParams.getAdministration(Administration.UNKNOWN)) 
					.setModel( fromAppParamName(rec.getValue(APP_PARAM.NAME)) )
					.setStatus( FiscalStatus.MISSING)
					.setPeriod("Y".equals(rec.getValue(APP_PARAM.VALUE))?Period.YEAR:"Q".equals(rec.getValue(APP_PARAM.VALUE))?Period.T1:Period.M01)
				 )
			;
	}
	private static FiscalModelType fromAppParamName( String name ) {
		String model = AonStringUtils.substringAfter(name,PARAM_PREFIX);
		FiscalModelType fmt = FiscalModelType.valueOf(model);
		if (fmt == FiscalModelType.M303_RG || fmt == FiscalModelType.M303_RS) {
			return FiscalModelType.M303;
		}
		return fmt;
	}

	private static Stream<IFiscalModel> fillOldModel303(AONContext ctx, int domain,FiscalMatrixParams params,int user) {
			return ctx.getDslContext()
				.select(
						 FS_VAT.ID 
						,FS_VAT.YEAR
						,FS_VAT.STATUS
						,FS_VAT.PERIOD
						,FS_VAT_DECLARATION.ADMINISTRATION
						,FS_VAT.COMPLEMENTARY
						,FS_VAT.REPLACEMENT
						,DOMAIN.ID
						,DOMAIN.DESCRIPTION
						)
				.from(FS_VAT)
				.join(DOMAIN).on( FS_VAT.DOMAIN.equal(DOMAIN.ID))
				.leftOuterJoin(FS_VAT_DECLARATION).on( FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(DOMAIN.PARENT.equal(domain))
					.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
					.and(FS_VAT.YEAR.equal(params.getYear()))
				.fetch()
				.stream()
				.map( rec -> { FiscalModel item = 
					new FiscalModel()
						.setId(rec.getValue(FS_VAT.ID))
						.setDomain(rec.getValue(DOMAIN.ID))
						.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
						.setYear(rec.getValue(FS_VAT.YEAR))
						.setPeriod( Period.safeValueOf(rec.getValue(FS_VAT.PERIOD)) )
						.setStatus(rec.getValue(FS_VAT_DECLARATION.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
						.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION)))
						.setComplementary(rec.getValue(FS_VAT.COMPLEMENTARY)==1 )
						.setReplacement(rec.getValue(FS_VAT.REPLACEMENT)==1 )
						;
					FiscalModelType model = FiscalModelType.M303_RG;
					if (item.getPeriod() == Period.YEAR ) {
						model = FiscalModelType.M390_HF;
					}
					item.setModel( model );
					return (IFiscalModel) item;
				} );
	}
	
 	 private static Stream<IFiscalModel> fillFiscalModel(AONContext ctx, int domain,FiscalMatrixParams params,int user) {
 		return ctx.getDslContext()
			.select(FS_MODEL.ID
					,FS_MODEL.YEAR, FS_MODEL.STATUS, FS_MODEL.DOCUMENT, FS_MODEL.NAME,FS_MODEL.SURNAME
					,FS_MODEL.PERIOD, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION
					,FS_MODEL.COMPLEMENTARY,FS_MODEL.REPLACEMENT
					,DOMAIN.ID,DOMAIN.DESCRIPTION)
			.from(FS_MODEL)
			.join(DOMAIN).on( FS_MODEL.DOMAIN.equal(DOMAIN.ID))
			.where(DOMAIN.PARENT.equal(domain))
				.and(FS_MODEL.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
				.and(FS_MODEL.MODEL.ne("3O3"))
			.fetch()
			.stream()
			.map( rec -> (IFiscalModel) new FiscalModel()
				.setId(rec.getValue(FS_MODEL.ID))
				.setDomain(rec.getValue(DOMAIN.ID))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setDocument(rec.getValue(FS_MODEL.DOCUMENT))
				.setName(rec.getValue(FS_MODEL.NAME))
				.setSurname(rec.getValue(FS_MODEL.SURNAME))
				.setYear(rec.getValue(FS_MODEL.YEAR))
				.setPeriod(Period.safeValueOf(rec.getValue(FS_MODEL.PERIOD)))
				.setAdministration(Administration.safeValueOf(rec.getValue(FS_MODEL.ADMINISTRATION)))
				.setStatus(FiscalStatus.safeValueOf( rec.getValue(FS_MODEL.STATUS)))
				.setModel(FiscalModelType.M303_RS.getValue().equals(rec.getValue(FS_MODEL.MODEL)) 
						?FiscalModelType.M303_RS
						:FiscalModelType.safeValueOf(rec.getValue(FS_MODEL.MODEL)) )
				.setComplementary(rec.getValue(FS_MODEL.COMPLEMENTARY)==1 )
				.setReplacement(rec.getValue(FS_MODEL.REPLACEMENT)==1 )
				);
	}
	
 	 private static Stream<IFiscalModel> fillModel347(AONContext ctx, int domain, FiscalMatrixParams params, int user ) {
		return ctx.getDslContext()
				.select(FS_MOD347.ID, FS_MOD347.STATUS, FS_MOD347.YEAR,FS_MOD347.ADMINISTRATION
						,FS_MOD347.DOCUMENT, FS_MOD347.NAME
						,FS_MOD347.COMPLEMENTARY, FS_MOD347.REPLACEMENT
						,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MOD347)
				.join(DOMAIN).on( FS_MOD347.DOMAIN.equal(DOMAIN.ID))
				.where(DOMAIN.PARENT.equal(domain))
				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
				.and(FS_MOD347.YEAR.equal(params.getYear()))
				.fetch()
				.stream()
				.map( rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MOD347.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MOD347.DOCUMENT))
					.setName(rec.getValue(FS_MOD347.NAME))
					.setYear(rec.getValue(FS_MOD347.YEAR))
					.setPeriod(Period.YEAR)
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MOD347.ADMINISTRATION)))
					.setStatus(FiscalStatus.safeValueOf( rec.getValue(FS_MOD347.STATUS)))
					.setModel( FiscalModelType.M347)
					.setComplementary(rec.getValue(FS_MOD347.COMPLEMENTARY)==1 )
					.setReplacement(rec.getValue(FS_MOD347.REPLACEMENT)==1 )
				);
	}

 	 private static Stream<IFiscalModel> fillModel349(AONContext ctx, int domain, FiscalMatrixParams params, int user ) {
 		 return ctx.getDslContext()
 				 .select(FS_MOD349.ID,FS_MOD349.YEAR,FS_MOD349.STATUS, FS_MOD349.YEAR, FS_MOD349.PERIOD,FS_MOD349.ADMINISTRATION
 						,FS_MOD349.DOCUMENT, FS_MOD349.NAME
 						,FS_MOD349.COMPLEMENTARY, FS_MOD349.REPLACEMENT
 						,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				 .from(FS_MOD349)
 				 .join(DOMAIN).on( FS_MOD349.DOMAIN.equal(DOMAIN.ID))
 				 .where(DOMAIN.PARENT.equal(domain))
 				 .and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				 .and(FS_MOD349.YEAR.equal(params.getYear()))
 				 .fetch()
 				 .stream()
 				 .map( rec -> (IFiscalModel) new FiscalModel()
 						 .setId(rec.getValue(FS_MOD349.ID))
 						 .setDomain(rec.getValue(DOMAIN.ID))
 						 .setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
 						 .setDocument(rec.getValue(FS_MOD349.DOCUMENT))
 						 .setName(rec.getValue(FS_MOD349.NAME))
 						 .setYear(rec.getValue(FS_MOD349.YEAR))
 						 .setPeriod(Period.safeValueOf(rec.getValue(FS_MOD349.PERIOD)))
 						 .setAdministration(Administration.safeValueOf( rec.getValue(FS_MOD349.ADMINISTRATION)))
 						 .setStatus( FiscalStatus.safeValueOf( rec.getValue(FS_MOD349.STATUS) ) ) 
 						 .setModel( FiscalModelType.M349 )
 						 .setComplementary(rec.getValue(FS_MOD349.COMPLEMENTARY)==1 )
 						 .setReplacement(rec.getValue(FS_MOD349.REPLACEMENT)==1 )
				 );
 	 }
 	 
 	 private static Stream<IFiscalModel> fillModel180(AONContext ctx, int domain, FiscalMatrixParams params, int user) {
 		 return ctx.getDslContext()
 				.select(FS_MODEL180.ID,FS_MODEL180.STATUS, FS_MODEL180.YEAR,FS_MODEL180.ADMINISTRATION
 						 ,FS_MODEL180.DOCUMENT,FS_MODEL180.NAME
  						 ,FS_MODEL180.COMPLEMENTARY, FS_MODEL180.REPLACEMENT
 						 ,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				.from(FS_MODEL180)
 				.join(DOMAIN).on( FS_MODEL180.DOMAIN.equal(DOMAIN.ID))
 				.where(DOMAIN.PARENT.equal(domain))
 				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				.and(FS_MODEL180.YEAR.equal(params.getYear()))
 				.fetch()
 				.stream()
 				.map(rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MODEL180.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MODEL180.DOCUMENT))
					.setName(rec.getValue(FS_MODEL180.NAME))
					.setYear(rec.getValue(FS_MODEL180.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL180.STATUS)))
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MODEL180.ADMINISTRATION)))
					.setModel( FiscalModelType.M180 )
					.setComplementary(rec.getValue(FS_MODEL180.COMPLEMENTARY)==1 )
					.setReplacement(rec.getValue(FS_MODEL180.REPLACEMENT)==1 )
				 );
 	 }
 	 
 	 private static Stream<IFiscalModel> fillModel184(AONContext ctx, int domain, FiscalMatrixParams params, int user) {
 		 return ctx.getDslContext()
 				.select(FS_MODEL184.ID,FS_MODEL184.STATUS, FS_MODEL184.YEAR,FS_MODEL184.ADMINISTRATION
 						,FS_MODEL184.DOCUMENT,FS_MODEL184.NAME
 						,FS_MODEL184.COMPLEMENTARY, FS_MODEL184.REPLACEMENT
 						,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				.from(FS_MODEL184)
 				.join(DOMAIN).on( FS_MODEL184.DOMAIN.equal(DOMAIN.ID))
 				.where(DOMAIN.PARENT.equal(domain))
 				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				.and(FS_MODEL184.YEAR.equal(params.getYear()))
 				.fetch()
 				.stream()
 				.map(rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MODEL184.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MODEL184.DOCUMENT))
					.setName(rec.getValue(FS_MODEL184.NAME))
					.setYear(rec.getValue(FS_MODEL184.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL184.STATUS)))
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MODEL184.ADMINISTRATION)))
					.setModel( FiscalModelType.M184 )
					.setComplementary(rec.getValue(FS_MODEL184.COMPLEMENTARY)==1 )
					.setReplacement(rec.getValue(FS_MODEL184.REPLACEMENT)==1 )
				 );
 	 }

 	 private static Stream<IFiscalModel> fillModel190(AONContext ctx, int domain, FiscalMatrixParams params, int user) {
 		 return ctx.getDslContext()
 				.select(FS_MODEL190.ID,FS_MODEL190.STATUS, FS_MODEL190.YEAR,FS_MODEL190.ADMINISTRATION
 						 ,FS_MODEL190.DOCUMENT,FS_MODEL190.NAME
 						,FS_MODEL190.COMPLEMENTARY, FS_MODEL190.REPLACEMENT
 						 ,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				.from(FS_MODEL190)
 				.join(DOMAIN).on( FS_MODEL190.DOMAIN.equal(DOMAIN.ID))
 				.where(DOMAIN.PARENT.equal(domain))
 				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				.and(FS_MODEL190.YEAR.equal(params.getYear()))
 				.fetch()
 				.stream()
 				.map(rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MODEL190.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MODEL190.DOCUMENT))
					.setName(rec.getValue(FS_MODEL190.NAME))
					.setYear(rec.getValue(FS_MODEL190.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL190.STATUS)))
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MODEL190.ADMINISTRATION)))
					.setModel( FiscalModelType.M190 )
					.setComplementary(rec.getValue(FS_MODEL190.COMPLEMENTARY)==1 )
					.setReplacement(rec.getValue(FS_MODEL190.REPLACEMENT)==1 )
				 );
 	 }

 	 private static Stream<IFiscalModel> fillModel193(AONContext ctx, int domain, FiscalMatrixParams params, int user) {
 		 return ctx.getDslContext()
 				.select(FS_MODEL193.ID,FS_MODEL193.STATUS, FS_MODEL193.YEAR,FS_MODEL193.ADMINISTRATION
 						 ,FS_MODEL193.DOCUMENT,FS_MODEL193.NAME
 						,FS_MODEL193.COMPLEMENTARY, FS_MODEL193.REPLACEMENT
 						 ,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				.from(FS_MODEL193)
 				.join(DOMAIN).on( FS_MODEL193.DOMAIN.equal(DOMAIN.ID))
 				.where(DOMAIN.PARENT.equal(domain))
 				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				.and(FS_MODEL193.YEAR.equal(params.getYear()))
 				.fetch()
 				.stream()
 				.map(rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MODEL193.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MODEL193.DOCUMENT))
					.setName(rec.getValue(FS_MODEL193.NAME))
					.setYear(rec.getValue(FS_MODEL193.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL193.STATUS)))
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MODEL193.ADMINISTRATION)))
					.setModel( FiscalModelType.M193 )
					.setComplementary(rec.getValue(FS_MODEL193.COMPLEMENTARY)==1 )
					.setReplacement(rec.getValue(FS_MODEL193.REPLACEMENT)==1 )
				 );
 	 }

 	 private static Stream<IFiscalModel> fillModel390(AONContext ctx, int domain, FiscalMatrixParams params, int user) {
 		 return ctx.getDslContext()
 				.select(FS_MODEL390.ID,FS_MODEL390.STATUS, FS_MODEL390.YEAR,FS_MODEL390.ADMINISTRATION
 						 ,FS_MODEL390.DOCUMENT,FS_MODEL390.NAME
 						,FS_MODEL390.COMPLEMENTARY, FS_MODEL390.REPLACEMENT
 						 ,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				.from(FS_MODEL390)
 				.join(DOMAIN).on( FS_MODEL390.DOMAIN.equal(DOMAIN.ID))
 				.where(DOMAIN.PARENT.equal(domain))
 				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				.and(FS_MODEL390.YEAR.equal(params.getYear()))
 				.fetch()
 				.stream()
 				.map(rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MODEL390.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MODEL390.DOCUMENT))
					.setName(rec.getValue(FS_MODEL390.NAME))
					.setYear(rec.getValue(FS_MODEL390.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL390.STATUS)))
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MODEL390.ADMINISTRATION)))
					.setModel( FiscalModelType.M390 )
					.setComplementary(rec.getValue(FS_MODEL390.COMPLEMENTARY)==1 )
					.setReplacement(rec.getValue(FS_MODEL390.REPLACEMENT)==1 )
				 );
 	 }
 	 
 	 private static Stream<IFiscalModel> fillModel200(AONContext ctx, int domain, FiscalMatrixParams params, int user) {
 		 return ctx.getDslContext()
 				.select(FS_MODEL200.ID,FS_MODEL200.STATUS, FS_MODEL200.YEAR,FS_MODEL200.ADMINISTRATION
 						 ,FS_MODEL200.DOCUMENT,FS_MODEL200.NAME
 						,FS_MODEL200.COMPLEMENTARY
 						 ,DOMAIN.ID,DOMAIN.DESCRIPTION)
 				.from(FS_MODEL200)
 				.join(DOMAIN).on( FS_MODEL200.DOMAIN.equal(DOMAIN.ID))
 				.where(DOMAIN.PARENT.equal(domain))
 				.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
 				.and(FS_MODEL200.YEAR.equal(params.getYear()))
 				.fetch()
 				.stream()
 				.map(rec -> (IFiscalModel) new FiscalModel()
					.setId(rec.getValue(FS_MODEL200.ID))
					.setDomain(rec.getValue(DOMAIN.ID))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setDocument(rec.getValue(FS_MODEL200.DOCUMENT))
					.setName(rec.getValue(FS_MODEL200.NAME))
					.setYear(rec.getValue(FS_MODEL200.YEAR))
					.setPeriod(Period.YEAR)
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL200.STATUS)))
					.setAdministration(Administration.safeValueOf( rec.getValue(FS_MODEL200.ADMINISTRATION)))
					.setModel( FiscalModelType.M200 )
					.setComplementary(rec.getValue(FS_MODEL200.COMPLEMENTARY)==1 )
					.setReplacement(false )
				 );
 	 }
	
}
