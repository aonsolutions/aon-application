package com.code.aon.ui.fiscal.controller.batch;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
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

public class Mod303BatchModel extends AbstractFiscalBatchModel {

	private static final String SELECT = "SELECT" + " vatdec.id "
			+ ID
			+ ",vat.year "
			+ YEAR
			+ ",vat.period "
			+ PERIOD
			+ ",vat.replacement "
			+ REPLACEMENT
			+ ",vat.complementary "
			+ COMPLEMENTARY
			+ ",vat.domain "
			+ DOMAIN
			+ ",r.name "
			+ COMPANY
			+ ",IF((vatdec.compensate>vatdec.deposit),ROUND(vatdec.compensate*(-1),2),ROUND(vatdec.deposit,2)) "
			+ RESULT
			+ ",IF((vatdec.compensate=0),"
			+ "	IF((vatdec.compensable = 1),'A Compensar','A Devolver')"
			+ "	,'A Ingresar') "
			+ DESCRIPTION
			+ " FROM fs_vat vat"
			+ " INNER JOIN domain dom on vat.domain = dom.id"
			+ "  AND (dom.id = ? or dom.parent = ?)"
			+ " INNER JOIN fs_vat_declaration vatdec on vatdec.fs_vat = vat.id"
			+ "  AND vatdec.administration = ? "
			+ "  AND vatdec.status != 2"
			+ " INNER JOIN company c on c.domain = vat.domain"
			+ " INNER JOIN registry r on r.id = c.registry"
			+ " WHERE vat.year = ?"
			+ "  AND vat.period = ?"
			+ "  AND vat.security_level = ?"
			+ "  AND vatdec.id NOT IN ( SELECT detail_id FROM fs_batch_detail fbd WHERE fbd.fs_batch = ?)";

	@Override
	protected String getSelect() {
		return SELECT;
	}

	@Override
	public void batch(FiscalBatchDetail detail) throws AonException {
		updateStatus(detail.getDetailId(), VatTaxDeclarationStatus.BATCHED);
	}

	@Override
	public void unbatch(FiscalBatchDetail detail) throws AonException {
		updateStatus(detail.getDetailId(), VatTaxDeclarationStatus.PENDING);
	}

	private void updateStatus(Integer detailId, VatTaxDeclarationStatus status)
			throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDeclaration.class);
		VatTaxDeclaration dec = (VatTaxDeclaration) bean.get(detailId);
		dec.setStatus(status);
		bean.update(dec);
	}

	@Override
	public byte[] getData(FiscalBatch fb) throws AonException {
		MOD303Writer mod303Writer = new MOD303Writer();
		int year = fb.getYear();
		if (year < 2010) {
			String msg = "No se permite la generación de archivos para declaraciones anteriores al ejercicio 2010.";
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		if (fb.getPeriod() == Period.YEAR) {
			String msg = "La generación de archivos para declaraciones anuales del ejercicio "
					+ year + " no está aún implementada.";
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		MOD303Format format = null;
		for (MOD303Format f : MOD303Format.values()) {
			if (f.getAdministration() == fb.getAdministration()
					&& year >= f.getYear()) {
				format = f;
				break;
			}
		}
		if (format == null) {
			String msg = "La generación de archivos para la administracion "
					+ fb.getAdministration() + " no está aún implementada.";
			AonUtil.addErrorMessage(msg);
			throw new AonException(msg);
		}
		List<VatTaxDeclaration> declarations = new LinkedList<VatTaxDeclaration>();
		IManagerBean decBean = BeanManager
				.getManagerBean(VatTaxDeclaration.class);
		IManagerBean bean = BeanManager.getManagerBean(FiscalBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				bean.getFieldName(IEntityAlias.FISCAL_BATCH_DETAIL_FISCAL_BATCH_ID),
				fb.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalBatchDetail detail = (FiscalBatchDetail) to;
			declarations.add((VatTaxDeclaration) decBean.get(detail
					.getDetailId()));
		}
		FileOutput fileOutput = mod303Writer.createMOD303(declarations, format);
		if (fileOutput != null && fileOutput.getErrors().size() > 0) {
			AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY,
					IFinanceMessages.FINANCE_BATCH_DISK_ERROR);
			AonUtil.addErrorMessage("");
			int i = 0;
			for (Exception ex : fileOutput.getErrors()) {
				AonUtil.addErrorMessage(++i + ") " + ex.getLocalizedMessage());
			}
		}
		return fileOutput.getContent();
	}

}
