package com.code.aon.department;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.department.ast.INode;
import com.code.aon.department.ast.INodeVisitor;

@Entity
@Table(name="department")
public class Department implements ITransferObject, INode {
	
	/**
	 * Identificador Único
	 */
	private Integer id;
	
	/**
	 * Nodo padre
	 */
	private Department parent;
	
	/**
	 * Descripción
	 */
	private String description;
	
	
	public Department() {
		super();
	}

	public Department(Integer id, Department parent, String description) {
		super();
		this.id = id;
		this.parent = parent;
		this.description = description;
	}

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
    @JoinColumn(name="parent")
	public Department getParent() {
		return parent;
	}

	public void setParent(Department parent) {
		this.parent = parent;
	}

	@Column(length=32)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void accept(INodeVisitor visitor) {
		visitor.visitDepartment(this);
	}
}
