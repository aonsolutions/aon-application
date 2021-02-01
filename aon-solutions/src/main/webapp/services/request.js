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
    : localStorage.getItem("company")
    ? JSON.parse(localStorage.getItem("company")).id
    : "";
  xhr.setRequestHeader("domain_id", domainId);
  const domainName = localStorage.getItem("aon_domain_name")
    ? localStorage.getItem("aon_domain_name")
    : localStorage.getItem("company")
    ? JSON.parse(localStorage.getItem("company")).domain
    : "";
  xhr.setRequestHeader("domain_name", domainName);
  const domainLogin = localStorage.getItem("aon_domain_login") || "";
  xhr.setRequestHeader("domain_login", domainLogin);
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

export const requestFile = (method, url, fn) => {
  const xhr = new XMLHttpRequest();
  xhr.open(method, url);
  xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
  xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
  xhr.responseType = "blob";
  xhr.send();
  xhr.onload = () => {
    xhr.status != 200 ? fn(undefined, xhr.response) : fn(xhr.response);
  };
  xhr.onerror = () => {};
};

export const get = (url, data) => {
  return new Promise((resolve, reject) => {
    request("GET", url, getToken(), data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const post = (url, data) => {
  return new Promise((resolve, reject) => {
    request("POST", url, getToken(), data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const remove = (url, data) => {
  return new Promise((resolve, reject) => {
    request("DELETE", url, getToken(), data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const getToken = () => localStorage.getItem("aon_session_id");

export const openPDF = async (url, data) => {
  const token = getToken();
  const domainId = localStorage.getItem("aon_domain_id");
  const domainName = localStorage.getItem("aon_domain_name");
  const datos = {
    ...data,
    domain_name: domainName,
    session_id: token,
    domain_id: domainId,
  };
  const json = btoa(JSON.stringify(datos));
  const newUrl = `${url}?json=${json}`;

  if (webkitRequestMobile())
    await openFileMobile(newUrl)
      .then(async (obj) => await actionRequestMobile(obj))
      .catch((e) => null);
  else openFileDesktop(newUrl);

  return;
};

const openFileMobile = async (url) =>
  new Promise((resolve, reject) => {
    requestFile("GET", url, (result, error) => {
      if (error) reject(error);
      else {
        const contentType = "application/pdf";
        const reader = new FileReader();
        reader.readAsDataURL(result);
        reader.onload = function () {
          const base64Str = reader.result
            .toString()
            .replace(/^data:.+;base64,/, "");
          const obj = {
            fileBase64: base64Str,
            fileName: "document.pdf",
            contentType,
            action: "fileDownload",
          };
          resolve(obj);
        };
        reader.onerror = function () {
          reject(true);
        };
      }
    });
  });

const openFileDesktop = (url) => open(url);

//if true is mobile APP
export const webkitRequestMobile = () => {
  let result = false;
  try {
    if ("undefined" !== typeof webkit && webkit.messageHandlers.cordova_iab) {
      result = true;
    }
  } catch (e) {}
  return result;
};

export const actionRequestMobile = (data) => {
  return new Promise((resolve) => {
    let result = false;
    try {
      if (webkitRequestMobile()) {
        let _webkit = webkit.messageHandlers.cordova_iab;
        if (data) _webkit.postMessage(JSON.stringify(data));
        result = true;
      }
    } catch (e) {
      console.log(e);
    }
    resolve(result);
  });
};
