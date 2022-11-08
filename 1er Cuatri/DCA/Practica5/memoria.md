#### *Nikita Polyanskiy Y4441167L*

# Compilación acelerada de Vim
Codigo usado: https://github.com/vim/vim

## **Tiempos de ejecución**

### **Sin ccache**

Comando ejemplo: time make -j1

- j1:
1m37,673s

- j2:
1m6,717s

- j3:
1m2,356s

- j4:
0m53,586s

- j5:
1m0,350s

### **Con ccache**

Comando ejemplo: time make -j1 CC="cache gcc"

- j1:
1m47,170s

- Segunda compilacion:
0m16,152s

Antes de probar con -j4 limpiamos la cache con "ccache -C"

- j4:
1m8,038s

- Segunda compilación:
0m3,894s


## **Conclusiones**

***¿A partir de qué valores de N ya no supone una mejora sustancial el incremento en el número de trabajos en paralelo?***

La máquina virtual en la que se ha compilado el código sólo tiene asignados 4 núcleos, por lo que a partir de N=5 el tiempo de compilación empeora, si tuviese más núcleos el valor de N podría ser mayor y seguir mejorando el tiempo de compilación.

***¿Hay fallos de compilación con ejecuciones en paralelo?***

No he encontrado ningun fallo de compilación.

***Diferencia al usar ccache***

Como podemos observar en los resultados del tiempo de compilación al utilizar ccache el tiempo mejora considerablemente en la segunda compilacion, ya que no es necesario compilar el codigo entero, al guardarse partes en la caché, las cuales se acceden en la segunda compilación.



