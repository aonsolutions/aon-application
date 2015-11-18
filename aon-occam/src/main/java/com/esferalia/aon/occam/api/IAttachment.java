package com.esferalia.aon.occam.api;

import java.util.List;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;


public interface IAttachment {
	public Attach getRattach(AONContext ctx, Condition condition);
	public Attach getRattach(AONContext ctx, AttachFilter filter);
	public List<Attach> getRattachList(AONContext ctx, Condition condition);
	
	public void insertContractAttach(AONContext ctx, Attach attach);
	public void insertItemAttach(AONContext ctx, Attach attach);
	public void insertInvoiceAttach(AONContext ctx, Attach attach);
	public void insertOfferAttach(AONContext ctx, Attach attach);
	public void insertPayrollAttach(AONContext ctx, Attach attach);
	public void insertProjectAttach(AONContext ctx, Attach attach);
	public void insertRegistryAttach(AONContext ctx, Attach attach);
	public void insertSepeAttach(AONContext ctx, Attach attach);
	
	public void updateContractAttach(AONContext ctx, Attach attach);
	public void updateItemAttach(AONContext ctx, Attach attach);
	public void updateInvoiceAttach(AONContext ctx, Attach attach);
	public void updateOfferAttach(AONContext ctx, Attach attach);
	public void updatePayrollAttach(AONContext ctx, Attach attach);
	public void updateProjectAttach(AONContext ctx, Attach attach);
	public void updateRegistryAttach(AONContext ctx, Attach attach);
	public void updateSepeAttach(AONContext ctx, Attach attach);
	
	public void deleteContractAttach(AONContext ctx, Condition condition);
	public void deleteItemAttach(AONContext ctx, Condition condition);
	public void deleteInvoiceAttach(AONContext ctx, Condition condition);
	public void deleteOfferAttach(AONContext ctx, Condition condition);
	public void deletePayrollAttach(AONContext ctx, Condition condition);
	public void deleteProjectAttach(AONContext ctx, Condition condition);
	public void deleteRegistryAttach(AONContext ctx, Condition condition);
	public void deleteSepeAttach(AONContext ctx, Condition condition);
	
	public void deleteContractAttach(AONContext ctx, Integer attachId);
	public void deleteItemAttach(AONContext ctx, Integer attachId);
	public void deleteInvoiceAttach(AONContext ctx, Integer attachId);
	public void deleteOfferAttach(AONContext ctx, Integer attachId);
	public void deletePayrollAttach(AONContext ctx, Integer attachId);
	public void deleteProjectAttach(AONContext ctx, Integer attachId);
	public void deleteRegistryAttach(AONContext ctx, Integer attachId);
	public void deleteSepeAttach(AONContext ctx, Integer attachId);
	
}
