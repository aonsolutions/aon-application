package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalActivityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalMatrixDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod111DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod131DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod180DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod184DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod190DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod193DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod202DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902014DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod3902015DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.Mod2002013DAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.Mod2002014DAO;

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

	// --------------------------------------------- [FISCAL ACTIVITIES]
	@Override
	public FiscalActivity calculate(AONContext ctx, FiscalActivity fa) {
		return FiscalActivityDAO.calculate(ctx, fa);
	}
	@Override
	public FiscalActivity save(AONContext ctx, FiscalActivity fa) {
		return ctx.getDslContext().transactionResult(
				configuration -> FiscalActivityDAO.save(ctx, fa));
	}
	@Override
	public void delete(AONContext ctx, FiscalActivity fa) {
		ctx.getDslContext().transaction(
				configuration -> FiscalActivityDAO.delete(ctx, fa));
	}
	@Override
	public FiscalActivity getActivity(AONContext ctx, int id) {
		return FiscalActivityDAO.getActivity(ctx, id);
	}

	@Override
	public LinkedList<FiscalActivity> getActivities(AONContext ctx, int domainId) {
		return FiscalActivityDAO.getActivities(ctx, domainId);
	}

	@Override
	public FiscalActivity getActivityFor(AONContext ctx, Epigraph epigraph, FiscalActivity fa) {
		return FiscalActivityDAO.getActivityFor(ctx, epigraph, fa);
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
		return FiscalModelDAO.getModel(ctx, id);
	}

	@Override
	public LinkedList<FiscalModel> getModels(AONContext ctx, int domain) {
		LinkedList<FiscalModel> list = new LinkedList<FiscalModel>();
		FiscalModelDAO.getModels(ctx, domain)
			.forEach(list::add);
		return list;
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
		return Mod111DAO.saveMod111(ctx, mod111);
	}

	@Override
	public void deleteMod111(AONContext ctx, Mod111 mod111) {
		Mod111DAO.delete(ctx, mod111);
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
	public String getMod111Info(AONContext ctx, Mod111 mod111, Mod111Key key, Mod111KeyInfo infoKey) {
		return Mod111DAO.getMod111Info(ctx,mod111,key,infoKey);
	}

	// ----------------------------------------------------------- [MODELO 131]
	@Override
	public Mod131 getMod131(AONContext ctx, int id) {
		return Mod131DAO.getMod131(ctx, id);
	}

	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain) {
		LinkedList<Mod131> list = new LinkedList<Mod131>();
		Mod131DAO.getMod131s(ctx, domain).forEach(list::add);
		return list;
	}
	
	@Override
	public Mod131 calculateMod131(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.calculateMod131(ctx, mod131);
	}
	@Override
	public Mod131 saveMod131(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.saveMod131(ctx, mod131);
	}
	
	@Override
	public void deleteMod131(AONContext ctx, Mod131 mod131) {
		Mod131DAO.delete(ctx, mod131);
	}

	@Override
	public Mod131 initializeMod131(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.initializeMod131(ctx,mod131);
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
		return Mod202DAO.saveMod202(ctx, mod202);
	}

	@Override
	public void deleteMod202(AONContext ctx, Mod202 mod202) {
		Mod202DAO.delete(ctx, mod202);
	}

	@Override
	public Mod202 initializeMod202(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.initializeMod202(ctx,mod202);
	}

	// ----------------------------------------------------------- [MODELO 200 - 2013]
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
}
