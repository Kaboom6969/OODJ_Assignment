package entities.Linker;

import Interfaces.ConvertToFileData;

import java.util.ArrayList;
import java.util.List;

public class LinkerManager implements ConvertToFileData
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

    public void addLinker(Linker linker)
    {
        linkers.add(linker);
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

    @Override
    public String toFileData()
    {
        List<String> ans = new ArrayList<>();
        for (Linker linker : linkers)
        {
            ans.add(linker.first + "|" + linker.second);
        }
        return String.join("\n", ans);
    }
}
