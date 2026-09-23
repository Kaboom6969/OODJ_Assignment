package Exceptions.PatientExceptions;

/** Indicates invalid appointment booking, rescheduling, or cancellation input. */
public class BookingValidationException extends IllegalArgumentException
{
    public BookingValidationException(String message)
    {
        super(message);
    }
}
