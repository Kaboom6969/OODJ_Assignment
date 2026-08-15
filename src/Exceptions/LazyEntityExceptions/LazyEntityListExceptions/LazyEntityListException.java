package Exceptions.LazyEntityExceptions.LazyEntityListExceptions;

import Exceptions.LazyEntityExceptions.LazyEntityException;

public class LazyEntityListException extends LazyEntityException
{
    public LazyEntityListException(String message)
    {
        super(message);
    }

    public LazyEntityListException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyEntityListException(Throwable cause)
    {
        super(cause);
    }

    public LazyEntityListException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyEntityListException()
    {
    }
}
