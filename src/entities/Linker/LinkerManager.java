package entities.Linker;

import java.util.ArrayList;
import java.util.List;

public class LinkerManager
{
    public List<Linker> linkers;

    public LinkerManager()
    {
        linkers = new ArrayList<Linker>();
    }

    public LinkerManager(List<Linker> linkers)
    {
        this.linkers = linkers;
    }

    public List<String> findBasedOnFirst(String first)
    {
        List<String> ans = new ArrayList<>();
        for (Linker linker : linkers)
        {
            if (linker.first.equals(first))
            {
                ans.add(linker.second);
            }
        }
        if (ans.isEmpty()) return null;
        return ans;
    }
    public List<String> findBasedOnSecond(String second)
    {
        List<String> ans = new ArrayList<>();
        for (Linker linker : linkers)
        {
            if (linker.second.equals(second))
            {
                ans.add(linker.first);
            }
        }
        if (ans.isEmpty()) return null;
        return ans;
    }
}
