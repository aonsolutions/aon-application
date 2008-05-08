package com.code.aon.common.dao.hibernate;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.AssociationType;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.NullableType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimeType;
import org.hibernate.type.Type;

import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.BetweenExpression;
import com.code.aon.ql.ast.ConstantExpression;
import com.code.aon.ql.ast.CriterionVisitor;
import com.code.aon.ql.ast.IdentExpression;
import com.code.aon.ql.ast.LogicalAndExpression;
import com.code.aon.ql.ast.LogicalOrExpression;
import com.code.aon.ql.ast.NotNullExpression;
import com.code.aon.ql.ast.NullExpression;
import com.code.aon.ql.ast.RelationalExpression;

/**
 * Expression visitor that obtains an SQL WHERE clause.
 * 
 * @author Consulting & Development. Aimar Tellitu - 17-may-2005
 * @since 1.0
 * 
 */

public class HibernateRenderer implements CriterionVisitor {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(HibernateRenderer.class.getName());
	
	private static final char INNER_JOIN_SEPARATOR = '.';
	
	private static final String INNER_JOIN_ALIAS_SUFFIX = "_";
	
	private static final char LEFT_JOIN_SEPARATOR = '<';
	
	private static final String LEFT_JOIN_ALIAS_SUFFIX = "_lf_";
	
	private static final char FULL_JOIN_SEPARATOR = '!';

	private static final String FULL_JOIN_ALIAS_SUFFIX = "_fj_";

    /** System Property name to set the pattern to parse the Time strings.  */
    public static final String TIME_PATTERN_PROPERTY = "com.code.aon.ql.time.pattern";
	
    /** System Property name to set the pattern to parse the Date strings.  */
    public static final String DATE_PATTERN_PROPERTY = "com.code.aon.ql.date.pattern";
    
    /** System Property name to set the pattern to parse the TimeStamp strings.  */
    public static final String TIME_STAMP_PATTERN_PROPERTY = "com.code.aon.ql.timeStamp.pattern";    
	
	/** SEPARATORS */
	public static final String SEPARATORS = "" + INNER_JOIN_SEPARATOR + LEFT_JOIN_SEPARATOR + FULL_JOIN_SEPARATOR;

	/**
	 * Field classMetaData
	 */
	private ClassMetadata classMetaData;

	/**
	 * Field aliasCriterias
	 */
	private Set<String> aliasSet;

	/**
	 * Field criterion
	 */
	private Criterion criterion;

	/**
	 * Field criteria
	 */
	private org.hibernate.Criteria criteria;

	/**
	 *  The projection list.
	 */
	private ProjectionList projectionList;
	
	/**
	 * Store literal constants value of the expression.
	 */
	private Object literal;

	/**
	 * Store identifier name of the expression.
	 */
	private String identifier;

	/**
	 * Store identifier full name of the expression.
	 */
	private String fullIdentifier;

	/**
	 * Store exceptions in Criterion constructor.
	 */
	private Exception exception;
	
	/**
	 * Distinct needed because multiple results of the root entity can be found.
	 */
	private boolean distinctNeeded;

