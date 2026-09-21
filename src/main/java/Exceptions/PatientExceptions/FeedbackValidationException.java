package Exceptions.PatientExceptions;

/** Indicates invalid feedback submission input. */
public class FeedbackValidationException extends IllegalArgumentException
{
    public FeedbackValidationException(String message)
    {
        super(message);
    }
}
