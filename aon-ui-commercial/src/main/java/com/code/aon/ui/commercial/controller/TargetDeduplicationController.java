package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_DEDUPLICATION_CONTROLLER_NAME;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;

import java.io.Serializable;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.Registry;
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
	private boolean showProgressBarWindow;
	private boolean enabledProgressBar;
	private int progressValue;
	private int maxProgressValue;
	private List<Integer> duplicateTargets;
	private List<DeduplicationValues> deduplicationList;
	private Target selectedTarget;
	private DeduplicationValues selectedValues;
	private DeduplicationEntry selectedEntry;
	
	public void onEditSearch( ActionEvent event ) {
		getTargetController().onEditSearch(event);
		reset();
	}
	
	private void reset() {
		setShowProgressBarWindow(false);
		setType(null);
	}
	
	private void finish() {
		if ( this.enabledProgressBar ) {
			setModel(new SerializableListDataModel(deduplicationList));		
			this.progressValue = this.maxProgressValue+1;
			this.enabledProgressBar = false;			
		}
	}

	public DeduplicationType getType() {
		return type;
	}

	public void setType(DeduplicationType type) {
		this.type = type;
	}
	
	public boolean isShowProgressBarWindow() {
		return showProgressBarWindow;
	}

	public void setShowProgressBarWindow(boolean showProgressBarWindow) {
		this.showProgressBarWindow = showProgressBarWindow;
	}
	
	public int getProgressValue() {
		return progressValue;
	}

	public int getMaxProgressValue() {
		return maxProgressValue;
	}
	
	public boolean isEnabledProgressBar() {
		return enabledProgressBar;
	}
	
	public long getProgressValuePercent() {
		long value = 0;
		if ( this.maxProgressValue > 0 && this.progressValue > 0 ) {
			value = Math.round((this.progressValue * 100.0)/this.maxProgressValue);
		}
		return value;
	}
	
	public int getNumberOfDuplicates() {
		return this.duplicateTargets.size();
	}

	public void onStopSearch( ActionEvent event ) {
		setShowProgressBarWindow(false);
		finish();
	}
	
	public void onCloseProgressBarWindow( ActionEvent event ) {
		setShowProgressBarWindow(false);
	}

	private TargetController getTargetController() {
		return (TargetController) AonUtil.getRegisteredBean(ICommercialConstants.TARGET_CONTROLLER_NAME);
	}
	
	private Criteria getTargetCriteria( TargetController controller ) throws ManagerBeanException {	
		Integer pageLimit = controller.getPageLimit();
		controller.setPageLimit(0);
		controller.onSearch(null);
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
			case FIXED_PHONE:
				criteria.addEqualExpression(MEDIA_TYPE, MediaType.FIXED_PHONE);
				criteria.addOrder(MEDIA_VALUE);
				break;
			case CELLULAR:
				criteria.addEqualExpression(MEDIA_TYPE, MediaType.CELLULAR);
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
			case FIXED_PHONE:
				criteria.addEqualExpression(MEDIA_TYPE, MediaType.FIXED_PHONE);
				criteria.addEqualExpression(MEDIA_VALUE, value);
				break;
			case CELLULAR:
				criteria.addEqualExpression(MEDIA_TYPE, MediaType.CELLULAR);				
				criteria.addEqualExpression(MEDIA_VALUE, value);
				break;
		}
		return criteria;
	}
	
	private String[] getRegistryMedias(DSLContext context, Integer id, MediaType type) {
		String[] result = context.select(RMEDIA.VALUE).from(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(id), RMEDIA.MEDIA.eq((byte)type.ordinal()))
				.fetchArray(RMEDIA.VALUE);
		return result;
	}	
	
	private String getRegistryDocument( DSLContext context, Integer id ) {
		return context.select(REGISTRY.DOCUMENT).from(REGISTRY)
				.where(REGISTRY.ID.eq(id))
				.fetchOne(REGISTRY.DOCUMENT);
	}	

	private String getRegistryName( DSLContext context, Integer id ) {
		return context.select(REGISTRY.NAME).from(REGISTRY)
				.where(REGISTRY.ID.eq(id))
				.fetchOne(REGISTRY.NAME);
	}	
	
	private String[] getValues( DSLContext context, Integer id ) {
		String[] values = null;
		switch ( type ) {
			case DOCUMENT:
				String document = getRegistryDocument(context, id);
				if (! StringUtils.isBlank(document) ) {
					values = new String[]{document};	
				}
				break;
			case NAME:
				String name = getRegistryName(context, id);
				if (! StringUtils.isBlank(name) ) {
					values = new String[]{name};
				}
				break;
			case EMAIL:
				values = getRegistryMedias(context, id, MediaType.EMAIL);
				break;
			case FIXED_PHONE:
				values = getRegistryMedias(context, id, MediaType.FIXED_PHONE);
				break;
			case CELLULAR:
				values = getRegistryMedias(context, id, MediaType.CELLULAR);
				break;
		}
		return values;
	}
	
	@SuppressWarnings("unchecked")
	private void findDuplicates( Integer id, DSLContext context, IManagerBean bean, Criteria mainCriteria ) throws ManagerBeanException {
		String[] values = getValues(context, id);
		if(! ArrayUtils.isEmpty(values) ) {
			for( String value : values ) {
				Criteria criteria = getDeduplicateCriteria(id, value, bean, mainCriteria);
				List<Integer> list = bean.getList(getProjectionList(bean), criteria);
				if (! list.isEmpty() ) {
					duplicateTargets.addAll(list);
					DeduplicationValues entry = new DeduplicationValues(value, id);
					for( Integer duplicateId : list ) {
						entry.addTarget(duplicateId);
					}
					deduplicationList.add(entry);
				}
			}			
		}
	}
	
	public void onSearch( ActionEvent event ) throws ManagerBeanException {
		this.progressValue = 0;
		this.maxProgressValue = 0;
		this.enabledProgressBar = true;
		setModel(null);
		setShowProgressBarWindow(true);
		TargetController controller = getTargetController();
		Criteria mainCriteria = getTargetCriteria(controller);
		this.maxProgressValue = controller.getManagerBean().getCount(mainCriteria);
		TargetDeduplicationThread thread = new TargetDeduplicationThread(this); 
		thread.setTargetController(controller); 
		thread.setCriteria(mainCriteria);
		thread.start();
	}

	private Connection getConnection() {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return connection;
	}
	
	@SuppressWarnings("unchecked")
	public void searchDuplicates(HttpServletRequest httpServletRequest, TargetController controller, Criteria mainCriteria ) {
		this.duplicateTargets = new LinkedList<Integer>();
		this.deduplicationList = new LinkedList<DeduplicationValues>();
		Connection connection = null;
		try {
			HttpServletRequestValve.setHttpServletRequest(httpServletRequest);
			connection = getConnection();
			DSLContext context = DSL.using(connection, AccountingUtil.getDefaultSettings());
			IManagerBean bean = controller.getManagerBean();
			Criteria searchCriteria = getSearchCriteria(bean, mainCriteria);
			List<Integer> list = bean.getList(getProjectionList(bean), searchCriteria);
			LOGGER.info( "Targets deduplication: {}, {}", list.size(), searchCriteria);
			for( Integer id : list ) {
				if (! duplicateTargets.contains(id) ) {
					findDuplicates(id, context, bean, mainCriteria);	
				}
				this.progressValue++;
				if (! isEnabledProgressBar() ) {
					break;
				}
			}			
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
		} finally {
			HttpServletRequestValve.setHttpServletRequest(null);
			DatabaseUtil.closeQuietly(connection);
		}
		finish();
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
		this.selectedValues = getCurrentDeduplicationValues();
		if ( (selectedValues != null) && selectedValues.getModel().isRowAvailable()) {
			this.selectedEntry = (DeduplicationEntry) selectedValues.getModel().getRowData();
			return this.selectedEntry.getTarget();
		}
		return null;		
	}
	
	public void onGoToTarget( ActionEvent event ) throws ManagerBeanException {
		this.selectedTarget = getCurrentTarget();
		if ( selectedTarget != null ) {
			TargetController controller = getTargetController();
			controller.select(event, selectedTarget.getId());
			controller.setBackAction(getBeanName()+BasicController.LIST_SUFFIX);
			controller.setBackActionListener(TARGET_DEDUPLICATION_CONTROLLER_NAME + ".onBackToDeduplication");
		}
	}
	
	public void onBackToDeduplication( ActionEvent event ) throws ManagerBeanException {
		TargetController controller = getTargetController();
		Target target = (Target) controller.getManagerBean().get(this.selectedTarget.getId());
		if ( target == null ) {
			removeTarget();
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

	public void onRemoveTarget( ActionEvent event ) throws ManagerBeanException {
		Target target = getCurrentTarget();
		if ( target != null ) {
			TargetController controller = getTargetController();
			controller.select(event, target.getId());
			controller.onRemove(event);
			removeTarget();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void removeTarget() {
		if ( this.selectedValues.getSize() == 2 ) {
			List<DeduplicationValues> list = (List) getDirectModel().getWrappedData();
			list.remove(this.selectedValues);
			getDirectModel().setWrappedData(list);
		} else {
			this.selectedValues.removeEntry(this.selectedEntry);
		}					
	}
	
	public static class DeduplicationValues implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String value;
		
		private List<DeduplicationEntry> entries;
		
		private boolean checked;
		
		private DataModel model;

		public DeduplicationValues(String value, Integer id) {
			this.value = value;
			this.entries = new LinkedList<DeduplicationEntry>();
			this.entries.add(new DeduplicationEntry(id));
		}
		
		public void addTarget( Integer id ) {
			this.entries.add(new DeduplicationEntry(id));			
		}
		
		public void addDuplicates( List<Integer> list ) {
			for( int i = 1; i < entries.size(); i++ ) {
				list.add(entries.get(i).getId());
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
		
		public void removeEntry( DeduplicationEntry entry ) {
			entries.remove(entry);
			getModel().setWrappedData(entries);
		}
		
	}

	public static class DeduplicationEntry implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Integer id;
		
		private Target target;
		
		public DeduplicationEntry(Integer id) {
			this.id = id;
		}
		
		public Target getTarget() {
			if ( target == null ) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(Target.class);
					this.target = (Target) bean.get(this.id);
				} catch (ManagerBeanException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			return target;
		}
		
		public Integer getId() {
			return id;
		}

		public Registry getRegistry() {
			return getTarget().getRegistry();
		}
		
		public boolean isActive() {
			return getTarget().getStatus() == TargetStatus.ACTIVE;
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
