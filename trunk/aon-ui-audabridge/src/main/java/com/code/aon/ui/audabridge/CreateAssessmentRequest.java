package com.code.aon.ui.audabridge;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class CreateAssessmentRequest {
	
	private static final String ENCODING = "ISO-8859-1";
	private static final String MESSAGE = "Message";
	private static final String HEADER = "Header";
	private static final String MESSAGE_TYPE_IDENTIFIER = "MessageTypeIdentifier";
	private static final String CREATE_ASSESSMENT_REQUEST = "CreateAssessmentRequest";
	private static final String BODY = "Body";
	private static final String REFERENCE_NUMBER = "ReferenceNumber";
	private static final String CUSTOMER_ID = "CustomerId";
	private static final String REPAIRER_ID = "RepairerId";
	private static final String PARTNERSHIP = "Partnership";
	private static final String PAINT_RATE = "PaintRate";
	private static final String BODYWORK_RATE = "BodyworkRate";	
	private static final String LABOUR_RATE = "LabourRate";	
	private static final String VEHICLE = "Vehicle";	
	private static final String REGISTRATION = "Registration";
	private static final String ODOMETER = "Odometer";
	private static final String VEHICLE_REGISTRATION_DATE = "VehicleRegistrationDate";	

	private static final NumberFormat INT_FORMATTER = new DecimalFormat("0");
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * Referencia de la valoración.
	 */
	private String referenceNumber;
	
	/**
	 * Número de abonado.
	 */
	private String customerId;
	
	/**
	 * Identificación del taller.
	 */
	private String repairerId;
	
	/**
	 * Precio de la hora de pintura.
	 */
	private double paintRate;
	
	/**
	 * Precio de la hora de chapa.
	 */
	private double bodyworkRate;

	/**
	 * Precio de la hora.
	 */
	private double labourRate;
	
	/**
	 * Número de matrícula.
	 */
	private String registration;
	
	/**
	 * Kilometraje del vehículo.
	 */
	private double odometer;
	
	/**
	 * Fecha de matriculación del vehículo.
	 */
	private Date registrationDate;

	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getRepairerId() {
		return repairerId;
	}
	public void setRepairerId(String repairerId) {
		this.repairerId = repairerId;
	}

	public double getPaintRate() {
		return paintRate;
	}
	public void setPaintRate(double paintRate) {
		this.paintRate = paintRate;
	}

	public double getBodyworkRate() {
		return bodyworkRate;
	}
	public void setBodyworkRate(double bodyworkRate) {
		this.bodyworkRate = bodyworkRate;
	}

	public double getLabourRate() {
		return labourRate;
	}
	public void setLabourRate(double labourRate) {
		this.labourRate = labourRate;
	}

	public String getRegistration() {
		return registration;
	}
	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public double getOdometer() {
		return odometer;
	}
	public void setOdometer(double odometer) {
		this.odometer = odometer;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}
	

	public void createXMLRequest(Writer writer) throws AudaBridgeException{
		try {
			if (StringUtils.isBlank(getReferenceNumber())) {
				throw new AudaBridgeException("El número de referencia no puede estar vacio.");				
			}
			DocumentFactory factory = DocumentFactory.getInstance();
			Document doc = factory.createDocument( ENCODING );
			Element root = factory.createElement( MESSAGE );
			doc.setRootElement(root);
			
			Element header = root.addElement(HEADER);
			header.addElement(MESSAGE_TYPE_IDENTIFIER).addText(CREATE_ASSESSMENT_REQUEST);
			
			Element body = root.addElement(BODY);
			Element assessment = body.addElement(CREATE_ASSESSMENT_REQUEST);
			assessment.addElement(REFERENCE_NUMBER).addText(getReferenceNumber());
			if (StringUtils.isNotBlank(getCustomerId())) {
				assessment.addElement(CUSTOMER_ID).addText(getCustomerId());	
			}
			if (StringUtils.isNotBlank(getRepairerId())) {
				assessment.addElement(REPAIRER_ID).addText(getRepairerId());	
			}
			Element partnership = assessment.addElement(PARTNERSHIP);
			partnership.addElement(PAINT_RATE).addText(Double.toString(getPaintRate()));
			partnership.addElement(BODYWORK_RATE).addText(Double.toString(getBodyworkRate()));
			partnership.addElement(LABOUR_RATE).addText(Double.toString(getLabourRate()));

			Element vehicle = assessment.addElement(VEHICLE);
			if (StringUtils.isNotBlank(getRegistration())) {
				vehicle.addElement(REGISTRATION).addText(getRegistration());	
			}
			if (getOdometer() != 0.0) {
				vehicle.addElement(ODOMETER).addText(INT_FORMATTER.format(getOdometer()));	
			}
			if (getRegistrationDate() != null) {
				vehicle.addElement(VEHICLE_REGISTRATION_DATE).addText(DATE_FORMATTER.format(getRegistrationDate()));
			}
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding(ENCODING);
			XMLWriter xmlWriter = new XMLWriter(writer, outformat);
			xmlWriter.write(doc);
			xmlWriter.flush();
		} catch (UnsupportedEncodingException e) {
			throw new AudaBridgeException(e.getLocalizedMessage(),e);
		} catch (IOException e) {
			throw new AudaBridgeException(e.getLocalizedMessage(),e);
		}
	}
	
}
