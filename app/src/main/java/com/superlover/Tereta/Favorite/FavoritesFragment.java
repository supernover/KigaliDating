package com.superlover.Tereta.Favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.superlover.Tereta.Profile.ProfileActivity;
import com.superlover.Tereta.R;

public class FavoritesFragment extends Fragment {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerViewFavoritesView;
    private FavoritesFirestore favoritesFirestore;

    LinearLayout linearLayoutFavoritesContent;
    LinearLayout linearLayoutFavoritesEmpty;


    private AdView adViewFavorites;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorites_fragment, container, false);

        recyclerViewFavoritesView = view.findViewById(R.id.recyclerViewFavoritesView);

        FavoritesRecyclerView();


        linearLayoutFavoritesContent = view.findViewById(R.id.linearLayoutFavoritesContent);
        linearLayoutFavoritesContent.setVisibility(View.VISIBLE);
        linearLayoutFavoritesEmpty = view.findViewById(R.id.linearLayoutFavoritesEmpty);
        linearLayoutFavoritesEmpty.setVisibility(View.GONE);

        adViewFavorites = view.findViewById(R.id.adViewFavorites);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("2B8A66841577BC8BDE80A595867FC2A4") // Enter your test device id here from Logcat
                .build();
        adViewFavorites.loadAd(adRequest);

        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("favors")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            if (queryDocumentSnapshots.size() == 0) {

                                linearLayoutFavoritesContent.setVisibility(View.GONE);
                                linearLayoutFavoritesEmpty.setVisibility(View.VISIBLE);

                            } else {

                                linearLayoutFavoritesContent.setVisibility(View.VISIBLE);
                                linearLayoutFavoritesEmpty.setVisibility(View.GONE);
                            }
                        }
                    }
                });


        return view;
    }

    private void FavoritesRecyclerView() {

        Query query = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("favors")
                .orderBy("user_favorited", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<FavoritesClass> options = new FirestoreRecyclerOptions.Builder<FavoritesClass>()
                .setQuery(query, FavoritesClass.class)
                .build();

        favoritesFirestore = new FavoritesFirestore(options);

        recyclerViewFavoritesView.setHasFixedSize(true);
        recyclerViewFavoritesView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavoritesView.setAdapter(favoritesFirestore);

        favoritesFirestore.setOnItemClickListener(new FavoritesFirestore.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final FavoritesClass favoritesClass = documentSnapshot.toObject(FavoritesClass.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();


                final Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("user_uid", favoritesClass.getUser_favorite());
                startActivity(intent);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        favoritesFirestore.startListening();

    }

}
