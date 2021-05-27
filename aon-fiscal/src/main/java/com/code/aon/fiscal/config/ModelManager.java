package com.code.aon.fiscal.config;

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
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record9;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.config.ModelStatus.Status;
import com.code.aon.fiscal.enumeration.Period;

public class ModelManager {
	
	public static final String PARAM_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	public static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";

	public List<ModelConfig> getModelsPanel(Connection conn, int domainId,int year,int userId) {
		ModelManagerParams params = new ModelManagerParams(domainId);
		params.setYear(year);
		params.setUserId(userId);
		return getModelsPanel(conn, params);
	}
	
	public List<ModelConfig> getModelsPanel(Connection conn, ModelManagerParams params ) {
		List<ModelConfig> list = getAvailableModels(conn, params);
		ensureNotNull( list );
		return list;
	}
	
	private void ensureNotNull(List<ModelConfig> list) {
		for (ModelConfig mc : list) {
			for (int i = 0; i < mc.getStatuses().length; i++) {
				if (mc.getStatuses()[i] == null) {
					ModelStatus ms = null;
					if (mc.getStatuses().length == 12) {
						ms = new ModelStatus(Period.values()[i]);	
					} else if (mc.getStatuses().length == 4) {
						ms = new ModelStatus(Period.values()[i + 12]);
					} else {
						ms = new ModelStatus(Period.YEAR);
					}
					mc.getStatuses()[i] = ms;	
				}
			}
		}
	}
	
	public List<ModelConfig> getAvailableModels(Connection conn, int domain, int year, int userId) {
		ModelManagerParams params = new ModelManagerParams(domain);
		params.setYear(year);
		params.setUserId(userId);
		return getAvailableModels(conn, params);
	}
	
	public List<ModelConfig> getAvailableModels(Connection conn, ModelManagerParams params) {
		List<ModelConfig> list = new LinkedList<ModelConfig>();
		DSLContext ctx = DSL.using(conn, AccountingUtil.getDefaultSettings());
		fillConfiguratedModels(ctx,list,params);
		if (!params.isShowOnlyConfiguratedModels()) {
			fillModel303(ctx,list,params);
			fillFiscalModel(ctx,list,params);
			fillModel347(ctx,list,params);
			fillModel349(ctx,list,params);
			fillModel180(ctx,list,params);
			fillModel190(ctx,list,params);
			fillModel390(ctx,list,params);
			fillModel200(ctx,list,params);
			fillModel184(ctx,list,params);
			fillModel193(ctx,list,params);
		}
		return list;
	}
	
