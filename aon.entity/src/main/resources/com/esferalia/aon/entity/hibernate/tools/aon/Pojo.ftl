// AON-ENTITY ${date} - ${version}
package ${aonPackage};

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.code.aon.AonVersion;
import ${generatedPackage}.${generatedEntity};

@Entity
${table}
public class ${aonEntity} extends ${generatedEntity} {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
} 
