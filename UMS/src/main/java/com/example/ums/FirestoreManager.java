package com.example.ums;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirestoreManager {
    private static FirestoreManager instance = new FirestoreManager();
    private com.google.cloud.firestore.Firestore db;

    public FirestoreManager() {
    try {
        FileInputStream serviceAccount =
            new FileInputStream("/Users/Malak/D/UNI/Senior-1 Year/Semester 7/Agile Software Engineering/Project/University-Management-System/UMS/src/main/resources/university-management-sy-9314c-firebase-adminsdk-fbsvc-7e85945818.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                .build();

        this.db = firestoreOptions.getService();
    } catch (IOException e) {
        throw new RuntimeException("Failed to initialize Firebase", e);
    }
}

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }



public com.google.cloud.firestore.Firestore getDb() {
        return db;
    }


    public void addAdmission(Admission admission) {
        DocumentReference ref = db.collection("Admission").document();
        String generatedId = ref.getId();
        admission.setAdmissionId(generatedId);

        ApiFuture<WriteResult> result = ref.set(admission);
        try {
            System.out.println("Admission added at: " + result.get().getUpdateTime());
            System.out.println("Generated ID: " + generatedId);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Admission> getAdmissionsByStatus(String status) {
        ArrayList<Admission> admissionsList = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future =
                    db.collection("Admission")
                            .whereEqualTo("status", status)
                            .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                Admission admission = doc.toObject(Admission.class);

                admission.setAdmissionId(doc.getId());

                admissionsList.add(admission);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return admissionsList;
    }


    public Admission getAdmissionByName(String name) {
        try {
            ApiFuture<QuerySnapshot> future =
                    db.collection("Admission")
                            .whereEqualTo("name", name)
                            .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (!documents.isEmpty()) {
                QueryDocumentSnapshot doc = documents.get(0);
                Admission admission = doc.toObject(Admission.class);
                admission.setAdmissionId(doc.getId());
                return admission;
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null; // No admission found
    }

    public ArrayList<Admission> getAllAdmissions() {
        ArrayList<Admission> admissionsList = new ArrayList<>();

        try {
            // Query entire collection
            ApiFuture<QuerySnapshot> future =
                    db.collection("Admission").get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot doc : documents) {
                Admission admission = doc.toObject(Admission.class);

                admission.setAdmissionId(doc.getId());

                admissionsList.add(admission);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return admissionsList;
    }







    public void addStudent(Student student) {
        DocumentReference ref = db.collection("Students").document(student.getStudentID());
        ApiFuture<WriteResult> result = ref.set(student);
        try {
            System.out.println("Student added at: " + result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public Student getStudent(String studentId) {
        DocumentReference ref = db.collection("Students").document(studentId);
        try {
            DocumentSnapshot snapshot = ref.get().get();
            if (!snapshot.exists()) return null;

            return snapshot.toObject(Student.class);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void Login(String username, String password) {

    }

    public void addInstructor(Instructor instructor) {
        try {

            DocumentReference ref = db.collection("Instructors").document(instructor.getEmail());

            ApiFuture<WriteResult> result = ref.set(instructor);
            System.out.println("Instructor added at: " + result.get().getUpdateTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Instructor getInstructor(String username) {
        try {
            DocumentReference ref = db.collection("Instructors").document(username);
            ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = ref.get();
            com.google.cloud.firestore.DocumentSnapshot snapshot = future.get();

            if (!snapshot.exists()) {
                System.out.println("Instructor not found");
                return null;
            }

            return snapshot.toObject(Instructor.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    public void addHR(HR hr) {
        try {
            DocumentReference ref = db.collection("HR").document(hr.getEmail());

            ApiFuture<WriteResult> result = ref.set(hr);
            System.out.println("HR added at: " + result.get().getUpdateTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HR getHR(String username) {
        try {
            DocumentReference ref = db.collection("HR").document(username);
            ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = ref.get();
            com.google.cloud.firestore.DocumentSnapshot snapshot = future.get();

            if (!snapshot.exists()) {
                System.out.println("HR user not found.");
                return null;
            }

            return snapshot.toObject(HR.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





    public void addAdmin(Admin admin) {
        try {
            DocumentReference ref = db.collection("Admin").document(admin.getEmail());

            ApiFuture<WriteResult> result = ref.set(admin);
            System.out.println("Admin added at: " + result.get().getUpdateTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Admin getAdmin(String username) {
        try {
            DocumentReference ref = db.collection("Admin").document(username);
            ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = ref.get();
            com.google.cloud.firestore.DocumentSnapshot snapshot = future.get();

            if (!snapshot.exists()) {
                System.out.println("Admin user not found.");
                return null;
            }

            return snapshot.toObject(Admin.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    public void addParent(Parent parent) {
        try {
            DocumentReference ref = db.collection("Parent").document(parent.getEmail());

            ApiFuture<WriteResult> result = ref.set(parent);
            System.out.println("Parent added at: " + result.get().getUpdateTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Parent getParent(String username) {
        try {
            DocumentReference ref = db.collection("Parent").document(username);
            ApiFuture<com.google.cloud.firestore.DocumentSnapshot> future = ref.get();
            com.google.cloud.firestore.DocumentSnapshot snapshot = future.get();

            if (!snapshot.exists()) {
                System.out.println("Parent user not found.");
                return null;
            }

            return snapshot.toObject(Parent.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
