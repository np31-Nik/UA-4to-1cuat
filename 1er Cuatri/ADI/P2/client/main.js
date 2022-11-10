import {ClienteAPI} from './ClienteAPI'
import Handlebars from "handlebars"

var app_plantilla = `
<h1>Lista de la compra</h1>
<input type="text" id="nuevoItem"><button id="botonInsertar">Insertar</button>
<ul id="lista">
   {{#each .}} 
    <li id="{{id}}" {{#comprado}}class="tachado"{{/comprado}}>
    {{nombre}}
    <button id="{{id}}_button">Eliminar</button>
    </li>
   {{/each}} 
</ul>
`

document.addEventListener('DOMContentLoaded', async function() {
    var cliente = new ClienteAPI()
    var items = await cliente.getItems()
    func_plantilla = Handlebars.compile(app_plantilla)
    document.getElementById('app').innerHTML = func_plantilla(items)
    
    
    document.getElementById('lista').addEventListener('click', function(evento){
        var id_target = evento.target.id
        if (evento.target.nodeName=="LI") {
            if (evento.target.className=="tachado") {
                //marcarlo como no comprado
                cliente.setItemState(id_target, false)
                evento.target.className=""
            }
            else {
                //marcarlo como comprado
                cliente.setItemState(id_target, true)
                evento.target.className="tachado"
            }
        }
        if (evento.target.nodeName=="BUTTON") {
            console.log("borrar!")

            var id_borrar = parseInt(evento.target.id)
            cliente.deleteItem(id_borrar)
            evento.target.parentElement.remove()
        }


    })

    document.getElementById('botonInsertar').addEventListener('click', async function(evento){
        var nombre = document.getElementById('nuevoItem').value
        var lista = document.getElementById('lista')

        if(nombre!=null){
            console.log("Insertar!")
            var item = await cliente.addItem(nombre)
            

            var nuevo = document.createElement("li")
            nuevo.id=item.id
            nuevo.class=""

            var texto = document.createTextNode(nombre)
            nuevo.appendChild(texto)

            var button = document.createElement("button")
            button.id = item.id+"_button"
            button.textContent="Eliminar"
            nuevo.appendChild(button)

            lista.appendChild(nuevo)
        }
    })

    
})

