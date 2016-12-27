package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRegistry;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.RegistryMediaFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
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

	@Override
	public Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRSegmentStream(ctx, registryId));
	}

	@Override
	public Stream<Seller> getRSellerStream(AONContext ctx, Integer registryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRSellerStream(ctx, registryId));
	}

	@Override
	public Stream<RAddress> getRAddressStream(AONContext ctx, Integer registryId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getRAddressStream(ctx, registryId));
	}

	@Override
	public Registry insertRegistry(AONContext ctx, Registry registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRegistry(ctx, registry));
	}

	@Override
	public Registry updateRegistry(AONContext ctx, Registry registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRegistry(ctx, registry));
	}

	@Override
	public Registry deleteRegistry(AONContext ctx, Integer registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteRegistry(ctx, registry));
	}

	@Override
	public RegistryMedia insertRMedia(AONContext ctx, RegistryMedia rmedia) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.insertRMedia(ctx, rmedia));
	}

	@Override
	public RegistryMedia updateRMedia(AONContext ctx, RegistryMedia rmedia) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.updateRMedia(ctx, rmedia));
	}

	@Override
	public RegistryMedia deleteRMedia(AONContext ctx, Integer registry) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.deleteRMedia(ctx, registry));

	}
	
	@Override
	public Customer getCustomer(AONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(
				configuration -> RegistryDAO.getCustomer(ctx, registry));

	}
	
	
}
