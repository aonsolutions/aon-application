package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrixItem;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrixItem.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMatrixDAO {
	
	public static final String PARAM_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	public static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";
	
	public static Stream<FiscalModelMatrixItem> getModelsPanel(AONContext ctx, int domain,int year,int user) {
		
		return Stream.concat(Stream.concat(
				fillConfiguratedModels(ctx, domain, user)
				,fillModel303(ctx, domain, year, user))
				,fillFiscalModel(ctx, domain, year, user))
				;
		
//		fillFiscalModel(ctx,list,params);
//		fillModel347(ctx,list,params);
//		fillModel349(ctx,list,params);
//		fillModel180(ctx,list,params);
//		fillModel190(ctx,list,params);
//		fillModel390(ctx,list,params);
//		fillModel200(ctx,list,params);
	}
	
	private static Stream<FiscalModelMatrixItem> fillModel303(AONContext ctx, int domain, int year,int user) {
			return ctx.getDslContext()
				.select(FS_VAT.STATUS, FS_VAT.PERIOD,FS_VAT_DECLARATION.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
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
					.and(FS_VAT.YEAR.equal(year))
				.orderBy(FS_VAT.PERIOD)
				.fetch()
				.map( rec -> { FiscalModelMatrixItem item = 
					new FiscalModelMatrixItem()
						.setDomainId(rec.getValue(DOMAIN.ID))
						.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
						.setYear(year)
						.setPeriod( Period.values()[rec.getValue(FS_VAT.PERIOD)])
						.setStatus(rec.getValue(FS_VAT_DECLARATION.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
						.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_VAT_DECLARATION.ADMINISTRATION)))
						;
					FiscalModel model = FiscalModel.M303_RG;
					if (item.getPeriod() == Period.YEAR ) {
						model = FiscalModel.M390_HF;
					}
					item.setModel( model );
					return item;
				} )
			.stream();
	}
	
 	 private static Stream<FiscalModelMatrixItem> fillFiscalModel(AONContext ctx, int domain, int year,int user) {
 		return ctx.getDslContext()
			.select(FS_MODEL.STATUS, FS_MODEL.DOCUMENT, FS_MODEL.NAME,FS_MODEL.SURNAME
						,FS_MODEL.PERIOD, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION ,DOMAIN.ID,DOMAIN.DESCRIPTION)
			.from(FS_MODEL)
			.join(DOMAIN).onKey()
			.where(FS_MODEL.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MODEL.YEAR.equal(year))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.getDslContext().select(USER_SCOPE.SCOPE)
								.from(USER_SCOPE)
								.where(USER_SCOPE.USER_ID.equal(user))
								.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
								)))
 			.orderBy(FS_MODEL.YEAR)
			.fetch()
			.map( rec -> new FiscalModelMatrixItem()
				.setDomainId(rec.getValue(DOMAIN.ID))
				.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
				.setDocument(rec.getValue(FS_MODEL.DOCUMENT))
				.setYear(year)
				.setName(AonStringUtils.trim(AonStringUtils.isEmpty(rec.getValue(FS_MODEL.NAME))
							?AonStringUtils.EMPTY
							:(rec.getValue(FS_MODEL.NAME) + AonStringUtils.SPACE + rec.getValue(FS_MODEL.SURNAME))
						))
				.setPeriod(Period.values()[rec.getValue(FS_MODEL.PERIOD)])
				.setModel( "303".equals(rec.getValue(FS_MODEL.MODEL))?FiscalModel.M303_RS:FiscalModel.valueOf("M" + rec.getValue(FS_MODEL.MODEL) ) )
				.setAdministration(AonEnumUtils.enumValue(Administration.class, rec.getValue(FS_MODEL.ADMINISTRATION)))
				.setStatus(rec.getValue(FS_MODEL.STATUS)==1?FiscalStatus.FINISHED:FiscalStatus.PENDING)
					)
			.stream();
	}
	
