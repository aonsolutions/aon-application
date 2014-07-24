package com.code.aon.ui.commercial.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.commercial.enumeration.DeduplicationType;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetDeduplicationController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TargetDeduplicationController.class);
	
	private static final String MEDIA_TYPE = "Target.registry.medias.mediaType";
	private static final String MEDIA_VALUE = "Target.registry.medias.value";
	
	private DeduplicationType type;
	
	public void onEditSearch( ActionEvent event ) {
		getTargetController().onEditSearch(event);
		setType(null);
	}

	public DeduplicationType getType() {
		return type;
	}

	public void setType(DeduplicationType type) {
		this.type = type;
	}
	
	private TargetController getTargetController() {
		return (TargetController) AonUtil.getRegisteredBean(ICommercialConstants.TARGET_CONTROLLER_NAME);
	}
	
	private Criteria getTargetCriteria( TargetController controller, ActionEvent event ) throws ManagerBeanException {	
		Integer pageLimit = controller.getPageLimit();
		controller.setPageLimit(0);
		controller.onSearch(event);
		controller.setPageLimit(pageLimit);
		Criteria criteria = controller.getCriteria();
		criteria.setOrderByList(null);
		return criteria;
	}
	
	private Criteria getSearchCriteria( IManagerBean bean, Criteria mainCriteria ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addExpression(mainCriteria.getExpression());
		switch ( type ) {
			case DOCUMENT:
				String documentAlias = bean.getFieldName(IEntityAlias.TARGET_REGISTRY_DOCUMENT);
				criteria.addNotNullExpression(documentAlias);
				criteria.addOrder(documentAlias);
				break;
			case NAME:
				String nameAlias = bean.getFieldName(IEntityAlias.TARGET_REGISTRY_NAME);
				criteria.addNotNullExpression(nameAlias);
				criteria.addOrder(nameAlias);
				break;
			case EMAIL:
				criteria.addEqualExpression(MEDIA_TYPE, MediaType.EMAIL);
				criteria.addOrder(MEDIA_VALUE);
				break;
			case TELEPHONE:
				Expression expr1 = ExpressionUtilities.getEqualExpression(MEDIA_TYPE, MediaType.FIXED_PHONE);
				Expression expr2 = ExpressionUtilities.getEqualExpression(MEDIA_TYPE, MediaType.CELLULAR);
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
				criteria.addOrder(MEDIA_VALUE);
				break;
		}
		return criteria;
	}
	
	private ProjectionList getProjectionList( IManagerBean bean ) throws ManagerBeanException {
		return new ProjectionList(Projection.property(bean.getFieldName(IEntityAlias.TARGET_ID)));		
	}
	
	private Criteria getDeduplicateCriteria( Integer id, String value, IManagerBean bean, Criteria mainCriteria ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addExpression(mainCriteria.getExpression());
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.TARGET_ID), id);
		switch ( type ) {
			case DOCUMENT:
				String documentAlias = bean.getFieldName(IEntityAlias.TARGET_REGISTRY_DOCUMENT);
				criteria.addEqualExpression(documentAlias, value);
				break;
			case NAME:
				String nameAlias = bean.getFieldName(IEntityAlias.TARGET_REGISTRY_NAME);
				criteria.addEqualExpression(nameAlias, value);
				break;
			case EMAIL:
				criteria.addEqualExpression(MEDIA_TYPE, MediaType.EMAIL);
				criteria.addEqualExpression(MEDIA_VALUE, value);
				break;
			case TELEPHONE:
				Expression expr1 = ExpressionUtilities.getEqualExpression(MEDIA_TYPE, MediaType.FIXED_PHONE);
				Expression expr2 = ExpressionUtilities.getEqualExpression(MEDIA_TYPE, MediaType.CELLULAR);
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));				
				criteria.addEqualExpression(MEDIA_VALUE, value);
				break;
		}
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	private String[] getRegistryMedias(Registry registry, MediaType ... types) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
		if ( types.length == 1 ) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), types[0]);	
		} else {
			criteria.addInExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), Arrays.asList(types));
		}
		ProjectionList pl = new ProjectionList(Projection.property(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_VALUE)));
		List<String> list = bean.getList(pl, criteria);
		if (!list.isEmpty()){
			return list.toArray(new String[list.size()]);
		}
		return null;
	}	
	
	private String[] getValues( Registry registry ) throws ManagerBeanException {
		String[] values = null;
		switch ( type ) {
			case DOCUMENT:
				if (! StringUtils.isBlank(registry.getDocument()) ) {
					values = new String[]{registry.getDocument()};	
				}
				break;
			case NAME:
				if (! StringUtils.isBlank(registry.getName()) ) {
					values = new String[]{registry.getName()};
				}
				break;
			case EMAIL:
				values = getRegistryMedias(registry, MediaType.EMAIL);
				break;
			case TELEPHONE:
				values = getRegistryMedias(registry, MediaType.FIXED_PHONE, MediaType.CELLULAR);
				break;
		}
		return values;
	}
	
	@SuppressWarnings("unchecked")
	private List<DeduplicationValues> findDuplicates( Integer id, IManagerBean bean, Criteria mainCriteria ) throws ManagerBeanException {
		List<DeduplicationValues> entries = new LinkedList<DeduplicationValues>();
		Target target = (Target) bean.get(id);
		String[] values = getValues(target.getRegistry());
		if(! ArrayUtils.isEmpty(values) ) {
			for( String value : values ) {
				Criteria criteria = getDeduplicateCriteria(id, value, bean, mainCriteria);
				List<Integer> list = bean.getList(getProjectionList(bean), criteria);
				if (! list.isEmpty() ) {
					DeduplicationValues entry = new DeduplicationValues(value, target);
					for( Integer duplicateId : list ) {
						Target duplicateTarget = (Target) bean.get(duplicateId);
						entry.addTarget(duplicateTarget);
					}
					entries.add(entry);
				}
			}			
		}
		return entries;
	}
	
	@SuppressWarnings("unchecked")
	public void onSearch( ActionEvent event ) {
		List<Integer> duplicateTargets = new LinkedList<Integer>();
		List<DeduplicationValues> deduplicationList = new LinkedList<DeduplicationValues>();
		try {
			TargetController controller = getTargetController();
			IManagerBean bean = controller.getManagerBean();
			Criteria mainCriteria = getTargetCriteria(controller, event);
			Criteria searchCriteria = getSearchCriteria(bean, mainCriteria);
			List<Integer> list = bean.getList(getProjectionList(bean), searchCriteria);
			StopWatch sw = new StopWatch();
			sw.start();
			LOGGER.info( "Targets deduplication: {}, {}", list.size(), searchCriteria);
			for( Integer id : list ) {
				if (! duplicateTargets.contains(id) ) {
					List<DeduplicationValues> entries = findDuplicates(id, bean, mainCriteria);	
					if (! entries.isEmpty()) {
						deduplicationList.addAll(entries);
						for( DeduplicationValues entry : entries ) {
							entry.addDuplicates(duplicateTargets);
						}
					}
				}
			}			
			sw.stop();
			LOGGER.info( "Tiempo: {}", sw.toString());
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
		}
		setModel(new SerializableListDataModel(deduplicationList));
	}
	
	private DeduplicationValues getCurrentDeduplicationValues() {
		if (getDirectModel().isRowAvailable()) {
			return (DeduplicationValues)getDirectModel().getRowData();
		}
		return null;
	}
	
	public void onSelectEntry( ActionEvent event ) {
		DeduplicationValues dv = getCurrentDeduplicationValues();
		if (dv != null) {
			dv.setChecked(!dv.isChecked());
		}
	}

	public DataModel getRegistryModel() {
		DeduplicationValues dv = getCurrentDeduplicationValues();
		if (dv != null) {
			return dv.getModel();
		}
		return null;
	}
	
	private Target getCurrentTarget() {
		DeduplicationValues dv = getCurrentDeduplicationValues();
		if ( (dv != null) && dv.getModel().isRowAvailable()) {
			DeduplicationEntry entry = (DeduplicationEntry) dv.getModel().getRowData();
			return entry.getTarget();
		}
		return null;		
	}
	
	public void onGoToTarget( ActionEvent event ) throws ManagerBeanException {
		Target target = getCurrentTarget();
		if ( target != null ) {
			TargetController controller = getTargetController();
			controller.select(event, target.getId());
			controller.setBackAction(getBeanName()+BasicController.LIST_SUFFIX);
		}
	}

	public void onActivateTarget( ActionEvent event ) {
		updateTargetStatus(TargetStatus.ACTIVE);
	}

	public void onDeactivateTarget( ActionEvent event ) {
		updateTargetStatus(TargetStatus.INACTIVE);
	}
	
	private void updateTargetStatus( TargetStatus status ) {
		Target target = getCurrentTarget();
		if ( target != null ) {
			try {
				TargetController controller = getTargetController();
				target.setStatus(status);
				controller.getManagerBean().update(target);	
			} catch ( ManagerBeanException e ) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onRemoveTarget( ActionEvent event ) throws ManagerBeanException {
		Target target = getCurrentTarget();
		if ( target != null ) {
			TargetController controller = getTargetController();
			controller.select(event, target.getId());
			controller.onRemove(event);
			DeduplicationValues dv = getCurrentDeduplicationValues();
			if ( dv.getSize() == 2 ) {
				List<DeduplicationValues> list = (List) getDirectModel().getWrappedData();
				list.remove(dv);
				getDirectModel().setWrappedData(list);
			} else {
				dv.removeCurrentEntry();
			}			
		}
	}
	
	public class DeduplicationValues {
		
		private String value;
		
		private List<DeduplicationEntry> entries;
		
		private boolean checked;
		
		private DataModel model;

		public DeduplicationValues(String value, Target target) {
			this.value = value;
			this.entries = new LinkedList<DeduplicationEntry>();
			addTarget(target);
		}
		
		public void addTarget( Target target ) {
			this.entries.add(new DeduplicationEntry(target));			
		}
		
		public void addDuplicates( List<Integer> list ) {
			for( int i = 1; i < entries.size(); i++ ) {
				list.add(entries.get(i).getRegistry().getId());
			}
		}

		public String getValue() {
			return value;
		}
		
		public int getSize() {
			return entries.size();
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		public DataModel getModel() {
			if ( model == null ) {
				this.model = new SerializableListDataModel(entries);
			}
			return model;
		}
		
		public void removeCurrentEntry() {
			DeduplicationEntry entry = (DeduplicationEntry) getModel().getRowData();
			entries.remove(entry);
			getModel().setWrappedData(entries);
		}
		
	}

	public class DeduplicationEntry {
		
		private Target target;
		
		public DeduplicationEntry(Target target) {
			this.target = target;
		}
		
		public Target getTarget() {
			return target;
		}

		public Registry getRegistry() {
			return target.getRegistry();
		}
		
		public boolean isActive() {
			return target.getStatus() == TargetStatus.ACTIVE;
		}
		
		public String getTelephones() throws ManagerBeanException {
			StringBuffer sb = new StringBuffer();
			String phones = getRegistry().getPhones();
			if (! StringUtils.isEmpty(phones)) {
				sb.append(phones);
			}
			String cellulars = getRegistry().getCellulars();
			if (! StringUtils.isEmpty(cellulars)) {
				if ( sb.length() > 0 ) {
					sb.append( ", ");
				}
				sb.append(cellulars);
			}
			return sb.toString();
		}
		
	}
	
}
