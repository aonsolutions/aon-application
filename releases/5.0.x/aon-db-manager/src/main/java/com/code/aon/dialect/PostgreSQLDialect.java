package com.code.aon.dialect;

public class PostgreSQLDialect extends org.hibernate.dialect.PostgreSQLDialect {

	/**
	 * The syntax used to add a foreign key constraint to a table.
	 * 
	 * @param constraintName
	 *            The FK constraint name.
	 * @param foreignKey
	 *            The names of the columns comprising the FK
	 * @param referencedTable
	 *            The table referenced by the FK
	 * @param primaryKey
	 *            The explicit columns in the referencedTable referenced by this
	 *            FK.
	 * @param referencesPrimaryKey
	 *            if false, constraint should be explicit about which column
	 *            names the constraint refers to
	 * 
	 * @return the "add FK" fragment
	 */
	public String getAddForeignKeyConstraintString(String constraintName,
			String[] foreignKey, String referencedTable, String[] primaryKey,
			boolean referencesPrimaryKey) {
		StringBuffer res = new StringBuffer(super
				.getAddForeignKeyConstraintString(constraintName, foreignKey,
						referencedTable, primaryKey, referencesPrimaryKey));
		res.append( " ON DELETE RESTRICT ON UPDATE RESTRICT" );
		return res.toString();
	}

}
