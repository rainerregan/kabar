package com.regan.kabar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoomProgramAdapter extends RecyclerView.Adapter<ChatRoomProgramAdapter.ViewHolder>{

    Context context;

    ArrayList<Message> messageArrayList;
    DateFunctions dateFunctions = new DateFunctions();

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView chatBubbleDisplayName, chatBubbleText, chatBubbleTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chatBubbleDisplayName = itemView.findViewById(R.id.chat_bubble_display_name);
            chatBubbleText = itemView.findViewById(R.id.chat_bubble_text);
            chatBubbleTime = itemView.findViewById(R.id.chat_bubble_time);
        }
    }

    public ChatRoomProgramAdapter(Context context, ArrayList<Message> messageArrayList) {
        this.context = context;
        this.messageArrayList = messageArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chatBubbleDisplayName.setText(messageArrayList.get(position).getSender().getDisplay_name());
        holder.chatBubbleText.setText(messageArrayList.get(position).getText());

        holder.chatBubbleTime.setText(dateFunctions.convertLongToHours(messageArrayList.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


}
