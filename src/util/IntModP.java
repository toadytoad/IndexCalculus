package util;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class IntModP implements Serializable {
    public BigInteger p;
    public BigInteger n;

    public IntModP(BigInteger p, BigInteger n) {
        this.p = p;
        this.n = n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntModP intModP = (IntModP) o;
        return Objects.equals(p, intModP.p) && Objects.equals(n, intModP.n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p, n);
    }

    public IntModP add(IntModP o){
        assert o.p.equals(p);
        BigInteger r = o.n.add(n);
        //noinspection ComparatorResultComparison
        if(r.compareTo(p)!=-1){
            r = r.subtract(p);
        }
        return new IntModP(p, r);
    }
    public IntModP subtract(IntModP o){
        assert o.p.equals(p);
        BigInteger r = o.n.subtract(n);
        if(r.signum()==-1){
            r = r.add(p);
        }
        return new IntModP(p, r);
    }
    public IntModP multiply(IntModP o){
        assert o.p.equals(p);
        BigInteger r = o.n.multiply(n).mod(p);
        return new IntModP(p, r);
    }
    public IntModP divide(IntModP o){
        assert o.p.equals(p);
        return new IntModP(p, n.multiply(o.n.modInverse(p)).mod(p));
    }

}
