package Exceptions.LazyEntityExceptions.LazyEntityListExceptions;

import Exceptions.LazyEntityExceptions.LazyEntityListExceptions.LazyEntityListException;

public class LazyEntityListEntityCantGetException extends LazyEntityListException
{
    public LazyEntityListEntityCantGetException(String message)
    {
        super(message);
    }

    public LazyEntityListEntityCantGetException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public LazyEntityListEntityCantGetException(Throwable cause)
    {
        super(cause);
    }

    public LazyEntityListEntityCantGetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LazyEntityListEntityCantGetException()
    {
    }
}
