//$Id: CtsqlDialect.java,v 1.7 2008/07/18 08:01:58 fermin Exp $
package com.transtools.ctsql.hibernate.dialect;

import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.sql.JoinFragment;
import org.hibernate.util.StringHelper;

/**
 * Multibase Ctsql dialect.<br>
 * <br>
 * @author Fermin Fernandez
 */
public class CtsqlDialect extends Dialect {

	/**
	 * Creates new <code>CtsqlDialect</code> instance. Sets up the JDBC /
	 */
	public CtsqlDialect() {
		super();

		registerColumnType(Types.BIGINT, "integer");
		registerColumnType(Types.BINARY, "binary($l)");
		registerColumnType(Types.BIT, "smallint"); 
		registerColumnType(Types.CHAR, "char($l)");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.DECIMAL, "decimal");
		registerColumnType(Types.DOUBLE, "decimal");
		registerColumnType(Types.FLOAT, "decimal");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.LONGVARBINARY, "binary"); // or BYTE
		registerColumnType(Types.LONGVARCHAR, "char"); // or TEXT?
		registerColumnType(Types.NUMERIC, "decimal"); // or MONEY
		registerColumnType(Types.REAL, "decimal");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIMESTAMP, "datetime");
		registerColumnType(Types.TIME, "time");
		registerColumnType(Types.TINYINT, "smallint");
		registerColumnType(Types.VARBINARY, "binary($l)");
		registerColumnType(Types.VARCHAR, "char($l)");
		registerColumnType(Types.VARCHAR, 255, "char($l)");
		registerColumnType(Types.VARCHAR, 32739, "char($l)");

		registerHibernateType( Types.CHAR, Hibernate.STRING.getName() );

		registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "(", "||", ")" ) );
	}

	public String getAddColumnString() {
		return "add";
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public String getIdentitySelectString() {
		return "select last_insert_id()";
	}

	public String getIdentityColumnString(int type) throws MappingException {
		return "serial not null";
	}

	public boolean hasDataTypeInIdentityColumn() {
		return false;
	}

	public String getAddForeignKeyConstraintString(
			String constraintName, 
			String[] foreignKey, 
			String referencedTable, 
			String[] primaryKey, boolean referencesPrimaryKey
	) {
		StringBuffer result = new StringBuffer(30);
		
		result.append(" add  ")
			.append(" foreign key " +  constraintName + " (")
			.append( StringHelper.join(", ", foreignKey) )
			.append(") references ")
			.append(referencedTable);
		
		if(!referencesPrimaryKey) {
			result.append(" (")
			   .append( StringHelper.join(", ", primaryKey) )
			   .append(')');
		}

		result.append(" on update restrict on delete restrict");
			
			return result.toString();
	}

	/**
	 * The syntax used to add a primary key constraint to a table.
	 * @return String
	 */
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " primary key  " + constraintName + " ";
	}

	/*public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName + " restrict";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select " + getSelectSequenceNextValString( sequenceName ) + " from systables where tabid=1";
	}

	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}*/

	public boolean supportsSequences() {
		return false;
	}

	public boolean supportsLimit() {
		return true;
	}

	/*
	public boolean useMaxForLimit() {
		return true;
	}
	*/

	/*
	 * Temporary, until MySQL fix Connector/J bug
	 */
	public String getLimitString(String sql, int offset, int limit) {
		StringBuffer buf = new StringBuffer( sql.length()+20 )
			.append(sql);
		if (offset>0) {
			buf.append(" limit ")
				.append(offset)
				.append(", ")
				.append(limit);
		}
		else {
			buf.append(" limit ")
				.append(limit);
		}
		return buf.toString();
	}
	
/*	public String getLimitString(String querySelect, int offset, int limit) {
		if (offset>0) throw new UnsupportedOperationException("informix has no offset");
		return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( querySelect.toLowerCase().indexOf( "select" ) + 6, " first " + limit )
			.toString();
	}
*/
	public boolean supportsVariableLimit() {
		return false;
	}

	
//	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
//        return EXTRACTER;
//	}
//
//	private static ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
//
//		/**
//		 * Extract the name of the violated constraint from the given SQLException.
//		 *
//		 * @param sqle The exception that was the result of the constraint violation.
//		 * @return The extracted constraint name.
//		 */
//		public String extractConstraintName(SQLException sqle) {
//			String constraintName = null;
//			
//			int errorCode = JDBCExceptionHelper.extractErrorCode(sqle);
////			.-1299
////			Impossible to INSERT row.  Duplicated value in column with UNIQUE INDEX.
////			.-1202
////			Impossible to INSERT/UPDATE.  There is no reference in table "%s".
////			.-1196
////			Impossible to DELETE/UPDATE.  There are rows in table "%s" pointing to this row.
//			if ( errorCode == -268 ) {
//				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
//			}
//			else if ( errorCode == -691 ) {
//				constraintName = extractUsingTemplate( "Missing key in referenced table for referential constraint (", ").", sqle.getMessage() );
//			}
//			else if ( errorCode == -692 ) {
//				constraintName = extractUsingTemplate( "Key value for constraint (", ") is still being referenced.", sqle.getMessage() );
//			}
//			
//			if (constraintName != null) {
//				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
//				int i = constraintName.indexOf('.');
//				if (i != -1) {
//					constraintName = constraintName.substring(i + 1);
//				}
//			}
//
//			return constraintName;
//		}
//
//	};

	public boolean supportsCurrentTimestampSelection() {
		return true;
	}

	public boolean isCurrentTimestampSelectStringCallable() {
		return false;
	}

	/*
	public String getCurrentTimestampSelectString() {
		return "select distinct current timestamp from informix.systables";
	}
	*/
	
	public JoinFragment createOuterJoinFragment() {
		return new CtsqlJoinFragment();
	}
	
	@Override
	public int getMaxAliasLength() {
		return 8;
	}

}