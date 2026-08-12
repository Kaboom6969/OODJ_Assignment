package entities.BusinessEntity;

import Interfaces.OwnEntity;
import entities.LazyEntity.LazyEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorToFile;

public class Doctor extends BusinessEntity<DoctorToFile> implements OwnEntity<DepartmentToFile>
{
    private LazyEntity<DepartmentToFile> belongsToDepartment;

    public Doctor(String selfId, FileDataHandler selfFile, String departmentId, FileDataHandler departmentFile)
    {
        super(selfId,selfFile);
        belongsToDepartment = new LazyEntity<DepartmentToFile>(departmentId,new EntityHandler(departmentFile));
    }

    @Override
    public DepartmentToFile getEntity()
    {
        return belongsToDepartment.getSelf();
    }

    @Override
    public void setEntity(DepartmentToFile entity)
    {
        this.belongsToDepartment.setSelf(entity);
    }
}
