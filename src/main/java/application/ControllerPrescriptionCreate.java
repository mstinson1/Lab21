package application;

import application.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import application.service.SequenceService;
import view.PrescriptionView;

import java.time.LocalDate;

@Controller
public class ControllerPrescriptionCreate {
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private SequenceService sequence;

    /**
     * Doctor requests blank form for new prescription.
     */
    @GetMapping("/prescription/new")
    public String getPrescriptionForm(Model model) {
        model.addAttribute("prescription", new PrescriptionView());
        return "prescription_create";
    }

    /**
     * Process data entered on prescription_create form.
     */
    @PostMapping("/prescription")
    public String createPrescription(PrescriptionView p, Model model) {
        // validate doctor by last name

        Doctor doctor = doctorRepository.findByLastName(p.getDoctorLastName());
        if (doctor == null) {
            model.addAttribute("message", "Doctor not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
        }

        // validate patient by id and last name
        Patient patient = patientRepository.findByIdAndLastName(p.getPatientId(), p.getPatientLastName());
        if (patient == null) {
            model.addAttribute("message", "Patient not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
        }

        // validate drug by name
        Drug drug = drugRepository.findByName(p.getDrugName());
        if (drug == null) {
            model.addAttribute("message", "Drug not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
        }

        // create prescription document
        int rxId = sequence.getNextSequence("RXID_SEQUENCE");
        Prescription rx = new Prescription();
        rx.setRxid(rxId);
        rx.setDrugName(p.getDrugName());
        rx.setQuantity(p.getQuantity());
        rx.setPatientId(p.getPatientId());
        rx.setDoctorId(doctor.getId());
        rx.setDateCreated(LocalDate.now().toString());
        rx.setRefills(p.getRefills());

        prescriptionRepository.insert(rx);

        p.setRxid(rxId);
        model.addAttribute("message", "Prescription created successfully.");
        model.addAttribute("prescription", p);
        return "prescription_show";
    }
}
