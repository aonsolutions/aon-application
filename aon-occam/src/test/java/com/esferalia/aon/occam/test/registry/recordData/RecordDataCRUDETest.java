package com.esferalia.aon.occam.test.registry.recordData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.registry.CommercialRegistryCode;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RecordDataType;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.RecordDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class RecordDataCRUDETest extends AbstractOccamTest {

	@Test
	public void testInsert() {
		Registry registry = saveRegistry();
		try {
			RecordData recordData = buildRecordData(registry.getId());
			RecordData saved = RecordDataDAO.save(ctx, recordData);

			assertNotNull("Id after insert", saved.getId());
			assertEqualsRecordData(recordData, saved);

			RecordDataDAO.delete(ctx, recordData.getId());
		} finally {
			RegistryDAO.delete(ctx, registry.getId());
		}
	}

	@Test
	public void testGet() {
		Registry registry = saveRegistry();
		try {
			RecordData recordData = RecordDataDAO.save(ctx, buildRecordData(registry.getId()));
			RecordData fetched = RecordDataDAO.get(ctx, recordData.getId());

			assertNotNull("RecordData not found", fetched);
			assertEqualsRecordData(recordData, fetched);

			RecordDataDAO.delete(ctx, recordData.getId());
		} finally {
			RegistryDAO.delete(ctx, registry.getId());
		}
	}

	@Test
	public void testUpdate() {
		Registry registry = saveRegistry();
		try {
			RecordData recordData = RecordDataDAO.save(ctx, buildRecordData(registry.getId()));

			recordData
				.setDescription("Descripcion actualizada")
				.setNotary("Notario actualizado")
				.setIrus("IRUS-UPDATED")
				.setType(RecordDataType.COMPANY_NAME_CHANGE)
				.setCommercialRegistryCode(CommercialRegistryCode.MADRID);

			RecordDataDAO.save(ctx, recordData);
			RecordData updated = RecordDataDAO.get(ctx, recordData.getId());

			assertEqualsRecordData(recordData, updated);
			
			RecordDataDAO.delete(ctx, recordData.getId());
		} finally {
			RegistryDAO.delete(ctx, registry.getId());
		}
	}

	@Test
	public void testGetStream() {
		Registry registry = saveRegistry();
		try {
			RecordData rd1 = RecordDataDAO.save(ctx, buildRecordData(registry.getId()));
			RecordData rd2 = RecordDataDAO.save(ctx, buildRecordData(registry.getId()));

			List<RecordData> list = RecordDataDAO.getStream(ctx, registry.getId())
					.collect(Collectors.toList());

			assertTrue("At least 2 results", list.size() >= 2);
			assertTrue("Contains rd1", list.stream().anyMatch(r -> r.getId().equals(rd1.getId())));
			assertTrue("Contains rd2", list.stream().anyMatch(r -> r.getId().equals(rd2.getId())));

			RecordDataDAO.delete(ctx, rd1.getId());
			RecordDataDAO.delete(ctx, rd2.getId());

			RecordData rd3 = RecordDataDAO.save(ctx, buildRecordData(registry.getId()).setType(RecordDataType.COMPANY_NAME_CHANGE));
			RecordData rd4 = RecordDataDAO.save(ctx, buildRecordData(registry.getId()).setType(RecordDataType.COMPANY_NAME_CHANGE));

			List<RecordData> list2 = RecordDataDAO.getStream(ctx, RecordDataType.COMPANY_NAME_CHANGE, registry.getId())
					.collect(Collectors.toList());

			assertTrue("At least 2 results", list2.size() >= 2);
			assertTrue("Contains rd3", list2.stream().anyMatch(r -> r.getId().equals(rd3.getId())));
			assertTrue("Contains rd4", list2.stream().anyMatch(r -> r.getId().equals(rd4.getId())));

			RecordDataDAO.delete(ctx, rd3.getId());
			RecordDataDAO.delete(ctx, rd4.getId());		
		} finally {
			RegistryDAO.delete(ctx, registry.getId());
		}
	}

	@Test
	public void testDelete() {
		Registry registry = saveRegistry();
		try {
			RecordData recordData = RecordDataDAO.save(ctx, buildRecordData(registry.getId()));
			assertNotNull("Exists before delete", RecordDataDAO.get(ctx, recordData.getId()));

			RecordDataDAO.delete(ctx, recordData.getId());
			assertNull("Null after delete", RecordDataDAO.get(ctx, recordData.getId()));
		} finally {
			RegistryDAO.delete(ctx, registry.getId());
		}
	}

	// ---------------------------------------------------------------- helpers

	private Registry saveRegistry() {
		return RegistryDAO.save(ctx, AonFaker.getRegistry(ctx));
	}

	private RecordData buildRecordData(Integer registryId) {
		return new RecordData()
				.setDomain(ctx.getDomainId())
				.setRegistry(registryId)
				.setCreationDate(AonRandom.today())
				.setDescription(AonRandom.string(0, 64))
				.setNotary(AonRandom.string(50, 64))
				.setNumber(AonRandom.string(50, 16))
				.setRecordDate(AonRandom.getPastDate(0))
				.setVolume(AonRandom.string(50, 16))
				.setSection(AonRandom.string(50, 16))
				.setPage(AonRandom.string(50, 16))
				.setSheet(AonRandom.string(50, 16))
				.setRegistration(AonRandom.string(50, 64))
				.setType(AonRandom.getEnum(RecordDataType.class, false))
				.setIrus(AonRandom.string(50, 13))
				.setCommercialRegistryCode(AonRandom.getEnum(CommercialRegistryCode.class, 30));
	}

	private static void assertEqualsRecordData(RecordData expected, RecordData actual) {
		assertNotNull(actual);
		assertEquals("Domain",                expected.getDomain(),                actual.getDomain());
		assertEquals("Registry",              expected.getRegistry(),              actual.getRegistry());
		assertEquals("Description",           expected.getDescription(),           actual.getDescription());
		assertEquals("Notary",                expected.getNotary(),                actual.getNotary());
		assertEquals("Number",                expected.getNumber(),                actual.getNumber());
		assertEquals("Volume",                expected.getVolume(),                actual.getVolume());
		assertEquals("Section",               expected.getSection(),               actual.getSection());
		assertEquals("Page",                  expected.getPage(),                  actual.getPage());
		assertEquals("Sheet",                 expected.getSheet(),                 actual.getSheet());
		assertEquals("Registration",          expected.getRegistration(),          actual.getRegistration());
		assertEquals("Type",                  expected.getType(),                  actual.getType());
		assertEquals("Irus",                  expected.getIrus(),                  actual.getIrus());
		assertEquals("CommercialRegistryCode",expected.getCommercialRegistryCode(),actual.getCommercialRegistryCode());
	}

}
