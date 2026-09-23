package Exceptions.PatientExceptions;

/** Indicates invalid patient profile input. */
public class ProfileValidationException extends IllegalArgumentException
{
    public ProfileValidationException(String message)
    {
        super(message);
    }
}
