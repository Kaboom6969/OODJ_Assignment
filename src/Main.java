import Tools.FileDataHandler;
import entities.Patient;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
void main() throws FileNotFoundException {
    FileDataHandler myHanlder = new FileDataHandler("C:/Users/leezh/IdeaProjects/OODJ Assignment/data/Patient.txt");
    Patient myPatient = myHanlder.getEntity(2,Patient::new);
    IO.println(myPatient);
}
