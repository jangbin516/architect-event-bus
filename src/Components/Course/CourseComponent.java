/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University
 */
package Components.Course;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CourseComponent {
    protected ArrayList<Course> vCourse;

    public CourseComponent(String sCourseFileName) throws FileNotFoundException, IOException { 	
        BufferedReader bufferedReader  = new BufferedReader(new FileReader(resolveInputPath(sCourseFileName)));
        this.vCourse  = new ArrayList<Course>();
        while (bufferedReader.ready()) {
            String courseInfo = bufferedReader.readLine();
            if(!courseInfo.equals("")) this.vCourse.add(new Course(courseInfo));
        }    
        bufferedReader.close();
    }
    public ArrayList<Course> getCourseList() {
        return this.vCourse;
    }
    public boolean isRegisteredCourse(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            if(((Course) this.vCourse.get(i)).match(courseId)) return true;
        }
        return false;
    }
    public Course findCourseById(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            Course course = this.vCourse.get(i);
            if (course.match(courseId)) return course;
        }
        return null;
    }
    public boolean deleteCourse(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            if (this.vCourse.get(i).match(courseId)) {
                this.vCourse.remove(i);
                return true;
            }
        }
        return false;
    }
    public void addCourse(Course course) {
        this.vCourse.add(course);
    }
    private static String resolveInputPath(String fileName) {
        File directPath = new File(fileName);
        if (directPath.exists()) return directPath.getPath();

        File srcPath = new File("src" + File.separator + fileName);
        if (srcPath.exists()) return srcPath.getPath();

        return fileName;
    }
}
