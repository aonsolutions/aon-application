import { BankFactory } from "../factorys/BankFactory";
import { AuthenticationFactory } from "../factorys/AuthenticationFactory";
import { EnterpriseFactory } from "../factorys/EnterpriseFactory";
import { TaxModelFactory } from "../factorys/TaxModelFactory";
import { MessageFactory } from "../factorys/MessageFactory";

export async function makeTest(){
    await testLogin();
    await testEnterprise();
    await testBank();
    await testMessage();
    await testTaxModel();
    await logout();
}

async function testLogin(){
    let auth = new AuthenticationFactory();
    let checkLogin = await auth.createAuthenticationManager().login('info@aonsolutions.es','test');
    checkLogin ? console.log('TEST LOGIN OK') : console.log('TEST LOGIN ERROR')
}

async function logout(){
    let auth = new AuthenticationFactory();
    auth.createAuthenticationManager().logout();
}

async function testEnterprise(){
    let factory = new EnterpriseFactory();
    let enterpriseSelected = await factory.createMultipleObjectCrud().getCollection()
    enterpriseSelected ? console.log('TEST ENTEPRISE OK', enterpriseSelected) : console.log('TEST ENTEPRISE ERROR')
    if(enterpriseSelected){
        let auth = new AuthenticationFactory();
        auth.createAuthenticationManager().setEnterprise(enterpriseSelected.result.toArray()[0]);
    }
}

async function testTaxModel(){
    let factory = new TaxModelFactory();
    let taxModelSelected = await factory.createMultipleObjectCrud().getCollection()
    taxModelSelected ? console.log('TEST TAX MODEL OK', taxModelSelected) : console.log('TEST TAX MODEL ERROR')
}

async function testBank(){
    let factory = new BankFactory();
    let bankSelected = await factory.createMultipleObjectCrud().getCollection()
    bankSelected ? console.log('TEST BANK OK', bankSelected) : console.log('TEST BANK ERROR')
}

async function testMessage(){
    let factory = new MessageFactory();
    let messageSelected = await factory.createMultipleObjectCrud().getCollection()
    messageSelected ? console.log('TEST MESSAGE OK', messageSelected) : console.log('TEST MESSAGE ERROR')
}