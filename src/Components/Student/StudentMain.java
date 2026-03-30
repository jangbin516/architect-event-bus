/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Components.Student;

import java.util.StringTokenizer;

import Components.Course.Course;
import Components.Course.CourseComponent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class StudentMain {
	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** StudentMain(ID:" + componentId + ") is successfully registered. \n");

		StudentComponent studentsList = new StudentComponent("Students.txt");
		CourseComponent coursesList = new CourseComponent("Courses.txt");
	
		Event event = null;
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent();
				switch (event.getEventId()) {
				case ListStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeStudentList(studentsList)));
					break;
				case RegisterStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerStudent(studentsList, event.getMessage())));
					break;
				case DeleteStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteStudent(studentsList, event.getMessage())));
					break;
				case RegisterCourses:
					registerCourseCache(coursesList, event.getMessage());
					break;
				case DeleteCourses:
					deleteCourseCache(coursesList, event.getMessage());
					break;
				case EnrollCourse:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, enrollCourse(studentsList, coursesList, event.getMessage())));
					break;
				case QuitTheSystem:
					printLogEvent("Get", event);
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}

	private static String registerStudent(StudentComponent studentsList, String message) {
		Student  student = new Student(message);
		if (!studentsList.isRegisteredStudent(student.studentId)) {
			studentsList.vStudent.add(student);
			return "This student is successfully added.";
		} else
			return "This student is already registered.";
	}

	private static String makeStudentList(StudentComponent studentsList) {
		String returnString = "";
		for (int j = 0; j < studentsList.vStudent.size(); j++) {
			returnString += studentsList.getStudentList().get(j).getString() + "\n";
		}
		return returnString;
	}

	private static String deleteStudent(StudentComponent studentsList, String studentId) {
		if (studentsList.deleteStudent(studentId)) {
			return "The selected student(" + studentId + ") is deleted.";
		}
		return "The selected student(" + studentId + ") does not exist.";
	}

	private static void registerCourseCache(CourseComponent coursesList, String message) {
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.getCourseId())) {
			coursesList.addCourse(course);
		}
	}

	private static void deleteCourseCache(CourseComponent coursesList, String courseId) {
		coursesList.deleteCourse(courseId);
	}

	private static String enrollCourse(StudentComponent studentsList, CourseComponent coursesList, String message) {
		StringTokenizer stringTokenizer = new StringTokenizer(message);
		if (stringTokenizer.countTokens() < 2) {
			return "Invalid request. Use: <courseId> <studentId>";
		}
		String courseId = stringTokenizer.nextToken();
		String studentId = stringTokenizer.nextToken();

		Student student = studentsList.findStudentById(studentId);
		if (student == null) {
			return "Enrollment failed: student(" + studentId + ") does not exist.";
		}
		Course course = coursesList.findCourseById(courseId);
		if (course == null) {
			return "Enrollment failed: course(" + courseId + ") does not exist.";
		}
		for (int i = 0; i < course.getPrerequisiteCoursesList().size(); i++) {
			String prerequisiteCourseId = course.getPrerequisiteCoursesList().get(i);
			if (!student.getCompletedCourses().contains(prerequisiteCourseId)) {
				return "Enrollment failed: prerequisite(" + prerequisiteCourseId + ") is not completed.";
			}
		}
		return "Enrollment succeeded: student(" + studentId + ") -> course(" + courseId + ").";
	}

	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}

}
