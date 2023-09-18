// AON-MOCK-ENTITY ${date}
package ${aonPackage};

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
<#if hasUniqueConstraint>
import jakarta.persistence.UniqueConstraint;
</#if>
import ${generatedPackage}.${generatedEntity};

@Entity
${table}
public class ${aonEntity} extends ${generatedEntity} {
	
	private static final long serialVersionUID = 1L;
	
} 
