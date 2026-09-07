package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.time.LocalDateTime;
import java.util.Objects;

public class BillToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "BL";

    public enum BillStatus
    {
        UNPAID,
        PAID
    }

    private int consultationFee;
    private int assessmentFee;
    private int insuranceDeduction;
    private LocalDateTime issuedAt;
    private BillStatus status;
    private LocalDateTime paidAt;

    public int getConsultationFee()
    {
        return consultationFee;
    }

    public int getAssessmentFee()
    {
        return assessmentFee;
    }

    public int getInsuranceDeduction()
    {
        return insuranceDeduction;
    }

    public int getMoney()
    {
        return Math.addExact(consultationFee, assessmentFee) - insuranceDeduction;
    }

    public LocalDateTime getIssuedAt()
    {
        return issuedAt;
    }

    public BillStatus getStatus()
    {
        return status;
    }

    public LocalDateTime getPaidAt()
    {
        return paidAt;
    }

    public void markPaid(LocalDateTime paidAt)
    {
        if (status != BillStatus.UNPAID)
            throw new IllegalStateException("Bill has already been paid");

        if (paidAt == null || paidAt.isBefore(issuedAt))
            throw new IllegalArgumentException
                    (
                            "Payment time cannot be null or before issued time"
                    );

        this.paidAt = paidAt;
        this.status = BillStatus.PAID;
    }

    public BillToFile(String id, int consultationFee, int assessmentFee,
                      int insuranceDeduction, LocalDateTime issuedAt)
    {
        this
                (
                        id,
                        consultationFee,
                        assessmentFee,
                        insuranceDeduction,
                        issuedAt,
                        BillStatus.UNPAID,
                        null
                );
    }

    public BillToFile(String id, int consultationFee, int assessmentFee,
                      int insuranceDeduction, LocalDateTime issuedAt,
                      BillStatus status, LocalDateTime paidAt)
    {
        super(id);

        if (consultationFee < 0 || assessmentFee < 0 || insuranceDeduction < 0)
            throw new IllegalArgumentException("Amounts cannot be negative");

        int total = Math.addExact(consultationFee, assessmentFee);

        if (insuranceDeduction > total)
            throw new IllegalArgumentException("Insurance deduction exceeds total fee");

        if (issuedAt == null || status == null)
            throw new IllegalArgumentException("Issued time and status cannot be null");

        if (status == BillStatus.PAID &&
                (paidAt == null || paidAt.isBefore(issuedAt)))
            throw new IllegalArgumentException("Paid bill requires a valid payment time");

        if (status == BillStatus.UNPAID && paidAt != null)
            throw new IllegalArgumentException("Unpaid bill cannot have a payment time");

        this.consultationFee = consultationFee;
        this.assessmentFee = assessmentFee;
        this.insuranceDeduction = insuranceDeduction;
        this.issuedAt = issuedAt;
        this.status = status;
        this.paidAt = paidAt;
    }

    public BillToFile(String[] data)
    {
        this
                (
                        data[0],
                        Integer.parseInt(data[1]),
                        Integer.parseInt(data[2]),
                        Integer.parseInt(data[3]),
                        LocalDateTime.parse(data[4]),
                        BillStatus.valueOf(data[5].toUpperCase().trim()),
                        data[6].isEmpty() ? null : LocalDateTime.parse(data[6])
                );
    }

    @Override
    public String getIdPrefix()
    {
        return PREFIX;
    }

    @Override
    public String toFileData()
    {
        return
                this.getId() + "|" +
                        this.getConsultationFee() + "|" +
                        this.getAssessmentFee() + "|" +
                        this.getInsuranceDeduction() + "|" +
                        this.getIssuedAt() + "|" +
                        this.getStatus() + "|" +
                        (this.getPaidAt() == null ? "" : this.getPaidAt());
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        BillToFile bill = (BillToFile) o;
        if (consultationFee != bill.consultationFee) return false;
        if (assessmentFee != bill.assessmentFee) return false;
        if (insuranceDeduction != bill.insuranceDeduction) return false;
        if (!Objects.equals(issuedAt, bill.issuedAt)) return false;
        if (status != bill.status) return false;
        return Objects.equals(paidAt, bill.paidAt);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), consultationFee, assessmentFee,
                insuranceDeduction, issuedAt, status, paidAt);
    }
}