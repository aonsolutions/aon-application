export const BIDOQ_METHOD = 'get_user_login';
export const BIDOQ_URL = 'https://dev.mispapeles.es/api/aon/v1/index.php';
export const BIDOQ_API_KEY = 'K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I';
export const AON_SESSION_ID = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJzY2hlbWFcIjpcImF5dWRhdC1hb25zb2x1dGlvbnMtbmV0XCIsXCJzY2hlbWFfZmlyc3RfZG9tYWluXCI6XCIwMDIyNDIwMzllLWF5dWRhdC5hb25zb2x1dGlvbnMubmV0XCIsXCJ1dWlkXCI6XCJFNkFGMjg1NEI2NjYxMUVBODMyMzA2QTBCREQ3MkE0NlwifSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjAwNzkzNDgyfQ.4O-z1Hldqz1WAmX7kcsBkRlb0zy64ucYXQIoLnDL7mA';


export const bidoq = () => {
  const sendData = `api_key=${BIDOQ_API_KEY}&method=${BIDOQ_METHOD}&session_id=${AON_SESSION_ID}`;
  const sendDataJSON = {
    api_key: BIDOQ_API_KEY,
    method: BIDOQ_METHOD,
    session_id: AON_SESSION_ID
  };
  return new Promise( (resolve, reject) => {
    requestBidoq('POST', BIDOQ_URL, sendData, (result, error) => {
      if(error) {
        reject(error);
      } else {
        resolve(result);
      }
    });
  });
}

export const requestBidoq = (method, url, sendData, fn) => {
  let xhr = new XMLHttpRequest();
  xhr.open(method, url);
  xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
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
