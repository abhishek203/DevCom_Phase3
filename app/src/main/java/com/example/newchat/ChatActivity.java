package com.example.newchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;
    private androidx.appcompat.widget.Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private DatabaseReference mMsgRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserID;
    private ImageButton mChatSendBtn;
    private EditText mChatMsg;
    private RecyclerView MsgRecyclerList;
    private View mView;
    private LinearLayoutManager mLinearLayout;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mChatSendBtn = findViewById(R.id.chatsendbtn);
        mChatMsg = findViewById(R.id.chatmsg);

        mView = (LinearLayout) findViewById(R.id.user_details);

        MsgRecyclerList =(RecyclerView) findViewById(R.id.chat_list);
        MsgRecyclerList.setHasFixedSize(true);
        mLinearLayout = new LinearLayoutManager(this);
        MsgRecyclerList.setLayoutManager(mLinearLayout);


        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChatUser = getIntent().getStringExtra("user_id");
        mMsgRef = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserID).child(mChatUser);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.child("Users").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String chat_user_name = dataSnapshot.child("name").getValue().toString();
                getSupportActionBar().setTitle(chat_user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRootRef.child("Chats").child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(mChatUser)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+mCurrentUserID+"/"+mChatUser,chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser+"/" + mCurrentUserID, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Log.d("CHAT_LOG",databaseError.getMessage());
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });


    }

    private void sendMessage() {
        String message = mChatMsg.getText().toString();
        if(!TextUtils.isEmpty(message)){
            String current_user_ref = "messages/"+mCurrentUserID+"/" + mChatUser;
            String chat_user_ref = "messages/"+ mChatUser + "/" + mCurrentUserID;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserID).child(mChatUser).push();

            String push_id = user_message_push.getKey();
            Map msg = new HashMap();
            msg.put("messages",message);
            msg.put("seen", false);
            msg.put("type", "text");
            msg.put("time", ServerValue.TIMESTAMP);

            Map messageUsermap = new HashMap();
            messageUsermap.put(current_user_ref+"/"+ push_id,msg);
            messageUsermap.put(chat_user_ref+"/"+push_id,msg);

            mRootRef.updateChildren(messageUsermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("CHAR_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
        FirebaseRecyclerOptions<messages> options =
                new FirebaseRecyclerOptions.Builder<messages>()
                        .setQuery(mMsgRef,messages.class)
                        .build();
        FirebaseRecyclerAdapter<messages, ChatActivity.ChatViewHolder> adapter = new FirebaseRecyclerAdapter<messages, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatActivity.ChatViewHolder holder, int position, @NonNull messages model) {
                holder.setName(model.getMessage());
            }

            @NonNull
            @Override
            public ChatActivity.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_all_users, parent, false);

                return new ChatActivity.ChatViewHolder(view);
            }

        };
        MsgRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }
    public class  ChatViewHolder extends RecyclerView.ViewHolder{
        public View mView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String message){
            TextView chat = (TextView) mView.findViewById(R.id.single_chat);
            chat.setText(message);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.



    }

}
