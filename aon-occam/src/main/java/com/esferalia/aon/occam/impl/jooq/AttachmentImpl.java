package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAttachment;
import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;

public class AttachmentImpl implements IAttachment{

	@Override
	public Stream<Attach> getRegistryAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRegistryAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getContractAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getContractAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getInvoiceAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getInvoiceAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getItemAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getItemAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getOfferAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getOfferAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getPayrollAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getPayrollAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getProjectAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getProjectAttachStream(ctx, filter));
	}
	@Override
	public Stream<Attach> getSepeAttachStream(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getSepeAttachStream(ctx, filter));
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

	
	//-------------------- FULL UPDATE
	
	@Override
	public void updateContractAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateContractAttach(ctx, attach));
	}

	@Override
	public void updateItemAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateItemAttach(ctx, attach));
	}

	@Override
	public void updateInvoiceAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateInvoiceAttach(ctx, attach));
	}

	@Override
	public void updateOfferAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateOfferAttach(ctx, attach));
	}

	@Override
	public void updatePayrollAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updatePayrollAttach(ctx, attach));
	}

	@Override
	public void updateProjectAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateProjectAttach(ctx, attach));
	}

	@Override
	public void updateRegistryAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateRegistryAttach(ctx, attach));
	}

	@Override
	public void updateSepeAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateSepeAttach(ctx, attach));
	}

	//-------------------- DATA UPDATE
	
	@Override
	public void updateContractAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateContractAttachData(ctx, attach));
	}

	@Override
	public void updateItemAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateItemAttachData(ctx, attach));
	}

	@Override
	public void updateInvoiceAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateInvoiceAttachData(ctx, attach));
	}

	@Override
	public void updateOfferAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateOfferAttachData(ctx, attach));
	}

	@Override
	public void updatePayrollAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updatePayrollAttachData(ctx, attach));
	}

	@Override
	public void updateProjectAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateProjectAttachData(ctx, attach));
	}

	@Override
	public void updateRegistryAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateRegistryAttachData(ctx, attach));
	}

	@Override
	public void updateSepeAttachData(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateSepeAttachData(ctx, attach));
	}
	
	//-------------------- DRIVE ID UPDATE
	
	@Override
	public void updateContractAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateContractAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updateItemAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateItemAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updateInvoiceAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateInvoiceAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updateOfferAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateOfferAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updatePayrollAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updatePayrollAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updateProjectAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateProjectAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updateRegistryAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateRegistryAttachDriveId(ctx, attach, driveId));
	}

	@Override
	public void updateSepeAttachDriveId(AONContext ctx, Integer attach, String driveId) {
		ctx.getDslContext().transaction(configuration -> 
			AttachmentDAO.updateSepeAttachDriveId(ctx, attach, driveId));
	}
	
	//-------------------- DELETES
	
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
	
	@Override
	public Integer insertRegistryAttachTag(AONContext ctx, Integer rattachId, Integer tagId){
		return ctx.getDslContext().transactionResult(configuration -> 
			AttachmentDAO.insertRegistryAttachTag(ctx, rattachId, tagId));
	}

	@Override
	public void deleteRegistryAttachTag(AONContext ctx, Integer rattachId) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.deleteRegistryAttachTag(ctx, rattachId);
		} );
	}	
}
