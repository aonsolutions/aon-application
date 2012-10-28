package com.code.aon.ui.fiscal.controller.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.model.MOD303.MOD303Format;
import com.code.aon.fiscal.FiscalBatch;
import com.code.aon.fiscal.FiscalBatchDetail;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.fiscal.file.MOD303Writer;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod303BatchModel extends AbstractFiscalBatchModel  {

	private static final String SELECT = "SELECT" 
	    +" vatdec.id " + ID
	    +",vat.year " + YEAR
		+",vat.period " + PERIOD
		+",vat.replacement " + REPLACEMENT
		+",vat.complementary " + COMPLEMENTARY
		+",vat.domain " + DOMAIN
		+",r.name " + COMPANY
		+",IF((vatdec.compensate>vatdec.deposit),ROUND(vatdec.compensate*(-1),2),ROUND(vatdec.deposit,2)) " + RESULT
		+",IF((vatdec.compensate=0),"
		+"	IF((vatdec.compensable = 1),'A Compensar','A Devolver')"
		+"	,'A Ingresar') " + DESCRIPTION
		+" FROM fs_vat vat"
		+" INNER JOIN domain dom on vat.domain = dom.id" 
		+"  AND (dom.id = ? or dom.parent = ?)"
		+" INNER JOIN fs_vat_declaration vatdec on vatdec.fs_vat = vat.id" 
		+"  AND vatdec.administration = ? "
		+"  AND vatdec.status != 2"
		+" INNER JOIN company c on c.domain = vat.domain"
		+" INNER JOIN registry r on r.id = c.registry"
		+" WHERE vat.year = ?"
		+"  AND vat.period = ?"
		+"  AND vat.security_level = ?"
		+"  AND vatdec.id NOT IN ( SELECT detail_id FROM fs_batch_detail fbd WHERE fbd.fs_batch = ?)"
	;
	
	
	@Override
	public List<Batchable> getPendingList(FiscalBatch fiscalBatch) throws AonException {
		String sessionFactoryName =HibernateUtil.getSessionFactoryName(FiscalBatch.class.getName());
		Connection c = HibernateUtil.getSQLConnection(sessionFactoryName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1, DomainManager.getCurrentDomain());
			ps.setInt(2, DomainManager.getCurrentDomain());
			ps.setInt(3, fiscalBatch.getAdministration().ordinal());
			ps.setInt(4, fiscalBatch.getYear());
			ps.setInt(5, fiscalBatch.getPeriod().ordinal());
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
		}
	}


	@Override
	public void batch(FiscalBatchDetail detail) throws AonException {
		updateStatus(detail.getDetailId(), VatTaxDeclarationStatus.BATCHED);
	}
	@Override
	public void unbatch(FiscalBatchDetail detail) throws AonException {
		updateStatus(detail.getDetailId(), VatTaxDeclarationStatus.PENDING);
	}

	private void updateStatus(Integer detailId, VatTaxDeclarationStatus status) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
		VatTaxDeclaration dec = (VatTaxDeclaration) bean.get(detailId);
		dec.setStatus(status);
		bean.update(dec);
	}
	
	@Override
	public byte[] getData(FiscalBatch fb) throws AonException{
		MOD303Writer mod303Writer = new MOD303Writer();
		int year = fb.getYear();
		if (year < 2010) {
			String msg = "No se permite la generación de archivos para declaraciones anteriores al ejercicio 2010."; 
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		if (fb.getPeriod() == Period.YEAR) {
			String msg = "La generación de archivos para declaraciones anuales del ejercicio " + year + " no está aún implementada."; 
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		MOD303Format format = null;
		for (MOD303Format f : MOD303Format.values()) {
			if (f.getAdministration() ==  fb.getAdministration() && year >= f.getYear()) {
				format= f;
				break;
			}
		}
		if (format == null) { 
			String msg = "La generación de archivos para la administracion "+ fb.getAdministration() +" no está aún implementada."; 
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		List<VatTaxDeclaration> declarations = new LinkedList<VatTaxDeclaration>();
		IManagerBean decBean = BeanManager.getManagerBean(VatTaxDeclaration.class);
		IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FISCAL_BATCH_DETAIL_FISCAL_BATCH_ID), fb.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalBatchDetail detail = (FiscalBatchDetail) to;
			declarations.add( (VatTaxDeclaration) decBean.get(detail.getDetailId()));			
		}
		FileOutput fileOutput = mod303Writer.createMOD303(declarations,format);
        if (fileOutput != null && fileOutput.getErrors().size() > 0) {
    		AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
    		AonUtil.addErrorMessage("");
    		int i = 0;
    		for (Exception ex:fileOutput.getErrors()) {
    			AonUtil.addErrorMessage(++i + ") " + ex.getLocalizedMessage());
    		}
        }
		return fileOutput.getContent();
	}

}
