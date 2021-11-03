import { get, openFile } from "./request.js";
import { API_URL } from "../environments/environments.js";
import { AonDateUtils } from "../modules/utils/AonDateUtils.js";

export const getEmployeeSalaries = (data) =>
  get(`${API_URL}/contract/employee/salaries`, data);
export const getEnterpriseSalaries = (data) =>
  get(`${API_URL}/contract/enterprise/salaries`, data);
export const getAllEmployeesWorkplace = (data) =>
  get(`${API_URL}/contract/employee/workplace`, data);
export const getSalaryPdf = (data) =>
  openFile(`${API_URL}/contract/salary/pdf`, data);

export const getPeriodLaboral = (data) => {
  const now = new Date();
  let jsonValues = [
    {
      name: "Mes actual",
      value: "this_month",
      startDate: AonDateUtils.formatDateOrigin(
        new Date(now.getFullYear(), now.getMonth(), 1)
      ),
      endDate: AonDateUtils.formatDateOrigin(
        new Date(now.getFullYear(), now.getMonth() + 1, 0)
      ),
    },
    {
      name: "Mes anterior",
      value: "last_month",
      startDate: AonDateUtils.formatDateOrigin(
        new Date(now.getFullYear(), now.getMonth() - 1, 1)
      ),
      endDate: AonDateUtils.formatDateOrigin(
        new Date(now.getFullYear(), now.getMonth() - 1 + 1, 0)
      ),
    },
  ];
  if (data) {
    jsonValues = jsonValues.find((f) => f.value.indexOf(data) >= 0);
  } else {
    const firstDayOfYear  = new Date(now.getFullYear(),0,1);
    let endDate = lastDayOfMonth(firstDayOfYear, +2);
    if(now>=endDate){
      jsonValues.push({
        name: "Primer trimestre",
        value: "first_quarterly",
        startDate: AonDateUtils.formatDateOrigin(firstDayOfYear),
        endDate:AonDateUtils.formatDateOrigin(endDate),
      });
      endDate = lastDayOfMonth(firstDayOfYear, +5);
      if(now>=endDate){
        jsonValues.push({
          name: "Segundo trimestre",
          value: "second_quarterly",
          startDate: AonDateUtils.formatDateOrigin(new Date(firstDayOfYear).addMonth(+3)),
          endDate:AonDateUtils.formatDateOrigin(endDate),
        });
        endDate = lastDayOfMonth(firstDayOfYear, +8);
        if(now>=endDate){
          jsonValues.push({
            name: "Tercer trimestre",
            value: "third_quarterly",
            startDate: AonDateUtils.formatDateOrigin(new Date(firstDayOfYear).addMonth(+6)),
            endDate:AonDateUtils.formatDateOrigin(endDate),
          });
          endDate = lastDayOfMonth(firstDayOfYear, +11);
          if(now>=endDate){
            jsonValues.push({
              name: "Cuarto trimestre",
              value: "fourth_quarterly",
              startDate: AonDateUtils.formatDateOrigin(new Date(firstDayOfYear).addMonth(+9)),
              endDate:AonDateUtils.formatDateOrigin(endDate),
            });
          }
        }
      }
    }

    jsonValues.push({
      name: "Año actual",
      value: "this_year",
      startDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear(), 0, 1)),
      endDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear(), 12, 0)),
    },
    {
      name: "Año anterior",
      value: "last_year",
      startDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear() - 1, 0, 1)),
      endDate: AonDateUtils.formatDateOrigin(new Date(now.getFullYear() - 1, 12, 0)),
    },
    {
      name: "Personalizado",
      value: "personalized",
    });
  }
  return jsonValues;
};

const lastDayOfMonth = (d, monthNumber) =>{
  let newDate = new Date(d).addMonth(monthNumber);
  return new Date(newDate.getFullYear(), newDate.getMonth() + 1, 0);
}
