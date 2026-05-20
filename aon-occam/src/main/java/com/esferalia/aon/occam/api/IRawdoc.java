package com.esferalia.aon.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocUserData;

public interface IRawdoc {
	
	Rawdoc addLogComment(AONContext ctx, Integer rawdocId, String comment);
	Rawdoc toTrash(AONContext ctx, Integer rawdocId);
	Rawdoc toRejected(AONContext ctx, Integer rawdocId, String reason);
	Rawdoc toInbox(AONContext ctx, Integer rawdocId);
	Rawdoc restore(AONContext ctx, Integer rawdocId);
	
	public Stream<Rawdoc> getRawdocStream(AONContext ctx, RawdocFilter filter, int offset, int limit);
	public Stream<Rawdoc> getRawdocFullStream(AONContext ctx, RawdocFilter filter, int offset, int limit);
	public RawdocUserData getRawdocUserData(AONContext ctx, byte[] auth);
	public RawdocUserData getRawdocUserData(AONContext ctx, int searchDomain);
	public RawdocInvoiceCounter getRawdocInvoiceCounter(AONContext ctx);
	public Optional<Rawdoc> getRawdocFull(AONContext ctx, int id);
	public Rawdoc rawdocSave(AONContext ctx, Rawdoc rawdoc);
	public Rawdoc rawdocSave(AONContext ctx, Integer rawdocId, String invoiceJson);
	void rawdocDelete(AONContext ctx, RawdocFilter filter);
	void rawdocDelete(AONContext ctx, Integer domain, Integer rawdocId);
	boolean rawdocHasData(AONContext ctx, Integer rawdocId);

}
	