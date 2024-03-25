package com.code.aon.ui.registry.controller;

import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;
import org.jooq.AggregateFunction;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;

public class RegistryDirStaffLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryDirStaffLinesController.class);

	private double totalPercentShare;
	private double totalShareNumber;
	private double totalNominalValue;
	
	
	public double getTotalPercentShare() {
		return totalPercentShare;
	}
	public void setTotalPercentShare(double totalPercentShare) {
		this.totalPercentShare = totalPercentShare;
	}

	public double getTotalShareNumber() {
		return totalShareNumber;
	}
	public void setTotalShareNumber(double totalShareNumber) {
		this.totalShareNumber = totalShareNumber;
	}

	public double getTotalNominalValue() {
		return totalNominalValue;
	}
	public void setTotalNominalValue(double totalNominalValue) {
		this.totalNominalValue = totalNominalValue;
	}
	
	public void refreshTotals() {
		String domainName = AonUtil.getDomainName();
		String user = AonUtil.getRemoteUser();
		ITransferObject masterTo = getMasterController().getTo();
		if (masterTo instanceof IRegistry) {
			IRegistry master = (IRegistry) masterTo;
			if (master.getRegistry() != null) {
				Integer id =  master.getRegistry().getId();
				Integer domain =  master.getRegistry().getDomain();
				if (id != null && domain != null) {
					try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domain, user)) {
						AggregateFunction<BigDecimal> percentShare = DSL.sum( RDIR_STAFF.PERCENT_SHARE );
						AggregateFunction<BigDecimal> shareNumber = DSL.sum( RDIR_STAFF.SHARE_NUMBER );
						AggregateFunction<BigDecimal> nominalValue = DSL.sum( RDIR_STAFF.NOMINAL_VALUE );
						ctx.getDslContext()
							.select(percentShare,shareNumber,nominalValue)
							.from(RDIR_STAFF)
							.where(RDIR_STAFF.DOMAIN.eq(domain))
							.and(RDIR_STAFF.REGISTRY.eq(id))
							.limit(1)
							.fetch()
							.stream()
							.forEach( r -> {
								setTotalPercentShare( Optional.ofNullable(r.getValue( percentShare )).map( bd -> bd.doubleValue()).orElse( Double.valueOf(0.0)));
								setTotalShareNumber( Optional.ofNullable(r.getValue( shareNumber )).map( bd -> bd.doubleValue()).orElse( Double.valueOf(0.0)));
								setTotalNominalValue( Optional.ofNullable(r.getValue( nominalValue )).map( bd -> bd.doubleValue()).orElse( Double.valueOf(0.0)));
							});
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public double _getTotalPercentShare() throws ManagerBeanException {
		double total = 0.0;
		List<RegistryDirStaff> list = (List<RegistryDirStaff>) getModel().getWrappedData();
		System.out.println( "getTotalPercentShare list ...: " + list.size() );
		for (RegistryDirStaff rDirStaff: list) {
			total += rDirStaff.getPercentShare();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public int _getTotalShareNumber() throws ManagerBeanException{
		int total = 0;
		List<RegistryDirStaff> list = (List<RegistryDirStaff>) getModel().getWrappedData();
		System.out.println( "getTotalShareNumber list ...: " + list.size() );
		for (RegistryDirStaff rDirStaff: list) {
			total += rDirStaff.getShareNumber();
		}
		return total;
	}
	
	@SuppressWarnings("unchecked")
	public double _getTotalNominalValue() throws ManagerBeanException{
		double total = 0.0;
		List<RegistryDirStaff> list = (List<RegistryDirStaff>) getModel().getWrappedData();
		System.out.println( "getTotalNominalValue list ...: " + list.size() );
		for (RegistryDirStaff rDirStaff: list) {
			total += rDirStaff.getNominalValue();
		}
		return total;
	}

	public void onChangeShareHolder( ValueChangeEvent event) {
		RegistryDirStaff rds = (RegistryDirStaff) getTo();
		rds.setNominalValue(0.0);
		rds.setPercentShare(0.0);
		rds.setShareNumber(0);
	}

	public void onDocumentChange( ActionEvent event) {
		try {
			RegistryDirStaff rds = (RegistryDirStaff) getTo();
			String document = rds.getDocument();
			if (StringUtils.isEmpty(rds.getName())) {
				String docAlias = getManagerBean().getFieldName(IEntityAlias.REGISTRY_DIR_STAFF_DOCUMENT);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(docAlias, document);
				List<ITransferObject> list = getManagerBean().getList(criteria);
				if (list != null && list.size() > 0 ) {
					RegistryDirStaff r = (RegistryDirStaff) list.get(0);
					rds.setName( r.getName() );
				} else {
					IManagerBean rBean = BeanManager.getManagerBean(Registry.class);
					docAlias = rBean.getFieldName(IEntityAlias.REGISTRY_DOCUMENT);
					criteria = new Criteria();
					criteria.addEqualExpression(docAlias, document);
					list = rBean.getList(criteria);
					if (list != null && list.size() > 0 ) {
						Registry registry = (Registry) list.get(0);
						rds.setName( registry.getFullName() );	
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(),e);			
		}
		
		
	}
	
}
