import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Forms.LoginForm.LoginForm;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws EntityRepeatedException, EntityNotFoundException, EntityNotMatchException {
        // 设置全局 ID 数字宽度为 4 位
        BaseEntity.setIdNumberWidth(4);

        // 以下为你朋友保留的测试代码（如不需要跑测试可以先注释）
        /*
        HospitalEntityAllocator hea = new HospitalEntityAllocator(
                Path.of("data/Linker"),
                Path.of("data/Entity")
        );
        */
        Path linkerPath = Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Linker");
        Path entityPath = Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Entity");
        HospitalEntityAllocator hea = new HospitalEntityAllocator(linkerPath,entityPath);
        LoginForm loginForm = new LoginForm(hea);
        loginForm.setVisible(true);
    }
}