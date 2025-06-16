package org.meicode.kukuafya;

import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * CommunityFragment – shows a simple chat room backed by Firebase Realtime Database.
 * Messages sent by the logged‑in user are right‑aligned; others are left‑aligned with the sender name.
 */
public class CommunityFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private Button sendButton;

    private MessageAdapter messageAdapter;
    private final ArrayList<Message> messagesList = new ArrayList<>();

    private DatabaseReference messagesRef;
    private FirebaseAuth mAuth;
    private static String currentUsername;   // Loaded from "Users/{uid}/username"

    /* ------------------------------------------------------------
     * Fragment lifecycle
     * ------------------------------------------------------------ */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        // UI elements
        recyclerView = view.findViewById(R.id.messageRecyclerView);
        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage());

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        // Load the current user's username, then set up RecyclerView + messages
        fetchCurrentUsername();

        return view;
    }

    /* ------------------------------------------------------------
     * Firebase helpers
     * ------------------------------------------------------------ */
    private void fetchCurrentUsername() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            setupRecycler("Anonymous");
            loadMessages();
            return;
        }

        String uid = firebaseUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usernameFromDb = snapshot.child("username").getValue(String.class);
                if (!TextUtils.isEmpty(usernameFromDb)) {
                    currentUsername = usernameFromDb;
                } else {
                    currentUsername = firebaseUser.getDisplayName();
                }

                if (TextUtils.isEmpty(currentUsername)) {
                    currentUsername = uid;
                }

                setupRecycler(currentUsername);
                loadMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to get user info", Toast.LENGTH_SHORT).show();
                setupRecycler("Anonymous");
                loadMessages();
            }
        });
    }

    private void setupRecycler(String username) {
        messageAdapter = new MessageAdapter(messagesList, username);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);
    }

    /* ------------------------------------------------------------
     * Messaging
     * ------------------------------------------------------------ */
    private void sendMessage() {
        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(getContext(), "Please wait – loading user info…", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = messageEditText.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageId = messagesRef.push().getKey();
        if (messageId == null) return;

        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("sender", currentUsername);

        messagesRef.child(messageId).setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                messageEditText.setText("");
            } else {
                Toast.makeText(getActivity(), "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Message msg = child.getValue(Message.class);
                    if (msg != null) {
                        messagesList.add(msg);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                if (!messagesList.isEmpty()) {
                    recyclerView.post(() -> recyclerView.smoothScrollToPosition(messagesList.size() - 1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* ------------------------------------------------------------
     * Message model – Firebase needs a no‑arg constructor
     * ------------------------------------------------------------ */
    public static class Message {
        public String text;
        public String sender;

        public Message() { /* Firebase */ }

        public Message(String text, String sender) {
            this.text = text;
            this.sender = sender;
        }

        public boolean isSentByUser(String currentUsername) {
            return CommunityFragment.currentUsername != null && CommunityFragment.currentUsername.equals(sender);
        }
    }
}
