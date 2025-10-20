package application;

// TODO: Modify the controller classes to use the entity classes and repository.
//  Remember to add instance variables for SequenceService and the repository interfaces.
//  See ControllerDoctor for an example.
import org.springframework.stereotype.Controller;

import application.model.*;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PrescriptionView;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ControllerPrescriptionFill {
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SequenceService sequenceService;
    @Autowired
    private PatientRepository patientRepository;


    /*
     * Patient requests form to fill prescription.
     */
    @GetMapping("/prescription/fill")
    public String getfillForm(Model model) {
        model.addAttribute("prescription", new PrescriptionView());
        return "prescription_fill";
    }

    // process data from prescription_fill form
    @PostMapping("/prescription/fill")
    public String processFillForm(PrescriptionView p, Model model) {
        /*
         * validate pharmacy name and address, get pharmacy id and phone
         */
        Pharmacy pharmacy = pharmacyRepository.findByNameAndAddress(p.getPharmacyName(), p.getPharmacyAddress());
        if (pharmacy == null) {
            model.addAttribute("message", "Pharmacy not found");
            model.addAttribute("prescription", p);
            return "prescription_fill";
        }
        p.setPharmacyID(pharmacy.getId());
        p.setPharmacyPhone(pharmacy.getPhone());

        /*
         * validate rxid and patient last name matches name on prescription
         */

        Prescription prescription = prescriptionRepository.findByRxid(p.getRxid());
        Patient patient = patientRepository.findByLastName(p.getPatientLastName());


        if (prescription == null) {
            model.addAttribute("message", "Prescription not found");
            model.addAttribute("prescription", p);
            return "prescription_fill";
        }
        if (prescription.getPatientId() != patient.getId()) {
            model.addAttribute("message", "Prescription does not belong to this patient");
            return "prescription_fill";
        }
        p.setPatientFirstName(patient.getFirstName());
        p.setPatientId(patient.getId());


        /*
         * have we exceeded the number of allowed refills
         * the first fill is not considered a refill.
         */
        int refillsAllowed = prescription.getRefills();
        int refillsUsed = prescription.getFills().size();
        int totalFillsAllowed = refillsAllowed + 1;

        if (refillsUsed >= totalFillsAllowed) {
            model.addAttribute("message", "Maximum number of refills exceeded");
            model.addAttribute("prescription", prescription);
            return "prescription_fill";
        }

        int refillsRemaining = refillsAllowed - (refillsUsed - 1);
        if (refillsUsed == 0) {
            refillsRemaining = refillsAllowed;
        }

        p.setQuantity(prescription.getQuantity());
        p.setRefills(refillsAllowed);
        p.setRefillsRemaining(refillsRemaining);
        prescriptionRepository.save(prescription);


        /*
         * calculate cost of prescription
         */

        String drugName = prescription.getDrugName();
        int drugQuantity = prescription.getQuantity();
        double totalCost = pharmacy.getCostForDrug(drugName);

        if (totalCost <= 0) {
            model.addAttribute("message", "Drug not stocked at this pharmacy.");
            model.addAttribute("prescription", p);
            return "prescription_fill";
        }

        BigDecimal totalPrice = BigDecimal.valueOf(totalCost).multiply(BigDecimal.valueOf(drugQuantity));
        p.setDrugName(drugName);
        p.setCost(totalPrice.toString());
        model.addAttribute("totalPrice", totalPrice);

        /*
         * get doctor information
         */
        Doctor doctor = doctorRepository.findById(prescription.getDoctorId());
        if (doctor == null) {
            model.addAttribute("message", "Doctor not found");
            model.addAttribute("prescription", p);
            return "prescription_fill";
        }
        p.setDoctorId(doctor.getId());
        p.setDoctorFirstName(doctor.getFirstName());
        p.setDoctorLastName(doctor.getLastName());

        //record date that prescription was filled
        p.setDateFilled(prescription.getDateCreated());


        // show the updated prescription with the most recent fill information
        model.addAttribute("message", "Prescription filled.");
        model.addAttribute("prescription", p);
        return "prescription_show";
    }

}
