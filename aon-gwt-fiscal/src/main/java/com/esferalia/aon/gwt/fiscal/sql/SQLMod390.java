package com.esferalia.aon.gwt.fiscal.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.code.aon.fiscal.enumeration.FiscalActivityInfoKey;
import com.code.aon.fiscal.enumeration.FiscalActivityInfoType;
import com.code.aon.fiscal.enumeration.Mod311Key;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.shared.Mod311Results;

public class SQLMod390 {
	
	//@formatter:off
	/*
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

	private static final String VAT_TAX_DECLARATION_SELECT = 
			"SELECT "  
				+SQLConstants.FS_VAT +"." + FsVatColumns.PERIOD +" "+FsVatColumns.PERIOD + ","  
				+SQLConstants.FS_VAT +"." + FsVatColumns.TAX_REFUND_REGISTRY +" "+FsVatColumns.TAX_REFUND_REGISTRY + ","
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
	private static final String VAT_TAX_DETAIL_SELECT = 
			"SELECT "
				+SQLConstants.FS_VAT_DETAIL +"." + FsVatDetailColumns.VAT_KEY +" "+FsVatDetailColumns.VAT_KEY + ","
				+SQLConstants.FS_VAT_DETAIL +"." + FsVatDetailColumns.TAXABLE_BASE +" "+FsVatDetailColumns.TAXABLE_BASE
				+" FROM " + SQLConstants.FS_VAT
				+ " INNER JOIN " + SQLConstants.FS_VAT_DETAIL 
				+" ON " +SQLConstants.FS_VAT_DETAIL+"."+FsVatDetailColumns.FS_VAT
				+" = " + SQLConstants.FS_VAT+"."+FsVatColumns.ID
				+" WHERE " + SQLConstants.FS_VAT +"."+FsVatColumns.DOMAIN+"=?"
				+" AND " + SQLConstants.FS_VAT +"."+FsVatColumns.YEAR +"=?"
				+" AND " + SQLConstants.FS_VAT +"."+FsVatColumns.PERIOD +"!=" + Period.YEAR.ordinal();  
*/
	//@formatter:on
/*
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
			SQLUtils.setInt(stmt, 4, (int) mod390.getAdministration());
			SQLUtils.setInt(stmt, 5, 0);
			SQLUtils.setInt(stmt, 6, mod390.isConfidential() ? 1 : 0);
			SQLUtils.setString(stmt, 7, mod390.getDocument());
			SQLUtils.setString(stmt, 8, mod390.getEnterpriseName());
			SQLUtils.setInt(stmt, 9, 0);
			SQLUtils.setInt(stmt, 10,mod390.isReplacement() ? 1 : 0);
			SQLUtils.setString(stmt, 11, mod390.getComments());
			SQLUtils.setString(stmt, 12, mod390.getReceipt());
			SQLUtils.setString(stmt, 13, mod390.getReplacedReceipt());
			
			AEATIVA2013 iva = Mod390toAEATIVA2013.getAEATIVA2013(mod390);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);				
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(iva,writer);
			SQLUtils.setString(stmt, 14, writer.toString());
			SQLUtils.setInt(stmt, 15, mod390.getId());
			stmt.execute();
			return mod390;
//			return getById(mod390.getId(), conn);
//		} catch (AonSQLException e) {
//			throw e;
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
			SQLUtils.setInt(insertStmt, 4, (int) mod390.getAdministration());
			SQLUtils.setInt(insertStmt, 5, 0);
			SQLUtils.setInt(insertStmt, 6, mod390.isConfidential() ? 1 : 0);
			SQLUtils.setString(insertStmt, 7, mod390.getDocument());
			SQLUtils.setString(insertStmt, 8, mod390.getEnterpriseName());
			SQLUtils.setInt(insertStmt, 9, 0);
			SQLUtils.setInt(insertStmt, 10,mod390.isReplacement() ? 1 : 0);
			SQLUtils.setString(insertStmt, 11, mod390.getComments());
			SQLUtils.setString(insertStmt, 12, mod390.getReceipt());
			SQLUtils.setString(insertStmt, 13, mod390.getReplacedReceipt());
			
			AEATIVA2013 iva = Mod390toAEATIVA2013.getAEATIVA2013(mod390);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(AEATIVA2013.class);				
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(iva,writer);
			SQLUtils.setString(insertStmt, 14, writer.toString());
			
			insertStmt.execute();
			rs = insertStmt.getGeneratedKeys();
			if (!rs.next()) {
				throw new AonSQLException("Unable to recover last inserted id");
			}
			mod390.setId(rs.getInt(1));
			return mod390;
//			return getById(mod390.getId(), conn);
//		} catch (AonSQLException e) {
//			throw e;
		} catch (Throwable e) {
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(insertStmt);
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
*/

/*
	private static Mod303Results getMod303Results(int domain, int year,
			Connection conn)  throws AonSQLException  {
		
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
				boolean taxRefundRegistry = rs.getBoolean(FsVatColumns.TAX_REFUND_REGISTRY);
				double deposit = rs.getDouble(FsVatDeclarationColumns.DEPOSIT);
				double payBack = rs.getDouble(FsVatDeclarationColumns.PAY_BACK);
				mod303Results.setDepositSum(AonUtil.round(mod303Results.getDepositSum() + deposit));
				if ( taxRefundRegistry ) {
					mod303Results.setPaybackSum(AonUtil.round(mod303Results.getPaybackSum() + payBack));
				}
				// Last Period
				if (period == Period.M12 || period == Period.T4) {
					mod303Results.setLastPeriodPaybackResult(payBack);
					mod303Results.setLastPeriodCompensateResult(rs.getDouble(FsVatDeclarationColumns.COMPENSATE));
					if ( taxRefundRegistry ) {
						mod303Results.setLastPeriodPaybackResult(0);	
					}
				}
			}
			rs.close();
			stmt.close();
			
			stmt = conn.prepareStatement(VAT_TAX_DETAIL_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, year);
			rs = stmt.executeQuery();
			while (rs.next()) {
				double taxableBase = rs.getDouble(FsVatDetailColumns.TAXABLE_BASE);
				VatTaxKey key = VatTaxKey.valueOf( rs.getString(FsVatDetailColumns.VAT_KEY) );
				if (VatTaxKey.A1 == key ) {
					mod303Results.setNationalSales( AonUtil.round(mod303Results.getNationalSales() + taxableBase ) ); 
				} else if (VatTaxKey.A2 == key ) {
					mod303Results.setReSales( AonUtil.round(mod303Results.getReSales() + taxableBase ) );
				} else if (VatTaxKey.A4 == key ) {
					mod303Results.setISPSales( AonUtil.round(mod303Results.getISPSales() + taxableBase ) );
				} else if (VatTaxKey.A5 == key ) {
					mod303Results.setNationalSales( AonUtil.round(mod303Results.getNationalSales() + taxableBase ) );
				} else if (VatTaxKey.EI == key ) {
					mod303Results.setIntracommunitarySales( AonUtil.round(mod303Results.getIntracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.EX1 == key ) {
					mod303Results.setExtracommunitarySales( AonUtil.round(mod303Results.getExtracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.EX2 == key ) {
					mod303Results.setExtracommunitarySales( AonUtil.round(mod303Results.getExtracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.OO == key ) {
					mod303Results.setExtracommunitarySales( AonUtil.round(mod303Results.getExtracommunitarySales() + taxableBase ) );
				} else if (VatTaxKey.OS == key ) {
					mod303Results.setWithoutRightSales( AonUtil.round(mod303Results.getWithoutRightSales() + taxableBase ) );
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
*/
	public static ArrayList<Mod311Results> getMod311Results(int domain, int year,
			Connection conn) throws AonSQLException  {
		
		String SELECT_ACTIVITY = 
		"SELECT fsd.info_key ky,fsd.type type,fsd.line line,fsd.value value,fsd.base base,fs.farmer"
			+" FROM fs_activity fs"
			+" INNER JOIN fs_activity_info fsd ON fsd.fs_activity = fs.id"
			+" WHERE fs.domain = ? "
			+" AND fs.year = ? "
			+" AND fs.epigraph = ? "
			+" AND " 
			+" ((fsd.info_key like 'M%' and fsd.type = 1) OR"
			+" (fsd.info_key like 'X%' and fsd.type = 6) OR"
			+" (fsd.info_key like 'Y%' and fsd.type = 7))";

		String SELECT_MOD311 = "SELECT fsd.type type, fsd.description description, fsd.amount amount"
				+" FROM fs_model fs"
				+" INNER JOIN fs_model_detail fsd ON fsd.fs_model = fs.id"
				+" WHERE fs.domain = ? "
				+" AND fs.year = ?  "
				+" AND fs.model = '311'";
		ArrayList<Mod311Results> result = new ArrayList<Mod311Results>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;
		try {
			stmt1 = conn.prepareStatement(SELECT_ACTIVITY,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt = conn.prepareStatement(SELECT_MOD311,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setInt(1, domain);
			stmt.setInt(2, year);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String description = rs.getString("description");
				Mod311Key key = null;
				String type = rs.getString("type");
				for (Mod311Key k : Mod311Key.values()) {
					if (k.getValue().equals(type)) {
						key = k;
						break;
					}
				}
				if (key != null && ( 
						key.getValue().startsWith(Mod311Key.ACTIVITIES_PREFIX)
						|| key.getValue().startsWith(Mod311Key.FARMING_ACTIVITIES_PREFIX)
						)) {
					Mod311Results r = new Mod311Results();
					String epi = AonUtil.substringBefore(description, "-");
					epi = AonUtil.trim(epi);
					stmt1.setInt(1, domain);
					stmt1.setInt(2, year);
					stmt1.setString(3, epi);
					rs1 = stmt1.executeQuery();
					while (rs1.next()) {
						boolean farmer = rs1.getBoolean("farmer");
						FiscalActivityInfoKey k = FiscalActivityInfoKey.valueOf(rs1.getString("ky"));
						FiscalActivityInfoType t = FiscalActivityInfoType.values()[rs1.getInt("type")];
						int line = rs1.getInt("line");
						double value = rs1.getDouble("value");
						double base = rs1.getDouble("base");
						r.setFarmer(farmer);
						if (!farmer) {
							r.setEpigrafe(epi);
							if (FiscalActivityInfoType.M311_DETAIL == t) {
								if (FiscalActivityInfoKey.X01 == k) {
									r.setBoxC(value);
								} else if (FiscalActivityInfoKey.X00 == k) {
									r.setBoxC(value);
								} else if (FiscalActivityInfoKey.X05 == k) {
									r.setBoxD(value);
								} else if (FiscalActivityInfoKey.X06 == k) {
									r.setBoxE(value);
								} else if (FiscalActivityInfoKey.X07 == k) {
									r.setBoxF(value);
								} else if (FiscalActivityInfoKey.X08 == k) {
									r.setBoxG(value);
								} else if (FiscalActivityInfoKey.X09 == k) {
									r.setBoxH(value);
								} else if (FiscalActivityInfoKey.X10 == k) {
									r.setBoxI(value);
								} else if (FiscalActivityInfoKey.X11 == k) {
									r.setBoxJ(value);
								}
							} else {
								if (line == 1) {
									r.setUnit1(value);
									r.setAmount1(base);
								} else if (line == 2) {
									r.setUnit2(value);
									r.setAmount2(base);
								} else if (line == 3) {
									r.setUnit3(value);
									r.setAmount3(base);
								} else if (line == 4) {
									r.setUnit4(value);
									r.setAmount4(base);
								} else if (line == 5) {
									r.setUnit5(value);
									r.setAmount5(base);
								} else if (line == 6) {
									r.setUnit6(value);
									r.setAmount6(base);
								} else if (line == 7) {
									r.setUnit7(value);
									r.setAmount7(base);
								}
							}
						} else {
							r.setCodigo(epi);
							if (FiscalActivityInfoKey.Y01 == k) {
								r.setIncomes(value);
							} else if (FiscalActivityInfoKey.Y02 == k) {
								r.setQuotaIndex(value);
							} else if (FiscalActivityInfoKey.Y03 == k) {
								r.setAccrualQuota(value);
							} else if (FiscalActivityInfoKey.Y07 == k) {
								r.setInputQuotas(value);
							} else if (FiscalActivityInfoKey.Y08 == k) {
								r.setQuota(value);
							}
						}
					}
					rs1.close();
					result.add(r);
				}
			}
			return result;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e);
		} finally {
			SQLUtils.closeQuietly(rs);
			SQLUtils.closeQuietly(stmt);
			SQLUtils.closeQuietly(rs1);
			SQLUtils.closeQuietly(stmt1);
		}
	}
	
}
 