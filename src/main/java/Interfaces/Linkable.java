package Interfaces;

import Tools.FileHandler.FileDataHandler;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.util.List;

public interface Linkable
{
    public FileDataHandler getLinkFile();

    public void setLinkFile(FileDataHandler linkFile);

    public List<LinkerManager> getLinkerManager();
}
