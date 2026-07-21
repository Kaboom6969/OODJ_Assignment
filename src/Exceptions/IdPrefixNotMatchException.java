package Exceptions;

public class IdPrefixNotMatchException extends IdPrefixException
{
    public IdPrefixNotMatchException() {super();}
    public IdPrefixNotMatchException(String message) {super(message);}
    public IdPrefixNotMatchException(Throwable cause) {super(cause);}
    public IdPrefixNotMatchException(String message,Throwable cause){super(message,cause);}
}
