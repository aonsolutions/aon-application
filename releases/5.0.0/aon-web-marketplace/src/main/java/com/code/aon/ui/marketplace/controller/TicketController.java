package com.code.aon.ui.marketplace.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.marketplace.tickets.Ticket;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ApplicationParameterController;

/**
 * Controller used in the ticket maintenance.
 */
public class TicketController implements ICollectionProvider {

	/** The current ticket. */
	private Ticket to;
	
	/** The tickets. */
	private LinkedList<Ticket> tickets = new LinkedList<Ticket>();
	
	/** The model. */
	private DataModel model = new ListDataModel();
	
	/** The sanatary id. */
	private String sanataryId;
	
	/**
	 * The empty constructor.
	 */
	public TicketController(){
		super();
		FacesContext ctx = FacesContext.getCurrentInstance(); 
		ValueBinding vb = ctx.getApplication().createValueBinding( "#{appParams}" );
		ApplicationParameterController paramsController = (ApplicationParameterController) vb.getValue(ctx);
		sanataryId = paramsController.getParameters().get("N_AUTORIZACION_SANITARIO").getValue();
		to = new Ticket();
		to.setSanataryId(sanataryId);
	}
	
	/**
	 * Gets the current ticket.
	 * 
	 * @return Returns the current ticket.
	 */
	public Ticket getTo() {
		return to;
	}

	/**
	 * Sets the current ticket.
	 * 
	 * @param to The current ticket to set.
	 */
	public void setTo(Ticket to) {
		this.to = to;
	}

	/**
	 * On new. Initializes the controller
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onNew(ActionEvent event) {
		to = new Ticket();
		to.setSanataryId(sanataryId);
	}
	
	/**
	 * On save. Adds the current ticket into the list and into the model
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onSave(ActionEvent event) {
		if (!tickets.contains(to))
			tickets.add(to);
		to = new Ticket();
		to.setSanataryId(sanataryId);
		model.setWrappedData(tickets);
	}

	/**
	 * On remove. Removes the ticket from the model and the list
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onRemove(ActionEvent event) {
		tickets.remove(to);
		to = new Ticket();
		to.setSanataryId(sanataryId);
		model.setWrappedData(tickets);
	}

	/**
	 * On select. Sets the current ticket
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		int row = this.model.getRowIndex();
		to = tickets.get(row);
	}

	/**
	 * Checks if is saved.
	 * 
	 * @return true, if is saved
	 */
	public boolean isSaved(){
		return tickets.contains(to);
	}

	/**
	 * Checks if is ready.
	 * 
	 * @return true, if is ready
	 */
	public boolean isReady(){
		if (tickets.size()==0)return false;
		return true;
	}

	/**
	 * Gets the collection.
	 * 
	 * @return the collection
	 */
	public Collection getCollection() {
		Collection<Ticket> col = new ArrayList<Ticket>();
		Iterator iter = tickets.iterator();
		while (iter.hasNext()){
			Ticket t = (Ticket) iter.next();
			for (int i = 0; i<t.getQuantity(); i++){
				col.add(t);
			}
		}
		return col;
	}

	/**
	 * Gets the ticket list.
	 * 
	 * @return Returns the errors.
	 */
	public List getTicketList() {
		return tickets;
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public DataModel getModel(){
		return this.model;
	}

	/** The compositions. */
	private LinkedList<Composition> compositions;
	
	/** The composition model. */
	private DataModel compositionModel = new ListDataModel(); 

	/**
	 * Gets the composition model.
	 * 
	 * @return the composition model
	 */
	public DataModel getCompositionModel(){
		return this.compositionModel;
	}

	/**
	 * Load into the composition model the compositions
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onListComposition(ActionEvent event) {
		compositions = new LinkedList<Composition>();
		try {
			IManagerBean compositionBean = BeanManager.getManagerBean(Composition.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionBean.getFieldName(ICompositionAlias.COMPOSITION_TYPE), Short.valueOf((short) 1));
			Iterator<ITransferObject> iter = compositionBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				Composition composition = (Composition) iter.next();
				compositions.add(composition);
			}
			compositionModel.setWrappedData(compositions);
		} catch (ManagerBeanException e) {
		}
	}

	/**
	 * Sets the current ticket, and loads the ingredients of the composition
	 * 
	 * @param event the event
	 */
	@SuppressWarnings("unused")
	public void onSelectComposition(ActionEvent event) {
		try{
			to = new Ticket();
			int row = this.compositionModel.getRowIndex();
			Composition c = compositions.get(row); 
			to.setItem(c.getDescription());
            to.setSanataryId(sanataryId);
			to.setQuantity(1);
			
			IManagerBean cdbean = BeanManager.getManagerBean(CompositionDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(cdbean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID),c.getId());
			List<ITransferObject> cdlist = cdbean.getList(criteria);
			Iterator<ITransferObject> iter = cdlist.iterator();
			String compositionDesc = new String();
			while (iter.hasNext()) {
				CompositionDetail cd = (CompositionDetail) iter.next();
				compositionDesc+=cd.getItem().getProduct().getName();
                compositionDesc+=((cd.getItem().getDetail()==null) ? "" : " " + cd.getItem().getDetail()) + ";";
			}
			to.setIngredients(compositionDesc);
		} catch (ManagerBeanException e) {
		}
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}
}