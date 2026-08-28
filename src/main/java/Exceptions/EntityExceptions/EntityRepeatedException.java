package Exceptions.EntityExceptions;

public class EntityRepeatedException extends EntityException
{
    public EntityRepeatedException(String message)
    {
        super(message);
    }

    public EntityRepeatedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityRepeatedException(Throwable cause)
    {
        super(cause);
    }

    public EntityRepeatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EntityRepeatedException()
    {
    }
}
