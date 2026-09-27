package Forms.DoctorForm;

import Forms.BaseFrame;
import Tools.HospitalEntityAllocator;
import entities.BusinessEntity.Doctor;

public class DoctorPanelTest extends BaseFrame
{
    public DoctorPanelTest(Doctor doctor, HospitalEntityAllocator hea)
    {
        setTitle("Doctor Panel");
        DoctorPanel doctorPanel = new DoctorPanel(hea, doctor);

        setContentPane(doctorPanel);

        doctorPanel.addLogoutListener(e -> 
        {

            goToParent();
        });
        pack();
        setLocationRelativeTo(null);
    }

}