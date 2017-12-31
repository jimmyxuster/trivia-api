package com.dummy.trivia.db.model;

import com.dummy.trivia.db.model.base.BaseModel;
import com.google.gson.annotations.Expose;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@Table(name = "question")
public class Question {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose
    private String id;

    private final long createdTime = System.currentTimeMillis();

    @NotNull
    @Expose
    private String description;

    @Expose
    private String type;

    @Expose
    @Column(name = "option_a")
    private String optionA;

    @Expose
    @Column(name = "option_b")
    private String optionB;

    @Expose
    @Column(name = "option_c")
    private String optionC;

    @Expose
    @Column(name = "option_d")
    private String optionD;

    @Expose
    private String answer;

    public long getCreatedTime() {
        return createdTime;
    }

    public String getAnswer() {
        return answer;
    }

    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (createdTime ^ (createdTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", createdTime=" + createdTime +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