//
//
//	private void fillModel349(DSLContext ctx, List<FiscalModelConfig> list,ModelManagerParams params) {
//		if (params.getModel() == null || params.getModel() == FiscalModel.M349 ) {
//			Result<Record6<Byte, Integer, Byte,Byte,Integer,String>> models = ctx
//					.select(FS_MOD349.STATUS, FS_MOD349.YEAR, FS_MOD349.PERIOD,FS_MOD349.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
//					.from(FS_MOD349)
//					.join(DOMAIN).onKey()
//					.where(FS_MOD349.DOMAIN.equal(params.getMasterDomain()))
//						.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
//					.and(FS_MOD349.YEAR.equal(params.getYear()))					
//					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
//							ctx.select(USER_SCOPE.SCOPE)
//							.from(USER_SCOPE)
//							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
//							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
//							)))
//					.orderBy(FS_MOD349.YEAR)
//					.fetch();
//			for (Record6<Byte,Integer,Byte,Byte,Integer,String> mod : models) {
//				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
//				int domainId = mod.getValue(DOMAIN.ID);
//				byte adm = mod.getValue(FS_MOD349.ADMINISTRATION);
//				byte per = mod.getValue(FS_MOD349.PERIOD);
//				Period period = Period.values()[per];
//				byte st = mod.getValue(FS_MOD349.STATUS);
//				putModelConfig(list,FiscalModel.M349,params.getYear(),period,st,adm,domainId,domainName,null,null);
//			}
//		}
//	}
//
//	private void fillModel347(DSLContext ctx, List<FiscalModelConfig> list,ModelManagerParams params) {
//		if (params.getModel() == null || params.getModel() == FiscalModel.M347 ) {
//			Result<Record5<Byte,Integer,Byte,Integer,String>> models = ctx
//					.select(FS_MOD347.STATUS, FS_MOD347.YEAR,FS_MOD347.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
//					.from(FS_MOD347)
//					.join(DOMAIN).onKey()
//					.where(FS_MOD347.DOMAIN.equal(params.getMasterDomain()))
//						.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
//					.and(FS_MOD347.YEAR.equal(params.getYear()))
//					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
//							ctx.select(USER_SCOPE.SCOPE)
//							.from(USER_SCOPE)
//							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
//							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
//							)))
//					.orderBy(FS_MOD347.YEAR)
//					.fetch();
//			for (Record5<Byte,Integer,Byte,Integer,String> mod : models) {
//				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
//				int domainId = mod.getValue(DOMAIN.ID);
//				byte adm = mod.getValue(FS_MOD349.ADMINISTRATION);
//				byte st = mod.getValue(FS_MODEL.STATUS);
//				putModelConfig(list,FiscalModel.M347,params.getYear(),Period.YEAR,st,adm,domainId,domainName,null,null);
//			}
//		}
//	}
//
//	private void fillModel180(DSLContext ctx, List<FiscalModelConfig> list,ModelManagerParams params) {
//		if (params.getModel() == null || params.getModel() == FiscalModel.M180 ) {
//			Result<Record5<Byte,Integer,Byte,Integer,String>> models = ctx
//					.select(FS_MODEL180.STATUS, FS_MODEL180.YEAR,FS_MODEL180.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
//					.from(FS_MODEL180)
//					.join(DOMAIN).onKey()
//					.where(FS_MODEL180.DOMAIN.equal(params.getMasterDomain()))
//						.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
//					.and(FS_MODEL180.YEAR.equal(params.getYear()))
//					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
//							ctx.select(USER_SCOPE.SCOPE)
//							.from(USER_SCOPE)
//							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
//							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
//							)))
//					.orderBy(FS_MODEL180.YEAR)
//					.fetch();
//			for (Record5<Byte,Integer,Byte,Integer,String> mod : models) {
//				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
//				int domainId = mod.getValue(DOMAIN.ID);
//				byte adm = mod.getValue(FS_MODEL180.ADMINISTRATION);
//				byte st = mod.getValue(FS_MODEL180.STATUS);
//				putModelConfig(list,FiscalModel.M180,params.getYear(),Period.YEAR,st,adm,domainId,domainName,null,null);
//			}
//		}
//	}
//
//	private void fillModel190(DSLContext ctx, List<FiscalModelConfig> list,ModelManagerParams params) {
//		if (params.getModel() == null || params.getModel() == FiscalModel.M190 ) {
//			Result<Record5<Byte, Integer, Byte, Integer, String>> models = ctx
//					.select(FS_MODEL190.STATUS, FS_MODEL190.YEAR,FS_MODEL190.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
//					.from(FS_MODEL190)
//					.join(DOMAIN).onKey()
//					.where(FS_MODEL190.DOMAIN.equal(params.getMasterDomain()))
//						.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
//					.and(FS_MODEL190.YEAR.equal(params.getYear()))
//					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
//							ctx.select(USER_SCOPE.SCOPE)
//							.from(USER_SCOPE)
//							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
//							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
//							)))
//					.orderBy(FS_MODEL190.YEAR)
//					.fetch();
//			for (Record5<Byte, Integer, Byte, Integer, String> mod : models) {
//				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
//				int domainId = mod.getValue(DOMAIN.ID);
//				byte adm = mod.getValue(FS_MODEL190.ADMINISTRATION);
//				byte st = mod.getValue(FS_MODEL190.STATUS);
//				putModelConfig(list,FiscalModel.M190,params.getYear(),Period.YEAR,st,adm,domainId,domainName,null,null);
//			}
//		}
//	}
//	
//	private void fillModel390(DSLContext ctx, List<FiscalModelConfig> list,ModelManagerParams params) {
//		if (params.getModel() == null || params.getModel() == FiscalModel.M390 ) {
//			Result<Record5<Byte, Integer, Byte, Integer, String>> models = ctx
//					.select(FS_MODEL390.STATUS, FS_MODEL390.YEAR,FS_MODEL390.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
//					.from(FS_MODEL390)
//					.join(DOMAIN).onKey()
//					.where(FS_MODEL390.DOMAIN.equal(params.getMasterDomain()))
//						.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
//					.and(FS_MODEL390.YEAR.equal(params.getYear()))
//					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
//							ctx.select(USER_SCOPE.SCOPE)
//							.from(USER_SCOPE)
//							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
//							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
//							)))
//					.orderBy(FS_MODEL390.YEAR)
//					.fetch();
//			for (Record5<Byte, Integer, Byte, Integer, String> mod : models) {
//				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
//				int domainId = mod.getValue(DOMAIN.ID);
//				byte adm = mod.getValue(FS_MODEL390.ADMINISTRATION);
//				byte st = mod.getValue(FS_MODEL390.STATUS);
//				putModelConfig(list,FiscalModel.M390,params.getYear(),Period.YEAR,st,adm,domainId,domainName,null,null);
//			}
//		}
//	}
//
//	private void fillModel200(DSLContext ctx, List<FiscalModelConfig> list,ModelManagerParams params) {
//		if (params.getModel() == null || params.getModel() == FiscalModel.M200 ) {
//			Result<Record4<Integer, Byte, Integer, String>> models = ctx
//					.select(FS_MODEL200.YEAR,FS_MODEL200.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
//					.from(FS_MODEL200)
//					.join(DOMAIN).onKey()
//					.where(FS_MODEL200.DOMAIN.equal(params.getMasterDomain()))
//						.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
//					.and(FS_MODEL200.YEAR.equal(params.getYear()))
//					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
//							ctx.select(USER_SCOPE.SCOPE)
//							.from(USER_SCOPE)
//							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
//							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
//							)))
//					.orderBy(FS_MODEL200.YEAR)
//					.fetch();
//			for (Record4<Integer, Byte, Integer, String> mod : models) {
//				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
//				int domainId = mod.getValue(DOMAIN.ID);
//				byte adm = mod.getValue(FS_MODEL200.ADMINISTRATION);
//				byte st = 0;
//				putModelConfig(list,FiscalModel.M200,params.getYear(),Period.YEAR,st,adm,domainId,domainName,null,null);
//			}
//		}
//	}

	public static Stream<FiscalModelMatrixItem> fillConfiguratedModels(AONContext ctx, int domain, int user) {
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
						?new FiscalModelMatrixItem()
							.setDomainId(rec.getValue(DOMAIN.ID))
							.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
							.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY))
							.setModel( FiscalModel.valueOf(AonStringUtils.substringAfter(rec.getValue(APP_PARAM.NAME),PARAM_PREFIX)) )
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


		
		