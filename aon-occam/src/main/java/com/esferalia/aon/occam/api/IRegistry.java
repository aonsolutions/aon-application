package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

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

public interface IRegistry {
	
	public Category getCategory(AONContext ctx, Integer categoryId);
	public LinkedList<Category> getCategoryList(AONContext ctx);
	
	public Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter);
	
	
	public Registry getRegistry(AONContext ctx, String name);
	public Registry getRegistry(AONContext ctx, Integer domainId, String name);
	public Registry getRegistry(AONContext ctx, Integer id);

	
	public Stream<RegistryMedia> getRMediaStream(AONContext ctx, RegistryMediaFilter filter);
	public Stream<RegistryNote> getRNoteStream(AONContext ctx, RegistryNoteFilter filter);

	public Stream<Segment> getRSegmentStream(AONContext ctx, Integer registryId);

	public Stream<Seller> getRSellerStream(AONContext ctx, Integer registryId);

	public Stream<RAddress> getRAddressStream(AONContext ctx, Integer registryId);
	
}
