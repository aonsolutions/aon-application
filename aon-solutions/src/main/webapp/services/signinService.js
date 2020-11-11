// import { request, post, get } from "./request.js";
// import { API_URL } from "../environments/environments.js";


export const getSigninStatus = () => {
  let signin = {
    status: localStorage.getItem('aon-signin-status') || 'out',
    time: {
      hours: localStorage.getItem('aon-signin-time-hours') || '00',
      minutes: localStorage.getItem('aon-signin-time-minutes') || '00',
      seconds: localStorage.getItem('aon-singin-time-seconds') || '00'
    },
    started: Date.parse(localStorage.getItem('aon-signing-started'))
  };
  return new Promise((resolve, reject) => {
    resolve(signin);
  });
}

export const updateSigninStatus = (data) => {
  let date = new Date();
  localStorage.setItem('aon-signin-status', data.status);
  if(data.status !== 'in'){
    // let started = Date.parse(localStorage.getItem('aon-signin-started'));
    // let tDate = Date.parse(date - started);
    // localStorage.setItem('aon-signin-time-hours', tDate.getHours());
    // localStorage.setItem('aon-signin-time-minutes', tDate.getMinutes());
    // localStorage.setItem('aon-signin-time-seconds', tDate.getSeconds());
    localStorage.removeItem('aon-signin-started');
  } else {
    localStorage.setItem('aon-signin-started', date);
  }
}
