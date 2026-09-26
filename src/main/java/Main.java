import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Forms.AdminForm.AdminForm;
import Forms.LoginForm.LoginForm;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;

import javax.swing.*;
import java.nio.file.Path;



void main() throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
{
    try
    {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
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
    LoginForm loginForm = new LoginForm(hea);
    loginForm.setVisible(true);

}