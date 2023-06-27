import { CONSTANT, MSG, SIG_DOMAIN_ID, SIG_DOMAIN_NAME } from "../environments/environments.js";
import { extensionsEnums } from "./extensionsEnums.js";

const formatParams = (params) => {
  return (
    "?" +
    Object.keys(params)
      .map((key) => key + "=" + encodeURIComponent(params[key]))
      .join("&")
  );
};

export const getToken = () => localStorage.getItem("aon_session_id");

export const domainId = () =>  localStorage.getItem("aon_domain_id") ? localStorage.getItem("aon_domain_id") : localStorage.getItem("company")
? JSON.parse(localStorage.getItem("company")).id: "";

export const domainName = () => localStorage.getItem("aon_domain_name") ? localStorage.getItem("aon_domain_name") : localStorage.getItem("company")
? JSON.parse(localStorage.getItem("company")).domain : "";

export const domainLogin = () => localStorage.getItem("aon_domain_login") || "";

const xmlHttpRequestAon = (method, url, token, sendData) =>{
  let header = {
    "session_id": token,
    "domain_id": domainId(),
    "domain_name": domainName(),
    "domain_login": domainLogin(),
    "Content-Type": "application/json;charset=UTF-8",
    "Access-Control-Allow-Origin": "*"
  }
  return xmlHttpRequest(method, url, header, sendData);
}

const xmlHttpRequestXml = (method, url, sendData) =>{
  const header = {
    "Content-Type": "text/xml",
    "Access-Control-Allow-Origin": "*"
  }
  return xmlHttpRequest(method, url, header, sendData);
}

const xmlHttpRequestInvofox = (method, url, sendData) =>{
  const header = {
    "x-api-key": "$2b$10$ZyMOXKSmPwl4VUFk76wFWuK9aCDsXRiaxytOwpqk3gK.epVl6Mfwi",
    "Content-Type": "application/json;charset=UTF-8",
    "Access-Control-Allow-Origin": "*"
  }
  return xmlHttpRequest(method, url, header, sendData);
}

const xmlHttpRequest = (method, url, header, sendData) =>{
  let xhr = new XMLHttpRequest();
  if (sendData && method === "GET") url = url + formatParams(sendData); //send params url method GET
  xhr.open(method, url);
  for (let name in header) {
    xhr.setRequestHeader(name, header[name]);
  }
  return xhr;
}

