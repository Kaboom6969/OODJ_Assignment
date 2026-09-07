package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnerShip;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.Users.AdminToFile;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Admin extends BusinessEntity<AdminToFile> implements OwnerShip, Linkable
{
    public Admin(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        super(selfId,selfFile);
    }
}