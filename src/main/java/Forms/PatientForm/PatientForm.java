/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Forms.PatientForm;

import Tools.HospitalEntityAllocator;
import entities.BaseEntity.BaseEntity;
import entities.BaseEntity.AppointmentToFile.AppointmentStatus;
import entities.BaseEntity.PrescriptionToFile;
import entities.BusinessEntity.Appointment;
import entities.BusinessEntity.Department;
import entities.BusinessEntity.Doctor;
import entities.BusinessEntity.Facility;
import entities.BusinessEntity.MedicalRecord;
import entities.BusinessEntity.Patient;
import Operations.PatientOperation.PatientOperation;
import Operations.PatientOperation.PatientOperation.DoctorAvailability;
import entities.BaseEntity.BillToFile;
import entities.BaseEntity.InsuranceToFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            int result = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to log out?", "Confirm Logout",
                    javax.swing.JOptionPane.YES_NO_OPTION);
            if (result != javax.swing.JOptionPane.YES_OPTION) {
                return;
            }
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
        
        departmentCb.addActionListener(evt -> refreshDoctorList());
        dateSn.addChangeListener(evt -> refreshDoctorList());
        doctorLs.addListSelectionListener(evt -> {
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

    // Tab1: Dashboard 
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

    // Tab 2: Booking Appointment
    private void populateBookingTab() {
        departments = operation.loadAllDepartments();
        departmentCb.removeAllItems();
        for (Department department : departments) {
            departmentCb.addItem(department.getSelf().getName());
        }
        
        // Custom format
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSn, "yyyy-MM-dd");
        dateSn.setEditor(dateEditor);
        dateSn.setValue(new java.util.Date()); // show today (default)
        refreshDoctorList();
    }

    private void refreshDoctorList() {
        // log
        System.out.println("refreshDoctorList called, date=" + dateSn.getValue());
        int departmentIndex = departmentCb.getSelectedIndex();
        
        if (departmentIndex < 0 || departmentIndex >= departments.size()) {
            doctorLs.setModel(new DefaultListModel<>());
            timeLs.setModel(new DefaultListModel<>());
            ratingLbl.setText("");
            return;
        }

        Date selectedDate = (Date) dateSn.getValue();
        // converts the old Date object into a modern Instant object.
        LocalDate date = selectedDate.toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        Department department = departments.get(departmentIndex);
        currentDoctorOptions = operation.loadDoctorAvailability(department, date);

        DefaultListModel<String> doctorModel = new DefaultListModel<>();
        // Show a message instead of an empty list when no doctors are available for that date.
        if (currentDoctorOptions.isEmpty()) {
            doctorModel.addElement("No doctors available");
        } else {
            for (DoctorAvailability option : currentDoctorOptions) {
                doctorModel.addElement(option.doctor().getSelf().getName()
                        + " (" + option.availableSlots().size() + " slots)");
            }
        }
        doctorLs.setModel(doctorModel);
        timeLs.setModel(new DefaultListModel<>());
        ratingLbl.setText("");
    }

    private void refreshSlotList() {
        int doctorIndex = doctorLs.getSelectedIndex();
        if (doctorIndex < 0 || currentDoctorOptions == null
                || doctorIndex >= currentDoctorOptions.size()) {
            timeLs.setModel(new DefaultListModel<>());
            ratingLbl.setText("");
            return;
        }

        DoctorAvailability doctorOption = currentDoctorOptions.get(doctorIndex);
        Doctor doctor = doctorOption.doctor();
        ratingLbl.setText(String.format("Rating: %.1f",
                operation.loadDoctorAverageRating(doctor)));
        currentSlotOptions = doctorOption.availableSlots();
        
        // EEE: The day of the week(e.g., Mon, Tue, Wed).
        // dd: The day of the month (e.g., 05, 12).
        // MMM: The month (e.g., Jan, Feb, Mar).
        DateTimeFormatter slotFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM — HH:mm");
        DefaultListModel<String> slotModel = new DefaultListModel<>();
        // When a doctor has no free times, show a clear placeholder instead of an empty list.
        if (currentSlotOptions.isEmpty()) {
            slotModel.addElement("No time slots available");
        } else {
            for (LocalDateTime slot : currentSlotOptions) {
                slotModel.addElement(slot.format(slotFormatter));
            }
        }
        timeLs.setModel(slotModel);
    }

    // Tab 3: All Appoinments
    private void populateAppointmentsTable() {
        currentAppointments = operation.loadMyAppointments();
        
        // Can also do this at design part
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

    // Enable the right action button based on the selected appointment state.
    private void updateAppointmentActionButtons() {
        int selectedRow = appointmentTbl.getSelectedRow();
        if (selectedRow < 0 || currentAppointments == null
                || selectedRow >= currentAppointments.size()) {
            RescheduleBtn.setEnabled(false);
            CancelBtn.setEnabled(false);
            feedbackBtn.setEnabled(false);
            feedbackBtn.setText("Feedback");
            return;
        }

        Appointment appointment = currentAppointments.get(selectedRow);
        AppointmentStatus status = appointment.getSelf().getStatus();
        boolean active = status == AppointmentStatus.BOOKED
                || status == AppointmentStatus.RESCHEDULED;
        boolean canRate = status == AppointmentStatus.COMPLETED
                && appointment.getFeedback() == null;
        boolean canViewFeedback = status == AppointmentStatus.COMPLETED
            && appointment.getFeedback() != null;
        RescheduleBtn.setEnabled(active);
        CancelBtn.setEnabled(active);
        feedbackBtn.setEnabled(canRate || canViewFeedback);
        feedbackBtn.setText(canViewFeedback ? "View Feedback" : "Feedback");
    }

    private Appointment getSelectedAppointment() {
        int selectedRow = appointmentTbl.getSelectedRow();
        if (selectedRow < 0 || currentAppointments == null
                || selectedRow >= currentAppointments.size()) {
            return null;
        }
            return currentAppointments.get(selectedRow);
    }

    // Refresh everything after a save so the screen shows the newest data from the files.
    private void refreshAllTabs() {
        // Reloads the patient from the data files (get latest info)
        patient = allocator.getBusinessEntity(patient.getId());
        operation = new PatientOperation(allocator, patient);
        populateDashboard();
        refreshDoctorList();
        populateAppointmentsTable();
    }

    // Tab 5: Profile
    private void populateProfileTab() {
        usernameTf.setText(patient.getSelf().getName());
        emailTf.setText(patient.getSelf().getEmail());
        emailTf1.setText(patient.getSelf().getPhoneNumber());
        usernameTf.setEditable(false);
        emailTf.setEditable(false);
        emailTf1.setEditable(false);

        InsuranceToFile insurance = operation.loadInsuranceStatus();
        // for constructing long strings of text piece by piece.
        StringBuilder profileText = new StringBuilder();
        if (insurance == null) {
            profileText.append("No insurance on file.");
        } else {
            profileText.append("Company: ").append(insurance.getCompanyName()).append('\n')
                    .append("Coverage: ").append(insurance.getCoveragePercentage()).append("%\n")
                    .append("Accepted: ").append(insurance.isAccepted());
        }
        insuranceTextArea.setText(profileText.toString());
        insuranceTextArea.setEditable(false);
        
        List<BillToFile> billingRecords = operation.loadBillingList();
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] {"Assessment Fee", "Consultation Fee", "Insurance Deduct",
                        "Total", "Issued At", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        if (billingRecords != null) {
            for (BillToFile bill : billingRecords) {
                tableModel.addRow(new Object[] {
                    "RM " + bill.getAssessmentFee(),
                    "RM " + bill.getConsultationFee(),
                    "RM " + bill.getInsuranceDeduction(),
                    "RM " + bill.getMoney(),
                    bill.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    bill.getStatus()
                });
            }
        }
        // Install the model even when there are no bills, so the empty table keeps its columns.
        billingTb.setModel(tableModel);
    }

    // Tab 4: Medical History
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
        medicalTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        medicalTbl.getColumnModel().getColumn(0).setPreferredWidth(150); // Date
        medicalTbl.getColumnModel().getColumn(1).setPreferredWidth(110); // Doctor
        medicalTbl.getColumnModel().getColumn(2).setPreferredWidth(150); // Diagnosis
        medicalTbl.getColumnModel().getColumn(3).setPreferredWidth(304); // Vitals — longest content
        refreshPrescriptionsTable();
    }

    // Tab 4: Prescriptions
    private void refreshPrescriptionsTable() {
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] {"Medication", "Dosage", "Frequency", "Duration & Instructions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        PrescriptionsTbl.setModel(tableModel);

        PrescriptionsTbl.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        PrescriptionsTbl.getColumnModel().getColumn(0).setPreferredWidth(150); // Medication
        PrescriptionsTbl.getColumnModel().getColumn(1).setPreferredWidth(110);  // Dosage
        PrescriptionsTbl.getColumnModel().getColumn(2).setPreferredWidth(150); // Frequency
        PrescriptionsTbl.getColumnModel().getColumn(3).setPreferredWidth(304); // Duration & Instructions

        int selectedRow = medicalTbl.getSelectedRow();
        if (selectedRow < 0 || currentMedicalHistory == null
                || currentMedicalHistory.isEmpty()
                || selectedRow >= currentMedicalHistory.size()) {
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
        jLabel3 = new javax.swing.JLabel();
        allAppBtn = new javax.swing.JButton();
        bookAppBtn = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        departmentCb = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        timeLs = new javax.swing.JList<>();
        confirmBtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ratingLbl = new javax.swing.JLabel();
        dateSn = new javax.swing.JSpinner();
        jScrollPane5 = new javax.swing.JScrollPane();
        doctorLs = new javax.swing.JList<>();
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
        jLabel16 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        billingTb = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBackground(new java.awt.Color(0, 0, 0));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(241, 232, 236));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel1.setText("Hi XXX");
        jLabel1.setToolTipText("");

        logOutBtn.setBackground(new java.awt.Color(244, 170, 170));
        logOutBtn.setFont(new java.awt.Font("Tw Cen MT", 1, 12)); // NOI18N
        logOutBtn.setText("Log Out");
        logOutBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel6.setBackground(new java.awt.Color(255, 255, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Your Next Appointment:", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tw Cen MT", 1, 20))); // NOI18N

        jLabel3.setBackground(new java.awt.Color(249, 249, 228));
        jLabel3.setFont(new java.awt.Font("YouYuan", 0, 24)); // NOI18N
        jLabel3.setText("No");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        allAppBtn.setBackground(new java.awt.Color(241, 237, 237));
        allAppBtn.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        allAppBtn.setText("See all Appoinment");
        allAppBtn.addActionListener(this::allAppBtnActionPerformed);

        bookAppBtn.setBackground(new java.awt.Color(238, 234, 234));
        bookAppBtn.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
        bookAppBtn.setText("Book new Appoinment");
        bookAppBtn.addActionListener(this::bookAppBtnActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(184, 184, 184)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(logOutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(allAppBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bookAppBtn))))
                        .addGap(186, 186, 186))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(logOutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(allAppBtn)
                    .addComponent(bookAppBtn))
                .addContainerGap(99, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dashboard", jPanel1);

        jPanel2.setBackground(new java.awt.Color(241, 232, 236));

        departmentCb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        timeLs.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(timeLs);

        confirmBtn.setBackground(new java.awt.Color(252, 232, 252));
        confirmBtn.setText("Confirm Booking");
        confirmBtn.addActionListener(this::confirmBtnActionPerformed);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel4.setText("Booking Appointment");
        jLabel4.setToolTipText("");

        jLabel5.setBackground(new java.awt.Color(153, 153, 153));
        jLabel5.setFont(new java.awt.Font("Sylfaen", 1, 14)); // NOI18N
        jLabel5.setText("Date:");

        jLabel6.setBackground(new java.awt.Color(153, 153, 153));
        jLabel6.setFont(new java.awt.Font("Sylfaen", 1, 14)); // NOI18N
        jLabel6.setText("Time slot:");

        jLabel7.setBackground(new java.awt.Color(153, 153, 153));
        jLabel7.setFont(new java.awt.Font("Sylfaen", 1, 14)); // NOI18N
        jLabel7.setText("Dortor:");

        jLabel8.setBackground(new java.awt.Color(153, 153, 153));
        jLabel8.setFont(new java.awt.Font("Sylfaen", 1, 14)); // NOI18N
        jLabel8.setText("Department:");

        ratingLbl.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        ratingLbl.setText("Rating");

        dateSn.setModel(new javax.swing.SpinnerDateModel());

        doctorLs.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(doctorLs);

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
                .addGap(59, 59, 59)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateSn)
                    .addComponent(departmentCb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(82, 82, 82)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ratingLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
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
                        .addGap(18, 18, 18)
                        .addComponent(ratingLbl)
                        .addGap(37, 37, 37)
                        .addComponent(confirmBtn))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(dateSn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(departmentCb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Booking", jPanel2);

        jPanel3.setBackground(new java.awt.Color(241, 232, 236));

        appointmentTbl.setFont(new java.awt.Font("Tw Cen MT", 0, 14)); // NOI18N
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

        RescheduleBtn.setBackground(new java.awt.Color(252, 252, 235));
        RescheduleBtn.setText("Reschedule");
        RescheduleBtn.addActionListener(this::RescheduleBtnActionPerformed);

        CancelBtn.setBackground(new java.awt.Color(253, 224, 224));
        CancelBtn.setText("Cancel");
        CancelBtn.addActionListener(this::CancelBtnActionPerformed);

        feedbackBtn.setBackground(new java.awt.Color(239, 239, 255));
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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(147, 147, 147)
                        .addComponent(RescheduleBtn)
                        .addGap(102, 102, 102)
                        .addComponent(CancelBtn)
                        .addGap(108, 108, 108)
                        .addComponent(feedbackBtn))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 697, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
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
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Appointments", jPanel3);

        jPanel4.setBackground(new java.awt.Color(241, 232, 236));

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
                .addGap(0, 16, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(122, 122, 122))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 720, Short.MAX_VALUE)
                            .addComponent(jScrollPane3))
                        .addGap(11, 11, 11))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Records", jPanel4);

        jPanel7.setBackground(new java.awt.Color(241, 232, 236));
        jPanel7.setPreferredSize(new java.awt.Dimension(500, 713));

        jLabel12.setFont(new java.awt.Font("Times New Roman", 1, 36)); // NOI18N
        jLabel12.setText("My Profile");
        jLabel12.setToolTipText("");

        usernameTf.setEditable(false);
        usernameTf.setText("name");

        jLabel11.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel11.setText("username:");

        jLabel13.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel13.setText("email:");

        emailTf.setEditable(false);
        emailTf.setText("email");

        editProfileBtn.setBackground(new java.awt.Color(253, 253, 238));
        editProfileBtn.setText("Edit Profile");
        editProfileBtn.addActionListener(this::editProfileBtnActionPerformed);

        resetPwBtn.setBackground(new java.awt.Color(253, 253, 241));
        resetPwBtn.setText("Reset Password");
        resetPwBtn.addActionListener(this::resetPwBtnActionPerformed);

        jLabel14.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel14.setText("Insurance:");

        jLabel15.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel15.setText("phone:");

        emailTf1.setEditable(false);
        emailTf1.setText("your phone");

        insuranceTextArea.setEditable(false);
        insuranceTextArea.setLineWrap(true);
        insuranceTextArea.setRows(5);
        insuranceTextArea.setWrapStyleWord(true);
        jScrollPane6.setViewportView(insuranceTextArea);

        jLabel16.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel16.setText("Billings:");

        billingTb.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Assessment Fee", "Consultation Fee", "Insurance Deduct", "Total", "Issued At", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(billingTb);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(283, 283, 283)
                        .addComponent(jLabel12))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(emailTf1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(resetPwBtn)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(editProfileBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(usernameTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(emailTf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(emailTf1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(resetPwBtn)
                            .addComponent(editProfileBtn)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(290, 290, 290))
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
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Make sure the patient picked a real doctor and slot, not the placeholder message used for empty lists.
    private void confirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmBtnActionPerformed
        int doctorIndex = doctorLs.getSelectedIndex();
        int slotIndex = timeLs.getSelectedIndex();
        String selectedDoctor = doctorLs.getSelectedValue();
        String selectedSlot = timeLs.getSelectedValue();
        if (doctorIndex < 0 || currentDoctorOptions == null
            || doctorIndex >= currentDoctorOptions.size()
            || "No doctors available".equals(selectedDoctor)) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please select a doctor.", "Booking Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (slotIndex < 0 || currentSlotOptions == null
            || slotIndex >= currentSlotOptions.size()
            || "No time slots available".equals(selectedSlot)) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Please select an appointment slot.", "Booking Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        Doctor doctor = currentDoctorOptions.get(doctorIndex).doctor();
        LocalDateTime slot = currentSlotOptions.get(slotIndex);
        String reason = javax.swing.JOptionPane.showInputDialog(this,
            "Reason for appointment:");
        if (reason == null) {
            return;
        }
        try {
            operation.bookAppointment(doctor, slot, reason);
            javax.swing.JOptionPane.showMessageDialog(this,
                "Appointment booked successfully.", "Booking Confirmed",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            refreshAllTabs();
        } catch (IllegalArgumentException exception) {
            javax.swing.JOptionPane.showMessageDialog(this,
                exception.getMessage(), "Booking Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (Exception exception) {
            // Keep unexpected errors inside the dialog instead of crashing the window.
            javax.swing.JOptionPane.showMessageDialog(this,
                "Something went wrong: " + exception.getMessage(), "Booking Error",
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
        } catch (Exception exception) {
            // Keep unexpected errors inside the dialog instead of crashing the window.
            javax.swing.JOptionPane.showMessageDialog(this,
                "Something went wrong: " + exception.getMessage(), "Reschedule Error",
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
        } catch (Exception exception) {
            // Keep unexpected errors inside the dialog instead of crashing the window.
            javax.swing.JOptionPane.showMessageDialog(this,
                "Something went wrong: " + exception.getMessage(), "Cancellation Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_CancelBtnActionPerformed

    // Show saved feedback or open the rating form when the appointment is completed and not rated yet.
    private void feedbackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_feedbackBtnActionPerformed
        Appointment appointment = getSelectedAppointment();
        if (appointment == null) {
            return;
        }

        if ("View Feedback".equals(feedbackBtn.getText())) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Rating: " + appointment.getFeedback().getRating()
                    + "\nComment: " + appointment.getFeedback().getComment(),
                    "Appointment Feedback", javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
        } catch (Exception exception) {
            // Keep unexpected errors inside the dialog instead of crashing the window.
            javax.swing.JOptionPane.showMessageDialog(this,
                "Something went wrong: " + exception.getMessage(), "Feedback Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_feedbackBtnActionPerformed

    // Toggle edit mode, then save the profile only after the user clicks update.
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
        } catch (Exception exception) {
            // Keep unexpected errors inside the dialog instead of crashing the window.
            javax.swing.JOptionPane.showMessageDialog(this,
                "Something went wrong: " + exception.getMessage(), "Profile Update Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_editProfileBtnActionPerformed

    // Check the old password first, then update only the password field with validation.
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
        } catch (Exception exception) {
            // Keep unexpected errors inside the dialog instead of crashing the window.
            javax.swing.JOptionPane.showMessageDialog(this,
                "Something went wrong: " + exception.getMessage(), "Reset Password Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_resetPwBtnActionPerformed

    private void bookAppBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookAppBtnActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_bookAppBtnActionPerformed

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

        java.awt.EventQueue.invokeLater(() -> {
            try {
                BaseEntity.setIdNumberWidth(4);
                HospitalEntityAllocator allocator = new HospitalEntityAllocator(
                        Path.of("data", "Linker"), Path.of("data", "Entity"));

                Patient defaultPatient = allocator.getBusinessEntity("PT0001");
                if (defaultPatient == null) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Default patient PT0001 was not found in the data files.",
                            "Startup Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }

                new PatientForm(allocator, defaultPatient).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Patient form failed to start: " + e.getMessage(),
                        "Startup Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CancelBtn;
    private javax.swing.JTable PrescriptionsTbl;
    private javax.swing.JButton RescheduleBtn;
    private javax.swing.JButton allAppBtn;
    private javax.swing.JTable appointmentTbl;
    private javax.swing.JTable billingTb;
    private javax.swing.JButton bookAppBtn;
    private javax.swing.JButton confirmBtn;
    private javax.swing.JSpinner dateSn;
    private javax.swing.JComboBox<String> departmentCb;
    private javax.swing.JList<String> doctorLs;
    private javax.swing.JButton editProfileBtn;
    private javax.swing.JTextField emailTf;
    private javax.swing.JTextField emailTf1;
    private javax.swing.JButton feedbackBtn;
    private javax.swing.JTextArea insuranceTextArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JTable medicalTbl;
    private javax.swing.JLabel ratingLbl;
    private javax.swing.JButton resetPwBtn;
    private javax.swing.JList<String> timeLs;
    private javax.swing.JTextField usernameTf;
    // End of variables declaration//GEN-END:variables
}
