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

    async cobrosPagos(from: Date, to: Date): Promise<any> {
        let json = await ApiHttpRequest.get(BASE_URL + '/ms/api/stat/finance?from='+from.toISOString()+'&to='+to.toISOString(), {}, {})
        /** PARSE JSON TO CHARTS.JS LIBRARY => MAYBE THIS WILL BE DO IT IN THE FUTURE IN ANGULAR PROJECT SERVICE */
        let keys = json.data;
        let datasets: any[] = [], ventas: any[] = [], gastos: any[] = [], fechas: any[] = [];
        for(let i = 0; i < keys.length; i++){
            fechas.push(keys[i].date)
            ventas.push(keys[i].pago ? keys[i].pago : 0)
            gastos.push(keys[i].cobro ? keys[i].cobro : 0)
        }
        datasets.push({data:ventas, label:'cobros'})
        datasets.push({data:gastos, label:'pagos'})
        return { datasets: datasets, label: fechas };
    }

    async ventasGastos(from: Date): Promise<any> {
        let json = await ApiHttpRequest.get(BASE_URL + '/ms/api/stat/invoice?from='+from.toISOString(), {}, {})
        /** PARSE JSON TO CHARTS.JS LIBRARY => MAYBE THIS WILL BE DO IT IN THE FUTURE IN ANGULAR PROJECT SERVICE */
        let keys = json.data;
        let datasets: any[] = [], ventas: any[] = [], gastos: any[] = [], fechas: any[] = [];
        for(let i = 0; i < keys.length; i++){
            fechas.push(keys[i].date)
            ventas.push(keys[i].Ventas ? keys[i].Ventas : 0)
            gastos.push(keys[i].Gastos ? keys[i].Gastos : 0)
        }
        datasets.push({data:ventas, label:'ventas'})
        datasets.push({data:gastos, label:'gastos'})
        return { datasets: datasets, label: fechas };
    }
}