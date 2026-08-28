package Exceptions.IdPrefixExceptions;

public class IdPrefixNotFoundException extends IdPrefixException
{
    public IdPrefixNotFoundException()
    {
    }

    public IdPrefixNotFoundException(String message)
    {
        super(message);
    }

    public IdPrefixNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IdPrefixNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public IdPrefixNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
