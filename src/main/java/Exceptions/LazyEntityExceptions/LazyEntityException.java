package Exceptions.LazyEntityExceptions;

public class LazyEntityException extends RuntimeException
{
    public LazyEntityException(String message)
    {
        super(message);
    }

    public LazyEntityException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyEntityException(Throwable cause)
    {
        super(cause);
    }

    public LazyEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyEntityException()
    {
    }
}
