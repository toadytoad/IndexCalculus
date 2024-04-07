package relations;


import java.io.Serializable;

public class Relation implements Serializable {
    public long exp;
    public int[] relations;

    public Relation(long exp, int[] relations) {
        this.exp = exp;
        this.relations = relations;
    }
}
