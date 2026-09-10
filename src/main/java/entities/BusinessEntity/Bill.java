package entities.BusinessEntity;

import Exceptions.LinkerExceptions.LinkerRequireOneOnlyException;
import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.*;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;
import java.util.List;

public class Bill extends BusinessEntity<BillToFile> implements OwnEntity, Linkable
{
    private LazyEntity<MedicalRecordToFile> medicalRecord;
    private LazyEntity<InsuranceToFile> insurance;
    private LazyEntity<ConsultationRateToFile> consultationRate;

    public MedicalRecordToFile getMedicalRecord()
    {
        return medicalRecord.getSelf();
    }

    public void setMedicalRecord(MedicalRecordToFile medicalRecordToFile)
    {
        medicalRecord.changeSelf(medicalRecordToFile);
    }

    public InsuranceToFile getInsurance()
    {
        return insurance.getSelf();
    }

    public void setInsurance(InsuranceToFile insuranceToFile)
    {
        insurance.changeSelf(insuranceToFile);
    }

    public ConsultationRateToFile getConsultationRate()
    {
        return consultationRate.getSelf();
    }

    public void setConsultationRate(ConsultationRateToFile consultationRateToFile)
    {
        consultationRate.changeSelf(consultationRateToFile);
    }

    public Bill(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap,boolean isJustConstruct)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null,isJustConstruct);
    }

    public Bill(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, BillToFile self,boolean isJustConstruct)
    {
        super(selfId, selfFile, self);
        boolean requireOne = !isJustConstruct;
        medicalRecord = new LazyEntity<MedicalRecordToFile>
        (
            linkerManagerHashMap.get(MedicalRecordToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(MedicalRecordToFile.PREFIX))
        );
        insurance = new LazyEntity<InsuranceToFile>
        (
            linkerManagerHashMap.get(InsuranceToFile.PREFIX).findBasedOnKeyOneResult(selfId, false),
            new EntityHandler(fileDataHandlerHashMap.get(InsuranceToFile.PREFIX))
        );
        consultationRate = new LazyEntity<ConsultationRateToFile>
        (
            linkerManagerHashMap.get(ConsultationRateToFile.PREFIX).findBasedOnKeyOneResult(selfId, requireOne),
            new EntityHandler(fileDataHandlerHashMap.get(ConsultationRateToFile.PREFIX))
        );
    }
    @Override
    public List<LinkerManager> getLinkerManager()
    {
        List<LinkerManager> linkerManagers = getLinkerManagerWithoutValidate();
        for (LinkerManager linkerManager : linkerManagers)
        {
            if
            (
                linkerManager.includeClass(MedicalRecordToFile.class) ||
                linkerManager.includeClass(ConsultationRateToFile.class)
            )
            {
                if (!linkerManager.isThisRequireOne()) throw new LinkerRequireOneOnlyException();
            }
        }
        return linkerManagers;
    }
}
