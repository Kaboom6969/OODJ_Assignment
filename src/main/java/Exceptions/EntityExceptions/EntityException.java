package Exceptions.EntityExceptions;

public class EntityException extends Exception
{
    public EntityException()
    {
    }

    public EntityException(String message)
    {
        super(message);
    }

    public EntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityException(Throwable cause)
    {
        super(cause);
    }

    public EntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
