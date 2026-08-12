package Exceptions.IdPrefixExceptions;

public class IdPrefixOversizeException extends IdPrefixException
{
    public IdPrefixOversizeException(String message)
    {
        super(message);
    }

    public IdPrefixOversizeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IdPrefixOversizeException(Throwable cause)
    {
        super(cause);
    }

    public IdPrefixOversizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdPrefixOversizeException()
    {
    }
}
