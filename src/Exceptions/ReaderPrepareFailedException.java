package Exceptions;

public class ReaderPrepareFailedException extends RuntimeException
{
    public ReaderPrepareFailedException()
    {
        super();
    }

    public ReaderPrepareFailedException(String message)
    {
        super(message);
    }

    public ReaderPrepareFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ReaderPrepareFailedException(Throwable cause)
    {
        super(cause);
    }

    public ReaderPrepareFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
