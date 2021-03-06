package com.example.scheduleh;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenJioAdapter extends FirestoreRecyclerAdapter<Event, OpenJioAdapter.OpenJioHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OpenJioAdapter(@NonNull FirestoreRecyclerOptions<Event> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final OpenJioHolder holder, int position, @NonNull Event model) {
        holder.textViewStartTime.setText(model.getStartTime());
        holder.textViewEndTime.setText(model.getEndTime());
        holder.textViewEventName.setText(model.getEventName());
        holder.textViewDisplayName.setText(model.getDisplayName());

        if (model.getPriority() == 1) {
            holder.textViewPriority.setText("Low Priority");
            holder.textViewPriority.setTextColor(Color.parseColor("#00cf78"));
        } else if (model.getPriority() == 2) {
            holder.textViewPriority.setText("Medium Priority");
            holder.textViewPriority.setTextColor(Color.parseColor("#FFFF00"));
        } else if (model.getPriority() == 3) {
            holder.textViewPriority.setText("High Priority");
            holder.textViewPriority.setTextColor(Color.parseColor("#d11a2a"));
        }

        DocumentReference Ref = db.collection("users").document(model.getUserId());
        Ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> taskPhoto) {
                if(taskPhoto.isSuccessful()){
                    DocumentSnapshot Document = taskPhoto.getResult();
                    if(Document.exists()){

                        if (Document.getString("photoUrl") != null) {
                            //if not null, we will add the photo to the imageview of the profile screen
                            Glide.with(holder.userProfilePic.getContext())
                                    .load(Document.getString("photoUrl"))
                                    .into(holder.userProfilePic);
                        } else {
                            holder.userProfilePic.setImageResource(R.drawable.ic_home_default_profile_pic);
                        }

                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public OpenJioHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.openjio_item, viewGroup, false);
        return new OpenJioHolder(v);
    }

    class OpenJioHolder extends RecyclerView.ViewHolder {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        TextView textViewStartTime;
        TextView textViewEndTime;
        TextView textViewEventName;
        TextView textViewEventColor;
        TextView textViewDisplayName;
        TextView textViewPriority;
        Button buttonJoinJio;
        CircleImageView userProfilePic;

        public OpenJioHolder(@NonNull View itemView) {
            super(itemView);
            textViewStartTime = itemView.findViewById(R.id.openJio_start_time);
            textViewEndTime = itemView.findViewById(R.id.openJio_end_time);
            textViewEventName = itemView.findViewById(R.id.openJio_name);
            textViewEventColor = itemView.findViewById(R.id.openJio_color);
            textViewDisplayName = itemView.findViewById(R.id.openJioItem_displayName_textView);
            textViewPriority = itemView.findViewById(R.id.openJio_priority);
            buttonJoinJio = itemView.findViewById(R.id.openJio_joinJio_button);
            userProfilePic = itemView.findViewById(R.id.openJioItem_profilePhoto_imageButton);

            buttonJoinJio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());

                    Map<String, Object> eventData = documentSnapshot.getData();
                    db.collection("users").document(currentUser.getUid())
                            .collection("events").document(documentSnapshot.getId())
                            .set(eventData);

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", currentUser.getUid());
                    userInfo.put("displayName", currentUser.getDisplayName());
                    db.collection("users").document(documentSnapshot.get("userId").toString())
                            .collection("user jios").document(documentSnapshot.getId())
                            .collection("friends joined").document(currentUser.getUid())
                            .set(userInfo);

                    // sending notification
                    String message = mAuth.getCurrentUser().getDisplayName() + " has joined your OpenJio event: "
                            + documentSnapshot.get("eventName").toString();
                    Notification notification = new Notification(message, currentUser.getUid(), "joinOpenJio");
                    db.collection("users").document(documentSnapshot.get("userId").toString())
                            .collection("notifications").add(notification);

                    documentSnapshot.getReference().delete();

                }
            });
        }
    }
}
