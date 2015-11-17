package com.esferalia.aon.occam.impl.jooq;

import java.util.List;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAttachment;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;

public class AttachmentImpl implements IAttachment{

	@Override
	public Attach getRattach(AONContext ctx, Condition condition) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRattach(ctx, condition));
	}
	
	@Override
	public List<Attach> getRattachList(AONContext ctx, Condition condition) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRattachList(ctx, condition));
	}

	@Override
	public void insertContractAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertContractAttach(ctx, attach);
		} );
	}
	
	@Override
	public void insertItemAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertItemAttach(ctx, attach);
		} );
	}
	
	@Override
	public void insertInvoiceAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertInvoiceAttach(ctx, attach);
		} );
	}
	
	@Override
	public void insertOfferAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertOfferAttach(ctx, attach);
		} );
	}
	
	@Override
	public void insertPayrollAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertPayrollAttach(ctx, attach);
		} );
	}
	
	@Override
	public void insertProjectAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertProjectAttach(ctx, attach);
		} );
	}
	
	@Override
	public void insertRegistryAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertRegistryAttach(ctx, attach);
		} );			
	}

	@Override
	public void insertSepeAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertSepeAttach(ctx, attach);
		} );
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
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.updateProjectAttach(ctx, attach);
		} );
	}

	@Override
	public void updateRegistryAttach(AONContext ctx, Attach attach) {
		
	}

	@Override
	public void updateSepeAttach(AONContext ctx, Attach attach) {
		
	}

	@Override
	public void deleteContractAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteContractAttach(ctx, condition);
		} );
	}

	@Override
	public void deleteItemAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteItemAttach(ctx, condition);
		} );
	}

	@Override
	public void deleteInvoiceAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteInvoiceAttach(ctx, condition);
		} );
	}

	@Override
	public void deleteOfferAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteOfferAttach(ctx, condition);
		} );
	}

	@Override
	public void deletePayrollAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deletePayrollAttach(ctx, condition);
		} );
	}

	@Override
	public void deleteProjectAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteProjectAttach(ctx, condition);
		} );
	}

	@Override
	public void deleteRegistryAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteRegistryAttach(ctx, condition);
		} );
	}

	@Override
	public void deleteSepeAttach(AONContext ctx, Condition condition) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteSepeAttach(ctx, condition);
		} );
	}
	
	@Override
	public void deleteContractAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteContractAttach(ctx, attachId);
		} );
	}

	@Override
	public void deleteItemAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteItemAttach(ctx, attachId);
		} );
	}

	@Override
	public void deleteInvoiceAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteInvoiceAttach(ctx, attachId);
		} );
	}

	@Override
	public void deleteOfferAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteOfferAttach(ctx, attachId);
		} );
	}

	@Override
	public void deletePayrollAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deletePayrollAttach(ctx, attachId);
		} );
	}

	@Override
	public void deleteProjectAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteProjectAttach(ctx, attachId);
		} );
	}

	@Override
	public void deleteRegistryAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteRegistryAttach(ctx, attachId);
		} );
	}

	@Override
	public void deleteSepeAttach(AONContext ctx, Integer attachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteSepeAttach(ctx, attachId);
		} );
	}
}
