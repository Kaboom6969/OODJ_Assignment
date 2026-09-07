package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDateTime;
import java.util.Objects;

public class FeedbackToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "FB";

    private int rating;
    private String comment;
    private LocalDateTime createdTime;

    public int getRating()
    {
        return rating;
    }

    public String getComment()
    {
        return comment;
    }

    public LocalDateTime getCreatedTime()
    {
        return createdTime;
    }

    public void setRating(int rating)
    {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating should be between 1 and 5");
        this.rating = rating;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public FeedbackToFile(String id, int rating, String comment, LocalDateTime createdTime)
    {
        super(id);
        setRating(rating);
        this.comment = comment;
        this.createdTime = createdTime;
    }

    public FeedbackToFile(String[] data)
    {
        this
                (
                        data[0],
                        Integer.parseInt(data[1]),
                        data[2],
                        LocalDateTime.parse(data[3])
                );
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }

    @Override
    public String toFileData()
    {
        return
                this.getId() + "|" +
                        this.getRating() + "|" +
                        this.getComment() + "|" +
                        this.getCreatedTime();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        FeedbackToFile feedback = (FeedbackToFile) o;
        if (rating != feedback.rating) return false;
        if (!Objects.equals(comment, feedback.comment)) return false;
        return Objects.equals(createdTime, feedback.createdTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), rating, comment, createdTime);
    }
}