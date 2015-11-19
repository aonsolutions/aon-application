package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAttachment;
import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;

public class AttachmentImpl implements IAttachment{
	
	@Override
	public Attach getRattach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRattach(ctx, filter));
	}
	
	@Override
	public LinkedList<Attach> getRattachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRattachList(ctx, filter));
	}

	@Override
	public Integer insertContractAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertContractAttach(ctx, attach));
	}
	
	@Override
	public Integer insertItemAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertItemAttach(ctx, attach));
	}
	
	@Override
	public Integer insertInvoiceAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertInvoiceAttach(ctx, attach));
	}
	
	@Override
	public Integer insertOfferAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertOfferAttach(ctx, attach));
	}
	
	@Override
	public Integer insertPayrollAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertPayrollAttach(ctx, attach));
	}
	
	@Override
	public Integer insertProjectAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertProjectAttach(ctx, attach));
	}
	
	@Override
	public Integer insertRegistryAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertRegistryAttach(ctx, attach));			
	}

	@Override
	public Integer insertSepeAttach(AONContext ctx, Attach attach) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertSepeAttach(ctx, attach));
	}

	@Override
	public void updateContractAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void updateItemAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void updateInvoiceAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void updateOfferAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void updatePayrollAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void updateProjectAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateProjectAttach(ctx, attach));
	}

	@Override
	public void updateRegistryAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void updateSepeAttach(AONContext ctx, Attach attach) {
	}

	@Override
	public void deleteContractAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteContractAttach(ctx, filter);
		} );
	}

	@Override
	public void deleteItemAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteItemAttach(ctx, filter);
		} );
	}

	@Override
	public void deleteInvoiceAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteInvoiceAttach(ctx, filter);
		} );
	}

	@Override
	public void deleteOfferAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteOfferAttach(ctx, filter);
		} );
	}

	@Override
	public void deletePayrollAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deletePayrollAttach(ctx, filter);
		} );
	}

	@Override
	public void deleteProjectAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteProjectAttach(ctx, filter);
		} );
	}

	@Override
	public void deleteRegistryAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteRegistryAttach(ctx, filter);
		} );
	}

	@Override
	public void deleteSepeAttach(AONContext ctx, AttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteSepeAttach(ctx, filter);
		} );
	}
}
