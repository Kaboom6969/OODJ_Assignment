package Exceptions.LazyEntityExceptions.LazyEntityListExceptions;

import Exceptions.LazyEntityExceptions.LazyEntityException;

public class LazyEntityListEntityRepeatedException extends LazyEntityException
{
    public LazyEntityListEntityRepeatedException(String message)
    {
        super(message);
    }

    public LazyEntityListEntityRepeatedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyEntityListEntityRepeatedException(Throwable cause)
    {
        super(cause);
    }

    public LazyEntityListEntityRepeatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyEntityListEntityRepeatedException()
    {
    }
}
