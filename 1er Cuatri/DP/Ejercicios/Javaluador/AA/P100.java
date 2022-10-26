package AA;

import java.util.ArrayList;
import java.util.Arrays;

public class P100{

    private int L;
    private int N;

    public void init(int L, int N){
        this.L=L;
        this.N=N;
    }

    private static ArrayList<Integer> dataToArray(String cad){
        String[] splited = cad.split("\\p{Space}+");
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for(int i = 0; i < splited.length; i++) {
            numbers.add(Integer.parseInt(splited[i]));
        }
        return numbers;
    }

    public ArrayList<Integer> bestSolutionR(String data){
        ArrayList<Integer> res = new ArrayList<Integer>();

        return res;
    }

    public static void main(String args[]){
        P100 p = new P100();

        String cad = "3 2 1 2 4 7 3";
        ArrayList<Integer> array = dataToArray(cad);

        int L=array.get(0);
        int N=array.get(1);
        p.init(L,N);

        ArrayList<Integer> dias = new ArrayList<Integer>(array.subList(2,array.size()));
        System.out.println(dias);         
  
    }
}