export const requestInvofox = (method, url, sendData, fn) => {
  try {
    let xhr = xmlHttpRequestInvofox(method, url, sendData);
    xhr.send(JSON.stringify(sendData));
    xhr.onload = () => {
      if (xhr.status != 200) {
        // analyze HTTP status of the response
        console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
        fn(undefined, xhr.response);
      } else {
        // show the result
        console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
        let response = !xhr.response ? "[]" : xhr.response;
        fn(response);
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
  } catch (error) {
    console.log("error");
    fn(undefined, error);
  }
}

export const request = (method, url, token, sendData, fn) => {
  try {
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
        let response = !xhr.response ? "[]" : xhr.response;
        fn(response);
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
  } catch (error) {
    console.log("error");
    fn(undefined, error);
  }
};

export const requestXml = (method, url, sendData, fn) => {
  try {
    let xhr = xmlHttpRequestXml(method, url, sendData);
    xhr.send(sendData);
    xhr.onload = () => {
      if (xhr.status != 200) {
        // analyze HTTP status of the response
        console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
        fn(undefined, xhr.response);
      } else {
        // show the result
        console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
        let response = !xhr.response ? "[]" : xhr.response;
        fn(response);
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
  } catch (error) {
    console.log("error");
    fn(undefined, error);
  }
};

export const requestSig = (method, url, token, sendData, fn) => {
  try {
    let xhr = new XMLHttpRequest();
    if (sendData && method === "GET") url = url + formatParams(sendData); //send params url method GET
    xhr.open(method, url);
    xhr.setRequestHeader("session_id", token);
    xhr.setRequestHeader("domain_id", SIG_DOMAIN_ID);
    xhr.setRequestHeader("domain_name", SIG_DOMAIN_NAME);
    xhr.setRequestHeader("domain_login", domainLogin());
    xhr.send(JSON.stringify(sendData));
    xhr.onload = () => {
      if (xhr.status != 200) {
        // analyze HTTP status of the response
        console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
        fn(undefined, xhr.response);
      } else {
        // show the result
        console.log(`Done, got ${xhr.response.length} bytes`); // responseText is the server
        let response = !xhr.response ? "[]" : xhr.response;
        fn(response);
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
  } catch (error) {
    console.log("error");
    fn(undefined, error);
  }
};

export const requestPro = (method, url, sendData, fn) => {
  try {
    let xhr = new XMLHttpRequest();
    if (sendData && method === "GET") url = url + formatParams(sendData); //send params url method GET
    xhr.open(method, url);
    xhr.setRequestHeader("session_id", "AONd95770f269e711eb94390242ac130002");
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
        let response = !xhr.response ? "[]" : xhr.response;
        fn(response);
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
  } catch (error) {
    console.log("error");
    fn(undefined, error);
  }
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

export const getInvofox = (url, data) => {
  return new Promise((resolve, reject) => {
    requestInvofox("GET", url, data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const getPro = (url, data) => {
  return new Promise((resolve, reject) => {
    requestPro("GET", url, data, (result, error) => {
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

export const postPro = (url, data) => {
  return new Promise((resolve, reject) => {
    requestPro("POST", url, data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const postXml = (url, data) => {
  return new Promise((resolve, reject) => {
    requestXml("POST", url, data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const put = (url, data) => {
  return new Promise((resolve, reject) => {
    request("PUT", url, getToken(), data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

export const putPro = (url, data) => {
  return new Promise((resolve, reject) => {
    requestPro("PUT", url, data, (result, error) => {
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

export const removePro = (url, data) => {
  return new Promise((resolve, reject) => {
    requestPro("DELETE", url, data, (result, error) => {
      try{
        if (error) reject(error);
        else resolve(JSON.parse(result));
      } catch(e){reject(e);}
    });
  });
};

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
 * @param {String} type contentType
 * @returns {Object} Object {fileBase64, fileName, contentType, action}
 */
const getObjFromBase64 = (base64Data, fileName = undefined, type=null) => {
  const base64Str = base64Data.replace(/^data:.+;base64,/, "");
  const contentType = type || base64Data.match(/[^:]\w+\/[\w-+\d.]+(?=;|,)/)[0];
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
        await sendActionMobile(getObjFromBase64(base64Data, fileName));
      } else {
        // ------------IS DESKTOP---------------
        try {
          const newUrl = URL.createObjectURL(blob);
          openFileDesktop(newUrl);
          setTimeout(()=>{ URL.revokeObjectURL(url);},50);
        } catch (e) {
          reject({message:e.message, type:CONSTANT.ERROR});
        }
      }
      resolve(true);
    }
  });
});

export const openFileBase64 = async (base64Str, contentType) => new Promise(async (resolve, reject) => {
  if (webkitRequestMobile()){
      //------------ IS MOBILE APP---------
    const base64Data = `data:${contentType},${base64Str}`;
    await sendActionMobile(getObjFromBase64(base64Data));
  } else {
    // ------------IS DESKTOP---------------
    try {
      let byteCharacters = atob(base64Str);
      let byteNumbers = new Array(byteCharacters.length);
  
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
  
      const blob = new Blob([new Uint8Array(byteNumbers)], { type: `${contentType}` });
      const newUrl = URL.createObjectURL(blob);
      openFileDesktop(newUrl);
    } catch (e) {
      reject({message:e.message, type:CONSTANT.ERROR});
    }
  }
  resolve(true);
});

export const openFileDesktop = (url) => {
  try {
      const openWindow =  window.open(url, '_blank');
      if(openWindow) return openWindow;
  } catch (error) {
    console.log(error);
  }
  throw new Error(MSG.BLOCKED_POPUP);
}

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

export const sendActionMobile = (data) => {
  return new Promise((resolve) => {
    let result = false;
    try {
      if (webkitRequestMobile()) {
        let _webkit = webkit.messageHandlers.cordova_iab;
        if (data) _webkit.postMessage(JSON.stringify(data));
        result = true;
      }
    } catch (e) {}
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

export const openFileMobile = async (url, contentType=null) => new Promise((resolve, reject) => {;
    requestFileUrl("GET", url, (result, error) => {
      if (error) reject(error);
      else {
        const reader = new FileReader();
        reader.readAsDataURL(result);
        reader.onload = function () {
          resolve(getObjFromBase64(reader.result.toString(), undefined, contentType));
        };
        reader.onerror = function () {
          reject(true);
        };
      }
    });
});