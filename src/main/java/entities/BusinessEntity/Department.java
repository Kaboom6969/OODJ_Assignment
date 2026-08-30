package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntities;
import entities.BaseEntity.BaseEntity;
import entities.LazyEntity.LazyEntityList;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorToFile;
import entities.Linker.Linker;
import entities.Linker.LinkerManager;

import java.util.ArrayList;
import java.util.List;

public class Department extends BusinessEntity<DepartmentToFile> implements OwnEntities, Linkable
{

    public FileDataHandler linkFile;
    private LazyEntityList<DoctorToFile> doctors;

    public Department(String selfId, FileDataHandler selfFile, List<String> doctorIds, FileDataHandler doctorDataHandler)
    {
        super(selfId, selfFile);
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
    public List<LazyEntityList<? extends BaseEntity>> getEntities()
    {
        List<LazyEntityList<? extends BaseEntity>> list = new ArrayList<>();
        list.add(doctors);
        return list;
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

    @Override
    public List<LinkerManager> getLinkerManager()
    {
        LinkerManager manager = new LinkerManager(DepartmentToFile.class,DoctorToFile.class);
        for (DoctorToFile doctor : doctors)
        {
            Linker linker = new Linker(this.self.getId(),doctor.getId());
            manager.addLinker(linker);
        }
        return new ArrayList<>(List.of(manager));
    }
}
