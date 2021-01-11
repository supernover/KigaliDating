package com.ReallyDatingSite.KigaliRwanda.Loves;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ReallyDatingSite.KigaliRwanda.Profile.ProfileActivity;
import com.ReallyDatingSite.KigaliRwanda.R;

public class LovesFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String user_keyz;
    private RecyclerView recyclerViewLovesView;
    private LovesFirestore lovesFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loves_fragment, container, false);

        recyclerViewLovesView = view.findViewById(R.id.recyclerViewLovesView);
        DateRecyclerView();

        return view;
    }

    private void DateRecyclerView() {
        Query query = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("loves")
                .orderBy("user_loved", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<LovesClass> options = new FirestoreRecyclerOptions.Builder<LovesClass>()
                .setQuery(query, LovesClass.class)
                .build();

        lovesFirestore = new LovesFirestore(options);
        recyclerViewLovesView.setHasFixedSize(true);
        recyclerViewLovesView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewLovesView.setAdapter(lovesFirestore);

        lovesFirestore.setOnItemClickListener(new LovesFirestore.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final LovesClass lovesClass = documentSnapshot.toObject(LovesClass.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                final Intent intent = new Intent(getContext(), ProfileActivity.class);

                String user_uids = lovesClass.getUser_loves();
                intent.putExtra("user_uid", lovesClass.getUser_loves());
                startActivity(intent);
            }
        });
    }


    private String UserProfile(final String user_uid, final String user_key) {
        firebaseFirestore.collection("users")
                .document(user_uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            user_keyz = documentSnapshot.getString(user_key);
                        }
                    }
                });
        return user_keyz;
    }

    @Override
    public void onStart() {
        super.onStart();
        lovesFirestore.startListening();
    }
}
