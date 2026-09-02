package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import entities.BaseEntity.BaseEntity;
import entities.LazyEntity.LazyEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorToFile;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends BusinessEntity<DoctorToFile> implements OwnEntity, Linkable
{
    private LazyEntity<DepartmentToFile> belongsToDepartment;

    public Doctor(String selfId, FileDataHandler selfFile, String departmentId, FileDataHandler departmentFile)
    {
        super(selfId,selfFile);
        belongsToDepartment = new LazyEntity<DepartmentToFile>(departmentId,new EntityHandler(departmentFile));
    }

    public DepartmentToFile getBelongsToDepartment()
    {
        return belongsToDepartment.getSelf();
    }

    public void setBelongsToDepartment(DepartmentToFile departmentToFile)
    {
        belongsToDepartment.changeSelf(departmentToFile);
    }

    @Override
    public List<LazyEntity<? extends BaseEntity>> getEntity()
    {
        List<LazyEntity<? extends BaseEntity>> list = new ArrayList<>();
        list.add(belongsToDepartment);
        return list;
    }


    @Override
    public List<LinkerManager> getLinkerManager()
    {
       List<LinkerManager> list = new ArrayList<>();
       LinkerManager linkerManager = new LinkerManager(DoctorToFile.class, DepartmentToFile.class);
       linkerManager.addLinker(new Linker(this.self.getId(),this.belongsToDepartment.getId()));
       list.add(linkerManager);
       return list;
    }
}
