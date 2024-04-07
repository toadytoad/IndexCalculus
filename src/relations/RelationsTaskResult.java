package relations;

import java.io.Serializable;
import java.util.List;

public class RelationsTaskResult implements Serializable {
    public List<Relation> fullRelations;
    public List<PartialRelation> partialRelations;

    public RelationsTaskResult(List<Relation> fullRelations, List<PartialRelation> partialRelations) {
        this.fullRelations = fullRelations;
        this.partialRelations = partialRelations;
    }
}
