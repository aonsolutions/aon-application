package com.esferalia.aon.gwt.fiscal.test.mod180;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import com.esferalia.aon.gwt.fiscal.server.Mod180CertificatePrint;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.Administration;

import net.sf.jasperreports.engine.JRException;



public class Model180CertificatePrintTestCase {
	
	static final String TESTING_TEMPLATE = "/home/COMMON-RESOURCES/aon-report/retentionCertificate/mod180_retentionCertificate.jasper";
	
	public static void main(String[] args) {
		
		Mod180 mod180 = getTestMod180();
		Mod180CertificatePrint print = new Mod180CertificatePrint();
		
		try {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("mod180", mod180);
			params.put("messages", ResourceBundle.getBundle(print.MESSAGES_RESOURCE_BUNDLE));
			byte[] employeeData = print.createReport(new BufferedInputStream(new FileInputStream(TESTING_TEMPLATE)), params, mod180.getDetails());
			FileUtils.writeByteArrayToFile(File.createTempFile("certificate_mod180",".pdf"), employeeData);
			
		} catch (JRException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static Mod180 getTestMod180() {
		Mod180 mod180 = new Mod180();
		mod180.setYear(2015);
		mod180.setAdministration(Administration.ALAVA);
		mod180.setConfidential(false);
		mod180.setReplacement(false);
		mod180.setReceipt("");
		mod180.setReplacedReceipt("");
		mod180.setComments("");
		mod180.setDocument("");
		mod180.setName("");
		mod180.setContactPerson("");
		mod180.setContactPhone("");
		mod180.setReceiverCountTotal(0);
		mod180.setReceiptTotal(0.0);
		mod180.setRetentionTotal(0.0);
		mod180.setDetails(new LinkedList<Mod180Detail>());
		Mod180Detail detail = new Mod180Detail();
		detail.setMod180(0);
		detail.setName(null);
		detail.setDocument(null);
		detail.setRepresentativeDocument(null);
		detail.setProvince(0);
		detail.setInKind(false);
		detail.setPercent(0.0);
		detail.setPerception(0.0);
		detail.setRetention(0.0);
		detail.setAccrualYear(2015);
		detail.setLocation(null);
		detail.setCadasdralReference(null);
		detail.setStreetType(null);
		detail.setStreetName(null);
		detail.setNumberType(null);
		detail.setNumber(null);
		detail.setNumberSuffix(null);
		detail.setBlock(null);
		detail.setHall(null);
		detail.setStair(null);
		detail.setFloor(null);
		detail.setDoor(null);
		detail.setComplement(null);
		detail.setCity(null);
		detail.setTown(null);
		detail.setTownCode(null);
		detail.setProvinceCode(null);
		detail.setZip(null);
		mod180.getDetails().add(detail);
		return mod180;
	}
	
}
