package com.magiri.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.magiri.animalcare.Adapters.ChatAdapter;
import com.magiri.animalcare.Adapters.VetClientChatAdapter;
import com.magiri.animalcare.Model.Chat;
import com.magiri.animalcare.Model.ChatMemory;
import com.magiri.animalcare.Session.Prevalent;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FarmerVet_Chat extends AppCompatActivity {
    private static final String TAG = "FarmerVet_Chat";
    private FloatingActionButton sendFAB;
    private EditText messageEditTxt;
    private RecyclerView chatRecyclerView;
    private MaterialToolbar chatMaterialToolbar;
    private List<Chat> chatList;
//    private ChatAdapter chatAdapter;
    private VetClientChatAdapter clientChatAdapter;
    private DatabaseReference databaseReference;
    String currentTimeStamp;
    String Reg_Num,vetName,FarmerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Reg_Num=getIntent().getStringExtra("Vet_REGNUM");
        vetName=getIntent().getStringExtra("Vet_Name");
        chatMaterialToolbar=findViewById(R.id.chatMaterialToolbar);
        messageEditTxt=findViewById(R.id.et_message);
        chatRecyclerView=findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setHasFixedSize(true);
        sendFAB=findViewById(R.id.btn_send);
        chatList=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        clientChatAdapter=new VetClientChatAdapter(this,chatList);
        chatRecyclerView.setAdapter(clientChatAdapter);
        FarmerID=Prevalent.currentOnlineFarmer.getFarmerID();

        chatMaterialToolbar.setTitle(vetName);
        chatMaterialToolbar.setNavigationOnClickListener(v -> finish());

        getMessages();

        sendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messageEditTxt.getText().toString().trim();

                if(TextUtils.isEmpty(message)){
                    return;
                }
                saveMessage(message);
                messageEditTxt.setText("");
                chatRecyclerView.scrollToPosition(chatList.size()-1);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveMessage(String message) {
        currentTimeStamp= DateFormat.getInstance().format(new Date());
        final DatabaseReference ref;
        String chatID= String.valueOf(System.currentTimeMillis());

        String chatKey=Reg_Num.substring(3);
        ChatMemory.saveLastChat(chatID,FarmerID,FarmerVet_Chat.this);
        Chat chat=new Chat(message,currentTimeStamp,FarmerID,Reg_Num,true,false);
        databaseReference.child(FarmerID).child("client").setValue(FarmerID);
        databaseReference.child(FarmerID).child("Vet").setValue(Reg_Num);
        databaseReference.child(FarmerID).child("Messages").child(chatID).setValue(chat).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                clientChatAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });

    }

    private void getMessages() {
        String FarmerID=Prevalent.currentOnlineFarmer.getFarmerID();
        databaseReference.child(FarmerID).child("Messages").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getFarmerID().equals(FarmerID)){
                        chatList.add(chat);
                    }
                }
                clientChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
                Toast.makeText(getApplicationContext(),"Something Wrong Happened Please contact Support Team",Toast.LENGTH_SHORT).show();
            }
        });
    }
}