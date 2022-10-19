// Serhii Vidernikov Y4459773H
// Nikita Polyanskiy Y4441167L

var express = require('express')
var jwt = require('jwt-simple')
var moment = require('moment')

var app = express()
app.use(express.json())

var users = new Map()
users.set("pepe", {login:"pepe", password:"pepe"})
users.set("adi", {login:"adi", password:"adi"})


var secret = '123456123456123456123456123456123456'


//En una app con autentificación basada en Token, el login genera y devuelve el token
app.post('/miapi/login', function(pet, resp){
    var loginBuscado = pet.body.login
    var passwordBuscado = pet.body.password
    var user = users.get(loginBuscado)
    if (user && user.password==passwordBuscado) {
        var payload = {
            login: loginBuscado,
            exp: moment().add(1, 'days').valueOf()
        }        
        
        var token = jwt.encode(payload,secret)
        resp.send({
                  mensaje:"OK",
                  jwt: token
                  })
    }
    else {
        resp.send(403).end()
    }
})

//Middleware: lo pondremos ANTES de procesar cualquier petición que requiera autentificación
function chequeaJWT(pet, resp, next) {

    var correcto=false
    var token = getTokenFromAuthHeader(pet)
    if(token!=undefined){
      try{
        var decoded = jwt.decode(token,secret)
        correcto=true
      }catch(error){
        console.log(error)
      }
    }
    if (correcto) {
        //Al llamar a next, el middleware "deja pasar" a la petición
        //llamando al siguiente middleware
        next()
    }
    else {
        resp.status(403)
        resp.send({mensaje: "no tienes permisos"})
    }
}

//Si en la petición HTTP "pet" existe una cabecera "Authorization"
//con el formato "Authorization: Bearer XXXXXX"  
//devuelve el XXXXXX (en JWT esto sería el token)
function getTokenFromAuthHeader(pet) {
    var cabecera = pet.header('Authorization')
    if (cabecera) {
        //Parte el string por el espacio. Si está, devolverá un array de 2
        //la 2ª pos será lo que hay detrás de "Bearer"
        var campos = cabecera.split(' ')
        if (campos.length>1 && cabecera.startsWith('Bearer')) {
            return campos[1]
        }
    }
    return undefined
}

app.get('/', function(pet, resp) {
  resp.send('Esta es la raíz de la app')  
})

app.get('/miapi/saludo', function(pet, resp) {
  resp.send("hola soy el API, este es un recurso que no requiere autentificación")
})

app.get('/miapi/protegido1', chequeaJWT, function(pet, resp){
    var token = getTokenFromAuthHeader(pet)
    var payload=jwt.decode(token,secret)
    var login=payload.login
    
    var respuesta = "hola " + login

    resp.send({dato: "recurso protegido 1",
      respuesta: respuesta})
})

app.get('/miapi/protegido2', chequeaJWT, function(pet, resp){
    resp.send({dato: "recurso protegido 2"})
})

var listener = app.listen(process.env.PORT||3000, () => {
    console.log(`Servidor en el puerto ${listener.address().port}`);
});



