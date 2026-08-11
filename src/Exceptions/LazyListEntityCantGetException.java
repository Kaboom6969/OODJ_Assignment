package Exceptions;

public class LazyListEntityCantGetException extends LazyListException
{
    public LazyListEntityCantGetException(String message)
    {
        super(message);
    }

    public LazyListEntityCantGetException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyListEntityCantGetException(Throwable cause)
    {
        super(cause);
    }

    public LazyListEntityCantGetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyListEntityCantGetException()
    {
    }
}
