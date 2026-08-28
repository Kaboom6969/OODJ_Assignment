package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import entities.LazyEntity.LazyEntityList;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorToFile;

import java.util.List;

public class Department extends BusinessEntity<DepartmentToFile> implements OwnEntities<DoctorToFile>, Linkable
{

    public FileDataHandler linkFile;
    private LazyEntityList<DoctorToFile> doctors;

    public Department(String selfId,FileDataHandler selfFile,List<String> doctorIds, FileDataHandler doctorDataHandler)
    {
        super(selfId,selfFile);
        doctors = new LazyEntityList<DoctorToFile>(doctorIds, doctorDataHandler);
    }

    public void addDoctor(String doctorId)
    {
        doctors.add(doctorId);
    }

    public void addDoctor(DoctorToFile doctor)
    {
        doctors.add(doctor);
    }

    public void setDoctor(int index, DoctorToFile doctor)
    {
        doctors.set(index, doctor);
    }

    public DoctorToFile getDoctor(int index)
    {
        return doctors.get(index);
    }


    @Override
    public LazyEntityList<DoctorToFile> getEntities()
    {
        return doctors;
    }

    @Override
    public void setEntities(LazyEntityList<DoctorToFile> entities)
    {
        this.doctors = entities;
    }

    @Override
    public DoctorToFile getEntity(int index)
    {
        return getEntities().get(index);
    }

    @Override
    public DoctorToFile getEntity(String id)
    {
        return getEntities().get(id);
    }

    @Override
    public void setEntity(int index, DoctorToFile entity)
    {
        doctors.set(index, entity);
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
