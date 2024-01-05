package com.esferalia.aon.occam.server.fbatch;

import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.FBatchProperties;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FBatchUtils {

	public static Filter getFilter(FBatchProperties p, FBatchParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		
		if(AonStringUtils.isNotBlank(params.getDescription())) {
			prop = prop.and(p.getDescriptionProperty().like("%" + params.getDescription() + "%"));
		}
		
		if (params.getFromIssueDate() != null) {
			prop = prop.and(p.getIssueDateProperty().ge(params.getFromIssueDate()));
		}
		
		if (params.getToIssueDate() != null) {
			prop = prop.and(p.getIssueDateProperty().le(params.getToIssueDate()));
		}
		
		if (params.getRbank() != null) {
			prop = prop.and(p.getRBankProperty().eq(params.getRbank()));
		}
		
		if (params.getType() != null) {
			if(params.getType() == -1) prop = prop.and(p.getTypeProperty().eq((byte)0).or(p.getTypeProperty().eq((byte)9)));
			else prop = prop.and(p.getTypeProperty().eq(params.getType()));
		}
		
		if (params.getStatus() != null) {
			prop = prop.and(p.getStatusProperty().eq(params.getStatus()));
		}
		
		if (params.getConfidential() != null) {
			prop = prop.and(p.getConfidentialProperty().eq(params.getConfidential() ? (byte)1 : (byte)0));
		}

		return prop;
	}

}
