package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;

public interface IAttachment {

	public Stream<Attach> getRegistryAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getContractAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getInvoiceAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getItemAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getOfferAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getPayrollAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getProjectAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getSepeAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getDataAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);

	
	public Integer insertContractAttach(AONContext ctx, Attach attach);
	public Integer insertItemAttach(AONContext ctx, Attach attach);
	public Integer insertInvoiceAttach(AONContext ctx, Attach attach);
	public Integer insertOfferAttach(AONContext ctx, Attach attach);
	public Integer insertPayrollAttach(AONContext ctx, Attach attach);
	public Integer insertProjectAttach(AONContext ctx, Attach attach);
	public Integer insertRegistryAttach(AONContext ctx, Attach attach);
	public Integer insertSepeAttach(AONContext ctx, Attach attach);
	public Integer insertDataAttach(AONContext ctx, Attach attach);
	
	
	public void updateContractAttach(AONContext ctx, Attach attach);
	public void updateItemAttach(AONContext ctx, Attach attach);
	public void updateInvoiceAttach(AONContext ctx, Attach attach);
	public void updateOfferAttach(AONContext ctx, Attach attach);
	public void updatePayrollAttach(AONContext ctx, Attach attach);
	public void updateProjectAttach(AONContext ctx, Attach attach);
	public void updateRegistryAttach(AONContext ctx, Attach attach);
	public void updateSepeAttach(AONContext ctx, Attach attach);
	public void updateDataAttach(AONContext ctx, Attach attach);
	
	public void updateContractAttachData(AONContext ctx, Attach attach);
	public void updateItemAttachData(AONContext ctx, Attach attach);
	public void updateInvoiceAttachData(AONContext ctx, Attach attach);
	public void updateOfferAttachData(AONContext ctx, Attach attach);
	public void updatePayrollAttachData(AONContext ctx, Attach attach);
	public void updateProjectAttachData(AONContext ctx, Attach attach);
	public void updateRegistryAttachData(AONContext ctx, Attach attach);
	public void updateSepeAttachData(AONContext ctx, Attach attach);
	public void updateDataAttachData(AONContext ctx, Attach attach);
	
	
	public void updateContractAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updateItemAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updateInvoiceAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updateOfferAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updatePayrollAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updateProjectAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updateRegistryAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	public void updateSepeAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	
	public void deleteContractAttach(AONContext ctx, AttachFilter filter);
	public void deleteItemAttach(AONContext ctx, AttachFilter filter);
	public void deleteInvoiceAttach(AONContext ctx, AttachFilter filter);
	public void deleteOfferAttach(AONContext ctx, AttachFilter filter);
	public void deletePayrollAttach(AONContext ctx, AttachFilter filter);
	public void deleteProjectAttach(AONContext ctx, AttachFilter filter);
	public void deleteRegistryAttach(AONContext ctx, AttachFilter filter);
	public void deleteSepeAttach(AONContext ctx, AttachFilter filter);
	public void deleteDataAttach(AONContext ctx, AttachFilter filter);
	
	public Integer insertRegistryAttachTag(AONContext ctx, Integer rattachId, Integer tagId);
	public void deleteRegistryAttachTag(AONContext ctx, Integer rattachId);
}
