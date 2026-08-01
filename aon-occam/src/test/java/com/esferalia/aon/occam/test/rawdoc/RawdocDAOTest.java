package com.esferalia.aon.occam.test.rawdoc;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertFalse;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.occam.impl.jooq.dao.RawdocDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.watson.util.AonStringUtils;


public class RawdocDAOTest extends AbstractOccamTest {

	@Test
	public void testInsert() throws IOException {
		Rawdoc rawdoc = RawdocFaker.getRawdoc( ctx ); 
		rawdoc = RawdocDAO.save(ctx, rawdoc);
		Optional<Rawdoc> optInserted = RawdocDAO.get(ctx, rawdoc.getId());
		assertNotNull( optInserted );
		assertTrue( optInserted.isPresent() );
		Rawdoc inserted =  optInserted.get();
		Asserts.assertEqualsRawdoc(rawdoc, inserted);
	}
	
	@Test
	public void testDelete() throws IOException {
		Rawdoc rawdoc = RawdocFaker.getRawdoc( ctx ); 
		rawdoc = RawdocDAO.save(ctx, rawdoc);
		Optional<Rawdoc> optInserted = RawdocDAO.get(ctx, rawdoc.getId());
		assertNotNull( optInserted );
		assertTrue( optInserted.isPresent() );
		Rawdoc inserted =  optInserted.get();
		Asserts.assertEqualsRawdoc(rawdoc, inserted);
		RawdocDAO.delete(ctx, DOMAIN_ID, rawdoc.getId() );
		Optional<Rawdoc> optDeleted = RawdocDAO.get(ctx, rawdoc.getId());
		assertNotNull( optDeleted);
		assertTrue( optDeleted.isEmpty() );
		
	}

	@Test
	public void testFlow() {
		Rawdoc rawdoc = RawdocFaker.getRawdoc( ctx ); 
		rawdoc = RawdocDAO.save(ctx, rawdoc);
		assertNotNull( rawdoc );
		String reason = AonRandom.lorem(0,50);
		
		Rawdoc rejected =  RawdocDAO.toRejected(ctx, rawdoc.getId(), reason);
		assertNotNull( rejected );
		assertTrue( AonStringUtils.isNotBlank( rejected.getLog()) );
		JSONArray jsonRejectedLog = new JSONArray( rejected.getLog() );
		assertFalse( jsonRejectedLog.isEmpty() );
		assertTrue( jsonRejectedLog.length() > 0 );
		JSONObject jsonRejected = jsonRejectedLog.getJSONObject( jsonRejectedLog.length() - 1);
		assertFalse( JsonUtils.isEmpty( jsonRejected) );
		String rRejected = jsonRejected.getString(IJsonNames.REASON);
		assertEquals(reason, rRejected, "Reason");
		RawdocStatus sRejected = RawdocStatus.safeValueOf( jsonRejected.getString(IJsonNames.STATUS) );
		assertEquals( RawdocStatus.REJECTED, sRejected );
		
		Rawdoc draft =  RawdocDAO.toTrash(ctx, rawdoc.getId());
		assertNotNull( draft );
		assertTrue( AonStringUtils.isNotBlank( draft.getLog()) );
		JSONArray jsonDraftLog = new JSONArray( draft.getLog() );
		assertFalse( jsonDraftLog.isEmpty() );
		assertTrue( jsonDraftLog.length() > 0 );
		JSONObject jsonDraft = jsonDraftLog.getJSONObject( jsonDraftLog.length() - 1);
		assertFalse( JsonUtils.isEmpty( jsonDraft) );
		String rDraft = jsonDraft.optString(IJsonNames.REASON);
		assertTrue( AonStringUtils.isBlank( rDraft) );
		RawdocStatus sDraft = RawdocStatus.safeValueOf( jsonDraft.getString(IJsonNames.STATUS) );
		assertEquals( RawdocStatus.TRASH, sDraft );
		
		Rawdoc inbox =  RawdocDAO.toInbox(ctx, rawdoc.getId());
		assertNotNull( inbox );
		assertTrue( AonStringUtils.isNotBlank( inbox.getLog()) );
		JSONArray jsonInboxLog = new JSONArray( inbox.getLog() );
		assertFalse( jsonInboxLog.isEmpty() );
		assertTrue( jsonInboxLog.length() > 0 );
		JSONObject jsonInbox = jsonInboxLog.getJSONObject( jsonInboxLog.length() - 1);
		assertFalse( JsonUtils.isEmpty( jsonInbox) );
		String rInbox = jsonInbox.optString(IJsonNames.REASON);
		assertTrue( AonStringUtils.isBlank( rInbox) );
		RawdocStatus sInbox = RawdocStatus.safeValueOf( jsonInbox.getString(IJsonNames.STATUS) );
		assertEquals( RawdocStatus.INBOX, sInbox );
		
	}
	
	
}
