package entities.Linker;

import Exceptions.IdPrefixExceptions.IdPrefixReapeatedException;
import Exceptions.LinkerExceptions.LinkerException;
import Exceptions.LinkerExceptions.LinkerNotFoundException;
import Tools.FileHandler.FileDataHandler;
import Tools.PrefixHandler.PrefixFinder;

import java.util.Objects;

public class Linker
{
    public final String first;
    public final String second;

    public String getData(LinkerManager.KeyLocation keyLocation)
    {
        switch (keyLocation)
        {
            case FIRST:
                return this.first;
            case SECOND:
                return this.second;
            default:
                throw new LinkerException("Cannot find the data");
        }
    }
    public Linker(String first, String second)
    {
        this.first = first;
        if (PrefixFinder.findPrefix(first).equals(PrefixFinder.findPrefix(second)))
        {
            throw new IdPrefixReapeatedException("first and second prefixes are the same");
        }
        this.second = second;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linker linker = (Linker) o;
        return first.equals(linker.first) && second.equals(linker.second);
    }
    @Override
    public int hashCode()
    {
        return Objects.hash(first, second);
    }

}
