package Exceptions.LinkerExceptions;

public class LinkerException extends RuntimeException
{
    public LinkerException()
    {
    }

    public LinkerException(String message)
    {
        super(message);
    }

    public LinkerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LinkerException(Throwable cause)
    {
        super(cause);
    }

    public LinkerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
