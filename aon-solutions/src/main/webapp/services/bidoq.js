import {requestBidoq} from   './request.js';


export const BIDOQ_METHOD = 'get_user_login';
export const BIDOQ_URL = 'https://dev.mispapeles.es/api/aon/v1/index.php';
export const BIDOQ_API_KEY = 'K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I';

export const bidoq = () => {
  let token = localStorage.getItem('aon_session_id');
  const sendData = `api_key=${BIDOQ_API_KEY}&method=${BIDOQ_METHOD}&session_id=${token}`;
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
