package com.tfg.brais.Service.ComplementaryServices;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
                checkIfUserOnSubject(userRegisterDTO);
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

    private void checkIfUserOnSubject(UserRegisterDTO userRegisterDTO) {
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
}
