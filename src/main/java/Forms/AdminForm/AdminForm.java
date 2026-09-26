/*
 * Created by JFormDesigner on Sat Sep 19 00:36:11 GMT+08:00 2026
 */

package Forms.AdminForm;

import Forms.AdminForm.CRUDPanel.*;import Forms.AdminForm.LinkPanel.AllocateConsultationRateAndFacilityToDepartment;import Forms.AdminForm.LinkPanel.AllocateDoctorToMedicalManagerPanel;import Interfaces.RefreshablePanel;import Operations.AdminOperation.AdminOperation;
import Tools.EntityConvertManager;
import Tools.HospitalEntityAllocator;
import Tools.PrefixHandler.PrefixFinder;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Admin;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;
import java.util.List;

/**
 * @author leezh
 */
public class AdminForm extends JFrame {
    private AdminOperation adminOperation;



    public AdminForm(Admin admin, HospitalEntityAllocator hospitalEntityAllocator)
    {
        adminOperation = new AdminOperation(hospitalEntityAllocator,admin);
        initComponents();
        adminTab.add(new UserPanel(this,adminOperation),"User Panel");
        adminTab.add(new FacilityPanel(this,adminOperation),"Facility Panel");
        adminTab.add(new InsurancePanel(this,adminOperation),"Insurance Panel");
        adminTab.add(new ConsultationRatePanel(this,adminOperation), "Consultation Panel");
        adminTab.add(new AssessmentTypePanel(this,adminOperation),"Assessment Type Panel");
        adminTab.add(new MedicalRequestPanel(this,adminOperation),"Medical Request Panel");
        adminTab.add(new AllocateDoctorToMedicalManagerPanel(adminOperation),"Allocate Doctor To Medical Manager Panel");
        adminTab.add(new AllocateConsultationRateAndFacilityToDepartment(adminOperation),"Allocate Consultation Rate And Facility To Department Panel");
        adminTab.addChangeListener(e ->
        {
            if (adminTab.getSelectedComponent() instanceof RefreshablePanel rp)
            {
                rp.refreshData();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        adminTab = new JTabbedPane();

        //======== this ========
        var contentPane = getContentPane();

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(adminTab, GroupLayout.PREFERRED_SIZE, 890, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(adminTab, GroupLayout.PREFERRED_SIZE, 507, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JTabbedPane adminTab;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on



}

