package com.superlover.Tereta.Likes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.superlover.Tereta.Profile.ProfileActivity;
import com.superlover.Tereta.R;

public class LikesFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerViewLikesView;
    private LikesFirestore likesFirestore;


    LinearLayout linearLayoutLikesContent;
    LinearLayout linearLayoutLikesEmpty;


    private AdView adViewLikes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.likes_fragment, container, false);

        recyclerViewLikesView = view.findViewById(R.id.recyclerViewLikesView);
        LikesRecyclerView();

        linearLayoutLikesContent = view.findViewById(R.id.linearLayoutLikesContent);
        linearLayoutLikesContent.setVisibility(View.VISIBLE);
        linearLayoutLikesEmpty = view.findViewById(R.id.linearLayoutLikesEmpty);
        linearLayoutLikesEmpty.setVisibility(View.GONE);


        adViewLikes = view.findViewById(R.id.adViewLikes);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("2B8A66841577BC8BDE80A595867FC2A4") // Enter your test device id here from Logcat
                .build();
        adViewLikes.loadAd(adRequest);

        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("likes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            if (queryDocumentSnapshots.size() == 0) {

                                linearLayoutLikesContent.setVisibility(View.GONE);
                                linearLayoutLikesEmpty.setVisibility(View.VISIBLE);

                            } else {

                                linearLayoutLikesContent.setVisibility(View.VISIBLE);
                                linearLayoutLikesEmpty.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        return view;
    }

    private void LikesRecyclerView() {

        Query query = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("likes")
                .orderBy("user_liked", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<LikesClass> options = new FirestoreRecyclerOptions.Builder<LikesClass>()
                .setQuery(query, LikesClass.class)
                .build();

        likesFirestore = new LikesFirestore(options);
        recyclerViewLikesView.setHasFixedSize(true);
        recyclerViewLikesView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewLikesView.setAdapter(likesFirestore);

        likesFirestore.setOnItemClickListener(new LikesFirestore.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final LikesClass likesClass = documentSnapshot.toObject(LikesClass.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();

                final Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user_uid", likesClass.getUser_likes());
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        likesFirestore.startListening();
    }
}
