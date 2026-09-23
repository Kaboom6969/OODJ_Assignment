package Operations.AdminOperation;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Interfaces.ConvertToFileData;
import Interfaces.Linkable;
import Interfaces.OwnerShip;
import Tools.EntityConvertManager;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.Users.*;
import entities.BusinessEntity.*;

import java.util.ArrayList;
import java.util.List;

public class AdminOperation
{
    private final Admin admin;
    private final HospitalEntityAllocator hospitalEntityAllocator;
    public record CRUDInformation(boolean isSuccess,String message){}

    public AdminOperation(HospitalEntityAllocator hospitalEntityAllocator,Admin admin)
    {
        this.admin = admin;
        this.hospitalEntityAllocator = hospitalEntityAllocator;
    }
    private CRUDInformation failure(Throwable throwable)
    {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause)
        {
            rootCause = rootCause.getCause();
        }
        String message = rootCause.getMessage();
        if (message == null || message.isBlank())
        {
            message = rootCause.getClass().getSimpleName();
        }
        return new CRUDInformation(false, message);
    }

    public List<BusinessEntity<? extends User>> getAllUsers()
    {
        List<BusinessEntity<? extends User>> admins = hospitalEntityAllocator.getAllBusinessEntities(AdminToFile.PREFIX);
        List<BusinessEntity<? extends User>> patients = hospitalEntityAllocator.getAllBusinessEntities(PatientToFile.PREFIX);
        List<BusinessEntity<? extends User>> medicalManagers = hospitalEntityAllocator.getAllBusinessEntities(MedicalManagerToFile.PREFIX);
        List<BusinessEntity<? extends User>> doctors = hospitalEntityAllocator.getAllBusinessEntities(DoctorToFile.PREFIX);
        List<BusinessEntity<? extends User>> allUsers = new ArrayList<BusinessEntity<? extends User>>();
        for (int i = 0; i<admins.size();i++)
        {
            if ((admins.get(i).getId().equals(admin.getId())))
            {
                admins.remove(i);
                break;
            }
        }
        allUsers.addAll(admins);allUsers.addAll(patients); allUsers.addAll(medicalManagers); allUsers.addAll(doctors);
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
            return failure(e);
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
            return failure(e);
        }
    }

    public <T extends User & ConvertToFileData> CRUDInformation addUser(List<String> data,Class<?> clazz)
    {
        try
        {
            T user = constructUser(data,clazz);
            hospitalEntityAllocator.addEntityForceNewId(user);
            return new CRUDInformation(true,"Success");
        } catch (RuntimeException e)
        {
            return failure(e);
        }

    }

    public <T extends BaseEntity & ConvertToFileData> T constructUser(List<String> data,Class<?> clazz)
    {
        String prefix = null;
        return (T) EntityConvertManager.getConvertMap().get(EntityConvertManager.getPrefixMap().get(clazz)).apply(data.toArray(new String[data.size()]));
    }

    public <T extends BusinessEntity<User>> T constructUserFull(List<String> data, Class<?> clazz)
    {
        return hospitalEntityAllocator.convertToBusinessEntity(constructUser(data,clazz),false);
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
            return failure(e);
        }
    }

    public<T extends BusinessEntity<?>> CRUDInformation updateUser(T user)
    {
        try
        {
            hospitalEntityAllocator.saveChanges(user);
            return new CRUDInformation(true,"Success");
        } catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public List<Doctor> getAllDoctors()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(DoctorToFile.PREFIX);
    }

    public List<MedicalManager> getAllMedicalManagers()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(MedicalManagerToFile.PREFIX);
    }

    public CRUDInformation allocateDoctorToMedicalManager(Doctor doctor,MedicalManager medicalManager)
    {
        try
        {
            doctor.setBelongsToMedicalManager(medicalManager.getSelf());
            hospitalEntityAllocator.saveChanges(doctor);
            return (new CRUDInformation(true, "Success to allocate doctor to medical manager"));
        } catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public CRUDInformation unallocatedDoctorToMedicalManager(Doctor doctor,MedicalManager medicalManager)
    {
        try
        {
            if (!doctor.getBelongsToMedicalManager().getId().equals(medicalManager.getId()))
            {
                return new CRUDInformation(false, "Manager is not the original doctor's manager");
            }
            doctor.setBelongsToMedicalManager(null);
            hospitalEntityAllocator.saveChanges(doctor);
            return (new CRUDInformation(true, "Success to unallocated doctor's medical manager"));
        }  catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public List<Facility> getAllFacilities()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(FacilityToFile.PREFIX);
    }

    public CRUDInformation addFacility(Facility facility)
    {
        try
        {
            hospitalEntityAllocator.addEntityForceNewId(facility.getSelf());
            return new CRUDInformation(true, "Success");
        }
        catch (RuntimeException e)
        {
            return failure(e);
        }
    }



}
