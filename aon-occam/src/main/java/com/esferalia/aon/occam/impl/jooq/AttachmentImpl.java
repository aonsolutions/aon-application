package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAttachment;
import com.esferalia.aon.occam.api.model.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachQueryProperties;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;

public class AttachmentImpl implements IAttachment{
	
	@Override
	public Attach getRegistryAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRegistryAttach(ctx, filter));
	}
	
	@Override
	public Attach getContractAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getContractAttach(ctx, filter));
	}
	
	@Override
	public Attach getInvoiceAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getInvoiceAttach(ctx, filter));
	}
	
	@Override
	public Attach getItemAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getItemAttach(ctx, filter));
	}
	
	@Override
	public Attach getOfferAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getOfferAttach(ctx, filter));
	}
	
	@Override
	public Attach getPayrollAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getPayrollAttach(ctx, filter));
	}
	
	@Override
	public Attach getProjectAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getProjectAttach(ctx, filter));
	}
	
	@Override
	public Attach getSepeAttach(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getSepeAttach(ctx, filter));
	}
	
	@Override
	public LinkedList<Attach> getRegistryAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRegistryAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getContractAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getContractAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getInvoiceAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getInvoiceAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getItemAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getItemAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getOfferAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getOfferAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getPayrollAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getPayrollAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getProjectAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getProjectAttachList(ctx, filter));
	}
	@Override
	public LinkedList<Attach> getSepeAttachList(AONContext ctx, AttachFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getSepeAttachList(ctx, filter));
	}
	

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
	public LinkedList<Attach> getRegistryAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRegistryAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getContractAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getContractAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getInvoiceAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getInvoiceAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getItemAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getItemAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getOfferAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getOfferAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getPayrollAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getPayrollAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getProjectAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getProjectAttachList(ctx, filter, aqp));
	}
	@Override
	public LinkedList<Attach> getSepeAttachList(AONContext ctx, AttachFilter filter, AttachQueryProperties aqp) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getSepeAttachList(ctx, filter, aqp));
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
