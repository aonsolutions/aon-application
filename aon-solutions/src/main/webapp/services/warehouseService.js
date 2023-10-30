import { get, post, remove } from "./request.js";
import { API } from "../environments/environments.js";


export const getDelivery = (data) => get(API.DELIVERIES + '/' + data.id, data);
export const getDeliveries = (data) => get(API.DELIVERIES, data);

export const getWarehouses = (data) => get(API.WAREHOUSES, data);

export const getElaborations = (data) => post(API.ELABORATION, data);
export const getElaboration = (id) => post(API.ELABORATION, {id});
export const saveElaboration = (data) => put(API.ELABORATION, data);
export const deleteElaboration = (id)  => remove(API.ELABORATION, {id});

export const getElaborationPackages = () => {
    return new Promise((resolve, reject) => {
        resolve([{
            reference: 'CAJA 1',
            description: '10 bolsas'
        }, {
            reference: 'PALET 1',
            description: '100 cajas'
        }]);
    });
}