import Exceptions.EntityExceptions.EntityNotFoundException;
import Exceptions.EntityExceptions.EntityNotMatchException;
import Exceptions.EntityExceptions.EntityRepeatedException;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;
import entities.BusinessEntity.Department;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或

// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
void main() throws EntityRepeatedException, EntityNotFoundException, EntityNotMatchException
{
      BaseEntity.setIdNumberWidth(4);
//    FileDataHandler myHandlder = new FileDataHandler("C:/Users/leezh/IdeaProjects/OODJ Assignment/data/Patient.txt");
//    EntityHandler eh = new EntityHandler(myHandlder);
//    Patient myPatient = (Patient) eh.getEntity(0);
//    IO.println(myPatient);
//    Patient myPatient2 = new Patient("PT0002", "Kek", "YYYY", UserWithDetails.Gender.MALE, "2004/09/05");
//    //eh.updateEntity(myPatient2);
//    eh.deleteEntity(myPatient2, EntityHandler.MatchLogic.EXACT_DATA);

    //Test for LazyEntityList
    HospitalEntityAllocator hea = new HospitalEntityAllocator
            (Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Linker"),
                    Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Admin.txt"),
                    Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Patient.txt"),
                    Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Doctor.txt"),
                    Path.of("C:\\Users\\leezh\\IdeaProjects\\OODJ Assignment\\data\\Department.txt")
            );

    Department department = hea.getDepartment("DP0001");
}
