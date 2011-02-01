package com.transtools.ctsql.hibernate.dialect;

import org.hibernate.sql.JoinFragment;



/**
 *
 * @author Fermin Fernandez
 */
public class CtsqlJoinFragment extends JoinFragment {

	private StringBuffer afterFrom = new StringBuffer();
	private StringBuffer afterWhere = new StringBuffer();
	
	private JoinTreeNode root = null;

	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType) {

		//addCrossJoin(tableName, alias, joinType);
		addToTree(tableName, alias,  fkColumns, pkColumns, joinType);

		for ( int j=0; j<fkColumns.length; j++) {
			//full joins are not supported.. yet!
			if (joinType==JoinFragment.FULL_JOIN ) throw new UnsupportedOperationException();

			afterWhere.append(" and ")
				.append( fkColumns[j] )
				.append( " " );

			//if (joinType==LEFT_OUTER_JOIN ) afterWhere.append("*");
			afterWhere.append('=');
			//if (joinType==RIGHT_OUTER_JOIN ) afterWhere.append("*");

			afterWhere.append (" ")
				.append(alias)
				.append('.')
				.append( pkColumns[j] );
		}

	}

	private void addToTree(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType) {
		JoinTreeNode newNode = new JoinTreeNode(tableName, alias, joinType);
		if(root == null){
			root = newNode;
		}else{
			String leftTable = getTableFromColumn(fkColumns[0]);
			JoinTreeNode leftNode = findLeftTable(root, leftTable);
			if(leftNode == null){
				appendNode(root, newNode);
			}else{
				addChildNode(leftNode, newNode);
			}
		}
	}

	private void addChildNode(JoinTreeNode leftNode, JoinTreeNode newNode) {
		if(leftNode.getChildren() == null){
			leftNode.setChildren(newNode);
		}else{
			appendNode(leftNode.getChildren(), newNode);
		}
	}

	private void appendNode(JoinTreeNode leftNode, JoinTreeNode newNode) {
		while(leftNode.getNext() != null){
			leftNode = leftNode.getNext();
		}
		leftNode.setNext(newNode);
	}

	private JoinTreeNode findLeftTable(JoinTreeNode from, String tableAlias) {
		while(from != null){
			if(from.getTableAlias().equals(tableAlias)){
				return from;
			}
			if(from.getChildren() != null){
				JoinTreeNode node = findLeftTable(from.getChildren(), tableAlias);
				if(node != null){
					return node;
				}
			}
			from = from.getNext();
		}
		return null;
	}

	private String getTableFromColumn(String columnName) {
		if(columnName != null){
			int idx = columnName.indexOf('.');
			if(idx > 0){
				return columnName.substring(0, idx);
			}			
		}
		return null;
	}

	private void appendOuterTree(JoinTreeNode node, StringBuffer buffer) {
		if(node == null){
			return;
		}
		while(node != null){
			if(node.getJoinType() == JoinFragment.LEFT_OUTER_JOIN){
				buffer.append(", outer ");
				if(node.getChildren() != null){
					buffer.append("(");					
				}
			}else{  // Asumo JoinFragment.INNER_JOIN 
				buffer.append(", ");
			}
			buffer.append(node.getTableName())
				.append(' ')
				.append(node.getTableAlias());
			if(node.getChildren() != null){
				appendOuterTree(node.getChildren(), buffer);
				if(node.getJoinType() == JoinFragment.LEFT_OUTER_JOIN){
					buffer.append(")");
				}
			}
			node = node.getNext();
		}
	}
	
	public String toFromFragmentString() {
		appendOuterTree(root, afterFrom);
		return afterFrom.toString();
	}


	public String toWhereFragmentString() {
		return afterWhere.toString();
	}

	public void addJoins(String fromFragment, String whereFragment) {
		afterFrom.append(fromFragment);
		afterWhere.append(whereFragment);
	}

	public JoinFragment copy() {
		CtsqlJoinFragment copy = new CtsqlJoinFragment();
		copy.afterFrom = new StringBuffer( afterFrom.toString() );
		copy.afterWhere = new StringBuffer( afterWhere.toString() );
		return copy;
	}

	public void addCondition(String alias, String[] columns, String condition) {
		for ( int i=0; i<columns.length; i++ ) {
			afterWhere.append(" and ")
				.append(alias)
				.append('.')
				.append( columns[i] )
				.append(condition);
		}
	}

	public void addCrossJoin(String tableName, String alias, int joinType) {
		afterFrom.append(", ");
		if(joinType == LEFT_OUTER_JOIN){
			afterFrom.append("outer ");			
		}
		afterFrom.append(tableName)
			.append(' ')
			.append(alias);
	}

	public void addCrossJoin(String tableName, String alias) {
		afterFrom.append(", ")
			.append(tableName)
			.append(' ')
			.append(alias);
	}

	public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
		throw new UnsupportedOperationException();

	}

	public boolean addCondition(String condition) {
		return addCondition(afterWhere, condition);
	}


	public void addFromFragmentString(String fromFragmentString) {
		afterFrom.append(fromFragmentString);
	}


	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType, String on) {
		addJoin(tableName, alias, fkColumns, pkColumns, joinType);
		addCondition(on);
	}
}

class JoinTreeNode{
	private JoinTreeNode next;
	private String tableName;
	private String tableAlias;
	private JoinTreeNode children;
	private int joinType;

	public JoinTreeNode(String tableName, String tableAlias, int joinType){
		this.tableName = tableName;
		this.tableAlias = tableAlias;
		this.joinType = joinType;
	}

	protected JoinTreeNode getChildren() {
		return children;
	}

	protected void setChildren(JoinTreeNode children) {
		this.children = children;
	}

	protected JoinTreeNode getNext() {
		return next;
	}

	protected void setNext(JoinTreeNode next) {
		this.next = next;
	}

	protected String getTableAlias() {
		return tableAlias;
	}

	protected void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	protected String getTableName() {
		return tableName;
	}

	protected void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getJoinType() {
		return joinType;
	}	
	
}
