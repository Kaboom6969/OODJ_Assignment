import Exceptions.EntityNotFoundException;
import Exceptions.EntityNotMatchException;
import Exceptions.EntityRepeatedException;
import Tools.EntityHandler;
import Tools.FileDataHandler;
import entities.BaseEntity;
import entities.Patient;
import entities.UserWithDetails;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或

// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
void main() throws EntityRepeatedException, EntityNotFoundException, EntityNotMatchException
{
    BaseEntity.setIdNumberWidth(4);
    FileDataHandler myHandlder = new FileDataHandler("C:/Users/leezh/IdeaProjects/OODJ Assignment/data/Patient.txt");
    EntityHandler eh = new EntityHandler(myHandlder);
    Patient myPatient = (Patient) eh.getEntity(0);
    IO.println(myPatient);
    Patient myPatient2 = new Patient("PT0002", "Kek", "YYYY", UserWithDetails.Gender.MALE, "2004/09/05");
    //eh.updateEntity(myPatient2);
    eh.deleteEntity(myPatient2, EntityHandler.MatchLogic.EXACT_DATA);
}
