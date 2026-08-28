package Exceptions.LinkerExceptions;

public class LinkerNotFoundException extends LinkerException
{
    public LinkerNotFoundException()
    {
    }

    public LinkerNotFoundException(String message)
    {
        super(message);
    }

    public LinkerNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LinkerNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public LinkerNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
