
function dump(connection, domain, stream) {
	stream.write( "var enterprises = enterprises || [];\r\n" );
	stream.write( "var employees  = employees || [];\r\n" );
	return new Promise(( resolve, reject ) => {
		connection.query("SHOW DATABASES", function(err, results, fields) {
			if ( err ) throw err;
			const databases = results.map( d => d.Database );
			databases.forEach( database => {
				dumpEnterprise(connection, database, domain )
				.then( enterprises => { 
					enterprises.forEach( enterprise => stream.write( "enterprises.push(" + JSON.stringify(toEnterprise(enterprise)) +");\r\n" )); 
					return dumpEmployee(connection, database, domain)
				})
				.then ( employees => {
					employees.forEach ( employee => stream.write( "employees.push(" + JSON.stringify(toEmployee(employee)) +");\r\n" ));
					resolve( {} );
				})
				.catch( err =>{
					//reject(err);
				 });
			});
		});
	});
	
}
 
function toEnterprise(enterprise) {
	return { 
		__id: enterprise.id,
		name : enterprise.name,
		document : enterprise.document
	};
}

function toEmployee(employee) {
	return { 
		__id: employee.id,
		name : employee.name,
		document : employee.document
	};
}

function dumpEnterprise(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" domain.id AS id"
			+", registry.name as name"
			+", registry.document as document"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`company` ON (  `domain`.`id` = `company`.`domain` )"
			+ " INNER JOIN `" + database + "`.`enterprise` ON (  `company`.`registry` = `enterprise`.`registry` )"
			+ " INNER JOIN `" + database + "`.`registry` ON (  `enterprise`.`registry` = `registry`.`id` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

function dumpEmployee(connection, database, domain ) {
	return new Promise(( resolve, reject ) => {
		connection.query( { 
			sql:"SELECT"
			+" contract.id AS id"
			+", registry.name as name"
			+", registry.document as document"
			+ " FROM `" + database + "`.`domain`"
			+ " INNER JOIN `" + database + "`.`contract` ON (  `domain`.`id` = `contract`.`domain` )"
			+ " INNER JOIN `" + database + "`.`person` ON (  `contract`.`person` = `person`.`registry` )"
			+ " INNER JOIN `" + database + "`.`registry` ON (  `person`.`registry` = `registry`.`id` )"
			+ " WHERE `domain`.`name` = ? "
			,
			values: [domain]
		}, 
		function( err, results, fields ){
			if ( err ) {
				reject(err);
			} else if ( results.length > 0 ) {
				resolve( results );		
			}
		});
	});
}

export default dump;
