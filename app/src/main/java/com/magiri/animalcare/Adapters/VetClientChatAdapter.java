package com.magiri.animalcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.magiri.animalcare.Model.Chat;
import com.magiri.animalcare.R;
import com.magiri.animalcare.Session.Prevalent;

import java.util.List;

public class VetClientChatAdapter extends RecyclerView.Adapter<VetClientChatAdapter.MyViewHolder> {

    private Context context;
    private List<Chat> chatList;
    private String FarmerID;

    public VetClientChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        FarmerID= Prevalent.currentOnlineFarmer.getFarmerID();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.vet_client_chat_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Chat chat=chatList.get(position);
        if(chat.getFarmer()){
            holder.opponentMessageLayout.setVisibility(View.GONE);
            holder.yourChatDateTxt.setText(chat.getTimeStamp());
            holder.yourMessageTxt.setText(chat.getMessage());
        }else{
            holder.yourMessageLayout.setVisibility(View.GONE);
            holder.opponentDateTxt.setText(chat.getTimeStamp());
            holder.opponentMessageTxt.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder{
        private final RelativeLayout opponentMessageLayout,yourMessageLayout;
        private final TextView opponentMessageTxt,yourMessageTxt;
        private final TextView opponentDateTxt,yourChatDateTxt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            opponentMessageLayout=itemView.findViewById(R.id.opponentMessageLayout);;
            yourMessageLayout=itemView.findViewById(R.id.yourMessageLayout);
            opponentMessageTxt=itemView.findViewById(R.id.opponentMessage);
            yourMessageTxt=itemView.findViewById(R.id.yourChatTxt);
            opponentDateTxt=itemView.findViewById(R.id.opponentDateTxt);
            yourChatDateTxt=itemView.findViewById(R.id.yourChatDateTxt);
        }
    }

}
