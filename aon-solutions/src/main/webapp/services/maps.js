import {actionMobile} from './actionMobile.js';
const options = {};

let position;

const getCurrent = () => new Promise((resolve, reject) => {
    if (navigator.geolocation)
        navigator.geolocation.getCurrentPosition(resolve, reject, options);
    else
        reject("browser no sopported");
});

const successCallback = ({ coords, timestamp }) => {
    return { latitude: coords.latitude, longitude: coords.longitude, timestamp };
}

const errorCallback = (error) => {
    let msg = null;
    switch (error.code) {
        case error.PERMISSION_DENIED:
            msg = "El usuario denegó la solicitud de geolocalización.";
            break;
        case error.POSITION_UNAVAILABLE:
            msg = "La información de ubicación no está disponible.";
            break;
        case error.TIMEOUT:
            msg = "Se agotó el tiempo de espera de la solicitud para obtener la ubicación del usuario.";
            break;
        case error.UNKNOWN_ERROR:
            msg = "Un error desconocido ocurrió.";
            break;
    }
    return msg;
}

// kilometers default       
// 'M' is meters   
const distance = (lat1, lon1, lat2, lon2, unit) => {
    let R = 6371; // km
    let dLat = degrees_to_radians(lat2 - lat1);
    let dLon = degrees_to_radians(lon2 - lon1);
    lat1 = degrees_to_radians(lat1);
    lat2 = degrees_to_radians(lat2);
    let a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    let d = R * c;

    if ("M" === unit) { d = d * 1000 }

    return d;
}

const degrees_to_radians = (degrees) => degrees * (Math.PI / 180);

export const getPosition = async () => {
    let result = null;
    let isApp = await actionMobile({ action: "setPosition" });
     
    if(isApp){
        result = await sleepPosition();
    } else {
        try {
            result = await getCurrent().then(successCallback)
        } catch (e) {
            console.warn(errorCallback(e));
        }
    }
    return result;
};

export const setPosition = async (pos) => {
    position = pos; 
};

const sleepPosition = () => {
    return new Promise((resolve,reject)=>{
        let idInterval = setInterval(()=>{
            if(position){
                resolve(position);
                clearInterval(idInterval);
            }
        }, 300);
    })
}