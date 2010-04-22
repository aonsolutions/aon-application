package com.code.aon.report.jr;

import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IFinderBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * Paginated JRDataSource.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005 [Teodor Danciu
 *         (teodord@users.sourceforge.net)]
 * 
 */
public class JRPagedBeanDataSource implements JRDataSource,ICurrentBeanProvider {

	private static Logger LOGGER = LoggerFactory.getLogger(JRPagedBeanDataSource.class);  
	/**
	 * Name provider.
	 */
	protected PropertyNameProvider propertyNameProvider = null;

	/**
	 * Collection Iterator.
	 */
	private Iterator<ITransferObject> iterator = null;

	/**
	 * Bean that is being iterated.
	 */
	private ITransferObject currentBean = null;

	/**
	 * Collection provider bean.
	 */
	private IFinderBean bean;

	/**
	 * The criteria that the data will match.
	 */
	private Criteria criteria;

	/**
	 * Collection obtained.
	 */
	private Collection<ITransferObject> data;

	/**
	 * Number of beans that must be recovered in each reading.
	 */
	private int count;

	/**
	 * Offset of the proccess.
	 */
	private int offset;

	/**
	 * Constructor for this class.
	 * 
	 * @param bean
	 *            The collection provider bean.
	 * @param criteria
	 *            The criteria that the data will match.
	 * @param count
	 *            Number of beans recovered per reading.
	 * 
	 * @throws ManagerBeanException
	 *             If an error ocurred.
	 * 
	 */
	public JRPagedBeanDataSource(IFinderBean bean, Criteria criteria, int count)
			throws ManagerBeanException {
		this(bean, criteria, count, true);
	}

	/**
	 * Constructor for this class.
	 * 
	 * @param bean
	 *            The collection provider bean.
	 * @param criteria
	 *            The criteria that the data will match.
	 * @param count
	 *            Number of beans recovered per reading.
	 * @param isUseFieldDescription
	 *            True if the filed description must be used.
	 * 
	 * @throws ManagerBeanException
	 *             If an error ocurred.
	 * 
	 */
	public JRPagedBeanDataSource(IFinderBean bean, Criteria criteria,
			int count, boolean isUseFieldDescription)
			throws ManagerBeanException {
		this.criteria = criteria;
		this.bean = bean;
		this.count = count;
		offset = 0;
		this.data = bean.getList(criteria, offset, count);

		if (this.data != null) {
			this.iterator = this.data.iterator();
		}
		if (isUseFieldDescription) {
			propertyNameProvider = new PropertyNameProvider() {
				public String getPropertyName(JRField field) {
					if (field.getDescription() == null) {
						return field.getName();
					}
					return field.getDescription();
				}
			};
		} else {
			propertyNameProvider = new PropertyNameProvider() {
				public String getPropertyName(JRField field) {
					return field.getName();
				}
			};
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	public boolean next() {
		boolean hasNext = false;

		if (this.iterator != null) {
			hasNext = this.iterator.hasNext();

			if (hasNext) {
				this.currentBean = this.iterator.next();
			} else {
				if (data.size() == count) {
					try {
						offset += count;
						LOGGER.debug("Paginated report search: offset={}, count={}",count,offset);
						data = bean.getList(criteria, offset, count);
						if (this.data != null) {
							this.iterator = this.data.iterator();
							return next();
						}
					} catch (ManagerBeanException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
		}

		return hasNext;
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	public Object getFieldValue(JRField field) throws JRException {
		Object value = null;

		if (currentBean != null) {
			String propertyName = propertyNameProvider.getPropertyName(field);

			try {
				value = PropertyUtils.getProperty(currentBean, propertyName);
			} catch (java.lang.IllegalAccessException e) {
				throw new JRException(
						"Error retrieving field value from bean : "
								+ propertyName, e);
			} catch (java.lang.reflect.InvocationTargetException e) {
				throw new JRException(
						"Error retrieving field value from bean : "
								+ propertyName, e);
			} catch (java.lang.NoSuchMethodException e) {
				throw new JRException(
						"Error retrieving field value from bean : "
								+ propertyName, e);
			}
		}

		return value;
	}

	/**
	 * @author Consulting & Development. ecastellano - 14-nov-2005
	 *
	 */
	interface PropertyNameProvider {
		/**
		 * @param field
		 * @return .
		 */
		public String getPropertyName(JRField field);
	}

	public Object getCurrentBean() {
		return this.currentBean;
	}
}
