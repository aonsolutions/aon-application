package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMatrixDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod111DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod115DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod123DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod130DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod131DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod180DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod184DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod190DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod193DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod200DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod202DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902014DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902015DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390DAO;
import com.esferalia.aon.occam.impl.jooq.dao.VATDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.Mod2002013DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.Mod2002014DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.Mod2002015DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2016.Mod2002016DAO;
import com.esferalia.aon.occam.server.finance.FinanceUtils;

public class FiscalImpl implements IFiscal {

	// -------------------------------------------------- FISCAL PANEL
	public FiscalModelMatrix getFiscalPanel(AONContext ctx,int domain,int year,int user){
		return FiscalMatrixDAO.getModelsPanel(ctx, domain, year, user);
	}
	@Override
	public LinkedList<IFiscalModel> getAllModels(AONContext ctx, int domain,int user) {
		return FiscalMatrixDAO.getAllModels(ctx, domain, user);
	}
	@Override
	public LinkedList<IFiscalModel> getAllModels(AONContext ctx, int domain,
			int year, int user) {
		return FiscalMatrixDAO.getAllModels(ctx, domain, year, user);
	}

	// --------------------------------------------- [FISCAL MODELS]
	@Override
	public FiscalModel save(AONContext ctx, FiscalModel fm) {
		return ctx.getDslContext().transactionResult(
				configuration -> FiscalModelDAO.save(ctx, fm));
	}
	@Override
	public void delete(AONContext ctx, FiscalModel fm) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, fm));
	}
	@Override
	public FiscalModel getModel(AONContext ctx, int id) {
		return FiscalModelDAO.getFiscalModel(ctx, id);
	}

	// ---------------------------------------------------- [MODELO 180]
	@Override
	public LinkedList<Mod180> getMod180s(AONContext ctx, int domain) {
		return Mod180DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod180 getMod180(AONContext ctx, Integer id) {
		return Mod180DAO.getById(ctx, id);
	}

	@Override
	public Mod180 initializeMod180(AONContext ctx, int year) {
		return Mod180DAO.initialize(ctx, year);
	}

	@Override
	public Mod180 saveMod180(AONContext ctx, Mod180 mod180) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.save(ctx, mod180));
	}

	@Override
	public void deleteMod180(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().transaction(
				configuration -> Mod180DAO.delete(ctx, mod180));
	}

	@Override
	public Mod180Detail getMod180Detail(AONContext ctx, Integer id) {
		return Mod180DAO.getDetail(ctx, id);
	}


	// ----------------------------------------------------------- [MODELO 190]
	@Override
	public LinkedList<Mod190> getMod190s(AONContext ctx, int domain) {
		return Mod190DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod190 getMod190(AONContext ctx, Integer id) {
		return Mod190DAO.getById(ctx, id);
	}

	@Override
	public Mod190 initializeMod190(AONContext ctx, int year) {
		return Mod190DAO.initialize(ctx, year);
	}
	@Override
	public Mod190 saveMod190(AONContext ctx, Mod190 mod190) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.save(ctx, mod190));
	}

	@Override
	public void deleteMod190(AONContext ctx, Mod190 mod190) {
		ctx.getDslContext().transaction(
				configuration -> Mod190DAO.delete(ctx, mod190));
	}

	@Override
	public Mod190Detail getMod190Detail(AONContext ctx, Integer id) {
		return Mod190DAO.getDetail(ctx, id);
	}
	
	// ----------------------------------------------------------- [MODELO 193]
	@Override
	public LinkedList<Mod193> getMod193s(AONContext ctx, int domain) {
		return Mod193DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod193 getMod193(AONContext ctx, Integer id) {
		return Mod193DAO.getById(ctx, id);
	}

	@Override
	public Mod193 initializeMod193(AONContext ctx, int year) {
		return Mod193DAO.initialize(ctx, year);
	}
	@Override
	public Mod193 saveMod193(AONContext ctx, Mod193 mod193) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.save(ctx, mod193));
	}

	@Override
	public void deleteMod193(AONContext ctx, Mod193 mod193) {
		ctx.getDslContext().transaction(
				configuration -> Mod193DAO.delete(ctx, mod193));
	}

	// ----------------------------------------------------------- [MODELO 184]
	@Override
	public LinkedList<Mod184> getMod184s(AONContext ctx, int domain) {
		return Mod184DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod184 getMod184(AONContext ctx, Integer id) {
		return Mod184DAO.getById(ctx, id);
	}

	@Override
	public Mod184 initializeMod184(AONContext ctx, int year) {
		return Mod184DAO.initialize(ctx, year);
	}
	@Override
	public Mod184 saveMod184(AONContext ctx, Mod184 mod184) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod184DAO.save(ctx, mod184));
	}

	@Override
	public void deleteMod184(AONContext ctx, Mod184 mod184) {
		ctx.getDslContext().transaction(
				configuration -> Mod184DAO.delete(ctx, mod184));
	}

	// ----------------------------------------------------------- [MODELO 390 - 2014]
	@Override
	public LinkedList<Mod390> getMod390s(AONContext ctx, int domain) {
		try {
			return Mod390DAO.getByDomain(ctx, domain);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	// ----------------------------------------------------------- [MODELO 390 - 2014]
	@Override
	public Mod3902014 getMod3902014(AONContext ctx, Integer id) {
		try {
			return Mod3902014DAO.getById(ctx, id);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public String getMod3902014XML(AONContext ctx, int id) {
		return Mod3902014DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902014 initializeMod3902014(AONContext ctx, int year) {
		try {
			return Mod3902014DAO.initialize(ctx, year);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public Mod3902014 saveMod3902014(AONContext ctx, Mod3902014 mod390) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902014DAO.save(ctx, mod390));
	}

	@Override
	public void deleteMod3902014(AONContext ctx, Mod3902014 mod390) {
		ctx.getDslContext().transaction(
				configuration -> Mod3902014DAO.delete(ctx, mod390));
	}
	
	// ----------------------------------------------------------- [MODELO 390 - 2015]
	
	@Override
	public Mod3902015 getMod3902015(AONContext ctx, Integer id) {
		try {
			return Mod3902015DAO.getById(ctx, id);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public String getMod3902015XML(AONContext ctx, int id) {
		return Mod3902015DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902015 initializeMod3902015(AONContext ctx, int year) {
		try {
			return Mod3902015DAO.initialize(ctx, year);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public Mod3902015 saveMod3902015(AONContext ctx, Mod3902015 mod390) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902015DAO.save(ctx, mod390));
	}

	@Override
	public void deleteMod3902015(AONContext ctx, Mod3902015 mod390) {
		ctx.getDslContext().transaction(
				configuration -> Mod3902015DAO.delete(ctx, mod390));
	}
	
	// ----------------------------------------------------------- [MODELO 111]
	@Override
	public Mod111 getMod111(AONContext ctx, int id) {
		return Mod111DAO.getMod111(ctx, id);
	}
	@Override
	public LinkedList<Mod111> getMod111s(AONContext ctx, int domain) {
		LinkedList<Mod111> list = new LinkedList<Mod111>();
		Mod111DAO.getMod111s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod111 calculateMod111(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.calculateMod111(ctx, mod111);
	}
	@Override
	public Mod111 saveMod111(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.saveMod111(ctx, mod111));		
	}
	@Override
	public Mod111 saveCommentsMod111(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.saveCommentsMod111(ctx, mod111));		
	}
	@Override
	public Mod111 initializeForFinishMod111(AONContext ctx, Mod111 mod111){
		return Mod111DAO.initializeForFinish(ctx, mod111);
	}
	@Override
	public Mod111 finishMod111(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.finish(ctx, mod111));		
	}
	@Override
	public Mod111 reopenMod111(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.reopen(ctx, mod111));		
	}
	
	@Override
	public void deleteMod111(AONContext ctx, Mod111 mod111) {
		ctx.getDslContext().transaction(
				configuration -> Mod111DAO.delete(ctx, mod111));
	}

	@Override
	public Mod111 initializeMod111(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.initializeMod111(ctx,mod111);
	}

	@Override
	public Mod111 createMod111(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.createMod111(ctx,mod111);
	}
	@Override
	public String getMod111Info(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) {
		return Mod111DAO.getMod111Info(ctx,mod111,script,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 115]
	@Override
	public Mod115 getMod115(AONContext ctx, int id) {
		return Mod115DAO.getMod115(ctx, id);
	}
	@Override
	public LinkedList<Mod115> getMod115s(AONContext ctx, int domain) {
		LinkedList<Mod115> list = new LinkedList<Mod115>();
		Mod115DAO.getMod115s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod115 calculateMod115(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.calculateMod115(ctx, mod115);
	}
	@Override
	public Mod115 saveMod115(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.saveMod115(ctx, mod115));		
	}
	@Override
	public Mod115 saveCommentsMod115(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.saveCommentsMod115(ctx, mod115));		
	}
	@Override
	public Mod115 initializeForFinishMod115(AONContext ctx, Mod115 mod115){
		return Mod115DAO.initializeForFinish(ctx, mod115);
	}
	@Override
	public Mod115 finishMod115(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.finish(ctx, mod115));		
	}
	@Override
	public Mod115 reopenMod115(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.reopen(ctx, mod115));		
	}
	
	@Override
	public void deleteMod115(AONContext ctx, Mod115 mod115) {
		ctx.getDslContext().transaction(
				configuration -> Mod115DAO.delete(ctx, mod115));
	}

	@Override
	public Mod115 initializeMod115(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.initializeMod115(ctx,mod115);
	}

	@Override
	public Mod115 createMod115(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.createMod115(ctx,mod115);
	}
	@Override
	public String getMod115Info(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) {
		return Mod115DAO.getMod115Info(ctx,mod115,script,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 123]
	@Override
	public Mod123 getMod123(AONContext ctx, int id) {
		return Mod123DAO.getMod123(ctx, id);
	}
	@Override
	public LinkedList<Mod123> getMod123s(AONContext ctx, int domain) {
		LinkedList<Mod123> list = new LinkedList<Mod123>();
		Mod123DAO.getMod123s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod123 calculateMod123(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.calculateMod123(ctx, mod123);
	}
	@Override
	public Mod123 saveMod123(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveMod123(ctx, mod123));		
	}
	@Override
	public Mod123 saveCommentsMod123(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveCommentsMod123(ctx, mod123));		
	}
	@Override
	public Mod123 initializeForFinishMod123(AONContext ctx, Mod123 mod123){
		return Mod123DAO.initializeForFinish(ctx, mod123);
	}
	@Override
	public Mod123 finishMod123(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.finish(ctx, mod123));		
	}
	@Override
	public Mod123 reopenMod123(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.reopen(ctx, mod123));		
	}
	
	@Override
	public void deleteMod123(AONContext ctx, Mod123 mod123) {
		ctx.getDslContext().transaction(
				configuration -> Mod123DAO.delete(ctx, mod123));
	}

	@Override
	public Mod123 initializeMod123(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.initializeMod123(ctx,mod123);
	}

	@Override
	public Mod123 createMod123(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.createMod123(ctx,mod123);
	}
	@Override
	public String getMod123Info(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
		return Mod123DAO.getMod123Info(ctx,mod123,script,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 130]
	@Override
	public Mod130 getMod130(AONContext ctx, int id) {
		return Mod130DAO.getMod130(ctx, id);
	}
	@Override
	public LinkedList<Mod130> getMod130s(AONContext ctx, int domain) {
		LinkedList<Mod130> list = new LinkedList<Mod130>();
		Mod130DAO.getMod130s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod130 calculateMod130(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.calculateMod130(ctx, mod130);
	}
	@Override
	public Mod130 saveMod130(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.saveMod130(ctx, mod130));		
	}
	@Override
	public Mod130 saveCommentsMod130(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.saveCommentsMod130(ctx, mod130));		
	}
	@Override
	public Mod130 initializeForFinishMod130(AONContext ctx, Mod130 mod130){
		return Mod130DAO.initializeForFinish(ctx, mod130);
	}
	@Override
	public Mod130 finishMod130(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.finish(ctx, mod130));		
	}
	@Override
	public Mod130 reopenMod130(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.reopen(ctx, mod130));		
	}
	
	@Override
	public void deleteMod130(AONContext ctx, Mod130 mod130) {
		ctx.getDslContext().transaction(
				configuration -> Mod130DAO.delete(ctx, mod130));
	}

	@Override
	public Mod130 initializeMod130(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.initializeMod130(ctx,mod130);
	}

	@Override
	public Mod130 createMod130(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.createMod130(ctx,mod130);
	}
	@Override
	public String getMod130Info(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) {
		return Mod130DAO.getMod130Info(ctx,mod130,script,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 131]
	@Override
	public Mod131 getMod131(AONContext ctx, int id) {
		return Mod131DAO.getMod131(ctx, id);
	}
	@Override
	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain) {
		LinkedList<Mod131> list = new LinkedList<Mod131>();
		Mod131DAO.getMod131s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod131 calculateMod131(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.calculateMod131(ctx, mod131);
	}
	@Override
	public Mod131Activity calculateMod131Activity(AONContext ctx, Mod131Activity activity) {
		return Mod131DAO.calculateMod131Activity(ctx, activity);
	}
	
	@Override
	public Mod131 saveMod131(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.saveMod131(ctx, mod131));		
	}
	@Override
	public Mod131 saveCommentsMod131(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.saveCommentsMod131(ctx, mod131));		
	}
	@Override
	public Mod131 initializeForFinishMod131(AONContext ctx, Mod131 mod131){
		return Mod131DAO.initializeForFinish(ctx, mod131);
	}
	@Override
	public Mod131 finishMod131(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.finish(ctx, mod131));		
	}
	@Override
	public Mod131 reopenMod131(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.reopen(ctx, mod131));		
	}
	
	@Override
	public void deleteMod131(AONContext ctx, Mod131 mod131) {
		ctx.getDslContext().transaction(
				configuration -> Mod131DAO.delete(ctx, mod131));
	}

	@Override
	public Mod131 initializeMod131(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.initializeMod131(ctx,mod131);
	}

	@Override
	public Mod131 createMod131(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.createMod131(ctx,mod131);
	}
	@Override
	public String getMod131Info(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		return Mod131DAO.getMod131Info(ctx,mod131,script,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 202]
	@Override
	public Mod202 getMod202(AONContext ctx, int id) {
		return Mod202DAO.getMod202(ctx, id);
	}
	@Override
	public LinkedList<Mod202> getMod202s(AONContext ctx, int domain) {
		LinkedList<Mod202> list = new LinkedList<Mod202>();
		Mod202DAO.getMod202s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod202 calculateMod202(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.calculateMod202(ctx, mod202);
	}
	@Override
	public Mod202 saveMod202(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.saveMod202(ctx, mod202));
	}

	@Override
	public void deleteMod202(AONContext ctx, Mod202 mod202) {
		ctx.getDslContext().transaction(
				configuration -> Mod202DAO.delete(ctx, mod202));
	}

	@Override
	public Mod202 initializeMod202(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.initializeMod202(ctx,mod202);
	}
	@Override
	public Mod202 saveCommentsMod202(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.saveCommentsMod202(ctx, mod202));		
	}
	@Override
	public Mod202 initializeForFinishMod202(AONContext ctx, Mod202 mod202){
		return Mod202DAO.initializeForFinish(ctx, mod202);
	}
	@Override
	public Mod202 finishMod202(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.finish(ctx, mod202));		
	}
	@Override
	public Mod202 reopenMod202(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.reopen(ctx, mod202));		
	}

	@Override
	public Mod202 createMod202(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.createMod202(ctx,mod202);
	}
	@Override
	public String getMod202Info(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) {
		return Mod202DAO.getMod202Info(ctx,mod202,script,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 200]
	@Override
	public LinkedList<Mod200> getMod200s(AONContext ctx, int domain) {
		LinkedList<Mod200> list = new LinkedList<Mod200>();
		Mod200DAO.getMod200s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	// ----------------------------------------------------------- [MODELO 200 - 2013]
	@Override
	public Mod2002013 createMod2002013(AONContext ctx, int year) {
		return Mod2002013DAO.createNewMod200(ctx,year);
	}
	@Override
	public Mod2002013 initializeNewMod2002013(AONContext ctx, Mod2002013 mod200) {
		return Mod2002013DAO.initializeNewMod200(ctx,mod200);
	}
	
	@Override
	public Mod2002013 initializeMod2002013(AONContext ctx, Mod2002013 mod200) {
		return Mod2002013DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002013 getMod2002013ByYear(AONContext ctx, int year) {
		return Mod2002013DAO.getByYear(ctx,year);
	}

	@Override
	public Mod2002013 getMod2002013ById(AONContext ctx, int id) {
		return Mod2002013DAO.getById(ctx,id);
	}
	@Override
	public Mod2002013 calculateMod2002013(Mod2002013 mod200) {
		return Mod2002013DAO.calculate(mod200);
	}
	@Override
	public Mod2002013 validateMod2002013(Mod2002013 mod200) {
		return Mod2002013DAO.validate(mod200);
	}
	@Override
	public Mod2002013 saveMod2002013(AONContext ctx, Mod2002013 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002013DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002013(AONContext ctx, int id) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002013DAO.delete(ctx, id));
	}

	@Override
	public String dumpAEATMod2002013(Mod2002013 mod200) {
		return Mod2002013DAO.dumpAEAT(mod200);
	}

	// ----------------------------------------------------------- [MODELO 200 - 2014]
	@Override
	public Mod2002014 createMod2002014(AONContext ctx, int year) {
		return Mod2002014DAO.createNewMod200(ctx,year);
	}
	@Override
	public Mod2002014 initializeNewMod2002014(AONContext ctx, Mod2002014 mod200) {
		return Mod2002014DAO.initializeNewMod200(ctx,mod200);
	}
	
	@Override
	public Mod2002014 initializeMod2002014(AONContext ctx, Mod2002014 mod200) {
		return Mod2002014DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002014 getMod2002014ByYear(AONContext ctx, int year) {
		return Mod2002014DAO.getByYear(ctx,year);
	}

	@Override
	public Mod2002014 getMod2002014ById(AONContext ctx, int id) {
		return Mod2002014DAO.getById(ctx,id);
	}
	@Override
	public Mod2002014 calculateMod2002014(Mod2002014 mod200) {
		return Mod2002014DAO.calculate(mod200);
	}
	@Override
	public Mod2002014 validateMod2002014(Mod2002014 mod200) {
		return Mod2002014DAO.validate(mod200);
	}
	@Override
	public Mod2002014 saveMod2002014(AONContext ctx, Mod2002014 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002014DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002014(AONContext ctx, int id) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002014DAO.delete(ctx, id));
	}

	@Override
	public String dumpAEATMod2002014(Mod2002014 mod200) {
		return Mod2002014DAO.dumpAEAT(mod200);
	}

	@Override
	public Mod2002014 importMod2002013(AONContext ctx, Mod2002014 mod200) {
		return Mod2002014DAO.importMod2002013(ctx,mod200);
	}

	// ----------------------------------------------------------- [MODELO 200 - 2015]
	@Override
	public Mod2002015 createMod2002015(AONContext ctx, int year) {
		return Mod2002015DAO.createNewMod200(ctx,year);
	}
	@Override
	public Mod2002015 initializeNewMod2002015(AONContext ctx, Mod2002015 mod200) {
		return Mod2002015DAO.initializeNewMod200(ctx,mod200);
	}
	
	@Override
	public Mod2002015 initializeMod2002015(AONContext ctx, Mod2002015 mod200) {
		return Mod2002015DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002015 getMod2002015ByYear(AONContext ctx, int year) {
		return Mod2002015DAO.getByYear(ctx,year);
	}

	@Override
	public Mod2002015 getMod2002015ById(AONContext ctx, int id) {
		return Mod2002015DAO.getById(ctx,id);
	}
	@Override
	public Mod2002015 calculateMod2002015(Mod2002015 mod200) {
		return Mod2002015DAO.calculate(mod200);
	}
	@Override
	public Mod2002015 validateMod2002015(Mod2002015 mod200) {
		return Mod2002015DAO.validate(mod200);
	}
	@Override
	public Mod2002015 saveMod2002015(AONContext ctx, Mod2002015 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002015DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002015(AONContext ctx, int id) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002015DAO.delete(ctx, id));
	}

	@Override
	public String dumpAEATMod2002015(Mod2002015 mod200) {
		return Mod2002015DAO.dumpAEAT(mod200);
	}

	@Override
	public Mod2002015 importMod2002014(AONContext ctx, Mod2002015 mod200) {
		return Mod2002015DAO.importMod2002014(ctx,mod200);
	}
	
	// ----------------------------------------------------------- [MODELO 200 - 2016]
	@Override
	public Mod2002016 createMod2002016(AONContext ctx, int year) {
		return Mod2002016DAO.createNewMod200(ctx,year);
	}
	@Override
	public Mod2002016 initializeNewMod2002016(AONContext ctx, Mod2002016 mod200) {
		return Mod2002016DAO.initializeNewMod200(ctx,mod200);
	}
	
	@Override
	public Mod2002016 initializeMod2002016(AONContext ctx, Mod2002016 mod200) {
		return Mod2002016DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002016 getMod2002016ByYear(AONContext ctx, int year) {
		return Mod2002016DAO.getByYear(ctx,year);
	}

	@Override
	public Mod2002016 getMod2002016ById(AONContext ctx, int id) {
		return Mod2002016DAO.getById(ctx,id);
	}
	@Override
	public Mod2002016 calculateMod2002016(Mod2002016 mod200) {
		return Mod2002016DAO.calculate(mod200);
	}
	@Override
	public Mod2002016 validateMod2002016(Mod2002016 mod200) {
		return Mod2002016DAO.validate(mod200);
	}
	@Override
	public Mod2002016 saveMod2002016(AONContext ctx, Mod2002016 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002016DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002016(AONContext ctx, int id) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002016DAO.delete(ctx, id));
	}

	@Override
	public String dumpAEATMod2002016(Mod2002016 mod200) {
		return Mod2002016DAO.dumpAEAT(mod200);
	}

	@Override
	public Mod2002016 importMod2002015(AONContext ctx, Mod2002016 mod200) {
		return Mod2002016DAO.importMod2002015(ctx,mod200);
	}
	
	
	@Override
	public Stream<VatSummaryContext> getVatSummaryContext(AONContext ctx, VatParams params) {
		return VATDAO.getVatSummary(ctx, params.getFromDate()
				,params.getToDate(),p -> FinanceUtils.getVATFilter(p, params))
				.stream();
	}
	
	@Override
	public Stream<VatContext> getVatContext(AONContext ctx, VatParams params) {
		return VATDAO.getVatBreakdown(ctx, params.getFromDate()
				,params.getToDate(),p -> FinanceUtils.getVATFilter(p, params));
	}
	
	@Override
	public Stream<VatContext> getSiiVatContext(AONContext ctx, VatParams params, String sii) {
		return VATDAO.getSiiVatContext(ctx, p -> FinanceUtils.getVATFilter(p, params), sii);
	}
}
