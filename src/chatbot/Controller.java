package chatbot;
import java.lang.Integer;
import java.net.URL;
import java.util.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import org.jpl7.*;
public class Controller {
    @FXML
    private VBox chatArea;

    @FXML
    private TextField userInput;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    Set<String> knownNames,knownCourses;
    String output;
    public void initialize() { 
        knownNames= new HashSet<>(Arrays. asList("john","sam","sara"));
        // known students
        knownCourses= new HashSet<>(Arrays. asList("physics","math"));
        // known students
    }
    
    @FXML
    private void handleSend() {
        String input = userInput.getText().toLowerCase().replaceAll("[,;?]", " ");
        if (!input.isEmpty()) {
            addUserMessage(input);
            String [] tokens = input.split("\\s+"); // split based on spaces
            AnalayzeInput(input,tokens);
            userInput.clear();
        }
    }

    private void addUserMessage(String message) {
        Label userLabel = new Label(message);
        userLabel.setFont(new Font(14));
        userLabel.setWrapText(true);
        userLabel.setMaxWidth(Double.MAX_VALUE);
        userLabel.setStyle("-fx-background-color: #c7f9cc; -fx-background-radius: 15; -fx-padding: 10;");

        HBox userBox = new HBox(userLabel);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        userBox.setPadding(new Insets(5, 10, 5, 50));
        HBox.setHgrow(userLabel, Priority.ALWAYS);

        chatArea.getChildren().add(userBox);
        scrollToBottom();
    }

