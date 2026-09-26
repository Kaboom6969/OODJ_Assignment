package Forms.DoctorForm;

import Forms.BaseFrame;
import Tools.HospitalEntityAllocator;
import entities.BusinessEntity.Doctor;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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