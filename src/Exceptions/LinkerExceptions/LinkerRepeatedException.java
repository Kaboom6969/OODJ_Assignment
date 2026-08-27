package Exceptions.LinkerExceptions;

public class LinkerRepeatedException extends LinkerException
{
    public LinkerRepeatedException()
    {
    }

    public LinkerRepeatedException(String message)
    {
        super(message);
    }

    public LinkerRepeatedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LinkerRepeatedException(Throwable cause)
    {
        super(cause);
    }

    public LinkerRepeatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
