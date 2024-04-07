package relations;

import server.Task;
import util.IntModP;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class RelationsTask extends Task implements Serializable {
    public BigInteger p;
    public IntModP g;
    public IntModP b;
    public BigInteger[] factorBase;
    public long start,end;
    public RelationsTaskResult res;
    static BigInteger PARTIAL_BOUND = new BigInteger(Integer.toString(Integer.MAX_VALUE));

    public RelationsTask(BigInteger p, BigInteger g, BigInteger[] factorBase, long start, long end) {
        this.p = p;
        this.g = new IntModP(p, g);
        this.b = new IntModP(p, g.modPow(BigInteger.valueOf(start-1), p));
        this.factorBase = factorBase;
        this.start = start;
        this.end = end;
    }
    public void runTask(){
        if(res!=null){
            System.err.println("Task has already been run, do not run again");
            return;
        }
        res = new RelationsTaskResult(new ArrayList<>(), new ArrayList<>());
        for(long x = start; x<end; x++){
            b = b.multiply(g);
            Object r = tryFactorWithBase(b.n, b.p, x);
            if (r==null){
                continue;
            }
            if(r instanceof Relation) {
                res.fullRelations.add((Relation) r);
            } else {
                res.partialRelations.add((PartialRelation) r);
            }
        }
    }
    private Object tryFactorWithBase(BigInteger n, BigInteger p, long exp){
        int[] facsx = new int[factorBase.length];
        BigInteger x = n;
        for(int i = 1; i< factorBase.length; i++){
            while(x.mod(factorBase[i]).signum()==0){
                x = x.divide(factorBase[i]);
                facsx[i]++;
            }
        }
        if(x.equals(BigInteger.ONE)){
            return new Relation(exp, facsx);
        }
        int[] facsy = new int[factorBase.length];
        facsy[0] = 1;
        BigInteger y = p.subtract(n);
        for(int i = 1; i<factorBase.length; i++){
            while(y.mod(factorBase[i]).signum()==0){
                facsy[i]+=1;
                y = y.divide(factorBase[i]);
            }
        }
        if(y.equals(BigInteger.ONE)){
            return new Relation(exp, facsy);
        }
        if(x.compareTo(PARTIAL_BOUND) < 0){
            return new PartialRelation(exp, facsx, x.intValue());
        }
        if(y.compareTo(PARTIAL_BOUND) < 0){
            return new PartialRelation(exp, facsy, y.intValue());
        }
        return null;
    }
}
