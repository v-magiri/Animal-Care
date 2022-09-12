package com.magiri.animalcare.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.magiri.animalcare.Model.Chat;
import com.magiri.animalcare.R;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private Context context;
    private List<Chat> chatList;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Chat getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Chat chat=chatList.get(position);
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(chatList.get(position).getFarmer()){
            convertView=layoutInflater.from(context).inflate(R.layout.right_row_chat,null);
        }else{
            convertView=layoutInflater.from(context).inflate(R.layout.left_row_chat,null);
        }
        TextView messageTextView=(TextView) convertView.findViewById(R.id.txtMsg);
        TextView dateTextView=(TextView) convertView.findViewById(R.id.dateText);
        messageTextView.setText(chat.getMessage());
        dateTextView.setText(chat.getTimeStamp());

        return convertView;
    }
}
