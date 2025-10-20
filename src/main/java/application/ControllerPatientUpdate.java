package application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import application.model.*;
import application.service.*;
import view.*;

@Controller
public class ControllerPatientUpdate {

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private DoctorRepository doctorRepository;

  @Autowired
  private SequenceService sequenceService;

  @GetMapping("/patient/edit/{id}")
  public String getUpdateForm(@PathVariable int id, Model model) {
    Patient patient = patientRepository.findById(id).orElse(null);

    if (patient == null) {
      model.addAttribute("message", "No patient record found!");
      return "index";
    }

    PatientView pv = new PatientView();
    pv.setId(patient.getId());
    pv.setFirstName(patient.getFirstName());
    pv.setLastName(patient.getLastName());
    pv.setSsn(patient.getSsn());
    pv.setBirthdate(String.valueOf(patient.getBirthdate()));
    pv.setPrimaryName(patient.getPrimaryName() != null ? patient.getPrimaryName(): "");
    pv.setStreet(patient.getStreet());
    pv.setCity(patient.getCity());
    pv.setState(patient.getState());
    pv.setZipcode(patient.getZipcode());

    model.addAttribute("patient", pv);
    model.addAttribute("message", "Edit patient profile below:");
    return "patient_edit";
  }

  @PostMapping("/patient/edit")
  public String updatePatient(PatientView pv, Model model) {
    Patient patient = patientRepository.findById(pv.getId()).orElse(null);

    if (patient == null) {
      model.addAttribute("message", "Patient update failed! Patient not found.");
      model.addAttribute("patient", new PatientView());  // empty object to avoid Thymeleaf error
      return "patient_edit";
    }
    Doctor doctor = doctorRepository.findByLastName(pv.getPrimaryName());
    if (doctor == null) {
      model.addAttribute("message", "Primary doctor not found: " + pv.getPrimaryName());
      model.addAttribute("patient", pv);
      return "patient_edit";
    }

    patient.setPrimaryName(doctor.getLastName());
    patient.setStreet(pv.getStreet());
    patient.setCity(pv.getCity());
    patient.setState(pv.getState());
    patient.setZipcode(pv.getZipcode());

    patientRepository.save(patient);

    model.addAttribute("message", "Patient updated!");
    model.addAttribute("patient", pv);
    return "patient_show";
  }
}
