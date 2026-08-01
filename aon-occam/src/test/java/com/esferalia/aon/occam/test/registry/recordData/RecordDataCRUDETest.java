package com.esferalia.aon.occam.test.registry.recordData;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertTrue;

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

			assertNotNull(saved.getId(), "Id after insert");
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

			assertNotNull(fetched, "RecordData not found");
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

			assertTrue(list.size() >= 2, "At least 2 results");
			assertTrue(list.stream().anyMatch(r -> r.getId().equals(rd1.getId())), "Contains rd1");
			assertTrue(list.stream().anyMatch(r -> r.getId().equals(rd2.getId())), "Contains rd2");

			RecordDataDAO.delete(ctx, rd1.getId());
			RecordDataDAO.delete(ctx, rd2.getId());

			RecordData rd3 = RecordDataDAO.save(ctx, buildRecordData(registry.getId()).setType(RecordDataType.COMPANY_NAME_CHANGE));
			RecordData rd4 = RecordDataDAO.save(ctx, buildRecordData(registry.getId()).setType(RecordDataType.COMPANY_NAME_CHANGE));

			List<RecordData> list2 = RecordDataDAO.getStream(ctx, RecordDataType.COMPANY_NAME_CHANGE, registry.getId())
					.collect(Collectors.toList());

			assertTrue(list2.size() >= 2, "At least 2 results");
			assertTrue(list2.stream().anyMatch(r -> r.getId().equals(rd3.getId())), "Contains rd3");
			assertTrue(list2.stream().anyMatch(r -> r.getId().equals(rd4.getId())), "Contains rd4");

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
			assertNotNull(RecordDataDAO.get(ctx, recordData.getId()), "Exists before delete");

			RecordDataDAO.delete(ctx, recordData.getId());
			assertNull(RecordDataDAO.get(ctx, recordData.getId()), "Null after delete");
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
		assertEquals(expected.getDomain(), actual.getDomain(), "Domain");
		assertEquals(expected.getRegistry(), actual.getRegistry(), "Registry");
		assertEquals(expected.getDescription(), actual.getDescription(), "Description");
		assertEquals(expected.getNotary(), actual.getNotary(), "Notary");
		assertEquals(expected.getNumber(), actual.getNumber(), "Number");
		assertEquals(expected.getVolume(), actual.getVolume(), "Volume");
		assertEquals(expected.getSection(), actual.getSection(), "Section");
		assertEquals(expected.getPage(), actual.getPage(), "Page");
		assertEquals(expected.getSheet(), actual.getSheet(), "Sheet");
		assertEquals(expected.getRegistration(), actual.getRegistration(), "Registration");
		assertEquals(expected.getType(), actual.getType(), "Type");
		assertEquals(expected.getIrus(), actual.getIrus(), "Irus");
		assertEquals(expected.getCommercialRegistryCode(), actual.getCommercialRegistryCode(), "CommercialRegistryCode");
	}

}
