package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.EnterpriseData;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseCCCFilter;
import com.esferalia.aon.occam.api.model.Filter.EnterpriseDataFilter;

public interface IEnterprise {
	
	
	//---------ENTEPRISE_CCC--------
	public EnterpriseCCC saveEnterpriseCCC(AONContext ctx, EnterpriseCCC ec);
	
	public Stream<EnterpriseCCC> getEnterpriseCCCStream(AONContext ctx, EnterpriseCCCFilter filter);
	
	public void deleteEnterpriseCCC(AONContext ctx, Integer id);
	
	public EnterpriseCCC getEnterpriseCCC(AONContext ctx, EnterpriseCCCFilter filter);
	
	//---------ENTEPRISE_DATA--------
	
	public LinkedList<EnterpriseData> getEnterpriseDataList(AONContext ctx, EnterpriseDataFilter filter);
	
	public void insertEnterpriseData(AONContext ctx, List<EnterpriseData> enterpriseData);
	
	public void updateEnterpriseData(AONContext ctx, EnterpriseData enterpriseData);
	
	public void deleteEnterpriseData(AONContext ctx, Integer id);
}
