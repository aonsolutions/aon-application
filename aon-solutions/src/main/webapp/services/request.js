	window.request = request;
	window.requestFile = requestFile;

	function requestFile(url, token, formData){
		var xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("aon_domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		xhr.send(formData);
	}

	function request(method, url, token, sendData, headers, fn){
		let xhr = new XMLHttpRequest();
		xhr.open(method, url);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("aon_domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		const domainName = localStorage.getItem("aon_domain_name");
		xhr.setRequestHeader('domain_name', domainName);
		xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
		xhr.setRequestHeader('Access-Control-Allow-Origin', '*');

		if(headers) {
			xhr.setRequestHeader("schema", headers.schema);
			xhr.setRequestHeader("page", headers.page);
			xhr.setRequestHeader("per_page", headers.per_page);
		}

		xhr.send(JSON.stringify(sendData));

		xhr.onload = function() {
			if (xhr.status != 200) { // analyze HTTP status of the response
				console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
			} else { // show the result
				console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
				fn(xhr.response);
			}
		};

		xhr.onprogress = function(event) {
			if (event.lengthComputable) {
				console.log(`Received ${event.loaded} of ${event.total} bytes`);
			} else {
				console.log(`Received ${event.loaded} bytes`); // no Content-Length
			}
		};

		xhr.onerror = function() {
			console.log("Request failed");
		};
	}
