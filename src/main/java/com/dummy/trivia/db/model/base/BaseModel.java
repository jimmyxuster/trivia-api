package com.dummy.trivia.db.model.base;
import org.springframework.data.annotation.Id;
public class BaseModel {

    @Id
    public String id;

    private final long createdTime = System.currentTimeMillis();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseModel that = (BaseModel) o;

        if (createdTime != that.createdTime) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (createdTime ^ (createdTime >>> 32));
        return result;
    }
}
