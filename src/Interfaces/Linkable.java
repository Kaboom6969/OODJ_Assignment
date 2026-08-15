package Interfaces;

import Tools.FileHandler.FileDataHandler;

public interface Linkable
{
    public FileDataHandler getLinkFile();

    public void setLinkFile(FileDataHandler linkFile);
}
