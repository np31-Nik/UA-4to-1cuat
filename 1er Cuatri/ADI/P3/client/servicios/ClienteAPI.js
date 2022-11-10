export class ClienteAPI {
    constructor(url) {
        this.API_URL = url
    }

    async getItems() {
        var resp = await fetch(this.API_URL)
        if (resp.ok) {
            return resp.json()
        }
        else {
            throw new Error(resp.status)
        }
    }

    async toggleItem(id, valor) {
        var resp = await fetch(this.API_URL+'/'+id,
                            {
                                method: 'PATCH',
                                headers: {
                                    'Content-Type':'application/json'
                                },
                                body: JSON.stringify({comprado:valor})
                            })
        if (resp.ok) {
            return true
        } 
        else {
            throw new Error(resp.status)
        }                   
    }

    async addItem(nom) {
        var datos = JSON.stringify({nombre:nom})
        var resp = await fetch(this.API_URL,{
            method: 'POST',
            headers: {
                'Content-Type':'application/json'
            },
            body: datos
        })
        if (resp.ok) {
            return resp.json()
        } 
        else {
            throw new Error(resp.status)
        } 
    }

    async delItem(id) {
        var resp = await fetch(this.API_URL+'/'+id,{
            method:'DELETE'
        })
        if (resp.ok) {
            return true
        }
        else {
            return false
        }
    }
}