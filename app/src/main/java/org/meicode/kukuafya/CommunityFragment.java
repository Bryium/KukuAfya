package org.meicode.kukuafya;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messagesList;

    private DatabaseReference messagesDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        recyclerView = view.findViewById(R.id.messageRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);

        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messagesList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        // Initialize Firebase Realtime Database
        messagesDatabase = FirebaseDatabase.getInstance().getReference("messages");

        sendButton.setOnClickListener(v -> sendMessage());

        loadMessages();

        return view;
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageId = messagesDatabase.push().getKey();
        if (messageId != null) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("text", messageText);
            messageMap.put("sender", "User"); // Replace with actual user identification

            messagesDatabase.child(messageId).setValue(messageMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    messageEditText.setText("");
                } else {
                    Toast.makeText(getActivity(), "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMessages() {
        messagesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messagesList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                if (!messagesList.isEmpty()) {
                    recyclerView.post(() -> recyclerView.smoothScrollToPosition(messagesList.size() - 1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class Message {
        public String text;
        public String sender;

        public Message() {
        }

        public Message(String text, String sender) {
            this.text = text;
            this.sender = sender;
        }
    }
}
