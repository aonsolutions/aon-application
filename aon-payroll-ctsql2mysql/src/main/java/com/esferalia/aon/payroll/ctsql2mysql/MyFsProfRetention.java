package com.esferalia.aon.payroll.ctsql2mysql;

import java.util.Date;
import java.sql.SQLException;

import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Otrperc;

public class MyFsProfRetention extends DefaultCtsqlDBVisitor {
	
	private Date fromDate;
	private DefaultMysqlDB mysqlDB;
	private IEnterprises enterprises;

	public MyFsProfRetention(DefaultMysqlDB mysqlDB, IEnterprises enterprises, Date fromDate) {
		this.fromDate = fromDate;
		this.mysqlDB = mysqlDB;
		this.enterprises = enterprises;
	}
	
	@Override
	public void visit(AbstractCtsqlDB ctsqlDB) throws SQLException {
		ctsqlDB.visitOtrperc(this);
	}
	
	@Override
	public void visitOtrperc(Otrperc otrperc) throws SQLException {

		java.sql.Date paymentDate = otrperc.getFecha();
		if ( outOfDate(paymentDate)) {
			return;
		}
		
		Integer enterprise = enterprises.getEnterprise(otrperc.getCodemp());
		if ( enterprise == null ) {
			MysqlDB.error("otrperc[{}]: Enterprise not found for {}", 
					otrperc.getCdg(),
					otrperc.getCodemp());
			return;
		}
		
		String clave = otrperc.getClave();
		String withholdingKey = isValidKey( clave );
		if ( withholdingKey == null ) {
			MysqlDB.error("otrperc[{}]: Key invalid {}", 
					otrperc.getCdg(),
					clave);
			return;
		}
		
		String subClave = otrperc.getSubclave();
		String withholdingSubkey = isValidSubkey( clave, subClave );
		if ( withholdingSubkey == null ) {
			MysqlDB.error("otrperc[{}]: SubKey invalid {}/{}", 
					otrperc.getCdg(),
					clave, subClave);
		}
		
		String concept = otrperc.getConcepto();

		double importe = MysqlDB.toDouble( otrperc.getImporte() );
		double percent = MysqlDB.toDouble( otrperc.getPrcret() );
		double retencion = MysqlDB.toDouble( otrperc.getRetencion() );
		
		Boolean inKind = "D".equals(otrperc.getNatret()) ? Boolean.FALSE: Boolean.TRUE;
		
		Otrperc_persona otrperc_persona = 
			new Otrperc_persona();
		otrperc.visitOtrperc_persona(otrperc_persona);
		
		String document = otrperc_persona.getPersona_Numdoc();
		
		DocumentType docType = mysqlDB.getDocumentType(otrperc_persona.getPersona_Inddoc());
		if ( docType == null ) {
			docType = DocumentType.NIF;
		}
		
		Country docCountry = mysqlDB.getCountry( otrperc_persona.getPersona_Paiemi() );
		
		String nombre = otrperc_persona.getPersona_Nombre();
		String apellido = otrperc_persona.getPersona_Descripcion();
		String apellido2 = otrperc_persona.getPersona_Apellido2();

		String name =  (nombre!=null?nombre:"") 
			+(apellido!=null?" " + apellido:"")
			+(apellido2!=null?" " + apellido2:"");
		/*
		mysqlDB.insertFs_prof_retention(
				enterprise, 
				paymentDate, 
				document, 
				MysqlDB.enum2short(docType), 
				docCountry != null ? docCountry.getValue() : null, 
				name, 
				concept, 
				importe, //taxable_base, 
				percent, 
				retencion, //quota, 
				inKind, 
				withholdingKey, 
				withholdingSubkey);
				*/
	}

	private boolean outOfDate ( Date date ) {
		if ( date == null )
			return false;
		if ( fromDate == null )
			return false;
		return fromDate.compareTo(date) > 0 ; 
	}
	
	private static String isValidKey(String clave) {
		if (clave == null || "".equals(clave.trim())) {
			return null;
		}
		if ("A".equals(clave)) return "A";
		if ("B".equals(clave)) return "B";
		if ("C".equals(clave)) return "C";
		if ("D".equals(clave)) return "D";
		if ("E".equals(clave)) return "E";
		if ("F".equals(clave)) return "F";
		if ("G".equals(clave)) return "G";
		if ("H".equals(clave)) return "H";
		if ("I".equals(clave)) return "I";
		if ("J".equals(clave)) return "J";
		if ("K".equals(clave)) return "K";
		if ("L".equals(clave)) return "L";
		
		return null;
	}

	private static String isValidSubkey(String clave, String subclave) {
		if (clave == null || "".equals(clave.trim())) {
			return null;
		}
		if (subclave == null || "".equals(subclave.trim())) {
			return null;
		}
		if ("A".equals(clave)		 || "C".equals(clave)
		 || "D".equals(clave)		 || "E".equals(clave)
		 || "J".equals(clave)) {
			System.out.println( "Error subclave ..: " + clave + "-" + subclave);
			return null;
		}
		if ("B".equals(clave) || "F".equals(clave) || "I".equals(clave)) {
			if ("01".equals(subclave) || "02".equals(subclave)) {
				return clave.trim() + subclave;
			}
		}
		if ("G".equals(clave)) {
			if ("01".equals(subclave) || "02".equals(subclave)|| "03".equals(subclave)) {
				return clave.trim() + subclave;
			}
		}
		if ("H".equals(clave)) {
			if ("01".equals(subclave) || "02".equals(subclave)|| "03".equals(subclave)|| "04".equals(subclave)) {
				return clave.trim() + subclave;
			}
		}
		if ("K".equals(clave)) {
			if ("01".equals(subclave) || "02".equals(subclave)) {
				return clave.trim() + subclave;
			}
		}
		if ("L".equals(clave)) {
			if (
				"01".equals(subclave) || "02".equals(subclave)|| "03".equals(subclave)|| "04".equals(subclave)
				|| "05".equals(subclave) || "06".equals(subclave)|| "07".equals(subclave)|| "08".equals(subclave)
				|| "09".equals(subclave) || "10".equals(subclave)|| "11".equals(subclave)|| "12".equals(subclave)
				|| "13".equals(subclave) || "14".equals(subclave)|| "15".equals(subclave)|| "16".equals(subclave)
				|| "17".equals(subclave) || "18".equals(subclave)|| "19".equals(subclave)|| "21".equals(subclave)
				|| "22".equals(subclave) || "23".equals(subclave)
				) {
				return clave.trim() + subclave;
			}
		}
		
		return null;
	}

}
