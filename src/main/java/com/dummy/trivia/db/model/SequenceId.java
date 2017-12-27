package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "sequence")
public class SequenceId extends BaseModel {

    @Field("sequence_id")
    private long sequenceId;

    @Field("coll_name")
    private String collectionName;

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
