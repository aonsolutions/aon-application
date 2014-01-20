package com.esferalia.aon.gwt.fiscal.sql;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.fiscal.enumeration.Period;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Administraciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.Conjunta;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.OpTercerasPax;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.Otras;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.Pral;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatIdent;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.ConcursoUltPerNO;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.ConcursoUltPerSI;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.DecSustitutiva;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.RegDevMensual;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.LiqAnual;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.OpEspecificas;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.AdqIntracomBienes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.AdqIntracomServicios;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModBasesyCuotas;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.OpIntragrupo;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RecargoEquivalencia;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RegAgViajes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RegBienesUsados;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RegOrdinario;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.AdqIntracomunitariasServicios;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.ComRegAgricGanadPesca;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.ImportacionesBienesCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.ImportacionesBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpInterioresBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpIntragrupoBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpIntragrupoCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.RectifDeducciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.ResLiquidaciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.ResLiquidaciones.PerNoRegGrupos;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.ResLiquidaciones.PerSiRegGrupos;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.VolOperaciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoBaseImponibleYCuota;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoDoc;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoDomicilio;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Art65NO;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Art65SI;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Dependiente;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Dominante;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.UltAutoliquidNO;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.UltAutoliquidSI;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoIdentificacionPersonaFisica;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoIdentificacionPersonaJuridica;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoPersonaFisica;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoPersonaJuridica;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoRepresentanteFisica;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoRepresentanteJuridica;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.Address;
import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Mod390DetailKey;
import com.esferalia.aon.gwt.fiscal.shared.LegalRepresentative;
import com.esferalia.aon.gwt.fiscal.shared.Mod303Results;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.FsModel390Columns;
import com.esferalia.aon.payroll.sql.SQLConstants.FsVatColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.FsVatDeclarationColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceDetailColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceTaxColumns;

public class SQLMod390 {
	// private static Logger LOGGER = Logger.getLogger(SQLMod390.class.getName());

	//@formatter:off
	private static final String MOD390_SELECT_BY_DOMAIN = "SELECT "
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ID + " " + FsModel390Columns.ID + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ADMINISTRATION + " " + FsModel390Columns.ADMINISTRATION + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.YEAR + " " + FsModel390Columns.YEAR + ","
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.DOMAIN + " " + FsModel390Columns.DOMAIN+ ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ENTERPRISE + " " + FsModel390Columns.ENTERPRISE + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.DOCUMENT + " " + FsModel390Columns.DOCUMENT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.NAME + " " + FsModel390Columns.NAME + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.RECEIPT + " " + FsModel390Columns.RECEIPT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.REPLACEMENT  + " " + FsModel390Columns.REPLACEMENT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.REPLACED_RECEIPT + " " + FsModel390Columns.REPLACED_RECEIPT  
			+" FROM " + SQLConstants.FS_MODEL390
			+ " INNER JOIN " + SQLConstants.DOMAIN
			+ " ON " + SQLConstants.FS_MODEL390 + "." +SQLConstants.FsModel390Columns.DOMAIN
			+ "=" + SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.ID
			+ " WHERE  ( " 
			+ SQLConstants.FsModel390Columns.DOMAIN + " = ? OR ( "
			+ SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.PARENT + " IS NOT NULL AND " +
			SQLConstants.FS_MODEL390 + "." +SQLConstants.FsModel390Columns.DOMAIN + " IN (SELECT " +
			SQLConstants.DomainColumns.ID + " FROM " + SQLConstants.DOMAIN + " WHERE " +
			SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.PARENT + " =  ? )))"
			+ " ORDER BY "
				+ SQLConstants.FS_MODEL390 + "." + FsModel390Columns.YEAR + " DESC, "
				+ SQLConstants.FS_MODEL390 + "." + FsModel390Columns.NAME + ", "
				+ SQLConstants.FS_MODEL390 + "." + FsModel390Columns.REPLACEMENT;
	
	
	private static final String MOD390_SELECT = "SELECT "
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ID + " " + FsModel390Columns.ID + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ADMINISTRATION + " " + FsModel390Columns.ADMINISTRATION + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.YEAR + " " + FsModel390Columns.YEAR + ","
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.DOMAIN + " " + FsModel390Columns.DOMAIN+ ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ENTERPRISE + " " + FsModel390Columns.ENTERPRISE + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.DOCUMENT + " " + FsModel390Columns.DOCUMENT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.NAME + " " + FsModel390Columns.NAME + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.RECEIPT + " " + FsModel390Columns.RECEIPT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.REPLACEMENT  + " " + FsModel390Columns.REPLACEMENT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.REPLACED_RECEIPT + " " + FsModel390Columns.REPLACED_RECEIPT + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.COMMENTS + " " + FsModel390Columns.COMMENTS + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.COMPLEMENTARY + " " + FsModel390Columns.COMPLEMENTARY + ","  
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.SECURITY_LEVEL + " " + FsModel390Columns.SECURITY_LEVEL + ","
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.STATUS + " " + FsModel390Columns.STATUS + ","
			+SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.MODEL + " " + FsModel390Columns.MODEL 
			+" FROM " + SQLConstants.FS_MODEL390
			+" WHERE " + SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ID + " = ?";
			
	private static final String MOD390_DELETE = "DELETE FROM " +SQLConstants.FS_MODEL390 
			+" WHERE " + SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ID + " = ?";

	private static final String MOD390_INSERT = "INSERT INTO "
			+SQLConstants.FS_MODEL390 + "( " 
			+ FsModel390Columns.DOMAIN + ","
			+ FsModel390Columns.ENTERPRISE + ","
			+ FsModel390Columns.YEAR + ","
			+ FsModel390Columns.ADMINISTRATION + ","
			+ FsModel390Columns.STATUS + ","
			+ FsModel390Columns.SECURITY_LEVEL + ","
			+ FsModel390Columns.DOCUMENT + ","
			+ FsModel390Columns.NAME + ","
			+ FsModel390Columns.COMPLEMENTARY + ","
			+ FsModel390Columns.REPLACEMENT + ","
			+ FsModel390Columns.COMMENTS + ","
			+ FsModel390Columns.RECEIPT + ","
			+ FsModel390Columns.REPLACED_RECEIPT + ","
			+ FsModel390Columns.MODEL 
			+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
	
	private static final String MOD390_UPDATE = "UPDATE "
			+SQLConstants.FS_MODEL390 + " SET " 
			+ FsModel390Columns.DOMAIN + "=?,"
			+ FsModel390Columns.ENTERPRISE + "=?,"
			+ FsModel390Columns.YEAR + "=?,"
			+ FsModel390Columns.ADMINISTRATION + "=?,"
			+ FsModel390Columns.STATUS + "=?,"
			+ FsModel390Columns.SECURITY_LEVEL + "=?,"
			+ FsModel390Columns.DOCUMENT + "=?,"
			+ FsModel390Columns.NAME + "=?,"
			+ FsModel390Columns.COMPLEMENTARY + "=?,"
			+ FsModel390Columns.REPLACEMENT + "=?,"
			+ FsModel390Columns.COMMENTS + "=?,"
			+ FsModel390Columns.RECEIPT + "=?,"
			+ FsModel390Columns.REPLACED_RECEIPT + "=?,"
			+ FsModel390Columns.MODEL + "=?"
			+" WHERE " + SQLConstants.FS_MODEL390 +"."+ FsModel390Columns.ID + " = ?";  

	private static final String INVOICE_SELECT = "SELECT "
			+SQLConstants.INVOICE +"."+ InvoiceColumns.TYPE + " " + InvoiceColumns.TYPE +","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.RECTIFICATION_TYPE + " " + InvoiceColumns.RECTIFICATION_TYPE+ ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.SERVICE + " "+ InvoiceColumns.SERVICE + ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.TRANSACTION +" "+ InvoiceColumns.TRANSACTION+ ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.INVESTMENT +" "+ InvoiceColumns.INVESTMENT+ ","
			+SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.PERCENTAGE +" "+ InvoiceTaxColumns.PERCENTAGE+ ","
			+SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.SURCHARGE +" "+ InvoiceTaxColumns.SURCHARGE+ ","
			+ SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.VAT_DEDUCTION_TYPE +" "+ InvoiceTaxColumns.VAT_DEDUCTION_TYPE+ ","
			+" SUM( " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.TAXABLE_BASE + ") " + InvoiceDetailColumns.TAXABLE_BASE + ","
			+ "SUM( IF( " + SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.QUOTA + "!= 0," +
					SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.QUOTA +"," +
					"ROUND(" + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.TAXABLE_BASE + "*" 
					+ SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.PERCENTAGE + "/ 100, 2) ) ) " + InvoiceTaxColumns.QUOTA+ "," 
			+" SUM( IF(" + SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.SURCHARGE_QUOTA+ " != 0," +
					SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.SURCHARGE_QUOTA+"," 
					+"ROUND(" + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.TAXABLE_BASE + "*" 
					+ SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.SURCHARGE +"/ 100, 2) ) ) "  + InvoiceTaxColumns.SURCHARGE_QUOTA
			+" FROM " + SQLConstants.INVOICE_TAX
		+" INNER JOIN "+SQLConstants.INVOICE_DETAIL
		+ " ON " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.INVOICE_DETAIL 
		+ " = " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.ID 
		+" INNER JOIN "+SQLConstants.INVOICE
		+" ON " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.INVOICE
		+ " = " + SQLConstants.INVOICE + "." + InvoiceColumns.ID
		+" WHERE "+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.DOMAIN +" = ?"
		+" AND " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.TAX_TYPE + " = 1"
		+" AND "+ SQLConstants.INVOICE + "." + InvoiceColumns.TAX_DATE +" BETWEEN ? AND ?"
		+" GROUP BY "
			+SQLConstants.INVOICE +"."+ InvoiceColumns.TYPE + ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.RECTIFICATION_TYPE + ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.SERVICE + ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.TRANSACTION + ","
			+SQLConstants.INVOICE +"."+ InvoiceColumns.INVESTMENT + ","
			+SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.PERCENTAGE + ","
			+SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.SURCHARGE + ","
			+ SQLConstants.INVOICE_TAX +"."+ InvoiceTaxColumns.VAT_DEDUCTION_TYPE
		;
	//@formatter:on

