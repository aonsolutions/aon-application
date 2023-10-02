export class KeyGenerator {
    static characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    constructor(){}
    static generate(length: number): string {
        let result = ''
        for (let i = 0; i < length; i++) {
            const randomIndex = Math.floor(Math.random() * this.characters.length);
            result += this.characters.charAt(randomIndex);
        }
        return result;
    }
}