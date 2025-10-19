package application;

// TODO: Modify the controller classes to use the entity classes and repository.
//  Remember to add instance variables for SequenceService and the repository interfaces.
//  See ControllerDoctor for an example.

import application.model.Doctor;
import application.model.DoctorRepository;
import application.model.Patient;
import application.model.PatientRepository;
import application.service.SequenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import view.PatientView;

@Controller
public class ControllerPatientCreate {

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    SequenceService sequence;

    /*
     * Request blank patient registration form.
     */
    @GetMapping("/patient/new")
    public String getNewPatientForm(Model model) {
        // return blank form for new patient registration
        model.addAttribute("patient", new PatientView());
        return "patient_register";
    }

    /*
     * Process data from the patient_register form
     */
    @PostMapping("/patient/new")
    public String createPatient(PatientView p, Model model) {

        /*
         * validate doctor last name and find the doctor id
         */
        Doctor d = doctorRepository.findByLastName(p.getPrimaryName());

        /*
         * insert to patient table
         */

        if (d != null) {
            // display patient data and the generated patient ID,  and success message
            Patient patientToInsert = Patient.fromView(p);
            int id = sequence.getNextSequence("PATIENT_SEQUENCE");
            patientToInsert.setId(id);
            try {
                // check for unique ssn
                Patient patientExist = patientRepository.findBySsn(p.getSsn());
                if (patientExist != null) {
                    throw new Exception("Patient already exists");
                }
                patientRepository.insert(patientToInsert);
                model.addAttribute("message", "Registration successful.");
                model.addAttribute("patient", p);
                p.setId(patientToInsert.getId());
                return "patient_show";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                model.addAttribute("patient", p);
                model.addAttribute("message", "Error inserting patient");
                return "patient_register";
            }
        } else {
            /*
             * on error
             * model.addAttribute("message", some error message);
             * model.addAttribute("patient", p);
             * return "patient_register";
             */
            model.addAttribute("patient", p);
            model.addAttribute("message", "Doctor does not exist");
            return "patient_register";
        }
    }

    /*
     * Request blank form to search for patient by id and name
     */
    @GetMapping("/patient/edit")
    public String getSearchForm(Model model) {
        model.addAttribute("patient", new PatientView());
        return "patient_get";
    }

    /*
     * Perform search for patient by patient id and name.
     */
    @PostMapping("/patient/show")
    public String showPatient(PatientView p, Model model) {
        // if found, return "patient_show", else return error message and "patient_get"
        Patient patient = patientRepository.findByIdAndLastName(p.getId(), p.getLastName());

        if (patient != null) {
            p.setId(patient.getId());
            p.setFirstName(patient.getFirstName());
            p.setLastName(patient.getLastName());
            p.setPrimaryName(patient.getPrimaryName());
            p.setBirthdate(patient.getBirthdate());
            p.setCity(patient.getCity());
            p.setState(patient.getState());
            p.setZipcode(patient.getZipcode());
            p.setStreet(patient.getStreet());
            model.addAttribute("patient", p);
            return "patient_show";
        } else {
            model.addAttribute("message", "No records found");
            model.addAttribute("patient", p);
            return "patient_get";
        }
    }
}
