package com.code.aon.ui.project.controller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ui.form.BasicController;

public class TaskMonitorController extends BasicController implements ITaskController {

	private static final Logger LOGGER = Logger.getLogger(TaskMonitorController.class.getName());

	private Customer customer;
	private Date startDateFrom;
	private Date startDateTo;
	private Date endDateFrom;
	private Date endDateTo;
	private Date dueDateFrom;
	private Date dueDateTo;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		startDateFrom = null;
		startDateTo = null;
		endDateFrom = null;
		endDateTo = null;
		dueDateFrom = null;
		dueDateTo = null;
	}

	public void addStartDateFromExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getStartDateFrom() != null) {
			try {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_START_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM start date expression", e);
			}
		}
	}

	public void addStartDateToExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getStartDateTo() != null) {
			try {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_START_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO start date expression", e);
			}
		}
	}

	public void addEndDateFromExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getEndDateFrom() != null) {
			try {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_END_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM end date expression", e);
			}
		}
	}

	public void addEndDateToExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getStartDateTo() != null) {
			try {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_END_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO end date expression", e);
			}
		}
	}

	public void addDueDateFromExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getDueDateFrom() != null) {
			try {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_DUE_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM due date expression", e);
			}
		}
	}

	public void addDueDateToExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && getDueDateTo() != null) {
			try {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_DUE_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO due date expression", e);
			}
		}
	}

	public void addCustomerExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			try {
				getCriteria().addEqualExpression(getFieldName(IProjectAlias.TASK_CUSTOMER_ID),
						new Integer(event.getNewValue().toString()));
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding customer expression", e);
			}
		}
	}

    public void addCustomerPojoExpression(ValueChangeEvent event) {
        if(event.getNewValue() != null && !event.getNewValue().equals("")) {
            try {
            	Customer c = (Customer) event.getNewValue();
                getCriteria().addEqualExpression(getFieldName(IProjectAlias.TASK_CUSTOMER_ID), new Integer(c.getId().toString()));
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding customer expression", e);
            }
        }
    }

    public void addStatusExpression(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			try {
				getCriteria().addEqualExpression(getFieldName(IProjectAlias.TASK_STATUS),
						event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding status expression", e);
			}
		}
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public void setDueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public void setDueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
	}

	public void onSearchTasks(ActionEvent event) {
		if (getStartDateFrom() != null) {
			try {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_START_DATE), getStartDateFrom());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM start date expression", e);
			}
		}
		if (getStartDateTo() != null) {
			try {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_START_DATE), getStartDateTo());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO start date expression", e);
			}
		}
		if (getEndDateFrom() != null) {
			try {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_END_DATE), getEndDateFrom());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM end date expression", e);
			}
		}
		if (getEndDateTo() != null) {
			try {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_END_DATE), getEndDateTo());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO end date expression", e);
			}
		}
		if (getDueDateFrom() != null) {
			try {
				getCriteria().addGreaterThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_DUE_DATE), getDueDateFrom());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM due date expression", e);
			}
		}
		if (getDueDateTo() != null) {
			try {
				getCriteria().addLessThanOrEqualExpression(
						getFieldName(IProjectAlias.TASK_DUE_DATE), getDueDateTo());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO due date expression", e);
			}
		}
		super.onSearch(event);
	}

}