
	export const requestFile = (url, token, formData) => {
		let xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("aon_domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		xhr.send(formData);
	}

	export const request = (method, url, token, sendData, fn) => {
		let xhr = new XMLHttpRequest();
		xhr.open(method, url);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("aon_domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		const domainName = localStorage.getItem("aon_domain_name");
		xhr.setRequestHeader('domain_name', domainName);
		xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
		xhr.setRequestHeader('Access-Control-Allow-Origin', '*');

		xhr.send(JSON.stringify(sendData));

		xhr.onload = () => {
			if (xhr.status != 200) { // analyze HTTP status of the response
				console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
				fn(undefined, xhr.response);
			} else { // show the result
				console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
				fn(xhr.response);
			}
		};

		xhr.onprogress = (event) => {
			if (event.lengthComputable) {
				console.log(`Received ${event.loaded} of ${event.total} bytes`);
			} else {
				console.log(`Received ${event.loaded} bytes`); // no Content-Length
			}
		};

		xhr.onerror = () => {
			console.log("Request failed");
		};
	}


	export const requestBidoq = (method, url, sendData, fn) => {
	  let xhr = new XMLHttpRequest();
	  xhr.open(method, url);
	  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	  xhr.send(sendData);

	  xhr.onload = () => {
	    if (xhr.status != 200) { // analyze HTTP status of the response
	      console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
	      fn(undefined, xhr.response);
	    } else { // show the result
	      console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
	      fn(xhr.response);
	    }
	  };

	  xhr.onprogress = (event) => {
	    if (event.lengthComputable) {
	      console.log(`Received ${event.loaded} of ${event.total} bytes`);
	    } else {
	      console.log(`Received ${event.loaded} bytes`); // no Content-Length
	    }
	  };

	  xhr.onerror = () => {
	    console.log("Request failed");
	  };
	}
