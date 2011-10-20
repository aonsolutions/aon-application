package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfRegularization;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;

public abstract class AbstractIrpfController implements IIrpfController {

	private IrpfRegularization irpfRegularization;
	
	
	public abstract IrpfResult getIrpfResult();
	
	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getBirthYear()
	 */
	@Override
	public Integer getBirthYear() {
		Person person = getPerson();
		return CommonUtil.getYear(person.getBirthDate());
	}
	
	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDocument()
	 */
	@Override
	public String getDocument() {
		return getRegistry().getDocument();
	}
	
	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getEffectiveDate()
	 */
	@Override
	public Date getEffectiveDate() {
		return getIrpfResult().getEffectiveDate();
	}
	
	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getEffectiveYear()
	 */
	@Override
	public Integer getEffectiveYear() {
		return CommonUtil.getYear(getEffectiveDate());
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getBaseIrpf()
	 */
	@Override
	public Double getBaseIrpf() {
		return getDisplay( getIrpfResult().getBaseIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getMinimunPersonalFamily()
	 */
	@Override
	public Double getMinimunPersonalFamily() {
		return getDisplay( getIrpfResult().getMinimunPersonalFamily() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDeductHomeLoanAmount()
	 */
	@Override
	public Double getDeductHomeLoanAmount() {
		return getDisplay( getIrpfResult().getDeductHomeLoanAmount() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDeduct80Bis()
	 */
	@Override
	public Double getDeduct80Bis() {
		return getDisplay( getIrpfResult().getDeduct80Bis() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getIrpf()
	 */
	@Override
	public Double getIrpf() {
		return getDisplay( getIrpfResult().getIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAnnualIrpf()
	 */
	@Override
	public Double getAnnualIrpf() {
		return getDisplay( getIrpfResult().getAnnualIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAnnualRemuneration()
	 */
	@Override
	public Double getAnnualRemuneration() {
		return getDisplay( getIrpfResult().getAnnualRemuneration() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getIrregular18_2Reduction()
	 */
	@Override
	public Double getIrregular18_2Reduction() {
		return getDisplay( getIrpfResult().getIrregular18_2Reduction() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getIrregular18_3Reduction()
	 */
	@Override
	public Double getIrregular18_3Reduction() {
		return getDisplay( getIrpfResult().getIrregular18_3Reduction() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDeducciblesExpenses()
	 */
	@Override
	public Double getDeducciblesExpenses() {
		return getDisplay( getIrpfResult().getDeducciblesExpenses() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getWorkRemunerationReduction()
	 */
	@Override
	public Double getWorkRemunerationReduction() {
		return getDisplay( getIrpfResult().getWorkRemunerationReduction() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getWorkProlongationReduction()
	 */
	@Override
	public Double getWorkProlongationReduction() {
		return getDisplay( getIrpfResult().getWorkProlongationReduction() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getWorkMovingReduction()
	 */
	@Override
	public Double getWorkMovingReduction() {
		return getDisplay( getIrpfResult().getWorkMovingReduction() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getWorkDisabilityReduction()
	 */
	@Override
	public Double getWorkDisabilityReduction() {
		return getDisplay( getIrpfResult().getWorkDisabilityReduction() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getSocialSecurityPensioner()
	 */
	@Override
	public Double getSocialSecurityPensioner() {
		return getDisplay( getIrpfResult().getSocialSecurityPensioner() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getTwoOrMoreDescendentsMin()
	 */
	@Override
	public Double getTwoOrMoreDescendentsMin() {
		return getDisplay( getIrpfResult().getTwoOrMoreDescendentsMin() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getSpousalSupport()
	 */
	@Override
	public Double getSpousalSupport() {
		return getDisplay( getIrpfResult().getSpousalSupport() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getFoodAnnuity()
	 */
	@Override
	public Double getFoodAnnuity() {
		return getDisplay( getIrpfResult().getFoodAnnuity() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getMinimunPersonal()
	 */
	@Override
	public Double getMinimunPersonal() {
		return getDisplay( getIrpfResult().getMinimunPersonal() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getMinimunAscendents()
	 */
	@Override
	public Double getMinimunAscendents() {
		return getDisplay( getIrpfResult().getMinimunAscendents() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getMinimunDescendents()
	 */
	@Override
	public Double getMinimunDescendents() {
		return getDisplay( getIrpfResult().getMinimunDescendents() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getMinimunDisability()
	 */
	@Override
	public Double getMinimunDisability() {
		return getDisplay( getIrpfResult().getMinimunDisability() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsMinor3Total()
	 */
	@Override
	public Integer getDescendentsMinor3Total() {
		return getDisplay( getIrpfResult().getDescendentsMinor3Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsMinor3Entirely()
	 */
	@Override
	public Integer getDescendentsMinor3Entirely() {
		return getDisplay( getIrpfResult().getDescendentsMinor3Entirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsRemainderTotal()
	 */
	@Override
	public Integer getDescendentsRemainderTotal() {
		return getDisplay( getIrpfResult().getDescendentsRemainderTotal() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsRemainderEntirely()
	 */
	@Override
	public Integer getDescendentsRemainderEntirely() {
		return getDisplay( getIrpfResult().getDescendentsRemainderEntirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendents33_65Total()
	 */
	@Override
	public Integer getDescendents33_65Total() {
		return getDisplay( getIrpfResult().getDescendents33_65Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendents33_65Entirely()
	 */
	@Override
	public Integer getDescendents33_65Entirely() {
		return getDisplay( getIrpfResult().getDescendents33_65Entirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsMovingTotal()
	 */
	@Override
	public Integer getDescendentsMovingTotal() {
		return getDisplay( getIrpfResult().getDescendentsMovingTotal() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsMovingEntirely()
	 */
	@Override
	public Integer getDescendentsMovingEntirely() {
		return getDisplay( getIrpfResult().getDescendentsMovingEntirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendents65Total()
	 */
	@Override
	public Integer getDescendents65Total() {
		return getDisplay( getIrpfResult().getDescendents65Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendents65Entirely()
	 */
	@Override
	public Integer getDescendents65Entirely() {
		return getDisplay( getIrpfResult().getDescendents65Entirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsFirst()
	 */
	@Override
	public Integer getDescendentsFirst() {
		return getDisplay( getIrpfResult().getDescendentsFirst() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsSecond()
	 */
	@Override
	public Integer getDescendentsSecond() {
		return getDisplay( getIrpfResult().getDescendentsSecond() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsThird()
	 */
	@Override
	public Integer getDescendentsThird() {
		return getDisplay( getIrpfResult().getDescendentsThird() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsFourthSubsequentTotal()
	 */
	@Override
	public Integer getDescendentsFourthSubsequentTotal() {
		return getDisplay( getIrpfResult().getDescendentsFourthSubsequentTotal() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getDescendentsFourthSubsequentEntirely()
	 */
	@Override
	public Integer getDescendentsFourthSubsequentEntirely() {
		return getDisplay( getIrpfResult().getDescendentsFourthSubsequentEntirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendentsMinor75Total()
	 */
	@Override
	public Integer getAscendentsMinor75Total() {
		return getDisplay( getIrpfResult().getAscendentsMinor75Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendentsMinor75Entirely()
	 */
	@Override
	public Integer getAscendentsMinor75Entirely() {
		return getDisplay( getIrpfResult().getAscendentsMinor75Entirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendentsMayor75Total()
	 */
	@Override
	public Integer getAscendentsMayor75Total() {
		return getDisplay( getIrpfResult().getAscendentsMayor75Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendentsMayor75Entirely()
	 */
	@Override
	public Integer getAscendentsMayor75Entirely() {
		return getDisplay( getIrpfResult().getAscendentsMayor75Entirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendents33_65Total()
	 */
	@Override
	public Integer getAscendents33_65Total() {
		return getDisplay( getIrpfResult().getAscendents33_65Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendents33_65Entirely()
	 */
	@Override
	public Integer getAscendents33_65Entirely() {
		return getDisplay( getIrpfResult().getAscendents33_65Entirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendentsMovingTotal()
	 */
	@Override
	public Integer getAscendentsMovingTotal() {
		return getDisplay( getIrpfResult().getAscendentsMovingTotal() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendentsMovingEntirely()
	 */
	@Override
	public Integer getAscendentsMovingEntirely() {
		return getDisplay( getIrpfResult().getAscendentsMovingEntirely() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendents65Total()
	 */
	@Override
	public Integer getAscendents65Total() {
		return getDisplay( getIrpfResult().getAscendents65Total() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getAscendents65Entirely()
	 */
	@Override
	public Integer getAscendents65Entirely() {
		return getDisplay( getIrpfResult().getAscendents65Entirely() );
	}
	

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPaidIrpf()
	 */
	@Override
	public Double getPaidIrpf() {
		return getDisplay( getIrpfRegularization().getPaidIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getReason()
	 */
	@Override
	public IrpfRegularizationReason getReason() {
		IrpfRegularization irpfRegularization = 
			getIrpfRegularization();
		return irpfRegularization != null ? getIrpfRegularization().getReason(): null;
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPaidRemuneration()
	 */
	@Override
	public Double getPaidRemuneration() {
		return getDisplay( getIrpfRegularization().getPaidRemuneration() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPriorAnnualIrpf()
	 */
	@Override
	public Double getPriorAnnualIrpf() {
		return getDisplay( getIrpfRegularization().getPriorAnnualIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPriorAnnualRemuneration()
	 */
	@Override
	public Double getPriorAnnualRemuneration() {
		return getDisplay( getIrpfRegularization().getPriorAnnualRemuneration() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPriorBaseIrpf()
	 */
	@Override
	public Double getPriorBaseIrpf() {
		return getDisplay( getIrpfRegularization().getPriorBaseIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPriorIrpf()
	 */
	@Override
	public Double getPriorIrpf() {
		return getDisplay( getIrpfRegularization().getPriorIrpf() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPriorMinimunPersonalFamily()
	 */
	@Override
	public Double getPriorMinimunPersonalFamily() {
		return getDisplay( getIrpfRegularization().getPriorMinimunPersonalFamily() );
	}

	/* (non-Javadoc)
	 * @see com.esferalia.aon.ui.payroll.controller.contract.IIrpfController#getPriorDeductHomeLoanAmount()
	 */
	@Override
	public Double getPriorDeductHomeLoanAmount() {
		return getDisplay( getIrpfRegularization().getPriorDeductHomeLoanAmount() );
	}

	private Contract getContract(){
		return getIrpfResult().getContract();
	}
	
	private Person getPerson() {
		return getContract().getPerson();
	}
	
	private Registry getRegistry() {
		return getPerson().getRegistry();
	}
	
	private IrpfRegularization getIrpfRegularization(){
		if ( irpfRegularization == null  ) {
			irpfRegularization = searchIrpfRegularization();
		}
		else if (irpfRegularization.getContract().getId() != getContract().getId() ) {
			irpfRegularization = searchIrpfRegularization();
		} // different contracts 
		else if (irpfRegularization.getEffectiveDate().compareTo(getEffectiveDate()) != 0)  {
			irpfRegularization = searchIrpfRegularization();
		} // different dates
		return ( IrpfRegularization ) irpfRegularization;
	}
	
	private IrpfRegularization searchIrpfRegularization() {
		try {
			Contract contract = getContract();
			IManagerBean bean = BeanManager.getManagerBean(IrpfRegularization.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.IRPF_REGULARIZATION_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE), getEffectiveDate());
			List<ITransferObject> list = bean.getList(criteria);
			return list.isEmpty() ? (IrpfRegularization) null : (IrpfRegularization) list.get(0);
		} catch (ManagerBeanException e) {
			String msg = "Fallo en la obtención de los datos de regularización calculada";
			AonUtil.addErrorMessage(msg);
			return (IrpfRegularization) null ;
		}
	}

	private Integer getDisplay(Integer value) {
		return value != null ? value : 0;
	}

	private Double getDisplay(Double value) {
		return value != null ? value : 0.00;
	}
	

}
