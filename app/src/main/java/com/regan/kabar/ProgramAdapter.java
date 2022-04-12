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

import java.util.ArrayList;

/*
    Sat, March 6th, 2021
    Dibuat sebagai adapetr untuk recyclerview yang terletak di main page Kabar
 */
public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ViewHolder> {

    Context context;
    ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            String chatId = chatRoomArrayList.get(getAdapterPosition()).getRoomId();
            ArrayList<String> chatRoomParticipants = chatRoomArrayList.get(getAdapterPosition()).getParticipantsUID();

            intent = new Intent(v.getContext(), ChatRoomActivity.class);
            intent.putExtra("CHAT_ID", chatId);
            intent.putExtra("CHAT_PARTICIPANTS", chatRoomParticipants);

            context.startActivity(intent);
        }
    }

    public ProgramAdapter(Context context, ArrayList<ChatRoom> chatRoomArrayList) {
        this.context = context;
        this.chatRoomArrayList = chatRoomArrayList;
    }

//    public void updateData(ArrayList<ChatRoom> newList){
//        ChatRoomDiffCallback diffCallback = new ChatRoomDiffCallback(this.chatRoomArrayList, newList);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
//
//        diffResult.dispatchUpdatesTo(this);
//    }

    @NonNull
    @Override
    public ProgramAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_room_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramAdapter.ViewHolder holder, int position) {
        holder.chatTitleTextView.setText(chatRoomArrayList.get(position).getRoomName());
        holder.chatLastMessageTextView.setText(chatRoomArrayList.get(position).getLastMessage());
        holder.chatImageView.setImageResource(R.drawable.default_profile_picture); // Ganti ini nanti
        holder.chatLastTime.setText(chatRoomArrayList.get(position).getLastMessageTime());
    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }
}
