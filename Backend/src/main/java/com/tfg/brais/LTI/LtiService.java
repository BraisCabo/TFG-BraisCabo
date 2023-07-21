package com.tfg.brais.LTI;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.asynchttpclient.uri.Uri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tfg.brais.LTI.Tool.Score;
import com.tfg.brais.Model.Exam;
import com.tfg.brais.Model.ExerciseUpload;
import com.tfg.brais.Model.Subject;
import com.tfg.brais.Model.User;
import com.tfg.brais.Model.DTOS.UserRegisterDTO;
import com.tfg.brais.Repository.ExamRepository;
import com.tfg.brais.Repository.ExerciseUploadRepository;
import com.tfg.brais.Repository.UserRepository;
import com.tfg.brais.Service.ComplementaryServices.CSVService;
import com.tfg.brais.Service.ComplementaryServices.ExamCheckService;
import com.tfg.brais.Service.ComplementaryServices.ExerciseUploadCheckService;
import com.tfg.brais.Service.ComplementaryServices.SubjectCheckService;
import com.tfg.brais.Service.ComplementaryServices.UserCheckService;
import com.tfg.brais.Service.ControllerServices.SubjectService;

import edu.uoc.elc.lti.tool.AssignmentGradeService;
import edu.uoc.elc.lti.tool.Tool;
import edu.uoc.elc.lti.tool.oidc.AuthRequestUrlBuilder;
import edu.uoc.elc.lti.tool.oidc.LoginRequest;
import edu.uoc.elc.lti.tool.oidc.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LtiService {

    @Autowired
    private LtiInfo ltiInfo;

    @Autowired
    private LtiTool ltiTool;

    @Autowired
    private CSVService csvService;

    @Autowired
    private UserCheckService userCheckService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectCheckService subjectCheckService;

    @Autowired
    private ExamCheckService examCheckService;

    @Autowired
    private ExerciseUploadRepository exerciseUploadRepository;

    @Autowired
    private ExerciseUploadCheckService exerciseUploadCheckService;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<String> loginOnLti(HttpServletRequest request, HttpServletResponse response) {
        LoginRequest loginRequest = getLoginRequest(request);
        try {
            new URI(loginRequest.getTarget_link_uri());
            String authRequest = getLoginResponse(loginRequest);
            response.sendRedirect(authRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<UserExamModel> getResource(String id_token, String state, HttpServletRequest request) {
        Tool tool = this.ltiTool.getTool(request);
        tool.validate(id_token, state);
        if (!tool.isValid()) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(403));
        }
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail(tool.getUser().getEmail());
        userRegisterDTO.setName(tool.getUser().getGivenName());
        userRegisterDTO.setLastName(tool.getUser().getFamilyName());
        userRegisterDTO.setPassword(tool.getUser().getGivenName() + tool.getUser().getFamilyName());
        userRegisterDTO.setLtiId(tool.getUser().getId());
        csvService.registerUser(userRegisterDTO);
        ResponseEntity<User> response = userCheckService.findByMail(userRegisterDTO.getEmail());
        if (response.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(response.getStatusCode());
        }
        User user = response.getBody();
        String subjectName = tool.getCustomParameter("subject_name").toString();
        String examName = tool.getCustomParameter("exam_name").toString();
        ResponseEntity<Exam> examResponse = addToSubject(subjectName, examName, user, tool.isInstructor());
        if (examResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(examResponse.getStatusCode());
        }
        UserExamModel userExamModel = new UserExamModel();
        userExamModel.setEmail(user.getEmail());
        userExamModel.setExam(examResponse.getBody());
        setLtiInfo(user, examResponse.getBody(), tool);
        return ResponseEntity.ok(userExamModel);
    }

    public ResponseEntity<String> scoreTask(ExerciseUpload exerciseUpload) {
        ResponseEntity<String> accessTokenResponse = ltiTool.getAccessToken();
        if (accessTokenResponse.getStatusCode().isError()) {
            return new ResponseEntity<>(accessTokenResponse.getStatusCode());
        }
        String accessToken = accessTokenResponse.getBody();
        Score score = buildScore(exerciseUpload);
        return ltiTool.score(score, exerciseUpload.getExam().getLtiURI(), accessToken);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request) {
        return LoginRequest.builder().iss(request.getParameter("iss")).login_hint(request.getParameter("login_hint"))
                .target_link_uri(request.getParameter("target_link_uri"))
                .lti_message_hint(request.getParameter("lti_message_hint"))
                .lti_deployment_id(request.getParameter("lti_deployment_id"))
                .client_id(request.getParameter("client_id")).build();
    }

    private String getLoginResponse(LoginRequest loginRequest) {
        LoginResponse loginResponse = LoginResponse.builder()
                .client_id(
                        loginRequest.getClient_id() != null ? loginRequest.getClient_id() : this.ltiInfo.getClientId())
                .redirect_uri(loginRequest.getTarget_link_uri()).login_hint(loginRequest.getLogin_hint())
                .state((new BigInteger(50, new SecureRandom())).toString(16))
                .nonce((new BigInteger(50, new SecureRandom())).toString(16))
                .lti_message_hint(loginRequest.getLti_message_hint()).build();
        try {
            new URI(loginRequest.getTarget_link_uri());
            this.ltiTool.setState(loginResponse.getState());
            this.ltiTool.setNonce(loginResponse.getNonce());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AuthRequestUrlBuilder.build(this.ltiInfo.getOidcAuthUrl(), loginResponse);
    }

    private ResponseEntity<Exam> addToSubject(String subjectName, String examName, User user, boolean isTeacher) {
        ResponseEntity<Subject> subjectResponse = subjectCheckService.findByName(subjectName);
        if (subjectResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<Exam>(subjectResponse.getStatusCode());
        }
        Subject subject = subjectResponse.getBody();
        if (!subjectCheckService.isStudentOfSubject(user.getId(), subject.getId())
                && !subjectCheckService.isTeacherOfSubject(user.getId(), subject.getId())) {
            if (isTeacher) {
                subjectService.addTeacher(user, subject.getId());
            } else {
                subjectService.addStudent(user, subject.getId());
            }
        }
        ResponseEntity<Exam> examResponse = examCheckService.findExamByNameAndSubjectId(examName, subject.getId());
        if (examResponse.getStatusCode().is4xxClientError() || isTeacher) {
            return examResponse;
        }
        Exam exam = examResponse.getBody();
        if (!exerciseUploadCheckService.existUploadByStudentIdAndExamIdAndExamSubjectId(user.getId(), exam.getId(),
                subject.getId())) {
            ExerciseUpload exerciseUpload = new ExerciseUpload();
            exerciseUpload.setExam(exam);
            exerciseUpload.setStudent(user);
            exerciseUploadRepository.save(exerciseUpload);
        }
        return examResponse;
    }

    public void setLtiInfo(User user, Exam exam, Tool tool) {
        if (user.getLtiId() == null || !user.getLtiId().equals(tool.getUser().getId())) {
            user.setLtiId(tool.getUser().getId());
            try {
                userRepository.save(user);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        AssignmentGradeService agsServiceProvider = tool.getAssignmentGradeService();
        Uri uri = Uri.create(agsServiceProvider.getLineitem());
        String lineItemUri = uri.getBaseUrl() + uri.getPath();
        if (exam.getLtiURI() == null || !lineItemUri.equals(exam.getLtiURI())) {
            exam.setLtiURI(lineItemUri);
            examRepository.save(exam);
        }
    }

    private Score buildScore(ExerciseUpload upload) {
        Score score = new Score();
        score.setComment(upload.getComment());
        score.setTimestamp(getFormatedDate());
        score.setUserId(upload.getStudent().getLtiId());
        score.setScoreMaximum(10);
        if (!upload.getCalification().equals("")) {
            score.setScoreGiven(Double.parseDouble(upload.getCalification()));
        }
        score.setGradingProgress(upload.getCalification().equals("") ? "Pending" : "FullyGraded");
        score.setActivityProgress(upload.getCalification().equals("") ? "Initialized " : "Completed");
        return score;
    }

    private String getFormatedDate(){
        LocalDateTime fechaHora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return fechaHora.atZone(ZoneOffset.UTC).format(formatter);
    }

}
