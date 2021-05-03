import { post, get } from "./request.js";
import { API_URL } from "../environments/environments.js";

export const getMessenger = () => new Promise(resolve=>{
    resolve([
        {
            id:1,
            title:"Where does it come from",
            read: 1,
            date: new Date()
        },
        {
            id:2,
            title:"Why do we use it",
            read: 1,
            date: new Date().addDay(-1),
        },
        {
            id:3,
            title:"Where can I get some",
            read: 1,
            date: new Date().addDay(-2),
        }
    ]);
});

/**
 * Gegt messenger chat data
 * @returns 
 */
export const getMessengerChat = () => new Promise(resolve => {
    resolve({
        //No data available
        status : "Not implemented" 
    });
});