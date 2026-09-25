package Operations.AdminOperation;

import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Interfaces.ConvertToFileData;
import Interfaces.Linkable;
import Interfaces.OwnerShip;
import Tools.EntityConvertManager;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.*;
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
    public CRUDInformation delete(BusinessEntity<?> businessEntity)
    {
        try
        {
            hospitalEntityAllocator.deleteBusinessEntity(businessEntity);
            return new CRUDInformation(true,"Success");
        } catch (EntityNotMatchException | EntityNotFoundException e)
        {
            return failure(e);
        }
    }

    public <T extends BaseEntity & ConvertToFileData> CRUDInformation add(List<String> data,Class<?> clazz)
    {
        try
        {
            T object = construct(data, clazz);
            hospitalEntityAllocator.addEntityForceNewId(object);
            return new CRUDInformation(true,"Success");
        } catch (RuntimeException e)
        {
            return failure(e);
        }
    }


    public <T extends BaseEntity & ConvertToFileData> T construct(List<String> data,Class<?> clazz)
    {
        String prefix = null;
        return (T) EntityConvertManager.getConvertMap().get(EntityConvertManager.getPrefixMap().get(clazz)).apply(data.toArray(new String[data.size()]));
    }

    public <Q extends BaseEntity&ConvertToFileData ,T extends BusinessEntity<Q>> T constructFull(List<String> data, Class<? extends BaseEntity> clazz)
    {
        try
        {
            return hospitalEntityAllocator.convertToBusinessEntity(construct(data,clazz),false);
        } catch (Exception e)
        {
            throw new RuntimeException(failure(e).message());
        }

    }
    private CRUDInformation add(BusinessEntity<?> businessEntity)
    {
        try
        {
            hospitalEntityAllocator.addEntityForceNewId(businessEntity.getSelf());
            return new CRUDInformation(true,"Success");
        }  catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public CRUDInformation update(BusinessEntity<?> businessEntity)
    {
        try
        {
            hospitalEntityAllocator.saveChanges(businessEntity);
            return new CRUDInformation(true,"Success");
        } catch (RuntimeException e)
        {
            return failure(e);
        }
    }
    public List<Department> getAllDepartments()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(DepartmentToFile.PREFIX);
    }
    public List<ConsultationRate> getAllConsultationRates()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(ConsultationRateToFile.PREFIX);
    }

    public List<Doctor> getAllDoctors()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(DoctorToFile.PREFIX);
    }

    public List<MedicalManager> getAllMedicalManagers()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(MedicalManagerToFile.PREFIX);
    }
    public CRUDInformation allocateConsultationRateToDepartment(ConsultationRate consultationRate,Department department)
    {
        try
        {
            consultationRate.setBelongsToDepartment(department.getSelf());
            hospitalEntityAllocator.saveChanges(consultationRate);
            return (new CRUDInformation(true, "Success to allocate consultation rate to department"));
        } catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public CRUDInformation unallocatedConsultationRateToDepartment(ConsultationRate consultationRate,Department department)
    {
        try
        {
            if (!consultationRate.getBelongsToDepartment().getId().equals(department.getId()))
            {
                return new CRUDInformation(false, "Department is not the original consultation rate's department");
            }
            consultationRate.setBelongsToDepartment(null);
            hospitalEntityAllocator.saveChanges(consultationRate);
            return (new CRUDInformation(true, "Success to unallocated consultation rate to department"));
        }  catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public CRUDInformation allocateFacilityToDepartment(Facility facility,Department department)
    {
        try
        {
            facility.setBelongsToDepartment(department.getSelf());
            hospitalEntityAllocator.saveChanges(facility);
            return (new CRUDInformation(true, "Success to allocate facility to department"));
        }
        catch (RuntimeException e)
        {
            return failure(e);
        }
    }

    public CRUDInformation unallocatedFacilityToDepartment(Facility facility,Department department)
    {
        try
        {
            if (!facility.getBelongsToDepartment().getId().equals(department.getId()))
            {
                return new CRUDInformation(false, "Department is not the original facility's department");
            }
            facility.setBelongsToDepartment(null);
            hospitalEntityAllocator.saveChanges(facility);
            return (new CRUDInformation(true, "Success to unallocated facility to department"));
        }  catch (RuntimeException e)
        {
            return failure(e);
        }
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

    public List<Insurance> getAllInsurances()
    {
        return hospitalEntityAllocator.getAllBusinessEntities(InsuranceToFile.PREFIX);
    }




}
