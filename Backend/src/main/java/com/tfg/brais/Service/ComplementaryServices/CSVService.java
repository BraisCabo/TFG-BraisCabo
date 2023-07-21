package com.tfg.brais.Service.ComplementaryServices;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.ImportedExamDTO;
import com.tfg.brais.Model.DTOS.UserRegisterDTO;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Service.ControllerServices.AccountService;
import com.tfg.brais.Service.ControllerServices.SubjectService;

@Service
public class CSVService {

    @Autowired
    private FileService fileService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ExamCheckService examCheckService;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserCheckService userCheckService;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    public void examToCSV(String name, String email, String filePath, String fileName, List<String> questions,
            List<String> answers)
            throws IOException {
        Files.createDirectories(fileService.createPath(filePath.toString()));
        ArrayList<String> firstRow = new ArrayList<String>();
        firstRow.add("Nombre y apellidos");
        firstRow.add("Email");
        firstRow.addAll(questions);
        ArrayList<String> secondRow = new ArrayList<String>();
        secondRow.add(name);
        secondRow.add(email);
        secondRow.addAll(answers);
        CSVWriter csvWriter = new CSVWriter(
                Files.newBufferedWriter(fileService.createPath(Paths.get(filePath.toString(), fileName).toString())),
                ';',
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(firstRow.toArray(new String[firstRow.size()]));
        csvWriter.writeNext(secondRow.toArray(new String[secondRow.size()]));
        csvWriter.close();
    }

    public ResponseEntity<Exam> CSVToExam(long subjectId, ImportedExamDTO importedExam, Principal userPrincipal) {
        Exam exam = importedExam.createExam();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(importedExam.getFile().getInputStream()));
            String[] line = csvReader.readNext();
            int lineLength = line.length;
            if ((lineLength - 8) / 2 <= 0) {
                return ResponseEntity.notFound().build();
            }
            line = csvReader.readNext();
            for (int i = 8; i < lineLength; i += 2) {
                exam.getQuestions().add(line[i]);
                exam.getQuestionsCalifications().add(1.0);
            }

            ResponseEntity<Exam> checkIfCanCreate = examCheckService.checkIfCanCreate(subjectId, exam, userPrincipal);
            if (checkIfCanCreate.getStatusCode().is4xxClientError()) {
                return checkIfCanCreate;
            }
            exam.setSubject(subjectService.findSubjectById(subjectId).getBody());
            exam = examRepository.save(exam);
            do {
                UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
                userRegisterDTO.setName(line[1]);
                userRegisterDTO.setLastName(line[0]);
                userRegisterDTO.setEmail(line[2]);
                userRegisterDTO.setPassword(userRegisterDTO.getName() + userRegisterDTO.getLastName());
                registerUser(userRegisterDTO);
                ResponseEntity<User> response = userCheckService.findByMail(userRegisterDTO.getEmail());
                if (response.getStatusCode().is4xxClientError()) {
                    csvReader.close();
                    return new ResponseEntity<Exam>(response.getStatusCode());
                }
                User user = response.getBody();
                checkIfSubjectExists(subjectId, user);
                ExerciseUpload upload = new ExerciseUpload();
                upload.setExam(exam);
                upload.setStudent(userCheckService.findByMail(userRegisterDTO.getEmail()).getBody());
                List<String> answers = generateAnswers(line);
                upload.setFileName(user.getName() + user.getLastName() + ".csv");
                upload.importUpload(user, exam, answers, line[5], line[4], line[7]);
                exerciseUploadRepository.save(upload);
                Path path = Paths.get(exam.getSubject().getId().toString(), exam.getId().toString(),
                        user.getName() + user.getLastName());
                examToCSV(user.getName() + " " + user.getLastName(), user.getEmail(), path.toString(),
                        upload.getFileName(), exam.getQuestions(), upload.getAnswers());
            } while ((line = csvReader.readNext()) != null);
            exam = examRepository.findById(exam.getId()).get();
            csvReader.close();
            return ResponseEntity.ok(exam);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public void registerUser(UserRegisterDTO userRegisterDTO) {
        if (!userCheckService.emailUsed(userRegisterDTO.getEmail())) {
            accountService.register(userRegisterDTO, UriComponentsBuilder.newInstance());
        }
    }

    private void checkIfSubjectExists(long subjectId, User user) {
        if (!subjectCheckService.isStudentOfSubject(user.getId(), subjectId)) {
            subjectService.addStudent(user, subjectId);
        }
    }

    private List<String> generateAnswers(String[] line) {
        List<String> answers = new ArrayList<String>();
        for (int i = 9; i < line.length; i += 2) {
            answers.add(line[i]);
        }
        return answers;
    }

    public ResponseEntity<Resource> exportToCSV(Exam exam) {
        try {
            Files.createDirectories(fileService.createPath(exam.getSubject().getId().toString()));
            ArrayList<String> firstRow = new ArrayList<String>();
            firstRow.add("Apellido(s)");
            firstRow.add("Nombre");
            firstRow.add("Dirección de correo");
            firstRow.add("Estado");
            firstRow.add("Comenzado el");
            firstRow.add("Finalizado");
            firstRow.add("Tiempo requerido");
            firstRow.add("Calificación/10,00");

            for (int i = 0; i < exam.getQuestions().size(); i++) {
                firstRow.add("Pregunta " + (i + 1));
                firstRow.add("Respuesta " + (i + 1));
            }

            Path path = fileService.createPath(Paths
                            .get(exam.getSubject().getId().toString(), exam.getName() + ".csv")
                            .toString());
            CSVWriter csvWriter = new CSVWriter(
                    Files.newBufferedWriter(path));
            csvWriter.writeNext(firstRow.toArray(new String[firstRow.size()]));

            for (ExerciseUpload exerciseUpload : exam.getExerciseUploads()) {
                ArrayList<String> secondRow = new ArrayList<String>();
                secondRow.add(exerciseUpload.getStudent().getLastName());
                secondRow.add(exerciseUpload.getStudent().getName());
                secondRow.add(exerciseUpload.getStudent().getEmail());
                secondRow.add(exerciseUpload.isUploaded() ? "Finalizado" : "No entregado");
                secondRow.add(exerciseUpload.getStartedDate() == null ? "-" : parseDate(exerciseUpload.getStartedDate()));
                secondRow.add(exerciseUpload.isUploaded() == false ? "-" : parseDate(exerciseUpload.getStartedDate()));
                secondRow.add(timeDifference(exerciseUpload.getStartedDate(), exerciseUpload.getUploadDate()));
                secondRow.add(exerciseUpload.getCalification().toString().equals("") ? "-"
                        : exerciseUpload.getCalification().toString());
                if (exerciseUpload.getAnswers().size() == 0) {
                    exerciseUpload.setAnswers(Collections.nCopies(exam.getQuestions().size(), "-"));
                }
                for (int i = 0; i < exam.getQuestions().size(); i++) {
                    secondRow.add(exam.getQuestions().get(i));
                    secondRow.add(exerciseUpload.getAnswers().get(i));
                }
                csvWriter.writeNext(secondRow.toArray(new String[secondRow.size()]));
            }
            csvWriter.close();
            return fileService.downloadFile(Paths.get(exam.getSubject().getId().toString(), exam.getName() + ".csv"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    private String timeDifference(Date startedDate, Date uploadDate) {
        if (startedDate == null || uploadDate == null) {
            return "-";
        }
        long difference = uploadDate.getTime() - startedDate.getTime();
        long minutes = Math.floorDiv(difference, (60 * 1000));
        long seconds = difference / 1000 % 60;
        return minutes + " minutos " + seconds + " segundos";
    }

    private String parseDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy HH:mm");
        return formatter.format(date);
    }
}
