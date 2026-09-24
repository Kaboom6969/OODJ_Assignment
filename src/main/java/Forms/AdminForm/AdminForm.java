/*
 * Created by JFormDesigner on Sat Sep 19 00:36:11 GMT+08:00 2026
 */

package Forms.AdminForm;

import java.awt.*;
import java.awt.event.*;

import Forms.AdminForm.CRUDDialog.UserDialog;
import Forms.AdminForm.CRUDPanel.ConsultationRatePanel;import Forms.AdminForm.CRUDPanel.FacilityPanel;
import Forms.AdminForm.CRUDPanel.InsurancePanel;
import Forms.AdminForm.CRUDPanel.UserPanel;
import Forms.AdminForm.LinkPanel.AllocateDoctorToMedicalManagerPanel;import Operations.AdminOperation.AdminOperation;
import Tools.HospitalEntityAllocator;
import entities.BaseEntity.Users.User;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Admin;
import entities.BusinessEntity.BusinessEntity;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.table.*;

import static Forms.AdminForm.FrameHelper.getObjectFromCurrentSelectedRow;

/**
 * @author leezh
 */
public class AdminForm extends JFrame {
    private AdminOperation adminOperation;



    public AdminForm(Admin admin, HospitalEntityAllocator hospitalEntityAllocator)
    {
        adminOperation = new AdminOperation(hospitalEntityAllocator,admin);
        initComponents();
        UserPanel userPanel = new UserPanel(this,adminOperation);
        AllocateDoctorToMedicalManagerPanel allocateDoctorToMedicalManagerPanel = new AllocateDoctorToMedicalManagerPanel(adminOperation);
        InsurancePanel insurancePanel = new InsurancePanel(this, adminOperation);
        FacilityPanel facilityPanel = new FacilityPanel(this, adminOperation);
        ConsultationRatePanel consultationRatePanel = new ConsultationRatePanel(this, adminOperation);
        adminTab.add(userPanel,"User Panel");
        adminTab.add(insurancePanel,"Insurance Panel");
        adminTab.add(facilityPanel,"Facility Panel");
        adminTab.add(consultationRatePanel,"Consultation Rate Panel");
        adminTab.add(allocateDoctorToMedicalManagerPanel,"Allocate Doctor To Medical Manager");

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
