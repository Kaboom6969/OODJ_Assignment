package Operations.AdminOperation;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Interfaces.ConvertToFileData;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.BaseEntity.Users.User;
import entities.BusinessEntity.BusinessEntity;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.MedicalManager;
import entities.BusinessEntity.Patient;

import java.util.ArrayList;
import java.util.List;

public class AdminOperation
{
    private final HospitalEntityAllocator hospitalEntityAllocator;
    public record CRUDInformation(boolean isSuccess,String message){}

    public AdminOperation(HospitalEntityAllocator hospitalEntityAllocator)
    {
        this.hospitalEntityAllocator = hospitalEntityAllocator;
    }

    public List<BusinessEntity<? extends User>> getAllUsers()
    {
        List<BusinessEntity<? extends User>> patients = hospitalEntityAllocator.getAllBusinessEntities(PatientToFile.PREFIX);
        List<BusinessEntity<? extends User>> medicalManagers = hospitalEntityAllocator.getAllBusinessEntities(MedicalManagerToFile.PREFIX);
        List<BusinessEntity<? extends User>> doctors = hospitalEntityAllocator.getAllBusinessEntities(DoctorToFile.PREFIX);
        List<BusinessEntity<? extends User>> allUsers = new ArrayList<BusinessEntity<? extends User>>();
        allUsers.addAll(patients); allUsers.addAll(medicalManagers); allUsers.addAll(doctors);
        return allUsers;
    }

    public CRUDInformation deleteUser(BusinessEntity<? extends User> user)
    {
        try
        {
            hospitalEntityAllocator.deleteBusinessEntity(user);
            return new CRUDInformation(true,"Success");
        } catch (EntityNotMatchException | EntityNotFoundException e)
        {
            return new CRUDInformation(false,e.getMessage());
        }
    }
    public <T extends BaseEntity & ConvertToFileData> CRUDInformation addUser(T user)
    {
        try
        {
            hospitalEntityAllocator.addEntityForceNewId(user);
            return new CRUDInformation(true,"Success");
        } catch (RuntimeException e)
        {
            return new CRUDInformation(false,e.getMessage());
        }
    }

    public CRUDInformation addUser(BusinessEntity<? extends User> user)
    {
        try
        {
            hospitalEntityAllocator.addEntityForceNewId(user.getSelf());
            return new CRUDInformation(true, "Success");
        }
        catch (RuntimeException e)
        {
            return new CRUDInformation(false,e.getMessage());
        }
    }

    public void allocateDoctorToMedicalManager(Doctor doctor,MedicalManager medicalManager)
    {
        doctor.setBelongsToMedicalManager(medicalManager.getSelf());
        hospitalEntityAllocator.saveChanges(doctor);
    }

}
