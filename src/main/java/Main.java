import Forms.LoginForm.LoginForm;
import Tools.HospitalEntityAllocator;
import com.formdev.flatlaf.FlatLightLaf;
import entities.BaseEntity.BaseEntity;
import javax.swing.*;


void main()
{
    System.setProperty("flatlaf.useNativeLibrary", "false");
    FlatLightLaf.setup();
    BaseEntity.setIdNumberWidth(4);
    Path linkerPath = Path.of("data", "Linker");
    Path entityPath = Path.of("data", "Entity");
    HospitalEntityAllocator hea = new HospitalEntityAllocator(linkerPath,entityPath);
    LoginForm loginForm = new LoginForm(hea);
    loginForm.setVisible(true);
}