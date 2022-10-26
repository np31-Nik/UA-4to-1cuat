package AA;

import java.util.HashMap;

/*
 * Autor: Juan R. Rico
 * 
 * Descripción: P004 es una solución propuesta al juego del Nim para 2 jugadores con N fichas totales 
 * y M a retirar por jugada. El programa devuelve la primera jugada ganadora, o bien, -1 en 
 * caso de no tener estrategia ganadora.
 * 
 * Está resulto por:
 *   - programación dinámica (PD) recursiva (pura);
 *   - PD con almacén (memoization); 
 *   - PD iterativa.
 * 
 * 
 * Advertencia: Se puede probar este código en el sistema de corrección de problemas de la asignatura 
 * llamado Javaludor (http://javaluador.dlsi.ua.es/) el usuario y la contraseña es el mismo que el usado
 * en los servicios de la EPSA. Este sistema automático testea el programa con una batería de test y 
 * devuelve el porcentaje de tests superador correctamente.
 * 
 */
public class P004 {

    int N, M;
    HashMap<Integer, Integer> A;

    private void init(String data) {
        String[] token = data.split("\\p{Space}+");
        this.N = Integer.parseInt(token[0]);  //Número total de fichas
        this.M = Integer.parseInt(token[1]);  //Número máximo a retirar por jugada
        A = new HashMap<Integer, Integer>();  //Almacén de soluciones
    }

    /**
     * pdr: Programación dinámica recursiva
     * @param n número de fichas que quedan en el tablero
     */
    public int pdr(int n) {
        int res = -1;

        if (n > 0) {    //Quedan fichas
            for (int k = 1; k <= Math.min(n, M); k++) {
                if (pdr(n - k) < 0) {
                    res = k;
                }
            }
        }
        return res;
    }

    /**
     * pdr_a: Programación dinámica recursiva con almacén (memoization)
     * @param n número de fichas que quedan en el tablero
     */
    public int pdr_a(int n) {
        int res = -1;

        if (n > 0) {    //Quedan fichas
            if (A.containsKey(n)) { //Valor almacenado. 
                res = A.get(n);
            } else {                //Hay que calcularlo.
                for (int k = 1; k <= Math.min(n, M); k++) {
                    if (pdr_a(n - k) < 0) {
                        res = k;
                    }
                }
                A.put(n, res);
            }
        }
        return res;
    }

    /**
     * pdi: Programación dinámica iterativa
     * @param n número de fichas que quedan en el tablero
     */
    public int pdi(int n) {
        int res = -1;

        A.put(0, -1);
        for (int i = 1; i <= n; i++) {
            res=-1;
            for (int k = 1; k <= Math.min(i, M); k++) {
                if (A.get(i - k) < 0) {
                    res = k;
                }
            }
            A.put(i, res);
        }

        return A.get(n);
    }

    public Integer best(String s) {
        init(s);
        
        //Diferentes métodos de resolver el problema
        
        return pdr(N);
        //return pdr_a(N);
        //return pdi(N);
    }

    /*
     * Se puede probar los tres algoritmos desde la línea de instrucciones con
     * 
     * java AA/P004 "8 4" "5 4"
     * con resultados de 3 y -1 respectivamente.
     *  
     * Podéis comprobar con valores mayores de M y N, y verificar la diferencia de tiempos
     * del algoritmo recursivo puro respecto de los otros dos.
     * 
     */
   
    public static void main(String[] args) {
        P004 p = new P004();
        for (String s : args) {
            System.out.println(p.best(s));
        }
    }
}

