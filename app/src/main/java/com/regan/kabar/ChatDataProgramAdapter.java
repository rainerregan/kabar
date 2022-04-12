package com.regan.kabar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatDataProgramAdapter extends RecyclerView.Adapter<ChatDataProgramAdapter.ViewHolder> {

    Context context;
    ArrayList<Chat> chatRoomArrayList = new ArrayList<>();

    DateFunctions dateFunctions = new DateFunctions();

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView chatTitleTextView;
        TextView chatLastMessageTextView;
        ImageView chatImageView;
        TextView chatLastTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatTitleTextView = itemView.findViewById(R.id.chat_title);
            chatLastMessageTextView = itemView.findViewById(R.id.chat_last_message);
            chatImageView = itemView.findViewById(R.id.chat_image);
            chatLastTime = itemView.findViewById(R.id.chat_last_time);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            String chatId = chatRoomArrayList.get(getAdapterPosition()).getRoom_id();
            ArrayList<String> chatRoomParticipants = chatRoomArrayList.get(getAdapterPosition()).getParticipants();

            intent = new Intent(v.getContext(), ChatRoomActivity.class);
            intent.putExtra("CHAT_ID", chatId);
            intent.putExtra("CHAT_PARTICIPANTS", chatRoomParticipants);

            context.startActivity(intent);
        }
    }

    public ChatDataProgramAdapter(Context context, ArrayList<Chat> chatRoomArrayList) {
        this.context = context;
        this.chatRoomArrayList = chatRoomArrayList;
    }

    public void updateData(ArrayList<Chat> newList){
        ChatRoomDiffCallback diffCallback = new ChatRoomDiffCallback(this.chatRoomArrayList, newList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        chatRoomArrayList.clear();
        chatRoomArrayList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_room_item, parent, false);
        ChatDataProgramAdapter.ViewHolder viewHolder = new ChatDataProgramAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String room_name = "";
        for(String uid : chatRoomArrayList.get(position).getParticipants()){
            if (!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                FirebaseDatabase.getInstance().getReference("users")
                        .child(uid).get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User user = task.getResult().getValue(User.class);

                        String room_name = user.getDisplay_name();
                        holder.chatTitleTextView.setText(room_name);
                    }
                });
            }
        }

        holder.chatLastMessageTextView.setText(chatRoomArrayList.get(position).getLast_message());

        holder.chatImageView.setImageResource(R.drawable.default_profile_picture); // Ganti ini nanti

        holder.chatLastTime.setText(dateFunctions.convertLongToHours(chatRoomArrayList.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }
}
