package relations;

import java.math.BigInteger;

public class PartialRelation {
    public long exp;
    public int[] relations;
    public int extra;

    public PartialRelation(long exp, int[] relations, int extra) {
        this.exp = exp;
        this.relations = relations;
        this.extra=extra;
    }
}