    private void addBotResponse(String message) {
        Label botLabel = new Label(message);
        botLabel.setFont(new Font(14));
        botLabel.setWrapText(true);
        botLabel.setMaxWidth(400);
        botLabel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 15; -fx-padding: 10;");

        HBox botBox = new HBox(botLabel);
        botBox.setAlignment(Pos.CENTER_LEFT);
        botBox.setPadding(new Insets(5, 50, 5, 10));
        HBox.setHgrow(botLabel, Priority.ALWAYS);

        chatArea.getChildren().add(botBox);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (scrollPane != null) {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        }
    }
    public void AnalayzeInput(String input,String [] tokens) {
        int QuestionType = 0;
        Query q = new Query("consult('src/chatbot/knowledge.pl')");
        q.hasSolution();
        output="";
        // name of all students
        if(input.contains("name") && input.contains("students")) {
            output="the names of all students are :\n";
            q = new Query("student(X,_,_)");
            QuestionType=1;
        }
        // courses of all students
        if(input.contains("courses") && (input.contains("students") || input.contains("availble") ) ) {
            output="the courses of all students are :\n";
            q = new Query("student(_,X,_)");
            QuestionType=1;
        }
        // course of a specific student
        if( (input.contains("courses") || input.contains("course") )&& (input.contains("john") || input.contains("sam") || input.contains("sara")) ) {
            String studentName=getNameFromSentence(tokens);
            output="the courses of "+studentName+" are :\n";
            q = new Query(String.format("student(%s, X, _)", studentName));
            QuestionType=1;
        }
        // Grade of a specific student
        if( (input.contains("grade") ||input.contains("mark") ) && (input.contains("john") || input.contains("sam") || input.contains("sara")) ) {
            String studentName=getNameFromSentence(tokens);
            output="the grade of "+studentName+" is :\n";
            q = new Query(String.format("student(%s, Y, X)", studentName));
            QuestionType=2;
        }
        // Grades of all students
        if((input.contains("marks") || input.contains("grades") )&& input.contains("students")) {
            output="the grades of all students are :\n";
            q = new Query("student(X,Y,Z)");
            QuestionType=3;
        }
        // year of a student
        if(input.contains("year")&& (input.contains("john") || input.contains("sam") || input.contains("sara"))) {
            String studentName=getNameFromSentence(tokens);
            output="the year of study of "+studentName+" is :\n";
            q = new Query(String.format("year_of_study(%s,X)", studentName));
            QuestionType=1;
        }
        // years of all students
        if(input.contains("years")&& input.contains("students")) {
            output="the year of study of all students are :\n";
            q = new Query("year_of_study(X,Y)");
            QuestionType=2;
        }
        // birthday of a student
        if(input.contains("birthday")&& (input.contains("john") || input.contains("sam") || input.contains("sara")) ) {
            String studentName=getNameFromSentence(tokens);
            output="the birthday of "+studentName+" is :\n";
            q = new Query(String.format("birthday(%s,X)", studentName));
            QuestionType=1;
        }
        // birthdays of all students
        if(input.contains("birthdays")&& input.contains("students")) {
            output="the birthdays of all students are :\n";
            q = new Query("birthday(X,Y)");
            QuestionType=2;
        }
        // group of a student
        if(input.contains("group")&& (input.contains("john") || input.contains("sam") || input.contains("sara")) ) {
            String studentName=getNameFromSentence(tokens);
            output="the group of "+studentName+" is :\n";
            q = new Query(String.format("group(%s,X)", studentName));
            QuestionType=1;
        }
        // groups of all students
        if(input.contains("groups")&& input.contains("students")) {
            output="the groupa of all students are :\n";
            q = new Query("group(X,Y)");
            QuestionType=2;
        }
        // passed of a student
        if(input.contains("passed")&& (input.contains("john") || input.contains("sam") || input.contains("sara"))&& (input.contains("physics") || input.contains("math")) ) {
            String studentName=getNameFromSentence(tokens);
            String course=getCourseFromSentence(tokens);
            output="the student "+studentName;
            q = new Query(String.format("passed(%s,%s)", studentName,course));
            if(q.hasSolution()) {
                output+=" is passed "+course;
            }else {
                output+=" doas't passed "+course;
            }
        }
        // passed students
        if(input.contains("passed")&& input.contains("students")) {
            output="the passed students are :\n";
            q = new Query("passed(X,Y)");
            QuestionType=2;
        }
        // average grade of a student
        if(input.contains("average")&& (input.contains("john") || input.contains("sam") || input.contains("sara")) ) {
            String studentName=getNameFromSentence(tokens);
            output="the average of "+studentName+" is :\n";
            q = new Query(String.format("average_grade(%s,X)", studentName));
            QuestionType=1;
        }
        // best student
        if( (input.contains("best") ||input.contains("top") ) && input.contains("student")) {
            output="the best student is :\n";
            q = new Query("best_student(X)");
            QuestionType=1;
        }
        //eligible_for_scholarship
        if(input.contains("scholarship") && (input.contains("john") || input.contains("sam") || input.contains("sara")) ) {
            String studentName=getNameFromSentence(tokens);
            output="the student "+studentName;
            q = new Query(String.format("eligible_for_scholarship(%s,X)", studentName));
            if(q.hasSolution()) {
                output+=" is taking the scholarship becouse his avr is ";
                QuestionType=1;
            }else {
                output+=" doas't taking the scholarship ";
            }
        }
        // ranking
        if(input.contains("ranking")) {
            Variable List = new Variable("List");
            q = new Query("ranking_list", new Term[]{List});
            output="Student Ranking (Best to Worst): \n";
            QuestionType4(q,2);
        }
        // same_birth_year students
        if(input.contains("same") && input.contains("age")) {
            Variable List = new Variable("List");
            q = new Query("find_same_birth_year", new Term[]{List});
            output="All students that have the same age : \n";
            QuestionType4(q,3);
        }
        // find_same_group_pairs students
        if(input.contains("same") && input.contains("group")) {
            Variable List = new Variable("List");
            q = new Query("find_same_group_pairs", new Term[]{List});
            output="All students that in the same group : \n";
            QuestionType4(q,3);
        }
        if(QuestionType==1) {
            while (q.hasMoreSolutions()) {
                Map<String, Term> solution = q.nextSolution(); // Move this inside the loop
                output += solution.get("X");
                output += "\n";
            }
        }else if(QuestionType==2){
            while (q.hasMoreSolutions()) {
                 Map<String, Term> solution = q.nextSolution(); // Move this inside the loop
                 output += solution.get("X");
                 output += " : ";
                 output += solution.get("Y");
                 output += "\n";
            }    
        }else if(QuestionType==3){
            while (q.hasMoreSolutions()) {
                 Map<String, Term> solution = q.nextSolution(); // Move this inside the loop
                 output += solution.get("X");
                 output += " : ";
                 output += solution.get("Z");
                 output += " in ";
                 output += solution.get("Y");
                 output += "\n";
            } 
        }
        addBotResponse("ChatBot: " + output);
    }
    
    public String getNameFromSentence(String[] tokens) {
        for (String token : tokens) {
            if (knownNames.contains(token)) {
                return token ;
            }
        }
        return null ;
    }
    
    public String getCourseFromSentence(String[] tokens) {
        for (String token : tokens) {
            if (knownCourses.contains(token)) {
                return token ;
            }
        }
        return null ;
    }
    void QuestionType4(Query q,int arg) {
            if (q.hasSolution()) {
                String s1="",s2="",s3="";
                Term result = q.oneSolution().get("List");
                for (Term pair : result.toTermArray()) {
                    if(arg==2) {
                        s1 = pair.arg(1)+"";                       
                        s2 = pair.arg(2)+"";
                    }else if(arg==3) {
                        Term student1 = pair.arg(1);
                        Term inner = pair.arg(2); // this is ','(Student2, Year)
                        Term student2 = inner.arg(1);
                        Term year = inner.arg(2);
                        s1 = student1+"";                       
                        s2 = student2+"";
                        s3=year+"";
                    }
                    output += s1+" : "+s2+" : "+s3;
                    output += "\n";
                }
            }
    }
}
