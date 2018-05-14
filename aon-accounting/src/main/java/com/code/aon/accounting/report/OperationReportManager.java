package com.code.aon.accounting.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.jooq.tools.StringUtils;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.ManagerBeanException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class OperationReportManager {

	public List<OperationReport> getReport(OperationReportParams params) throws ManagerBeanException{
		String select = " SELECT ae.id"
				+", ae.entry_date entry_date "
				+", aed.concept concept "
				+", a.description account "
				+ (params.isExpenses()?",SUM(aed.debit-aed.credit)":",SUM(aed.credit-aed.debit)")
				+", IF (@lastEntry = ae.id,NULL,CONCAT(ELT((i.type+1),'R','E','R','G'),'-',i.series,'/',LPAD(i.number,6,'0'))) docNumber "
				+", IF (@lastEntry = ae.id,NULL,i.reference_code ) reference_code "
				+", IF (@lastEntry = ae.id,NULL,i.rdocument ) rdocument "
				+", IF (@lastEntry = ae.id,NULL,i.rname ) rname "
				+", i.id invocice "
				+", @lastEntry := ae.id "
				+"FROM account_entry ae "
				+"INNER JOIN ( select @lastEntry := 0) SQLVars "
				+"INNER JOIN account_entry_detail aed on aed.account_entry = ae.id "
				+"INNER JOIN account a on aed.account = a.id "
				+"LEFT OUTER JOIN account_entry_invoice aei on aei.account_entry = ae.id "
				+"LEFT OUTER JOIN invoice i on aei.invoice = i.id "
				+"WHERE ae.domain = ? "
				+"AND ae.entry_date between ? and ? "
//				+"AND ae.entry_type != " + AccountEntryType.OPERATING.ordinal()
//				+"AND a.code like ? "
				+"AND ("
					+"(ae.entry_type IN ("+AccountEntryType.SALES_INVOICE.ordinal() 
								+ "," + AccountEntryType.PURCHASE_INVOICE.ordinal()	
								+ "," + AccountEntryType.EXPENSE_INVOICE.ordinal()
								+ "," + AccountEntryType.EXPENSES.ordinal()
					+") AND (a.code like ? || a.code like ?))"
					+" OR (ae.entry_type != "+AccountEntryType.OPERATING.ordinal()+" AND a.code like ?)"
					+") "
				+"GROUP BY ae.id,aed.account "
				+"ORDER BY ae.entry_date,ae.id,aed.id";
		
		System.out.println(select);
		
		String taxSelect = "SELECT " 
				+"ELT(it.tax_type,'IVA','IRPF') taxType "
				+", it.percentage percentage "
				+", SUM(it.base) base "
				+", SUM(IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) )) quota "
				+", it.surcharge surcharge " 
				+", SUM(IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) )) surcharge_quota "
				+" FROM invoice_detail id "
				+" INNER JOIN invoice_tax it on it.invoice_detail = id.id "
				+" WHERE id.invoice = ?";
		if(!params.isIva()){
			taxSelect = taxSelect + " AND it.tax_type <> 1";
		}
		if(!params.isIrpf()){
			taxSelect = taxSelect + " AND it.tax_type <> 2";
		}
		taxSelect = taxSelect +" GROUP BY it.tax_type,it.percentage,it.surcharge"
				+" ORDER BY it.tax_type,it.percentage,it.surcharge";
		
		List<OperationReport> list = new LinkedList<OperationReport>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement taxPs = null;
		ResultSet taxRs = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, params.getDomainId());
			java.sql.Date fromDate = null;
			if (params.getFromDate() == null) {
				fromDate = new java.sql.Date( params.getPeriod().getInitiationDate().getTime());	
			} else {
				fromDate = new java.sql.Date( params.getFromDate().getTime());
			}
			ps.setDate(2, fromDate );
			java.sql.Date toDate = null;
			if (params.getToDate() == null) {
				toDate = new java.sql.Date( params.getPeriod().getDeadline().getTime());	
			} else {
				toDate = new java.sql.Date( params.getToDate().getTime());
			}
			ps.setDate(3, toDate );
			if (params.isExpenses()) {
				ps.setString(4, "6%");
				ps.setString(5, "2%");
				ps.setString(6, "6%");
			} else {
				ps.setString(4, "7%");
				ps.setString(5, "2%");
				ps.setString(6, "7%");
			}
			rs = ps.executeQuery();
			
			taxPs = conn.prepareStatement(taxSelect,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			OperationReport op = null;
			OperationReportTax opt  = null;
			int id = -1;
			int counter = 0;
			LinkedList<OperationReportTax> l = new LinkedList<>();
			while (rs.next()) {
				op = new OperationReport();
				op.setEntryDate(rs.getDate(2));;
				Double taxes = 0.0;
				if (id != rs.getInt(1)) {
					op.setId(++counter);
					id = rs.getInt(1);
					int invoiceId = rs.getInt(10);
					if (!rs.wasNull()) {
						taxPs.setInt(1, invoiceId);
						taxRs = taxPs.executeQuery();
						while (taxRs.next()) {
							if (op.getTaxes() == null) {
								op.setTaxes(new LinkedList<OperationReportTax>());
								op.setIvaTypes(new LinkedList<>());
							}
							opt = new OperationReportTax();
							String tax = taxRs.getString(1);
							opt.setTaxType(tax);
							opt.setPercentage(taxRs.getDouble(2));
							opt.setBase(taxRs.getDouble(3));
							opt.setQuota(taxRs.getDouble(4));
							double surchargePercent = taxRs.getDouble(5);
							if (StringUtils.equals("IVA", tax) && surchargePercent != 0) {
								opt.setSurchargePercentage(surchargePercent);
								opt.setSurchargeQuota(taxRs.getDouble(6));
							}
							
							if(opt.getTaxType().equals("IVA")){
								taxes = taxes + opt.getQuota();
								if(!contains(l, opt.getPercentage())){
									OperationReportTax t = new OperationReportTax();
									t.setPercentage(opt.getPercentage());
									t.setQuota(opt.getQuota());
									l.add(t);
								} else {
									for(Integer i = 0 ; i < l.size();i++){
										if(l.get(i).getPercentage().equals(opt.getPercentage())){
											Double q = l.get(i).getQuota() + (opt.getQuota()!= null ? opt.getQuota():0.0);
											l.get(i).setQuota(q);
										}
									}
								}
							}
							else if(opt.getTaxType().equals("IRPF")){ 
								taxes = taxes - opt.getQuota();
							}
							op.getTaxes().add(opt);
						}
						taxRs.close();
					}
				}
				op.setConcept(rs.getString(3));
				op.setAccount(rs.getString(4));
				op.setBalance(rs.getDouble(5) + taxes);
				op.setDocumentNumber(rs.getString(6));
				op.setReferenceCode(rs.getString(7));
				op.setRdocument(rs.getString(8));
				op.setRname(rs.getString(9));
				op.setIvaTypes(l);
				list.add(op);
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage());
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage());
		} finally {
			DatabaseUtil.closeQuietly(taxRs);
			DatabaseUtil.closeQuietly(taxPs);
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return list;
		
	}
	
	private Boolean contains(LinkedList<OperationReportTax> l, Double p) {
		for (OperationReportTax operationReportTax : l) {
			if(p.equals(operationReportTax.getPercentage())){
				return true;
			}
		}
		return false;
	}

}
