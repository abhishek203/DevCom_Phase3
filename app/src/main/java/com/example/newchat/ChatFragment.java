package com.example.newchat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class ChatFragment extends Fragment {
    private RecyclerView mUsers_RecycleView;
    private List<Users> mUsers;
    private View mMainView;
    private Button mChatBtn;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chat, container,false);
        mChatBtn = (Button) mMainView.findViewById(R.id.chat_btn);
        mChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chat_intent = new Intent(getContext(), ChatActivity.class);
                startActivity(chat_intent);
            }
        });
        mUsers_RecycleView = (RecyclerView) mMainView.findViewById(R.id.users_chat_list);
        mUsers_RecycleView.setHasFixedSize(true);
        mUsers_RecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        for (int i =0; i<100; i++){
            Users users = new Users();
            users.setName("User #"+i);
            users.setStatus("Hi I am Using NewChat");
            mUsers.add(users);
        }
        mUsers_RecycleView.setAdapter((new UserAdapter(mUsers)));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{
        private  List<Users> mUsers;

        public UserAdapter(List<Users> users) {
            super();
            this.mUsers = users;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UserViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            holder.bind(this.mUsers.get(position));

        }

        @Override
        public int getItemCount() {
            return this.mUsers.size();
        }
    }
    class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mStatus;
        public UserViewHolder(ViewGroup container){
            super(LayoutInflater.from(getContext()).inflate(R.layout.users_single_layout,container,false));
            mName =(TextView) itemView.findViewById(R.id.users_single_name);
        }
        public void bind(Users users){
            mName.setText(users.getName());
        }

    }
}
