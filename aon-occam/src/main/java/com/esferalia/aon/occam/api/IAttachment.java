package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.AuthAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.RattachTag;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.AuthAttach;

public interface IAttachment {

	public AuthAttach getAuthAttach(AONContext ctx, AuthAttachFilter filter, Boolean withData);
	public AuthAttach saveAuthAttach(AONContext ctx, AuthAttach authAttach);

	public Stream<Attach> getDocumentalRegistryAttachStream(AONContext ctx, AttachFilter filter, Boolean withData, Options...options);
	public Stream<Attach> getRegistryAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getContractAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getInvoiceAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getItemAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getOfferAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getPayrollAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getProjectAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getSepeAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getDataAttachStream(AONContext ctx, AttachFilter filter, Boolean withData);
	public Stream<Attach> getRawdocAttachStream(AONContext ctx, RawdocFilter filter);

	public void setRegistryAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setContractAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setInvoiceAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setItemAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setOfferAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setPayrollAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setProjectAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setSepeAttachStream(AONContext ctx, Integer attachId, byte[] data);
	public void setDataAttachStream(AONContext ctx, Integer attachId, byte[] data);
	
	
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
	public void updateDataAttachDriveId(AONContext ctx, Integer attachId, String driveId);
	
	public void deleteContractAttach(AONContext ctx, AttachFilter filter);
	public void deleteItemAttach(AONContext ctx, AttachFilter filter);
	public void deleteInvoiceAttach(AONContext ctx, AttachFilter filter);
	public void deleteOfferAttach(AONContext ctx, AttachFilter filter);
	public void deletePayrollAttach(AONContext ctx, AttachFilter filter);
	public void deleteProjectAttach(AONContext ctx, AttachFilter filter);
	public void deleteRegistryAttach(AONContext ctx, AttachFilter filter);
	public void deleteSepeAttach(AONContext ctx, AttachFilter filter);
	public void deleteDataAttach(AONContext ctx, AttachFilter filter);
	
	public LinkedList<Tag> getRegistryAttachmentTag(AONContext ctx, Integer rattachId);
	public RattachTag save(AONContext ctx, RattachTag ratttachTag);
	public void deleteRegistryAttachTag(AONContext ctx, Integer rattachId);
	public void deleteTagRegistryAttach(AONContext ctx, Integer tagId);

}
