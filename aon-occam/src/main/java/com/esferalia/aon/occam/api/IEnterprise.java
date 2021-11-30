package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseCCCFilter;

public interface IEnterprise {
	
	
	//---------ENTEPRISE_CCC--------
	public EnterpriseCCC saveEnterpriseCCC(AONContext ctx, EnterpriseCCC ec);
	
	public Stream<EnterpriseCCC> getEnterpriseCCCStream(AONContext ctx, EnterpriseCCCFilter filter);
	
	public void deleteEnterpriseCCC(AONContext ctx, Integer id);
	
	public EnterpriseCCC getEnterpriseCCC(AONContext ctx, EnterpriseCCCFilter filter);
}
