package relations;

import java.io.Serializable;
import java.math.BigInteger;

public class PartialRelation implements Serializable {
    public long exp;
    public int[] relations;
    public int extra;

    public PartialRelation(long exp, int[] relations, int extra) {
        this.exp = exp;
        this.relations = relations;
        this.extra=extra;
    }
}
