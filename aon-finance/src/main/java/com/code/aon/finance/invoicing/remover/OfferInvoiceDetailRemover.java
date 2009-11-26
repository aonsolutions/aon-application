package com.code.aon.finance.invoicing.remover;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.InvoicingException;
import com.code.aon.ql.Criteria;

public class OfferInvoiceDetailRemover implements IInvoiceDetailRemover {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OfferInvoiceDetailRemover.class.getName());

	@Override
	public boolean accept(InvoiceSource source) {
		return source.equals(InvoiceSource.OFFER);
	}

	@Override
	public void removeDetail(InvoiceDetail invoiceDetail) throws InvoicingException{
		try {
			if (invoiceDetail.getSourceId() != null) {
				IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
				OfferDetail offerDetail = (OfferDetail)offerDetailBean.get(invoiceDetail.getSourceId());
				offerDetail.setStatus(OfferDetailStatus.PENDING);
				offerDetailBean.update(offerDetail);

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offerDetail.getOffer().getId());
				criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_STATUS), OfferDetailStatus.ON_INVOICE);
				if (offerDetailBean.getCount(criteria) == 0) {
					IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
					Offer offer = offerDetail.getOffer();
					offer.setStatus(OfferStatus.PENDING);
					offerBean.update(offer);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error removing Details", e);
			throw new InvoicingException(e.getMessage(),e);
		}
	}

}