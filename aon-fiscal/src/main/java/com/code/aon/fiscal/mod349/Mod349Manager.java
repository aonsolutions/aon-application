package com.code.aon.fiscal.mod349;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.Mod349;
import com.code.aon.fiscal.Mod349Detail;
import com.code.aon.fiscal.enumeration.Mod349Type;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class Mod349Manager {

	private static final String KEY_ALIAS = "key349";
	private static final String REGISTRY_ALIAS = "registry";
	private static final String DOCUMENT_ALIAS = "document";
	private static final String NAME_ALIAS = "name";
	private static final String COUNTRY_ALIAS = "country";
	private static final String AMOUNT_ALIAS = "amount";
//	private static final String RECTIFICATION_TYPE_ALIAS = "rectification_type";
//	private static final String RECTIFICATION_INVOICE_ALIAS = "rectification_invoice";
	
	public Mod349 generateDetails(Mod349Parameters params) throws ManagerBeanException {
		PreparedStatement declaredPs = null; 
		PreparedStatement ps = null; 
		ResultSet declaredRs = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());
			
			Mod349 mod349 = params.getMod349();
			declaredPs = conn.prepareStatement(getDeclaredSentence(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps = conn.prepareStatement(getMainSentence(params),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearFirstDay(mod349.getYear()).getTime()));
			ps.setDate(++i, new java.sql.Date( mod349.getPeriod().getDueDate(mod349.getYear()).getTime()));
			rs = ps.executeQuery();
			IManagerBean bean = BeanManager.getManagerBean(Mod349Detail.class);
			while (rs.next()) {
				Mod349Detail detail = new Mod349Detail();
				detail.setMod349(mod349);
				String type = rs.getString( KEY_ALIAS );
				detail.setType(Mod349Type.valueOf(type));
				detail.setDocument(rs.getString( DOCUMENT_ALIAS ));
				detail.setRegistry(rs.getInt( REGISTRY_ALIAS ));
				detail.setName(rs.getString(NAME_ALIAS));
				Country country = Country.valueOf( rs.getString(COUNTRY_ALIAS) ); 
				detail.setCountry( country );
				
				detail.setAccumulated(CommonUtil.round(rs.getDouble(AMOUNT_ALIAS)));
				
				declaredPs.setInt(1, mod349.getDomain());
				declaredPs.setInt(2, mod349.getYear());
				declaredPs.setInt(3, mod349.getPeriod().ordinal() );
				declaredPs.setInt(4, detail.isRectification()?1:0 );
				declaredPs.setString(5, detail.getType().getValue() );
				declaredPs.setString(6, detail.getCountry().getValue() );
				declaredPs.setString(7, detail.getDocument() );
				declaredRs = declaredPs.executeQuery();
				if (declaredRs.next()) {
					detail.setDeclared(CommonUtil.round(declaredRs.getDouble(AMOUNT_ALIAS)));	
				}
				declaredRs.close();
				detail.setAmount( CommonUtil.round(detail.getAccumulated() - detail.getDeclared() ));
				detail.setRectifiedAmount(0.0);
				if (CommonUtil.round(detail.getAmount()) != 0.0) {
					bean.insert(detail);	
				}
			}
			return mod349;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(declaredRs);
			DatabaseUtil.closeQuietly(declaredPs);
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private String getMainSentence(Mod349Parameters params) {
		// 
		// S --> Ventas de servicios
		// E --> Resto de ventas
		// I --> Gastos
		// A --> Compras
		// 
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT ELT(i.type+1, 'A', IF(i.service=1,'S','E'), 'I', 'I') ");
		buf.append(KEY_ALIAS);
		buf.append(",i.registry ");
		buf.append(REGISTRY_ALIAS);
		buf.append(",r.document ");
		buf.append(DOCUMENT_ALIAS);
		buf.append(",MIN(i.rname) ");
		buf.append(NAME_ALIAS);
		buf.append(",r.document_country ");
		buf.append(COUNTRY_ALIAS);
		buf.append(",SUM( i.taxable_base ) ");
		buf.append(AMOUNT_ALIAS);
		buf.append(" FROM invoice i ");
		buf.append(" INNER JOIN registry r ON (r.id = i.registry) ");
		buf.append(" WHERE i.transaction=1"); // InvoiceTransactionType.INTRACOMMUNITY
		buf.append(" AND " + DomainManager.getSQLWhereClause("i.domain"));
		buf.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") +" BETWEEN ? AND ?");
		buf.append(" GROUP BY ");
		buf.append(KEY_ALIAS);
		if (!params.isGroupedByNIF() ) {
			buf.append(",");
			buf.append(REGISTRY_ALIAS);	
		}
		buf.append(",");
		buf.append(COUNTRY_ALIAS);
		buf.append(",");
		buf.append(DOCUMENT_ALIAS);
		buf.append(" ORDER BY key349,name,amount desc");
		return buf.toString();
	}

	private String getDeclaredSentence() {
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT SUM(det.amount) amount");
		buf.append(" FROM fs_mod349_detail det");  
		buf.append(" INNER JOIN fs_mod349 cab ON (cab.id = det.fs_mod349)");  
		buf.append(" WHERE cab.domain  = ?");
		buf.append(" AND cab.year = ?");
		buf.append(" AND cab.period < ?");
		buf.append(" AND det.rectification =  ?");
		buf.append(" AND det.type =  ?");
		buf.append(" AND det.country =  ?");
		buf.append(" AND det.document =  ?");
		return buf.toString();		
	}

}




