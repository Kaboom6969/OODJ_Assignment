package entities.Linker;

import Exceptions.IdPrefixExceptions.IdPrefixReapeatedException;
import Tools.FileHandler.FileDataHandler;

import java.util.Objects;

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
