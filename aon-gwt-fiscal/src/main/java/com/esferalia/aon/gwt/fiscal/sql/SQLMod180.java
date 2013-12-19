package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.shared.Mod180;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Detail;
import com.esferalia.aon.gwt.fiscal.shared.Mod180Receiver;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.FsModel180Columns;
import com.esferalia.aon.payroll.sql.SQLConstants.FsModel180DetailColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceDetailColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.InvoiceTaxColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RaddressColumns;

public class SQLMod180 {
	private static Logger LOGGER = Logger.getLogger(SQLMod180.class.getName());

	//@formatter:off
	private static String VALIDATE_MOD180 = "SELECT 1 FROM " + SQLConstants.FS_MODEL180
			+ " WHERE " + SQLConstants.FS_MODEL180 + "." + FsModel180Columns.YEAR + " = ? "
			+ " AND " + SQLConstants.FS_MODEL180 + "." + FsModel180Columns.ENTERPRISE + " = ? "
			+ " AND " + SQLConstants.FS_MODEL180 + "." + FsModel180Columns.REPLACEMENT + " = ? ";
			
	private static String SELECT_INVOICE = "SELECT "
			+ SQLConstants.INVOICE + "." + InvoiceColumns.RDOCUMENT + ","
			+ SQLConstants.INVOICE + "." + InvoiceColumns.RNAME + ","
			+ " MIN( " + SQLConstants.INVOICE + "." + InvoiceColumns.REGISTRY + ") " + InvoiceColumns.REGISTRY+ ","
			+ " SUM( " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.BASE + ") " + InvoiceTaxColumns.BASE + ","
			+ " SUM(IF( " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.QUOTA + "!= 0," 
					+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.QUOTA
					+ ",ROUND(" + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.BASE
						+ " * " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.PERCENTAGE
						+ " / 100, 2) ) ) " + InvoiceTaxColumns.QUOTA + "," 
			+ " MAX( " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.PERCENTAGE + ") " + InvoiceTaxColumns.PERCENTAGE
			+" FROM " + SQLConstants.INVOICE
			+" INNER JOIN "+SQLConstants.INVOICE_DETAIL
			+ " ON " + SQLConstants.INVOICE + "." + InvoiceColumns.ID 
			+ " = " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.INVOICE 
			+" INNER JOIN "+SQLConstants.INVOICE_TAX
			+" ON " + SQLConstants.INVOICE_DETAIL + "." + InvoiceDetailColumns.ID
			+ " = " + SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.INVOICE_DETAIL
			+" WHERE "+ SQLConstants.INVOICE + "." + InvoiceColumns.DOMAIN +" = ?"
			// No Ventas
			+" AND "+ SQLConstants.INVOICE + "." + InvoiceColumns.TYPE +" != 1 "
			// IRPF
			+" AND "+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.TAX_TYPE + " = 2" 
			// IRPF de Alquiler
			+" AND "+ SQLConstants.INVOICE_TAX + "." + InvoiceTaxColumns.WITHHOLDING_TYPE + " = 1"
			+" AND "+ SQLConstants.INVOICE + "." + InvoiceColumns.ISSUE_DATE +" BETWEEN ? AND ?"
			+" GROUP BY "
				+ SQLConstants.INVOICE + "." + InvoiceColumns.RDOCUMENT + ","
				+ SQLConstants.INVOICE + "." + InvoiceColumns.RNAME;
	
	private static String GEOZONE_SELECT = "SELECT "
		+ SQLConstants.GEOZONE + "." + GeozoneColumns.CODE + " " + GeozoneColumns.CODE
		+ " FROM " + SQLConstants.RADDRESS + "," + SQLConstants.GEOZONE 
		+ " WHERE "
			+ SQLConstants.RADDRESS + "." + RaddressColumns.GEOZONE + " = "
			+ SQLConstants.GEOZONE + "." + GeozoneColumns.ID	
			+" AND " + SQLConstants.RADDRESS + "." + RaddressColumns.REGISTRY + " =  ?"
			+" AND " + SQLConstants.RADDRESS + "." + RaddressColumns.TYPE + " =  0";

