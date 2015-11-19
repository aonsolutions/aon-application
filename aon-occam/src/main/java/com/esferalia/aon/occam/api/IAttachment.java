package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;


public interface IAttachment {
	public Attach getRattach(AONContext ctx, AttachFilter filter);
	public LinkedList<Attach> getRattachList(AONContext ctx, AttachFilter filter);
	
	public Integer insertContractAttach(AONContext ctx, Attach attach);
	public Integer insertItemAttach(AONContext ctx, Attach attach);
	public Integer insertInvoiceAttach(AONContext ctx, Attach attach);
	public Integer insertOfferAttach(AONContext ctx, Attach attach);
	public Integer insertPayrollAttach(AONContext ctx, Attach attach);
	public Integer insertProjectAttach(AONContext ctx, Attach attach);
	public Integer insertRegistryAttach(AONContext ctx, Attach attach);
	public Integer insertSepeAttach(AONContext ctx, Attach attach);
	
	public void updateContractAttach(AONContext ctx, Attach attach);
	public void updateItemAttach(AONContext ctx, Attach attach);
	public void updateInvoiceAttach(AONContext ctx, Attach attach);
	public void updateOfferAttach(AONContext ctx, Attach attach);
	public void updatePayrollAttach(AONContext ctx, Attach attach);
	public void updateProjectAttach(AONContext ctx, Attach attach);
	public void updateRegistryAttach(AONContext ctx, Attach attach);
	public void updateSepeAttach(AONContext ctx, Attach attach);
	
	public void deleteContractAttach(AONContext ctx, AttachFilter filter);
	public void deleteItemAttach(AONContext ctx, AttachFilter filter);
	public void deleteInvoiceAttach(AONContext ctx, AttachFilter filter);
	public void deleteOfferAttach(AONContext ctx, AttachFilter filter);
	public void deletePayrollAttach(AONContext ctx, AttachFilter filter);
	public void deleteProjectAttach(AONContext ctx, AttachFilter filter);
	public void deleteRegistryAttach(AONContext ctx, AttachFilter filter);
	public void deleteSepeAttach(AONContext ctx, AttachFilter filter);
}
