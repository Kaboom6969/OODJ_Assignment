package Interfaces;

import Tools.FileHandler.FileDataHandler;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.util.List;

public interface Linkable
{
    public List<LinkerManager> getLinkerManager();
}
