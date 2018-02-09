package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.CommissionCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionItemFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.OfferDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.commission.Commission;
import com.esferalia.aon.occam.api.model.commission.CommissionCategory;
import com.esferalia.aon.occam.api.model.commission.CommissionItem;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.commission.CommissionTypeCommission;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;

public interface ICommission {
	
	public Stream<Commission> getCommissionStream(AONContext ctx, CommissionFilter filter);
	public Stream<CommissionType> getCommissionTypeStream(AONContext ctx, CommissionTypeFilter filter);
	public Stream<CommissionTypeCommission> getCommissionTypeCommissionStream(AONContext ctx, CommissionTypeCommissionFilter filter);
	public Stream<CommissionCategory> getCommissionCategoryStream(AONContext ctx, CommissionCategoryFilter filter);
	public Stream<CommissionItem> getCommissionItemStream(AONContext ctx, CommissionItemFilter filter);
	
	public Stream<OfferDetailCommission> getOfferDetailCommissionStream(AONContext ctx, OfferDetailCommissionFilter filter);
	public OfferDetailCommission insertOfferDetailCommission(AONContext ctx, OfferDetailCommission odc);
	public OfferDetailCommission updateOfferDetailCommission(AONContext ctx, OfferDetailCommission odc);	
	
 	public Stream<InvoiceDetailCommission> getInvoiceDetailCommissionStream(AONContext ctx, InvoiceDetailCommissionFilter filter);
	public InvoiceDetailCommission insertInvoiceDetailCommission(AONContext ctx, InvoiceDetailCommission idc);
	public InvoiceDetailCommission updateInvoiceDetailCommission(AONContext ctx, InvoiceDetailCommission idc);	
}
