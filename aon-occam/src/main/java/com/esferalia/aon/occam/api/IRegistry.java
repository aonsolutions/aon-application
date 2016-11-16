package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.Registry;

public interface IRegistry {
	
	public Category getCategory(AONContext ctx, Integer categoryId);
	public LinkedList<Category> getCategoryList(AONContext ctx);
	
	public Stream<Creditor> getBasicCreditors(AONContext ctx, CreditorFilter filter);
	
	
	public Registry getRegistry(AONContext ctx, String name);
	public Registry getRegistry(AONContext ctx, Integer domainId, String name);
	public Registry getRegistry(AONContext ctx, Integer id);

}
