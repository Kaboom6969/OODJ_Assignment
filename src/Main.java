import Exceptions.EntityNotFoundException;
import Exceptions.EntityNotMatchException;
import Exceptions.EntityRepeatedException;
import Tools.EntityConvertManager;
import Tools.EntityHandler;
import Tools.FileDataHandler;
import entities.BaseEntity;
import entities.Doctor;
import entities.Patient;
import entities.UserWithDetails;
import Iterator.LazyEntityList;

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
    FileDataHandler DoctorFile = new FileDataHandler("C:/Users/leezh/IdeaProjects/OODJ Assignment/data/Doctor.txt");
    List<String> AllDoctorsId = new ArrayList<>();
    List<Doctor> allDoctors = new EntityHandler(DoctorFile).getAllEntities();
    for(Doctor doctor : allDoctors)
    {
        AllDoctorsId.add(doctor.getId());
    }
    allDoctors = null;
    LazyEntityList<Doctor> lazyDoctorList = new LazyEntityList<Doctor>(AllDoctorsId,DoctorFile);
    for(Doctor doctor : lazyDoctorList)
    {
        System.out.println(doctor.toFileData());
    }
}
