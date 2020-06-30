import { Observable, Observer } from 'rxjs';

export class Request {

	static requestFile(url: string, token: string, formData: any){
		var xhr = new XMLHttpRequest();
		xhr.open('POST', url, true);
		xhr.setRequestHeader('session_id', token);
		const domainId = localStorage.getItem("domain_id");
		xhr.setRequestHeader('domain_id', domainId);
		xhr.send(formData);
	}

	static request(method: string, url: string, token: string, sendData?: any, headers?: any) : Observable<any>{
		return Observable.create((observer: Observer<any>) => {
			let xhr = new XMLHttpRequest();
			xhr.open(method, /*'/tedi-angular'+*/ url);
			xhr.setRequestHeader('session_id', token);
			const domainId = localStorage.getItem("aon_domain_id");
			xhr.setRequestHeader('domain_id', domainId);
			const domainName = localStorage.getItem("aon_domain_name");
			xhr.setRequestHeader('domain_name', domainName);
			xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
			if(headers) {
				xhr.setRequestHeader("schema", headers.schema);
				xhr.setRequestHeader("page", headers.page);
				xhr.setRequestHeader("per_page", headers.per_page);
			}
			xhr.send(JSON.stringify(sendData));

			xhr.onload = () => {
				if (xhr.status != 200) { // analyze HTTP status of the response
					console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
					observer.error(xhr.response);
				} else { // show the result
					console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
					observer.next(JSON.parse(xhr.response));
					observer.complete();
				}
			};

			xhr.onprogress = (event) => {
				if (event.lengthComputable) {
					console.log(`Received ${event.loaded} of ${event.total} bytes`);
				} else {
					console.log(`Received ${event.loaded} bytes`); // no Content-Length
				}
			};

			xhr.onerror = (error) => {
				console.log('Request failed');
				observer.error(error);
			};
		});
	}
}
