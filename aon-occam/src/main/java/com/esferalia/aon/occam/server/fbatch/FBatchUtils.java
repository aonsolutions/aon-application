package com.esferalia.aon.occam.server.fbatch;

import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.FBatchProperties;
import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FBatchUtils {

	public static Filter getFilter(FBatchProperties p, FBatchParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());

		if (AonStringUtils.isNotBlank(params.getDescription())) {
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

		if (params.getFbatchType() == null)
			throw new AonCoreException(AonError.EMPTY_DATA.format("Tipo de remesa"));

		prop = prop.and(p.getPaymentProperty().eq(params.getFbatchType().getPayment()));

		// PAYMENT y PAYROLL_PAYMENT comparten payment=1; el tipo 10 los separa.
		if (FBatchType.PAYROLL_PAYMENT == params.getFbatchType())
			prop = prop.and(p.getTypeProperty().eq((byte) 10));
		else if (FBatchType.PAYMENT == params.getFbatchType())
			prop = prop.and(p.getTypeProperty().ne((byte) 10));

		if (params.getType() != null) {
			if (!params.getFbatchType().isSelectable(params.getType()))
				throw new AonCoreException(AonError.INVALID_DATA.format("Tipo de fichero"));
			prop = prop.and(p.getTypeProperty().eq(params.getType()));
		}

		if (params.getStatus() != null) {
			prop = prop.and(p.getStatusProperty().eq(params.getStatus()));
		}

		if (params.getConfidential() != null) {
			prop = prop.and(p.getConfidentialProperty().eq(params.getConfidential() ? (byte) 1 : (byte) 0));
		}

		return prop;
	}
	
	private static Filter anyType(FBatchProperties p, byte[] codes) {
	    Filter f = null;
	    for (byte c : codes)
	        f = (f == null) ? p.getTypeProperty().eq(c) : f.or(p.getTypeProperty().eq(c));
	    return f;
	}

}
