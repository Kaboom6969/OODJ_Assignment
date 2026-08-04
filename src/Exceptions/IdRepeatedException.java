package Exceptions;

public class IdRepeatedException extends IdException
{
    public IdRepeatedException(String message)
    {
        super(message);
    }

    public IdRepeatedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public IdRepeatedException(Throwable cause)
    {
        super(cause);
    }

    public IdRepeatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IdRepeatedException()
    {
    }
}
