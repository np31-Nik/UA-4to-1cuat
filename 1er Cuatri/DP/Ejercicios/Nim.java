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

    public static void main(String args[]){
        Nim ej = new Nim();
        
        int n = 10000;
        int m = 0;
        int M = 3;

        ej.init(n,M);

        System.out.print(ej.bestA(n,m));
    }
}
