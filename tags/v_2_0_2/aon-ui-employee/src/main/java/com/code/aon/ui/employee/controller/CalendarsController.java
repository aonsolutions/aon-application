/**
 * 
 */
package com.code.aon.ui.employee.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Status;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.shared_tomahawk.util.MessageUtils;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.tree.IEmployeeConstants;
import com.code.aon.common.tree.ITreeNode;
import com.code.aon.common.tree.event.TreeSelectionEvent;
import com.code.aon.common.tree.event.TreeSelectionListener;
import com.code.aon.common.tree.jsf.AbstractTreeModel;
import com.code.aon.common.tree.jsf.DefaultMutableTreeNode;
import com.code.aon.common.tree.jsf.TreeCalendar;
import com.code.aon.common.tree.jsf.TreeCalendarModel;
import com.code.aon.company.Company;
import com.code.aon.company.WorkActivity;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.planner.IEvent;
import com.code.aon.planner.IPlannerListener;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.planner.core.Event;
import com.code.aon.planner.core.SpreadEventException;
import com.code.aon.planner.enumeration.EventStatus;
import com.code.aon.planner.model.CalendarScheduleModel;
import com.code.aon.planner.util.PlannerUtil;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.CompanyUtil;
import com.code.aon.ui.company.controller.WorkActivityController;
import com.code.aon.ui.company.controller.WorkPlaceController;
import com.code.aon.ui.employee.util.Constants;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 30/01/2007
 *
 */
public class CalendarsController implements TreeSelectionListener, IPlannerListener {

	public static final String TREE_CALENDAR_NAME = "treecalendar";
	public static final String CALENDARS_NAME = "calendars";

	private Map<Class, CalendarAction> actions = new HashMap<Class, CalendarAction>();
	private Object selected;
	private String workPlaceSelectedChild = "activity";
	private boolean isDirty;
	private Transfer transfer;

	public static final CalendarsController getCalendarsController() {
	    FacesContext ctx = FacesContext.getCurrentInstance();
	    ValueBinding vb = 
	    	ctx.getApplication().createValueBinding( "#{" + CALENDARS_NAME + "}" );
	    return (CalendarsController) vb.getValue(ctx);
	}

	/**
	 * Construct the Calendar controller.
	 */
	public CalendarsController() {
		TreeCalendar tree = (TreeCalendar) AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
		tree.getTreeModel().addTreeSelectionListener(this);
		actions.put( Company.class, new CompanyCalendarAction() );
		actions.put( WorkPlace.class, new WorkPlaceCalendarAction() );
		actions.put( WorkActivity.class, new WorkActivityCalendarAction() );
		actions.put( Employee.class, new EmployeeCalendarAction() );
	}

	/**
	 * @return the isDirty
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * @param isDirty the isDirty to set
	 */
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	/**
	 * @return the workPlaceSelectedChild
	 */
	public String getWorkPlaceSelectedChild() {
		return workPlaceSelectedChild;
	}
	/**
	 * @param workPlaceSelectedChild the workPlaceSelectedChild to set
	 */
	public void setWorkPlaceSelectedChild(String workPlaceSelectedChild) {
		this.workPlaceSelectedChild = workPlaceSelectedChild;
	}
	/**
	 * @return the name of selected node.
	 */
	public String getName() {
		return this.actions.get( selected.getClass() ).getName();
	}
	/**
	 * @return the transfer
	 */
	public Transfer getTransfer() {
		return transfer;
	}
	/**
	 * @param transfer the transfer to set
	 */
	public void setTransfer(Transfer transfer) {
		this.transfer = transfer;
	}

	/**
	 * Indicate if selected node is a Company.
	 *  
	 * @return
	 */
	public boolean isCompanyNode() {
		return (selected instanceof Company);
	}

	/**
	 * Indicate if selected node is a Working Place.
	 *  
	 * @return
	 */
	public boolean isWorkPlaceNode() {
		return (selected instanceof WorkPlace);
	}

	/**
	 * Indicate if selected node is a Working Activity.
	 *  
	 * @return
	 */
	public boolean isWorkActivityNode() {
		return (selected instanceof WorkActivity);
	}

