const formatParams = (params) => {
  return (
    "?" +
    Object.keys(params)
      .map((key) => key + "=" + encodeURIComponent(params[key]))
      .join("&")
  );
};

export const request = (method, url, token, sendData, fn) => {
  let xhr = new XMLHttpRequest();
  if (sendData && method === "GET") url = url + formatParams(sendData); //send params url method GET
  xhr.open(method, url);
  xhr.setRequestHeader("session_id", token);
  const domainId = localStorage.getItem("aon_domain_id")
    ? localStorage.getItem("aon_domain_id")
    : (localStorage.getItem("company") ? JSON.parse(localStorage.getItem("company")).id : '');
  xhr.setRequestHeader("domain_id", domainId);
  const domainName = localStorage.getItem("aon_domain_name")
    ? localStorage.getItem("aon_domain_name")
    : (localStorage.getItem("company") ? JSON.parse(localStorage.getItem("company")).domain : '');
  xhr.setRequestHeader("domain_name", domainName);
  xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
  xhr.send(JSON.stringify(sendData));

  xhr.onload = () => {
    if (xhr.status != 200) {
      // analyze HTTP status of the response
      console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
      fn(undefined, xhr.response);
    } else {
      // show the result
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
};

export const requestBidoq = (method, url, sendData, fn) => {
  let xhr = new XMLHttpRequest();
  xhr.open(method, url);
  xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhr.send(sendData);

  xhr.onload = () => {
    if (xhr.status != 200) {
      // analyze HTTP status of the response
      console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
      fn(undefined, xhr.response);
    } else {
      // show the result
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
};

export const get = (url, data) => {
  return new Promise((resolve, reject) => {
    request("GET", url, getToken(), data, (result, error) => {
      if (error) reject(error);
      else resolve(JSON.parse(result));
    });
  });
};

export const post = (url, data) => {
  return new Promise((resolve, reject) => {
    request("POST", url, getToken(), data, (result, error) => {
      if (error) reject(error);
      else resolve(JSON.parse(result));
    });
  });
};

export const remove = (url, data) => {
  return new Promise((resolve, reject) => {
    request("DELETE", url, getToken(), data, (result, error) => {
      if (error) reject(error);
      else resolve(JSON.parse(result));
    });
  });
};

export const getToken = () => localStorage.getItem("aon_session_id");


export const openPDF = (url, data) => {
  let token = getToken();
  let domainId = localStorage.getItem("aon_domain_id");
  let domainName = localStorage.getItem("aon_domain_name");
  let datos = {
    ...data,
    domain_name: domainName,
    session_id: token,
    domain_id: domainId
  }
  let json = btoa(JSON.stringify(datos));
  open(`${url}?json=${json}`)
}
