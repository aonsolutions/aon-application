import { extensionsEnums } from "./extensionsEnums.js";

const formatParams = (params) => {
  return (
    "?" +
    Object.keys(params)
      .map((key) => key + "=" + encodeURIComponent(params[key]))
      .join("&")
  );
};

const xmlHttpRequestAon = (method, url, token, sendData) =>{
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
  return xhr;
}

export const request = (method, url, token, sendData, fn) => {
  let xhr = xmlHttpRequestAon(method, url, token, sendData);
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

export const requestFile = (method, url, sendData, fn) => {
  try {
    const xhr = xmlHttpRequestAon(method, url, getToken(), sendData);
    xhr.onreadystatechange = () =>  {
       if(xhr.readyState == 2 && xhr.status == 200) {xhr.responseType = "blob";}
    }
    xhr.onload = () => {
      let fileName = "document";
      try {fileName = xhr.getResponseHeader('Content-Disposition').split('filename=')[1].split(';')[0].toString().replace(/"/g, '')} catch (e) {}
      if(xhr.status != 200){
        let response =  typeof  xhr.response === "string" ? JSON.parse(xhr.response) : xhr.response;
        fn(undefined,  response)
      } else {
        fn({blob:xhr.response,fileName});
      }
    };
    xhr.send(JSON.stringify(sendData));
    xhr.onerror = () => {console.log("error");};
  } catch (error) {
    fn(undefined, error);
  }

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

const blobToBase64 = blob => new Promise((resolve, reject) => {
  const reader = new FileReader();
  reader.readAsDataURL(blob);
  reader.onload = () => resolve(reader.result);
  reader.onerror = error => reject(error);
});

/**
 * 
 * @param {String} base64Data 
 * @param {String} fileName 
 * @returns {Object} Object {fileBase64, fileName, contentType, action}
 */
const objFileMobile = (base64Data, fileName = undefined) => {
  const base64Str = base64Data.replace(/^data:.+;base64,/, "");
  const contentType = base64Data.match(/[^:]\w+\/[\w-+\d.]+(?=;|,)/)[0];
  if(!fileName){
    fileName = "document";
    const extension = extensionsEnums[contentType];
    if(contentType&&extension) fileName = `${fileName}.${extension}`;
  }
  return {
    fileBase64: base64Str,
    fileName,
    contentType,
    action: "fileDownload",
  };
}

export const openFile = async (url, data) => new Promise(async (resolve, reject) => {
  requestFile("GET", url, data, async(result, error) => {
    if (error) reject(error);
    else {
      const {blob, fileName} = result;
      if (webkitRequestMobile()){
         //------------ IS MOBILE APP---------
        const base64Data = await blobToBase64(blob).catch(e=>reject(e));
        const obj = objFileMobile(base64Data, fileName);
        await actionRequestMobile(obj);
      } else {
        // ------------IS DESKTOP---------------
        const newUrl = URL.createObjectURL(blob);
        openFileDesktop(newUrl);
        setTimeout(()=>{ URL.revokeObjectURL(url);},50);
      }
      resolve(true);
    }
  });
});

export const openFileDesktop = (url) => open(url);

//if true is mobile APP
export const webkitRequestMobile = () => {
  let result = false;
  try {
    if ("undefined" !== typeof window.cordova_iab || ("undefined" !== typeof window.webkit && window.webkit.messageHandlers) ) {
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

const requestFileUrl = (method, url, fn) => {
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

export const openFileMobile = async (url) => new Promise((resolve, reject) => {
    requestFileUrl("GET", url, (result, error) => {
      if (error) reject(error);
      else {
        const reader = new FileReader();
        reader.readAsDataURL(result);
        reader.onload = function () {
          const obj = objFileMobile(reader.result.toString());
          resolve(obj);
        };
        reader.onerror = function () {
          reject(true);
        };
      }
    });
});