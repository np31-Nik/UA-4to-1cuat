// Nikita Polyanskiy Y4441167L
package AA;

import java.util.ArrayList;
import java.util.Arrays;
public class P100{

    private int L;  // Separacion minima entre dias
    private int N;  // Nº Max de dias
    private ArrayList<Integer> D = new ArrayList<Integer>(); // Dias
    private ArrayList<Integer> res = new ArrayList<Integer>(); // Resultado
    int [][] A; // Almacen

    private boolean primero=true;

//----------------------Funciones para inicializar y obtener datos----------------------
    public void init(String data){
        ArrayList<Integer> array = dataToArray(data);
        N=array.get(0);
        L=array.get(1);
        D=new ArrayList<Integer>(array.subList(2,array.size()));
        A = new int[N+1][D.size()+1];
    }

    private static ArrayList<Integer> dataToArray(String cad){
        String[] splited = cad.split("\\p{Space}+");
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for(int i = 0; i < splited.length; i++) {
            numbers.add(Integer.parseInt(splited[i]));
        }
        return numbers;
    }
//----------------------------------------------------------------------------------------

// Recursivo
    public void recursivo(int n,int m){
        // n = nº de dias que se pueden elegir
        // m = posicion del dia

        if(n<=0 || m<=0){
            res.clear();
        }else{
            if(recursivoA(n, m-1) < D.get(m-1)+recursivoA(n-1, m-L)){
                recursivo(n-1,m-L);
                res.add(m);
            }else{
                recursivo(n,m-1);
            }
        }
    }

// Funcion auxiliar para Recursivo (Almacen)
    public int recursivoA(int n, int m){
        if(n<=0 || m<=0){
            return 0;
        }else{
            if(A[n][m]<=0){
                A[n][m]=Integer.max(recursivoA(n,m-1),D.get(m-1)+recursivoA(n-1,m-L));
            }
        }
        return A[n][m];
    }

// Iterativo
    public void iterativo(int n, int m){
        if (!primero){
            res.clear();
        }else{
            primero=false;
        }

        // Rellenar almacen
        for(int i = D.size()-1;i>=0;i--){
            for(int j=0;j<N;j++){
                    if(i+L<D.size() && j+1<N){
                        A[j][i]=Integer.max(A[j][i+1],D.get(i)+A[j+1][i+L]);
                    }else{
                        A[j][i]=Integer.max(D.get(i),A[j][i+1]);
                    }
            }
        }

        // Obtener resultados
        for(int i=0 , j=0; i<N && j<D.size();){
            if(j+1 < D.size() && A[i][j]==A[i][j+1]){
                    j++;
            }else{
                res.add(j+1);
                i++;
                j=j+L;
            }
        }
    }

// Best Solution
    public ArrayList<Integer> bestSolution(String data){
        init(data); //Inicializa L,N,A

        recursivo(N,D.size()); // 0.114s en Javaluador
        // iterativo(N,D.size()); //0.133s en Javaluador
        
        return res;
    }


// Main de prueba
/*
public static void main(String args[]){
        P100 p = new P100();
        // Cad: N, L, D[]
        String cad = "3 2 1 2 4 7 3"; // Resultado: [2,4]
        String cad2 = "2 5 11 2 12 6 19 10 13 2 16 1"; //Resultado: [3,9]
        System.out.println(p.bestSolution(cad)); 
        System.out.println(p.bestSolution(cad2)); 

    }
    */
}