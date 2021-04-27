package com.esferalia.aon.gwt.payroll.util;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
/**
 * Class containing some utilities for payroll generator methods
 */
public class Utilities {
	/**
	 * Method to separate a string in various lines of a concrete length
	 * @param str The original string to be separated
	 * @param length The maximum length of the lines
	 * @return A String array containing the lines
	 */
	public static String[] separateString (String str, int length) {
		if (str == null) return null;
		if (str.length()<=length) {
			return new String[] {str};
		}
		else {
			LinkedList<String> strList = new LinkedList<String>();
			String tempStr = str;
			while (!tempStr.isEmpty()) {
				int lng = length;
				if (tempStr.length()<=lng) {
					lng = tempStr.length();
				}
				String line = tempStr.substring(0, lng);
				if (tempStr.charAt(lng-1)!=' ' && tempStr.length()>lng && line.contains(" ")) {
					int ind = line.lastIndexOf(' ');
					line = tempStr.substring(0, ind);
				}
				strList.add(line);
				if (tempStr.length() <= lng) {
					tempStr = "";
				}
				else
					tempStr = tempStr.substring(line.length());
			}
			return strList.toArray(new String[strList.size()]);
		}
	}
	
	/**
	 * Method to get an enum element out of its ordinal
	 * @param ordinal The ordinal of the enum element
	 * @param type The class of the enum
	 * @return the enum element
	 */
	public static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
		if ( ordinal == null )
			return null;
		try {
			return type.getEnumConstants()[ordinal];
		} catch ( Exception e ) {
			return null;
		}
	}
	
	/**
	 * Method to get the signature logo of an enterprise (or its logo if there is no signature) given the domain name
	 * @param domainName The domain name of the enterprise
	 * @return An optional with an InputStream containing the logo
	 */
	public static Optional<InputStream> getSignature(String domainName) {
		
		Optional<InputStream> optLogo = Optional.empty();
		
		try (AONContext aonContext = AONContext.getAONContext(domainName, "")) {
			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.SIGNATURE.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			if (attach1 == null || attach1.getData() == null)
				attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
					AttachType.REGISTRY);
			if (attach1 != null && attach1.getData() != null)
				optLogo = Optional.ofNullable(new ByteArrayInputStream(attach1.getData()));
		} catch (Exception e) {}
		return optLogo;
	}
	
	/**
	 * Method to get the abbreviation name commonly used on the database for a different deduction types
	 * @param type Ordinal of the DeductionType
	 * @return a string containing the key name of the deduction type
	 */
	public static String getDeductionType (Integer type) {
		switch (type) {
			case 0:
				return "CGC";
			case 2:
				return "DESMPL";
			case 3:
				return "FP";
			case 4:
				return "ESTR";
			case 5:
				return "NO_ESTR";
			case 6:
				return "IRPF";
			case 7:
				return "ADELANTO";
			case 8:
				return "EN_ESPECIE";
			case 9:
				return "OTRO";
			case 10:
				return "EMBARGO";
			default:
				return null;
		}
	}
	
	/**
	 * Method to get the deduction type int of the DefaultPayroll given the DeductionType of the payroll
	 * @param dt The original DeductionType
	 * @return the int of the type for DefaultPayroll
	 */
	public static int chooseType (DeductionType dt) {
		switch (dt.ordinal()) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return 1;
		case 6:
			return 2;
		case 7:
			return  3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}
	
	/**
	 * Method to get the deduction type int of the DefaultPayroll given the int
	 * @param dt The original int
	 * @return the int of the type for DefaultPayroll
	 */
	public static int chooseType (int dt) {
		switch (dt) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return 1;
		case 6:
			return 2;
		case 7:
			return  3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}
	
	/**
	 * Method to get a suitable description for the given DeductionType
	 * @param dt The DeductionType enum object
	 * @return a String containing a suitable description
	 */
	public static String chooseDescription (DeductionType dt) {
		switch (dt.ordinal()) {
		case 0:
			return "Contingencias comunes";
		case 1:
			return "Contingencias profesionales";
		case 2:
			return "Desempleo";
		case 3:
			return "Formación profesional";
		case 4:
			return "Horas extraordinarias (Estruc.)";
		case 5:
			return "Horas extraordinarias (No Estruc.)";
		case 6:
			return "Retribuciones dinerarias";
		case 7:
			return "Anticipo";
		case 8:
			return "En especie";
		case 10:
			return "Embargo";
		default:
			return "Otras deducciones";
		}
	}
	
	/**
	 * Method to get a suitable description for the given int
	 * @param dt 
	 * @return a String containing a suitable description
	 */
	public static String chooseDescription (int dt) {
		switch (dt) {
		case 0:
			return "Contingencias comunes";
		case 1:
			return "Contingencias profesionales";
		case 2:
			return "Desempleo";
		case 3:
			return "Formación profesional";
		case 4:
			return "Horas extraordinarias (Estruc.)";
		case 5:
			return "Horas extraordinarias (No Estruc.)";
		case 6:
			return "Retribuciones dinerarias";
		case 7:
			return "Anticipo";
		case 8:
			return "En especie";
		case 10:
			return "Embargo";
		default:
			return "Otras deducciones";
		}
	}
	
	/**
	 * <p>
	 * <b>Description:</b> <i>Format a date with specific format. </i>
	 * </p>
	 * 
	 * @return formatted date String (Optional)
	 */
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}
	
	public static String getDomainNameByEnterpriseId(String domain, Integer enterpriseId) {
		try {
			AONContext aonContext = AONContext.getAONContext(domain, "");
			Integer dom = AON.getEnterprise(aonContext.getDomainName(), aonContext.getDomainId(), "", enterpriseId).getDomain();
			DomainRecord domainRecord = aonContext.getDslContext().select().from(DOMAIN).where(DOMAIN.ID.eq(dom)).fetchAnyInto(DOMAIN);
			return domainRecord.getName();
		} catch (Exception e) {
			return null;
		}
	}

}