	private void fillModel303(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M303_RG || params.getModel() == Model.M390_HF ) {
			ctx.select(FS_VAT.STATUS, FS_VAT.PERIOD,FS_VAT_DECLARATION.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_VAT)
				.join(DOMAIN).on( FS_VAT.DOMAIN.equal(DOMAIN.ID))
				.leftOuterJoin(FS_VAT_DECLARATION).on( FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(FS_VAT.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
					.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
							ctx.select(USER_SCOPE.SCOPE)
							.from(USER_SCOPE)
							.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
							.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
							)))
					.and(FS_VAT.YEAR.equal(params.getYear()))
				.orderBy(FS_VAT.PERIOD)
				.fetch()
				.stream()
				.forEach(mod -> {
					Byte adm = mod.getValue(FS_VAT_DECLARATION.ADMINISTRATION);
					byte per = mod.getValue(FS_VAT.PERIOD);
					int domainId = mod.getValue(DOMAIN.ID);
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					Period period = Period.values()[per];
					byte st = mod.getValue(FS_VAT_DECLARATION.STATUS);
					Status status = null;
					if (st == 0) status = Status.PENDING;
					else if (st == 1) status = Status.FINISHED;
					else if (st == 2) status = Status.FINISHED;
					else status = Status.PENDING;
					Model model = Model.M303_RG;
					if (period == Period.YEAR ) {
						model = Model.M390_HF;
					}
					if (params.getModel() == null || params.getModel() == model) { 
						putModelConfig(list,model,params.getYear(),period,status,adm,domainId,domainName,null,null);
					}
				});
		}
	}
	
	private void fillFiscalModel(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		
		SelectConditionStep<Record9<Byte,String,String,String,Byte,String,Byte,Integer,String>> select = 
				ctx
				.select(FS_MODEL.STATUS, FS_MODEL.DOCUMENT, FS_MODEL.NAME,FS_MODEL.SURNAME
						,FS_MODEL.PERIOD, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION ,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL)
				.join(DOMAIN).onKey()
				.where(FS_MODEL.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
								ctx.select(USER_SCOPE.SCOPE)
								.from(USER_SCOPE)
								.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
								.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
								)));
		if (params.getModel() != null) {
			String modelLit = null;
			if (params.getModel()==Model.M303_RS) {
				modelLit = "303"; 
			} else if (params.getModel()==Model.MIVA) {
				modelLit =  "IVA";
			} else {
				modelLit = params.getModel().getName();
			}
			select = select.and(FS_MODEL.MODEL.equal(modelLit));
		}
		select.orderBy(FS_MODEL.YEAR)
			.fetch()
			.stream()
			.forEach( mod -> {
				String document = mod.getValue(FS_MODEL.DOCUMENT);
				String name = mod.getValue(FS_MODEL.NAME);
				String surname = mod.getValue(FS_MODEL.SURNAME);
				String fullName = StringUtils.isEmpty(name)?"":(name + " ") + surname;
				fullName = StringUtils.trim(fullName);
				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
				int domainId = mod.getValue(DOMAIN.ID);
				
				byte adm = mod.getValue(FS_MODEL.ADMINISTRATION);
				byte per = mod.getValue(FS_MODEL.PERIOD);
				Period period = Period.values()[per];
				byte st = mod.getValue(FS_MODEL.STATUS);
				Status status = getStatus(st);
				String m = mod.getValue(FS_MODEL.MODEL);
				if (!"3O3".equals(m)) {
					Model model = null;
					if ("303".equals(m)) {
						model = Model.M303_RS;
					} else {
						model = Model.valueOf("M" + m );
					}
					putModelConfig(list,model,params.getYear(),period,status,adm,domainId,domainName,document,fullName);
				}
		});
	}


	private Status getStatus(byte st) {
		 if (st == 0 ) return Status.PENDING;
		 else if (st == 1 ) return Status.FINISHED;
		 else if (st == 2 ) return Status.FINISHED;
		 else if (st == 3 ) return Status.BLOCKED;
		 else if (st == 4 ) return Status.SENT;
		 return Status.PENDING;
	}

	private void fillModel349(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M349 ) {
			ctx.select(FS_MOD349.STATUS, FS_MOD349.YEAR, FS_MOD349.PERIOD,FS_MOD349.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MOD349)
				.join(DOMAIN).onKey()
				.where(FS_MOD349.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MOD349.YEAR.equal(params.getYear()))					
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MOD349.YEAR)
				.fetch()
				.stream()
				.forEach(mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MOD349.ADMINISTRATION);
					byte per = mod.getValue(FS_MOD349.PERIOD);
					Period period = Period.values()[per];
					byte st = mod.getValue(FS_MOD349.STATUS);
					Status status = null;
					if (st == 0) status = Status.PENDING;
					else if (st == 1) status = Status.FINISHED;
					else status = Status.PENDING;
					putModelConfig(list,Model.M349,params.getYear(),period,status,adm,domainId,domainName,null,null);
				});
		}
	}

	private void fillModel347(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M347 ) {
			ctx.select(FS_MOD347.STATUS, FS_MOD347.YEAR,FS_MOD347.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MOD347)
				.join(DOMAIN).onKey()
				.where(FS_MOD347.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MOD347.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MOD347.YEAR)
				.fetch()
				.stream()
				.forEach(mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MOD349.ADMINISTRATION);
					byte st = mod.getValue(FS_MODEL.STATUS);
					Status status = null;
					if (st == 0) status = Status.PENDING;
					else if (st == 1) status = Status.FINISHED;
					else status = Status.PENDING;
					putModelConfig(list,Model.M347,params.getYear(),Period.YEAR,status,adm,domainId,domainName,null,null);
				});
		}
	}

	private void fillModel180(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M180 ) {
			ctx.select(FS_MODEL180.STATUS, FS_MODEL180.YEAR,FS_MODEL180.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL180)
				.join(DOMAIN).onKey()
				.where(FS_MODEL180.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL180.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL180.YEAR)
				.fetch()
				.stream()
				.forEach(mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MODEL180.ADMINISTRATION);
					byte st = mod.getValue(FS_MODEL180.STATUS);
					Status status = getStatus(st);
					putModelConfig(list,Model.M180,params.getYear(),Period.YEAR,status,adm,domainId,domainName,null,null);
				});
		}
	}

	private void fillModel184(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M184 ) {
			ctx.select(FS_MODEL184.STATUS, FS_MODEL184.YEAR,FS_MODEL184.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL184)
				.join(DOMAIN).onKey()
				.where(FS_MODEL184.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL184.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL184.YEAR)
				.fetch()
				.stream()
				.forEach(mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MODEL184.ADMINISTRATION);
					byte st = mod.getValue(FS_MODEL184.STATUS);
					Status status = getStatus(st);
					putModelConfig(list,Model.M184,params.getYear(),Period.YEAR,status,adm,domainId,domainName,null,null);
				});
		}
	}

	private void fillModel190(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M190 ) {
			ctx.select(FS_MODEL190.STATUS, FS_MODEL190.YEAR,FS_MODEL190.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL190)
				.join(DOMAIN).onKey()
				.where(FS_MODEL190.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL190.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL190.YEAR)
				.fetch()
				.stream()
				.forEach(mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MODEL190.ADMINISTRATION);
					byte st = mod.getValue(FS_MODEL190.STATUS);
					Status status = getStatus(st);
					putModelConfig(list,Model.M190,params.getYear(),Period.YEAR,status,adm,domainId,domainName,null,null);
				});
		}
	}
	
	private void fillModel193(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M193 ) {
			ctx.select(FS_MODEL193.STATUS, FS_MODEL193.YEAR,FS_MODEL193.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL193)
				.join(DOMAIN).onKey()
				.where(FS_MODEL193.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL193.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL193.YEAR)
				.fetch()
				.stream()
				.forEach(mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MODEL193.ADMINISTRATION);
					byte st = mod.getValue(FS_MODEL193.STATUS);
					Status status = getStatus(st);
					putModelConfig(list,Model.M193,params.getYear(),Period.YEAR,status,adm,domainId,domainName,null,null);
				});
		}
	}

	private void fillModel390(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M390 ) {
			ctx.select(FS_MODEL390.STATUS, FS_MODEL390.YEAR,FS_MODEL390.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL390)
				.join(DOMAIN).onKey()
				.where(FS_MODEL390.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL390.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL390.YEAR)
				.fetch()
				.stream()
				.forEach( mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MODEL390.ADMINISTRATION);
					byte st = mod.getValue(FS_MODEL390.STATUS);
					Status status = getStatus(st);
					putModelConfig(list,Model.M390,params.getYear(),Period.YEAR,status,adm,domainId,domainName,null,null);
				});
		}
	}

	private void fillModel200(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		if (params.getModel() == null || params.getModel() == Model.M200 ) {
			ctx.select(FS_MODEL200.YEAR,FS_MODEL200.ADMINISTRATION,DOMAIN.ID,DOMAIN.DESCRIPTION)
				.from(FS_MODEL200)
				.join(DOMAIN).onKey()
				.where(FS_MODEL200.DOMAIN.equal(params.getMasterDomain()))
					.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(FS_MODEL200.YEAR.equal(params.getYear()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.orderBy(FS_MODEL200.YEAR)
				.fetch()
				.stream()
				.forEach( mod -> {
					String domainName = mod.getValue(DOMAIN.DESCRIPTION);
					int domainId = mod.getValue(DOMAIN.ID);
					byte adm = mod.getValue(FS_MODEL200.ADMINISTRATION);
					putModelConfig(list,Model.M200,params.getYear(),Period.YEAR,Status.PENDING,adm,domainId,domainName,null,null);
				});
		}
	}

	private ModelConfig putModelConfig(List<ModelConfig> list, Model model,int year, Period period
			,Status status, Byte adm,int domainId,String domainName,String document,String fullName) {
		ModelConfig modelConfig = null;
		Administration administration = adm==null?null:Administration.values()[adm];
		for (ModelConfig mc : list) {
			if (mc.getModel() == model
			 && mc.getDomainId() == domainId
			 && ((administration == null && mc.getAdministration() == null)
			  || (mc.getAdministration() == administration))
			 && ((mc.isMonthly() && period.isMonthPeriod())
			  || (mc.isQuaterly() && period.isQuarterPeriod())
			  || (mc.isYearly() && period == Period.YEAR))) {
				
				 if (mc.getDocument() == null) {
					 mc.setDocument(document);
					 mc.setName(fullName);
				 }
				 if (StringUtils.equals(document, mc.getDocument())) {
					modelConfig = mc;
					break;
				 }
			}
		}
		if (modelConfig == null ) {
			modelConfig = new ModelConfig(model,year,period, administration);
			modelConfig.setDomainId(domainId);
			modelConfig.setDomainName(domainName);
			modelConfig.setDocument(document);
			modelConfig.setName(fullName);
			list.add(modelConfig);
		} 
		modelConfig.setStatuses(period,  status);
		return modelConfig;
	}
	
	public void fillConfiguratedModels(DSLContext ctx, int domain, List<ModelConfig> list, int year, int userId) {
		ModelManagerParams params = new  ModelManagerParams(domain);
		params.setYear(year);
		params.setUserId(userId);
		fillConfiguratedModels(ctx, list, params);	
	}

	public void fillConfiguratedModels(DSLContext ctx, List<ModelConfig> list,ModelManagerParams params) {
		Record1<String> defAdm = ctx.select(APP_PARAM.VALUE)
				   .from(APP_PARAM)
				   .where(APP_PARAM.DOMAIN.equal(params.getMasterDomain()))
				   .and(APP_PARAM.NAME.equal(PARAM_DEFAULT_ADMINISTRATION))
				   .fetchOne();
		byte adm0 = 4;
		if (defAdm != null && defAdm.getValue(APP_PARAM.VALUE) != null) {
			String da = defAdm.getValue(APP_PARAM.VALUE);
			try {
				adm0 = Byte.parseByte(da);
			} catch (NumberFormatException e) {
				adm0 = 4; // AEAT.
			}
		}
		final byte adm = adm0;
		String like = (params.getModel() == null)?PARAM_PREFIX_LIKE:(PARAM_PREFIX + params.getModel() + "%");
		ctx.select(APP_PARAM.NAME,APP_PARAM.VALUE,DOMAIN.ID,DOMAIN.DESCRIPTION)
			.from(APP_PARAM)
			.join(DOMAIN).onKey()
			.where(APP_PARAM.DOMAIN.equal(params.getMasterDomain()))
				.or(DOMAIN.PARENT.equal(params.getMasterDomain()))
				.and(DOMAIN.SCOPE.isNull().or(DOMAIN.SCOPE.equal(
						ctx.select(USER_SCOPE.SCOPE)
						.from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.equal(params.getUserId()))
						.and(USER_SCOPE.SCOPE.equal(DOMAIN.SCOPE))
						)))
				.and(APP_PARAM.NAME.like(like))
			.orderBy(APP_PARAM.NAME)
			.fetch()
			.stream()
			.forEach(mod -> {
				String name = mod.getValue(APP_PARAM.NAME);
				int domainId = mod.getValue(DOMAIN.ID);
				String domainName = mod.getValue(DOMAIN.DESCRIPTION);
				try {
					Model model = Model.valueOf(StringUtils.substringAfter(name,PARAM_PREFIX)); 
					String value = mod.getValue(APP_PARAM.VALUE);
					Period period = null;
					if  (StringUtils.equals("Y",value)) {
						period =Period.YEAR;
					} else if  (StringUtils.equals("Q",value)) {
						period =Period.T1;
					} else if  (StringUtils.equals("M",value)) {
						period =Period.M01;
					}
					if (period != null) {
						putModelConfig(list,model,params.getYear(),period,Status.MISSING,adm,domainId,domainName,null,null);
					}
				} catch (IllegalArgumentException e) {
					// Model.valueOf --> Ignore param.
				}
			});
	}
	
}
