package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.common.ICommonMessages.ACTION_ADD_TARGETS_FINISH;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME;

import java.util.Collection;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Target;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.TargetController;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignActionTargetController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignActionTargetController.class.getName());
	
	private IControllerListener actionFilter;
	
	@SuppressWarnings("unchecked")
	private List<Integer> getCurrentTargets() throws ManagerBeanException {
		Criteria criteria = getCriteria();
		String targetId = getFieldName(IEntityAlias.ACTION_TARGET_TARGET_ID);
		ProjectionList projectList = new ProjectionList(Projection.property(targetId));
		return getManagerBean().getList(projectList, criteria);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection<Integer> getSelectedTargets() {
		TargetController targetController = (TargetController) AonUtil.getRegisteredBean(ICommercialConstants.TARGET_CONTROLLER_NAME);
		return (Collection) targetController.getCheckList();
	}
	
	public void onAcceptTargets( ActionEvent event ) throws ManagerBeanException {
		LogPanelController logger = LogPanelController.getInstance();
		Collection<Integer> targets = getSelectedTargets();
		targets.removeAll(getCurrentTargets());
		IController actionController = FormUtil.getController(IMarketingConstants.CAMPAIGN_ACTION_CONTROLLER_NAME);
		MarketingAction action = (MarketingAction) actionController.getTo();
		logger.info( AonUtil.getMessage(ICommonMessages.ACTION_ADD_TARGETS_START, targets.size()) );
		Integer domainId = DomainManager.getCurrentDomain();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(sessionFactoryName);
		StatelessSession session = sessionFactory.openStatelessSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			int i = 0;
			for( Integer targetId : targets ) {
				ActionTarget at = new ActionTarget();
				at.setDomain( domainId );
				at.setAction( action );
				Target target = new Target();
				target.setId( targetId );
				at.setTarget( target );
				session.insert(at);
				if ( (++i % 1000) == 0 ) {
					logger.info( AonUtil.getMessage(ICommonMessages.ACTION_ADD_TARGETS_STATUS, i) );
				}
			}
			tx.commit();
		} catch (HibernateException he) {
			tx.rollback();
			logger.error( he.getMessage() );
			LOGGER.error(">>>> onAcceptTargets ", he);
			AonUtil.addErrorMessage( he.getMessage() );
			throw new AbortProcessingException(he.getMessage(), he);			
		} finally {
			session.close();
			logger.info( AonUtil.getMessage(ACTION_ADD_TARGETS_FINISH) );
		}		
		initializeModel();
	}

	public int getCount() throws ManagerBeanException {
		if ( getMasterController().getModel().isRowAvailable() ) {
			MarketingAction action = (MarketingAction) getMasterController().getModel().getRowData();
			Criteria criteria = new Criteria();
			String alias = getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID);
			criteria.addEqualExpression(alias, action.getId());
			return getManagerBean().getCount(criteria);
		}
		return 0;
	}	
	
	private int getCount( ActionTargetStatus status ) {
		Criteria criteria = new Criteria();
		try {
			MarketingAction action = (MarketingAction) getMasterController().getTo();
			criteria.addEqualExpression(getFieldName(IEntityAlias.ACTION_TARGET_ACTION_ID), action.getId());
			criteria.addEqualExpression(getFieldName(IEntityAlias.ACTION_TARGET_STATUS), status);
			return getManagerBean().getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return 0;
	}	

	public int getPendingCount() {
		return getCount(ActionTargetStatus.PENDING);
	}	

	public int getAbsentCount() {
		return getCount(ActionTargetStatus.ABSENT);
	}	

	public int getIncorrectCount() {
		return getCount(ActionTargetStatus.INCORRECT);
	}	

	public int getTryAgainCount() {
		return getCount(ActionTargetStatus.TRY_AGAIN);
	}	

	public int getCancelCount() {
		return getCount(ActionTargetStatus.CANCEL);
	}	

	public int getFinishedCount() {
		return getCount(ActionTargetStatus.FINISHED);
	}	

	public int getSentCount() {
		return getCount(ActionTargetStatus.SENT);
	}	
	
	public IControllerListener getActionFilter() {
		if ( this.actionFilter == null ) {
			this.actionFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						IController actionController = FormUtil.getController(CAMPAIGN_ACTION_CONTROLLER_NAME);
						MarketingAction action = (MarketingAction) actionController.getTo();
						String alias = controller.getFieldName(IEntityAlias.MARKETING_ACTION_ID);
						controller.getCriteria().addNotEqualExpression(alias, action.getId());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering action", e);
					}
				}
			};
		}
		return this.actionFilter;
	}
	
	public void onClearStatus( ActionEvent event ) {
		MarketingAction action = (MarketingAction) getMasterController().getTo();
		resetStatuses(action, ActionTargetStatus.PENDING);
	}

	private void resetStatuses( MarketingAction action, ActionTargetStatus status ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(sessionFactoryName);
		StatelessSession session = sessionFactory.openStatelessSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String hqlUpdate = "update ActionTarget at set at.status = :actionStatus, at.user = null where at.action = :actionId";
			int updatedEntities = session.createQuery( hqlUpdate )
					.setInteger( "actionStatus", status.ordinal() )
			        .setInteger( "actionId", action.getId() )
			        .executeUpdate();
			LOGGER.info( "updatedEntities: {}", updatedEntities );
			tx.commit();
		} catch (HibernateException he) {
			tx.rollback();
			LOGGER.error(">>>> onClearStatus ", he);
			AonUtil.addErrorMessage(he.getMessage());
			throw new AbortProcessingException(he.getMessage(), he);			
		} finally {
			session.close();
		}
	}
	
	public static void resetStatuses( List<Integer> actionTargets, ActionTargetStatus status ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(sessionFactoryName);
		StatelessSession session = sessionFactory.openStatelessSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			for( Integer id : actionTargets ) {
				ActionTarget at = (ActionTarget) session.get(ActionTarget.class, id);
				at.setStatus(status);
				at.setUser(null);
				session.update(at);
			}
			tx.commit();
		} catch (HibernateException he) {
			tx.rollback();
			LOGGER.error(">>>> onClearStatus ", he);
			AonUtil.addErrorMessage(he.getMessage());
			throw new AbortProcessingException(he.getMessage(), he);			
		} finally {
			session.close();
		}
	}
	
	public void deleteActionTargets( MarketingAction action ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(sessionFactoryName);
		StatelessSession session = sessionFactory.openStatelessSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String hqlUpdate = "delete ActionTarget at where at.action = :actionId";
			int deletedEntities = session.createQuery( hqlUpdate )
			        .setInteger( "actionId", action.getId() )
			        .executeUpdate();
			LOGGER.info( "deletedEntities: {}", deletedEntities );
			tx.commit();
		} catch (HibernateException he) {
			tx.rollback();
			LOGGER.error(">>>> deleteActionTargets ", he);
			AonUtil.addErrorMessage(he.getMessage());
			throw new AbortProcessingException(he.getMessage(), he);			
		} finally {
			session.close();
		}
	}
	
}