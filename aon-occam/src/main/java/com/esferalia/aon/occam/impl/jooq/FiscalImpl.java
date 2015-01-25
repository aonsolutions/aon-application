package com.esferalia.aon.occam.impl.jooq;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.impl.jooq.dao.Mod180DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod190DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod193DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390DAO;

public class FiscalImpl implements IFiscal {

	// ----------------------------------------------------------- [MODELO 180]
	@Override
	public ArrayList<Mod180> getMod180s(AONContext ctx, int domain) {
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
	public ArrayList<Mod190> getMod190s(AONContext ctx, int domain) {
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
	public ArrayList<Mod193> getMod193s(AONContext ctx, int domain) {
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
	public Mod193Detail getMod193Detail(AONContext ctx, Integer id) {
		return Mod193DAO.getDetail(ctx, id);
	}
	// ----------------------------------------------------------- [MODELO 390]
	@Override
	public ArrayList<Mod390> getMod390s(AONContext ctx, int domain) {
		try {
			return Mod390DAO.getByDomain(ctx, domain);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public Mod390 getMod390(AONContext ctx, Integer id) {
		try {
			return Mod390DAO.getById(ctx, id);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public String getMod390XML(AONContext ctx, int id) {
		return Mod390DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod390 initializeMod390(AONContext ctx, int year) {
		try {
			return Mod390DAO.initialize(ctx, year);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

	@Override
	public Mod390 saveMod390(AONContext ctx, Mod390 mod390) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod390DAO.save(ctx, mod390));
	}

	@Override
	public void deleteMod390(AONContext ctx, Mod390 mod390) {
		ctx.getDslContext().transaction(
				configuration -> Mod390DAO.delete(ctx, mod390));
	}

	@Override
	public ArrayList<Mod390Detail> getMod390Details(AONContext ctx, Mod390 mod390) {
		try {
			return Mod390DAO.getMod390Details(ctx, mod390);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}

}