	private static final String MOD180_DELETE = "DELETE FROM " + SQLConstants.FS_MODEL180 
			+ " WHERE " + FsModel180Columns.ID + " = ?"; 
	private static final String MOD180_DETAIL_DELETE_BY_MOD180 = "DELETE FROM " + SQLConstants.FS_MODEL180_DETAIL 
			+ " WHERE " + FsModel180DetailColumns.FS_MODEL180 + " = ?"; 
	private static final String MOD180_DETAIL_DELETE = "DELETE FROM " + SQLConstants.FS_MODEL180_DETAIL 
			+ " WHERE " + FsModel180DetailColumns.ID + " = ?"; 

	private static final String MOD180_SELECT = "SELECT "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.ID + ", " 
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.YEAR + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.ADMINISTRATION + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.REPLACEMENT + ", " 
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.DOCUMENT + ", " 
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.NAME + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.RECEIPT + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.REPLACED_RECEIPT + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.DOMAIN + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.ENTERPRISE + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.CONTACT_PERSON + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.CONTACT_PHONE + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.RECEIVER_COUNT_TOTAL + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.RECEIPT_TOTAL + ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.RETENTION_TOTAL+ ", "
			+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.COMMENTS
			+ " FROM " + SQLConstants.FS_MODEL180;

	private static final String MOD180_SELECT_ID = MOD180_SELECT + " WHERE "
			+ SQLConstants.FS_MODEL180 + "." +FsModel180Columns.ID + " = ?";
	private static final String MOD180_SELECT_DOMAIN = MOD180_SELECT
			+ " INNER JOIN " + SQLConstants.DOMAIN
			+ " ON " + SQLConstants.FS_MODEL180 + "." +SQLConstants.FsModel180Columns.DOMAIN
			+ "=" + SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.ID
			+ " WHERE  ( " 
			+ SQLConstants.FsModel180Columns.DOMAIN + " = ? OR ( "
			+ SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.PARENT + " IS NOT NULL AND " +
			SQLConstants.FS_MODEL180 + "." +SQLConstants.FsModel180Columns.DOMAIN + " IN (SELECT " +
			SQLConstants.DomainColumns.ID + " FROM " + SQLConstants.DOMAIN + " WHERE " +
			SQLConstants.DOMAIN + "."+ SQLConstants.DomainColumns.PARENT + " =  ? )))"
			+ " ORDER BY "
				+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.YEAR + " DESC, "
				+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.NAME + ", "
				+ SQLConstants.FS_MODEL180 + "." + FsModel180Columns.REPLACEMENT;

