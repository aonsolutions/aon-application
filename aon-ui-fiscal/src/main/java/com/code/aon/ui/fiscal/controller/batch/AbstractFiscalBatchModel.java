package com.code.aon.ui.fiscal.controller.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.hibernate.Session;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.fiscal.FiscalBatchDetail;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class AbstractFiscalBatchModel implements IFiscalBatchModel {

	protected static final String DOMAIN = "domain";
	protected static final String ID = "id";
	protected static final String YEAR = "year";
	protected static final String PERIOD = "period";
	protected static final String REPLACEMENT = "replacement";
	protected static final String COMPLEMENTARY = "complementary";
	protected static final String COMPANY = "company";
	protected static final String RESULT = "result";
	protected static final String DESCRIPTION = "description";


	@Override
	public List<Batchable> getDetailsList(FiscalBatch fiscalBatch) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_BATCH_DETAIL_FISCAL_BATCH_ID), fiscalBatch.getId());
		List<ITransferObject> list  = bean.getList(criteria);
		List<Batchable> details = new LinkedList<Batchable>(); 
		for (ITransferObject to : list) {
			FiscalBatchDetail detail = (FiscalBatchDetail) to;
			Batchable b = new Batchable();
			b.setId(detail.getId());
			b.setDomain(detail.getChildDomain());
			b.setCompany(detail.getCompany());
			b.setResult(detail.getResult());
			b.setComplementary(detail.isComplementary());
			b.setReplacement(detail.isReplacement());
			b.setDescription(detail.getDescription());
			details.add(b);
		}
		return details;
	}

	@Override
	public List<Batchable> getPendingList(FiscalBatch fiscalBatch) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sessionName = HibernateUtil.getSessionFactoryName(FiscalModel.class.getName());
		Connection conn = null;
		Session session = null;
		try {
			session = HibernateUtil.getSession(sessionName);
			conn = session.connection();
			ps = conn.prepareStatement(getSelect(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, DomainManager.getCurrentDomain());
			ps.setInt(2, DomainManager.getCurrentDomain());
			ps.setInt(3, fiscalBatch.getYear());
			ps.setInt(4, fiscalBatch.getPeriod().ordinal());
			ps.setInt(5, fiscalBatch.getAdministration().ordinal());
			ps.setInt(6, fiscalBatch.getSecurityLevel().ordinal());
			ps.setInt(7, fiscalBatch.getId());
			rs = ps.executeQuery();
			List<Batchable> list = new LinkedList<Batchable>();
			while (rs.next()) {
				Batchable b = new Batchable();
				b.setDetailId(rs.getInt(ID));
				b.setCompany(rs.getString(COMPANY));
				b.setResult(rs.getDouble(RESULT));
				b.setDomain(rs.getInt(DOMAIN));
				b.setComplementary(rs.getBoolean(COMPLEMENTARY));
				b.setReplacement(rs.getBoolean(REPLACEMENT));
				b.setDescription(rs.getString(DESCRIPTION));
				list.add(b);
			}
			return list;
		} catch (SQLException e ) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(ps);
			DbUtils.closeQuietly(rs);
			if (HibernateUtil.mustCloseSession()) {
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
				HibernateUtil.closeSession(sessionName);
			}
		}
	}
	
	protected abstract String getSelect();
}
