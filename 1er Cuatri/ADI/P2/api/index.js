var express = require("express")
var cors = require('cors')
var app = express()

var lista = new Map()
var idActual = 3

app.use(express.json())
app.use(cors())

//listar todos los items
app.get("/items", function(pet, resp){
    resp.status(200)
    var datos = Array.from(lista.values())
    resp.send(datos)
})

//obtener un item dado su id
//asumimos que el id está en un parámetro HTTP del mismo nombre
//es decir, habrá que hacer una petición a /obtener?id=XXX
app.get("/items/:id", function(pet, resp){
    //los parámetros HTTP se obtienen como cadenas, convertirlo a entero
    var id = parseInt(pet.params.id)
    //si no es un número, error
    if (isNaN(id)) {
        resp.status(400)
        resp.send({mensaje:"El dato debe ser numérico"})
    }
    else {
        //obtenemos el item del Map
        var dato = lista.get(id)
        //si != undefined es que lo hemos encontrado, lo enviamos al cliente
        if (dato) {
            resp.status(200)
            resp.send(dato)
        }
        else {
            resp.status(404)
            resp.end()
        }
    }
})

app.patch("/items/:id", function(pet, resp) {
    var id = parseInt(pet.params.id)
    if (!isNaN(id)) {
        var item = lista.get(id)
        if (item) {
            var nombre = pet.body.nombre
            var comprado = pet.body.comprado
            if (nombre) {
                item.nombre = nombre
            }
            if (comprado!=undefined) {
                item.comprado = comprado
            }
            resp.status(204)
            resp.end()
            console.log(item)
        }
        else {
            resp.status(404)
            resp.send({mensaje:"El item no existe"})
        }
    }
    else {
        resp.status(400)
        resp.send({mensaje:"el id debe ser numérico"})
        
    }
    
})

app.post("/items", function(pet, resp){
    var nombre = pet.body.nombre
    if (nombre) {
        var obj = {id:idActual, nombre:nombre, comprado:false}
        lista.set(idActual, obj)
        resp.status(201)
        resp.setHeader('Location','http://localhost:3000/items/'+idActual);
        idActual++
        console.log(obj)
        resp.send(obj)
    }
    else {
        resp.status(400)
        resp.send({mensaje:"falta el campo nombre"})
    }
}) 

app.delete('/items/:id', function(pet, resp){
    var id = parseInt(pet.params.id)
    //si no es un número, error
    if (isNaN(id)) {
        resp.status(400)
        resp.send({mensaje:"El dato debe ser numérico"})
    }
    else {
        //borramos el item del Map
        var dato = lista.delete(id)
        //si != undefined es que estaba, y lo hemos borrado
        if (dato) {
            console.log("borrado: "+id)
            resp.status(200)
            resp.end()
        }
        else {
            resp.status(404)
            resp.end()
        }
    }
})

//poner en marcha el servidor
app.listen(3000, function(){
    lista.set(1, {id:1, nombre:"pan", comprado:true})
    lista.set(2, {id:2, nombre:"patatas", comprado:false})
    console.log("Servidor escuchando en el 3000...")
})