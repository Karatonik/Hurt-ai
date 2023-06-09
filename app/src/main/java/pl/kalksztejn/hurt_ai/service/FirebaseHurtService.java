package pl.kalksztejn.hurt_ai.service;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.kalksztejn.hurt_ai.model.Hurt;

public class FirebaseHurtService {

    private final DatabaseReference databaseReference;

    public FirebaseHurtService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Hurt");
    }

    public void saveRecord(String image64Base, String data) {
        // Generowanie unikalnego ID rekordu
        String id = databaseReference.push().getKey();
        System.out.println(id);

        // Tworzenie obiektu rekordu
        Hurt hurt = new Hurt(id, AuthenticationService.getOwner(), image64Base, data);

        // Zapisywanie rekordu do bazy danych Firebase
        assert id != null;
        databaseReference.child(id).setValue(hurt).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Record saved successfully");
            } else {
                System.out.println("Failed to save record: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    public void deleteRecordById(String recordId, final RecordDeleteCallback callback) {
        DatabaseReference recordRef = databaseReference.child(recordId);

        recordRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onRecordDeleted(recordId);
                    } else {
                        callback.onDeleteFailure(task.getException().getMessage());
                    }
                });
    }

    public void getRecordsByOwner(String owner, final RecordListCallback callback) {
        // Tworzenie zapytania do odczytu rekordów na podstawie właściciela
        Query query = databaseReference.orderByChild("owner").equalTo(owner);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hurt> recordList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hurt hurt = snapshot.getValue(Hurt.class);
                    recordList.add(hurt);
                }

                callback.onRecordListReceived(recordList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancelled(databaseError.getMessage());
            }
        });
    }

    public interface RecordDeleteCallback {
        void onRecordDeleted(String recordId);

        void onDeleteFailure(String errorMessage);
    }


    public interface RecordListCallback {
        void onRecordListReceived(List<Hurt> recordList);

        void onCancelled(String errorMessage);
    }

    public interface RecordSaveCallback {
        void onRecordSaved(Hurt record);

        void onSaveFailure(String errorMessage);

        void onRecordRetrieved(Hurt record);
    }
}