	private static final String MOD180_INSERT = "INSERT INTO " + SQLConstants.FS_MODEL180 + "( " 
			+ FsModel180Columns.DOMAIN + ", "
			+ FsModel180Columns.ENTERPRISE + ", " 
			+ FsModel180Columns.YEAR + ", " 
			+ FsModel180Columns.ADMINISTRATION + ", "
			+ FsModel180Columns.STATUS + ", "
			+ FsModel180Columns.SECURITY_LEVEL + ", "
			+ FsModel180Columns.DOCUMENT + ", " 
			+ FsModel180Columns.NAME + ", "
			+ FsModel180Columns.CONTACT_PERSON + ", "
			+ FsModel180Columns.CONTACT_PHONE + ", "
			+ FsModel180Columns.COMPLEMENTARY + ", "
			+ FsModel180Columns.REPLACEMENT + ", " 
			+ FsModel180Columns.RECEIPT + ", "
			+ FsModel180Columns.REPLACED_RECEIPT + ", "
			+ FsModel180Columns.COMMENTS + ", " 
			+ FsModel180Columns.RECEIVER_COUNT_TOTAL + ", "
			+ FsModel180Columns.RECEIPT_TOTAL + ", "
			+ FsModel180Columns.RETENTION_TOTAL
			+ " ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String MOD180_UPDATE = "UPDATE " + SQLConstants.FS_MODEL180 + " SET " 
			+ FsModel180Columns.YEAR + " = ?, " 
			+ FsModel180Columns.ADMINISTRATION + " = ?, "
			+ FsModel180Columns.STATUS + " = ?, "
			+ FsModel180Columns.SECURITY_LEVEL + " = ?, "
			+ FsModel180Columns.DOCUMENT + " = ?, " 
			+ FsModel180Columns.NAME + " = ?, " 
			+ FsModel180Columns.CONTACT_PERSON + " = ?, "
			+ FsModel180Columns.CONTACT_PHONE + " = ?, "
			+ FsModel180Columns.COMPLEMENTARY + " = ?, "
			+ FsModel180Columns.REPLACEMENT + " = ?, "
			+ FsModel180Columns.RECEIPT + " = ?, "
			+ FsModel180Columns.REPLACED_RECEIPT + " = ?, "
			+ FsModel180Columns.COMMENTS + " = ?, " 
			+ FsModel180Columns.RECEIVER_COUNT_TOTAL + " = ?, "
			+ FsModel180Columns.RECEIPT_TOTAL + " = ?, "
			+ FsModel180Columns.RETENTION_TOTAL + " = ? " + " WHERE "
			+ FsModel180Columns.ID + " = ?";

	private static final String MOD180_DETAIL_ID_NAME_SELECT = "SELECT "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.ID + ", " 
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.NAME 
			+ " FROM " + SQLConstants.FS_MODEL180_DETAIL;
	private static final String MOD180_DETAIL_SELECT = "SELECT "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.ID + ", " 
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.NAME + ", " 
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.PROVINCE + ", "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.DOCUMENT + ", "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.REPRESENTATIVE_DOCUMENT + ", "	
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.INKIND + ", " 
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.PERCEPTION + ", "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.RETENTION + ", "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.PERCENTAGE+ ", "
			+ SQLConstants.FS_MODEL180_DETAIL + "." + FsModel180DetailColumns.ACCRUAL_YEAR 
			+ " FROM " + SQLConstants.FS_MODEL180_DETAIL;
	private static final String MOD180_DETAIL_SELECT_ID = MOD180_DETAIL_SELECT + " WHERE "
			+ SQLConstants.FS_MODEL180_DETAIL + "." +FsModel180DetailColumns.ID + " = ?";
	private static final String MOD180_RECEIVER_SELECT_ID = MOD180_DETAIL_SELECT + " WHERE "
			+ SQLConstants.FS_MODEL180_DETAIL + "." +FsModel180DetailColumns.FS_MODEL180 + " = ?";

	private static final String MOD180_DETAIL_INSERT = "INSERT INTO "
			+ SQLConstants.FS_MODEL180_DETAIL + "( "
			+FsModel180DetailColumns.DOMAIN + ", "	
			+FsModel180DetailColumns.FS_MODEL180 + ", "	
			+FsModel180DetailColumns.DOCUMENT + ", "	
			+FsModel180DetailColumns.NAME + ", "	
			+FsModel180DetailColumns.REPRESENTATIVE_DOCUMENT + ", "	
			+FsModel180DetailColumns.PROVINCE + ", "	
			+FsModel180DetailColumns.INKIND + ", "	
			+FsModel180DetailColumns.PERCEPTION + ", "	
			+FsModel180DetailColumns.RETENTION + ", "	
			+FsModel180DetailColumns.PERCENTAGE+ ", "	
			+FsModel180DetailColumns.ACCRUAL_YEAR	
			+ " ) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

