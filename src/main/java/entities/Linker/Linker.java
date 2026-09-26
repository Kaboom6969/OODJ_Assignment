package entities.Linker;

import Exceptions.IdPrefixExceptions.IdPrefixReapeatedException;
import Exceptions.LinkerExceptions.LinkerException;
import Tools.PrefixHandler.PrefixFinder;

public record Linker(String first, String second)
{
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

    public Linker swap()
    {
        return new Linker(this.second, this.first);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linker linker = (Linker) o;
        return first.equals(linker.first) && second.equals(linker.second);
    }

}
