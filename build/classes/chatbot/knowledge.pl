% Facts: student(Name, Course, Grade)
student(john, math, 9).
student(john, physics, 12).
student(sam, math, 30).
student(sara, physics, 15).
courses(math,physics).
% Additional facts
year_of_study(john, 1).
year_of_study(sam, 2).
year_of_study(sara, 2).

birthday(john, date(2000, 5, 10)).
birthday(sam, date(2000, 11, 2)).
birthday(sara, date(2003, 3, 25)).

group(john, a).
group(sam, b).
group(sara, b).

% Rules
passed(Student, Course) :- student(Student, Course, Grade), Grade >= 10.

% Get all students in a specific year
students_in_year(Year, Student) :- year_of_study(Student, Year).

% Get students in a group
students_in_group(Group, Student) :- group(Student, Group).

% Compare birth year
born_after(Student, Year) :-
    birthday(Student, date(BirthYear, _, _)),
    BirthYear > Year.

% Compute average grade
average_grade(StudentName, Avg) :-
    student(StudentName, _, _), % force StudentName to exist
    findall(Grade, student(StudentName, _, Grade), Grades),
    Grades \= [],
    sum_list(Grades, Sum),
    length(Grades, Count),
    Avg is Sum / Count.



% Rule: best student (with highest average grade)
best_student(BestStudent):-
    setof(Avg-StudentName, average_grade(StudentName, Avg), List),
    last(List, _-BestStudent),
    format('The best student is ~w~n', [BestStudent]).


% Rule: passed_math - True if a student passed math (grade >= 10)
passed_math(Student) :-
    student(Student, math, Grade),
    Grade >= 10.

% Rule: eligible_for_scholarship - True if student is eligible
eligible_for_scholarship(Student,Avg) :-
    average_grade(Student, Avg),
    Avg >= 14,
    passed_math(Student).



% Rule: ranking_list(-SortedList)
ranking_list(SortedList) :-
    setof(Avg-Student, average_grade(Student, Avg), List),
    reverse(List, SortedList).

% Rule: same_birth_year - True if two students were born in the same year
same_birth_year(Student1, Student2) :-
    birthday(Student1, date(Y, _, _)),
    birthday(Student2, date(Y, _, _)),
    Student1 \= Student2.

% Rule: find_same_birth_year_pairs(-Pairs)
% Returns a list of student pairs with the same birth year
find_same_birth_year(List) :-
    setof((Student1, Student2, Y), 
          (same_birth_year(Student1, Student2), birthday(Student1, date(Y, _, _))), 
          List), !.
find_same_birth_year([]).


% Rule: same_group - True if two students are in the same group
same_group(Student1, Student2) :-
    group(Student1, G),
    group(Student2, G),
    Student1 \= Student2.

% Rule: find_same_group_pairs(-Pairs)
% Returns a list of student pairs in the same group
find_same_group_pairs(Pairs) :-
    setof((Student1, Student2, Group), 
          (same_group(Student1, Student2), group(Student1, Group)), 
          Pairs), !.
find_same_group_pairs([]).
