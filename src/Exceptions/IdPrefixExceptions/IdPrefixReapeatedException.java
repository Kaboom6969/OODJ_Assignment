package Exceptions.IdPrefixExceptions;

public class IdPrefixReapeatedException extends IdPrefixException
{
    public IdPrefixReapeatedException(String message)
    {
        super(message);
    }

    public IdPrefixReapeatedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IdPrefixReapeatedException(Throwable cause)
    {
        super(cause);
    }

    public IdPrefixReapeatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdPrefixReapeatedException()
    {
    }
}
