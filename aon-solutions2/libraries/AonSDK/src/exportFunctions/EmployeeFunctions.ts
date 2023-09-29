import { EmployeeFactory } from "../factorys/EmployeeFactory";
import { IEmployee } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class EmployeeFunctions {
    private singleObjectCrud = new EmployeeFactory().createSingleObjectCrud();
    private multipleObjectCrud = new EmployeeFactory().createMultipleObjectCrud();

    async getEmployeeList(filter?: IFilter): Promise<IResponse<ICollection<IEmployee>>> {
        return (await this.multipleObjectCrud.getCollection(filter));
    }

    async updateEmployee(employee: IEmployee): Promise<IResponse<IEmployee>> {
        return (await this.singleObjectCrud.updateElement(employee));
    }

    async createEmployee(employee: IEmployee): Promise<IResponse<IEmployee>> {
        return (await this.singleObjectCrud.createElement(employee));
    }

    async deleteEmployee(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }

    async toggleEmployeeStatus(employeeId: string):Promise<IResponse<IEmployee>> {
        const employeeToUpdate: IEmployee = (await this.singleObjectCrud.getElement(employeeId)).result;
        if (employeeToUpdate) {
            employeeToUpdate.Active = !employeeToUpdate.Active;
            return this.updateEmployee(employeeToUpdate);
        }else{
            throw new Error("Error al tratar de acceder al elemento");
        }
    }
}