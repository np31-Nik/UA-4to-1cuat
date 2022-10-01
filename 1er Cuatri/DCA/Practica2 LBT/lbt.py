# Nikita Polyanskiy Y4441167L

from datetime import date
import itertools
from queue import Empty

### BD Usuarios:
class User:
    def __init__(self,name,password,role="user"):
        self.name=name
        self.password=password
        self.role=role

userlist = []
userlist.append(User("admin","admin","admin"))
userlist.append(User("u1","u1"))
userlist.append(User("u2","u2"))
userlist.append(User("u3","u3"))

### BD Issues:
class Issue:
    id_iter = itertools.count()

    def __init__(self,title,user,description,comments=[],status="open",date=date.today()):
        self.id=next(self.id_iter)
        self.title=title
        self.user=user
        self.description=description
        self.comments=comments
        self.date=date
        self.status=status


issuelist = []

issuelist.append(Issue("Problema para iniciar sesion","u2","Cuando pongo mi usuario y contraseña, se cierra la aplicacion.",date=date(2022,9,28),status="closed"))
issuelist.append(Issue("No se puede registrar","u3","Cuando pongo mi usuario y contraseña, me devuelve a la pagina principal.",date=date(2022,9,29)))
issuelist.append(Issue("Me sale error al crear nuevo issue","u3","Le doy a crear nuevo issue, y me aparece error, que hago?",date=date(2022,9,30)))

### BD Comentarios:
class Comment:
    def __init__(self,user,text):
        self.user=user
        self.text=text

commentlist1 = []
commentlist1.append(Comment("u1","a mi me ocurre lo mismo"))
commentlist1.append(Comment("u3","a ver si arreglan esto de una vez"))
commentlist1.append(Comment("admin","WONTFIX"))

issuelist[0].comments=commentlist1

commentlist2 = []
commentlist2.append(Comment("u2", "y como te has registrado entonces?"))
issuelist[1].comments=commentlist2

### Datos globales del usuario actual
current_username=""

### Funcion para listar hilos y comentar
def ListIssues(il):
    for i in il:
        print('Nº',i.id,' [',i.date,']',i.title,'[',i.status,']')
    selected_issue = input("Selecciona un issue por su Nº: ")

    issue_encontrado=False

    for i in il:
        if i.id == int(selected_issue):
            issue_encontrado=True
            print('\nIssue Nº',i.id,' [',i.date,'] User:',i.user)
            print('Title: ',i.title)
            print('Description: ',i.description)
            print('Status: ',i.status)
            print('Comentarios:')
            print('----------------------')

            if not i.comments:
                print('No hay comentarios para este problema.')
            else:
                for c in i.comments:
                    print('User: ',c.user)
                    print('"',c.text,'"')
                    print('----------------------')

            if i.status != 'closed':
                write = input('Quieres escribir un comentario? Y/N: ')

                if write=='Y' or write=='y':
                    comment = input('Escribe tu comentario: ')
                    i.comments.append(Comment(current_username,comment))
                    print('Comentario guardado con exito!')
            elif i.status=='closed':
                print('Este problema está resuelto y no se puede dejar comentarios.')
            break
    if not issue_encontrado:
        print(i.id,' No existe un issue con ese Nº: ',selected_issue)


### Funcion Menu de inicio de sesion
def LoginMenu():
    global current_username

    while current_username=="":
        print('1. Iniciar sesión')
        print('2. Registrarse')
        print('q. Salir')
        login_option = input('Selecciona una opcion: ')

        if login_option == '1':
            user = input("Nombre de usuario: ")
            password = input("Contraseña: ")

            for usr in userlist:
                    if usr.name==user:
                        if usr.password==password:
                            print('Iniciando sesión...')
                            current_username=user
                
            if current_username == "":
                print('Usuario o contraseña incorrectos.')

        elif login_option=='2':
            user = input("Nombre de usuario: ")
            password = input("Contraseña: ")

            userlist.append(User(user,password))
            print('Usuario creado.\n')

        elif login_option=='q':
            exit()

### Funcion Menu principal
def MainMenu():
    menu_option=""
    print('\nMenu principal')
    print("1. Ver issues")
    print("2. Crear nuevo issue")
    print("3. Buscar en issues")
    if current_username=='admin':
        print('4. Cerrar o reabrir issues')
    print("q. Salir")
    menu_option = input("Seleccione una opcion: ")
    return menu_option

### Main
def main():
    global current_username

    LoginMenu()

    menu_option=""
    while menu_option!='q':
        menu_option = MainMenu()

#### opcion Listado de issues y comentar
        if menu_option=='1':
            print('\nLista de issues:')
            ListIssues(issuelist)

#### opcion Crear issues
        elif menu_option=='2':
            title = input('\nTitulo: ')
            description = input('Descripcion: ')
            issuelist.append(Issue(title,current_username,description,comments=[]))
            print('Issue creado con exito!')

#### opcion Buscar por texto
        elif menu_option=='3':
            search = input('\n¿Que buscas?: ')
            issues = []
            for i in issuelist:
                if search in i.title or search in i.description:
                    issues.append(i)
                else:
                    for c in i.comments:
                        if search in c.text:
                            issues.append(i)

            if not issues:
                print('No se ha encontrado nada.')
            else:
                print('Resultados de búsqueda, estos hilos podrian ser de tu interés:')
                ListIssues(issues)

#### opcion Cerrar/Reabrir issues (solo admin)
        elif current_username=='admin' and menu_option=='4':
            print('\nElige el issue a cerrar/abrir:')
            for i in issuelist:
                print('Nº',i.id,' [',i.date,']',i.title,'[',i.status,']')
            selected_issue = input("Selecciona un issue por su Nº: ")
            issue_encontrado=False

            for i in issuelist:
                if i.id == int(selected_issue):
                    issue_encontrado=True
                    status_cambiado=False
                    while not status_cambiado:
                        status = input('¿Abrir o cerrar el issue? A/C:')
                        if status=='a' or status=='A':
                            i.status='open'
                            status_cambiado=True
                        elif status=='c' or status=='C':
                            i.status='closed'
                            status_cambiado=True
                        else:
                            print('Error, no se ha elegido una opcion correcta (A/C)')
                    print('Estado del issue cambiado con exito!')

#### opcion Salir
        elif menu_option=='q':
            exit()

if __name__ == "__main__":
    main()