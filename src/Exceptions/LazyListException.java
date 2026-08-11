package Exceptions;

public class LazyListException extends RuntimeException
{
    public LazyListException(String message)
    {
        super(message);
    }

    public LazyListException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyListException(Throwable cause)
    {
        super(cause);
    }

    public LazyListException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyListException()
    {
    }
}
