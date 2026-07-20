package Exceptions;

public class IdPrefixException extends RuntimeException
{
    public IdPrefixException()
    {
    }

    public IdPrefixException(String message)
    {
        super(message);
    }

    public IdPrefixException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IdPrefixException(Throwable cause)
    {
        super(cause);
    }

    public IdPrefixException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
