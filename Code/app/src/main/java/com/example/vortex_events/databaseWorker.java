package com.example.vortex_events;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class databaseWorker {
    FirebaseFirestore db;
    CollectionReference eventsRef;


    public databaseWorker() {
        this.db = FirebaseFirestore.getInstance();
        this.eventsRef = db.collection("Events");

        eventsRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("FireStore", error.toString());
            }
            if (value != null && !value.isEmpty()){
//                for (QueryDocumentSnapshot snapshot : value){
//                    String name = snapshot.getString("name");
//                    String name = snapshot.getString("name");
//                    String name = snapshot.getString("name");
//
//                }
            }
        }));
    }

    public void postEntrant(Entrant entrant){





    }

    public void postEvent(Event event){
        DocumentReference docuref = eventsRef.document(event.getName().toString());
        docuref.set(event);


    }


}
