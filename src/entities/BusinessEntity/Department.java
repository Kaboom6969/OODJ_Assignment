package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import Iterator.LazyEntityList;
import Tools.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.Doctor;

import java.util.List;

public class Department extends BusinessEntity<DepartmentToFile> implements OwnEntities<Doctor>, Linkable
{

    public FileDataHandler linkFile;
    private LazyEntityList<Doctor> doctors;

    public Department(String id, String name, List<String> doctorIds, FileDataHandler doctorDataHandler)
    {
        super(new DepartmentToFile(id,name));
        doctors = new LazyEntityList<Doctor>(doctorIds, doctorDataHandler);
    }

    public Department(DepartmentToFile self, List<String> doctorIds, FileDataHandler doctorDataHandler)
    {
        super(self);
        doctors = new LazyEntityList<Doctor>(doctorIds, doctorDataHandler);
    }

    public Department(DepartmentToFile self)
    {
        super(self);
    }


    @Override
    public LazyEntityList<Doctor> getEntities()
    {
        return doctors;
    }

    @Override
    public void setEntities(LazyEntityList<Doctor> entities)
    {
        this.doctors = entities;
    }

    @Override
    public Doctor getEntity(int index)
    {
        return getEntities().get(index);
    }

    @Override
    public Doctor getEntity(String id)
    {
        return getEntities().get(id);
    }

    @Override
    public void setEntity(int index, Doctor entity)
    {
    }

    @Override
    public FileDataHandler getLinkFile()
    {
        return linkFile;
    }

    @Override
    public void setLinkFile(FileDataHandler linkFile)
    {
        this.linkFile = linkFile;
    }
}
