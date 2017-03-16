package com.esferalia.aon.ui.pms.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.project.ProjectAttachment;
import com.code.aon.project.enumeration.ProjectAttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.IReservationConstants;
import com.esferalia.aon.ui.pms.controller.ProjectReservationController;

public class ProjectReservationSearchListener extends ControllerSearchListener implements IReservationConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel hotel;
	private Date creationDateFrom;
	private Date creationDateTo;
	private Date insideDateFrom;
	private Date insideDateTo;
	private Customer agency;
	private Seller seller;
	private ReservationCheckStatus[] reservationCheckStatuses;
	private ReservationStatus[] reservationStatuses;
	private Integer conexFlowOperation;

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getCreationDateFrom() {
		return creationDateFrom;
	}

	public void setCreationDateFrom(Date creationDateFrom) {
		this.creationDateFrom = creationDateFrom;
	}

	public Date getCreationDateTo() {
		return creationDateTo;
	}

	public void setCreationDateTo(Date creationDateTo) {
		this.creationDateTo = creationDateTo;
	}

	public Date getInsideDateFrom() {
		return insideDateFrom;
	}

	public void setInsideDateFrom(Date insideDateFrom) {
		this.insideDateFrom = insideDateFrom;
	}

	public Date getInsideDateTo() {
		return insideDateTo;
	}

	public void setInsideDateTo(Date insideDateTo) {
		this.insideDateTo = insideDateTo;
	}

	public Customer getAgency() {
		return agency;
	}

	public void setAgency(Customer agency) {
		this.agency = agency;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public ReservationCheckStatus[] getReservationCheckStatuses() {
		return reservationCheckStatuses;
	}

	public void setReservationCheckStatuses(ReservationCheckStatus[] reservationCheckStatuses) {
		this.reservationCheckStatuses = reservationCheckStatuses;
	}
	
	public ReservationStatus[] getReservationStatuses() {
		return reservationStatuses;
	}

	public void setReservationStatuses(ReservationStatus[] reservationStatuses) {
		this.reservationStatuses = reservationStatuses;
	}
	
	public Integer getConexFlowOperation() {
		return conexFlowOperation;
	}

	public void setConexFlowOperation(Integer conexFlowOperation) {
		this.conexFlowOperation = conexFlowOperation;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setCreationDateFrom(null);
		setCreationDateTo(null);
		setInsideDateFrom(null);
		setInsideDateTo(null);
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		setReservationCheckStatuses(null);
		setReservationStatuses(null);
		setConexFlowOperation(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());			
		}
		if (getCreationDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_CREATION_DATE), getCreationDateFrom());
		}
		if (getCreationDateTo() != null) {
			Date creationDateTo = DateUtils.addMilliseconds(getCreationDateTo(), (int)(DateUtils.MILLIS_PER_DAY - DateUtils.MILLIS_PER_SECOND));
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_CREATION_DATE), creationDateTo);
		}
		if (getInsideDateFrom() != null) {
			Date insideDateTo = (getInsideDateTo() != null) ? getInsideDateTo() : getInsideDateFrom();
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), insideDateTo);			
			criteria.addGreaterThanExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE), getInsideDateFrom());			
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_SELLER_ID), getSeller().getId());			
		}
		if (!ArrayUtils.isEmpty(getReservationCheckStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROJECT_RESERVATION_CHECK_STATUS);
			addEnumToCriteria(criteria, status, getReservationCheckStatuses());
		}
		if (!ArrayUtils.isEmpty(getReservationStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROJECT_RESERVATION_STATUS);
			addEnumToCriteria(criteria, status, getReservationStatuses());
		}
		completeScopeCriteria(criteria, getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_RESERVATION_SCOPE_ID));
		completeConexFlowCriteria(criteria);
	}

	private void completeScopeCriteria(Criteria criteria, String alias) throws ManagerBeanException, ExpressionException {
		Expression hotelScopeExp = UserUtils.getInstance().getNullableScopeExpression(alias);
		if (getController() instanceof ProjectReservationController) {
			hotelScopeExp = ExpressionUtilities.getOrExpression(hotelScopeExp, UserUtils.getInstance().getNullableScopeExpression(alias));
		}
		criteria.addExpression(hotelScopeExp);
	}

	@SuppressWarnings("unchecked")
	private void completeConexFlowCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getConexFlowOperation() != null) {
			if (getConexFlowOperation().intValue() == 0) {
				criteria.addNullExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_TOKEN));
			} else {
				criteria.addEqualExpression(getController().resolveAlias("ProjectReservation.attachments.attachType"), ProjectAttachmentType.CONEXFLOW);

				IManagerBean pAttachBean = BeanManager.getManagerBean(ProjectAttachment.class);
				Projection prjReservation = Projection.property(getFieldName(IEntityAlias.PROJECT_RESERVATION_ID));
				List<Integer> reservationIds = getController().getManagerBean().getList(new ProjectionList(prjReservation), criteria);
				Criteria pAttachCriteria = new Criteria();
				pAttachCriteria.addInExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID), reservationIds);
				pAttachCriteria.addEqualExpression(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ATTACH_TYPE), ProjectAttachmentType.CONEXFLOW);
				Projection prjAttach = Projection.max(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_ID));
				Projection prjGroup = Projection.group(pAttachBean.getFieldName(IEntityAlias.PROJECT_ATTACHMENT_PROJECT_ID));
				List<Integer> attachIds = new LinkedList<Integer>();
				for (Object id : pAttachBean.getList(new ProjectionList(prjAttach, prjGroup), pAttachCriteria)) {
					Object[] obj = (Object[])id;
					attachIds.add((Integer)obj[0]);
				}

				criteria.addInExpression(getController().resolveAlias("ProjectReservation.attachments.id"), attachIds);
				String alias = getController().resolveAlias("ProjectReservation.attachments.description");
				switch (getConexFlowOperation().intValue()) {
					case 1: Expression exp1 = ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_OK_PATTERN);
							Expression exp2 = ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_CHECK_OK_PATTERN);
							criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
							break;
					case 2: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_PREAUTH_FAIL_PATTERN));
							break;
					case 3: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_CONFIRM_OK_PATTERN));
							break;
					case 4: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_CONFIRM_FAIL_PATTERN));
							break;
					case 5: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_SALE_OK_PATTERN));
							break;
					case 6: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_SALE_FAIL_PATTERN));
							break;
					case 7: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_REFUND_OK_PATTERN));
							break;
					case 8: criteria.addExpression(ExpressionUtilities.getLikeExpression(alias, CONEXFLOW_REFUND_FAIL_PATTERN));
							break;
				}
			}
		}
	}

}