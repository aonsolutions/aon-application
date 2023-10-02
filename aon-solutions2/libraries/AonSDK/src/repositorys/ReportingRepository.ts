import { IReportingRepository } from "../interfaces/repositoryInterfaces";
import { ApiHttpRequest } from "../utils/Http";
import { BASE_URL, GET_METHOD } from "../utils/Environment";

export class ReportingRepository implements IReportingRepository {

    async cobrosPagos(): Promise<any> {
        let datasets: any[] = [], cobros: any[] = [], pagos: any[] = [], label: any[] = [];
        for(let i = 0; i < 12; i++){
            cobros.push(Math.floor(Math.random()*2000))
            pagos.push(Math.floor(Math.random()*2000))
        }
        datasets.push({data:cobros, label:'cobros'})
        datasets.push({data:pagos, label:'pagos'})
        return { datasets: datasets, label: [1,2,3,4,5,6,7,8,9,10,11,12] };
    }

    async ventasGastos(): Promise<any> {
        let datasets: any[] = [], ventas: any[] = [], gastos: any[] = [], label: any[] = [];
        for(let i = 0; i < 12; i++){
            ventas.push(Math.floor(Math.random()*2000))
            gastos.push(Math.floor(Math.random()*2000))
        }
        datasets.push({data:ventas, label:'ventas'})
        datasets.push({data:gastos, label:'gastos'})
        return { datasets: datasets, label: [1,2,3,4,5,6,7,8,9,10,11,12] };
    }

}

export class ApiReportingRepository implements IReportingRepository {

    private httpRequest = new ApiHttpRequest();

    async cobrosPagos(): Promise<any> {
        throw new Error("Method not implemented.");
    }

    async ventasGastos(): Promise<any> {
        let json = await ApiHttpRequest.get(BASE_URL + '/ms/api/stat/invoice', {}, {})
        /** PARSE JSON TO CHARTS.JS LIBRARY => MAYBE THIS WILL BE DO IT IN THE FUTURE IN ANGULAR PROJECT SERVICE */
        let keys = Object.keys(json);
        let datasets: any[] = [], ventas: any[] = [], gastos: any[] = [];
        for(let i = 0; i < keys.length; i++){
            ventas.push(json[keys[i]].Ventas)
            gastos.push(json[keys[i]].Gastos)
        }
        datasets.push({data:ventas, label:'ventas'})
        datasets.push({data:gastos, label:'gastos'})
        return { datasets: datasets, label: keys };
    }
}