	/**
	 * Indicate if selected node is an Employee.
	 *  
	 * @return
	 */
	public boolean isEmployeeNode() {
		return (selected instanceof Employee);
	}

	/**
	 * @return the isTransfering
	 */
	public boolean isTransfering() {
		return (transfer != null);
	}
	/**
	 * Load company resources tree.
	 * 
	 * @param event
	 */
	public void onLoad(MenuEvent event) {
		TreeCalendar tree = (TreeCalendar)AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
		if ( isDirty() ) {
			init( tree );
		}
		tree.getTreeModel().setSelectedNode(tree.getTreeModel().getRoot().getIdentifier());
	}

	/**
	 * Show selected node Calendar.
	 * 
	 * @param event
	 */
	public void onSchedule(ActionEvent event) {
		try {
			this.actions.get( selected.getClass() ).onSchedule(event);
		} catch (CalendarException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

	/**
	 * Save working place and its address into selected company and adds it to the tree.
	 * 
	 * @param event
	 */
	public void onAcceptWorkPlace(ActionEvent event) {
		WorkPlaceController wpc = 
			(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
		WorkPlace wp = (WorkPlace) wpc.getTo();
		wp.setDescription( wp.getAddress().getAddress() );
		wpc.accept(event);
		addNode( wp, wp.getDescription(), IEmployeeConstants.BRANCH_TYPE, IEmployeeConstants.WORKPLACE_ICON );
	}

	/**
	 * Save activity or Employee into selected working place and adds it to the tree.
	 * 
	 * @param event
	 */
	public void onAcceptWorkPlaceChild(ActionEvent event) {
		if ( workPlaceSelectedChild.equals( WorkActivityController.MANAGER_BEAN_NAME ) )
			onAcceptActivity( event );
		else
			onAcceptEmployee( event );
		this.workPlaceSelectedChild = WorkActivityController.MANAGER_BEAN_NAME;
	}

	/**
	 * Save activity into selected working place and adds it to the tree.
	 * 
	 * @param event
	 */
	public void onAcceptActivity(ActionEvent event) {
		WorkActivityController wac = 
			(WorkActivityController) AonUtil.getController( WorkActivityController.MANAGER_BEAN_NAME );
		wac.onAccept(event);
		WorkActivity activity = (WorkActivity) wac.getTo();
		addNode( activity, activity.getDescription(), IEmployeeConstants.BRANCH_TYPE, IEmployeeConstants.WORKACTIVITY_ICON );
	}

	/**
	 * Save resource into selected working place and activity, and adds it to the tree.
	 * 
	 * @param event
	 */
	public void onAcceptEmployee(ActionEvent event) {
		EmployeeController ec = 
			(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
		ec.accept( event );
		Employee e = (Employee) ec.getTo();
		if ( ( isWorkPlaceNode() || isWorkActivityNode() ) && e.isActive() ) {
			String name = e.getRegistry().getName() + " " + e.getRegistry().getSurname();
			addNode( e, name, IEmployeeConstants.LEAF_TYPE, IEmployeeConstants.EMPLOYEE_ICON );
		} else {
			TreeCalendar tree = (TreeCalendar)AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
	    	String name = e.getRegistry().getName() + " " + e.getRegistry().getSurname();
			if ( !name.equals( tree.getTreeModel().getSelectedNode().getDescription() ) ) {
				tree.getTreeModel().getSelectedNode().setDescription( name );
			}
			tree.getTreeModel().setSelectedNode( tree.getTreeModel().getSelectedNodeId() );
		}
	}

	/**
	 * Allows the transfer of the selected employee.
	 * 
	 * @param event
	 */
	public void onTransfering(ActionEvent event) {
		transfer = new Transfer();		
	}

	/**
	 * Transfers the employee to the selected working place or working activity.
	 * 
	 * @param event
	 */
	public void onAcceptTransfer(ActionEvent event) {
		try {
			transfer.accept( event );
			TreeCalendar tree = (TreeCalendar) AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
			String[] path = tree.getTreeModel().getPathInformation( tree.getTreeModel().getSelectedNodeId() );
	        TreeNode treeNode = tree.getTreeModel().getNodeById( path[ path.length - 2] );
	        init( tree );
			tree.getTreeModel().setSelectedNode( treeNode.getIdentifier() );
			this.transfer = null;		
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		} catch (CalendarException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		} catch (DAOException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.common.tree.event.TreeSelectionListener#valueChanged(com.code.aon.common.tree.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent event) {
		this.transfer = null;		
		AbstractTreeModel treeModel =(AbstractTreeModel)event.getSource();
		this.selected = treeModel.getSelectedNode().getUserObject();
		try {
			this.actions.get( this.selected.getClass() ).onSelect(null);
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

    /* (non-Javadoc)
	 * @see com.code.aon.planner.IPlannerListener#eventAdded(com.code.aon.planner.IEvent)
	 */
	public void eventAdded(IEvent event) {
		try {
			TreeCalendar tree = (TreeCalendar)AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
			AbstractTreeModel treeModel = tree.getTreeModel();
			ITreeNode node = treeModel.getSelectedNode();
			if ( event.isSpreadable() ) {
				( (Event) event ).setState( EventStatus.SPREAD );
				this.actions.get( this.selected.getClass() ).onSpreadEvent( node, event, IEvent.CREATE );
			}
		} catch (CalendarException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.planner.IPlannerListener#eventRemoved(com.code.aon.planner.IEvent)
	 */
	public void eventRemoved(IEvent event) {
		try {
			TreeCalendar tree = (TreeCalendar)AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
			AbstractTreeModel treeModel = tree.getTreeModel();
			ITreeNode node = treeModel.getSelectedNode();
			if ( event.isSpreadable() ) {
				( (Event) event ).setState( EventStatus.SPREAD );
				this.actions.get( this.selected.getClass() ).onSpreadEvent( node, event, IEvent.DELETE );
			}
		} catch (CalendarException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.planner.IPlannerListener#eventUpdated(com.code.aon.planner.IEvent)
	 */
	public void eventUpdated(IEvent event) {
		try {
			TreeCalendar tree = (TreeCalendar)AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
			AbstractTreeModel treeModel = tree.getTreeModel();
			ITreeNode node = treeModel.getSelectedNode();
			if ( event.isSpreadable() ) {
				( (Event) event ).setState( EventStatus.SPREAD );
				this.actions.get( this.selected.getClass() ).onSpreadEvent( node, event, IEvent.UPDATE );
			}
		} catch (CalendarException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		} catch (ManagerBeanException e) {
			MessageUtils.addMessage( FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

//	Calendar different actions interface.
	protected interface CalendarAction {
		String getName();
		void onSelect(ActionEvent event) throws ManagerBeanException;
		void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException;
		void onSpreadEvent(ITreeNode node, IEvent event, int operation) throws CalendarException, ManagerBeanException;
	}

	protected class CompanyCalendarAction implements CalendarAction {

		public String getName() {
			Company c = (Company) selected;
			return c.getName() + " " + c.getSurname();
		}

		public void onSelect(ActionEvent event) throws ManagerBeanException {
			WorkPlaceController wpc = 
				(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
			wpc.onReset(event);
			RegistryAddress address = wpc.getWorkPlace().getAddress();
			address.setRegistry( (Company) selected );
			address.setAddressType( AddressType.DELEGATION );
		}

		public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
			CompanyController cc = 
				(CompanyController) AonUtil.getController( CompanyController.COMPANY_NAME );
			PlannerController planner = ControllerUtil.getPlannerController();
			CalendarScheduleModel csm = cc.getCalendarScheduleModel();
			planner.initialize( csm, CALENDARS_NAME, true );
		}

		public void onSpreadEvent(ITreeNode node, IEvent event, int operation) 
					throws CalendarException, ManagerBeanException {
			Iterator iter = node.getChildren().iterator();
			while (iter.hasNext()) {
				ITreeNode childNode = (ITreeNode) iter.next();
				WorkPlace wp = (WorkPlace) childNode.getUserObject();
				AonCalendar aonCalendar = CalendarHelper.getCalendar( wp.getCalendar() );
				if ( aonCalendar != null ) {
					spreadEvent( aonCalendar, event, operation, wp.getDescription() );
				}
				actions.get( wp.getClass() ).onSpreadEvent( childNode, event, operation );
			}
		}

	}

	protected class WorkPlaceCalendarAction implements CalendarAction {

		public String getName() {
			return ( (WorkPlace) selected ).getDescription();
		}

		public void onSelect(ActionEvent event) throws ManagerBeanException {
//	Initialize Employee controller.
			EmployeeController ec = 
				(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
			ec.onReset(event);
			ec.setResourceAllowed( false );
			Resource wpr = (Resource) ec.getResource();
			wpr.setWorkPlace( (WorkPlace) selected );
			ec.setWorkActivities( CompanyUtil.findActivities( ( (WorkPlace) selected ).getId() ) );
//	Initialize Working activity controller.
			WorkActivityController wac = 
				(WorkActivityController) AonUtil.getController( WorkActivityController.MANAGER_BEAN_NAME );
			wac.onReset(event);
			WorkActivity activity = (WorkActivity) wac.getTo();
			WorkPlaceController wpc = 
				(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
//	Find tree selected working place node in Working place controller and assigns it to 
//	the new working activity.
			if ( wpc.getModel() != null ) {
				WorkPlace workPlace = (WorkPlace) selected; 
				List l = (List) wpc.getModel().getWrappedData();
				int index = 0;
				Iterator iter = l.iterator();
				while (iter.hasNext()) {
					WorkPlace wp = (WorkPlace) iter.next();
					if ( wp.getId().equals( workPlace.getId() ) ) {
						wpc.getModel().setRowIndex( index );
						activity.setWorkPlace(wp);
						break;
					}
					index++;
				}
				wpc.onSelect(event);
			}
		}

		public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
			WorkPlaceController wpc = 
				(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
			if ( wpc.getModel() != null ) {
				PlannerController planner = ControllerUtil.getPlannerController();
				CalendarScheduleModel csm = wpc.getCalendarScheduleModel( (WorkPlace) selected );
				planner.initialize( csm, CALENDARS_NAME, true );
			}
		}

		public void onSpreadEvent(ITreeNode node, IEvent event, int operation) 
					throws CalendarException, ManagerBeanException {
			Iterator iter = node.getChildren().iterator();
			while (iter.hasNext()) {
				ITreeNode childNode = (ITreeNode) iter.next();
				Object obj = childNode.getUserObject();
				if ( obj instanceof WorkActivity ) {
					WorkActivity wa = (WorkActivity) obj;
					AonCalendar aonCalendar = CalendarHelper.getCalendar( wa.getCalendar() );
					if ( aonCalendar != null ) {
						spreadEvent( aonCalendar, event, operation, wa.getDescription() );
					}
					actions.get( wa.getClass() ).onSpreadEvent( childNode, event, operation );
				} else {
					Employee e = (Employee) obj;
					AonCalendar aonCalendar = CalendarHelper.getCalendar( e.getCalendar() );
					if ( aonCalendar != null ) {
						String name = e.getRegistry().getName() + " " + e.getRegistry().getSurname();
						spreadEvent( aonCalendar, event, operation, name );
					}
				}
			}
		}

	}

	protected class WorkActivityCalendarAction implements CalendarAction {

		public String getName() {
			return ( (WorkActivity) selected ).getDescription();
		}

		public void onSelect(ActionEvent event) throws ManagerBeanException {
			EmployeeController ec = 
				(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
			ec.onReset(event);
			ec.setResourceAllowed( false );
			WorkActivityController wac = 
				(WorkActivityController) AonUtil.getController( WorkActivityController.MANAGER_BEAN_NAME );
			if ( wac.getModel() != null ) {
				WorkActivity activity = (WorkActivity) selected; 
				List l = (List) wac.getModel().getWrappedData();
				int index = 0;
				Iterator iter = l.iterator();
				while (iter.hasNext()) {
					WorkActivity wa = (WorkActivity) iter.next();
					if ( wa.getId().equals( activity.getId() ) ) {
						wac.getModel().setRowIndex( index );
						ec.getResource().setWorkPlace( wa.getWorkPlace() );
						ec.getResource().setWorkActivity( wa );
						ec.setActivityId( wa.getId() );
						ec.setWorkActivities( CompanyUtil.findActivities( wa.getWorkPlace().getId() ) );
						break;
					}
					index++;
				}
				wac.onSelect(event);
			}
		}

		public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
			WorkActivityController wac = 
				(WorkActivityController) AonUtil.getController( WorkActivityController.MANAGER_BEAN_NAME );
			if ( wac.getModel() != null ) {
				PlannerController planner = ControllerUtil.getPlannerController();
				CalendarScheduleModel csm = wac.getCalendarScheduleModel( (WorkActivity) selected );
				planner.initialize( csm, CALENDARS_NAME, true );
			}
		}

		public void onSpreadEvent(ITreeNode node, IEvent event, int operation) 
					throws CalendarException, ManagerBeanException {
			Iterator iter = node.getChildren().iterator();
			while (iter.hasNext()) {
				ITreeNode childNode = (ITreeNode) iter.next();
				Employee e = (Employee) childNode.getUserObject();
				AonCalendar aonCalendar = CalendarHelper.getCalendar( e.getCalendar() );
				if ( aonCalendar != null ) {
					String name = e.getRegistry().getName() + " " + e.getRegistry().getSurname();
					spreadEvent( aonCalendar, event, operation, name );
				}
			}
		}

	}

	protected class EmployeeCalendarAction implements CalendarAction {

		public String getName() {
			Employee e = (Employee) selected;
			return e.getRegistry().getName() + " " + e.getRegistry().getSurname();
		}

		public void onSelect(ActionEvent event) throws ManagerBeanException {
			EmployeeController ec = 
				(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
			ec.onEditSearch(event);
			Employee e = (Employee) selected; 
			String identifier = ec.getFieldName( ICompanyAlias.EMPLOYEE_ID );
			ec.getCriteria().addEqualExpression( identifier, e.getId() );
			ec.onSearch(event);
			ec.onSelect(event);
		}

		public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
			EmployeeController ec = 
				(EmployeeController) AonUtil.getController( EmployeeController.MANAGER_BEAN_NAME );
			ec.onSchedule(event);
			PlannerController planner = ControllerUtil.getPlannerController();
			planner.setOutcome( CALENDARS_NAME );

		}
		
		public void onSpreadEvent(ITreeNode node, IEvent event, int operation) 
					throws CalendarException, ManagerBeanException {
			// Do Nothing.
		}

	}

	/*
	 * Initialize Tree calendar.
	 */
	private void init(TreeCalendar tree) {
		setDirty( false );
		TreeCalendarModel treeModel = (TreeCalendarModel) tree.getTreeModel();
		TreeSelectionListener[] l = treeModel.getTreeSelectionListeners();
        tree.init();
        treeModel = (TreeCalendarModel) tree.getTreeModel();
        for (int i = 0; i < l.length; i++) {
			treeModel.addTreeSelectionListener( l[i] );
		}
	}

	@SuppressWarnings("unchecked")
	private void addNode(Object obj, String description, String type, String icon) {
		TreeCalendar tree = (TreeCalendar)AonUtil.getRegisteredBean( TREE_CALENDAR_NAME );
		ITreeNode selectedNode = tree.getTreeModel().getSelectedNode();
		String id = selectedNode.getIdentifier() + ":" + selectedNode.getChildren().size();
		ITreeNode node = 
			new DefaultMutableTreeNode( obj, type, description, id, false, icon, icon, Constants.EMPTY_STRING );
		tree.getTreeModel().addNode2Map( node );
		selectedNode.getChildren().add( node );
		tree.getTreeModel().setSelectedNode( id );
	}

	/**
	 * Spread the <code>IEvent</code> over the calendar passed by parameter.
	 * 
	 * @param aonCalendar
	 * @param event
	 * @param operation
	 * @param description
	 */
	private void spreadEvent(AonCalendar aonCalendar, IEvent event, int operation, String description) {
		int index = aonCalendar.indexOf( event.getId() );
    	try {
			if ( index > -1 ) {
				VEvent vevent = aonCalendar.getVEvent( index );
	            String status = vevent.getProperties().getProperty( Property.STATUS ).getValue();
	            if ( !status.equals( Status.VEVENT_LOCKED ) ) {
	            		saveSpreadEvent( aonCalendar, event, index, operation );
	            }
			} else {
				if ( aonCalendar.isAddSpreadEventAllowed() ) {
					addSpreadEvent( aonCalendar, event );
				}
			}
		} catch (SpreadEventException e) {
			Object[] obj = { event.getTitle(), description };
			MessageUtils.addMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), obj);
		}
	}

	/**
     * Adds the spread event inside <code>AonCalendar</code>.
	 * 
	 * @param aonCalendar
	 * @param event
	 * @throws SpreadEventException
	 */
    public static final void addSpreadEvent(AonCalendar aonCalendar, IEvent event)
    			throws SpreadEventException {
    	boolean isHoliday = PlannerUtil.isHoliday( event.getCategory().name() );
		if ( event.getCategory().equals( EventCategory.WORK ) ) { 
			if ( PlannerUtil.validate(aonCalendar, event) ) {
		    	aonCalendar.getCalendar().getComponents().add(event.getComponent());
				if ( isHoliday ) {
		        	PlannerUtil.addExDate( aonCalendar, event );
		        }
				PlannerUtil.addExDates(aonCalendar, event);
			} else {
				String msg = 
					ControllerUtil.getPlannerBundle().getString("aon_planner_workevent_spread_exception");
				throw new SpreadEventException( msg );
			}
		} else {
	    	aonCalendar.getCalendar().getComponents().add(event.getComponent());
			if ( isHoliday ) {
	        	PlannerUtil.addExDate( aonCalendar, event );
	        }
		}
		ControllerUtil.getCalendarManagerBean().updateCalendar( aonCalendar );
    }

    /**
     * Updates or Removes the spread event inside <code>AonCalendar</code>.
     * 
     * @param aonCalendar
     * @param event
     * @param index
     * @param operation
	 * @throws SpreadEventException
     */
    public static final void saveSpreadEvent(AonCalendar aonCalendar, IEvent event, int index, int operation)
    				throws SpreadEventException {
    	boolean isHoliday = PlannerUtil.isHoliday( event.getCategory().name() );
		if ( event.getCategory().equals( EventCategory.WORK ) ) { 
			if ( PlannerUtil.validate(aonCalendar, event) ) {
		    	if ( operation == IEvent.UPDATE) {
		    		VEvent oldVEvent = (VEvent) aonCalendar.updateEvent( index, (VEvent) event.getComponent() );
		    		if ( oldVEvent != null ) {
		    			if ( isHoliday ) {
		    	        	PlannerUtil.updateExDate(aonCalendar, oldVEvent, event);
		    	        }
		    		}
		    	}
		    	if ( operation == IEvent.DELETE) {
		    		aonCalendar.removeEvent( index );
		    		if ( isHoliday ) {
		            	PlannerUtil.removeExDate(aonCalendar, event);
		            }
		    	}	
			} else {
				String msg = 
					ControllerUtil.getPlannerBundle().getString("aon_planner_workevent_spread_exception");
				throw new SpreadEventException( msg );
			}
		} else {
	    	if ( operation == IEvent.UPDATE) {
	    		VEvent oldVEvent = (VEvent) aonCalendar.updateEvent( index, (VEvent) event.getComponent() );
	    		if ( oldVEvent != null ) {
	    			if ( isHoliday ) {
	    	        	PlannerUtil.updateExDate(aonCalendar, oldVEvent, event);
	    	        }
	    		}
	    	}
	    	if ( operation == IEvent.DELETE) {
	    		aonCalendar.removeEvent( index );
	    		if ( isHoliday ) {
	            	PlannerUtil.removeExDate(aonCalendar, event);
	            }
	    	}	
		}
		ControllerUtil.getCalendarManagerBean().updateCalendar( aonCalendar );
    }

}