	private static final String MOD180_DETAIL_UPDATE = 
			"UPDATE " + SQLConstants.FS_MODEL180_DETAIL + " SET "
			+FsModel180DetailColumns.DOCUMENT + "=?, "	
			+FsModel180DetailColumns.NAME + "=?, "	
			+FsModel180DetailColumns.REPRESENTATIVE_DOCUMENT + "=?, "	
			+FsModel180DetailColumns.PROVINCE + "=?, "	
			+FsModel180DetailColumns.INKIND + "=?, "	
			+FsModel180DetailColumns.PERCEPTION + "=?, "	
			+FsModel180DetailColumns.RETENTION + "=?, "	
			+FsModel180DetailColumns.PERCENTAGE + "=?, "	
			+FsModel180DetailColumns.ACCRUAL_YEAR + "=? "	
			+ " WHERE "
			+ SQLConstants.FS_MODEL180_DETAIL + "." +FsModel180DetailColumns.ID + " = ?";

	//@formatter:on

	public static Mod180 save(Connection conn, Mod180 mod180)
			throws AonSQLException {
		if (mod180.getId() == null) {
			LOGGER.log(Level.INFO, "INSERTING Mod180");
			return insert(conn, mod180);
		} else {
			LOGGER.log(Level.INFO, "UPDATING Mod180");
			return update(conn, mod180);
		}
	}

	public static Mod180 save(Connection conn, Mod180 mod180,
			ArrayList<Mod180Receiver> receivers) throws AonSQLException {
		PreparedStatement insertStmt = null;
		PreparedStatement updateStmt = null;
		PreparedStatement deleteStmt = null;
		try {
			mod180 = save(conn, mod180);
			insertStmt = conn.prepareStatement(MOD180_DETAIL_INSERT);
			updateStmt = conn.prepareStatement(MOD180_DETAIL_UPDATE);
			deleteStmt = conn.prepareStatement(MOD180_DETAIL_DELETE);
			for (Mod180Receiver receiver : receivers) {
				if (receiver.getId() == null || receiver.getId() < 0) {
					if (!receiver.isDeleted()) {
						receiver.setDomain(mod180.getDomain());
						receiver.setMod180(mod180.getId());
						insert(insertStmt, receiver);
						LOGGER.log(Level.INFO, "INSERTING Mod180Detail");
					}
				} else {
					if (receiver.isDeleted()) {
						delete(deleteStmt, receiver);
						LOGGER.log(Level.INFO, "DELETING Mod180Detail");
					} else {
						update(updateStmt, receiver);
						LOGGER.log(Level.INFO, "UPDATING Mod180Detail");
					}
				}
			}
			return mod180;
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(insertStmt);
			SQLUtils.closeQuietly(updateStmt);
			SQLUtils.closeQuietly(deleteStmt);
		}

	}

	private static Mod180 insert(Connection conn, Mod180 mod180)
			throws AonSQLException {
		PreparedStatement insertStmt = null;
		ResultSet rs = null;
		try {
			validate(conn,mod180);
			insertStmt = conn.prepareStatement(MOD180_INSERT,
					Statement.RETURN_GENERATED_KEYS);
			SQLUtils.setInt(insertStmt, 1, mod180.getDomain());
			SQLUtils.setInt(insertStmt, 2, mod180.getEnterprise());
			SQLUtils.setInt(insertStmt, 3, mod180.getYear());
			SQLUtils.setInt(insertStmt, 4, mod180.getAdministration());
			SQLUtils.setInt(insertStmt, 5, 0);
			SQLUtils.setInt(insertStmt, 6, mod180.isConfidential() ? 1 : 0);
			SQLUtils.setString(insertStmt, 7, mod180.getDocument());
			SQLUtils.setString(insertStmt, 8, mod180.getName());
			SQLUtils.setString(insertStmt, 9, mod180.getContactPerson());
			SQLUtils.setString(insertStmt, 10, mod180.getContactPhone());
			SQLUtils.setInt(insertStmt, 11, 0);
			SQLUtils.setInt(insertStmt, 12,mod180.isReplacement() ? 1 : 0);
			SQLUtils.setString(insertStmt, 13, mod180.getReceipt());
			SQLUtils.setString(insertStmt, 14, mod180.getReplacedReceipt());
			SQLUtils.setString(insertStmt, 15, mod180.getComments());
			SQLUtils.setInt(insertStmt, 16, mod180.getReceiverCountTotal());
			SQLUtils.setDouble(insertStmt, 17, mod180.getReceiptTotal());
			SQLUtils.setDouble(insertStmt, 18, mod180.getRetentionTotal());
			insertStmt.execute();
			rs = insertStmt.getGeneratedKeys();
			if (!rs.next()) {
				throw new AonSQLException("Unable to recover last inserted id");
			}
			mod180.setId(rs.getInt(1));
			insertModel180Detail(conn, mod180);
			return getById(mod180.getId(), conn);
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(insertStmt);
		}
	}

