package Exceptions.ConvertMapExceptions;

public class MapEmptyException extends RuntimeException
{
    public MapEmptyException()
    {
    }

    public MapEmptyException(String message)
    {
        super(message);
    }

    public MapEmptyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public MapEmptyException(Throwable cause)
    {
        super(cause);
    }

    public MapEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
