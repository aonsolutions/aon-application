package com.esferalia.aon.occam.test.finance.invoice;

import com.esferalia.aon.occam.test.AbstractOccamTest;

public class InvoiceCommunicationConfigurationTest extends AbstractOccamTest  {

//	@Test
//	public void testInvoiceCommunication() {
//
//	}
//	
//	// ARABA TESTS
//
//	@Test
//	public void testArabaWithouthTbaiAndSii() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.ALAVA.value()));
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.ALAVA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isTbai());
//		assertFalse(config.isSii());
//		
//		// assertTrue(config.isSif());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		// EnterpriseDataDAO.delete(ctx, config.getSifData().getId());
//	}
//	
//	@Test
//	public void testArabaWithTbai() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.ALAVA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.ALAVA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isTbai());
//		assertFalse(config.isTbaiTest());
//		assertEquals(AonDateUtils.today(), config.getTbaiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getTbaiData().getId());
//	}
//	
//	@Test
//	public void testArabaWithTbaiTestAndIncludeDate() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.ALAVA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.ALAVA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isTbai());
//		assertTrue(config.isTbaiTest());
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getTbaiData().getStartDate());
//		
//		assertFalse(config.isSii());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getTbaiData().getId());
//	}
//	
//	@Test
//	public void testArabaWithTbaiFirstIncludeDateAndSii() {
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.TBAI));
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.ALAVA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "");
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.ALAVA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isTbai());
//		assertFalse(config.isTbaiTest());		
//		assertEquals(firstDate, config.getTbaiData().getStartDate());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());		
//
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);	
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);	
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getTbaiData().getId());
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testArabaWithNoTbaiAndSiiTest() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.ALAVA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "false");
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.ALAVA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isTbai());
//		
//		assertTrue(config.isSii());
//		assertTrue(config.isSiiTest());		
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);	
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);	
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testArabaWithNoTbaiAndSiiFirstIncluide() {
//		
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.SII));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.ALAVA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "false");
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.ALAVA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isTbai());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());		
//		assertEquals(firstDate, config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);	
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);	
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	// GIPUZKOA TESTS
//	
//	@Test
//	public void testGipuzkoaWithouthTbaiAndSii() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.GIPUZKOA.value()));
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.GIPUZKOA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isTbai());
//		assertFalse(config.isSii());
//		
//		// assertTrue(config.isSif());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		// EnterpriseDataDAO.delete(ctx, config.getSifData().getId());
//	}
//	
//	@Test
//	public void testGipuzkoaWithTbai() {
//		deleteTbaiData();
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.GIPUZKOA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.GIPUZKOA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isTbai());
//		assertFalse(config.isTbaiTest());
//		assertEquals(AonDateUtils.today(), config.getTbaiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getTbaiData().getId());
//	}
//	
//	@Test
//	public void testGipuzkoaWithTbaiTestAndIncludeDate() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.GIPUZKOA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.GIPUZKOA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isTbai());
//		assertTrue(config.isTbaiTest());
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getTbaiData().getStartDate());
//		
//		assertFalse(config.isSii());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getTbaiData().getId());
//	}
//	
//	@Test
//	public void testGipuzkoaWithTbaiFirstIncludeDateAndSii() {
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.TBAI));
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.GIPUZKOA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "");
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.GIPUZKOA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isTbai());
//		assertFalse(config.isTbaiTest());		
//		assertEquals(firstDate, config.getTbaiData().getStartDate());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());		
//
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);	
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);	
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getTbaiData().getId());
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testGipuzkoaWithNoTbaiAndSiiTest() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.GIPUZKOA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "false");
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.GIPUZKOA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isTbai());
//		
//		assertTrue(config.isSii());
//		assertTrue(config.isSiiTest());		
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);	
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);	
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testGipuzkoaWithNoTbaiAndSiiFirstIncluide() {
//		
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.SII));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.GIPUZKOA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "false");
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.GIPUZKOA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isTbai());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());		
//		assertEquals(firstDate, config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);	
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);	
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//
//	// BIZKAIA TESTS
//	
//	@Test
//	public void testBizkaiaWithouthLroe() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.BIZKAIA.value()));
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.BIZKAIA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isLroe());
//		
//		// assertTrue(config.isSif());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		// EnterpriseDataDAO.delete(ctx, config.getSifData().getId());
//	}
//	
//	@Test
//	public void testBizkaiaWithLroe() {
//		deleteLroeData();
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.BIZKAIA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.BIZKAIA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isLroe());
//		assertFalse(config.isLroeTest());
//		assertEquals(AonDateUtils.today(), config.getLroeData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getLroeData().getId());
//	}
//	
//	@Test
//	public void testBizkaiaWithLroeTestAndIncludeDate() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.BIZKAIA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.BIZKAIA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isLroe());
//		assertTrue(config.isLroeTest());
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getLroeData().getStartDate());
//		
//		assertFalse(config.isSii());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getLroeData().getId());
//	}
//	
//	@Test
//	public void testBizkaiaWithLroeFirstIncludeDate() {
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.LROE));
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.BIZKAIA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE, "");
//
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.BIZKAIA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isLroe());
//		assertFalse(config.isLroeTest());		
//		assertEquals(firstDate, config.getLroeData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getLroeData().getId());
//	}
//	
//	// NAVARRA TESTS
//	
//	@Test
//	public void testNavarraWithouthSii() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.NAVARRA.value()));
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.NAVARRA.value(), config.getAdministration().value());
//		
//		assertFalse(config.isSii());
//		
//		// assertTrue(config.isSif());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		// EnterpriseDataDAO.delete(ctx, config.getSifData().getId());
//	}
//	
//	@Test
//	public void testNavarraWithSii() {
//		deleteSiiData();
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.NAVARRA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.NAVARRA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());
//		assertEquals(AonDateUtils.today(), config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testNavarraWithSiiTestAndIncludeDate() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.NAVARRA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.NAVARRA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isSii());
//		assertTrue(config.isSiiTest());
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testNavarraWithSiiFirstIncludeDate() {
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.SII));
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.NAVARRA.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.NAVARRA.value(), config.getAdministration().value());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());		
//		assertEquals(firstDate, config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	
//	// AEAT TESTS
//	
//	@Test
//	public void testAeatWithouthSiiAndVerifactu() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertFalse(config.isSii());
//		assertFalse(config.isVerifactu());
//		
//		// assertTrue(config.isSif());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		// EnterpriseDataDAO.delete(ctx, config.getSifData().getId());
//	}
//	
//	@Test
//	public void testAeatWithSii() {
//		deleteSiiData();
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());
//		assertEquals(AonDateUtils.today(), config.getSiiData().getStartDate());
//
//		assertFalse(config.isVerifactu());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testAeatWithSiiTestAndIncludeDate() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertTrue(config.isSii());
//		assertTrue(config.isSiiTest());
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getSiiData().getStartDate());
//		
//		assertFalse(config.isVerifactu());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testAeatWithSiiFirstIncludeDate() {
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.SII));
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE, "");
//
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertTrue(config.isSii());
//		assertFalse(config.isSiiTest());		
//		assertEquals(firstDate, config.getSiiData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getSiiData().getId());
//	}
//	
//	@Test
//	public void testAeatWithVerifactu() {
//		deleteVerifactuData();
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE, "");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertTrue(config.isVerifactu());
//		assertFalse(config.isVerifactuTest());
//		assertEquals(AonDateUtils.today(), config.getVerifactuData().getStartDate());
//
//		assertFalse(config.isSii());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE);
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getVerifactuData().getId());
//	}
//	
//	@Test
//	public void testAeatWithVerifactuTestAndIncludeDate() {
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE, "2024-12-16");
//		
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertTrue(config.isVerifactu());
//		assertTrue(config.isVerifactuTest());
//		assertEquals(AonDateUtils.getDate(2024, 11, 16), config.getVerifactuData().getStartDate());
//		
//		assertFalse(config.isSii());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE);
//
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getVerifactuData().getId());
//	}
//	
//	@Test
//	public void testAeatWithVerifactuFirstIncludeDate() {
//		Date firstDate = AonDateUtils.getDate(2021, 1, 1);
//		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, new DataResponse()
//				.setDomain(DOMAIN_ID)	
//				.setCode("ok")
//				.setResponseDate(firstDate)
//				.setSource(DataResponseSource.VERIFACTU));
//
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION, Integer.toString(Administration.COMMON_TERRITORY.value()));
//		
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE, "true");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST, "false");
//		AppParamDAO.save(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE, "");
//
//		InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx, DOMAIN_ID);
//		
//		assertEquals(Administration.COMMON_TERRITORY.value(), config.getAdministration().value());
//		
//		assertTrue(config.isVerifactu());
//		assertFalse(config.isVerifactuTest());		
//		assertEquals(firstDate, config.getVerifactuData().getStartDate());
//		
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.FS_DEFAULT_ADMINISTRATION);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE);
//		
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getIdProperty().eq(dr.getId()));
//		
//		config.getAdministrationHistory().stream().forEach(ah -> {
//			EnterpriseDataDAO.delete(ctx, ah.getId());
//		});
//		EnterpriseDataDAO.delete(ctx, config.getVerifactuData().getId());
//	}
//	
//	
//	// utils
//	
//	private void deleteTbaiData() {
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID).and(f.getSourceProperty().eq(DataResponseSource.TBAI.value())));
//		EnterpriseDataDAO.getList(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID)
//				.and(f.getNameProperty().eq(EnterpriseDataNames.ICC_TBAI.name())))
//		.forEach(ed -> {
//			EnterpriseDataDAO.delete(ctx, ed.getId());
//		});
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_REGISTRY_DATE);
//	}
//	
//	private void deleteLroeData() {
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID).and(f.getSourceProperty().eq(DataResponseSource.LROE.value())));
//		EnterpriseDataDAO.getList(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID)
//				.and(f.getNameProperty().eq(EnterpriseDataNames.ICC_LROE.name())))
//		.forEach(ed -> {
//			EnterpriseDataDAO.delete(ctx, ed.getId());
//		});
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_INCLUDE_DATE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.TBAI_REGISTRY_DATE);
//	}	
//	
//	private void deleteSiiData() {
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID).and(f.getSourceProperty().eq(DataResponseSource.SII.value())));
//		EnterpriseDataDAO.getList(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID)
//				.and(f.getNameProperty().eq(EnterpriseDataNames.ICC_SII.name())))
//		.forEach(ed -> {
//			EnterpriseDataDAO.delete(ctx, ed.getId());
//		});
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_INCLUDE_DATE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.SII_REGISTRY_DATE);
//	}	
//	
//	private void deleteVerifactuData() {
//		DataResponseDAO.deleteDataResponse(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID).and(f.getSourceProperty().eq(DataResponseSource.VERIFACTU.value())));
//		EnterpriseDataDAO.getList(ctx, f -> f.getDomainProperty().eq(DOMAIN_ID)
//				.and(f.getNameProperty().eq(EnterpriseDataNames.ICC_VERIFACTU.name())))
//		.forEach(ed -> {
//			EnterpriseDataDAO.delete(ctx, ed.getId());
//		});
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_ACTIVE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_TEST);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_INCLUDE_DATE);
//		AppParamDAO.delete(ctx, DOMAIN_ID, AppParam.VERIFACTU_REGISTRY_DATE);
//	}
	
}
