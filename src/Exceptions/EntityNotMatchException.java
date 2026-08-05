package Exceptions;

public class EntityNotMatchException extends EntityException
{
    public EntityNotMatchException()
    {
    }

    public EntityNotMatchException(String message)
    {
        super(message);
    }

    public EntityNotMatchException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EntityNotMatchException(Throwable cause)
    {
        super(cause);
    }

    public EntityNotMatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
