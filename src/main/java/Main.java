import Forms.BaseFrame;
import Forms.LoginForm.LoginForm;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;

import javax.swing.*;


void main() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
{

    try
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
        {
            ex.printStackTrace();
        }
    }
    BaseEntity.setIdNumberWidth(4);
    Path linkerPath = Path.of("data", "Linker");
    Path entityPath = Path.of("data", "Entity");
    HospitalEntityAllocator hea = new HospitalEntityAllocator(linkerPath,entityPath);
    BaseFrame baseFrame = new LoginForm(hea);
    baseFrame.setVisible(true);

}