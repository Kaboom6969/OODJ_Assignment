package entities.BaseEntity;

import Interfaces.ConvertToFileData;

import java.util.Objects;

public class AssessmentTypeToFile extends BaseEntity implements ConvertToFileData
{
    public static final String PREFIX = "AT";

    public enum AssessmentCategory
    {
        GENERAL_CHECKUP,
        LAB_TEST,
        X_RAY,
        MEDICAL_IMAGING
    }

    private String name;
    private AssessmentCategory category;
    private int price;

    public String getName()
    {
        return name;
    }

    public AssessmentCategory getCategory()
    {
        return category;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        if (price < 0) throw new IllegalArgumentException("Price should be positive");
        this.price = price;
    }

    public AssessmentTypeToFile(String id, String name, AssessmentCategory category, int price)
    {
        super(id);
        this.name = name;
        this.category = category;
        setPrice(price);
    }

    public AssessmentTypeToFile(String[] data)
    {
        this
                (
                        data[0],
                        data[1],
                        AssessmentCategory.valueOf(data[2].toUpperCase().trim()),
                        Integer.parseInt(data[3])
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
                        this.getName() + "|" +
                        this.getCategory() + "|" +
                        this.getPrice();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!super.equals(o)) return false;
        AssessmentTypeToFile assessment = (AssessmentTypeToFile) o;
        if (!Objects.equals(name, assessment.name)) return false;
        if (category != assessment.category) return false;
        return price == assessment.price;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), name, category, price);
    }
}