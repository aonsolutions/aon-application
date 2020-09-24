import {requestBidoq} from   './request.js';


export const BIDOQ_METHOD = 'get_user_login';
export const BIDOQ_URL = 'https://dev.mispapeles.es/api/aon/v1/index.php';
export const BIDOQ_API_KEY = 'K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I';
export const AON_SESSION_ID = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJzY2hlbWFcIjpcImF5dWRhdC1hb25zb2x1dGlvbnMtbmV0XCIsXCJzY2hlbWFfZmlyc3RfZG9tYWluXCI6XCIwMDIyNDIwMzllLWF5dWRhdC5hb25zb2x1dGlvbnMubmV0XCIsXCJ1dWlkXCI6XCJFNkFGMjg1NEI2NjYxMUVBODMyMzA2QTBCREQ3MkE0NlwifSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjAwNzkzNDgyfQ.4O-z1Hldqz1WAmX7kcsBkRlb0zy64ucYXQIoLnDL7mA';
export const CIF = 'X6443384A';
export const PASSWORD = 'bdba327d465700828ecd36680ffe822a';
export const TOKEN_USER = '6a2baa322e7ed8fad8af9181954cdcb2a36da8a02466a07d46a78ca3e8c50f9a77020e3d2baa7e9d101d6410529791a78d53';

export const bidoq = () => {
  const sendData = `api_key=${BIDOQ_API_KEY}&method=${BIDOQ_METHOD}&session_id=${AON_SESSION_ID}`;
  const sendData2 = `api_key=${BIDOQ_API_KEY}&method=${BIDOQ_METHOD}&session_id=${AON_SESSION_ID}&cif=${CIF}&password=${PASSWORD}&method=get_user_token&token_user=${TOKEN_USER}`;
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
