package com.esferalia.aon.occam.impl.jooq;

import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IRawdoc;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocInvoiceCounter;
import com.esferalia.aon.occam.api.model.RawdocUserData;
import com.esferalia.aon.occam.api.model.Filter.RawdocFilter;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;

public class RawdocImpl implements IRawdoc {

	@Override
	public Stream<Rawdoc> getRawdocStream(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.get(ctx,filter,offset,limit));
	}
	
	@Override
	public Stream<Rawdoc> getRawdocFullStream(AONContext ctx, RawdocFilter filter, int offset, int limit) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getFull(ctx, filter, offset, limit));
	}
	
	@Override
	public RawdocUserData getRawdocUserData(AONContext ctx, byte[] auth) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getUserData(ctx, auth));
	}
	
	@Override
	public RawdocUserData getRawdocUserData(AONContext ctx, int searchDomain) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getUserData(ctx, searchDomain));
	}
	
	@Override
	public RawdocInvoiceCounter getRawdocInvoiceCounter(AONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getInvoiceCounter(ctx));
	}
	
	@Override
	public Optional<Rawdoc> getRawdocFull(AONContext ctx, int id) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.getFull(ctx, id));
	}
	
	@Override
	public Rawdoc rawdocSave(AONContext ctx, Rawdoc rawdoc) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.save(ctx, rawdoc));			
	}
	
	@Override
	public Rawdoc rawdocSave(AONContext ctx, Integer rawdocId, String invoiceJson) {
		return ctx.getDslContext().transactionResult(configuration
				-> RawdocDAO.save(ctx, rawdocId, invoiceJson));			
	}
	
	@Override
	public void rawdocDelete(AONContext ctx, RawdocFilter filter) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.delete(ctx, filter);
		} );			
	}
	
	@Override
	public void rawdocDelete(AONContext ctx, Integer domain, Integer rawdocId) {
		ctx.getDslContext().transaction(configuration -> {
			RawdocDAO.delete(ctx, domain, rawdocId);
		} );			
	}
	
	@Override
	public Rawdoc toTrash(AONContext ctx, Integer rawdocId) {
		return ctx.getDslContext().transactionResult(configuration ->
			RawdocDAO.toTrash(ctx, rawdocId));
	}

	@Override
	public Rawdoc toRejected(AONContext ctx, Integer rawdocId, String reason) {
		return ctx.getDslContext().transactionResult(configuration -> 
			RawdocDAO.toRejected(ctx, rawdocId, reason));
	}

	@Override
	public Rawdoc toInbox(AONContext ctx, Integer rawdocId) {
		return ctx.getDslContext().transactionResult(configuration -> 
			RawdocDAO.toInbox(ctx, rawdocId));
	}
	@Override
	public boolean rawdocHasData(AONContext ctx, Integer rawdocId) {
		return ctx.getDslContext().transactionResult(configuration -> 
			RawdocDAO.hasData(ctx, rawdocId));
	}

}
