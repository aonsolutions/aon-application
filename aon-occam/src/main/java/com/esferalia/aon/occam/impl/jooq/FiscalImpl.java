package com.esferalia.aon.occam.impl.jooq;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.impl.jooq.dao.Mod180DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod190DAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390DAO;

public class FiscalImpl implements IFiscal {

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
	
	@Override
	public ArrayList<Mod390> getMod390s(AONContext ctx, int domain) {
		return Mod390DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod390 getMod390(AONContext ctx, Integer id) {
		return Mod390DAO.getById(ctx, id);
	}

	@Override
	public String getMod390XML(AONContext ctx, int id) {
		return Mod390DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod390 initializeMod390(AONContext ctx, int year) {
		return Mod390DAO.initialize(ctx, year);
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
		return Mod390DAO.getMod390Details(ctx, mod390);
	}

}
