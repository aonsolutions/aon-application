package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRegistry;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;

public class RegistryImpl implements IRegistry{
	
	// ------------------------------------- CATEGORY
	
	@Override
	public Category getCategory(AONContext ctx, Integer categoryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategory(ctx, categoryId));
	}
	
	@Override
	public LinkedList<Category> getCategoryList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCategoryList(ctx));
	}

	// ------------------------------------- CREDITOR

	@Override
	public Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> CreditorDAO.getBasicCreditors(ctx, filter));
	}
	
	
	

	@Override
	public Registry getRegistry(AONContext ctx, String name) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistry(ctx, name));
	}
	
	@Override
	public Registry getRegistry(AONContext ctx, Integer domainId, String name) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistry(ctx, domainId, name));
	}
	
	@Override
	public Registry getRegistry(AONContext ctx, Integer id) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRegistry(ctx, id));
	}
	
	// ------------------------------------- RMEDIA

	@Override
	public Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRMediaStream(ctx, filter));
	}

	// ------------------------------------- RNOTE
	
	@Override
	public Stream<RegistryNote> getRNoteStream(AONContext ctx, RegistryNoteFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRNoteStream(ctx, filter));
	}
	
	
}
