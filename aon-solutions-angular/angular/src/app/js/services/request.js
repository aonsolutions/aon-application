	window.request = request;
	window.requestFile = requestFile;

	function requestFile(url, token, formData){
		var xhr = new XMLHttpRequest();
		xhr.open('POST', "/tedi-center" + url, true);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		xhr.send(formData);
	}

	function request(method, url, token, sendData, fn){
		let xhr = new XMLHttpRequest();
		xhr.open(method, "/tedi-center" + url);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
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
