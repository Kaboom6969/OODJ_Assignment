package Forms;

public class ComboBoxItem<T>
{
    private final T item;
    private final String itemName;

    public ComboBoxItem(T item, String itemName)
    {
        this.item = item;
        this.itemName = itemName;
    }

    @Override
    public String toString()
    {
        return itemName;
    }

    public T getItem()
    {
        return item;
    }
}
