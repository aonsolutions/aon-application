package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;

public interface IRawdoc {
	
	public Stream<Rawdoc> getRawdocStream(AONContext ctx, RawdocFilter filter, int offset, int limit);
	public Stream<Rawdoc> getRawdocFullStream(AONContext ctx, RawdocFilter filter, int offset, int limit);
	public RawdocUserData getRawdocUserData(AONContext ctx, byte[] auth);
	public RawdocUserData getRawdocUserData(AONContext ctx, int searchDomain);
	public RawdocInvoiceCounter getRawdocInvoiceCounter(AONContext ctx);
	public Rawdoc getRawdocFull(AONContext ctx, int id);
	public Rawdoc rawdocSave(AONContext ctx, Rawdoc rawdoc);
	void rawdocDelete(AONContext ctx, RawdocFilter filter);
	void rawdocDelete(AONContext ctx, Integer domain, Integer rawdocId);
	void rawdocToDraft(AONContext ctx, Integer rawdocId);
	void rawdocToRejected(AONContext ctx, Integer rawdocId, String reason);
	void rawdocToInbox(AONContext ctx, Integer rawdocId);
	boolean rawdocHasData(AONContext ctx, Integer rawdocId);

}
	