import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { addDays } from "./utils.js";


export const sendNotification = (data) => post(`${API_URL}/notification/send`, data);

//remove test sendNotificationTest
export const sendNotificationTest = () =>{
    const tokenFCM = window.tokenFCM;
    if(tokenFCM){
        return get(`${API_URL}/notification/test`, {tokenFCM});
    }
    console.log("tokenFCM", tokenFCM);
}


export const getPending = ()  => new Promise(resolve=>{
    resolve({
        notification: 4,
        messenger:3
    });
})

export const getNotification = () => new Promise(resolve=>{
    resolve([
        {
            id:1,
            title:"Where does it come from",
            body:"Lorem Ipsum Lorem Ipsum  Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum",
            read: 0,
            date: new Date()
        },
        {
            id:2,
            title:"Why do we use it",
            body:"Lorem Ipsum Lorem Ipsum  Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum",
            read: 1,
            date: addDays(new Date(), -1)
        },
        {
            id:3,
            title:"Where can I get some",
            body:"Lorem Ipsum Lorem Ipsum  Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum",
            read: 0,
            date: addDays(new Date(), -2)
        },
        {
            id:4,
            title:"Where can I get some",
            body:"Lorem Ipsum Lorem Ipsum  Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum",
            read: 0,
            date: addDays(new Date(), -3)
        },
        {
            id:5,
            title:"Where can I get some",
            body:"Lorem Ipsum Lorem Ipsum  Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum",
            read: 0,
            date: addDays(new Date(), -4)
        },
    ]);
});