package Operations;

import Tools.HospitalEntityAllocator;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.ConsultationRateToFile;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.MedicalRequestToFile;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Bill;
import entities.BusinessEntity.ConsultationRate;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.MedicalRequest;
import entities.BusinessEntity.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OtherOperation
{
    private final HospitalEntityAllocator allocator;

    public OtherOperation(HospitalEntityAllocator allocator)
    {
        this.allocator = allocator;
    }
    public Bill generateBill(String appointmentId)
    {
        if (appointmentId == null || appointmentId.isBlank())
        {
            throw new IllegalArgumentException("Appointment must be selected.");
        }
        Appointment appointment = allocator.getBusinessEntity(appointmentId);
        if (appointment.getDoctor() == null)
        {
            throw new IllegalArgumentException("Appointment does not belong to this doctor.");
        }
        if (appointment.getSelf().getStatus() != AppointmentToFile.AppointmentStatus.COMPLETED)
        {
            throw new IllegalStateException("Appointment must be completed before generating a bill.");
        }
        if (appointment.getMedicalRecord() == null)
        {
            throw new IllegalStateException("Medical record has not been created.");
        }
        MedicalRecord medicalRecord = allocator.getBusinessEntity(appointment.getMedicalRecord().getId());
        if (medicalRecord.getBill() != null)
        {
            throw new IllegalStateException("A bill has already been generated for this medical record.");
        }
        if (appointment.getFacility() == null)
        {
            throw new IllegalStateException("Appointment facility is unavailable.");
        }
        Facility facility = allocator.getBusinessEntity(appointment.getFacility().getId());
        DepartmentToFile departmentData = facility.getBelongsToDepartment();
        if (departmentData == null)
        {
            throw new IllegalStateException("Appointment facility has no department.");
        }
        Department department = allocator.getBusinessEntity(departmentData.getId());
        List<ConsultationRateToFile> activeRates = new ArrayList<>();
        for (ConsultationRateToFile rate : department.getConsultations())
        {
            if (rate.isActive())
            {
                activeRates.add(rate);
            }
        }
        if (activeRates.isEmpty())
        {
            throw new IllegalStateException("The facility department has no active consultation rate.");
        }
        if (activeRates.size() > 1)
        {
            throw new IllegalStateException(
                    "The facility department has more than one active " +
                            "consultation rate. Please ask an administrator " +
                            "to correct the configuration."
            );
        }
        ConsultationRate consultationRate = allocator.getBusinessEntity(activeRates.get(0).getId());
        int consultationFee = consultationRate.getSelf().getPrice();
        int assessmentFee = 0;
        for (MedicalRequestToFile requestData : medicalRecord.getMedicalRequests())
        {
            switch (requestData.getStatus())
            {
                case COMPLETED ->
                {
                    MedicalRequest request = allocator.getBusinessEntity(requestData.getId());
                    assessmentFee = Math.addExact(assessmentFee, request.getAssessmentType().getPrice());
                }
                case REJECTED ->
                {
                }
                default -> throw new IllegalStateException(
                        "All medical requests must be completed or " +
                                "rejected before generating the bill."
                );
            }
        }
        if (appointment.getPatient() == null)
        {
            throw new IllegalStateException("Appointment patient is unavailable.");
        }
        Patient patient = allocator.getBusinessEntity(appointment.getPatient().getId());
        InsuranceToFile insurance = patient.getInsurance();
        int subtotal = Math.addExact(consultationFee, assessmentFee);
        int insuranceDeduction = 0;
        if (insurance != null && insurance.isAccepted())
        {
            long deduction = (long) subtotal * insurance.getCoveragePercentage() / 100;
            insuranceDeduction = Math.toIntExact(deduction);
        }
        BillToFile billData = new BillToFile(
                null,
                consultationFee,
                assessmentFee,
                insuranceDeduction,
                LocalDateTime.now()
        );
        allocator.assignNewId(billData);
        Bill bill = allocator.convertToBusinessEntity(billData, true);
        bill.setMedicalRecord(medicalRecord.getSelf());
        bill.setConsultationRate(consultationRate.getSelf());
        if (insurance != null && insurance.isAccepted())
        {
            bill.setInsurance(insurance);
        }
        allocator.saveChanges(bill);
        return bill;
    }
}
