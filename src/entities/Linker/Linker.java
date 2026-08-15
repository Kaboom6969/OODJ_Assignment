package entities.Linker;

import Exceptions.IdPrefixExceptions.IdPrefixReapeatedException;
import Tools.FileHandler.FileDataHandler;

public class Linker
{
    public final String first;
    public final String second;

    public Linker(String first, String second)
    {
        this.first = first;
        if (FileDataHandler.prefixFinder(first).equals(FileDataHandler.prefixFinder(second)))
        {
            throw new IdPrefixReapeatedException("first and second prefixes are the same");
        }
        this.second = second;
    }

}
