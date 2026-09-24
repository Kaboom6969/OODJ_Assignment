package Forms.DoctorForm;

import Tools.HospitalEntityAllocator;
import entities.BusinessEntity.Doctor;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DoctorPanelTest extends JFrame
{
    public DoctorPanelTest(Doctor doctor,HospitalEntityAllocator hea)
    {
        setTitle("Doctor Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(new DoctorPanel(hea,doctor));

        pack();
        setLocationRelativeTo(null);
    }

}