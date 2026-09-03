package Tools.PrefixHandler;

import Exceptions.IdPrefixExceptions.IdPrefixNotFoundException;

public class PrefixFinder
{
    private static final int MAXIMUM_PREFIX_LENGTH = 10;
    public static String findPrefix(String id)
    {
        if (id == null || id.isEmpty()) throw new IdPrefixNotFoundException("The id is empty!");
        char[] prefix = new char[MAXIMUM_PREFIX_LENGTH];
        int prefixPointer = 0;
        for (int i = 0;i < id.length(); i ++)
        {
            if (Character.isLetter(id.charAt(i)))
            {
                if (prefixPointer >= MAXIMUM_PREFIX_LENGTH)
                    throw new IllegalArgumentException
                            ("prefix length is larger than maximum prefix length\n" +
                                    "Please check your file Or change the maximum prefix length");
                prefix[prefixPointer] = id.charAt(i);
                prefixPointer ++;
                continue;
            }
            if (Character.isDigit(id.charAt(i))) {break;}
        }
        if (prefixPointer == 0) throw new IdPrefixNotFoundException("No Prefix Found in this id!");
        return new String(prefix,0,prefixPointer);
    }
}
