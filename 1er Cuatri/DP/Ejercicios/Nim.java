import java.util.Scanner;

//Juego del Nim
public class Nim{

    private int N;
    private int M;

    Boolean[][] A;
        
    public void init(int n, int m){
        N=n;
        M=m;
        A= new Boolean[N+1][M+1];
    }

    //Coste O(M^N)
    public boolean best (int n, int m) {
    
        boolean res=false;
        if ( n==0 || (n==1 && m==1)){
        res = false;
        } else {
        for ( int k = 1; k <= Math.min(n, M); ++k )
        if ( k != m && !best(n - k, k) )
        res = true;
        }
        return res;
    }
    
    //Coste O(N*M^2)
    public boolean bestA (int n, int m) {
        
        boolean res=false;
        if(A[n][m]==null){
            if ( n==0 || (n==1 && m==1)){
                res = false;
            } else {
                for ( int k = 1; k <= Math.min(n, M); ++k ){
                    if ( k != m && !bestA(n - k, k) )
                        res = true;
                }
            }
            A[n][m]= res;
        }else{
            res=A[n][m];
        }
        return res;
    }
        
    //Coste O(N*M^2)
    public boolean bestI (int n0, int m0) {
        boolean[][] A = new boolean[M+1][M+1]; //Con optimización de almacén

        for(int n=0;n<=N;++n){
            for(int m=0;m<=M;++m){
                A[n % (M+1)][m]=false;
                for ( int k = 1; k <= Math.min(n, M); ++k ){
                    if ( k != m && !A[(n-k) % (M+1)][k] )
                        A[n % (M+1)][m] = true;
                }
            }
        }
        return A[n0 % (M+1)][m0];
    }

    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Recursivo");
        System.out.println("2. Recursivo con Almacén");
        System.out.println("3. Iterativo");
        System.out.println("Elige: ");
        int op = sc.nextInt();

        System.out.print("n: ");
        int n = sc.nextInt();
        int m=0;
        int M=3;

        Nim ej = new Nim();
        ej.init(n,M);
 
        boolean result;
        if (op == 1){
            //max n=60
            result = ej.best(n,m);
        }else if (op==2){
            //max n=10000
            result=ej.bestA(n,m);
        }else{
            //max n=15.000.000
            result = ej.bestI(n,m);
        }

        System.out.println("Result: "+result);    
    }
}
