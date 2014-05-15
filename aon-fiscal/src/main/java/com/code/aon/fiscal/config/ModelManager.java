package com.code.aon.fiscal.config;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsMod347.FS_MOD347;
import static com.esferalia.aon.jooq.tables.FsMod349.FS_MOD349;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.FsModel180.FS_MODEL180;
import static com.esferalia.aon.jooq.tables.FsModel190.FS_MODEL190;
import static com.esferalia.aon.jooq.tables.FsModel390.FS_MODEL390;
import static com.esferalia.aon.jooq.tables.FsVat.FS_VAT;
import static com.esferalia.aon.jooq.tables.FsVatDeclaration.FS_VAT_DECLARATION;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Record5;
import org.jooq.Record6;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Period;

public class ModelManager {
	
	public static final String PARAM_DEFAULT_ADMINISTRATION = "FS_DEFAULT_ADMINISTRATION";
	public static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	public static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";
	
	public List<ModelConfig> getModelsPanel(Connection conn, int domainId,int year) {
		List<ModelConfig> list = getAvailableModels(conn, domainId,year);
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
	
	public List<ModelConfig> getAvailableModels(Connection conn, int domain, int year) {
		List<ModelConfig> list = new LinkedList<ModelConfig>();
		DSLContext ctx = DSL.using(conn, AccountingUtil.getDefaultSettings());
		fillConfiguratedModels(ctx,domain,list,year);
		fillModel303(ctx,domain,list,year);
		fillFiscalModel(ctx,domain,list,year);
		fillModel347(ctx,domain,list,year);
		fillModel349(ctx,domain,list,year);
		fillModel180(ctx,domain,list,year);
		fillModel190(ctx,domain,list,year);
		fillModel390(ctx,domain,list,year);
		return list;
	}
	
	private void fillModel303(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record5<Byte,Byte,Byte,Integer,String>> models = ctx
				.select(FS_VAT.STATUS, FS_VAT.PERIOD,FS_VAT_DECLARATION.ADMINISTRATION,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_VAT)
				.join(DOMAIN).on( FS_VAT.DOMAIN.equal(DOMAIN.ID))
				.leftOuterJoin(FS_VAT_DECLARATION).on( FS_VAT.ID.equal(FS_VAT_DECLARATION.FS_VAT))
				.where(FS_VAT.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
					.and(FS_VAT.YEAR.equal(year))
				.orderBy(FS_VAT.PERIOD)
				.fetch();
		for (Record5<Byte,Byte,Byte,Integer,String> mod : models) {
			Byte adm = mod.getValue(FS_VAT_DECLARATION.ADMINISTRATION);
			byte per = mod.getValue(FS_VAT.PERIOD);
			int domainId = mod.getValue(DOMAIN.ID);
			String domainName = mod.getValue(DOMAIN.NAME);
			Period period = Period.values()[per];
			byte st = mod.getValue(FS_VAT_DECLARATION.STATUS);
			Model model = Model.M303_RG;
			if (period == Period.YEAR ) {
				model = Model.M390_HF;
			}
			putModelConfig(list,model,year,period,st,adm,domainId,domainName,null,null);
		}
	}
	
	private void fillFiscalModel(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record9<Byte,String,String,String,Byte,String,Byte,Integer,String>> models = ctx
				.select(FS_MODEL.STATUS, FS_MODEL.DOCUMENT, FS_MODEL.NAME,FS_MODEL.SURNAME
						,FS_MODEL.PERIOD, FS_MODEL.MODEL, FS_MODEL.ADMINISTRATION ,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_MODEL)
				.join(DOMAIN).onKey()
				.where(FS_MODEL.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MODEL.YEAR.equal(year))
				.orderBy(FS_MODEL.YEAR)
				.fetch();
		for (Record9<Byte,String,String,String,Byte,String,Byte,Integer,String> mod : models) {
			String document = mod.getValue(FS_MODEL.DOCUMENT);
			String name = mod.getValue(FS_MODEL.NAME);
			String surname = mod.getValue(FS_MODEL.SURNAME);
			String fullName = StringUtils.isEmpty(name)?"":(name + " ") + surname;
			fullName = StringUtils.trim(fullName);
			String domainName = mod.getValue(DOMAIN.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			
			byte adm = mod.getValue(FS_MODEL.ADMINISTRATION);
			byte per = mod.getValue(FS_MODEL.PERIOD);
			Period period = Period.values()[per];
			byte st = mod.getValue(FS_MODEL.STATUS);
			String m = mod.getValue(FS_MODEL.MODEL);
			Model model = null;
			if ("303".equals(m)) {
				model = Model.M303_RS;
			} else {
				model = Model.valueOf("M" + m );
			}
			putModelConfig(list,model,year,period,st,adm,domainId,domainName,document,fullName);
		}
	}


	private void fillModel349(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record6<Byte, Integer, Byte,Byte,Integer,String>> models = ctx
				.select(FS_MOD349.STATUS, FS_MOD349.YEAR, FS_MOD349.PERIOD,FS_MOD349.ADMINISTRATION,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_MOD349)
				.join(DOMAIN).onKey()
				.where(FS_MOD349.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MOD349.YEAR.equal(year))					
				.orderBy(FS_MOD349.YEAR)
				.fetch();
		for (Record6<Byte,Integer,Byte,Byte,Integer,String> mod : models) {
			String domainName = mod.getValue(DOMAIN.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			byte adm = mod.getValue(FS_MOD349.ADMINISTRATION);
			byte per = mod.getValue(FS_MOD349.PERIOD);
			Period period = Period.values()[per];
			byte st = mod.getValue(FS_MOD349.STATUS);
			putModelConfig(list,Model.M349,year,period,st,adm,domainId,domainName,null,null);
		}
	}

	private void fillModel347(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record5<Byte,Integer,Byte,Integer,String>> models = ctx
				.select(FS_MOD347.STATUS, FS_MOD347.YEAR,FS_MOD347.ADMINISTRATION,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_MOD347)
				.join(DOMAIN).onKey()
				.where(FS_MOD347.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MOD347.YEAR.equal(year))
				.orderBy(FS_MOD347.YEAR)
				.fetch();
		for (Record5<Byte,Integer,Byte,Integer,String> mod : models) {
			String domainName = mod.getValue(DOMAIN.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			byte adm = mod.getValue(FS_MOD349.ADMINISTRATION);
			byte st = mod.getValue(FS_MODEL.STATUS);
			putModelConfig(list,Model.M347,year,Period.YEAR,st,adm,domainId,domainName,null,null);
		}
	}

	private void fillModel180(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record5<Byte,Integer,Byte,Integer,String>> models = ctx
				.select(FS_MODEL180.STATUS, FS_MODEL180.YEAR,FS_MODEL180.ADMINISTRATION,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_MODEL180)
				.join(DOMAIN).onKey()
				.where(FS_MODEL180.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MODEL180.YEAR.equal(year))
				.orderBy(FS_MODEL180.YEAR)
				.fetch();
		for (Record5<Byte,Integer,Byte,Integer,String> mod : models) {
			String domainName = mod.getValue(DOMAIN.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			byte adm = mod.getValue(FS_MODEL180.ADMINISTRATION);
			byte st = mod.getValue(FS_MODEL180.STATUS);
			putModelConfig(list,Model.M180,year,Period.YEAR,st,adm,domainId,domainName,null,null);
		}
	}

	private void fillModel190(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record5<Byte, Integer, Byte, Integer, String>> models = ctx
				.select(FS_MODEL190.STATUS, FS_MODEL190.YEAR,FS_MODEL190.ADMINISTRATION,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_MODEL190)
				.join(DOMAIN).onKey()
				.where(FS_MODEL190.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MODEL190.YEAR.equal(year))
				.orderBy(FS_MODEL190.YEAR)
				.fetch();
		for (Record5<Byte, Integer, Byte, Integer, String> mod : models) {
			String domainName = mod.getValue(DOMAIN.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			byte adm = mod.getValue(FS_MODEL190.ADMINISTRATION);
			byte st = mod.getValue(FS_MODEL190.STATUS);
			putModelConfig(list,Model.M190,year,Period.YEAR,st,adm,domainId,domainName,null,null);
		}
	}
	
	private void fillModel390(DSLContext ctx, int domain, List<ModelConfig> list,int year) {
		Result<Record5<Byte, Integer, Byte, Integer, String>> models = ctx
				.select(FS_MODEL390.STATUS, FS_MODEL390.YEAR,FS_MODEL390.ADMINISTRATION,DOMAIN.ID,DOMAIN.NAME)
				.from(FS_MODEL390)
				.join(DOMAIN).onKey()
				.where(FS_MODEL390.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(FS_MODEL390.YEAR.equal(year))
				.orderBy(FS_MODEL390.YEAR)
				.fetch();
		for (Record5<Byte, Integer, Byte, Integer, String> mod : models) {
			String domainName = mod.getValue(DOMAIN.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			byte adm = mod.getValue(FS_MODEL390.ADMINISTRATION);
			byte st = mod.getValue(FS_MODEL390.STATUS);
			putModelConfig(list,Model.M390,year,Period.YEAR,st,adm,domainId,domainName,null,null);
		}
	}

	private ModelConfig putModelConfig(List<ModelConfig> list, Model model,int year, Period period
			, byte st, Byte adm,int domainId,String domainName,String document,String fullName) {
		ModelConfig modelConfig = null;
		Administration administration = adm==null?null:Administration.values()[adm];
		for (ModelConfig mc : list) {
			if (mc.getModel() == model
				&& (StringUtils.equals(document, mc.getDocument()))
				&& ((administration == null && mc.getAdministration() == null)
					|| (mc.getAdministration() == administration))
				&& ((mc.isMonthly() && period.isMonthPeriod())
				 || (mc.isQuaterly() && period.isQuarterPeriod())
				 || (mc.isYearly() && period == Period.YEAR))) {
				modelConfig = mc;
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
		if (st != -1) {
			modelConfig.setStatuses(period,  (st == 1) );
		}
		return modelConfig;
	}

	public void fillConfiguratedModels(DSLContext ctx, int domain, List<ModelConfig> list, int year) {
		Record1<String> defAdm = ctx.select(APP_PARAM.VALUE)
				   .from(APP_PARAM)
				   .where(APP_PARAM.DOMAIN.equal(domain))
				   .and(APP_PARAM.NAME.equal(PARAM_DEFAULT_ADMINISTRATION))
				   .fetchOne();
		byte adm = 4;
		if (defAdm != null && defAdm.getValue(APP_PARAM.VALUE) != null) {
			String da = defAdm.getValue(APP_PARAM.VALUE);
			try {
				adm = Byte.parseByte(da);
			} catch (NumberFormatException e) {
				adm = 4; // AEAT.
			}
		} 
		Result<Record4<String,String,Integer,String>> models = ctx
				.select(APP_PARAM.NAME,APP_PARAM.VALUE,DOMAIN.ID,DOMAIN.NAME)
				.from(APP_PARAM)
				.join(DOMAIN).onKey()
				.where(APP_PARAM.DOMAIN.equal(domain))
					.or(DOMAIN.PARENT.equal(domain))
				.and(APP_PARAM.NAME.like(PARAM_PREFIX_LIKE))
				.orderBy(APP_PARAM.NAME)
				.fetch();
		for (Record4<String,String,Integer,String> mod : models) {
			String name = mod.getValue(APP_PARAM.NAME);
			int domainId = mod.getValue(DOMAIN.ID);
			String domainName = mod.getValue(DOMAIN.NAME);
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
					putModelConfig(list,model,year,period,(byte) -1,adm,domainId,domainName,null,null);
				}
			} catch (IllegalArgumentException e) {
				// Model.valueOf --> Ignore param.
			}
		}
	}
	
}
