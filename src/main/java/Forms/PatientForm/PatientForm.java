/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Forms.PatientForm;

import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.AppointmentToFile.AppointmentStatus;
import entities.BaseEntity.PrescriptionToFile;
import entities.BaseEntity.Users.PatientToFile;
import entities.BaseEntity.Users.UserWithDetails;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.Patient;
import Operations.PatientOperation.PatientOperation;
import Operations.PatientOperation.PatientOperation.DoctorAvailability;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.DepartmentToFile;
import entities.BaseEntity.DoctorShiftToFile;
import entities.BaseEntity.FacilityToFile;
import entities.BaseEntity.InsuranceToFile;
import entities.BaseEntity.MedicalRecordToFile;
import entities.BaseEntity.Users.DoctorToFile;
import entities.BaseEntity.Users.MedicalManagerToFile;
import entities.BusinessEntity.MedicalManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jingxian
 */
public class PatientForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PatientForm.class.getName());
    private HospitalEntityAllocator allocator;
    private Patient patient;
    private PatientOperation operation;
    private List<Department> departments;
    private List<DoctorAvailability> currentDoctorOptions;
    private List<LocalDateTime> currentSlotOptions;
    private List<Appointment> currentAppointments;
    private List<MedicalRecord> currentMedicalHistory;
    private boolean editingProfile = false;

    /**
     * Creates new form PatientForm
     */
    public PatientForm() {
        initComponents();
        logOutBtn.addActionListener(evt -> {
            // TODO: Navigate to the login screen once it is merged.
            dispose();
        });
    }

    public PatientForm(HospitalEntityAllocator allocator, Patient patient) {
        this();
        this.allocator = allocator;
        this.patient = patient;
        this.operation = new PatientOperation(allocator, patient);
        populateDashboard();
        populateBookingTab();
        populateAppointmentsTable();
        populateMedicalHistoryTable();
        populateProfileTab();
        
        jComboBox2.addActionListener(evt -> refreshDoctorList());
        jSpinner1.addChangeListener(evt -> refreshDoctorList());
        jList2.addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                refreshSlotList();
            }
        });
        appointmentTbl.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                updateAppointmentActionButtons();
            }
        });
        medicalTbl.getSelectionModel().addListSelectionListener(evt -> {
            if (!evt.getValueIsAdjusting()) {
                refreshPrescriptionsTable();
            }
        });
        updateAppointmentActionButtons();
    }

    private void populateDashboard() {
        jLabel1.setText("Hi " + patient.getSelf().getName());

        Appointment appointment = operation.loadNextUpcomingAppointment();
        if (appointment == null) {
            jLabel3.setText("No upcoming appointments");
            return;
        }

        Doctor doctor = allocator.getBusinessEntity(appointment.getDoctor().getId());
        String formattedTime = appointment.getSelf().getAppointmentTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        jLabel3.setText( doctor.getSelf().getName() + " - " + formattedTime);
    }

    private void populateBookingTab() {
        departments = operation.loadAllDepartments();
        jComboBox2.removeAllItems();
        for (Department department : departments) {
            jComboBox2.addItem(department.getSelf().getName());
        }

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(jSpinner1, "yyyy-MM-dd");
        jSpinner1.setEditor(dateEditor);
        jSpinner1.setValue(new java.util.Date()); // 默认显示今天
        refreshDoctorList();
    }

    private void refreshDoctorList() {
        System.out.println("refreshDoctorList called, date=" + jSpinner1.getValue());
        int departmentIndex = jComboBox2.getSelectedIndex();
        if (departmentIndex < 0 || departmentIndex >= departments.size()) {
            jList2.setModel(new DefaultListModel<>());
            jList1.setModel(new DefaultListModel<>());
            ratingLbl.setText("");
            return;
        }

        Date selectedDate = (Date) jSpinner1.getValue();
        LocalDate date = selectedDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        Department department = departments.get(departmentIndex);
        currentDoctorOptions = operation.loadDoctorAvailability(department, date);

        DefaultListModel<String> doctorModel = new DefaultListModel<>();
        for (DoctorAvailability option : currentDoctorOptions) {
            doctorModel.addElement(option.doctor().getSelf().getName()
                    + " (" + option.availableSlots().size() + " slots)");
        }
        jList2.setModel(doctorModel);
        jList1.setModel(new DefaultListModel<>());
        ratingLbl.setText("");
    }

    private void refreshSlotList() {
        int doctorIndex = jList2.getSelectedIndex();
        if (doctorIndex < 0 || currentDoctorOptions == null
                || doctorIndex >= currentDoctorOptions.size()) {
            jList1.setModel(new DefaultListModel<>());
            ratingLbl.setText("");
            return;
        }

        DoctorAvailability doctorOption = currentDoctorOptions.get(doctorIndex);
        Doctor doctor = doctorOption.doctor();
        ratingLbl.setText(String.format("Rating: %.1f",
                operation.loadDoctorAverageRating(doctor)));
        currentSlotOptions = doctorOption.availableSlots();

        DateTimeFormatter slotFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM — HH:mm");
        DefaultListModel<String> slotModel = new DefaultListModel<>();
        for (LocalDateTime slot : currentSlotOptions) {
            slotModel.addElement(slot.format(slotFormatter));
        }
        jList1.setModel(slotModel);
    }

    private void populateAppointmentsTable() {
        currentAppointments = operation.loadMyAppointments();
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] {"Doctor", "Date & Time", "Facility", "Status", "My Rating"},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter appointmentFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Appointment appointment : currentAppointments) {
            Doctor doctor = allocator.getBusinessEntity(appointment.getDoctor().getId());
            Facility facility = allocator.getBusinessEntity(appointment.getFacility().getId());
            String rating = appointment.getFeedback() == null
                    ? "" : String.valueOf(appointment.getFeedback().getRating());
            tableModel.addRow(new Object[] {
                doctor.getSelf().getName(),
                appointment.getSelf().getAppointmentTime().format(appointmentFormatter),
                facility.getSelf().getName(),
                appointment.getSelf().getStatus().toString(),
                rating
            });
        }
        appointmentTbl.setModel(tableModel);
        updateAppointmentActionButtons();
    }

    private void updateAppointmentActionButtons() {
        int selectedRow = appointmentTbl.getSelectedRow();
        if (selectedRow < 0 || currentAppointments == null
                || selectedRow >= currentAppointments.size()) {
            RescheduleBtn.setEnabled(false);
            CancelBtn.setEnabled(false);
            feedbackBtn.setEnabled(false);
            return;
        }

        Appointment appointment = currentAppointments.get(selectedRow);
        AppointmentStatus status = appointment.getSelf().getStatus();
        boolean active = status == AppointmentStatus.BOOKED
                || status == AppointmentStatus.RESCHEDULED;
        boolean canRate = status == AppointmentStatus.COMPLETED
                && appointment.getFeedback() == null;
        RescheduleBtn.setEnabled(active);
        CancelBtn.setEnabled(active);
        feedbackBtn.setEnabled(canRate);
    }

    private Appointment getSelectedAppointment() {
        int selectedRow = appointmentTbl.getSelectedRow();
        if (selectedRow < 0 || currentAppointments == null
                || selectedRow >= currentAppointments.size()) {
            return null;
        }
            return currentAppointments.get(selectedRow);
    }

    private void refreshAllTabs() {
        patient = allocator.getBusinessEntity(patient.getId());
        operation = new PatientOperation(allocator, patient);
        populateDashboard();
        refreshDoctorList();
        populateAppointmentsTable();
    }

    private void populateProfileTab() {
        usernameTf.setText(patient.getSelf().getName());
        emailTf.setText(patient.getSelf().getEmail());
        emailTf1.setText(patient.getSelf().getPhoneNumber());
        usernameTf.setEditable(false);
        emailTf.setEditable(false);
        emailTf1.setEditable(false);

        InsuranceToFile insurance = operation.loadInsuranceStatus();
        StringBuilder profileText = new StringBuilder();
        if (insurance == null) {
            profileText.append("No insurance on file.");
        } else {
            profileText.append("Company: ").append(insurance.getCompanyName()).append('\n')
                    .append("Coverage: ").append(insurance.getCoveragePercentage()).append("%\n")
                    .append("Accepted: ").append(insurance.isAccepted());
        }

        profileText.append("\n\nBilling:");
        boolean hasBillingRecords = false;
        if (currentMedicalHistory != null) {
            for (MedicalRecord record : currentMedicalHistory) {
                BillToFile bill = record.getBill();
                if (bill == null) {
                    continue;
                }
                hasBillingRecords = true;
                profileText.append("\nTotal: ").append(bill.getMoney())
                        .append(", Status: ").append(bill.getStatus());
            }
        }
        if (!hasBillingRecords) {
            profileText.append("\nNo billing records yet.");
        }
        insuranceTextArea.setText(profileText.toString());
        insuranceTextArea.setEditable(false);
    }

    private void populateMedicalHistoryTable() {
        currentMedicalHistory = operation.loadMedicalHistory();
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] {"Date", "Doctor", "Diagnosis", "Vitals"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter recordFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (MedicalRecord record : currentMedicalHistory) {
            Appointment appointment = allocator.getBusinessEntity(
                    record.getAppointment().getId());
            var recordData = record.getSelf();
            String vitals = "Temp " + recordData.getTemperature() + "°C, HR "
                    + recordData.getHeartRate() + "bpm, BP "
                    + recordData.getSystolicPressure() + "/"
                    + recordData.getDiastolicPressure();
            tableModel.addRow(new Object[] {
                record.getAppointment().getAppointmentTime().format(recordFormatter),
                appointment.getDoctor().getName(),
                recordData.getDiagnosis(),
                vitals
            });
        }
        medicalTbl.setModel(tableModel);
        refreshPrescriptionsTable();
    }

    private void refreshPrescriptionsTable() {
        int selectedRow = medicalTbl.getSelectedRow();
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] {"Medication", "Dosage", "Frequency", "Duration & Instructions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (selectedRow < 0 || currentMedicalHistory == null
                || currentMedicalHistory.isEmpty()
                || selectedRow >= currentMedicalHistory.size()) {
            PrescriptionsTbl.setModel(tableModel);
            return;
        }

        MedicalRecord record = currentMedicalHistory.get(selectedRow);
        List<PrescriptionToFile> prescriptions = operation.loadPrescriptions(record);
        for (PrescriptionToFile prescription : prescriptions) {
            tableModel.addRow(new Object[] {
                prescription.getMedicationName(),
                prescription.getDosage(),
                prescription.getFrequency(),
                prescription.getDurationDays() + " days — " + prescription.getInstructions()
            });
        }
        PrescriptionsTbl.setModel(tableModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        logOutBtn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        allAppBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        confirmBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ratingLbl = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        appointmentTbl = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        RescheduleBtn = new javax.swing.JButton();
        CancelBtn = new javax.swing.JButton();
        feedbackBtn = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        PrescriptionsTbl = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        medicalTbl = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        usernameTf = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        emailTf = new javax.swing.JTextField();
        editProfileBtn = new javax.swing.JButton();
        resetPwBtn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        emailTf1 = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        insuranceTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel1.setText("Hi XXX");
        jLabel1.setToolTipText("");

        logOutBtn.setBackground(new java.awt.Color(255, 204, 204));
        logOutBtn.setFont(new java.awt.Font("Tw Cen MT", 0, 12)); // NOI18N
        logOutBtn.setText("Log Out");

        jLabel2.setFont(new java.awt.Font("Tw Cen MT", 0, 12)); // NOI18N
        jLabel2.setText("Your Next Appoinment:");

        jLabel3.setFont(new java.awt.Font("YouYuan", 0, 24)); // NOI18N
        jLabel3.setText("No");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(232, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        allAppBtn.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        allAppBtn.setText("See all Appoinment");
        allAppBtn.addActionListener(this::allAppBtnActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 186, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(logOutBtn)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(114, 114, 114)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(186, 186, 186))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(allAppBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(294, 294, 294))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(logOutBtn)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(allAppBtn)
                .addContainerGap(203, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dashboard", jPanel1);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        confirmBtn.setText("Confirm Booking");
        confirmBtn.addActionListener(this::confirmBtnActionPerformed);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel4.setText("Booking Appointment");
        jLabel4.setToolTipText("");

        jLabel5.setBackground(new java.awt.Color(153, 153, 153));
        jLabel5.setFont(new java.awt.Font("Sylfaen", 0, 14)); // NOI18N
        jLabel5.setText("Date:");

        jLabel6.setBackground(new java.awt.Color(153, 153, 153));
        jLabel6.setFont(new java.awt.Font("Sylfaen", 0, 14)); // NOI18N
        jLabel6.setText("Time slot:");

        jLabel7.setBackground(new java.awt.Color(153, 153, 153));
        jLabel7.setFont(new java.awt.Font("Sylfaen", 0, 14)); // NOI18N
        jLabel7.setText("Dortor:");

        jLabel8.setBackground(new java.awt.Color(153, 153, 153));
        jLabel8.setFont(new java.awt.Font("Sylfaen", 0, 14)); // NOI18N
        jLabel8.setText("Department:");

        ratingLbl.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        ratingLbl.setText("Rating");

        jSpinner1.setModel(new javax.swing.SpinnerDateModel());

        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList2);

        jLabel16.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        jLabel16.setText("Rating:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(211, 211, 211))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(279, 279, 279)
                .addComponent(confirmBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ratingLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ratingLbl)
                            .addComponent(jLabel16))
                        .addGap(36, 36, 36)
                        .addComponent(confirmBtn))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(114, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Booking", jPanel2);

        appointmentTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(appointmentTbl);

        jLabel9.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel9.setText("Appointment Details");
        jLabel9.setToolTipText("");

        RescheduleBtn.setText("Reschedule");
        RescheduleBtn.addActionListener(this::RescheduleBtnActionPerformed);

        CancelBtn.setText("Cancel");
        CancelBtn.addActionListener(this::CancelBtnActionPerformed);

        feedbackBtn.setText("Feedback");
        feedbackBtn.addActionListener(this::feedbackBtnActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(215, 215, 215))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(RescheduleBtn)
                        .addGap(118, 118, 118)
                        .addComponent(CancelBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(feedbackBtn))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RescheduleBtn)
                    .addComponent(CancelBtn)
                    .addComponent(feedbackBtn))
                .addContainerGap(94, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Appointments", jPanel3);

        jLabel10.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel10.setText("Medical Records & Presriptions");
        jLabel10.setToolTipText("");

        PrescriptionsTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(PrescriptionsTbl);

        medicalTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(medicalTbl);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 8, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(122, 122, 122))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Records", jPanel4);

        jLabel12.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel12.setText("My Profile");
        jLabel12.setToolTipText("");

        usernameTf.setEditable(false);
        usernameTf.setText("name");

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel11.setText("username:");

        jLabel13.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel13.setText("email:");

        emailTf.setEditable(false);
        emailTf.setText("email");

        editProfileBtn.setText("Edit Profile");
        editProfileBtn.addActionListener(this::editProfileBtnActionPerformed);

        resetPwBtn.setText("Reset Password");
        resetPwBtn.addActionListener(this::resetPwBtnActionPerformed);

        jLabel14.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel14.setText("Insurance:");

        jLabel15.setFont(new java.awt.Font("Trebuchet MS", 0, 14)); // NOI18N
        jLabel15.setText("phone:");

        emailTf1.setEditable(false);
        emailTf1.setText("your phone");

        insuranceTextArea.setEditable(false);
        insuranceTextArea.setLineWrap(true);
        insuranceTextArea.setRows(5);
        insuranceTextArea.setWrapStyleWord(true);
        jScrollPane6.setViewportView(insuranceTextArea);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(283, 283, 283)
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(emailTf, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(usernameTf))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(emailTf1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(resetPwBtn, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(editProfileBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(132, 132, 132))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(usernameTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(emailTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetPwBtn))
                .addGap(25, 25, 25)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(emailTf1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(editProfileBtn)))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(jLabel14))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(142, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Profile", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void confirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnActionPerformed
        int doctorIndex = jList2.getSelectedIndex();
        int slotIndex = jList1.getSelectedIndex();
        if (doctorIndex < 0 || currentDoctorOptions == null
            || doctorIndex >= currentDoctorOptions.size()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please select a doctor.", "Booking Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (slotIndex < 0 || currentSlotOptions == null
            || slotIndex >= currentSlotOptions.size()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please select an appointment slot.", "Booking Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        Doctor doctor = currentDoctorOptions.get(doctorIndex).doctor();
        LocalDateTime slot = currentSlotOptions.get(slotIndex);
        Facility facility = allocator.<Facility>getAllBusinessEntities(
            entities.BaseEntity.FacilityToFile.PREFIX).get(0);
        String reason = javax.swing.JOptionPane.showInputDialog(this,
            "Reason for appointment:");
        try {
            operation.bookAppointment(doctor, facility, slot, reason);
            javax.swing.JOptionPane.showMessageDialog(this,
                "Appointment booked successfully.", "Booking Confirmed",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            refreshAllTabs();
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                exception.getMessage(), "Booking Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_confirmBtnActionPerformed

    private void allAppBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allAppBtnActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_allAppBtnActionPerformed

    private void RescheduleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RescheduleBtnActionPerformed
        Appointment appointment = getSelectedAppointment();
        if (appointment == null) {
            return;
        }

        Doctor doctor = allocator.getBusinessEntity(appointment.getDoctor().getId());
        List<LocalDateTime> availableSlots = operation.loadAvailableSlots(
                doctor, appointment.getId());
        DateTimeFormatter slotFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM — HH:mm");
        Object[] slotOptions = availableSlots.stream()
                .map(slot -> slot.format(slotFormatter))
                .toArray();
        Object selectedSlot = javax.swing.JOptionPane.showInputDialog(this,
                "Select a new appointment time:", "Reschedule Appointment",
                javax.swing.JOptionPane.QUESTION_MESSAGE, null, slotOptions,
                slotOptions.length == 0 ? null : slotOptions[0]);
        if (selectedSlot == null) {
            return;
        }

        int selectedSlotIndex = java.util.Arrays.asList(slotOptions).indexOf(selectedSlot);
        try {
            operation.rescheduleAppointment(appointment, availableSlots.get(selectedSlotIndex));
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Appointment rescheduled successfully.", "Appointment Updated",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                refreshAllTabs();
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    exception.getMessage(), "Reschedule Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_RescheduleBtnActionPerformed

    private void CancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelBtnActionPerformed
        Appointment appointment = getSelectedAppointment();
        if (appointment == null) {
            return;
        }

        int confirmation = javax.swing.JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this appointment?",
                "Cancel Appointment", javax.swing.JOptionPane.YES_NO_OPTION);
        if (confirmation != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }

        try {
            operation.cancelAppointment(appointment);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Appointment cancelled successfully.", "Appointment Updated",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                refreshAllTabs();
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    exception.getMessage(), "Cancellation Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CancelBtnActionPerformed

    private void feedbackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feedbackBtnActionPerformed
        Appointment appointment = getSelectedAppointment();
        if (appointment == null) {
            return;
        }

        FeedbackDialog dialog = new FeedbackDialog(this, true);
        dialog.setVisible(true);
        if (!dialog.isSubmitted()) {
            return;
        }

        try {
            operation.submitFeedback(appointment, dialog.getSelectedRating(),
                dialog.getEnteredComment());
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Feedback submitted successfully.", "Feedback Submitted",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                refreshAllTabs();
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    exception.getMessage(), "Feedback Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_feedbackBtnActionPerformed

    private void editProfileBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editProfileBtnActionPerformed
        if (!editingProfile) {
            usernameTf.setEditable(true);
            emailTf.setEditable(true);
            emailTf1.setEditable(true);
            editProfileBtn.setText("Update Profile");
            editingProfile = true;
            return;
        }

        try {
            operation.updateProfile(usernameTf.getText(), patient.getSelf().getPassword(),
                    patient.getSelf().getGender(), patient.getSelf().getDateOfBirth().toString(),
                    emailTf.getText(), emailTf1.getText());
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Profile updated successfully.", "Profile Updated",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            usernameTf.setEditable(false);
            emailTf.setEditable(false);
            emailTf1.setEditable(false);
            editProfileBtn.setText("Edit Profile");
            editingProfile = false;
            refreshAllTabs();
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    exception.getMessage(), "Profile Update Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_editProfileBtnActionPerformed

    private void resetPwBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetPwBtnActionPerformed
        ResetPassword dialog = new ResetPassword(this, true);
        dialog.setVisible(true);
        if (!dialog.isSubmitted()) {
            return;
        }
        if (!dialog.getOldPassword().equals(patient.getSelf().getPassword())) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Old password is incorrect", "Reset Password Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            operation.updateProfile(patient.getSelf().getName(), dialog.getNewPassword(),
                    patient.getSelf().getGender(), patient.getSelf().getDateOfBirth().toString(),
                    patient.getSelf().getEmail(), patient.getSelf().getPhoneNumber());
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Password reset successfully.", "Password Updated",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    exception.getMessage(), "Reset Password Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_resetPwBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Temporary bootstrap until the real login screen is merged. */
        try {
            Path entityDirectory = Path.of("data", "Entity");
            Path linkerDirectory = Path.of("data", "Linker");
            Files.createDirectories(entityDirectory);
            Files.createDirectories(linkerDirectory);

            BaseEntity.setIdNumberWidth(4);
            HospitalEntityAllocator allocator = new HospitalEntityAllocator(
                linkerDirectory, entityDirectory);

            List<Department> existingDepartments = allocator.getAllBusinessEntities(DepartmentToFile.PREFIX);
            Patient testPatient;

            if (existingDepartments.isEmpty()) {
                // 数据是空的,才建一次完整的测试数据
                PatientToFile patientData = new PatientToFile(
                    null, "Alice Tan", "OldPass1!", "alice@old.com",
                    UserWithDetails.Gender.FEMALE, LocalDate.of(1995, 5, 20), "0111234567");
                testPatient = allocator.convertToBusinessEntity(patientData, true);
                allocator.saveChanges(testPatient);

                DepartmentToFile deptData = new DepartmentToFile(null, "Cardiology");
                Department testDepartment = allocator.convertToBusinessEntity(deptData, true);
                allocator.saveChanges(testDepartment);

                MedicalManagerToFile managerData = new MedicalManagerToFile(
                    null, "Manager Lim", "Passw0rd!", "lim@hospital.com",
                    UserWithDetails.Gender.FEMALE, LocalDate.of(1975, 1, 1), "0138887777");
                MedicalManager testManager = allocator.convertToBusinessEntity(managerData, true);
                allocator.saveChanges(testManager);

                DoctorToFile doctorData = new DoctorToFile(
                    null, "Dr. Wong", "Passw0rd!", "wong@hospital.com",
                    UserWithDetails.Gender.MALE, LocalDate.of(1980, 3, 10), "0129876543");
                Doctor testDoctor = allocator.convertToBusinessEntity(doctorData, true);
                testDoctor.setBelongsToDepartment(deptData);
                testDoctor.setBelongsToMedicalManager(managerData);
                allocator.saveChanges(testDoctor);

                DoctorShiftToFile shiftData = new DoctorShiftToFile(
                    null, LocalDate.of(2026, 9 , 20), LocalTime.of(9, 0), LocalTime.of(17, 0));
                testDoctor.getDoctorShifts().add(shiftData);
                allocator.saveChanges(testDoctor);

                FacilityToFile facilityData = new FacilityToFile(
                    null, "Room 101", FacilityToFile.FacilityType.CONSULTATION_ROOM, 1, true);
                Facility testFacility = allocator.convertToBusinessEntity(facilityData, true);
                testFacility.setBelongsToDepartment(deptData);
                allocator.saveChanges(testFacility);
            } else {
                // 数据已经存在,直接拿第一个已有的病人来测试,不重复创建
                testPatient = (Patient) allocator.getAllBusinessEntities(PatientToFile.PREFIX).get(0);
            }
            
            if (allocator.getAllBusinessEntities(MedicalRecordToFile.PREFIX).isEmpty()) {
            List<Doctor> doctorsForHistory = allocator.getAllBusinessEntities(DoctorToFile.PREFIX);
            List<Facility> facilitiesForHistory = allocator.getAllBusinessEntities(FacilityToFile.PREFIX);
            if (!doctorsForHistory.isEmpty() && !facilitiesForHistory.isEmpty()) {
                AppointmentToFile pastAppointmentData = new AppointmentToFile(
                    null, LocalDateTime.now().minusDays(7), "Past checkup",
                    AppointmentToFile.AppointmentStatus.COMPLETED);
                Appointment pastAppointment = allocator.convertToBusinessEntity(pastAppointmentData, true);
                pastAppointment.setPatient(testPatient.getSelf());
                pastAppointment.setDoctor(doctorsForHistory.get(0).getSelf());
                pastAppointment.setFacility(facilitiesForHistory.get(0).getSelf());
                allocator.saveChanges(pastAppointment);

                MedicalRecordToFile recordData = new MedicalRecordToFile(
                    null, 37.2, 78, 120, 80, "Common cold", "Advised rest and fluids");
                MedicalRecord testRecord = allocator.convertToBusinessEntity(recordData, true);
                testRecord.setAppointment(pastAppointmentData);
                allocator.saveChanges(testRecord);
                pastAppointment.setMedicalRecord(recordData);
                allocator.saveChanges(pastAppointment);

                PrescriptionToFile prescriptionData = new PrescriptionToFile(
                    null, "Paracetamol", "500mg", "Every 6 hours", 5,
                    "Take after meals", LocalDateTime.now().minusDays(7));
                testRecord.getPrescriptions().add(prescriptionData);
                allocator.saveChanges(testRecord);
            }
        }
            java.awt.EventQueue.invokeLater(() ->
                new PatientForm(allocator, testPatient).setVisible(true));
        } catch (java.io.IOException exception) {
            logger.log(java.util.logging.Level.SEVERE, "Unable to create bootstrap data directories", exception);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelBtn;
    private javax.swing.JTable PrescriptionsTbl;
    private javax.swing.JButton RescheduleBtn;
    private javax.swing.JButton allAppBtn;
    private javax.swing.JTable appointmentTbl;
    private javax.swing.JButton confirmBtn;
    private javax.swing.JButton editProfileBtn;
    private javax.swing.JTextField emailTf;
    private javax.swing.JTextField emailTf1;
    private javax.swing.JButton feedbackBtn;
    private javax.swing.JTextArea insuranceTextArea;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JTable medicalTbl;
    private javax.swing.JLabel ratingLbl;
    private javax.swing.JButton resetPwBtn;
    private javax.swing.JTextField usernameTf;
    // End of variables declaration//GEN-END:variables
}
