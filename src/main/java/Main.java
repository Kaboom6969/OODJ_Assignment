import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import entities.BaseEntity.BaseEntity;

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
    }
}