package com.regan.kabar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactProgramAdapter extends RecyclerView.Adapter<ContactProgramAdapter.ViewHolder> {

    Context context;

    ArrayList<User> contactList = new ArrayList<>();
    User currentUser;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView display_name_text_view, username_text_view;
        ImageView prof_pict_image_view;
        Button start_chat_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            display_name_text_view = itemView.findViewById(R.id.contact_display_name_text);
            username_text_view = itemView.findViewById(R.id.contact_username_text);
            prof_pict_image_view = itemView.findViewById(R.id.contact_profile_picture);
            start_chat_btn = itemView.findViewById(R.id.contact_start_chat_button);

            start_chat_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ChatFunctions chatFunctions = new ChatFunctions();

                    // Membuat chat ID menggunakan uid from and uid destination
                    User userDestination = contactList.get(getAdapterPosition());
                    String chatID = currentUser.getUid() + "-" + userDestination.getUid();
                    String chatID2 = userDestination.getUid() + "-" + currentUser.getUid();

                    // Check apakah chat sudah dibuat
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("chat_rooms")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    Chat newChat;
                                    if (snapshot.child(chatID).exists()){
                                        newChat = snapshot.child(chatID).getValue(Chat.class);
                                    }else if(snapshot.child(chatID2).exists()){
                                        newChat = snapshot.child(chatID2).getValue(Chat.class);
                                    }else{
                                        // Jika tidak ada

                                        newChat = new Chat(chatID);
                                        chatFunctions.createNewChat(newChat, currentUser, userDestination);
                                    }

                                    Log.i("CHat!", newChat.getRoom_id());

                                    // Buka chat
                                    Intent intent = new Intent(v.getContext(), ChatRoomActivity.class);
                                    intent.putExtra("CHAT_ID", newChat.getRoom_id());
                                    intent.putExtra("CHAT_PARTICIPANTS", newChat.getParticipants());

                                    context.startActivity(intent);
//                                    chatFunctions.openChatActivity(context, chatID, newChat.getParticipants());
                                    ((Activity)context).finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            });
        }

    }

    public ContactProgramAdapter(Context context, ArrayList<User> contactList, User currentUser) {
        this.context = context;
        this.contactList = contactList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ContactProgramAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Layout Inflater
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contact_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactProgramAdapter.ViewHolder holder, int position) {
        // Load data to view
        holder.display_name_text_view.setText(contactList.get(position).getDisplay_name());
        holder.username_text_view.setText(contactList.get(position).getUsername());
        Picasso.get().load(contactList.get(position).getProfile_picture_link()).into(holder.prof_pict_image_view);
    }

    @Override
    public int getItemCount() {
//        return display_name_list.length;
        return contactList.size();
    }
}
