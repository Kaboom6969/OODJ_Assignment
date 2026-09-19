package Exceptions.LinkerExceptions;

public class LinkerRequireOneOnlyException extends LinkerException
{
    public LinkerRequireOneOnlyException(String message)
    {
        super(message);
    }

    public LinkerRequireOneOnlyException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LinkerRequireOneOnlyException(Throwable cause)
    {
        super(cause);
    }

    public LinkerRequireOneOnlyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LinkerRequireOneOnlyException()
    {
    }
}