	public static Mod390 save(Connection conn, Mod390 mod390) throws AonSQLException {
		if (mod390.getId() == null) {
			return insert(conn, mod390);
		} else {
			return update(conn, mod390);
		}
	}

	
	private static Mod390 update(Connection conn, Mod390 mod390)
		throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD390_UPDATE);
			SQLUtils.setInt(stmt, 1, mod390.getDomain());
			SQLUtils.setInt(stmt, 2, mod390.getEnterprise());
			SQLUtils.setInt(stmt, 3, mod390.getYear());
			SQLUtils.setInt(stmt, 4, mod390.getAdministration());
			SQLUtils.setInt(stmt, 5, 0);
			SQLUtils.setInt(stmt, 6, mod390.isConfidential() ? 1 : 0);
			SQLUtils.setString(stmt, 7, mod390.getDocument());
			SQLUtils.setString(stmt, 8, mod390.getName());
			SQLUtils.setInt(stmt, 9, 0);
			SQLUtils.setInt(stmt, 10,mod390.isReplacement() ? 1 : 0);
			SQLUtils.setString(stmt, 11, mod390.getComments());
			SQLUtils.setString(stmt, 12, mod390.getReceipt());
			SQLUtils.setString(stmt, 13, mod390.getReplacedReceipt());
			
			AEATIVA2013 iva = getAEATIVA2013(mod390);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);				
			Marshaller um = context.createMarshaller();
			um.marshal(iva,writer);
			SQLUtils.setString(stmt, 14, writer.toString());
			SQLUtils.setInt(stmt, 15, mod390.getId());
			stmt.execute();
			return getById(mod390.getId(), conn);
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}

	}


	private static Mod390 insert(Connection conn, Mod390 mod390)
			throws AonSQLException {
		PreparedStatement insertStmt = null;
		ResultSet rs = null;
		try {
			insertStmt = conn.prepareStatement(MOD390_INSERT,
					Statement.RETURN_GENERATED_KEYS);
			SQLUtils.setInt(insertStmt, 1, mod390.getDomain());
			SQLUtils.setInt(insertStmt, 2, mod390.getEnterprise());
			SQLUtils.setInt(insertStmt, 3, mod390.getYear());
			SQLUtils.setInt(insertStmt, 4, mod390.getAdministration());
			SQLUtils.setInt(insertStmt, 5, 0);
			SQLUtils.setInt(insertStmt, 6, mod390.isConfidential() ? 1 : 0);
			SQLUtils.setString(insertStmt, 7, mod390.getDocument());
			SQLUtils.setString(insertStmt, 8, mod390.getName());
			SQLUtils.setInt(insertStmt, 9, 0);
			SQLUtils.setInt(insertStmt, 10,mod390.isReplacement() ? 1 : 0);
			SQLUtils.setString(insertStmt, 11, mod390.getComments());
			SQLUtils.setString(insertStmt, 12, mod390.getReceipt());
			SQLUtils.setString(insertStmt, 13, mod390.getReplacedReceipt());
			
			AEATIVA2013 iva = getAEATIVA2013(mod390);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);				
			Marshaller um = context.createMarshaller();
			um.marshal(iva,writer);
			SQLUtils.setString(insertStmt, 14, writer.toString());
			
			insertStmt.execute();
			rs = insertStmt.getGeneratedKeys();
			if (!rs.next()) {
				throw new AonSQLException("Unable to recover last inserted id");
			}
			mod390.setId(rs.getInt(1));
			return getById(mod390.getId(), conn);
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(insertStmt);
		}
	}
	

	public static ArrayList<Mod390> getByDomain(int domain, Connection conn)
			throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD390_SELECT_BY_DOMAIN,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, domain);
			rs = stmt.executeQuery();
			ArrayList<Mod390> list = new ArrayList<Mod390>();
			Mod390 mod390 = null;
			while (rs.next()) {
				mod390 = new Mod390();
				mod390.setId(rs.getInt(FsModel390Columns.ID));
				mod390.setAdministration(rs.getInt(FsModel390Columns.ADMINISTRATION));
				mod390.setYear(rs.getInt(FsModel390Columns.YEAR));
				mod390.setDomain(rs.getInt(FsModel390Columns.DOMAIN));
				mod390.setEnterprise(rs.getInt(FsModel390Columns.ENTERPRISE));  
				mod390.setDocument(rs.getString(FsModel390Columns.DOCUMENT));
				mod390.setName(rs.getString(FsModel390Columns.NAME)); 
				mod390.setReceipt(rs.getString(FsModel390Columns.RECEIPT));
				mod390.setReplacement(rs.getBoolean(FsModel390Columns.REPLACEMENT));
				mod390.setReplacedReceipt(rs.getString(FsModel390Columns.REPLACED_RECEIPT));  
				list.add(mod390);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static Mod390 getById(int id, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD390_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			Mod390 mod390 = null;
			rs = stmt.executeQuery();
			while (rs.next()) {
				mod390 = new Mod390();
				mod390.setId(rs.getInt(FsModel390Columns.ID));
				mod390.setDomain(rs.getInt(FsModel390Columns.DOMAIN));
				mod390.setEnterprise(rs.getInt(FsModel390Columns.ENTERPRISE));
				mod390.setYear(rs.getInt(FsModel390Columns.YEAR));
				mod390.setAdministration(rs.getInt(FsModel390Columns.ADMINISTRATION));
				mod390.setConfidential(rs.getInt(FsModel390Columns.SECURITY_LEVEL)==1);
				mod390.setReplacement(rs.getBoolean(FsModel390Columns.REPLACEMENT));
				mod390.setReceipt(rs.getString(FsModel390Columns.RECEIPT));
				mod390.setReplacedReceipt(rs.getString(FsModel390Columns.REPLACED_RECEIPT));
				mod390.setComments(rs.getString(FsModel390Columns.COMMENTS));
				mod390.setDocument(rs.getString(FsModel390Columns.DOCUMENT));
				mod390.setName(rs.getString(FsModel390Columns.NAME));
				
				String model =  rs.getString(FsModel390Columns.MODEL);
				StringReader reader = new StringReader(model);
				JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);				
				Unmarshaller um = context.createUnmarshaller();
				AEATIVA2013 iva = (AEATIVA2013) um.unmarshal(reader);
				populate(mod390, iva);
			}
			return mod390;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}
	public static String getXMLContentById(int id, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD390_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			String model =  null;
			while (rs.next()) {
				model =  rs.getString(FsModel390Columns.MODEL);
			}
			return model;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static void delete(Connection conn, Mod390 mod390)
			throws AonSQLException {
		PreparedStatement deleteStmt = null;
		PreparedStatement deleteDetailStmt = null;
		try {
			deleteStmt = conn.prepareStatement(MOD390_DELETE);
			SQLUtils.setInt(deleteStmt, 1, mod390.getId());
			deleteStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(deleteDetailStmt);
			SQLUtils.closeQuietly(deleteStmt);
		}
	}


	public static ArrayList<Mod390Detail> getMod390Details(int domain,
			Integer year, Connection conn) throws AonSQLException  {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			Date firstDay = SQLUtils.getYearFirstDay(year);
			Date lastDay = SQLUtils.getYearLastDay(year);

			stmt = conn.prepareStatement(INVOICE_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setDate(2, new java.sql.Date(firstDay.getTime()));
			stmt.setDate(3, new java.sql.Date(lastDay.getTime()));
			
			ArrayList<Mod390Detail> list = initializeList(false);
			Mod390Detail detail = null;
			
			rs = stmt.executeQuery();
			while (rs.next()) {
				InvoiceType invoiceType = InvoiceType.values()[rs.getInt( InvoiceColumns.TYPE )];
				RectificationType rectificationType = RectificationType.values()[rs.getInt( InvoiceColumns.RECTIFICATION_TYPE )];
				boolean service = rs.getBoolean( InvoiceColumns.SERVICE ); 
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt( InvoiceColumns.TRANSACTION )];
				boolean investment = rs.getBoolean( InvoiceColumns.INVESTMENT );
				double percentage = rs.getDouble( InvoiceTaxColumns.PERCENTAGE );
				VatDeductionType vatDeductionType = VatDeductionType.values()[rs.getInt( InvoiceTaxColumns.VAT_DEDUCTION_TYPE )];
				double taxableBase = rs.getDouble( InvoiceDetailColumns.TAXABLE_BASE );
				double quota = rs.getDouble( InvoiceTaxColumns.QUOTA );
				double surchargePercent = rs.getDouble( InvoiceTaxColumns.SURCHARGE );
				boolean surcharge = (surchargePercent > 0); 
				double surchargeQuota = rs.getDouble( InvoiceTaxColumns.SURCHARGE_QUOTA );
				Mod390DetailKey[] keys = getKeys(invoiceType,rectificationType,service,transaction,investment,surcharge,percentage,vatDeductionType);
				if (keys != null) {
					for (Mod390DetailKey key : keys) {
						detail = getDetail(list,key);
						detail.setKey(key);
						detail.setPercent(percentage);
						detail.setQuota( AonUtil.round(detail.getQuota()  + quota));
						detail.setTaxableBase( AonUtil.round( detail.getTaxableBase() + taxableBase));
						list.add(detail);
						if (surcharge && invoiceType == InvoiceType.SALES && transaction == InvoiceTransactionType.NATIONAL) {
							Mod390DetailKey surchargeKey = null;
							if (percentage == 0.5) {
								surchargeKey = Mod390DetailKey.K10_05;
							} else if (percentage == 1) {
								surchargeKey = Mod390DetailKey.K10_1;
							} else if (percentage == 1.4) {
								surchargeKey = Mod390DetailKey.K10_14;
							} else if (percentage == 4) {
								surchargeKey = Mod390DetailKey.K10_4;
							} else if (percentage == 5.2) {
								surchargeKey = Mod390DetailKey.K10_52;
							} else if (percentage == 1.75) {
								surchargeKey = Mod390DetailKey.K10_175;
							}
							if (surchargeKey != null) {
								detail = getDetail(list,surchargeKey);
								detail.setKey(surchargeKey);
								detail.setPercent(surchargePercent);
								detail.setQuota( AonUtil.round(detail.getQuota()  + surchargeQuota));
								detail.setTaxableBase( AonUtil.round( detail.getTaxableBase() + taxableBase));
								list.add(detail);
							}
						}
					}
				}
			}
			return list;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	private static Mod390DetailKey[] getKeys(InvoiceType invoiceType,
			RectificationType rectificationType, boolean service,
			InvoiceTransactionType transaction, boolean investment,
			boolean surcharge, double percentage,
			VatDeductionType vatDeductionType) {

		boolean rectification = (rectificationType == RectificationType.SPECIAL_RECTIFIER);
		Mod390DetailKey[] keys = null;
		if (invoiceType == InvoiceType.SALES) {
			keys = getSalesKeys(invoiceType, rectification, service,
					transaction, investment, surcharge, percentage,
					vatDeductionType);
		} else if (invoiceType == InvoiceType.PURCHASE) {
			keys = getPurchaseKeys(invoiceType, rectification, service,
					transaction, investment, surcharge, percentage,
					vatDeductionType);
		} else if (invoiceType == InvoiceType.EXPENSES) {
			keys = getExpensesKeys(invoiceType, rectification, service,
					transaction, investment, surcharge, percentage,
					vatDeductionType);
		}
		return keys;
	}

	private static Mod390DetailKey[] getSalesKeys(InvoiceType invoiceType,
			boolean  rectification, boolean service,
			InvoiceTransactionType transaction, boolean investment,
			boolean surcharge, double percentage,
			VatDeductionType vatDeductionType) {
		Mod390DetailKey[] keys = null;
		if (transaction == InvoiceTransactionType.NATIONAL) {
			Mod390DetailKey page11Key = surcharge?Mod390DetailKey.B102:Mod390DetailKey.B099;
			if (rectification) {
				return new Mod390DetailKey[]{Mod390DetailKey.K07,page11Key};
			} else {
				if (percentage == 4) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.K00_04,page11Key};
				} else if (percentage == 8) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.K00_08,page11Key};
				} else if (percentage == 10) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.K00_10,page11Key};
				} else if (percentage == 18) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.K00_18,page11Key};
				} else if (percentage == 21) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.K00_21,page11Key};
				}
			}
		} else {
			if (vatDeductionType == VatDeductionType.WITHOUT_RIGHT) {
				keys = new Mod390DetailKey[]{Mod390DetailKey.B105};
			} else {
				if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.B103};
				} else if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.B104};
				} else if (transaction == InvoiceTransactionType.CAN_CEU_MEL) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.B104};
				} else if (transaction == InvoiceTransactionType.OTHER_ISP) {
					keys = new Mod390DetailKey[]{Mod390DetailKey.B110};
				}
			}
		}
		return keys;
	}

	private static Mod390DetailKey[] getPurchaseKeys(InvoiceType invoiceType,
			boolean  rectification, boolean service,
			InvoiceTransactionType transaction, boolean investment,
			boolean surcharge, double percentage,
			VatDeductionType vatDeductionType) {
		if (transaction == InvoiceTransactionType.NATIONAL) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K18_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K14_21};
				}
			}
		}
		if (transaction == InvoiceTransactionType.OTHER_ISP) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_21};
				}
			}
		}
		if (transaction == InvoiceTransactionType.EXTRACOMMUNITY || transaction == InvoiceTransactionType.CAN_CEU_MEL) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K24_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K22_21};
				}
			}
		}
		if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_04,Mod390DetailKey.K28_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K28_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_08,Mod390DetailKey.K28_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_10,Mod390DetailKey.K28_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K28_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_18,Mod390DetailKey.K28_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_21,Mod390DetailKey.K28_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_04,Mod390DetailKey.K26_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K26_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_08,Mod390DetailKey.K26_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_10,Mod390DetailKey.K26_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K26_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_18,Mod390DetailKey.K26_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K04_21,Mod390DetailKey.K26_21};
				}
			}
		}
		return null;
	}

	private static Mod390DetailKey[] getExpensesKeys(InvoiceType invoiceType,
			boolean  rectification, boolean service,
			InvoiceTransactionType transaction, boolean investment,
			boolean surcharge, double percentage,
			VatDeductionType vatDeductionType) {
		if (transaction == InvoiceTransactionType.NATIONAL) {
			if (transaction == InvoiceTransactionType.NATIONAL) {
				if (investment) {
					if (percentage == 4) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_04};
					} else if (percentage == 7) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_07};
					} else if (percentage == 8) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_08};
					} else if (percentage == 10) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_10};
					} else if (percentage == 16) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_16};
					} else if (percentage == 18) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_18};
					} else if (percentage == 21) {
						return new Mod390DetailKey[]{Mod390DetailKey.K18_21};
					}
				} else {
					if (percentage == 4) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_04};
					} else if (percentage == 7) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_07};
					} else if (percentage == 8) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_08};
					} else if (percentage == 10) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_10};
					} else if (percentage == 16) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_16};
					} else if (percentage == 18) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_18};
					} else if (percentage == 21) {
						return new Mod390DetailKey[]{Mod390DetailKey.K14_21};
					}
				}
			}
		} 
		if (transaction == InvoiceTransactionType.CAN_CEU_MEL || transaction == InvoiceTransactionType.OTHER_ISP) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K18_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K14_21};
				}
			}
		}
		if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K24_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K06,Mod390DetailKey.K22_21};
				}
			}
		}
		
		if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
			if (investment) {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_04,Mod390DetailKey.K28_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K28_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_08,Mod390DetailKey.K28_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_10,Mod390DetailKey.K28_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K28_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_18,Mod390DetailKey.K28_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_21,Mod390DetailKey.K28_21};
				}
			} else {
				if (percentage == 4) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_04,Mod390DetailKey.K26_04};
				} else if (percentage == 7) {
					return new Mod390DetailKey[]{Mod390DetailKey.K26_07};
				} else if (percentage == 8) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_08,Mod390DetailKey.K26_08};
				} else if (percentage == 10) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_10,Mod390DetailKey.K26_10};
				} else if (percentage == 16) {
					return new Mod390DetailKey[]{Mod390DetailKey.K26_16};
				} else if (percentage == 18) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_18,Mod390DetailKey.K26_18};
				} else if (percentage == 21) {
					return new Mod390DetailKey[]{Mod390DetailKey.K05_21,Mod390DetailKey.K26_21};
				}
			}
		}
		return null;
	}

	private static ArrayList<Mod390Detail> initializeList(boolean onlyPage5) {
		ArrayList<Mod390Detail> list = new ArrayList<Mod390Detail>();
		Mod390Detail detail = null;
		for (Mod390DetailKey key : Mod390DetailKey.values()) {
			if (!onlyPage5 || (onlyPage5 && key.isPage5Key())) {
				detail = new Mod390Detail();
				detail.setKey(key);
				detail.setPercent(key.getPercent());
				list.add(detail);
			}
		}
		return list;
	}


	private static Mod390Detail getDetail(ArrayList<Mod390Detail> list,
			Mod390DetailKey key) {
		for (Mod390Detail detail : list) {
			if (detail.getKey() == key) {
				return detail;
			}
		}
		throw new IllegalArgumentException("La clave " + key + " no soportada");
	}


	public static Mod303Results getMod303Results(int domain, int year,
			Connection conn)  throws AonSQLException  {
		String VAT_TAX_DECLARATION_SELECT = 
			"SELECT "  
				+SQLConstants.FS_VAT +"." + FsVatColumns.PERIOD +" "+FsVatColumns.PERIOD + ","  
				+SQLConstants.FS_VAT_DECLARATION +"." + FsVatDeclarationColumns.DEPOSIT+" "+FsVatDeclarationColumns.DEPOSIT + ","  
				+SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.PAY_BACK+" "+FsVatDeclarationColumns.PAY_BACK + ","
				+SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.COMPENSATE+" "+FsVatDeclarationColumns.COMPENSATE
				+" FROM " + SQLConstants.FS_VAT
				+ " INNER JOIN " + SQLConstants.FS_VAT_DECLARATION 
				+" ON " +SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.FS_VAT
				+" = " + SQLConstants.FS_VAT+"."+FsVatColumns.ID
				+" WHERE " + SQLConstants.FS_VAT +"."+FsVatColumns.DOMAIN+"=?"
				+" AND " + SQLConstants.FS_VAT +"."+FsVatColumns.YEAR +"=?"
				+" AND "+SQLConstants.FS_VAT_DECLARATION+"."+FsVatDeclarationColumns.ADMINISTRATION+"=" + Administration.COMMON_TERRITORY.ordinal();
				
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(VAT_TAX_DECLARATION_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, year);
			Mod303Results mod303Results = new Mod303Results();
			rs = stmt.executeQuery();
			while (rs.next()) {
				Period period = Period.values()[rs.getInt(FsVatColumns.PERIOD)];
				double deposit = rs.getDouble(FsVatDeclarationColumns.DEPOSIT);
				double payBack = rs.getDouble(FsVatDeclarationColumns.PAY_BACK);
				mod303Results.setDepositSum(AonUtil.round(mod303Results.getDepositSum() + deposit));
				mod303Results.setPaybackSum(AonUtil.round(mod303Results.getPaybackSum() + payBack));
				// Last Period
				if (period == Period.M12 || period == Period.T4) {
					mod303Results.setLastPeriodPaybackResult(payBack);
					mod303Results.setLastPeriodCompensateResult(rs.getDouble(FsVatDeclarationColumns.COMPENSATE));
				}
			}
			return mod303Results;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}
	
	private static AEATIVA2013 getAEATIVA2013(Mod390 mod390) {
		AEATIVA2013 iva = new AEATIVA2013();
		TipoDoc tipoDoc = new TipoDoc();
		tipoDoc.setCodModelo("390");
		tipoDoc.setEjercicio(mod390.getYear());
		iva.setIdDoc(tipoDoc);
		
		DatIdent datIdent = new DatIdent();
		TipoPersonaFisica tp = new TipoPersonaFisica();
		if ( mod390.isLegalEntity() ) {
			TipoPersonaJuridica tpj = new TipoPersonaJuridica();
			TipoIdentificacionPersonaJuridica tipj = new TipoIdentificacionPersonaJuridica();
			tipj.setNIF(toUppercase(mod390.getDocument()));
			tipj.setRazonSocial(toUppercase(mod390.getName()));
			tpj.setIdentPersJuridica(tipj);
			datIdent.setPersJuridica(tpj);
		} else {
			TipoIdentificacionPersonaFisica tipf = new TipoIdentificacionPersonaFisica();
			tipf.setNIF(mod390.getDocument());
			tipf.setNombre(toUppercase(mod390.getName()));
			tipf.setApe1(toUppercase(mod390.getFirstSurname()));
			tipf.setApe2(toUppercase(mod390.getSecondSurname()));;
			tp.setIdent(tipf);
			datIdent.setPersFisica(tp);
		}
		if (AonUtil.isNotEmpty(mod390.getContactPhone())) {
			datIdent.setTelefono(mod390.getContactPhone());
		}
		iva.setDatIdent(datIdent);
		
		iva.setDevengo(getDevengo(mod390));
		iva.setDatEstadisticos(getStatisticalData(mod390));
		iva.setRepresentanteFisica( getRepresentanteFisica(mod390) );	
		iva.getRepresentanteJuridica().addAll( getRepresentanteJuridica(mod390) );
		iva.setRegGeneral(getRegGeneral(mod390));
		// TODO
		// iva.setRegSimplificado(null);
		
		Administraciones adm = getAdministraciones(mod390);
		if (adm == null) {
			iva.setLiqAnual(getLiqAnual(mod390));
		} else {
			iva.setAdministraciones(adm);
		}
		iva.setResLiquidaciones(getResLiquidaciones(mod390));
		iva.setVolOperaciones(getVolOperaciones(mod390));
		iva.setOpEspecificas(getOpEspecificas(mod390));
		
		// TODO
		// iva.prorratas
		// TODO
		// iva.ivaDeducibleGrupo1
		// TODO
		// iva.ivaDeducibleGrupo2
		// TODO
		// iva.ivaDeducibleGrupo3
		
		return iva;
	}

	private static String toUppercase(String data) {
		return data==null?null:data.toUpperCase();
	}


	private static OpEspecificas getOpEspecificas(Mod390 mod390) {
		OpEspecificas op = new OpEspecificas();
		if (mod390.getBox230()>0) {
			op.setAdqInterioresExentas(ensureBigDecimal(mod390.getBox230()));
		}
		if (mod390.getBox109()>0) {
			op.setAdqIntracomunitariasExentas(ensureBigDecimal(mod390.getBox109()));
		}
		if (mod390.getBox231()>0) {
			op.setImportacionesExentas(ensureBigDecimal(mod390.getBox231()));
		}
		if (mod390.getBox232()>0) {
			op.setBasesIVASoportadoNoDeducible(ensureBigDecimal(mod390.getBox232()));
		}
		if (mod390.getBox111()>0) {
			op.setOpSujetas(ensureBigDecimal(mod390.getBox111()));
		}
		if (mod390.getBox113()>0) {
			op.setEntregasInteriores(ensureBigDecimal(mod390.getBox113()));
		}
		if (mod390.getBox523()>0) {
			op.setServInversionSP(ensureBigDecimal(mod390.getBox523()));
		}
		return op;
	}


	private static VolOperaciones getVolOperaciones(Mod390 mod390) {
		VolOperaciones vol = new VolOperaciones();
		if (mod390.getBox99()>0) {
			vol.setOpRegGeneral(ensureBigDecimal(mod390.getBox99()));
		}
		if (mod390.getBox103()>0) {
			vol.setEntregasIntracomunitariasExentas(ensureBigDecimal(mod390.getBox103()));
		}
		if (mod390.getBox104()>0) {
			vol.setExportacionesExentasConDrchoDeduccion(ensureBigDecimal(mod390.getBox104()));
		}
		if (mod390.getBox105()>0) {
			vol.setOpExentasSinDrchoDeduccion(ensureBigDecimal(mod390.getBox105()));
		}
		if (mod390.getBox110()>0) {
			vol.setOpNoSujetas(ensureBigDecimal(mod390.getBox110()));
		}
		if (mod390.getBox112()>0) {
			vol.setEntregasBienesInstalacionOtrosEM(ensureBigDecimal(mod390.getBox112()));
		}
		if (mod390.getBox100()>0) {
			vol.setOpRegSimplificado(ensureBigDecimal(mod390.getBox100()));
		}
		if (mod390.getBox101()>0) {
			vol.setOpRegEspAgricPescGanad(ensureBigDecimal(mod390.getBox101()));
		}
		if (mod390.getBox102()>0) {
			vol.setOpRegEspRecEquivalencia(ensureBigDecimal(mod390.getBox102()));
		}
		if (mod390.getBox227()>0) {
			vol.setOpRegEspBienesUsados(ensureBigDecimal(mod390.getBox227()));
		}
		if (mod390.getBox228()>0) {
			vol.setOpRegEspAgViajes(ensureBigDecimal(mod390.getBox228()));
		}
		if (mod390.getBox106()>0) {
			vol.setEntregasBienesInmuebles(ensureBigDecimal(mod390.getBox106()));
		}
		if (mod390.getBox107()>0) {
			vol.setEntregasBienesInversion(ensureBigDecimal(mod390.getBox107()));
		}
		vol.setTotalVolOp(ensureBigDecimal(mod390.getBox108()));
		return vol;
	}


	private static ResLiquidaciones getResLiquidaciones(Mod390 mod390) {
		ResLiquidaciones res = new ResLiquidaciones();
        
		PerNoRegGrupos perNoRegGrupos = new PerNoRegGrupos();
		if (mod390.getBox95() != 0) {
			perNoRegGrupos.setTotIngresosIVA(ensureBigDecimal(mod390.getBox95()));
		}
		if (mod390.getBox96() != 0) {
			perNoRegGrupos.setTotDevIVASPRegDevMensual(ensureBigDecimal(mod390.getBox96()));
		}
		// ???????????????
        // AEATIVA2013 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja exclusionBaja;
		if (mod390.getBox524() != 0) {
			perNoRegGrupos.setTotDevAdqElemTrans(ensureBigDecimal(mod390.getBox524()));
		}
		if (mod390.getBox97() != 0) {
			perNoRegGrupos.setImporteACompensarUltimoPeriodo(ensureBigDecimal(mod390.getBox97()));
		}
		if (mod390.getBox98() != 0) {
			perNoRegGrupos.setImporteADevolverUltimoPeriodo(ensureBigDecimal(mod390.getBox98()));
		}
        res.setPerNoRegGrupos(perNoRegGrupos);
        
        PerSiRegGrupos perSiRegGrupos = new PerSiRegGrupos();
		if (mod390.getBox525() != 0) {
			perSiRegGrupos.setTotResulPositivos322(ensureBigDecimal(mod390.getBox525()));
		}
		if (mod390.getBox526() != 0) {
			perSiRegGrupos.setTotResulNegativos322(ensureBigDecimal(mod390.getBox526()));
		}
        res.setPerSiRegGrupos(perSiRegGrupos);
		return res;
	}


	private static Administraciones getAdministraciones(Mod390 mod390) {
		if (mod390.getBox87() > 0.0 && mod390.getBox87() < 100.0) {
			Administraciones adm = new Administraciones();
			adm.setComun(ensureBigDecimal(mod390.getBox87()));
			if (mod390.getBox88()>0) {
				adm.setArabaAlava(ensureBigDecimal(mod390.getBox88()));
			}
			if (mod390.getBox89()>0) {
				adm.setGipuzkoa(ensureBigDecimal(mod390.getBox89()));
			}
			if (mod390.getBox90()>0) {
				adm.setBizkaia(ensureBigDecimal(mod390.getBox90()));
			}
			if (mod390.getBox91()>0) {
				adm.setNavarra(ensureBigDecimal(mod390.getBox91()));
			}
			adm.setSumResultados(ensureBigDecimal(mod390.getBox84()));
			adm.setResTerrComun(ensureBigDecimal(mod390.getBox92()));
			adm.setComCuotasEjercicioAnteriorTerrComun(ensureBigDecimal(mod390.getBox93()));
			adm.setResLiqAnualTerrComun(ensureBigDecimal(mod390.getBox94()));
			return adm;
		}
		return null;
	}


	private static LiqAnual getLiqAnual(Mod390 mod390) {
		LiqAnual liq = new LiqAnual();
		liq.setSumResultados(ensureBigDecimal(mod390.getBox84()) );
		if (mod390.getBox85() > 0) {
			liq.setCompCuotasEjercicioAnterior(ensureBigDecimal(mod390.getBox85()));
		}
		liq.setResLiquidacion(ensureBigDecimal(mod390.getBox86()));
        return liq;
	}


	private static RegGeneral getRegGeneral(Mod390 mod390) {
		RegGeneral regGeneral = new RegGeneral();
		BaseImponibleyCuota bases = getBaseImponibleyCuota(mod390);
		regGeneral.setBaseImponibleyCuota(bases);
		Deducciones ded = getDeducciones(mod390); 
		regGeneral.setDeducciones(ded);
		return regGeneral;
	}


	private static Deducciones getDeducciones(Mod390 mod390) {
		Deducciones deducciones = new Deducciones();
		deducciones.setOpInterioresBienesServiciosCorrientes(getOpInterioresBienesServiciosCorrientes(mod390));
		deducciones.setOpIntragrupoCorrientes(getOpIntragrupoCorrientes(mod390));
		deducciones.setOpInterioresBienesInversion(getOpInterioresBienesInversion(mod390));
		deducciones.setOpIntragrupoBienesInversion(getOpIntragrupoBienesInversion(mod390));
		deducciones.setImportacionesBienesCorrientes(getImportacionesBienesCorrientes(mod390));
		deducciones.setImportacionesBienesInversion(getImportacionesBienesInversion(mod390));
		deducciones.setAdqIntracomunitariasBienesCorrientes(getAdqIntracomunitariasBienesCorrientes(mod390));
		deducciones.setAdqIntracomunitariasBienesInversion(getAdqIntracomunitariasBienesInversion(mod390));
		deducciones.setAdqIntracomunitariasServicios(getAdqIntracomunitariasServicios(mod390));
		deducciones.setComRegAgricGanadPesca(getComRegAgricGanadPesca(mod390));
		deducciones.setRectifDeducciones(getRectifDeducciones(mod390));
		deducciones.setRegularizInversiones(getRegularizInversiones(mod390));
		deducciones.setRegularizPorcProrrata(getRegularizPorcProrrata(mod390));
		deducciones.setSumDeducciones(getSumDeducciones(mod390));
		return deducciones;
	}


	private static BigDecimal getSumDeducciones(Mod390 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K36);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizPorcProrrata(Mod390 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K35);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizInversiones(Mod390 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K34);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static RectifDeducciones getRectifDeducciones(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K33));
		if (tipo != null) {
			RectifDeducciones op = new RectifDeducciones();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}


	private static ComRegAgricGanadPesca getComRegAgricGanadPesca(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K32));
		if (tipo != null) {
			ComRegAgricGanadPesca op = new ComRegAgricGanadPesca();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}


	private static AdqIntracomunitariasServicios getAdqIntracomunitariasServicios(Mod390 mod390) {
		AdqIntracomunitariasServicios op = new AdqIntracomunitariasServicios();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K31)));
		return op;
	}


	private static AdqIntracomunitariasBienesInversion getAdqIntracomunitariasBienesInversion(Mod390 mod390) {
		AdqIntracomunitariasBienesInversion op = new AdqIntracomunitariasBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K29)));
		return op;
	}


	private static AdqIntracomunitariasBienesCorrientes getAdqIntracomunitariasBienesCorrientes(Mod390 mod390) {
		AdqIntracomunitariasBienesCorrientes op = new AdqIntracomunitariasBienesCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K27)));
		return op;
	}


	private static ImportacionesBienesInversion getImportacionesBienesInversion(Mod390 mod390) {
		ImportacionesBienesInversion op = new ImportacionesBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K25)));
		return op;
	}


	private static ImportacionesBienesCorrientes getImportacionesBienesCorrientes(Mod390 mod390) {
		ImportacionesBienesCorrientes op = new ImportacionesBienesCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K23)));
		return op;
	}


	private static OpIntragrupoBienesInversion getOpIntragrupoBienesInversion(Mod390 mod390) {
		OpIntragrupoBienesInversion op = new OpIntragrupoBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K21)));
		return op;
	}


	private static OpInterioresBienesInversion getOpInterioresBienesInversion(Mod390 mod390) {
		OpInterioresBienesInversion op = new OpInterioresBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K19)));
		return op;
	}


	private static OpIntragrupoCorrientes getOpIntragrupoCorrientes(Mod390 mod390) {
		OpIntragrupoCorrientes op = new OpIntragrupoCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K17)));
		return op;
	}


	private static OpInterioresBienesServiciosCorrientes getOpInterioresBienesServiciosCorrientes(Mod390 mod390) {
		OpInterioresBienesServiciosCorrientes op = new OpInterioresBienesServiciosCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K15)));
		return op;
	}


	private static BaseImponibleyCuota getBaseImponibleyCuota(Mod390 mod390) {
		BaseImponibleyCuota b = new BaseImponibleyCuota();
		b.setRegOrdinario(getRegOrdinario(mod390));
		b.setOpIntragrupo(getOpIntragrupo(mod390));
		b.setRegBienesUsados(getRegBienesUsados(mod390));
		b.setRegAgViajes(getRegAgViajes(mod390));
		b.setAdqIntracomBienes(getAdqIntracomBienes(mod390));
		b.setAdqIntracomServicios(getAdqIntracomServicios(mod390));
		b.setIVAdevengadoInversionSP(getIVAdevengadoInversionSP(mod390));
		b.setModBasesyCuotas(getModBasesyCuotas(mod390));
		b.setModBasesyCuotasConcursoAcreedores(getModBasesyCuotasConcursoAcreedores(mod390));
		b.setTotalBasesyCuotasIVA(getTotalBasesyCuotasIVA(mod390));
		b.setRecargoEquivalencia(getRecargoEquivalencia(mod390));
		b.setModRecargoEquivalencia(getModRecargoEquivalencia(mod390));
		b.setModRecargoEquivalenciaConcursoAcreedores(getModRecargoEquivalenciaConcursoAcreedores(mod390));
		b.setTotalCuotasIVA(getTotalCuotasIVA(mod390));
		return b;
	}


	private static BigDecimal getTotalCuotasIVA(Mod390 mod390) {
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K13);
		BigDecimal totalCuotasIVA = null; 
		if (detail != null) {
			totalCuotasIVA = ensureBigDecimal(detail.getQuota()); 			
		}
		return totalCuotasIVA;
	}


	private static ModRecargoEquivalenciaConcursoAcreedores getModRecargoEquivalenciaConcursoAcreedores(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K12));
		if (tipo != null) {
			ModRecargoEquivalenciaConcursoAcreedores modRecargoEquivalenciaConcursoAcreedores = new ModRecargoEquivalenciaConcursoAcreedores();
			modRecargoEquivalenciaConcursoAcreedores.setTipoX(tipo);
			return modRecargoEquivalenciaConcursoAcreedores;
		}
		return null;
	}


	private static ModRecargoEquivalencia getModRecargoEquivalencia(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K11));
		if (tipo != null) {
			ModRecargoEquivalencia modRecargoEquivalencia = new ModRecargoEquivalencia();
			modRecargoEquivalencia.setTipoX(tipo);
			return modRecargoEquivalencia;
		}
		return null;
	}


	private static RecargoEquivalencia getRecargoEquivalencia(Mod390 mod390) {
		RecargoEquivalencia recargoEquivalencia = new RecargoEquivalencia();
		recargoEquivalencia.setTipo05(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_05)));
		recargoEquivalencia.setTipo1(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_1)));
		recargoEquivalencia.setTipo14(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_14)));
		recargoEquivalencia.setTipo175(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_175)));
		recargoEquivalencia.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_4)));
		recargoEquivalencia.setTipo52(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_52)));
		return recargoEquivalencia;
	}


	private static TotalBasesyCuotasIVA getTotalBasesyCuotasIVA(Mod390 mod390) {
		TotalBasesyCuotasIVA totalBasesyCuotasIVA = new TotalBasesyCuotasIVA();
		totalBasesyCuotasIVA.setTipoX(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K09)));
		return totalBasesyCuotasIVA;
	}


	private static ModBasesyCuotasConcursoAcreedores getModBasesyCuotasConcursoAcreedores(
			Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K08));
		if (tipo != null) {
			ModBasesyCuotasConcursoAcreedores modBases = new ModBasesyCuotasConcursoAcreedores();
			modBases.setTipoX(tipo);
			return modBases;
		}
		return null;
	}


	private static ModBasesyCuotas getModBasesyCuotas(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K07));
		if (tipo != null) {
			ModBasesyCuotas modBasesyCuotas = new ModBasesyCuotas();
			modBasesyCuotas.setTipoX(tipo);
			return modBasesyCuotas;
		}
		return null;
	}


	private static IVAdevengadoInversionSP getIVAdevengadoInversionSP(Mod390 mod390) {
		IVAdevengadoInversionSP iVAdevengadoInversionSP = new IVAdevengadoInversionSP();
		iVAdevengadoInversionSP.setTipoX(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K06)));
		return iVAdevengadoInversionSP;
	}


	private static AdqIntracomServicios getAdqIntracomServicios(Mod390 mod390) {
		AdqIntracomServicios adqIntracomServicios = new AdqIntracomServicios();
		adqIntracomServicios.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_04)));
		adqIntracomServicios.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_08)));
		adqIntracomServicios.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_10)));
		adqIntracomServicios.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_18)));
		adqIntracomServicios.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_21)));
		return adqIntracomServicios;
	}


	private static AdqIntracomBienes getAdqIntracomBienes(Mod390 mod390) {
		AdqIntracomBienes adqIntracomBienes = new AdqIntracomBienes();
		adqIntracomBienes.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_04)));
		adqIntracomBienes.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_08)));
		adqIntracomBienes.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_10)));
		adqIntracomBienes.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_18)));
		adqIntracomBienes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_21)));
		return adqIntracomBienes;
	}


	private static RegAgViajes getRegAgViajes(Mod390 mod390) {
		RegAgViajes regAgViajes = new RegAgViajes();
		regAgViajes.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K03_18)));
		regAgViajes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K03_21)));
		return regAgViajes;
	}


	private static RegBienesUsados getRegBienesUsados(Mod390 mod390) {
		RegBienesUsados regBienesUsados = new RegBienesUsados();
		regBienesUsados.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_04)));
		regBienesUsados.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_08)));
		regBienesUsados.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_10)));
		regBienesUsados.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_18)));
		regBienesUsados.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_21)));
		return regBienesUsados;
	}


	private static OpIntragrupo getOpIntragrupo(Mod390 mod390) {
		OpIntragrupo opIntragrupo = new OpIntragrupo();
		opIntragrupo.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_04)));
		opIntragrupo.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_08)));
		opIntragrupo.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_10)));
		opIntragrupo.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_18)));
		opIntragrupo.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_21)));
		return opIntragrupo;
	}


	private static RegOrdinario getRegOrdinario(Mod390 mod390) {
		RegOrdinario regOrdinario = new RegOrdinario();
		regOrdinario.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_04)));
		regOrdinario.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_08)));
		regOrdinario.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_10)));
		regOrdinario.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_18)));
		regOrdinario.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_21)));
		return regOrdinario;
	}


	private static Mod390Detail getKey(Mod390 mod390, Mod390DetailKey key) {
		if (mod390 != null && mod390.getGeneralRegime() != null) {
			return mod390.getGeneralRegime().get(key);
		}
		return null;
	}


	private static TipoBaseImponibleYCuota getTipoBaseImponibleYCuota(Mod390Detail detail) {
		TipoBaseImponibleYCuota tipo = new TipoBaseImponibleYCuota();
		if (detail!= null && (detail.getTaxableBase() != 0 || detail.getQuota() != 0)) {
			tipo.setBI(ensureBigDecimal(detail.getTaxableBase()));
			tipo.setCuota(ensureBigDecimal(detail.getQuota()));
		} else {
			tipo.setBI(ensureBigDecimal(0));
			tipo.setCuota(ensureBigDecimal(0));
		}
		return tipo;
	}


	private static Devengo getDevengo(Mod390 mod390) {
		Devengo devengo = new Devengo();
		devengo.setEjercicio(mod390.getYear());
		if (mod390.isInsolvencyDeclarations()) {
			devengo.setConcursoUltPerSI( new ConcursoUltPerSI());	
		} else {
			devengo.setConcursoUltPerNO( new ConcursoUltPerNO());
		}
		if (mod390.isTaxRefund()) {
			devengo.setRegDevMensual(new RegDevMensual());
		}
		if (mod390.isReplacement()) {
			devengo.setDecSustitutiva( new DecSustitutiva() );
			devengo.setJustDecAnterior( mod390.getReplacedReceipt() );
		}
		if (mod390.isSpecialGroupRegime()) {
			TipoGrupoEntidades tge = new TipoGrupoEntidades();
			tge.setNumGrupo(mod390.getGroupNumber());
			if (mod390.isGroupDependent()) {
				tge.setDependiente(new Dependiente());	
			} else {
				tge.setDominante(new Dominante());
			}
			if (mod390.isGroupRegimeType()) {
				tge.setArt65SI( new Art65SI());
				tge.setNIFEntidadDominante(mod390.getGroupDocument());
			} else {
				tge.setArt65NO( new Art65NO());
			}
			if (mod390.isGroupDeclarations()) {
				tge.setUltAutoliquidSI(new UltAutoliquidSI());
			} else {
				tge.setUltAutoliquidNO(new UltAutoliquidNO());
			}
			devengo.setRegGrupoEntidades(tge);
		}
		return devengo;
	}
	
	private static DatEstadisticos getStatisticalData(Mod390 mod390) {
		DatEstadisticos datEstadisticos = new DatEstadisticos();
		
		if (mod390.getMainActivity() != null) {
			Pral pral = new Pral();
			pral.setClave(mod390.getMainActivity().getKey());
			pral.setDescripcion(mod390.getMainActivity().getDescription());
			pral.setEpigrafe(mod390.getMainActivity().getEpigraph());
			datEstadisticos.setPral(pral);
		}
		Activity[] activities = new Activity[]{
				mod390.getActivity1(),
				mod390.getActivity2(),
				mod390.getActivity3(),
				mod390.getActivity4(),
				mod390.getActivity5()
		};
		for (Activity activity : activities) {
			if (activity != null) {
				Otras otras = new Otras();
				otras.setClave(activity.getKey());
				otras.setDescripcion(activity.getDescription());
				otras.setEpigrafe(activity.getEpigraph());
				datEstadisticos.getOtras().add(otras);
			}
		}
		if (mod390.isMod347()) {
			datEstadisticos.setOpTercerasPax(new OpTercerasPax());
		}
		if (!AonUtil.isEmpty(mod390.getMergedDeclarationDocument())) {
			Conjunta conjunta = new Conjunta();
			conjunta.setNIF(mod390.getMergedDeclarationDocument());
			conjunta.setRazonSocial(mod390.getMergedDeclarationName());
			datEstadisticos.setConjunta(conjunta);
		}
		return datEstadisticos;
	}

	private static TipoRepresentanteFisica getRepresentanteFisica(Mod390 mod390) {
		Address address = mod390.getAddress();
		TipoRepresentanteFisica trf = null;
		if (address != null) {
			trf = new TipoRepresentanteFisica();
			TipoIdentificacionPersonaJuridica tipf = new TipoIdentificacionPersonaJuridica();
			tipf.setNIF(address.getRdocument());
			tipf.setRazonSocial(toUppercase(address.getRname()));
			trf.setIdent(tipf);
			TipoDomicilio domicilio = new TipoDomicilio();
			boolean something = false;
			if (AonUtil.isNotEmpty(address.getRstreetName())) {
				domicilio.setViaPublica(toUppercase(address.getRstreetName()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetType())) { 
				domicilio.setSG(toUppercase(address.getRstreetType()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetNumber())) {
				domicilio.setNum(toUppercase(address.getRstreetNumber()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetStair())) {
				domicilio.setEsc(toUppercase(address.getRstreetStair()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetFloor())) {
				domicilio.setPiso(toUppercase(address.getRstreetFloor()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetDoor())) {
				domicilio.setPuerta(toUppercase(address.getRstreetDoor()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRphone())) {
				domicilio.setTelefono(toUppercase(address.getRphone()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRzip())) {
				domicilio.setCPostal(toUppercase(address.getRzip()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRtown())) {
				domicilio.setMunicipio(toUppercase(address.getRtown()));
				something = true;
			}
			if (address.getRprovince() != 0) {
				domicilio.setCodProv(Integer.toString(address.getRprovince()));
				something = true;
			}
			if (something) {
				trf.setDomicilio(domicilio);
				something = true;
			}
		}
		return trf;
	}
	
	private static ArrayList<TipoRepresentanteJuridica> getRepresentanteJuridica(Mod390 mod390) {
		ArrayList<TipoRepresentanteJuridica> list = new ArrayList<TipoRepresentanteJuridica>();
		LegalRepresentative[] lrs = new LegalRepresentative[] {
			mod390.getLegalRepr1(),	
			mod390.getLegalRepr2(),
			mod390.getLegalRepr3()
		};
		for (LegalRepresentative lr : lrs) {
			if (lr != null) {
				TipoRepresentanteJuridica trj = new TipoRepresentanteJuridica();
				trj.setNIF(toUppercase(lr.getDocument()));
				trj.setNombre(toUppercase(lr.getName()));
				trj.setNotaria(toUppercase(lr.getNotary()));
				trj.setFechaPoder(lr.getNotaryDate());
				list.add(trj);
			}
		}
		return list;
	}
	

	private static void populate(Mod390 mod390, AEATIVA2013 iva) {
		DatIdent datIdent = iva.getDatIdent();
		if ( !mod390.isLegalEntity() ) {
			TipoPersonaFisica tpf = datIdent.getPersFisica();
			TipoIdentificacionPersonaFisica tipf = tpf.getIdent();
			mod390.setFirstSurname(tipf.getApe1());
			mod390.setSecondSurname(tipf.getApe2());
		}
		mod390.setContactPhone(datIdent.getTelefono());
		
		Devengo devengo = iva.getDevengo();
		mod390.setInsolvencyDeclarations((devengo.getConcursoUltPerSI() != null));
		mod390.setTaxRefund(devengo.getRegDevMensual() != null);
		TipoGrupoEntidades tge = devengo.getRegGrupoEntidades();
		if (tge != null) {
			mod390.setSpecialGroupRegime(true);
			mod390.setGroupNumber(tge.getNumGrupo());
			mod390.setGroupDependent( tge.getDependiente() != null );
			mod390.setGroupDependent( !(tge.getDominante() != null) );
			if (tge.getArt65SI() != null) {
				mod390.setGroupRegimeType( true );
				mod390.setGroupDocument(tge.getNIFEntidadDominante());
			} else {
				mod390.setGroupRegimeType( false );
			}
			mod390.setGroupDeclarations( tge.getUltAutoliquidSI() != null );
		}
		DatEstadisticos dat = iva.getDatEstadisticos();
		if (dat.getPral() != null) {
			Activity activity = new Activity();
			activity.setKey(dat.getPral().getClave());
			activity.setDescription(dat.getPral().getDescripcion());
			activity.setEpigraph(dat.getPral().getEpigrafe());
			mod390.setMainActivity(activity);
		}
		int i = 1;
		for (Otras otras : dat.getOtras() ) {
			if (otras != null) {
				Activity activity = new Activity();
				activity.setKey(otras.getClave());
				activity.setDescription(otras.getDescripcion());
				activity.setEpigraph(otras.getEpigrafe());
				if (i==1) {
					mod390.setActivity1(activity);
				} else if (i==2) { 
					mod390.setActivity2(activity);
				} else if (i==3) { 
					mod390.setActivity3(activity);
				} else if (i==4) { 
					mod390.setActivity4(activity);
				} else if (i==5) {
					mod390.setActivity5(activity);
				}
			}
			++i;
		}
		mod390.setMod347(dat.getOpTercerasPax() != null);
		if (dat.getConjunta() != null) {
			mod390.setMergedDeclarationDocument(dat.getConjunta().getNIF());
			mod390.setMergedDeclarationName(dat.getConjunta().getRazonSocial());
		}
		if (iva.getRepresentanteFisica() != null) {
			TipoRepresentanteFisica trf = iva.getRepresentanteFisica();
			TipoIdentificacionPersonaJuridica tipj = trf.getIdent();
			Address adr = new Address();
			if (tipj != null) {
				adr.setRdocument(tipj.getNIF());
				adr.setRname(tipj.getRazonSocial());
			}
			TipoDomicilio dom = trf.getDomicilio();
			if (dom != null) {
				adr.setRstreetType(dom.getSG());
				adr.setRstreetName(dom.getViaPublica());
				adr.setRstreetNumber(dom.getNum());
				adr.setRstreetStair(dom.getEsc());
				adr.setRstreetFloor(dom.getPiso());
				adr.setRstreetDoor(dom.getPuerta());
				adr.setRphone(dom.getTelefono());
				adr.setRtown(dom.getMunicipio());
				try {
					adr.setRprovince(Integer.parseInt(dom.getCodProv()));
				} catch (NumberFormatException e) {
					// Nothing.
				}
				adr.setRzip(dom.getCPostal());
			}
			mod390.setAddress(adr);
		}
		List<TipoRepresentanteJuridica> list = iva.getRepresentanteJuridica();
		i = 1;
		if (list != null && list.size() > 0) {
			for (TipoRepresentanteJuridica trj : list){
				LegalRepresentative lg = new  LegalRepresentative();
				lg.setDocument(trj.getNIF());
				lg.setName(trj.getNombre());
				lg.setNotary(trj.getNotaria());
				lg.setNotaryDate(trj.getFechaPoder());
				if (i == 1) {
					mod390.setLegalRepr1(lg);
				} else if (i == 2) {
					mod390.setLegalRepr2(lg);
				} else if (i == 3) {
					mod390.setLegalRepr3(lg);
				}
				i++;
			}
		}
		
		
		Map<Mod390DetailKey, Mod390Detail> map = new TreeMap<Mod390DetailKey, Mod390Detail>();
		ArrayList<Mod390Detail> mapList = initializeList(true);
		for (Mod390Detail detail : mapList) {
			map.put(detail.getKey(), detail);
		}
		mod390.setGeneralRegime(map);

		if (iva.getRegGeneral() != null) {
			BaseImponibleyCuota b = iva.getRegGeneral().getBaseImponibleyCuota();
			if (b != null) {
				if (b.getRegOrdinario() != null) {
					put(mod390,Mod390DetailKey.K00_04,b.getRegOrdinario().getTipo4());
					put(mod390,Mod390DetailKey.K00_08,b.getRegOrdinario().getTipo8());
					put(mod390,Mod390DetailKey.K00_10,b.getRegOrdinario().getTipo10());
					put(mod390,Mod390DetailKey.K00_18,b.getRegOrdinario().getTipo18());
					put(mod390,Mod390DetailKey.K00_21,b.getRegOrdinario().getTipo21());
				}
				if (b.getOpIntragrupo() != null) {
					put(mod390,Mod390DetailKey.K01_04,b.getOpIntragrupo().getTipo4());
					put(mod390,Mod390DetailKey.K01_08,b.getOpIntragrupo().getTipo8());
					put(mod390,Mod390DetailKey.K01_10,b.getOpIntragrupo().getTipo10());
					put(mod390,Mod390DetailKey.K01_18,b.getOpIntragrupo().getTipo18());
					put(mod390,Mod390DetailKey.K01_21,b.getOpIntragrupo().getTipo21());
				}
				if (b.getRegBienesUsados() != null) {
					put(mod390,Mod390DetailKey.K02_04,b.getRegBienesUsados().getTipo4());
					put(mod390,Mod390DetailKey.K02_08,b.getRegBienesUsados().getTipo8());
					put(mod390,Mod390DetailKey.K02_10,b.getRegBienesUsados().getTipo10());
					put(mod390,Mod390DetailKey.K02_18,b.getRegBienesUsados().getTipo18());
					put(mod390,Mod390DetailKey.K02_21,b.getRegBienesUsados().getTipo21());
				}
				if (b.getRegAgViajes() != null) {
					put(mod390,Mod390DetailKey.K03_18,b.getRegAgViajes().getTipo18());
					put(mod390,Mod390DetailKey.K03_21,b.getRegAgViajes().getTipo21());
				}
				if (b.getAdqIntracomBienes() != null) {
					put(mod390,Mod390DetailKey.K04_04,b.getAdqIntracomBienes().getTipo4());
					put(mod390,Mod390DetailKey.K04_08,b.getAdqIntracomBienes().getTipo8());
					put(mod390,Mod390DetailKey.K04_10,b.getAdqIntracomBienes().getTipo10());
					put(mod390,Mod390DetailKey.K04_18,b.getAdqIntracomBienes().getTipo18());
					put(mod390,Mod390DetailKey.K04_21,b.getAdqIntracomBienes().getTipo21());
				}
				if (b.getAdqIntracomServicios() != null) {
					put(mod390,Mod390DetailKey.K05_04,b.getAdqIntracomServicios().getTipo4());
					put(mod390,Mod390DetailKey.K05_08,b.getAdqIntracomServicios().getTipo8());
					put(mod390,Mod390DetailKey.K05_10,b.getAdqIntracomServicios().getTipo10());
					put(mod390,Mod390DetailKey.K05_18,b.getAdqIntracomServicios().getTipo18());
					put(mod390,Mod390DetailKey.K05_21,b.getAdqIntracomServicios().getTipo21());
				}
				if (b.getIVAdevengadoInversionSP() != null) {
					put(mod390,Mod390DetailKey.K06,b.getIVAdevengadoInversionSP().getTipoX());
				}
				if (b.getModBasesyCuotas() != null) {
					put(mod390,Mod390DetailKey.K07,b.getModBasesyCuotas().getTipoX());
				}
				if (b.getModBasesyCuotasConcursoAcreedores() != null) {
					put(mod390,Mod390DetailKey.K08,b.getModBasesyCuotasConcursoAcreedores().getTipoX());
				}
				if (b.getTotalBasesyCuotasIVA() != null) {
					put(mod390,Mod390DetailKey.K09,b.getTotalBasesyCuotasIVA().getTipoX());
				}
				if (b.getRecargoEquivalencia() != null) {
					put(mod390,Mod390DetailKey.K10_05,b.getRecargoEquivalencia().getTipo05());
					put(mod390,Mod390DetailKey.K10_1,b.getRecargoEquivalencia().getTipo1());
					put(mod390,Mod390DetailKey.K10_14,b.getRecargoEquivalencia().getTipo14());
					put(mod390,Mod390DetailKey.K10_175,b.getRecargoEquivalencia().getTipo175());
					put(mod390,Mod390DetailKey.K10_4,b.getRecargoEquivalencia().getTipo4());
					put(mod390,Mod390DetailKey.K10_52,b.getRecargoEquivalencia().getTipo52());
				}
				if (b.getModRecargoEquivalencia() != null) {
					put(mod390,Mod390DetailKey.K11,b.getModRecargoEquivalencia().getTipoX());
				}
				if (b.getModRecargoEquivalenciaConcursoAcreedores() != null) {
					put(mod390,Mod390DetailKey.K12,b.getModRecargoEquivalenciaConcursoAcreedores().getTipoX());
				}
				if (b.getTotalCuotasIVA() != null) {
					put(mod390,Mod390DetailKey.K13,b.getTotalCuotasIVA());
				}
			}
			if (iva.getRegGeneral().getDeducciones() != null) {
				Deducciones d = iva.getRegGeneral().getDeducciones();
				if (d.getOpInterioresBienesServiciosCorrientes() != null) {
					put(mod390,Mod390DetailKey.K14_04,d.getOpInterioresBienesServiciosCorrientes().getTipo4());
					put(mod390,Mod390DetailKey.K14_07,d.getOpInterioresBienesServiciosCorrientes().getTipo7());
					put(mod390,Mod390DetailKey.K14_08,d.getOpInterioresBienesServiciosCorrientes().getTipo8());
					put(mod390,Mod390DetailKey.K14_10,d.getOpInterioresBienesServiciosCorrientes().getTipo10());
					put(mod390,Mod390DetailKey.K14_16,d.getOpInterioresBienesServiciosCorrientes().getTipo16());
					put(mod390,Mod390DetailKey.K14_18,d.getOpInterioresBienesServiciosCorrientes().getTipo18());
					put(mod390,Mod390DetailKey.K14_21,d.getOpInterioresBienesServiciosCorrientes().getTipo21());
				}
				if (d.getOpIntragrupoCorrientes() != null) {
					put(mod390,Mod390DetailKey.K16_04,d.getOpIntragrupoCorrientes().getTipo4());
					put(mod390,Mod390DetailKey.K16_07,d.getOpIntragrupoCorrientes().getTipo7());
					put(mod390,Mod390DetailKey.K16_08,d.getOpIntragrupoCorrientes().getTipo8());
					put(mod390,Mod390DetailKey.K16_10,d.getOpIntragrupoCorrientes().getTipo10());
					put(mod390,Mod390DetailKey.K16_16,d.getOpIntragrupoCorrientes().getTipo16());
					put(mod390,Mod390DetailKey.K16_18,d.getOpIntragrupoCorrientes().getTipo18());
					put(mod390,Mod390DetailKey.K16_21,d.getOpIntragrupoCorrientes().getTipo21());
				}
				if (d.getOpInterioresBienesInversion() != null) {
					put(mod390,Mod390DetailKey.K18_04,d.getOpInterioresBienesInversion().getTipo4());
					put(mod390,Mod390DetailKey.K18_07,d.getOpInterioresBienesInversion().getTipo7());
					put(mod390,Mod390DetailKey.K18_08,d.getOpInterioresBienesInversion().getTipo8());
					put(mod390,Mod390DetailKey.K18_10,d.getOpInterioresBienesInversion().getTipo10());
					put(mod390,Mod390DetailKey.K18_16,d.getOpInterioresBienesInversion().getTipo16());
					put(mod390,Mod390DetailKey.K18_18,d.getOpInterioresBienesInversion().getTipo18());
					put(mod390,Mod390DetailKey.K18_21,d.getOpInterioresBienesInversion().getTipo21());
				}
				if (d.getOpIntragrupoBienesInversion() != null) {
					put(mod390,Mod390DetailKey.K20_04,d.getOpIntragrupoBienesInversion().getTipo4());
					put(mod390,Mod390DetailKey.K20_07,d.getOpIntragrupoBienesInversion().getTipo7());
					put(mod390,Mod390DetailKey.K20_08,d.getOpIntragrupoBienesInversion().getTipo8());
					put(mod390,Mod390DetailKey.K20_10,d.getOpIntragrupoBienesInversion().getTipo10());
					put(mod390,Mod390DetailKey.K20_16,d.getOpIntragrupoBienesInversion().getTipo16());
					put(mod390,Mod390DetailKey.K20_18,d.getOpIntragrupoBienesInversion().getTipo18());
					put(mod390,Mod390DetailKey.K20_21,d.getOpIntragrupoBienesInversion().getTipo21());
				}
				if (d.getImportacionesBienesCorrientes() != null) {
					put(mod390,Mod390DetailKey.K22_04,d.getImportacionesBienesCorrientes().getTipo4());
					put(mod390,Mod390DetailKey.K22_07,d.getImportacionesBienesCorrientes().getTipo7());
					put(mod390,Mod390DetailKey.K22_08,d.getImportacionesBienesCorrientes().getTipo8());
					put(mod390,Mod390DetailKey.K22_10,d.getImportacionesBienesCorrientes().getTipo10());
					put(mod390,Mod390DetailKey.K22_16,d.getImportacionesBienesCorrientes().getTipo16());
					put(mod390,Mod390DetailKey.K22_18,d.getImportacionesBienesCorrientes().getTipo18());
					put(mod390,Mod390DetailKey.K22_21,d.getImportacionesBienesCorrientes().getTipo21());
				}
				if (d.getImportacionesBienesInversion() != null) {
					put(mod390,Mod390DetailKey.K24_04,d.getImportacionesBienesInversion().getTipo4());
					put(mod390,Mod390DetailKey.K24_07,d.getImportacionesBienesInversion().getTipo7());
					put(mod390,Mod390DetailKey.K24_08,d.getImportacionesBienesInversion().getTipo8());
					put(mod390,Mod390DetailKey.K24_10,d.getImportacionesBienesInversion().getTipo10());
					put(mod390,Mod390DetailKey.K24_16,d.getImportacionesBienesInversion().getTipo16());
					put(mod390,Mod390DetailKey.K24_18,d.getImportacionesBienesInversion().getTipo18());
					put(mod390,Mod390DetailKey.K24_21,d.getImportacionesBienesInversion().getTipo21());
				}
				if (d.getAdqIntracomunitariasBienesCorrientes() != null) {
					put(mod390,Mod390DetailKey.K26_04,d.getAdqIntracomunitariasBienesCorrientes().getTipo4());
					put(mod390,Mod390DetailKey.K26_07,d.getAdqIntracomunitariasBienesCorrientes().getTipo7());
					put(mod390,Mod390DetailKey.K26_08,d.getAdqIntracomunitariasBienesCorrientes().getTipo8());
					put(mod390,Mod390DetailKey.K26_10,d.getAdqIntracomunitariasBienesCorrientes().getTipo10());
					put(mod390,Mod390DetailKey.K26_16,d.getAdqIntracomunitariasBienesCorrientes().getTipo16());
					put(mod390,Mod390DetailKey.K26_18,d.getAdqIntracomunitariasBienesCorrientes().getTipo18());
					put(mod390,Mod390DetailKey.K26_21,d.getAdqIntracomunitariasBienesCorrientes().getTipo21());
				}
				if (d.getAdqIntracomunitariasBienesInversion() != null) {
					put(mod390,Mod390DetailKey.K28_04,d.getAdqIntracomunitariasBienesInversion().getTipo4());
					put(mod390,Mod390DetailKey.K28_07,d.getAdqIntracomunitariasBienesInversion().getTipo7());
					put(mod390,Mod390DetailKey.K28_08,d.getAdqIntracomunitariasBienesInversion().getTipo8());
					put(mod390,Mod390DetailKey.K28_10,d.getAdqIntracomunitariasBienesInversion().getTipo10());
					put(mod390,Mod390DetailKey.K28_16,d.getAdqIntracomunitariasBienesInversion().getTipo16());
					put(mod390,Mod390DetailKey.K28_18,d.getAdqIntracomunitariasBienesInversion().getTipo18());
					put(mod390,Mod390DetailKey.K28_21,d.getAdqIntracomunitariasBienesInversion().getTipo21());
				}
				if (d.getAdqIntracomunitariasServicios() != null) {
					put(mod390,Mod390DetailKey.K30_04,d.getAdqIntracomunitariasServicios().getTipo4());
					put(mod390,Mod390DetailKey.K30_07,d.getAdqIntracomunitariasServicios().getTipo7());
					put(mod390,Mod390DetailKey.K30_08,d.getAdqIntracomunitariasServicios().getTipo8());
					put(mod390,Mod390DetailKey.K30_10,d.getAdqIntracomunitariasServicios().getTipo10());
					put(mod390,Mod390DetailKey.K30_16,d.getAdqIntracomunitariasServicios().getTipo16());
					put(mod390,Mod390DetailKey.K30_18,d.getAdqIntracomunitariasServicios().getTipo18());
					put(mod390,Mod390DetailKey.K30_21,d.getAdqIntracomunitariasServicios().getTipo21());
				}
				if (d.getComRegAgricGanadPesca() != null) {
					put(mod390,Mod390DetailKey.K32,d.getComRegAgricGanadPesca().getTipoX());
				}
				if (d.getRectifDeducciones() != null) {
					put(mod390,Mod390DetailKey.K33,d.getRectifDeducciones().getTipoX());
				}
				if (d.getRegularizInversiones() != null) {
					put(mod390,Mod390DetailKey.K34,d.getRegularizInversiones());
				}
				if (d.getRegularizPorcProrrata() != null) {
					put(mod390,Mod390DetailKey.K35,d.getRegularizPorcProrrata());
				}
				if (d.getSumDeducciones() != null) {
					put(mod390,Mod390DetailKey.K36,d.getSumDeducciones());
				}
			}
		}
		Administraciones adm = iva.getAdministraciones();
		if (adm == null) {
			LiqAnual liq = iva.getLiqAnual();
			if (liq != null) {
				mod390.setBox84( ensureBigDecimal(liq.getSumResultados()) );
				mod390.setBox85( ensureBigDecimal(liq.getCompCuotasEjercicioAnterior()) );
				mod390.setBox86( ensureBigDecimal(liq.getResLiquidacion()) );
			}
		} else {
			if (adm != null) {
				mod390.setBox87( ensureBigDecimal(adm.getComun()) );
				mod390.setBox88( ensureBigDecimal(adm.getArabaAlava()) );
				mod390.setBox89( ensureBigDecimal(adm.getGipuzkoa()) );
				mod390.setBox90( ensureBigDecimal(adm.getBizkaia()) );
				mod390.setBox91( ensureBigDecimal(adm.getNavarra()) );
				mod390.setBox84( ensureBigDecimal(adm.getSumResultados()) );
				mod390.setBox92( ensureBigDecimal(adm.getResTerrComun()) );
				mod390.setBox93( ensureBigDecimal(adm.getComCuotasEjercicioAnteriorTerrComun()) );
				mod390.setBox94( ensureBigDecimal(adm.getResLiqAnualTerrComun()) );
			}
		}
		ResLiquidaciones res = iva.getResLiquidaciones();
		if (res != null) {
			PerNoRegGrupos perNo = res.getPerNoRegGrupos();
			if (perNo != null) {
				mod390.setBox95( ensureBigDecimal(perNo.getTotIngresosIVA()) );
				mod390.setBox96( ensureBigDecimal(perNo.getTotDevIVASPRegDevMensual()) );
				mod390.setBox524(ensureBigDecimal(perNo.getTotDevAdqElemTrans()) );
				mod390.setBox97( ensureBigDecimal(perNo.getImporteACompensarUltimoPeriodo()) );
				mod390.setBox98( ensureBigDecimal(perNo.getImporteADevolverUltimoPeriodo()) );
			}
			PerSiRegGrupos perSi = res.getPerSiRegGrupos();
			if (perNo != null) {
				mod390.setBox525(ensureBigDecimal(perSi.getTotResulPositivos322()) );
				mod390.setBox526(ensureBigDecimal(perSi.getTotResulNegativos322()) );
			}
		}
		VolOperaciones vol = iva.getVolOperaciones();
		if (vol != null) {
			mod390.setBox99 (ensureBigDecimal(vol.getOpRegGeneral()) );
			mod390.setBox103(ensureBigDecimal(vol.getEntregasIntracomunitariasExentas()) );
			mod390.setBox104(ensureBigDecimal(vol.getExportacionesExentasConDrchoDeduccion()) );
			mod390.setBox105(ensureBigDecimal(vol.getOpExentasSinDrchoDeduccion()) );
			mod390.setBox110(ensureBigDecimal(vol.getOpNoSujetas()) );
			mod390.setBox112(ensureBigDecimal(vol.getEntregasBienesInstalacionOtrosEM()) );
			mod390.setBox100(ensureBigDecimal(vol.getOpRegSimplificado()) );
			mod390.setBox101(ensureBigDecimal(vol.getOpRegEspAgricPescGanad()) );
			mod390.setBox102(ensureBigDecimal(vol.getOpRegEspRecEquivalencia()) );
			mod390.setBox227(ensureBigDecimal(vol.getOpRegEspBienesUsados()) );
			mod390.setBox228(ensureBigDecimal(vol.getOpRegEspAgViajes()) );
			mod390.setBox106(ensureBigDecimal(vol.getEntregasBienesInmuebles()) );
			mod390.setBox107(ensureBigDecimal(vol.getEntregasBienesInversion()) );
			mod390.setBox108(ensureBigDecimal(vol.getTotalVolOp()) );
		}
		OpEspecificas op = iva.getOpEspecificas();
		if (op != null) {
			mod390.setBox230 (ensureBigDecimal(op.getAdqInterioresExentas()) );
			mod390.setBox109 (ensureBigDecimal(op.getAdqIntracomunitariasExentas()) );
			mod390.setBox231 (ensureBigDecimal(op.getImportacionesExentas()) );
			mod390.setBox232 (ensureBigDecimal(op.getBasesIVASoportadoNoDeducible()) );
			mod390.setBox111 (ensureBigDecimal(op.getOpSujetas()) );
			mod390.setBox113 (ensureBigDecimal(op.getEntregasInteriores()) );
			mod390.setBox523 (ensureBigDecimal(op.getServInversionSP()) );
		}
		
//	iva.setOpEspecificas(getOpEspecificas(mod390));
	
		// TODO
		// iva.setRegSimplificado(null);
		// TODO
		// iva.prorratas
		// TODO
		// iva.ivaDeducibleGrupo1
		// TODO
		// iva.ivaDeducibleGrupo2
		// TODO
		// iva.ivaDeducibleGrupo3

	}

	private static double ensureBigDecimal(BigDecimal bigDecimal) {
		return bigDecimal==null?0.0:bigDecimal.doubleValue();
	}
	private static BigDecimal ensureBigDecimal(double d) {
		return new BigDecimal(Double.toString(d)).setScale(2,RoundingMode.HALF_UP);
		//return new BigDecimal(AonUtil.round(d));
	}


	private static void put(Mod390 mod390,Mod390DetailKey key,TipoBaseImponibleYCuota tipo) {
		if (tipo != null) {
			Mod390Detail detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			if (tipo.getBI() != null) {
				detail.setTaxableBase(tipo.getBI().doubleValue());
			}
			if (tipo.getCuota() != null) {
				detail.setQuota(tipo.getCuota().doubleValue());
			}
			mod390.getGeneralRegime().put(key, detail);
		}
		
	}
	private static void put(Mod390 mod390,Mod390DetailKey key,BigDecimal quota) {
		if (quota != null) {
			Mod390Detail detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			detail.setQuota(quota.doubleValue());
			mod390.getGeneralRegime().put(key, detail);
		}
		
	}
}
 