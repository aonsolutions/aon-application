package com.esferalia.aon.occam.jooq.test;


import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;


public class AttachmentTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "garajeolabe.aibanez.net";
	private static Integer DOMAIN_ID = 596;
	private static String LOGIN = "contacto";

	private LinkedList<Attach> attachList;
	
	public LinkedList<Attach> getAttachList(){
		return attachList;
	}
	
	public void setAttachList(LinkedList<Attach> attachList){
		this.attachList = attachList;
	}
	
	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName(com.mysql.jdbc.Driver.class.getName());
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
		
	}
	
	// ------------------------------------ ATTACHMENT
	
	
	@Test
	@Ignore
	public void test() {
		LinkedList<Attach> attachList = new LinkedList<Attach>();
		
		Attach rattach = generateRattach();attachList.add(rattach);
		Attach contractAttach = generateContractAttach();attachList.add(contractAttach);
		Attach iattach = generateIattach();attachList.add(iattach);
		Attach invoiceAttach = generateInvoiceAttach();attachList.add(invoiceAttach);
		Attach offerAttach = generateOfferAttach();attachList.add(offerAttach);
		Attach payrollAttach = generatePayrollAttach();attachList.add(payrollAttach);
		Attach projectAttach = generateProjectAttach();attachList.add(projectAttach);
		Attach sepeAttach = generateSepeAttach();attachList.add(sepeAttach);
		
		setAttachList(attachList);

		testInsert();
		testSelect();
		generateUpdate();
		testUpdate();
		testSelect();
		testDelete();
		testSelect();
	}
	
	@Test
	@Ignore
	public void testInsert() {
		Date now = new Date();
		getAttachList().stream().forEach(attach ->{
			attach.setId(AON.insertAttach(DOMAIN_NAME, DOMAIN_ID, LOGIN, attach));
			System.out.println("Insert File -- ID: "+ attach.getId());
		});
		System.out.println( (((new Date()).getTime() - now.getTime() )) + " Ms. ");
	}
	
	@Test
	@Ignore
	public void testSelect() {
		Date now = new Date();
		getAttachList().stream().forEach(attach ->{
			Attach a = AON.getAttach(DOMAIN_NAME, DOMAIN_ID, LOGIN,
					filter -> filter.getIdProperty().eq(attach.getId())
					, attach.getAttachType());
			System.out.println("Get File -- ID: "+ a.getId() + " NAME: " + a.getDescription());
		});
		System.out.println( (((new Date()).getTime() - now.getTime() )) + " Ms. ");
	}
	
	@Test
	@Ignore
	public void testUpdate() {
		Date now = new Date();
		getAttachList().stream().forEach(attach ->{
			AON.updateAttach(DOMAIN_NAME, DOMAIN_ID, LOGIN, attach);
			System.out.println("Update File -- ID: "+ attach.getId());
		});
		System.out.println( (((new Date()).getTime() - now.getTime() )) + " Ms. ");
	}
	
	@Test
	@Ignore
	public void testDelete() {
		Date now = new Date();
		getAttachList().stream().forEach(attach ->{
			AON.deleteAttach(DOMAIN_NAME, DOMAIN_ID, LOGIN,
					filter -> filter.getIdProperty().eq(attach.getId())
					, attach.getAttachType());
			System.out.println("Delete File -- ID: "+ attach.getId());
		});
		System.out.println( (((new Date()).getTime() - now.getTime() )) + " Ms. ");
	}
	
	
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
	// ------------------------------------ GENERATE INFORMATION
	
	private Attach generateRattach(){
		return new Attach().setAttachModule(1)
				.setAttachType(AttachType.REGISTRY)
				.setCategory(1)
				.setConfidential(true)
				.setCreationDate(new Date())
				.setCreationUser(LOGIN)
				.setDate(new Date())
				.setDescription("PRUEBA")
				.setDomain(new Domain().setId(DOMAIN_ID).setName(DOMAIN_NAME))
				.setDparentId("0")
				.setDriveId(null)
				.setIcon("")
				.setMimeType(MimeType.TXT)
				.setModificationDate(new Date())
				.setModificationUser(LOGIN)
				.setScope(1)
				.setSourceBatch(1)
				.setSourceType((byte)1)
				.setType((byte)1)
				.setData(getData());
	}
	
	private Attach generateContractAttach(){
		return generateRattach().setAttachType(AttachType.CONTRACT);
	}
	
	private Attach generateIattach(){
		return generateRattach().setAttachType(AttachType.ITEM);
	}
	
	private Attach generateInvoiceAttach(){
		return generateRattach().setAttachType(AttachType.INVOICE);
	}
	
	private Attach generateOfferAttach(){
		return generateRattach().setAttachType(AttachType.OFFER);
	}
	
	private Attach generatePayrollAttach(){
		return generateRattach().setAttachType(AttachType.PAYROLL);
	}
	
	private Attach generateProjectAttach(){
		return generateRattach().setAttachType(AttachType.PROJECT);
	}
	
	private Attach generateSepeAttach(){
		return generateRattach().setAttachType(AttachType.SEPE);
	}
	
	private void generateUpdate() {
		getAttachList().stream().forEach(attach -> {
			attach.setDescription("MOD");
		});
	}
	
	private byte[] getData(){
		return null;
	}
}
