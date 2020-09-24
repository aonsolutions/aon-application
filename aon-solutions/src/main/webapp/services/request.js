
	export const requestFile = (url, token, formData) => {
		var xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("aon_domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		xhr.send(formData);
	}

	export const request = (method, url, token, sendData, headers, fn) => {
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
		//xhr.setRequestHeader('api_key', 'K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I');
	  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	  //xhr.setRequestHeader('Access-Control-Allow-Origin', '*');

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
