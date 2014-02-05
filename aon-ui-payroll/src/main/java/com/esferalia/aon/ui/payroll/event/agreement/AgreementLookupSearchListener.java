package com.esferalia.aon.ui.payroll.event.agreement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AgreementLookupSearchListener extends ControllerSearchListener {

	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		Integer[] ids = {0, DomainManager.getCurrentDomain(), getParentDomainId()};
		criteria.setSkipDomainFilter( true );
		criteria.addInExpression(this.getFieldName(IEntityAlias.AGREEMENT_DOMAIN), ids);
	}
	
	private Integer getParentDomainId() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT parent FROM domain WHERE id = " + DomainManager.getCurrentDomain();
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}

	
}