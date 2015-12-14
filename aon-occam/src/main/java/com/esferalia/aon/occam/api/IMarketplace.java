package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.office.Tag;

public interface IMarketplace {
	public LinkedList<Tag> getMarketplaceTagList(AONContext ctx);
}
