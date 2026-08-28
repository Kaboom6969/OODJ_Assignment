package Exceptions.LazyEntityExceptions;


public class LazyEntityCantGetException extends LazyEntityException
{
    public LazyEntityCantGetException(String message)
    {
        super(message);
    }

    public LazyEntityCantGetException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyEntityCantGetException(Throwable cause)
    {
        super(cause);
    }

    public LazyEntityCantGetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyEntityCantGetException()
    {
    }
}
