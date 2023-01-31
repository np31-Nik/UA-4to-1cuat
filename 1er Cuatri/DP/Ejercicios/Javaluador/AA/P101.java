// Nikita Polyanskiy Y4441167L
package AA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class P101 {

    int C[];
    int D[][];
    boolean[] S;
    ArrayList<Integer> sol;
    ArrayList<Integer> auxArray;
    int best;

    public void init(String[] data){
        String[] a = data[0].split("\\p{Space}+");
        D = new int[data.length][a.length];
        C = new int[D[0].length];
        S = new boolean[data.length];
        sol = new ArrayList<>();
        auxArray = new ArrayList<>();

        C[0]=0;

        for(int i=0;i<data.length;i++){
            for(int j=0;j<a.length;j++)
                D[i][j]=Integer.parseInt(a[j]);
            
            if(i<data.length-1)
                a=data[i+1].split("\\p{Space}+");
            
        }
        
        for(int i=1;i<C.length;++i)
            C[i]=C[i-1]+calcCota(data.length,i);
    }

    public int calcCota(int sup, int producto){
        int min=Integer.MAX_VALUE;
        
        for(int j=sup-1;j>=0;j--)
            min=Integer.min(min,D[j][producto]);
        
        if(min==0)
            min+=1;
    
        return min;
    }

    public ArrayList<Integer> bestSolution(String[] data){
        init(data);
        int res=Integer.MAX_VALUE;
        best=0;

        for(int i=0;i<D.length;i++){
            best=0;
            for(int j=0;j<D[i].length;j++){
                best+=D[i][j];
                if(best>res)
                    break;
            }
            res=Integer.min(res,best);
        }
        best+=1;
        
        return best(C.length-1,0);
    }

    public ArrayList<Integer> best(int producto, int sum){
    int aux;


         if (C[producto]+sum < best) {
             if (producto == 0) {
                 if(sum <= best){
                     best = sum;
                     sol = new ArrayList<>();
                     for(int i=auxArray.size()-1;i>=0;i--)
                         sol.add(auxArray.get(i));
                 }                
             } else {
                 for(int i=0;i< S.length;i++){
                     if(!S[i]){
                         aux=sum;
                         S[i]=true;
                         auxArray.add(i+1);
                         best(producto-1, sum+D[i][producto]+ D[i][0]);
                         if(auxArray.size()>0)
                             auxArray.remove(auxArray.size()-1);
                         S[i]=false;
                         sum=aux;
                     }else{
                         auxArray.add(i+1);
                         best(producto-1, sum+D[i][producto]);
                         if(auxArray.size()>0)
                             auxArray.remove(auxArray.size()-1);
                     }
                 }
             }
         }
         return sol;        
     }
}
