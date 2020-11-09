
    import {requestBidoq} from  './request.js';

    export const BIDOQ_METHOD   = 'get_user_login';
    export const BIDOQ_URL      = 'https://dev.mispapeles.es/api/aon/v1/index.php';
    export const BIDOQ_API_KEY  = 'K7>})(xQw~px_wgs#0=97..QGkBSxw*=.uatCfw[D.T{,fy.nrt?ok8jB@9}2}I';
    export const AON_SESSION_ID = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJzY2hlbWFcIjpcImF5dWRhdC1hb25zb2x1dGlvbnMtbmV0XCIsXCJzY2hlbWFfZmlyc3RfZG9tYWluXCI6XCIwMDIyNDIwMzllLWF5dWRhdC5hb25zb2x1dGlvbnMubmV0XCIsXCJ1dWlkXCI6XCJFNkFGMjg1NEI2NjYxMUVBODMyMzA2QTBCREQ3MkE0NlwifSIsImlzcyI6ImF1dGgwIiwiaWF0IjoxNjAwNzkzNDgyfQ.4O-z1Hldqz1WAmX7kcsBkRlb0zy64ucYXQIoLnDL7mA';
   
    export const loginbidoq = () => {
        // Datos que le pasamos para el login
        const data = {
            api_key     : BIDOQ_API_KEY,
            method      : BIDOQ_METHOD,
            session_id  : AON_SESSION_ID
        };
        
        // Codificamos el objeto a una query string de URL
        const sendData = new URLSearchParams(data).toString();

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
