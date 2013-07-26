package com.code.aon.facturae;

import static com.esferalia.aon.entity.IEntityAlias.PROJECT_RESERVATION_PROJECT_ID;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.pms.ProjectReservation;

public class PmsUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PmsUtil.class.getName());
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	private static final String ID_ESTABLECIMIENTO_ELEMENT = "IdEstablecimiento";
	
	private static final String RESERVA_ELEMENT = "Reserva";
	
	private static final String BONO_ELEMENT = "Bono";
	
	private static final String FECHA_ENTRADA_ELEMENT = "FechaEntrada";
	
	private static final String FECHA_SALIDA_ELEMENT = "FechaSalida";
	
	private static final String NOMBRE_PAX_ELEMENT = "NombrePax";
	
	private static final String CANTIDAD_PAX_ELEMENT = "CantidadPax";
	
	private static final String ADULTOS_ELEMENT = "Adultos";
	
	private static final String CHILDS_ELEMENT = "Ni\u00F1os";
	
	private static final String CUNAS_ELEMENT = "Cunas";
	
	private ProjectReservation reservation;
	
	private String guestFullName;
	
	private int adultCount;
	
	private int childCount;
	
	public PmsUtil(Invoice invoice) {
		init( invoice );
	}

	private void init( Invoice invoice ) {
		if (! invoice.isService() ) {
			Project project = invoice.getProject();
			if ( (project != null) && (project.getId() != null) ) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(PROJECT_RESERVATION_PROJECT_ID), project.getId());
					List<ITransferObject> list = bean.getList(criteria);
					if (! list.isEmpty() ) {
						this.reservation = (ProjectReservation) list.get(0);
						this.guestFullName = this.reservation.getGuestFullName();
						this.adultCount = this.reservation.getAdultCount();
						this.childCount = this.reservation.getChildCount();
					}
				} catch (ManagerBeanException e) {
					LOGGER.error( e.getMessage(), e );
				}
			}
		}
	}
	
	public boolean isAddExtensions() {
		return this.reservation != null;
	}
	
	private void fillExtension( Element element ) {
		element.addElement(ID_ESTABLECIMIENTO_ELEMENT).addText(reservation.getHotelReservation().getCode());
		element.addElement(RESERVA_ELEMENT).addText(String.valueOf(reservation.getId()));
		element.addElement(BONO_ELEMENT).addText(reservation.getCode());
		String start = DATE_FORMAT.format(reservation.getStartDate());
		element.addElement(FECHA_ENTRADA_ELEMENT).addText(start);
		String end = DATE_FORMAT.format(reservation.getEndDate());
		element.addElement(FECHA_SALIDA_ELEMENT).addText(end);
		element.addElement(NOMBRE_PAX_ELEMENT).addText(this.guestFullName);
		Element countElement = element.addElement(CANTIDAD_PAX_ELEMENT);
		countElement.addElement(ADULTOS_ELEMENT).addText(String.valueOf(this.adultCount));
		countElement.addElement(CHILDS_ELEMENT).addText(String.valueOf(this.childCount));
		countElement.addElement(CUNAS_ELEMENT).addText("0");
	}
	
	@SuppressWarnings("unchecked")
	private void fillExtensions( Document document ) {
		List<Node> list = document.selectNodes("//InvoiceLine/Extensions");
		if ( list != null ) {
			for( Node node : list ) {
				fillExtension( (Element) node );
			}
		}
	}
	
	public void transform( String fileName ) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		File file = new File(fileName);
		Document document = reader.read(file);
		fillExtensions(document);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setNewLineAfterDeclaration(false);
		XmlWriter writer = new XmlWriter( new FileWriter(file), format );
		writer.write( document );		
		writer.close();
	}
	
}
