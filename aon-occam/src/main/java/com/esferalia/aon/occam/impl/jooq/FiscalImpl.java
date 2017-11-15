package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
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
import com.esferalia.aon.occam.api.model.type.Mod303Key;
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
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod349DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902014DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902015DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390DAO;
import com.esferalia.aon.occam.impl.jooq.dao.VATDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.Mod2002013DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.Mod2002014DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.Mod2002015DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2016.Mod2002016DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod303.change.VatToMod303;
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
	
	@Override
	public Mod180 saveCommentsMod180(AONContext ctx, Mod180 mod180) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.saveComments(ctx, mod180));		
	}
	@Override
	public Mod180 changeStatusMod180(AONContext ctx, Mod180 mod180, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.changeStatusMod180(ctx, mod180, newStatus));		
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
	
	@Override
	public Mod190 saveCommentsMod190(AONContext ctx, Mod190 mod190) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.saveComments(ctx, mod190));		
	}
	@Override
	public Mod190 changeStatusMod190(AONContext ctx, Mod190 mod190, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.changeStatus(ctx, mod190, newStatus));		
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

	@Override
	public Mod193 saveCommentsMod193(AONContext ctx, Mod193 mod193) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.saveComments(ctx, mod193));		
	}
	@Override
	public Mod193 changeStatusMod193(AONContext ctx, Mod193 mod193, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.changeStatus(ctx, mod193, newStatus));		
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
	
	// ----------------------------------------------------------- [MODELO 303]
	@Override
	public Mod303 getMod303(AONContext ctx, int id) {
		return Mod303DAO.getMod303(ctx, id);
	}
	@Override
	public LinkedList<Mod303> getMod303s(AONContext ctx, int domain) {
		LinkedList<Mod303> list = new LinkedList<Mod303>();
		Mod303DAO.getMod303s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod303 calculateMod303(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.calculateMod303(ctx, mod303);
	}
	@Override
	public Mod303 saveMod303(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.saveMod303(ctx, mod303));		
	}
	@Override
	public Mod303 saveCommentsMod303(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.saveCommentsMod303(ctx, mod303));		
	}
	@Override
	public Mod303 initializeForFinishMod303(AONContext ctx, Mod303 mod303){
		return Mod303DAO.initializeForFinish(ctx, mod303);
	}
	@Override
	public Mod303 markAsFinishedMod303(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsFinished(ctx, mod303));		
	}
	@Override
	public Mod303 markAsPendingMod303(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsPending(ctx, mod303));		
	}
	@Override
	public Mod303 markAsSentMod303(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsSent(ctx, mod303));		
	}
	
	@Override
	public void deleteMod303(AONContext ctx, Mod303 mod303) {
		ctx.getDslContext().transaction(
				configuration -> Mod303DAO.delete(ctx, mod303));
	}

	@Override
	public Mod303 initializeMod303(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.initializeMod303(ctx,mod303);
	}

	@Override
	public Mod303 createMod303(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.createMod303(ctx,mod303);
	}
	@Override
	public Mod303 declarationChanged(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.declarationChanged(ctx,mod303);
	}

	@Override
	public String getMod303Info(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) {
		return Mod303DAO.getMod303Info(ctx,mod303,script,infoKey);
	}

	@Override
	public void importMod303(AONContext ctx, int domain) {
		ctx.getDslContext().transaction(configuration -> VatToMod303.importModels(ctx, domain));
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
	public Mod111 calculate(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.calculateMod111(ctx, mod111);
	}
	@Override
	public Mod111 save(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.saveMod111(ctx, mod111));		
	}
	@Override
	public Mod111 saveComments(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.saveCommentsMod111(ctx, mod111));		
	}
	@Override
	public Mod111 initializeForFinish(AONContext ctx, Mod111 mod111){
		return Mod111DAO.initializeForFinish(ctx, mod111);
	}
	@Override
	public Mod111 markAsFinished(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsFinished(ctx, mod111));		
	}
	@Override
	public Mod111 markAsPending(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsPending(ctx, mod111));		
	}
	
	@Override
	public Mod111 markAsSent(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsSent(ctx, mod111));		
	}

	@Override
	public void delete(AONContext ctx, Mod111 mod111) {
		ctx.getDslContext().transaction(
				configuration -> Mod111DAO.delete(ctx, mod111));
	}

	@Override
	public Mod111 initialize(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.initializeMod111(ctx,mod111);
	}

	@Override
	public Mod111 create(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.createMod111(ctx,mod111);
	}
	@Override
	public String getInfo(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) {
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
	public Mod115 calculate(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.calculateMod115(ctx, mod115);
	}
	@Override
	public Mod115 save(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.saveMod115(ctx, mod115));		
	}
	@Override
	public Mod115 saveComments(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.saveCommentsMod115(ctx, mod115));		
	}
	@Override
	public Mod115 initializeForFinish(AONContext ctx, Mod115 mod115){
		return Mod115DAO.initializeForFinish(ctx, mod115);
	}
	@Override
	public Mod115 markAsFinished(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsFinished(ctx, mod115));		
	}
	@Override
	public Mod115 markAsSent(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsSent(ctx, mod115));		
	}
	@Override
	public Mod115 markAsPending(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsPending(ctx, mod115));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod115 mod115) {
		ctx.getDslContext().transaction(
				configuration -> Mod115DAO.delete(ctx, mod115));
	}

	@Override
	public Mod115 initialize(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.initializeMod115(ctx,mod115);
	}

	@Override
	public Mod115 create(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.createMod115(ctx,mod115);
	}
	@Override
	public String getInfo(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) {
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
	public Mod123 calculate(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.calculateMod123(ctx, mod123);
	}
	@Override
	public Mod123 save(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveMod123(ctx, mod123));		
	}
	@Override
	public Mod123 saveComments(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveCommentsMod123(ctx, mod123));		
	}
	@Override
	public Mod123 initializeForFinish(AONContext ctx, Mod123 mod123){
		return Mod123DAO.initializeForFinish(ctx, mod123);
	}
	@Override
	public Mod123 markAsFinished(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsFinished(ctx, mod123));		
	}
	@Override
	public Mod123 markAsSent(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsSent(ctx, mod123));		
	}
	@Override
	public Mod123 markAsPending(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsPending(ctx, mod123));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod123 mod123) {
		ctx.getDslContext().transaction(
				configuration -> Mod123DAO.delete(ctx, mod123));
	}

	@Override
	public Mod123 initialize(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.initializeMod123(ctx,mod123);
	}

	@Override
	public Mod123 create(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.createMod123(ctx,mod123);
	}
	@Override
	public String getInfo(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
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
	public Mod130 calculate(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.calculateMod130(ctx, mod130);
	}
	@Override
	public Mod130 save(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.saveMod130(ctx, mod130));		
	}
	@Override
	public Mod130 saveComments(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.saveCommentsMod130(ctx, mod130));		
	}
	@Override
	public Mod130 initializeForFinish(AONContext ctx, Mod130 mod130){
		return Mod130DAO.initializeForFinish(ctx, mod130);
	}
	@Override
	public Mod130 markAsFinished(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsFinished(ctx, mod130));		
	}
	@Override
	public Mod130 markAsSent(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsSent(ctx, mod130));		
	}
	@Override
	public Mod130 markAsPending(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsPending(ctx, mod130));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod130 mod130) {
		ctx.getDslContext().transaction(
				configuration -> Mod130DAO.delete(ctx, mod130));
	}

	@Override
	public Mod130 initialize(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.initializeMod130(ctx,mod130);
	}

	@Override
	public Mod130 create(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.createMod130(ctx,mod130);
	}
	@Override
	public String getInfo(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) {
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
	public Mod131 calculate(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.calculateMod131(ctx, mod131);
	}
	@Override
	public Mod131Activity calculateActivity(AONContext ctx, Mod131Activity activity) {
		return Mod131DAO.calculateMod131Activity(ctx, activity);
	}
	
	@Override
	public Mod131 save(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.saveMod131(ctx, mod131));		
	}
	@Override
	public Mod131 saveComments(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.saveCommentsMod131(ctx, mod131));		
	}
	@Override
	public Mod131 initializeForFinish(AONContext ctx, Mod131 mod131){
		return Mod131DAO.initializeForFinish(ctx, mod131);
	}
	@Override
	public Mod131 markAsFinished(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsFinished(ctx, mod131));		
	}
	@Override
	public Mod131 markAsSent(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsSent(ctx, mod131));		
	}
	@Override
	public Mod131 markAsPending(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsPending(ctx, mod131));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod131 mod131) {
		ctx.getDslContext().transaction(
				configuration -> Mod131DAO.delete(ctx, mod131));
	}

	@Override
	public Mod131 initialize(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.initializeMod131(ctx,mod131);
	}

	@Override
	public Mod131 create(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.createMod131(ctx,mod131);
	}
	@Override
	public String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
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
	public Mod202 calculate(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.calculateMod202(ctx, mod202);
	}
	@Override
	public Mod202 save(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.saveMod202(ctx, mod202));
	}

	@Override
	public void delete(AONContext ctx, Mod202 mod202) {
		ctx.getDslContext().transaction(
				configuration -> Mod202DAO.delete(ctx, mod202));
	}

	@Override
	public Mod202 initialize(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.initializeMod202(ctx,mod202);
	}
	@Override
	public Mod202 saveComments(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.saveCommentsMod202(ctx, mod202));		
	}
	@Override
	public Mod202 initializeForFinish(AONContext ctx, Mod202 mod202){
		return Mod202DAO.initializeForFinish(ctx, mod202);
	}
	@Override
	public Mod202 markAsFinished(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.markAsFinished(ctx, mod202));		
	}
	@Override
	public Mod202 markAsSent(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.markAsSent(ctx, mod202));		
	}
	@Override
	public Mod202 markAsPending(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.markAsPending(ctx, mod202));		
	}

	@Override
	public Mod202 create(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.createMod202(ctx,mod202);
	}
	@Override
	public String getInfo(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) {
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
	
	// ---------------------------------------------------- [MODELO 349]
	@Override
	public LinkedList<Mod349> getMod349s(AONContext ctx, int domain) {
		return Mod349DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod349 getMod349(AONContext ctx, Integer id) {
		return Mod349DAO.getById(ctx, id);
	}

	@Override
	public Mod349 initializeMod349(AONContext ctx) {
		return Mod349DAO.initialize(ctx);		
	}

	@Override
	public Mod349 saveMod349(AONContext ctx, Mod349 mod349) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.save(ctx, mod349));
	}

	@Override
	public void deleteMod349(AONContext ctx, Mod349 mod349) {
		ctx.getDslContext().transaction(
				configuration -> Mod349DAO.delete(ctx, mod349));
	}

	@Override
	public Mod349Detail getMod349Detail(AONContext ctx, Mod349 mod349) {
		return Mod349DAO.getDetail(ctx, mod349);
	}
	
	@Override
	public Mod349 saveCommentsMod349(AONContext ctx, Mod349 mod349) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.saveComments(ctx, mod349));		
	}
	@Override
	public Mod349 changeStatusMod349(AONContext ctx, Mod349 mod349, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.changeStatusMod349(ctx, mod349, newStatus));		
	}	
	
}