	private static void validate(Connection conn, Mod180 mod180) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(VALIDATE_MOD180,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1,mod180.getYear());
			stmt.setInt(2,mod180.getEnterprise());
			
			if (mod180.isReplacement()) {
				// Se busca que exista una declaración a la que sustituir.
				stmt.setInt(3,0);
				rs = stmt.executeQuery();
				if (!rs.next()) {
					throw new AonSQLException("No existe una declaraci\u00F3n a la que sustituir.");
				}
				rs.close();
				
				// Se busca que no exista una declaración sustitutiva.
				stmt.setInt(3,1);
				rs = stmt.executeQuery();
				if (rs.next()) {
					throw new AonSQLException("Ya existe una declaraci\u00F3n sustitutiva.");
				}
				rs.close();
			} else {
				// Se busca que no exista ya una declaración.
				stmt.setInt(3,0);
				rs = stmt.executeQuery();
				if (rs.next()) {
					throw new AonSQLException("Ya existe una declaraci\u00F3n en el ejercicio.");
				}
				rs.close();
			}
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
				
	}

	private static Mod180 update(Connection conn, Mod180 mod180)
			throws AonSQLException {
		PreparedStatement updateStmt = null;
		try {
			updateStmt = conn.prepareStatement(MOD180_UPDATE);
			SQLUtils.setInt(updateStmt, 1, mod180.getYear());
			SQLUtils.setInt(updateStmt, 2, mod180.getAdministration());
			SQLUtils.setShort(updateStmt, 3, (short) 0);
			SQLUtils.setShort(updateStmt, 4,
					(short) (mod180.isConfidential() ? 1 : 0));
			SQLUtils.setString(updateStmt, 5, mod180.getDocument());
			SQLUtils.setString(updateStmt, 6, mod180.getName());
			SQLUtils.setString(updateStmt, 7, mod180.getContactPerson());
			SQLUtils.setString(updateStmt, 8, mod180.getContactPhone());
			SQLUtils.setShort(updateStmt, 9, (short) 0);
			SQLUtils.setShort(updateStmt, 10,
					(short) (mod180.isReplacement() ? 1 : 0));
			SQLUtils.setString(updateStmt,11, mod180.getReceipt());
			SQLUtils.setString(updateStmt,12, mod180.getReplacedReceipt());			
			SQLUtils.setString(updateStmt, 13, mod180.getComments());
			SQLUtils.setInt(updateStmt, 14, mod180.getReceiverCountTotal());
			SQLUtils.setDouble(updateStmt, 15, mod180.getReceiptTotal());
			SQLUtils.setDouble(updateStmt, 16, mod180.getRetentionTotal());
			SQLUtils.setInt(updateStmt, 17, mod180.getId());
			updateStmt.execute();
			return getById(mod180.getId(), conn);
		} catch (AonSQLException e) {
			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(updateStmt);
		}
	}

	public static void delete(Connection conn, Mod180 mod180)
			throws AonSQLException {
		PreparedStatement deleteStmt = null;
		PreparedStatement deleteDetailStmt = null;
		try {
			LOGGER.log(Level.INFO,
					"DELETING DECLARATION DETAIL (" + mod180.getId() + ")");
			deleteDetailStmt = conn
					.prepareStatement(MOD180_DETAIL_DELETE_BY_MOD180);
			SQLUtils.setInt(deleteDetailStmt, 1, mod180.getId());
			deleteDetailStmt.execute();

			LOGGER.log(Level.INFO, "DELETING DECLARATION(" + mod180.getId()
					+ ")");
			deleteStmt = conn.prepareStatement(MOD180_DELETE);
			SQLUtils.setInt(deleteStmt, 1, mod180.getId());
			deleteStmt.execute();
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(deleteDetailStmt);
			SQLUtils.closeQuietly(deleteStmt);
		}
	}

	public static ArrayList<Mod180> getByDomain(int domain, Connection conn)
			throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD180_SELECT_DOMAIN,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, domain);
			LOGGER.log(Level.INFO, "GETTING BY DOMAIN (" + domain + ")");
			rs = stmt.executeQuery();
			ArrayList<Mod180> list = new ArrayList<Mod180>();
			Mod180 mod180 = null;
			while (rs.next()) {
				mod180 = new Mod180();
				populateResultSet(rs, mod180);
				list.add(mod180);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	private static void populateResultSet(ResultSet rs, Mod180 mod180)
			throws SQLException {

		mod180.setId(rs.getInt(FsModel180Columns.ID));
		mod180.setDomain(rs.getInt(FsModel180Columns.DOMAIN));
		mod180.setEnterprise(rs.getInt(FsModel180Columns.ENTERPRISE));
		mod180.setYear(rs.getInt(FsModel180Columns.YEAR));
		mod180.setAdministration(rs.getInt(FsModel180Columns.ADMINISTRATION));
		mod180.setReplacement(rs.getBoolean(FsModel180Columns.REPLACEMENT));
		mod180.setDocument(rs.getString(FsModel180Columns.DOCUMENT));
		mod180.setName(rs.getString(FsModel180Columns.NAME));
		mod180.setContactPerson(rs.getString(FsModel180Columns.CONTACT_PERSON));
		mod180.setContactPhone(rs.getString(FsModel180Columns.CONTACT_PHONE));
		mod180.setReceipt(rs.getString(FsModel180Columns.RECEIPT));
		mod180.setReplacedReceipt(rs.getString(FsModel180Columns.REPLACED_RECEIPT));
		mod180.setReceiverCountTotal(rs
				.getInt(FsModel180Columns.RECEIVER_COUNT_TOTAL));
		mod180.setReceiptTotal(rs.getDouble(FsModel180Columns.RECEIPT_TOTAL));
		mod180.setRetentionTotal(rs
				.getDouble(FsModel180Columns.RETENTION_TOTAL));
		mod180.setComments(rs.getString(FsModel180Columns.COMMENTS));
	}

	public static Mod180 getById(int id, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD180_SELECT_ID,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			LOGGER.log(Level.INFO, "GETTING BY ID (" + id + ")");
			rs = stmt.executeQuery();
			Mod180 mod180 = null;
			if (rs.next()) {
				mod180 = new Mod180();
				populateResultSet(rs, mod180);
			}
			return mod180;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static Mod180Receiver getDetailById(int id, Connection conn)
			throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD180_DETAIL_SELECT_ID,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, id);
			LOGGER.log(Level.INFO, "GETTING DETAILS BY ID (" + id + ")");
			rs = stmt.executeQuery();
			Mod180Receiver receiver = null;
			if (rs.next()) {
				receiver = new Mod180Receiver();
				populateResultSet(rs, receiver);
			}
			return receiver;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static ArrayList<Mod180Detail> getDetailsByMod180(int mod180,
			int offset, int limit, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String select = MOD180_DETAIL_ID_NAME_SELECT + " WHERE "
					+ SQLConstants.FS_MODEL180_DETAIL + "."
					+ FsModel180DetailColumns.FS_MODEL180 + " = ?"
					+ " ORDER BY " + SQLConstants.FS_MODEL180_DETAIL + "."
					+ FsModel180DetailColumns.NAME + " LIMIT ?  OFFSET ?";
			stmt = conn.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, mod180);
			stmt.setInt(2, limit);
			stmt.setInt(3, offset);
			LOGGER.log(Level.INFO, "GETTING DETAILS BY MOD180 (" + mod180
					+ ", off: " + offset + ",lim: " + limit + " )");
			rs = stmt.executeQuery();
			ArrayList<Mod180Detail> list = new ArrayList<Mod180Detail>();
			Mod180Detail mod180Detail = null;
			while (rs.next()) {
				mod180Detail = new Mod180Detail();
				mod180Detail.setId(rs.getInt(FsModel180DetailColumns.ID));
				mod180Detail
						.setName(rs.getString(FsModel180DetailColumns.NAME));
				list.add(mod180Detail);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	public static ArrayList<Mod180Receiver> getReceiversByMod180(int mod180, Connection conn) throws AonSQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(MOD180_RECEIVER_SELECT_ID, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, mod180);
			LOGGER.log(Level.INFO, "GETTING RECEIVERS BY MOD180 (" + mod180 + " )");
			rs = stmt.executeQuery();
			ArrayList<Mod180Receiver> list = new ArrayList<Mod180Receiver>();
			Mod180Receiver receiver = null;
			while (rs.next()) {
				receiver = new Mod180Receiver();
				populateResultSet(rs, receiver);
				list.add(receiver);
			}
			return list;
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
		}
	}

	private static void populateResultSet(ResultSet rs, Mod180Receiver receiver)
			throws SQLException {
		receiver.setId(rs.getInt(FsModel180DetailColumns.ID));
		receiver.setDocument(rs.getString(FsModel180DetailColumns.DOCUMENT));
		receiver.setName(rs.getString(FsModel180DetailColumns.NAME));
		receiver.setRepresentativeDocument(rs
				.getString(FsModel180DetailColumns.REPRESENTATIVE_DOCUMENT));
		receiver.setProvince(rs.getInt(FsModel180DetailColumns.PROVINCE));
		receiver.setInKind(rs.getBoolean(FsModel180DetailColumns.INKIND));
		receiver.setPerception(rs
				.getDouble(FsModel180DetailColumns.PERCEPTION));
		receiver.setRetention(rs
				.getDouble(FsModel180DetailColumns.RETENTION));
		receiver.setPercent(rs
				.getDouble(FsModel180DetailColumns.PERCENTAGE));
		receiver.setAccrualYear(rs
				.getInt(FsModel180DetailColumns.ACCRUAL_YEAR));
	}

	private static void insertModel180Detail(Connection conn, Mod180 mod180)
			throws AonSQLException {
		Date firstDay = SQLUtils.getYearFirstDay(mod180.getYear());
		Date lastDay = SQLUtils.getYearLastDay(mod180.getYear());
		
		PreparedStatement insertStmt = null;
		
		PreparedStatement geozoneStmt = null;
		ResultSet geozoneRs = null;
		PreparedStatement invoiceStmt = null;
		ResultSet invoiceRs = null;
		
		try {
			geozoneStmt = conn.prepareStatement(GEOZONE_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			insertStmt = conn.prepareStatement(MOD180_DETAIL_INSERT);
			Mod180Receiver detail = null;
			
			invoiceStmt = conn.prepareStatement(SELECT_INVOICE,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			System.out.println( "Domain ..: " + mod180.getDomain());
			System.out.println( "First ...: " + firstDay);
			System.out.println( "Last ....: " + lastDay);
			invoiceStmt.setInt(1, mod180.getDomain());
			invoiceStmt.setDate(2, new java.sql.Date(firstDay.getTime()));
			invoiceStmt.setDate(3, new java.sql.Date(lastDay.getTime()));
			invoiceRs = invoiceStmt.executeQuery();
			while (invoiceRs.next()) {
				detail = new Mod180Receiver();
				detail.setDomain(mod180.getDomain());
				detail.setMod180(mod180.getId());
				detail.setDocument(invoiceRs.getString(InvoiceColumns.RDOCUMENT));
				detail.setName(invoiceRs.getString(InvoiceColumns.RNAME));
				detail.setInKind(false);
				detail.setPerception(invoiceRs.getDouble(InvoiceTaxColumns.BASE));
				detail.setRetention(invoiceRs.getDouble(InvoiceTaxColumns.QUOTA));
				detail.setPercent(invoiceRs.getDouble(InvoiceTaxColumns.PERCENTAGE));
				System.out.println( "Perception..: " + detail.getPerception());
				System.out.println( "Retention...: " + detail.getRetention());
				int registryId = invoiceRs.getInt(InvoiceColumns.REGISTRY);
				geozoneStmt.setInt(1, registryId);
				geozoneRs = geozoneStmt.executeQuery();
				if (geozoneRs.next()) {
					String code = geozoneRs.getString(GeozoneColumns.CODE);
					try {
						int province = Integer.parseInt(code);
						detail.setProvince(province);
					} catch (NumberFormatException e) {
						// nothing. Si la clave no es numero, no es provicncia válida.
					}
				}
				geozoneRs.close();
				insert(insertStmt, detail);
			}
		} catch (Throwable e) {
			throw new AonSQLException(e.getMessage());
		} finally {
			SQLUtils.closeQuietly(insertStmt);
			SQLUtils.closeQuietly(geozoneStmt);
			SQLUtils.closeQuietly(geozoneRs);
			SQLUtils.closeQuietly(invoiceStmt);
			SQLUtils.closeQuietly(invoiceRs);
		}
	}

	private static void insert(PreparedStatement insertStmt, Mod180Receiver perceptor)
			throws SQLException {
		LOGGER.log(Level.INFO, "INSERTING RECEIVERS BY MOD180 (" + perceptor.getDocument() + " )");
		SQLUtils.setInt(insertStmt, 1, perceptor.getDomain());
		SQLUtils.setInt(insertStmt, 2, perceptor.getMod180());
		SQLUtils.setString(insertStmt, 3, perceptor.getDocument());
		SQLUtils.setString(insertStmt, 4, perceptor.getName());
		SQLUtils.setString(insertStmt, 5, perceptor.getRepresentativeDocument());
		SQLUtils.setInt(insertStmt, 6, perceptor.getProvince());
		SQLUtils.setInt(insertStmt, 7, perceptor.isInKind()?1:0);
		SQLUtils.setDouble(insertStmt, 8, perceptor.getPerception());
		SQLUtils.setDouble(insertStmt, 9, perceptor.getRetention());
		SQLUtils.setDouble(insertStmt, 10, perceptor.getPercent());
		SQLUtils.setInt(insertStmt, 11, perceptor.getAccrualYear());
		insertStmt.execute();
	}

	private static void update(PreparedStatement updateStmt, Mod180Receiver perceptor)
			throws SQLException {
		LOGGER.log(Level.INFO, "UPDATING RECEIVERS BY MOD180 (" + perceptor.getDocument() + " )");
		SQLUtils.setString(updateStmt, 1, perceptor.getDocument());
		SQLUtils.setString(updateStmt, 2, perceptor.getName());
		SQLUtils.setString(updateStmt, 3, perceptor.getRepresentativeDocument());
		SQLUtils.setInt(updateStmt, 4, perceptor.getProvince());
		SQLUtils.setInt(updateStmt, 5, perceptor.isInKind()?1:0);
		SQLUtils.setDouble(updateStmt, 6, perceptor.getPerception());
		SQLUtils.setDouble(updateStmt, 7, perceptor.getRetention());
		SQLUtils.setDouble(updateStmt, 8, perceptor.getPercent());
		SQLUtils.setInt(updateStmt, 9, perceptor.getAccrualYear());
		SQLUtils.setInt(updateStmt, 10, perceptor.getId());
		updateStmt.execute();
	}

	private static void delete(PreparedStatement deleteStmt, Mod180Receiver perceptor)
			throws SQLException {
		LOGGER.log(Level.INFO, "DELETING RECEIVERS BY MOD180 (" + perceptor.getDocument() + " )");
		SQLUtils.setInt(deleteStmt, 1, perceptor.getId());
		deleteStmt.execute();
	}

}