	/**
	 * Constructor for HibernateRenderer
	 * 
	 * @param criteria
	 * @param pojo
	 */
	public HibernateRenderer(org.hibernate.Criteria criteria, String pojo) {
		this.criteria = criteria;
		this.classMetaData = HibernateUtil.getSessionFactory().getClassMetadata(pojo);
		this.aliasSet = new HashSet<String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitCriteria(com.code.aon.ql.Criteria)
	 */
	public void visitCriteria(Criteria criteria) {
		if (criteria.getExpression() != null) {
			criteria.getExpression().accept(this);
		}
		if (criteria.getOrderByList() != null) {
			criteria.getOrderByList().accept(this);
		}
		if (this.criterion != null) {
			this.criteria.add(this.criterion);
		}
		if ( distinctNeeded ) {
			this.criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		}
	}
	
	private void addCountProjection() {
		if ( distinctNeeded ) {
			String id = this.classMetaData.getIdentifierPropertyName();
			this.projectionList.add( Projections.countDistinct(id) );
		} else {
			this.projectionList.add(Projections.rowCount());
		}
	}
	
	public void visitProjection(Projection projection) {
		String propertyName = null;
		if ( projection.getExpression() != null ) {
			projection.getExpression().accept(this);
			propertyName = this.identifier;
		}
		switch ( projection.getType() ) {
			case COUNT:
				addCountProjection();
				break;
			case AVG:
				this.projectionList.add(Projections.avg(propertyName));
				break;
			case COUNT_DISTINCT:
				this.projectionList.add(Projections.countDistinct(propertyName));
				break;
			case GROUP:
				this.projectionList.add(Projections.groupProperty(propertyName));
				break;
			case PROPERTY:
				this.projectionList.add(Projections.property(propertyName));
				break;
			case MAX:
				this.projectionList.add(Projections.max(propertyName));
				break;
			case MIN:
				this.projectionList.add(Projections.min(propertyName));
				break;
			case SUM:
				this.projectionList.add(Projections.sum(propertyName));
				break;
		}
	}

	public void visitProjectionList(com.code.aon.ql.ProjectionList list) {
		this.projectionList = Projections.projectionList();
		for( Projection projection : list.getProjections() ) {
			projection.accept( this );
		}
		this.criteria.setProjection( this.projectionList );		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitOrderByList(com.code.aon.ql.OrderByList)
	 */
	public void visitOrderByList(OrderByList orderByList) {
		Iterator<Order> i = orderByList.getOrders().iterator();
		while (i.hasNext()) {
			Order order = i.next();
			order.accept(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitOrder(com.code.aon.ql.Order)
	 */
	public void visitOrder(Order order) {
		order.getExpression().accept(this);
		String propertyId = this.identifier;

		if (order.isAscending()) {
			this.criteria.addOrder(org.hibernate.criterion.Order.asc(propertyId));
		} else {
			this.criteria.addOrder(org.hibernate.criterion.Order.desc(propertyId));
		}
	}

	/**
	 * This method is called by a <code>LogicalOrExpression</code>. Concat
	 * "or" operator expressions.
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitLogicalOrExpression(LogicalOrExpression)
	 */
	public void visitLogicalOrExpression(LogicalOrExpression expression) {
		expression.getLeftExpression().accept(this);
		Criterion left = this.criterion;
		expression.getRightExpression().accept(this);
		Criterion right = this.criterion;
		this.criterion = Restrictions.or(left, right);
	}

	/**
	 * This method is called by a <code>LogicalAndExpression</code>. Concat
	 * "and" operator expressions.
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitLogicalAndExpression(LogicalAndExpression)
	 */
	public void visitLogicalAndExpression(LogicalAndExpression expression) {
		expression.getLeftExpression().accept(this);
		Criterion left = this.criterion;
		expression.getRightExpression().accept(this);
		Criterion right = this.criterion;
		this.criterion = Restrictions.and(left, right);
	}

	/**
	 * This method is called by a <code>NullExpression</code>. Concat "is
	 * null" operator expressions.
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitNullExpression(NullExpression)
	 */
	public void visitNullExpression(NullExpression expression) {
		expression.getExpression().accept(this);
		this.criterion = Restrictions.isNull(this.identifier);
	}

	/**
	 * This method is called by a <code>NotNullExpression</code>. Concat "is
	 * not null" operator expressions.
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitNotNullExpression(NotNullExpression)
	 */
	public void visitNotNullExpression(NotNullExpression expression) {
		expression.getExpression().accept(this);
		this.criterion = Restrictions.isNotNull(this.identifier);
	}
	
	private String getJoinTypeSuffix( int joinType ) {
		if ( joinType == CriteriaSpecification.INNER_JOIN ) {
			return INNER_JOIN_ALIAS_SUFFIX;
		} else if ( joinType == CriteriaSpecification.LEFT_JOIN ) {
			return LEFT_JOIN_ALIAS_SUFFIX;
		}
		return FULL_JOIN_ALIAS_SUFFIX;
	}

	private int getJoinType( char separator ) {
		if ( separator == '.' ) {
			return CriteriaSpecification.INNER_JOIN;
		} else if ( separator == '<' ) {
			return CriteriaSpecification.LEFT_JOIN;
		}
		return CriteriaSpecification.FULL_JOIN;
	}
	
	private String getReference( String parent, String property ) {
		return ( parent != null ? parent + "." : "" ) + property;
	}
	
	private String createAlias( String parentAlias, String property, int joinType ) {
		String suffix = getJoinTypeSuffix(joinType);
		String name = StringUtils.defaultString(parentAlias) + property + suffix;
		if (!this.aliasSet.contains(name)) {
			String reference = getReference(parentAlias, property); 
			this.criteria.createAlias(reference, name, joinType);
			this.aliasSet.add(name);
		}		
		return name;
	}
	
	private ClassMetadata getEntityMetaData( AssociationType associationType ) {
		ClassMetadata cmd = null;
		if ( associationType != null ) {
			SessionFactoryImpl sessionFactory = (SessionFactoryImpl) HibernateUtil.getSessionFactory();
			String entityName = associationType.getAssociatedEntityName( sessionFactory );
			cmd = sessionFactory.getClassMetadata( entityName );
		}
		return cmd;
	}
	
	private AssociationType getAssociationType( ClassMetadata parentCmd, String property ) {
        Type type = parentCmd.getPropertyType( property );
        if ( (type != null) && type.isAssociationType() ) {
        	return (AssociationType) type;
        }
        return null;
	}
	
	private boolean isDistinctNeeded( Type type ) {
		return (type != null) && type.isCollectionType();
	}
	
	/**
	 * This method is called by a <code>IdentExpression</code>. Print
	 * identifier name.
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitIdentExpression(IdentExpression)
	 */
	public void visitIdentExpression(IdentExpression expression) {
		this.fullIdentifier = expression.getName();
		int pos = this.fullIdentifier.indexOf('.');
		String id = this.fullIdentifier.substring(pos + 1);
		pos = StringUtils.indexOfAny( id, SEPARATORS );
		if ( pos != -1 ) {
			String parent = null;
			ClassMetadata cmd = this.classMetaData;
			while ( pos != -1 ) {
				String property = id.substring( 0, pos );
				int joinType = getJoinType( id.charAt(pos) );
				AssociationType type = getAssociationType( cmd, property );
				if ( isDistinctNeeded(type) ) {
					distinctNeeded = true;
				}
				cmd = getEntityMetaData( type );
				if ( cmd != null ) {
					parent = createAlias(parent, property, joinType);							
				} else {
					parent = getReference(parent, property);
				}
				id = id.substring( pos+1 );
				pos = StringUtils.indexOfAny( id, SEPARATORS );
			}
			id = getReference(parent, id);
		}
		this.identifier = id;
	}

	/**
	 * This method is called by a <code>ConstantExpression</code>. Print
	 * constant value quoted.
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitConstantExpression(ConstantExpression)
	 */
	public void visitConstantExpression(ConstantExpression expression) {
		if (expression.isLiteral()) {
			String propertyName = this.fullIdentifier;
			this.literal = getTypedObject(propertyName, expression.getData().toString());
		} else {
			this.literal = expression.getData();
		}
	}

	/**
	 * This method is called by a <code>BetweenExpression</code>. Print
	 * "{expr} between {minor} and {major}"
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitBetweenExpression(BetweenExpression)
	 */
	public void visitBetweenExpression(BetweenExpression expression) {
		expression.getLeftExpression().accept(this);
		String propertyId = this.identifier;
		expression.getMinorExpression().accept(this);
		Object minor = this.literal;
		expression.getMajorExpression().accept(this);
		Object major = this.literal;
		this.criterion = Restrictions.between(propertyId, minor, major);
	}

	/**
	 * This method is called by a <code>RelationalExpression</code>. Print 2
	 * expressions separated by its operator: <table border="1">
	 * <tr>
	 * <td>&lt;</td>
	 * <td><code>if ((expression.type & RelationalExpression.LT) > 0)</code></td>
	 * </tr>
	 * <tr>
	 * <td>&gt;</td>
	 * <td><code>if ((expression.type & RelationalExpression.GT) > 0)</code></td>
	 * </tr>
	 * <tr>
	 * <td>=</td>
	 * <td><code>if ((expression.type & RelationalExpression.EQ) > 0)</code></td>
	 * </tr>
	 * <tr>
	 * <td>&lt;&gt;</td>
	 * <td><code>if ((expression.type & RelationalExpression.NEQ) > 0)</code></td>
	 * </tr>
	 * <tr>
	 * <td>LIKE</td>
	 * <td><code>if ((expression.type & RelationalExpression.LIKE) > 0)</code></td>
	 * </tr>
	 * </table>
	 * 
	 * @param expression
	 * @see com.code.aon.ql.ast.CriterionVisitor#visitRelationalExpression(RelationalExpression)
	 */
	public void visitRelationalExpression(RelationalExpression expression) {
		expression.getLeftExpression().accept(this);
		String propertyId = this.identifier;

		expression.getRightExpression().accept(this);
		int type = expression.getType();
		
		if ( expression.getRightExpression() instanceof IdentExpression ) {
			String secondePropertyId = this.identifier;
			if ((type & RelationalExpression.LT) > 0) {
				this.criterion = Restrictions.ltProperty(propertyId, secondePropertyId);
			} else if ((type & RelationalExpression.GT) > 0) {
				this.criterion = Restrictions.gtProperty(propertyId, secondePropertyId);
			} else if ((type & RelationalExpression.EQ) > 0) {
				this.criterion = Restrictions.eqProperty(propertyId, secondePropertyId);
			} else if ((type & RelationalExpression.NEQ) > 0) {
				this.criterion = Restrictions.neProperty(propertyId, secondePropertyId);
			} else if ((type & RelationalExpression.LTE) > 0) {
				this.criterion = Restrictions.leProperty(propertyId, secondePropertyId);
			} else if ((type & RelationalExpression.GTE) > 0) {
				this.criterion = Restrictions.geProperty(propertyId, secondePropertyId);
			}
		} else {
			Object value = this.literal;
			if ((type & RelationalExpression.LT) > 0) {
				this.criterion = Restrictions.lt(propertyId, value);
			} else if ((type & RelationalExpression.GT) > 0) {
				this.criterion = Restrictions.gt(propertyId, value);
			} else if ((type & RelationalExpression.EQ) > 0) {
				this.criterion = Restrictions.eq(propertyId, value);
			} else if ((type & RelationalExpression.NEQ) > 0) {
				this.criterion = Restrictions.ne(propertyId, value);
			} else if ((type & RelationalExpression.LIKE) > 0) {
				this.criterion = Restrictions.like(propertyId, value);
			} else if ((type & RelationalExpression.LTE) > 0) {
				this.criterion = Restrictions.le(propertyId, value);
			} else if ((type & RelationalExpression.GTE) > 0) {
				this.criterion = Restrictions.ge(propertyId, value);
			}
		}
	}

	/**
	 * Return the exception if occurs during Criterion construction.
	 * 
	 * @return Exception
	 */
	public Exception getException() {
		return this.exception;
	}
	
	private Timestamp stringToTimestamp( String value, String pattern ) throws ParseException {
		return new Timestamp( new SimpleDateFormat(pattern).parse(value).getTime() );
	}
	
	private Object stringToDateTime( Type type, String value ) throws Exception {
		Object result = null;
		Class _class = type.getReturnedClass();
		if ( Date.class.isAssignableFrom(_class) ) {
			boolean time = TimeType.class.isAssignableFrom(_class);
			String pattern = System.getProperty(time ? TIME_PATTERN_PROPERTY : DATE_PATTERN_PROPERTY);	
			if ( pattern != null ) {
				result = new SimpleDateFormat(pattern).parse(value);
			}
		} else if ( Timestamp.class.isAssignableFrom(_class) ) {
			String pattern = System.getProperty(TIME_STAMP_PATTERN_PROPERTY);	
			if ( pattern != null ) {
				result = stringToTimestamp( value, pattern );				
			}
		} else if ( Calendar.class.isAssignableFrom(_class) ) {
			String pattern = System.getProperty(TIME_STAMP_PATTERN_PROPERTY);	
			if ( pattern != null ) {
				result = new GregorianCalendar();
				((Calendar)result).setTime( stringToTimestamp(value, pattern) );
			}
		}
		return result;		
	}
	
	private Object hibernateStringToObject( Type type, String value ) throws Exception {
		Object result = value;
		if (type instanceof IdentifierType) {
			result = ((IdentifierType) type).stringToObject(value);
		} else if (type instanceof NullableType) {
			result = ((NullableType) type).fromStringValue(value);
		}
		return result;
	}
	
	private Object stringToObject( Type type, String value ) throws Exception {
		if (!(type instanceof StringType)) {
			value = value.trim();
		}
		Object result = stringToDateTime( type, value );		
		if ( result == null ) {
			result = hibernateStringToObject(type, value);
		}
		return result;
	}
	
	private Type getType( String property ) {
		String propertyName = property.substring(StringUtils.indexOfAny( property, HibernateRenderer.SEPARATORS ) + 1);		
		Type type = HibernateUtil.getType(this.classMetaData, propertyName);
		if ( (type != null) && ((type instanceof IdentifierType) || (type instanceof NullableType))) {
			return type;
		}
		return null;
	}

	/**
	 * Return an object bound to the property name.
	 * 
	 * @param property
	 *            String Transfer Object property name.
	 * @param value
	 *            String literal constant.
	 * @return An object bound to the property name.
	 */
	private Object getTypedObject(String property, String value) {
		Object result = value;
		Type type = getType(property);
		if (type != null) {
			try {
				result = stringToObject(type, value);				
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error en el formato de la propiedad " + property + " con valor " + value
						+ " con tipo " + type, e);
				this.exception = new Exception("Error en el formato del campo de la busqueda", e);
			}
		} else {
			LOGGER.warning("No se ha encontrado el type para la propiedad " + property);
		}
		return result;
	}

}