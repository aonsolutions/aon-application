package com.code.aon.ui.commercial.controller;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;

/**
 * Controller used in the offer maintenance.
 */
public class OfferTermController extends LinesController {

	private boolean general;
	private IControllerListener lookupListener;
	private CommercialTerm commercialTerm;
	
	public CommercialTerm getCommercialTerm() {
		return commercialTerm;
	}

	public void setCommercialTerm(CommercialTerm commercialTerm) {
		this.commercialTerm = commercialTerm;
	}

	public boolean isGeneral() {
		return general;
	}

	public void onTermChanged(LookupChangeEvent event) {
		if ( event.getNewValue() != null ) {
			CommercialTerm ct = (CommercialTerm) event.getNewValue();
			OfferTerm offerTerm = (OfferTerm) getTo();
			offerTerm.setName( ct.getName() );
			offerTerm.setDescription( ct.getDescription() );
		}
	}
	
	public void setGeneral(boolean general) throws ManagerBeanException {
		this.general = general;
		List<Expression> initExpressions = new ArrayList<Expression>();
		String generalAlias = getFieldName(ICommercialAlias.OFFER_TERM_GENERAL);
		Expression expression = ExpressionUtilities.getEqualExpression(generalAlias, general);
		initExpressions.add( expression );
		setInitExpressions(initExpressions);
		this.lookupListener = getTermListener();
	}

	public IControllerListener getLookupListener() {
		return lookupListener;
	}

	private IControllerListener getTermListener() {
		return new ControllerAdapter() {

			@Override
			public void beforeModelInitialized(ControllerEvent event)
					throws ControllerListenerException {
				try {
					Criteria criteria = event.getController().getCriteria();
					String generalField = event.getController().getFieldName(ICommercialAlias.COMMERCIAL_TERM_GENERAL);
					criteria.addEqualExpression(generalField, general);
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException( e.getMessage(), e );
				}
			}

			@Override
			public void afterBeanCreated(ControllerEvent event)
					throws ControllerListenerException {
				CommercialTerm ct = (CommercialTerm) event.getController().getTo();
				ct.setGeneral(general);
			}
			
		};
	}

}
