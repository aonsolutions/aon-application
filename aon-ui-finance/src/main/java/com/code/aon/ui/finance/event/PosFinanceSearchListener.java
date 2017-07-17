package com.code.aon.ui.finance.event;

import static com.code.aon.ui.common.ICommonMessages.TIMESTAMP_2_PATTERN;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.finance.Pos;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.Shift;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosFinanceSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private WorkPlace workPlace;
	private Department department;
	private Pos pos;
	private Shift shift;
	private PayMethod payMethod;
	private Date fromDate;
	private Date toDate;
	private Integer[] posShifts;

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}
	
	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Integer[] getPosShifts() {
		return posShifts;
	}
	public void setPosShifts(Integer[] posShifts) {
		this.posShifts = posShifts;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setWorkPlace((WorkPlace)BeanManager.getManagerBean(WorkPlace.class).createNewTo());
		setDepartment((Department)BeanManager.getManagerBean(Department.class).createNewTo());
		setPos((Pos)BeanManager.getManagerBean(Pos.class).createNewTo());
		setShift(null);
		setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).createNewTo());
		setFromDate(DateUtils.truncate(DateUtils.addDays(new Date(), -1), Calendar.DATE));
		setToDate(DateUtils.truncate(new Date(), Calendar.DATE));
		setPosShifts(null);
	}

	public List<SelectItem> getWorkPlacePos() throws ManagerBeanException {
		List<SelectItem> posList = new LinkedList<SelectItem>();
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			IManagerBean posBean = BeanManager.getManagerBean(Pos.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_WORK_PLACE_ID), getWorkPlace().getId());
			if (getDepartment() != null && getDepartment().getId() != null) {
				criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_DEPARTMENT_ID), getDepartment().getId());
			}
			criteria.addEqualExpression(posBean.getFieldName(IEntityAlias.POS_ACTIVE), Boolean.TRUE);
			criteria.addOrder(posBean.getFieldName(IEntityAlias.POS_NAME));
			for (ITransferObject ito : posBean.getList(criteria)) {
				Pos pos = (Pos)ito;
				SelectItem posItem = new SelectItem(pos, pos.getName());
				posList.add(posItem);
			}
		}
		return posList;
	}

	public List<SelectItem> getWorkPlacePosShift() throws ManagerBeanException {
		List<SelectItem> posShiftList = new LinkedList<SelectItem>();
		if (getWorkPlace() != null && getWorkPlace().getId() != null && getPayMethod() != null && getPayMethod().getId() != null && getFromDate() != null) {
			Connection connection = null;
			PreparedStatement queryStmt = null;
			ResultSet queryRs = null;
			try {
				Locale locale = AonUtil.getCurrentLocale();
				DateFormat formatter = new SimpleDateFormat(AonUtil.getMessage(TIMESTAMP_2_PATTERN));
				String scopeList = getScopeList();
				if (scopeList != null) {
					connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
					StringWriter query = new StringWriter();
					query.append("SELECT DISTINCT pos_shift.id, pos_shift.start_time, pos.name, pos_shift.shift");
					query.append(" FROM finance, invoice, pos_shift, pos");
					query.append(" WHERE " + DomainManager.getSQLWhereClause("finance.domain"));
					query.append(" AND finance.invoice = invoice.id");
					query.append(" AND invoice.pos_shift = pos_shift.id");
					query.append(" AND pos_shift.pos = pos.id");
					query.append(" AND finance.payment = 0");
					query.append(" AND finance.status = " + FinanceStatus.PENDING.ordinal());
					query.append(" AND finance.pay_method = " + getPayMethod().getId());
					query.append(" AND finance.scope IN " + scopeList);
					query.append(" AND pos_shift.end_time IS NOT NULL");
					query.append(" AND pos_shift.start_time >= ?");
					if (getToDate() != null) {
						query.append(" AND pos_shift.start_time < ?");
					}
					if (getPos() != null && getPos().getId() != null) {
						query.append(" AND pos.id = " + getPos().getId());
					} else {
						if (getWorkPlace() != null && getWorkPlace().getId() != null) {
							query.append(" AND pos.workplace = " + getWorkPlace().getId());
						}
						if (getDepartment() != null && getDepartment().getId() != null) {
							query.append(" AND pos.department = " + getDepartment().getId());
						}
					}
					if (getShift() != null) {
						query.append(" AND pos_shift.shift = " + getShift().ordinal());
					}
					query.append(" ORDER BY pos_shift.start_time DESC, pos.name, pos_shift.shift");
	
					queryStmt = connection.prepareStatement(query.toString());
					queryStmt.setDate(1, new java.sql.Date(getFromDate().getTime()));
					if (getToDate() != null) {
						queryStmt.setDate(2, new java.sql.Date(DateUtils.addDays(getToDate(), 1).getTime()));
					}
					queryRs = queryStmt.executeQuery();
					while (queryRs.next()) {
						Integer posShiftId = queryRs.getInt(1);
						Date startDate = new Date(queryRs.getTimestamp(2).getTime());
						String posName = queryRs.getString(3);
						String shift = Shift.values()[queryRs.getInt(4)].getName(locale);
	
						SelectItem posShiftItem = new SelectItem(posShiftId, formatter.format(startDate) + " - " + posName + " - " + shift);
						posShiftList.add(posShiftItem);
					}
				}
			} catch (SQLException ex) {
				throw new ManagerBeanException(ex.getMessage(), ex);
			} catch (AonConnectionException ex) {
				throw new ManagerBeanException(ex.getMessage(), ex);
			} finally {
				DatabaseUtil.closeQuietly(queryRs);
				DatabaseUtil.closeQuietly(queryStmt);
				DatabaseUtil.closeQuietly(connection);
			}
		}
		return posShiftList;
	}

	private String getScopeList() {
		StringBuilder scopeList = new StringBuilder();
		for (Integer scopeId : UserUtils.getInstance().getCurrentUserScopeIds()) {
			scopeList.append(scopeId + ",");
		}
		if (scopeList.length() > 0) {
			scopeList.insert(0, "(");
			scopeList.replace(scopeList.lastIndexOf(","), scopeList.lastIndexOf(",") + 1, ")");
			return scopeList.toString();
		}
		return null;
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		criteria.addNotNullExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_END_TIME));
		if (getPayMethod() != null && getPayMethod().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), getPayMethod().getId());
		}
		if (getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_START_TIME), getFromDate());
		}
		if (getToDate() != null) {
			criteria.addLessThanExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_START_TIME), DateUtils.addDays(getToDate(), 1));
		}
		if (ArrayUtils.nullToEmpty(getPosShifts()).length > 0) {
			criteria.addInExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_ID), ArrayUtils.nullToEmpty(getPosShifts()));
		} else {
			if (getPos() != null && getPos().getId() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_POS_ID), getPos().getId());
			} else {
				if (getWorkPlace() != null && getWorkPlace().getId() != null) {
					criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_POS_WORK_PLACE_ID), getWorkPlace().getId());
				}
				if (getDepartment() != null && getDepartment().getId() != null) {
					criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_POS_DEPARTMENT_ID), getDepartment().getId());
				}
			}
			if (getShift() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_POS_SHIFT_SHIFT), getShift());
			}
		}
	}

}