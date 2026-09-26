import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Forms.AdminForm.AdminForm;
import Forms.LoginForm.LoginForm;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;

import java.nio.file.Path;



void main()
{
    BaseEntity.setIdNumberWidth(4);
    Path linkerPath = Path.of("data", "Linker");
    Path entityPath = Path.of("data", "Entity");
    HospitalEntityAllocator hea = new HospitalEntityAllocator(linkerPath,entityPath);
    LoginForm loginForm = new LoginForm(hea);
    loginForm.setVisible(true);
}